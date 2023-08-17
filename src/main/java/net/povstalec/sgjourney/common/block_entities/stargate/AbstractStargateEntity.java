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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.sound.SoundWrapper;
import net.povstalec.sgjourney.common.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateRingBlock;
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
import net.povstalec.sgjourney.common.stargate.Addressing;
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
	protected int animationTick = 0;
	protected boolean isPrimaryChevronEngaged = false;
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
	
	public SoundWrapper wormholeIdleSound = null;
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
		isPrimaryChevronEngaged = nbt.getBoolean("IsPrimaryChevronEngaged");
		dialingOut = nbt.getBoolean("DialingOut");
		timesOpened = nbt.getInt("TimesOpened");
		address = nbt.getIntArray("Address");
		address = nbt.getIntArray("Address");
		network = nbt.getInt("Network");
		
		connectionID = nbt.getString("ConnectionID");
		advancedProtocolsEnabled = nbt.getBoolean("AdvancedProtocolsEnabled");
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.putBoolean("IsPrimaryChevronEngaged", isPrimaryChevronEngaged);
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
			address = Addressing.addressIntArrayToString(Addressing.randomAddress(8, 36, random.nextLong()));
			
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
		
		if(Addressing.addressContainsSymbol(getAddress(), symbol))
			return setRecentFeedback(Stargate.Feedback.SYMBOL_IN_ADDRESS);
		
		if(symbol == 0)
			return setRecentFeedback(lockPrimaryChevron());
		else
			return setRecentFeedback(encodeChevron(symbol));
	}
	
	protected Stargate.Feedback encodeChevron(int symbol)
	{	
		if(getAddress().length >= 8)
			return resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		growAddress(symbol);
		engageChevron();
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
				setPrimaryChevronEndaged(true);
				engageChevron();
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
	
	protected void engageChevron()
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Chevron(this.worldPosition, false));
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
		this.setAddress(address);
		this.setDialingOut(dialingOut);
		this.timesOpened++;
		this.setChanged();
		
		this.updateStargate();
	}
	
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback)
	{
		if(isConnected())
		{
			level.playSound((Player)null, worldPosition, SoundInit.WORMHOLE_CLOSE.get(), SoundSource.BLOCKS, 0.25F, 1F);
			setConnected(false);
		}
		
		resetAddress();
		this.isPrimaryChevronEngaged = false;
		this.dialingOut = false;
		this.connectionID = EMPTY;
		
		if(feedback.playFailSound() && !level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Fail(this.worldPosition));
		
		setChanged();
		StargateJourney.LOGGER.info("Reset Stargate at " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ());
		return setRecentFeedback(feedback);
	}
	
	public Stargate.Feedback disconnectStargate(Stargate.Feedback feedback)
	{
		StargateNetwork.get(level).terminateConnection(level.getServer(), connectionID, feedback);
		return resetStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);//TODO Change this
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
	}
	
	protected void resetAddress()
	{
		this.address = new int[0];
		setStargateState(false, 0);
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
	
	public void setTickCount(int tick)
	{
		this.animationTick = tick;
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
		return this.isConnected() ? chevronsEngaged + 1 : chevronsEngaged;
	}
	
	public int chevronsRendered()
	{
		return getAddress().length;
	}
	
	public void setPrimaryChevronEndaged(boolean engaged)
	{
		this.isPrimaryChevronEngaged = engaged;
	}
	
	public boolean isPrimaryChevronEngaged()
	{
		return this.isPrimaryChevronEngaged;
	}
	
	public BlockPos getCenterPos()
	{
    	BlockPos mainBlockPos = this.getBlockPos();
    	Direction centerDirection = Orientation.getCenterDirection(getDirection(), getOrientation());
    	
    	return mainBlockPos.relative(centerDirection, 3);
	}
    
    public Vec3 getRelativeCenter()
    {
    	BlockPos mainBlockPos = this.getBlockPos();
    	BlockPos centerPos = getCenterPos();
    	
    	double y = 0.5;
    	Orientation orientation = getOrientation();
    	
    	if(orientation != null && orientation != Orientation.REGULAR)
    		y = 0.28125;
    	
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
				
				if((!state.canBeReplaced() && !(state.getBlock() instanceof AbstractStargateRingBlock)) || state.getFluidState().is(Fluids.LAVA))
					obstructingBlocks++;
			}
		}
		StargateJourney.LOGGER.info("Stargate is obstructed by " + obstructingBlocks + " blocks");
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
	
	public abstract SoundEvent chevronEngageSound();

	public abstract SoundEvent failSound();
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide)
			return;
		
		super.getStatus(player);
		player.sendSystemMessage(Component.literal("Point of Origin: " + pointOfOrigin).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.literal("Symbols: " + symbols).withStyle(ChatFormatting.LIGHT_PURPLE));
		player.sendSystemMessage(Component.literal("Open Time: " + getOpenTime() + "/" + getMaxGateOpenTime()).withStyle(ChatFormatting.DARK_AQUA));
		player.sendSystemMessage(Component.literal("Times Opened: " + timesOpened).withStyle(ChatFormatting.BLUE));
		player.sendSystemMessage(Component.literal("Connected to DHD: " + hasDHD).withStyle(ChatFormatting.GOLD));
		player.sendSystemMessage(Component.literal("Advanced Protocols Enabled: " + advancedProtocolsEnabled).withStyle(ChatFormatting.RED));
		player.sendSystemMessage(Component.literal("Time Since Last Traveler: " + getTimeSinceLastTraveler()).withStyle(ChatFormatting.DARK_PURPLE));
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

		StargateJourney.LOGGER.info("Couldn't find Stargate Typw");
		return null;
	}
	
	public double getGateAddition()
	{
		return this.getOrientation() == Orientation.REGULAR
				? getGateType().getVerticalCenterHeight()
				: getGateType().getHorizontalCenterHeight();
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractStargateEntity stargate)
    {
		stargate.increaseTickCount();
		
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientboundStargateUpdatePacket(stargate.worldPosition, stargate.address, stargate.dialingOut, stargate.animationTick, stargate.pointOfOrigin, stargate.symbols));
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientBoundSoundPackets.IdleWormhole(stargate.worldPosition));
    }
}
