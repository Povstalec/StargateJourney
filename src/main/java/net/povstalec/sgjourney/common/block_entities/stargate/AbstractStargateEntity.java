package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.Random;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.sound.SoundWrapper;
import net.povstalec.sgjourney.common.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.misc.ArrayHelper;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Connection;
import net.povstalec.sgjourney.common.stargate.Dialing;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Symbols;
import net.povstalec.sgjourney.common.stargate.Wormhole;

public abstract class AbstractStargateEntity extends SGJourneyBlockEntity
{
	// Basic Info
	protected static Stargate.Gen generation;
	protected int network;
	
	// Used during gameplay
	protected Stargate.Feedback recentFeedback = Stargate.Feedback.NONE;
	protected int kawooshTick = 0;
	protected int animationTick = 0;
	protected int[] engagedChevrons = Stargate.DIALING_CHEVRON_CONFIGURATION;
	protected boolean dialingOut = false;
	protected int timesOpened = 0;
	protected String pointOfOrigin = EMPTY;
	protected String symbols = EMPTY;
	
	// Dialing and memory
	protected int[] address = new int[0];
	protected String connectionID = EMPTY;
	protected Wormhole wormhole = new Wormhole();

	protected boolean hasDHD = false;
	protected boolean advancedProtocolsEnabled = false;

	protected int openSoundLead = 28;
	public SoundWrapper wormholeIdleSound = null;
	public SoundWrapper wormholeOpenSound = null;
	public SoundWrapper spinSound = null;
	
	//private Stargate.FilterType filter = Stargate.FilterType.NONE;
	//private ListTag whitelist;
	//private ListTag blacklist;

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, Stargate.Gen gen, int defaultNetwork)
	{
		super(blockEntity, pos, state, SGJourneyBlockEntity.Type.STARGATE);
		generation = gen;
		this.network = defaultNetwork;
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		dialingOut = nbt.getBoolean("DialingOut");
		timesOpened = nbt.getInt("TimesOpened");
		address = nbt.getIntArray("Address");
		network = nbt.getInt("Network");
		
		connectionID = nbt.getString("ConnectionID");
		advancedProtocolsEnabled = nbt.getBoolean("AdvancedProtocolsEnabled");
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.putBoolean("DialingOut", dialingOut);
		nbt.putInt("TimesOpened", timesOpened);
		nbt.putIntArray("Address", address);
		nbt.putInt("Network", network);
		
		nbt.putString("ConnectionID", connectionID);
		nbt.putBoolean("AdvancedProtocolsEnabled", advancedProtocolsEnabled);
		super.saveAdditional(nbt);
	}
	
	@Override
	public CompoundTag addToBlockEntityList()
	{
		CompoundTag blockEntity = super.addToBlockEntityList();
    	StargateNetwork.get(level).addStargate(level.getServer(), getID(), blockEntity, this.getGeneration().getGen());
		return blockEntity;
	}
	
	@Override
	public CompoundTag addNewToBlockEntityList()
	{
		CompoundTag blockEntity = super.addNewToBlockEntityList();
    	StargateNetwork.get(level).addStargate(level.getServer(), getID(), blockEntity, this.getGeneration().getGen());
		return blockEntity;
	}

	@Override
	public void removeFromBlockEntityList()
	{
		super.removeFromBlockEntityList();
		StargateNetwork.get(level).removeStargate(level, getID());
	}
	
	@Override
	protected String generateID()
	{
		Random random = new Random();
		String address = EMPTY;
		while(true)
		{
			address = Address.addressIntArrayToString(Address.randomAddress(8, 36, random.nextLong()));
			
			if(!BlockEntityList.get(level).getBlockEntities(SGJourneyBlockEntity.Type.STARGATE.id).contains(address))
				break;
		}
		return address;
	}

	
	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB(getCenterPos().getX() - 3, getCenterPos().getY() - 3, getCenterPos().getZ() - 3, getCenterPos().getX() + 4, getCenterPos().getY() + 4, getCenterPos().getZ() + 4);
    }
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	public Stargate.Feedback engageSymbol(int symbol)
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(symbol == 0)
			return setRecentFeedback(lockPrimaryChevron());
		else
			return setRecentFeedback(encodeChevron(symbol, false));
	}
	
	public Stargate.Feedback encodeChevron(int symbol, boolean incoming)
	{
		if(Address.addressContainsSymbol(getAddress(), symbol))
			return setRecentFeedback(Stargate.Feedback.SYMBOL_IN_ADDRESS);
		if(getAddress().length >= 8)
			return resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		growAddress(symbol);
		chevronSound(incoming);
		this.setChanged();
		
		return Stargate.Feedback.SYMBOL_ENCODED;
	}
	
	protected Stargate.Feedback lockPrimaryChevron()
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(getAddress().length < 6)
			return resetStargate(Stargate.Feedback.INCOPLETE_ADDRESS);
		else if(!isConnected())
		{
			if(!isObstructed())
			{
				chevronSound(false);
				return setRecentFeedback(engageStargate());
			}
			else
				return resetStargate(Stargate.Feedback.SELF_OBSTRUCTED);
		}
		else
		{
			if(!CommonStargateConfig.end_connection_from_both_ends.get() && !this.isDialingOut())
				return Stargate.Feedback.WRONG_DISCONNECT_SIDE;
			else
				return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
		}
		
	}
	
	public void chevronSound(boolean incoming)
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Chevron(this.worldPosition, false));
	}
	
	public void openWormholeSound()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.OpenWormhole(this.worldPosition));
	}
	
	public void idleWormholeSound()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.IdleWormhole(this.worldPosition));
	}
	
	public void closeWormholeSound()
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.CloseWormhole(this.worldPosition));
	}
	
	public abstract void playRotationSound();
	
	public abstract void stopRotationSound();
	
	public void playWormholeIdleSound()
	{
		wormholeIdleSound.playSound();
	}
	
	public Stargate.Feedback engageStargate()
	{
		return Dialing.dialStargate(this.level, this);
	}
	
	public Stargate.Feedback dialStargate(AbstractStargateEntity targetStargate)
	{
		return StargateNetwork.get(level).createConnection(this.level.getServer(), this, targetStargate);
	}
	
	public void connectStargate(String connectionID, boolean dialingOut, int[] address)
	{
		this.connectionID = connectionID;
		this.setConnected(true);
		this.setDialingOut(dialingOut);
		this.timesOpened++;
		this.setChanged();
		
		this.updateStargate();
	}
	
	public static double kawooshFunction(int kawooshTime)
	{
		return 8 * Math.sin(Math.PI * (double) kawooshTime / (Connection.KAWOOSH_TICKS));
	}
	
	public void doKawoosh(int kawooshTime)
	{
		setKawooshTickCount(kawooshTime);
		updateClient();
		
		if(kawooshTime > Connection.KAWOOSH_TICKS)
			return;
		
		BlockPos centerPos = getCenterPos();
		Direction axisDirection = getDirection().getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
		Direction direction = Orientation.getEffectiveDirection(getDirection(), getOrientation());
		
		double frontMultiplier =  kawooshFunction(kawooshTime);
		for(int width = -1; width <= 1; width++)
		{
			for(int height = -1; height <= 1; height++)
			{
				BlockPos pos = centerPos.relative(axisDirection, width).relative(Orientation.getCenterDirection(getDirection(), getOrientation()), height);
				BlockState state = level.getBlockState(pos);
				
				if((!(state.getBlock() instanceof AbstractStargateBlock)))
				{
					for(int i = 0; i < (int) Math.round(frontMultiplier); i++)
					{
						if(!level.getBlockState(pos.relative(direction, i)).is(Blocks.AIR))
							level.destroyBlock(pos.relative(direction, i), false);
					}
				}
			}
			
			Vec3 centerVector = this.getCenter();
			
			Vec3 backVector = centerVector.relative(axisDirection, -2.25).relative(Orientation.getCenterDirection(getDirection(), getOrientation()), -2.25);
			
			frontMultiplier = frontMultiplier > 7 ? 7 : frontMultiplier;
			Vec3 facingVector = Orientation.getEffectiveVector(direction, getOrientation());
			facingVector = facingVector.multiply(frontMultiplier, frontMultiplier, frontMultiplier);
			facingVector = facingVector.add(centerVector);
			facingVector = facingVector.relative(axisDirection, 2.25).relative(Orientation.getCenterDirection(getDirection(), getOrientation()), 2.25);
			
			AABB kawooshHitbox = new AABB(backVector.x(), backVector.y(), backVector.z(),
					facingVector.x(), facingVector.y(), facingVector.z());
			
			this.level.getEntitiesOfClass(Entity.class, kawooshHitbox).stream().forEach(entity -> 
			{
				if(!(entity instanceof Player player && player.isCreative()))
					entity.kill();
			});
		}
	}
	
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback)
	{
		if(isConnected())
		{
			closeWormholeSound();
			setConnected(false);
		}

		resetAddress();
		this.dialingOut = false;
		this.connectionID = EMPTY;
		setKawooshTickCount(0);
		updateClient();
		
		if(feedback.playFailSound() && !level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Fail(this.worldPosition));
		
		setChanged();
		StargateJourney.LOGGER.info("Reset Stargate at " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ() + " " + this.getLevel().dimension().location().toString());
		return setRecentFeedback(feedback);
	}
	
	public Stargate.Feedback disconnectStargate(Stargate.Feedback feedback)
	{
		StargateNetwork.get(level).terminateConnection(level.getServer(), connectionID, feedback);
		return resetStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
	}
	
	public void updateStargate()
	{
		updateStargate(this.level, this.getID(), this.timesOpened, this.hasDHD);
	}
	
	private void updateStargate(Level level, String id, int timesOpened, boolean hasDHD)
	{
		StargateNetwork.get(level).updateStargate(level, id, timesOpened, hasDHD);
		setStargateState(this.isConnected(), this.getChevronsEngaged());
	}
	
	protected void growAddress(int symbol)
	{
		this.address = ArrayHelper.growIntArray(this.address, symbol);
		setStargateState(this.isConnected(), this.getChevronsEngaged());
		updateClient();
	}
	
	protected void resetAddress()
	{
		this.address = new int[0];
		engagedChevrons = Stargate.DIALING_CHEVRON_CONFIGURATION;
		setStargateState(false, 0);
	}
	
	public String getConnectionAddress(int addressLength)
	{
		String dimension = this.level.dimension().location().toString();
		switch(addressLength)
		{
		case 6:
			String galaxy = Universe.get(this.level).getGalaxiesFromDimension(dimension).getCompound(0).getAllKeys().iterator().next();
			return Universe.get(level).getAddressInGalaxyFromDimension(galaxy, dimension);
		case 7:
			return Universe.get(level).getExtragalacticAddressFromDimension(dimension);
		default:
			return this.getID();
		}
	}
	
	//============================================================================================
	//******************************************Symbols*******************************************
	//============================================================================================
	
	public Stargate.Feedback setRecentFeedback(Stargate.Feedback feedback)
	{
		this.recentFeedback = feedback;
		return getRecentFeedback();
	}
	
	public Stargate.Feedback getRecentFeedback()
	{
		return this.recentFeedback;
	}
	
	/**
	 * Sets the Stargate's point of origin based on the dimension
	 */
	public void setPointOfOrigin(Level level)
	{
		pointOfOrigin = Universe.get(level).getPointOfOrigin(level.dimension().location().toString());
		this.setChanged();
	}
	
	protected boolean isPointOfOriginValid(Level level)
	{
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		if(!isLocationValid(pointOfOrigin))
			return false;
		
		return pointOfOriginRegistry.containsKey(new ResourceLocation(pointOfOrigin));
	}
	
	public void setSymbols(Level level)
	{
		symbols = Universe.get(level).getSymbols(level.dimension().location().toString());
		this.setChanged();
	}
	
	protected boolean areSymbolsValid(Level level)
	{
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<Symbols> symbolRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		if(!isLocationValid(symbols))
			return false;
		
		return symbolRegistry.containsKey(new ResourceLocation(symbols));
	}
	
	private boolean isLocationValid(String location)
	{
		String[] split = location.split(":");
		
		if(split.length > 2)
			return false;
		
		if(!ResourceLocation.isValidNamespace(split[0]))
			return false;
		
		return ResourceLocation.isValidPath(split[1]);
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================

	public int getMaxGateOpenTime()
	{
		return CommonStargateConfig.max_wormhole_open_time.get() * 20;
	}
	
	public Stargate.Gen getGeneration()
	{
		return generation;
	}
	
	public void setKawooshTickCount(int kawooshTick)
	{
		this.kawooshTick = kawooshTick;
	}
	
	public int getKawooshTickCount()
	{
		return this.kawooshTick;
	}
	
	public void setTickCount(int animationTick)
	{
		this.animationTick = animationTick;
	}
	
	public int getTickCount()
	{
		return this.animationTick;
	}
	
	public int increaseTickCount()
	{
		this.animationTick++;
		return this.animationTick;
	}
	
	public void setDialingOut(boolean dialingOut)
	{
		this.dialingOut = dialingOut;
	}
	
	public boolean isDialingOut()
	{
		return this.dialingOut;
	}
	
	public void setDHD(boolean hasDHD, boolean enableAdvancedProtocols)
	{
		if(this.hasDHD != hasDHD)
			updateStargate(this.level, this.getID(), this.timesOpened, hasDHD);
		
		this.advancedProtocolsEnabled = hasDHD ? enableAdvancedProtocols : false;
		this.hasDHD = hasDHD;
	}
	
	public boolean advancedProtocolsEnabled()
	{
		return this.advancedProtocolsEnabled;
	}
	
	public boolean hasDHD()
	{
		return this.hasDHD;
	}
	
	public int getOpenTime()
	{
		if(this.level.isClientSide())
			return 0;
		return StargateNetwork.get(this.level).getOpenTime(this.connectionID);
	}
	
	public int getTimeSinceLastTraveler()
	{
		if(this.level.isClientSide())
			return 0;
		return StargateNetwork.get(this.level).getTimeSinceLastTraveler(this.connectionID);
	}
	
	public int getTimesOpened()
	{
		return this.timesOpened;
	}
	
	public void setPointOfOrigin(String pointOfOrigin)
	{
		this.pointOfOrigin = pointOfOrigin;
	}
	
	public String getPointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	public void setSymbols(String symbols)
	{
		this.symbols = symbols;
	}
	
	public String getSymbols()
	{
		return this.symbols;
	}
	
	public void setAddress(int[] address)
	{
		this.address = address;
	}
	
	public int[] getAddress()
	{
		return this.address;
	}
	
	public int getChevronsEngaged()
	{
		int chevronsEngaged = getAddress().length;
		return isConnected() ? chevronsEngaged + 1 : chevronsEngaged;
	}
	
	public void setEngagedChevrons(int[] engagedChevrons)
	{
		this.engagedChevrons = engagedChevrons;
	}
	
	public static int[] getChevronConfiguration(int addressLength)
	{
		switch(addressLength)
		{
		case 6:
			return Stargate.DIALED_7_CHEVRON_CONFIGURATION;
		case 7:
			return Stargate.DIALED_8_CHEVRON_CONFIGURATION;
		case 8:
			return Stargate.DIALED_9_CHEVRON_CONFIGURATION;
		default:
			return Stargate.DIALING_CHEVRON_CONFIGURATION;
		}
	}
	
	public int[] getEngagedChevrons()
	{
		return this.engagedChevrons;
	}
	
	public int chevronsRendered()
	{
		return getAddress().length;
	}
	
	public BlockPos getCenterPos()
	{
    	BlockPos mainBlockPos = this.getBlockPos();
    	Direction centerDirection = Orientation.getCenterDirection(getDirection(), getOrientation());
    	
    	return mainBlockPos.relative(centerDirection, 3);
	}
    
    public Vec3 getCenter()
    {
    	BlockPos centerPos = getCenterPos();
    	
    	double y = this.getGateType().getVerticalCenterHeight();
    	Orientation orientation = getOrientation();
    	
    	if(orientation != null && orientation != Orientation.REGULAR)
    		y = this.getGateType().getHorizontalCenterHeight();
    	
    	return new Vec3(
    			centerPos.getX() + 0.5, 
    			centerPos.getY() + y, 
    			centerPos.getZ() + 0.5);
    }
    
    public Vec3 getRelativeCenter()
    {
    	BlockPos mainBlockPos = this.getBlockPos();
    	BlockPos centerPos = getCenterPos();
    	
    	Stargate.Type type = this.getGateType();
    	
    	double y = type == null ? 0.5 : type.getVerticalCenterHeight();
    	Orientation orientation = getOrientation();
    	
    	if(orientation != null && orientation != Orientation.REGULAR)
    		y = type.getHorizontalCenterHeight();
    	
    	return new Vec3(
    			centerPos.getX() - mainBlockPos.getX() + 0.5, 
    			centerPos.getY() - mainBlockPos.getY() + y, 
    			centerPos.getZ() - mainBlockPos.getZ() + 0.5);
    }
	
	public Orientation getOrientation()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
			return gateState.getValue(AbstractStargateBaseBlock.ORIENTATION);

		StargateJourney.LOGGER.info("Couldn't find Stargate Orientation");
		return null;
	}
	
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
			return gateState.getValue(AbstractStargateBaseBlock.FACING);

		StargateJourney.LOGGER.info("Couldn't find Stargate Direction");
		return null;
	}
	
	public void setConnected(boolean isConnected)
	{
		setStargateState(isConnected, this.getChevronsEngaged());
	}
	
	public void setStargateState(boolean isConnected, int chevronsEngaged)
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargate)
			stargate.updateStargate(level, gatePos, gateState, isConnected, chevronsEngaged);
		else
			StargateJourney.LOGGER.info("Couldn't find Stargate");
		setChanged();
		
	}
	
	public boolean isConnected()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
			return this.level.getBlockState(gatePos).getValue(AbstractStargateBaseBlock.CONNECTED);
		
		return false;
	}
	
	public boolean isObstructed()
	{
		Direction direction = getDirection().getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
		BlockPos centerPos = getCenterPos();
		int obstructingBlocks = 0;
		
		for(int width = -2; width <= 2; width++)
		{
			for(int height = -2; height <= 2; height++)
			{
				BlockPos pos = centerPos.relative(direction, width).relative(Orientation.getCenterDirection(getDirection(), getOrientation()), height);
				BlockState state = level.getBlockState(pos);
				
				if((!state.getMaterial().isReplaceable() && !(state.getBlock() instanceof AbstractStargateBlock)) || state.getMaterial() == Material.LAVA)
					obstructingBlocks++;
			}
		}
		//StargateJourney.LOGGER.info("Stargate is obstructed by " + obstructingBlocks + " blocks");
		return obstructingBlocks > 12;
	}
	
	public boolean hasEnergy(AbstractStargateEntity targetStargate)
	{
		return this.getEnergyStored() >= StargateNetwork.getConnectionType(this.level.getServer(), this, targetStargate).getEstabilishingPowerCost();
	}
	
	public Wormhole getWormhole()
	{
		return this.wormhole;
	}
	
	public void setOpenSoundLead(int openSoundLead)
	{
		this.openSoundLead = openSoundLead;
	}
	
	public int getOpenSoundLead()
	{
		return this.openSoundLead;
	}
	
	public abstract SoundEvent getChevronEngageSound();
	
	public abstract SoundEvent getWormholeOpenSound();
	
	public SoundEvent getWormholeCloseSound()
	{
		return SoundInit.WORMHOLE_CLOSE.get();
	}

	public abstract SoundEvent getFailSound();
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide())
			return;
		
		super.getStatus(player);
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": " + pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.open_time").append(Component.literal(": " + getOpenTime() + "/" + getMaxGateOpenTime())).withStyle(ChatFormatting.DARK_AQUA));
		player.sendSystemMessage(Component.translatable("info.sgjourney.times_opened").append(Component.literal(": " + timesOpened)).withStyle(ChatFormatting.BLUE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.has_dhd").append(Component.literal(": " + hasDHD)).withStyle(ChatFormatting.GOLD));
		player.sendSystemMessage(Component.translatable("info.sgjourney.advanced_protocols_enabled").append(Component.literal(": " + advancedProtocolsEnabled)).withStyle(ChatFormatting.RED));
		player.sendSystemMessage(Component.translatable("info.sgjourney.last_traveler_time").append(Component.literal(": " + getTimeSinceLastTraveler())).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.address").append(Component.literal(": " + Address.addressIntArrayToString(getAddress()))).withStyle(ChatFormatting.GREEN));
	}
	
	@Override
	public boolean isCorrectSide(Direction side)
	{
		return false;
	}

	@Override
	public long capacity()
	{
		return 1000000000000L;
	}

	@Override
	public long maxReceive()
	{
		return 1000000000L;
	}

	@Override
	public long maxExtract()
	{
		return 1000000000L;
	}
	
	public Stargate.Type getGateType()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargate)
			return stargate.getStargateType();

		StargateJourney.LOGGER.info("Couldn't find Stargate Type");
		return null;
	}
	
	public double getGateAddition()
	{
		return this.getOrientation() == Orientation.REGULAR
				? getGateType().getVerticalCenterHeight()
				: getGateType().getHorizontalCenterHeight();
	}
	
	public void updateClient()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateUpdatePacket(this.worldPosition, this.address, this.engagedChevrons, this.dialingOut, this.kawooshTick, this.animationTick, this.pointOfOrigin, this.symbols));
		
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractStargateEntity stargate)
    {
		stargate.increaseTickCount();
		
		if(level.isClientSide())
			return;
		
		stargate.updateClient();
    }
}
