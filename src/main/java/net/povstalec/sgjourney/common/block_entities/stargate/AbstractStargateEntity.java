package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Connection;
import net.povstalec.sgjourney.common.stargate.ConnectionState;
import net.povstalec.sgjourney.common.stargate.Dialing;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Symbols;
import net.povstalec.sgjourney.common.stargate.Wormhole;

public abstract class AbstractStargateEntity extends SGJourneyBlockEntity
{
	private static final String EVENT_CHEVRON_ENGAGED = "stargate_chevron_engaged";
	private static final String EVENT_RESET = "stargate_reset";

	public static final String ADDRESS = "Address";
	public static final String DHD_POS = "DHDPos";
	public static final String ENERGY = "Energy";
	// Connections
	public static final String CONNECTION_ID = "ConnectionID";
	public static final String NETWORK = "Network";
	public static final String RESTRICT_NETWORK = "RestrictNetwork";
	public static final String TIMES_OPENED = "TimesOpened";
	public static final String AUTOCLOSE = "Autoclose";
	// Upgrading and variants
	public static final String UPGRADED = "Upgraded";
	public static final String DISPLAY_ID = "DisplayID";
	public static final String VARIANT = "Variant";

	public static final float STANDARD_THICKNESS = 9.0F;
	public static final float VERTICAL_CENTER_STANDARD_HEIGHT = 0.5F;
	public static final float HORIZONTAL_CENTER_STANDARD_HEIGHT = (STANDARD_THICKNESS / 2) / 16;
	
	// Basic Info
	protected final Stargate.Gen generation;
	protected int symbolBounds = 38;
	protected int network;
	protected boolean restrictNetwork = false;
	
	// Blockstate values
	protected BlockPos centerPosition;
	protected Direction direction;
	protected Orientation orientation;
	
	// Used during gameplay
	protected Stargate.Feedback recentFeedback = Stargate.Feedback.NONE;
	protected int kawooshTick = 0;
	protected int animationTick = 0;
	protected int[] engagedChevrons = Dialing.DEFAULT_CHEVRON_CONFIGURATION;
	protected int timesOpened = 0;
	protected String pointOfOrigin = EMPTY;
	protected String symbols = EMPTY;
	
	protected String variant = EMPTY;
	
	// Dialing and memory
	protected Address address = new Address();
	protected String connectionID = EMPTY;
	protected Wormhole wormhole = new Wormhole();
	
	protected Optional<AbstractDHDEntity> dhd = Optional.empty();
	protected Optional<Vec3i> dhdRelativePos = Optional.empty();
	protected int autoclose = 0;

	protected int openSoundLead = 28;
	protected float verticalCenterHeight;
	protected float horizontalCenterHeight;
	
	public SoundWrapper wormholeIdleSound = null;
	public SoundWrapper wormholeOpenSound = null;
	public SoundWrapper spinSound = null;
	
	protected boolean displayID = false;
	protected boolean upgraded = false;
	
	//private Stargate.FilterType filter = Stargate.FilterType.NONE;
	//private ListTag whitelist;
	//private ListTag blacklist;

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, Stargate.Gen gen, int defaultNetwork,
			float verticalCenterHeight, float horizontalCenterHeight)
	{
		super(blockEntity, pos, state, SGJourneyBlockEntity.Type.STARGATE);
		
		generation = gen;
		this.network = defaultNetwork;
		
		this.verticalCenterHeight = verticalCenterHeight;
		this.horizontalCenterHeight = horizontalCenterHeight;
	}

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, Stargate.Gen gen, int defaultNetwork)
	{
		this(blockEntity, pos, state, gen, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
	}
	
	@Override
    public void onLoad()
	{
		super.onLoad();
		
        if(level.isClientSide())
	        return;
        
        loadDHD();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		timesOpened = tag.getInt(TIMES_OPENED);
		address.fromArray(tag.getIntArray(ADDRESS));
		network = tag.getInt(NETWORK);
		restrictNetwork = tag.getBoolean(RESTRICT_NETWORK);
		
		connectionID = tag.getString(CONNECTION_ID);

		displayID = tag.getBoolean(DISPLAY_ID);
		upgraded = tag.getBoolean(UPGRADED);
		
		variant = tag.getString(VARIANT);
		
		if(tag.contains(DHD_POS))
		{
			int[] pos = tag.getIntArray(DHD_POS);
			dhdRelativePos = Optional.of(new Vec3i(pos[0], pos[1], pos[2]));
		}
		autoclose = tag.getInt(AUTOCLOSE);
	}
	
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		timesOpened = tag.getInt(TIMES_OPENED);
		address.fromArray(tag.getIntArray(ADDRESS));
		network = tag.getInt(NETWORK);
		restrictNetwork = tag.getBoolean(RESTRICT_NETWORK);
		
		connectionID = tag.getString(CONNECTION_ID);
		
    	setID(tag.getString(ID));
    	addToNetwork = tag.getBoolean(ADD_TO_NETWORK);
    	
		this.setEnergy(tag.getLong(ENERGY));
		
		displayID = tag.getBoolean(DISPLAY_ID);
		upgraded = isUpgraded ? isUpgraded : tag.getBoolean(UPGRADED);
    	
		if(tag.contains(DHD_POS))
		{
			int[] pos = tag.getIntArray(DHD_POS);
			dhdRelativePos = Optional.of(new Vec3i(pos[0], pos[1], pos[2]));
		}
		autoclose = tag.getInt(AUTOCLOSE);
		
    	this.setChanged();
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.putInt(TIMES_OPENED, timesOpened);
		tag.putIntArray(ADDRESS, address.toArray());
		tag.putInt(NETWORK, network);
		tag.putBoolean(RESTRICT_NETWORK, restrictNetwork);
		
		tag.putString(CONNECTION_ID, connectionID);
		
		tag.putBoolean(DISPLAY_ID, displayID);
		tag.putBoolean(UPGRADED, upgraded);

		tag.putString(VARIANT, variant);
		
		if(dhdRelativePos.isPresent())
		{
			Vec3i pos = dhdRelativePos.get();
			tag.putIntArray(DHD_POS, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		}
		tag.putInt(AUTOCLOSE, autoclose);
		
		super.saveAdditional(tag);
	}
	
	public CompoundTag serializeStargateInfo()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(TIMES_OPENED, timesOpened);
		tag.putIntArray(ADDRESS, address.toArray());
		tag.putInt(NETWORK, network);
		tag.putBoolean(RESTRICT_NETWORK, restrictNetwork);
		
		tag.putString(CONNECTION_ID, connectionID);
		
		tag.putString(ID, getID());
		tag.putBoolean(ADD_TO_NETWORK, addToNetwork);

		tag.putLong(ENERGY, this.getEnergyStored());

		tag.putBoolean(DISPLAY_ID, displayID);
		tag.putBoolean(UPGRADED, upgraded);
		
		if(dhdRelativePos.isPresent())
		{
			Vec3i pos = dhdRelativePos.get();
			tag.putIntArray(DHD_POS, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		}
		tag.putInt(AUTOCLOSE, autoclose);
		
		return tag;
	}
	
	public void addStargateToNetwork()
	{
		if(this.getID().equals(EMPTY) || BlockEntityList.get(level).getStargate(new Address(getID())).isPresent())
		{
			setID(generateID());
		}
		
		BlockEntityList.get(level).addStargate(this);
		StargateNetwork.get(level).addStargate(level.getServer(), this);
		
		addToNetwork = true;
		this.setChanged();
	}
	
	@Override
	public CompoundTag addToBlockEntityList()
	{
		addStargateToNetwork();
		return new CompoundTag();
	}
	
	public CompoundTag addNewToBlockEntityList()
	{
		addStargateToNetwork();
		
		return new CompoundTag();
	}

	@Override
	public void removeFromBlockEntityList()
	{
		//super.removeFromBlockEntityList();
		StargateNetwork.get(level).removeStargate(level, getID());
		BlockEntityList.get(level).removeStargate(new Address(getID()));
	}
	
	@Override
	protected String generateID()
	{
		Random random = new Random();
		String addressString;
		Address address;
		while(true)
		{
			address = new Address().randomAddress(8, 36, random.nextLong());
			
			if(BlockEntityList.get(level).getStargate(address).isEmpty())
				break;
		}
		
		addressString = address.toString();
		return addressString;
	}

	
	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB(getCenterPos().getX() - 3, getCenterPos().getY() - 3, getCenterPos().getZ() - 3, getCenterPos().getX() + 4, getCenterPos().getY() + 4, getCenterPos().getZ() + 4);
    }
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	public int getSymbolBounds()
	{
		return this.symbolBounds;
	}
	
	public boolean isSymbolOutOfBounds(int symbol)
	{
		if(symbol < 0)
			return true;
		
		if(symbol >  getSymbolBounds())
			return true;
		
		return false;
	}
	
	public static int getChevron(AbstractStargateEntity stargate, int chevronNumber)
	{
		if(chevronNumber < 0 || chevronNumber > 8)
			return 0;
		else
			return stargate.getEngagedChevrons()[chevronNumber];
	}
	
	public Stargate.Feedback engageSymbol(int symbol)
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(isSymbolOutOfBounds(symbol))
			return Stargate.Feedback.SYMBOL_OUT_OF_BOUNDS;
		
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
			else
				return setRecentFeedback(Stargate.Feedback.ENCODE_WHEN_CONNECTED);
		}
		
		if(symbol == 0)
			return setRecentFeedback(lockPrimaryChevron());
		else
			return setRecentFeedback(encodeChevron(symbol, false, false));
	}
	
	public Stargate.Feedback encodeChevron(int symbol, boolean incoming, boolean encodeSound)
	{
		if(address.containsSymbol(symbol))
			return setRecentFeedback(Stargate.Feedback.SYMBOL_IN_ADDRESS);
		
		if(!address.canGrow())
			return resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		growAddress(symbol);
		
		chevronSound(false, incoming, false, encodeSound);
		
		if(!incoming)
		{
			updateBasicInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), incoming, symbol);
			updateCrystalInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), incoming, symbol);
		}
		else
		{
			updateBasicInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), incoming);
			updateCrystalInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), incoming);
		}
		updateAdvancedCrystalInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), incoming, symbol);
		this.setChanged();
		
		return Stargate.Feedback.SYMBOL_ENCODED;
	}
	
	protected Stargate.Feedback lockPrimaryChevron()
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(!address.isComplete())
		{
			chevronSound(true, false, false, false);
			return resetStargate(Stargate.Feedback.INCOMPLETE_ADDRESS);
		}
		else if(!isConnected())
		{
			if(!isObstructed())
			{
				chevronSound(true, false, false, false);
				updateInterfaceBlocks(EVENT_CHEVRON_ENGAGED, this.address.getLength() + 1, false, 0);
				return setRecentFeedback(engageStargate(this.getAddress(), true));
			}
			else
				return resetStargate(Stargate.Feedback.SELF_OBSTRUCTED, false);
		}
		else
			return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
		
	}
	
	public void chevronSound(boolean primary, boolean incoming, boolean open, boolean encode)
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Chevron(this.worldPosition, primary, incoming, open, encode));
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
	
	public Stargate.Feedback engageStargate(Address address, boolean doKawoosh)
	{
		return Dialing.dialStargate(this.level, this, address, doKawoosh);
	}
	
	public void connectStargate(String connectionID, ConnectionState connectionState)
	{
		this.connectionID = connectionID;
		this.setConnected(connectionState);
		this.timesOpened++;
		this.animationTick = 0;
		this.setChanged();
		
		this.updateStargate(false);
	}
	
	public static double kawooshFunction(int kawooshTime)
	{
		return 8 * Math.sin(Math.PI * (double) kawooshTime / Connection.KAWOOSH_TICKS);
	}
	
	public void doKawoosh(int kawooshTime)
	{
		setKawooshTickCount(kawooshTime);
		updateClient();
		
		if(kawooshTime > Connection.KAWOOSH_TICKS)
			return;
		
		Direction axisDirection = getDirection().getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
		Direction direction = Orientation.getEffectiveDirection(getDirection(), getOrientation());
		
		double frontMultiplier = kawooshFunction(kawooshTime);
		
		if(CommonStargateConfig.kawoosh_destroys_blocks.get())
			destroyBlocks(frontMultiplier, axisDirection, direction);
		if(CommonStargateConfig.kawoosh_disintegrates_entities.get())
			disintegrateEntities(frontMultiplier, axisDirection, direction);
	}
	
	protected void destroyBlocks(double frontMultiplier, Direction axisDirection, Direction direction)
	{
		BlockPos centerPos = getCenterPos();
		
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
						BlockPos relativePos = pos.relative(direction, i);
						if(!level.getBlockState(relativePos).is(Blocks.AIR))
						{
							BlockState relativeState = level.getBlockState(relativePos);
							if(!relativeState.is(TagInit.Blocks.KAWOOSH_IMMUNE))
								level.destroyBlock(relativePos, false);
						}
					}
				}
			}
		}
	}
	
	protected void disintegrateEntities(double frontMultiplier, Direction axisDirection, Direction direction)
	{
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
			if(shouldDisintegrate(entity))
				entity.kill();
		});
	}
	
	public boolean shouldDisintegrate(Entity entity)
	{
		if(entity instanceof Player player && player.isCreative())
			return false;
		
		if(!CommonStargateConfig.kawoosh_disintegrates_items.get() && entity instanceof ItemEntity)
			return false;
		
		if(entity.getType().is(TagInit.Entities.KAWOOSH_IMMUNE))
			return false;
		
		return true;
	}
	
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback, boolean updateInterfaces)
	{
		if(isConnected())
		{
			closeWormholeSound();
			setConnected(ConnectionState.IDLE);
		}

		resetAddress(updateInterfaces);
		this.connectionID = EMPTY;
		setKawooshTickCount(0);
		setTickCount(0);
		updateClient();
		
		if(feedback.playFailSound() && !level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Fail(this.worldPosition));
		
		if(updateInterfaces)
		{
			updateBasicInterfaceBlocks(EVENT_RESET, feedback.getCode());
			updateCrystalInterfaceBlocks(EVENT_RESET, feedback.getCode(), feedback.getMessage());
			updateAdvancedCrystalInterfaceBlocks(EVENT_RESET, feedback.getCode(), feedback.getMessage());
		}
		
		if(this.dhdRelativePos.isPresent())
		{
			Optional<BlockPos> dhdPos = getDHDPos();
			
			if(dhdPos.isPresent() && !(this.getLevel().getBlockEntity(dhdPos.get()) instanceof AbstractDHDEntity))
				unsetDHD(true);
				
		}
		
		setChanged();
		StargateJourney.LOGGER.info("Reset Stargate at " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ() + " " + this.getLevel().dimension().location().toString() + " " + feedback.getMessage());
		return setRecentFeedback(feedback);
	}
	
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback)
	{
		return resetStargate(feedback, true);
	}
	
	public Stargate.Feedback disconnectStargate(Stargate.Feedback feedback)
	{
		if(this.isConnected())
		{
			if(!CommonStargateConfig.end_connection_from_both_ends.get() && !this.isDialingOut())
				return Stargate.Feedback.WRONG_DISCONNECT_SIDE;
			else if(this.getOpenTime() <= 0)
				return Stargate.Feedback.CONNECTION_FORMING;
		}
		
		return bypassDisconnectStargate(feedback);
	}
	
	public Stargate.Feedback bypassDisconnectStargate(Stargate.Feedback feedback)
	{
		if(connectionID != null && !connectionID.equals(EMPTY))
			StargateNetwork.get(level).terminateConnection(level.getServer(), connectionID, feedback);
		return resetStargate(feedback, false);
	}
	
	public void updateStargate(boolean updateInterfaces)
	{
		updateStargate(this.level, this.getID(), this.timesOpened, this.hasDHD(), updateInterfaces);
	}
	
	private void updateStargate(Level level, String id, int timesOpened, boolean hasDHD, boolean updateInterfaces)
	{
		if(level.isClientSide())
			return;
			
		StargateNetwork.get(level).updateStargate(level, id, timesOpened, hasDHD);
		setStargateState(this.getConnectionState(), this.getChevronsEngaged(), updateInterfaces);
	}
	
	protected void growAddress(int symbol)
	{
		this.address.addSymbol(symbol);
		setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true);
		updateClient();
	}
	
	protected void resetAddress(boolean updateInterfaces)
	{
		this.address.reset();
		engagedChevrons = Dialing.DEFAULT_CHEVRON_CONFIGURATION;
		setStargateState(ConnectionState.IDLE, 0, updateInterfaces);
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
		
		sendDHDFeedback(feedback);
		updateDHD();
		
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
		
		if(split.length != 2)
			return false;
		
		if(!ResourceLocation.isValidNamespace(split[0]))
			return false;
		
		return ResourceLocation.isValidPath(split[1]);
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public int getNetwork()
	{
		return this.network;
	}
	
	public void setNetwork(int network)
	{
		this.network = network;
	}
	
	public boolean getRestrictNetwork()
	{
		return this.restrictNetwork;
	}
	
	public void setRestrictNetwork(boolean restrictNetwork)
	{
		this.restrictNetwork = restrictNetwork;
	}
	
	public boolean isRestricted(AbstractStargateEntity dialingStargate)
	{
		if(this.getRestrictNetwork())
			return dialingStargate.getNetwork() != this.getNetwork();
		
		return false;
	}

	public int getMaxGateOpenTime()
	{
		return CommonStargateConfig.max_wormhole_open_time.get() * 20;
	}
	
	public Stargate.Gen getGeneration()
	{
		return this.generation;
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
	
	public void setDHD(AbstractDHDEntity dhd, int autoclose)
	{
		Direction direction = this.getDirection();
		
		if(dhd != null && direction != null)
		{
			if(this.dhd.isEmpty() || (this.dhd.isPresent() && this.dhd.get() == dhd))
			{
				Vec3i relativeOffset = CoordinateHelper.Relative.getRelativeOffset(direction, this.getBlockPos(), dhd.getBlockPos());
				
				this.dhdRelativePos = Optional.of(relativeOffset);
				this.dhd = Optional.of(dhd);
				
				updateStargate(this.level, this.getID(), this.timesOpened, true, false);
				updateDHD();
			}
			
			this.autoclose = autoclose;
		}
		
		this.setChanged();
	}
	
	public void unsetDHD(boolean notifyDHD)
	{
		if(notifyDHD && this.dhd.isPresent())
			this.dhd.get().unsetStargate();
		
		this.dhd = Optional.empty();
		this.dhdRelativePos = Optional.empty();
		this.autoclose = 0;
		
		updateStargate(this.level, this.getID(), this.timesOpened, false, false);
		updateDHD();
		
		this.setChanged();
	}
	
	public Optional<BlockPos> getDHDPos()
	{
		if(this.dhdRelativePos.isEmpty())
			return Optional.empty();
		
		return Optional.of(CoordinateHelper.Relative.getOffsetPos(this.getDirection(), this.getBlockPos(), this.dhdRelativePos.get()));
	}
	
	public void loadDHD()
	{
		Optional<BlockPos> dhdPos = getDHDPos();
		
		if(dhdPos.isEmpty())
			return;
		
		if(this.getLevel().getBlockEntity(dhdPos.get()) instanceof AbstractDHDEntity dhd)
			this.dhd = Optional.of(dhd);
		
        updateDHD();
		
		this.setChanged();
	}
	
	public int autoclose()
	{
		return this.autoclose;
	}
	
	public boolean hasDHD()
	{
		if(this.dhd.isPresent())
		{
			if(this.dhd.get() != null)
				return true;
			else
				unsetDHD(true);
		}
		
		return false;
	}
	
	public void updateDHD()
	{
		if(hasDHD())
			this.dhd.get().updateDHD(!this.isConnected() || (this.isConnected() && this.isDialingOut()) ? 
					address : new Address(), this.isConnected());
	}
	
	public void sendDHDFeedback(Stargate.Feedback feedback)
	{
		if(hasDHD() && feedback.isError())
			this.dhd.get().sendMessageToNearbyPlayers(feedback.getFeedbackMessage(), 5);
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
		this.setChanged();
	}
	
	public String getPointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	public void setSymbols(String symbols)
	{
		this.symbols = symbols;
		this.setChanged();
	}
	
	public String getSymbols()
	{
		return this.symbols;
	}
	
	public void setVariant(String variant)
	{
		this.variant = variant;
		this.setChanged();
	}
	
	public String getVariant()
	{
		return this.variant;
	}
	
	public void setAddress(Address address)
	{
		this.address = address;
		this.setChanged();
	}
	
	public Address getAddress()
	{
		return this.address;
	}
	
	public int getChevronsEngaged()
	{
		int chevronsEngaged = this.address.getLength();
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
			return Dialing.DIALED_7_CHEVRON_CONFIGURATION;
		case 7:
			return Dialing.DIALED_8_CHEVRON_CONFIGURATION;
		case 8:
			return Dialing.DIALED_9_CHEVRON_CONFIGURATION;
		default:
			return Dialing.DEFAULT_CHEVRON_CONFIGURATION;
		}
	}
	
	public int[] getEngagedChevrons()
	{
		return this.engagedChevrons;
	}
	
	public int chevronsRendered()
	{
		return this.address.getLength();
	}
	
	//============================================================================================
	//**************************************Blockstate stuff**************************************
	//============================================================================================
	
	public BlockPos getCenterPos()
	{
		if(this.centerPosition == null)
		{
			BlockPos mainBlockPos = this.getBlockPos();
    		Direction centerDirection = Orientation.getCenterDirection(getDirection(), getOrientation());
    		this.centerPosition = mainBlockPos.relative(centerDirection, 3);
		}
    	
    	return this.centerPosition;
	}
    
    public Vec3 getCenter()
    {
    	BlockPos centerPos = getCenterPos();
    	
    	double y = getVerticalCenterHeight();
    	Orientation orientation = getOrientation();
    	
    	if(orientation != null && orientation != Orientation.REGULAR)
    		y = getHorizontalCenterHeight();
    	
    	return new Vec3(
    			centerPos.getX() + 0.5, 
    			centerPos.getY() + y, 
    			centerPos.getZ() + 0.5);
    }
    
    public Vec3 getRelativeCenter()
    {
    	BlockPos mainBlockPos = this.getBlockPos();
    	BlockPos centerPos = getCenterPos();
    	
    	double y = getVerticalCenterHeight();
    	Orientation orientation = getOrientation();
    	
    	if(orientation != null && orientation != Orientation.REGULAR)
    		y = getHorizontalCenterHeight();
    	
    	return new Vec3(
    			centerPos.getX() - mainBlockPos.getX() + 0.5, 
    			centerPos.getY() - mainBlockPos.getY() + y, 
    			centerPos.getZ() - mainBlockPos.getZ() + 0.5);
    }
    
    protected BlockState getState()
    {
    	BlockPos gatePos = this.getBlockPos();
		return this.level.getBlockState(gatePos);
    }
	
	public Orientation getOrientation()
	{
		if(this.orientation == null)
		{
			BlockState gateState = getState();
			
			if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
				this.orientation = gateState.getValue(AbstractStargateBaseBlock.ORIENTATION);
			else
				StargateJourney.LOGGER.error("Couldn't find Stargate Orientation");
		}

		return this.orientation;
	}
	
	public Direction getDirection()
	{
		if(this.direction == null)
		{
			BlockState gateState = getState();
			
			if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
				this.direction = gateState.getValue(AbstractStargateBaseBlock.FACING);
			else
				StargateJourney.LOGGER.error("Couldn't find Stargate Direction");
		}
		
		return this.direction;
	}
	
	public void setConnected(ConnectionState connectionState)
	{
		setStargateState(connectionState, this.getChevronsEngaged(), true);
	}
	
	public void setStargateState(ConnectionState connectionState, int chevronsEngaged, boolean updateInterfaces)
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = getState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargate)
		{
			stargate.updateStargate(level, gatePos, gateState, connectionState, chevronsEngaged);
			if(updateInterfaces)
				updateInterfaceBlocks(null);
		}
		else
			StargateJourney.LOGGER.error("Couldn't find Stargate");
		setChanged();
		
	}
	
	public ConnectionState getConnectionState()
	{
		BlockState gateState = getState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
			return gateState.getValue(AbstractStargateBaseBlock.CONNECTION_STATE);
		
		return ConnectionState.IDLE;
	}
	
	public boolean isConnected()
	{
		return getConnectionState().isConnected();
	}
	
	public boolean isDialingOut()
	{
		return getConnectionState().isDialingOut();
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
		return obstructingBlocks >= CommonStargateConfig.max_obstructive_blocks.get();
	}
	
    public void updateBasicInterfaceBlocks(@Nullable String eventName, Object... objects)
    {
    	BlockPos gatePos = this.getBlockPos();
		BlockState gateState = getState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			for(StargatePart part : stargateBlock.getParts())
			{
				BlockPos ringPos = part.getRingPos(gatePos, gateState.getValue(AbstractStargateBlock.FACING), gateState.getValue(AbstractStargateBlock.ORIENTATION));
				
				for(Direction direction : Direction.values())
		    	{
					BlockPos pos = ringPos.relative(direction);
		    		if(level.getBlockEntity(pos) instanceof BasicInterfaceEntity interfaceEntity)
		    		{
		    			if(eventName != null)
		    				interfaceEntity.queueEvent(eventName, objects);
		    			level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
		    			interfaceEntity.setChanged();
		    		}
		    	}
			}
		}
    }
	
    public void updateCrystalInterfaceBlocks(@Nullable String eventName, Object... objects)
    {
    	BlockPos gatePos = this.getBlockPos();
		BlockState gateState = getState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			for(StargatePart part : stargateBlock.getParts())
			{
				BlockPos ringPos = part.getRingPos(gatePos, gateState.getValue(AbstractStargateBlock.FACING), gateState.getValue(AbstractStargateBlock.ORIENTATION));
				
				for(Direction direction : Direction.values())
		    	{
					BlockPos pos = ringPos.relative(direction);
		    		if(level.getBlockEntity(pos) instanceof CrystalInterfaceEntity interfaceEntity)
		    		{
		    			if(eventName != null)
		    				interfaceEntity.queueEvent(eventName, objects);
		    			level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
		    			interfaceEntity.setChanged();
		    		}
		    	}
			}
		}
    }
	
    public void updateAdvancedCrystalInterfaceBlocks(@Nullable String eventName, Object... objects)
    {
    	BlockPos gatePos = this.getBlockPos();
		BlockState gateState = getState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			for(StargatePart part : stargateBlock.getParts())
			{
				BlockPos ringPos = part.getRingPos(gatePos, gateState.getValue(AbstractStargateBlock.FACING), gateState.getValue(AbstractStargateBlock.ORIENTATION));
				
				for(Direction direction : Direction.values())
		    	{
					BlockPos pos = ringPos.relative(direction);
		    		if(level.getBlockEntity(pos) instanceof AdvancedCrystalInterfaceEntity interfaceEntity)
		    		{
		    			if(eventName != null)
		    				interfaceEntity.queueEvent(eventName, objects);
		    			level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
		    			interfaceEntity.setChanged();
		    		}
		    	}
			}
		}
    }
	
	public void updateInterfaceBlocks(@Nullable String eventName, Object... objects)
	{
		updateBasicInterfaceBlocks(eventName, objects);
		updateCrystalInterfaceBlocks(eventName, objects);
		updateAdvancedCrystalInterfaceBlocks(eventName, objects);
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
	
	public abstract Stargate.ChevronLockSpeed getChevronLockSpeed();
	
	public abstract SoundEvent getRotationSound();
	
	public abstract SoundEvent getChevronEngageSound();
	
	public abstract SoundEvent getPrimaryChevronEngageSound();
	
	public abstract SoundEvent getChevronIncomingSound();
	
	public abstract SoundEvent getPrimaryChevronIncomingSound();
	
	public abstract SoundEvent getWormholeOpenSound();
	
	public abstract SoundEvent getWormholeIdleSound();
	/*{
		return SoundInit.WORMHOLE_IDLE.get();
	}*/
	
	public abstract SoundEvent getWormholeCloseSound();

	public abstract SoundEvent getFailSound();

	//TODO Add it to Stargates other than Universe Stargate
	public SoundEvent getStartupSound()
	{
		return SoundInit.EMPTY_SOUND_INSTANCE;
	}
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide())
			return;
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": " + pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.open_time").append(Component.literal(": " + getOpenTime() + "/" + getMaxGateOpenTime())).withStyle(ChatFormatting.DARK_AQUA));
		player.sendSystemMessage(Component.translatable("info.sgjourney.times_opened").append(Component.literal(": " + timesOpened)).withStyle(ChatFormatting.BLUE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.has_dhd").append(Component.literal(": " + hasDHD())).withStyle(ChatFormatting.GOLD));
		player.sendSystemMessage(Component.translatable("info.sgjourney.autoclose").append(Component.literal(": " + autoclose())).withStyle(ChatFormatting.RED));
		player.sendSystemMessage(Component.translatable("info.sgjourney.last_traveler_time").append(Component.literal(": " + getTimeSinceLastTraveler())).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.address").append(Component.literal(": " + address.toString())).withStyle(ChatFormatting.GREEN));
		player.sendSystemMessage(Component.translatable("info.sgjourney.recent_feedback").append(Component.literal(": ").append(getRecentFeedback().getFeedbackMessage())).withStyle(ChatFormatting.WHITE));
		
		super.getStatus(player);
	}
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return false;
	}

	@Override
	public long capacity()
	{
		return CommonStargateConfig.stargate_energy_capacity.get();
	}

	@Override
	public long maxReceive()
	{
		return CommonStargateConfig.stargate_energy_max_receive.get();
	}

	@Override
	public long maxExtract()
	{
		return CommonStargateConfig.intergalactic_connection_energy_cost.get();
	}
	
	public float getVerticalCenterHeight()
	{
		return this.verticalCenterHeight;
	}
	
	public float getHorizontalCenterHeight()
	{
		return this.horizontalCenterHeight;
	}
	
	public double getGateAddition()
	{
		return this.getOrientation() == Orientation.REGULAR
				? getVerticalCenterHeight() : getHorizontalCenterHeight();
	}
	
	public abstract void registerInterfaceMethods(StargatePeripheralWrapper wrapper);
	
	public void doWhileDialed(int openTime, Stargate.ChevronLockSpeed chevronLockSpeed) {}
	
	public void updateClient()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateUpdatePacket(this.worldPosition, this.address.toArray(), this.engagedChevrons, this.kawooshTick, this.animationTick, this.pointOfOrigin, this.symbols, this.variant));
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractStargateEntity stargate)
    {
		if(stargate.isConnected())
			stargate.increaseTickCount();
		
		if(level.isClientSide())
			return;
		
		stargate.updateClient();
    }
}
