package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.*;

import javax.annotation.Nullable;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.DamageSourceInit;
import net.povstalec.sgjourney.common.misc.*;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.info.AddressFilterInfo;
import net.povstalec.sgjourney.common.sgjourney.info.SymbolInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.sound.SoundWrapper;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blocks.tech_interface.AbstractInterfaceBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.CommonTransmissionConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.StatisticsInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundStargateParticleSpawnPacket;

public abstract class AbstractStargateEntity<SG extends BlockEntityStargate<?>> extends EnergyBlockEntity implements ITransmissionReceiver, StructureGenEntity,
		SymbolInfo.Interface, AddressFilterInfo.Interface, ProtectedBlockEntity, PDAStatus, AutoCache.IReceiver<AbstractDHDEntity, AbstractStargateEntity<?>>
{
	public static final String EMPTY = StargateJourney.EMPTY;
	public static final String ID = "ID"; //TODO For legacy reasons
	public static final String ID_9_CHEVRON_ADDRESS = "9ChevronAddress";
	
	public static final String EVENT_STARGATE_ROTATION_STARTED = "stargate_rotation_started";
	public static final String EVENT_STARGATE_ROTATION_STOPPED = "stargate_rotation_stopped";
	public static final String EVENT_CHEVRON_ENGAGED = "stargate_chevron_engaged";
	public static final String EVENT_STARGATE_ENGAGED = "stargate_stargate_engaged";
	public static final String EVENT_RESET = "stargate_reset";
	public static final String EVENT_MESSAGE_RECEIVED = "stargate_message_received";

	public static final String ADDRESS = "Address";
	public static final String ENERGY = "Energy";
	
	// Connections
	public static final String CONNECTION_STATE = "connection_state";
	public static final String CONNECTION_ID = "ConnectionID";
	public static final String NETWORKS = "networks";
	public static final String RESTRICT_NETWORK = "restrict_network";
	public static final String TIMES_OPENED = "TimesOpened";
	public static final String AUTOCLOSE = "Autoclose";
	
	// Upgrading and variants
	public static final String UPGRADED = "Upgraded";
	public static final String DISPLAY_ID = "DisplayID";
	public static final String VARIANT = "Variant";
	public static final String LOCAL_POINT_OF_ORIGIN = "local_point_of_origin";
	public static final String PRIMARY = "primary";
	
	public static final String COVER_BLOCKS = "CoverBlocks";
	
	public static final String ENGAGED_CHEVRONS = "engaged_chevrons";
	
	public static final String DHD_POS = "dhd_pos";
	
	public static final boolean FORCE_LOAD_CHUNK = CommonStargateConfig.stargate_loads_chunk_when_connected.get();
	
	public static final int SEGMENTS = 3;

	public static final float STANDARD_THICKNESS = 9.0F;
	public static final float VERTICAL_CENTER_STANDARD_HEIGHT = 0.5F;
	public static final float HORIZONTAL_CENTER_STANDARD_HEIGHT = (STANDARD_THICKNESS / 2) / 16;
	
	public static final long MIN_DHD_SEARCH_DISTANCE = 64;
	
	private final StargateType<SG> stargateType;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	// Stargate destruction
	protected boolean isItemDropped = false;
	
	// Basic Info
	protected Address.Immutable id9ChevronAddress = new Address.Immutable();
	
	protected int totalSymbols;
	public final SymbolMap symbolMap;
	
	protected Trinary restrictNetwork = Trinary.DEFAULT;
	protected Set<Integer> networks = new TreeSet<>();
	public final int defaultNetwork;
	
	// Blockstate values
	protected BlockPos centerPosition;
	protected Direction direction;
	protected Orientation orientation;
	
	// Used during gameplay
	protected StargateInfo.FeedbackMessage recentFeedback = StargateInfo.Feedback.NONE.withInfo();
	protected int kawooshTick = 0;
	protected int[] engagedChevrons = Dialing.DEFAULT_CHEVRON_CONFIGURATION;
	protected int timesOpened = 0;
	protected int openTime = 0;
	protected int timeSinceLastTraveler = 0;
	// Stuff mainly for client
	protected Random random = new Random();
	protected int animationTick = 0;
	protected int unstableTicks = 0;
	protected int disconnectTicks = 0;
	
	protected ResourceLocation variant = StargateJourney.EMPTY_LOCATION;
	private final ResourceLocation defaultVariant;
	
	// Dialing and memory
	protected Address.Mutable address = new Address.Mutable();
	@Nullable
	protected UUID connectionID = null;
	protected StargateConnection.State connectionState = StargateConnection.State.IDLE;

	protected int openSoundLead = 28;
	protected float verticalCenterHeight;
	protected float horizontalCenterHeight;
	
	@Nullable
	public SoundWrapper wormholeIdleSound = null;
	@Nullable
	public SoundWrapper wormholeOpenSound = null;
	@Nullable
	public SoundWrapper spinSound = null;
	
	protected boolean displayID = false;
	protected boolean upgraded = false;
	protected boolean localPointOfOrigin = false;
	protected boolean isPrimary = false;
	protected boolean isProtected = false;
	
	public StargateBlockCover blockCover = new StargateBlockCover(StargatePart.DEFAULT_PARTS);
	
	@Nullable
	protected Vec3i dhdRelativePos = null;
	protected long dhdSearchDistance = MIN_DHD_SEARCH_DISTANCE;
	public final AutoCache.Controller<AbstractDHDEntity, AbstractStargateEntity<?>> dhdCache = new AutoCache.Controller<>(this);
	
	protected SymbolInfo symbolInfo;
	protected AddressFilterInfo addressFilterInfo;
	//protected ShieldInfo shieldInfo;

	public AbstractStargateEntity(BlockEntityType<?> blockEntityType, StargateType<SG> stargateType, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
								  int totalSymbols, int defaultNetwork, float verticalCenterHeight, float horizontalCenterHeight)
	{
		super(blockEntityType, pos, state);
		this.stargateType = stargateType;
		
		this.defaultVariant = defaultVariant;
		
		this.totalSymbols = totalSymbols;
		this.symbolMap = new SymbolMap(totalSymbols);
		
		this.defaultNetwork = defaultNetwork;
		
		this.verticalCenterHeight = verticalCenterHeight;
		this.horizontalCenterHeight = horizontalCenterHeight;
		
		this.symbolInfo = new SymbolInfo();
		this.addressFilterInfo = new AddressFilterInfo();
	}

	public AbstractStargateEntity(BlockEntityType<?> blockEntityType, StargateType<SG> stargateType, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
								  int totalSymbols, int defaultNetwork)
	{
		this(blockEntityType, stargateType, defaultVariant, pos, state, totalSymbols, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
	}
	
	@Override
    public void onLoad()
	{
		if(level.isClientSide())
		{
			// Anything goes, DHD is responsible for taking care of everything on client
			dhdCache.setRevalidate(() -> true);
			dhdCache.setFetch(dhdCache::getCached);
		}
		else
		{
			setupServerAutoCache();
			
			checkStargate();
			
			if(generationStep == Step.READY)
				generate();
		}
		
		super.onLoad();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		else if(tag.contains("AddToNetwork"))
			generationStep = Step.SETUP;
		
		connectionState = StargateConnection.State.fromByte(tag.getByte(CONNECTION_STATE));
		if(tag.contains(CONNECTION_ID, CompoundTag.TAG_STRING))
		{
			try { connectionID = UUID.fromString(tag.getString(CONNECTION_ID)); }
			catch(IllegalArgumentException e) { StargateJourney.LOGGER.error("Unable to load Stargate Connection UUID", e); }
		}
		
		if(tag.contains(DHD_POS, Tag.TAG_INT_ARRAY))
			dhdRelativePos = Conversion.intArrayToVec(tag.getIntArray(DHD_POS));
		else
			dhdRelativePos = null;
		
		deserializeStargateInfo(tag, false);
	}
	
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		super.load(tag);
		
		timesOpened = tag.getInt(TIMES_OPENED);
		address.fromArray(tag.getIntArray(ADDRESS));
		
		restrictNetwork = Trinary.fromInt(tag.getByte(RESTRICT_NETWORK));
		if(tag.contains("Network", Tag.TAG_INT)) //TODO Keeping this here for the time being for legacy reasons
			networks = new TreeSet<>(List.of(tag.getInt("Network")));
		else if(tag.contains(NETWORKS, Tag.TAG_INT_ARRAY))
			networks = new TreeSet<>(Arrays.stream(tag.getIntArray(NETWORKS)).boxed().toList());
		
		if(tag.contains(ID)) //TODO Keeping this here for the time being for legacy reasons
			id9ChevronAddress = Address.Immutable.extendWithPointOfOrigin(new Address.Immutable(tag.getString(ID)));
		else
			id9ChevronAddress = Address.Immutable.extendWithPointOfOrigin(new Address.Immutable(tag.getIntArray(ID_9_CHEVRON_ADDRESS)));
		
		if(tag.contains(DISPLAY_ID))
			displayID = tag.getBoolean(DISPLAY_ID);
		
		if(isUpgraded)
			upgraded = true;
		else if(tag.contains(UPGRADED))
			upgraded = tag.getBoolean(UPGRADED);
		
		if(tag.contains(LOCAL_POINT_OF_ORIGIN))
			localPointOfOrigin = tag.getBoolean(LOCAL_POINT_OF_ORIGIN);
		
		if(tag.contains(PRIMARY))
			isPrimary = tag.getBoolean(PRIMARY);
		
		if(tag.contains(PROTECTED))
			isProtected = tag.getBoolean(PROTECTED);
		
		variant = new ResourceLocation(tag.getString(VARIANT));
		
		addressFilterInfo().deserializeFilters(tag);
		
		blockCover.deserializeNBT(tag.getCompound(COVER_BLOCKS));
		
		symbolMap.loadFromCompoundTag(tag);
		
		/*shieldProgress = tag.getShort(SHIELD_PROGRESS);
		oldShieldProgress = shieldProgress;
		shieldItemHandler.deserializeNBT(tag.getCompound(SHIELD_INVENTORY));*/
		
    	this.setChanged();
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		tag.putByte(CONNECTION_STATE, connectionState.byteValue());
		if(connectionID != null)
			tag.putString(CONNECTION_ID, connectionID.toString());
		
		if(dhdRelativePos != null)
			tag.putIntArray(DHD_POS, Conversion.vecToIntArray(dhdRelativePos));
		
		serializeStargateInfo(tag);
	}
	
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		tag.putInt(TIMES_OPENED, timesOpened);
		tag.putIntArray(ADDRESS, address.getArray());
		
		tag.putByte(RESTRICT_NETWORK, restrictNetwork.value);
		if(!networks.isEmpty())
			tag.putIntArray(NETWORKS, networks.stream().toList());
		
		tag.putIntArray(ID_9_CHEVRON_ADDRESS, id9ChevronAddress.toArray());

		tag.putLong(ENERGY, this.energyStorage.getTrueEnergyStored());
		
		if(displayID)
			tag.putBoolean(DISPLAY_ID, true);
		if(upgraded)
			tag.putBoolean(UPGRADED, true);
		if(localPointOfOrigin)
			tag.putBoolean(LOCAL_POINT_OF_ORIGIN, true);
		if(isPrimary)
			tag.putBoolean(PRIMARY, true);
		if(isProtected)
			tag.putBoolean(PROTECTED, true);

		tag.putString(VARIANT, variant.toString());
		
		addressFilterInfo().serializeFilters(tag);
		
		tag.put(COVER_BLOCKS, blockCover.serializeNBT());
		
		symbolMap.saveToCompoundTag(tag);
		
		/*tag.putShort(SHIELD_PROGRESS, shieldProgress);
		tag.put(SHIELD_INVENTORY, shieldItemHandler.serializeNBT());*/
		
		super.saveAdditional(tag);
		
		return tag;
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putLong(ENERGY, this.energyStorage.getTrueEnergyStored());
		
		tag.putIntArray(ADDRESS, address.getArray());
		tag.putIntArray(ENGAGED_CHEVRONS, engagedChevrons);
		
		tag.putByte(RESTRICT_NETWORK, restrictNetwork.value);
		tag.putIntArray(NETWORKS, networks.stream().toList());
		
		tag.putString(VARIANT, variant.toString());
		// Ticks
		tag.putInt(StargateConnection.KAWOOSH_TICKS, kawooshTick);
		tag.putInt(StargateConnection.OPEN_TIME, openTime);
		tag.putInt(StargateConnection.TIME_SINCE_LAST_TRAVELER, timeSinceLastTraveler);
		
		tag.putByte(CONNECTION_STATE, connectionState.byteValue());
		tag.put(COVER_BLOCKS, blockCover.serializeNBT());
		
		symbolMap.saveToCompoundTag(tag);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			energyStorage.setEnergy(tag.getLong(ENERGY));
			
			address.fromArray(tag.getIntArray(ADDRESS));
			engagedChevrons = tag.getIntArray(ENGAGED_CHEVRONS);
			
			restrictNetwork = Trinary.fromInt(tag.getByte(RESTRICT_NETWORK));
			networks = new TreeSet<>(Arrays.stream(tag.getIntArray(NETWORKS)).boxed().toList());
			
			variant = new ResourceLocation(tag.getString(VARIANT));
			// Ticks
			kawooshTick = tag.getInt(StargateConnection.KAWOOSH_TICKS);
			openTime = tag.getInt(StargateConnection.OPEN_TIME);
			timeSinceLastTraveler = tag.getInt(StargateConnection.TIME_SINCE_LAST_TRAVELER);
			
			StargateConnection.State oldConnectionState = connectionState;
			connectionState = StargateConnection.State.fromByte(tag.getByte(CONNECTION_STATE));
			if(oldConnectionState.isConnected() && !connectionState.isConnected()) // Stargate is no longer connected
				this.disconnectTicks++;
			
			blockCover.deserializeNBT(tag.getCompound(COVER_BLOCKS));
			
			symbolMap.loadFromCompoundTag(tag);
		}
	}
	
	public final StargateType<SG> getStargateType()
	{
		return this.stargateType;
	}
	
	public void addStargateToNetwork()
	{
		if(id9ChevronAddress.getType() != Address.Type.ADDRESS_9_CHEVRON || BlockEntityList.get(level).containsStargate(id9ChevronAddress))
			set9ChevronAddress(Address.Immutable.extendWithPointOfOrigin(BlockEntityList.get(level).generate9ChevronAddress(level.getRandom())));
		
		StargateNetwork.get(level).addStargateEntity(this);
		this.setChanged();
	}
	
	public void removeStargateFromNetwork()
	{
		StargateNetwork.get(level).removeStargate(id9ChevronAddress);
	}
	
	public void set9ChevronAddress(Address.Immutable address)
	{
		this.id9ChevronAddress = address;
		setChanged();
		StargateJourney.LOGGER.debug("Set 9-Chevron Address to " + this.id9ChevronAddress);
	}
	
	public Address.Immutable get9ChevronAddress()
	{
		return id9ChevronAddress;
	}

	
	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB(getCenterPos().getX() - 3, getCenterPos().getY() - 3, getCenterPos().getZ() - 3, getCenterPos().getX() + 4, getCenterPos().getY() + 4, getCenterPos().getZ() + 4);
    }
	
	@Nullable
	public Stargate getStargate()
	{
		return BlockEntityList.get(level).getStargate(this.id9ChevronAddress);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	protected LazyOptional<Stargate> getStargateCapability()
	{
		Stargate stargate = getStargate();
		
		if(stargate == null)
			return LazyOptional.empty();
		
		return LazyOptional.of(() -> stargate);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == Stargate.STARGATE_CAPABILITY)
			return getStargateCapability().cast();
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	public int totalSymbols()
	{
		return this.totalSymbols;
	}
	
	public boolean isSymbolOutOfBounds(int symbol)
	{
		return symbolMap.isSymbolOutOfBounds(symbol);
	}
	
	public static int getChevron(AbstractStargateEntity<?> stargate, int chevronNumber)
	{
		chevronNumber--;
		if(chevronNumber < 0 || chevronNumber >= 8)
			return 0;
		else
			return stargate.getEngagedChevrons()[chevronNumber];
	}
	
	/**
	 * Method to engage symbols that also allows the Stargate to do extra stuff, like rotate before encoding the symbols fully
	 * @param symbol Symbol to be encoded
	 * @param canEngageStargate If true, encoding the Point of Origin will automatically engage the Stargate
	 * @return Feedback from encoding the symbol
	 */
	public StargateInfo.FeedbackMessage indirectEngageSymbol(int symbol, boolean canEngageStargate)
	{
		return directEngageSymbol(symbol, canEngageStargate);
	}
	
	/**
	 * Method to engage symbols that doesn't allow the Stargate to do any extra stuff
	 * @param symbol Symbol to be encoded
	 * @param canEngageStargate If true, encoding the Point of Origin will automatically engage the Stargate
	 * @return Feedback from encoding the symbol
	 */
	public StargateInfo.FeedbackMessage directEngageSymbol(int symbol, boolean canEngageStargate)
	{
		if(isSymbolOutOfBounds(symbol))
			return setRecentFeedback(StargateInfo.Feedback.SYMBOL_OUT_OF_BOUNDS.withInfo(symbol));
		
		return encodeSymbol(symbolMap.getMappedSymbol(symbol), canEngageStargate);
	}
	
	/**
	 * Method to encode symbols and handle edge cases involving Point of Origin
	 * @param symbol Symbol to be encoded
	 * @param canEngageStargate If true, encoding the Point of Origin will automatically engage the Stargate
	 * @return Feedback from encoding the symbol
	 */
	protected StargateInfo.FeedbackMessage encodeSymbol(int symbol, boolean canEngageStargate)
	{
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo());
			else
				return setRecentFeedback(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED.withInfo());
		}
		
		StargateInfo.FeedbackMessage feedback = encodeChevron(symbol, false, false);
		
		if(canEngageStargate && getAddress().hasPointOfOriginOrMaxLength() && !feedback.feedback().isError())
			return engageStargate();
		
		return setRecentFeedback(feedback);
	}
	
	protected StargateInfo.FeedbackMessage encodeChevron(int symbol, boolean incoming, boolean encodeSound)
	{
		if(address.containsSymbol(symbol)) // Address already contains the encoded symbol
			return setRecentFeedback(StargateInfo.Feedback.SYMBOL_IN_ADDRESS.withInfo(symbol));
		
		if(!growAddress(symbol)) // Trying to encode 10th symbol (impossible)
			return resetStargate(StargateInfo.Feedback.INVALID_ADDRESS);
		
		chevronSound((short) getAddress().getLength(), incoming, false, encodeSound);
		
		if(!incoming)
		{
			updateBasicInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), false, symbol);
			updateCrystalInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), false, symbol);
		}
		else
		{
			updateBasicInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), true);
			updateCrystalInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), true);
		}
		updateAdvancedCrystalInterfaceBlocks(EVENT_CHEVRON_ENGAGED, address.getLength(), getChevron(this, address.getLength()), incoming, symbol);
		this.setChanged();
		
		return setRecentFeedback(StargateInfo.Feedback.SYMBOL_ENCODED.withInfo(symbol));
	}
	
	public StargateInfo.FeedbackMessage dhdEngageStargate() // Engages the Stargate if all chevrons are encoded, or informs it that it can engage once the last chevron is encoded
	{
		return engageStargate();
	}
	
	public StargateInfo.FeedbackMessage engageStargate()
	{
		if(!getAddress().canBeDialed()) // Address is too short or does not contain a Point of Origin
			return resetStargate(makeDialAttempt(StargateInfo.Feedback.INCOMPLETE_ADDRESS.withInfo()));
		else if(!isConnected())
		{
			if(!isObstructed())
			{
				updateInterfaceBlocks(EVENT_STARGATE_ENGAGED, getAddress().toList());
				return setRecentFeedback(makeDialAttempt(engageStargate(getAddress(), true)));
			}
			else
				return resetStargate(makeDialAttempt(StargateInfo.Feedback.SELF_OBSTRUCTED.withInfo()));
		}
		else
			return disconnectStargate(makeDialAttempt(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo()));
	}
	
	public StargateInfo.FeedbackMessage makeDialAttempt(StargateInfo.FeedbackMessage feedback)
	{
		dhdCache.ifPresent(dhd -> dhd.onDialAttempt(feedback, getAddress()));
		return feedback;
	}
	
	public void chevronSound(short chevron, boolean incoming, boolean open, boolean encode)
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Chevron(this.worldPosition, chevron, incoming, open, encode));
	}
	
	public void openWormholeSound(boolean incoming)
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.OpenWormhole(this.worldPosition, incoming));
	}
	
	public void idleWormholeSound(boolean incoming)
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.IdleWormhole(this.worldPosition, incoming));
	}
	
	public void closeWormholeSound(boolean incoming)
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.CloseWormhole(this.worldPosition, incoming));
	}
	
	public abstract void playRotationSound();
	
	public abstract void stopRotationSound();
	
	public void playWormholeIdleSound()
	{
		wormholeIdleSound.playSound();
	}
	
	public StargateInfo.FeedbackMessage engageStargate(Address address, boolean doKawoosh)
	{
		Stargate stargate = StargateNetwork.get(level).getStargate(this.get9ChevronAddress());
		
		if(stargate != null)
			return Dialing.dialStargate(((ServerLevel) this.level).getServer(), stargate, address, doKawoosh);
		
		StargateJourney.LOGGER.error("Stargate {} can't be found in the Stargate Network", this.get9ChevronAddress());
		return resetStargate(StargateInfo.Feedback.UNKNOWN_ERROR.withInfo());
	}
	
	public void connectStargate(UUID connectionID, StargateConnection.State connectionState)
	{
		this.connectionID = connectionID;
		this.setConnected(connectionState);
		this.timesOpened++;
		this.setChanged();
		
		this.updateStargate();
	}
	
	public static double kawooshFunction(int kawooshTime)
	{
		return 8 * Math.sin(Math.PI * (double) kawooshTime / StargateConnection.KAWOOSH_DURATION);
	}
	
	public void doKawoosh()
	{
		int kawooshTime = getKawooshTickCount();
		if(kawooshTime > StargateConnection.KAWOOSH_DURATION)
			return;
		
		Direction axisDirection = getDirection().getAxis() == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
		Direction direction = Orientation.getForwardDirection(getDirection(), getOrientation());
		
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
		Vec3 facingVector = Orientation.getForwardVector(direction, getOrientation());
		facingVector = facingVector.multiply(frontMultiplier, frontMultiplier, frontMultiplier);
		facingVector = facingVector.add(centerVector);
		facingVector = facingVector.relative(axisDirection, 2.25).relative(Orientation.getCenterDirection(getDirection(), getOrientation()), 2.25);
		
		AABB kawooshHitbox = new AABB(backVector.x(), backVector.y(), backVector.z(),
				facingVector.x(), facingVector.y(), facingVector.z());
		
		this.level.getEntitiesOfClass(Entity.class, kawooshHitbox).stream().forEach(entity -> 
		{
			if(shouldDisintegrate(entity) && entity.isAlive())
			{
				if(entity instanceof Player player)
					player.awardStat(StatisticsInit.TIMES_KILLED_BY_KAWOOSH.get());
				
				entity.hurt(DamageSourceInit.KAWOOSH, Float.MAX_VALUE);
				entity.kill();
			}
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
	
	public StargateInfo.FeedbackMessage resetStargate(StargateInfo.FeedbackMessage feedback)
	{
		if(level.isClientSide())
			return StargateInfo.Feedback.NONE.withInfo();
		
		if(isConnected())
		{
			closeWormholeSound(!isDialingOut());
			setConnected(StargateConnection.State.IDLE);
		}

		resetAddress();
		this.connectionID = null;
		setKawooshTickCount(0);
		setOpenTime(0);
		setTimeSinceLastTraveler(0);
		//updateClient();
		
		if(feedback.feedback().playFailSound() && !level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Fail(this.worldPosition, feedback.feedback()));
		
		updateBasicInterfaceBlocks(EVENT_RESET, feedback.feedback().getCode());
		updateCrystalInterfaceBlocks(EVENT_RESET, feedback.feedback().getCode(), feedback.feedback().getMessage());
		updateAdvancedCrystalInterfaceBlocks(EVENT_RESET, feedback.feedback().getCode(), feedback.feedback().getMessage());
		
		dhdCache.markDirtyTwoWays();
		
		setChanged();
		try
		{
			if(feedback.feedback() == StargateInfo.Feedback.UNKNOWN_ERROR)
				throw new RuntimeException("Unknown Stargate Error");
			else
				StargateJourney.LOGGER.debug("Reset Stargate {} at {} {} {}", id9ChevronAddress, getBlockPos().toShortString(), getLevel().dimension().location(), feedback);
		}
		catch(RuntimeException e)
		{
			StargateJourney.LOGGER.error("Reset Stargate {} at {} {} {}", id9ChevronAddress, getBlockPos().toShortString(), getLevel().dimension().location(), feedback, e);
			return setRecentFeedback(feedback);
		}
		
		return setRecentFeedback(feedback);
	}
	
	public StargateInfo.FeedbackMessage resetStargate(StargateInfo.Feedback feedback, Object... additionalInfo)
	{
		return resetStargate(feedback.withInfo(additionalInfo));
	}
	
	public StargateInfo.FeedbackMessage disconnectStargate(StargateInfo.FeedbackMessage feedback)
	{
		if(this.isConnected())
		{
			if(!CommonStargateConfig.end_connection_from_both_ends.get() && !this.isDialingOut())
				return setRecentFeedback(StargateInfo.Feedback.WRONG_DISCONNECT_SIDE.withInfo());
			else if(this.getOpenTime() <= 0)
				return setRecentFeedback(StargateInfo.Feedback.CONNECTION_FORMING.withInfo());
		}
		
		return bypassDisconnectStargate(feedback);
	}
	
	public StargateInfo.FeedbackMessage bypassDisconnectStargate(StargateInfo.FeedbackMessage feedback)
	{
		if(connectionID != null)
			StargateNetwork.get(level).terminateConnection(connectionID, feedback);
		return resetStargate(feedback);
	}
	
	public void updateStargate()
	{
		if(level.isClientSide())
			return;
		
		StargateNetwork.get(level).updateStargateEntity(this);
		setStargateState();
	}
	
	protected boolean growAddress(int symbol)
	{
		boolean result = this.address.addSymbol(symbol);
		setStargateState();
		//updateClient();
		return result;
	}
	
	protected void resetAddress()
	{
		this.address.reset();
		this.engagedChevrons = Dialing.DEFAULT_CHEVRON_CONFIGURATION;
		this.symbolMap.reset();
		setConnectionState(StargateConnection.State.IDLE);
		setStargateState();
	}
	
	public void updateDHD(AbstractDHDEntity dhd)
	{
		dhd.updateDHD(!isConnected() || (isConnected() && isDialingOut()) ? getAddress() : new Address.Mutable(), isConnected());
	}
	
	//============================================================================================
	//********************************************Info********************************************
	//============================================================================================
	
	@Override
	public AutoCache.Controller<AbstractDHDEntity, AbstractStargateEntity<?>> controllerCache()
	{
		return dhdCache;
	}
	
	public void setupServerAutoCache()
	{
		dhdCache.setRevalidate(() ->
		{
			if(dhdRelativePos == null)
				return false;
			
			BlockPos dhdPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), dhdRelativePos);
			if(dhdPos != null && level.getBlockEntity(dhdPos) instanceof AbstractDHDEntity dhd)
				return dhdCache.getCached() == dhd && CoordinateHelper.Relative.distanceSqr(dhdPos, getBlockPos()) <= dhd.getMaxConnectionDistanceSqr(); // Check if the DHD at the saved pos is the same DHD
			
			return false;
		});
		dhdCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(AbstractDHDEntity.class, level, worldPosition, dhdSearchDistance,
				dhd -> !dhd.stargateCache.isCached()));
		
		dhdCache.setOnChanged((oldDHD, newDHD) ->
		{
			if(newDHD != null)
			{
				dhdRelativePos = CoordinateHelper.Relative.getRelativeOffset(getDirection(), getBlockPos(), newDHD.getBlockPos());
				dhdSearchDistance = Math.round(Math.sqrt(CoordinateHelper.Relative.distanceSqr(newDHD.getBlockPos(), getBlockPos())));
				// Stargate will search at a distance equal to the distance of the last DHD it was connected to (or 64 if there was no DHD connected to it previously)
				if(dhdSearchDistance < MIN_DHD_SEARCH_DISTANCE)
					dhdSearchDistance = MIN_DHD_SEARCH_DISTANCE; // Make sure the distance is at least 64
			}
			else
				dhdRelativePos = null;
			
			updateStargate();
			updateClient();
		});
	}
	
	@Override
	public SymbolInfo symbolInfo()
	{
		return symbolInfo;
	}
	
	@Override
	public AddressFilterInfo addressFilterInfo()
	{
		return addressFilterInfo;
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public StargateInfo.FeedbackMessage setRecentFeedback(StargateInfo.FeedbackMessage feedback)
	{
		if(feedback.feedback() != StargateInfo.Feedback.NONE)
			this.recentFeedback = feedback;
		
		dhdCache.ifPresent(dhd ->
		{
			updateDHD(dhd);
			if(feedback.feedback().isError())
				dhd.sendMessageToNearbyPlayers(feedback.getMessageComponent(), AbstractDHDEntity.DHD_INFO_DISTANCE);
		});
		
		return feedback;
	}
	
	public StargateInfo.FeedbackMessage getRecentFeedback()
	{
		return this.recentFeedback;
	}
	
	public Set<Integer> getNetworks()
	{
		Set<Integer> networks = new TreeSet<>(this.networks);
		dhdCache.ifPresent(dhd -> networks.addAll(dhd.getNetworks()));
		
		if(!networks.isEmpty())
			return networks;
		
		return Set.of(defaultNetwork);
	}
	
	public Set<Integer> getCachedNetworks()
	{
		Set<Integer> networks = new TreeSet<>(this.networks);
		dhdCache.ifCached(dhd -> networks.addAll(dhd.getNetworks()));
		
		if(!networks.isEmpty())
			return networks;
		
		return Set.of(defaultNetwork);
	}
	
	public boolean addNetwork(int network)
	{
		boolean result = this.networks.add(network);
		updateStargate();
		updateClient();
		return result;
	}
	
	public boolean removeNetwork(int network)
	{
		boolean result = this.networks.remove(network);
		updateStargate();
		updateClient();
		return result;
	}
	
	public Trinary getRestrictNetwork()
	{
		return this.restrictNetwork;
	}
	
	public boolean hasNetworkRestrictions()
	{
		if(getRestrictNetwork().isNotDefault()) // If the restrictions (presumably set by a computer) aren't default, use them
			return getRestrictNetwork().isTrue();
		// Otherwise use DHD restrictions
		return dhdCache.returnOrDefault(AbstractDHDEntity::hasNetworkRestrictions, false);
	}
	
	public boolean hasCachedNetworkRestrictions()
	{
		if(getRestrictNetwork().isNotDefault()) // If the restrictions (presumably set by a computer) aren't default, use them
			return getRestrictNetwork().isTrue();
		// Otherwise use DHD restrictions
		return dhdCache.returnCachedOrDefault(AbstractDHDEntity::hasNetworkRestrictions, false);
	}
	
	public void setRestrictNetwork(Trinary restrictNetwork)
	{
		this.restrictNetwork = restrictNetwork;
		updateStargate();
		updateClient();
	}
	
	public StargateInfo.Gen getGeneration()
	{
		return this.stargateType.getGeneration();
	}
	
	public void setKawooshTickCount(int kawooshTick)
	{
		this.kawooshTick = kawooshTick;
	}
	
	public int getKawooshTickCount()
	{
		return this.kawooshTick;
	}
	
	public void resetAnimationTicks()
	{
		this.animationTick = 0;
	}
	
	public int getAnimationTicks()
	{
		return this.animationTick;
	}
	
	public void increaseAnimationTicks()
	{
		this.animationTick++;
	}
	
	public void animateUnstableWormhole()
	{
		if(unstableTicks > 0)
			unstableTicks--;
		else if(isWormholeUnstable() && random.nextFloat() <= 0.025)
			unstableTicks = random.nextInt(5, 20);
	}
	
	public void resetDisconnectTicks()
	{
		this.disconnectTicks = 0;
	}
	
	public int getDisconnectTicks()
	{
		return this.disconnectTicks;
	}
	
	public void increaseDisconnectTicks()
	{
		this.disconnectTicks++;
	}
	
	public boolean showUnstableWormhole()
	{
		return unstableTicks > 0;
	}
	
	public void setOpenTime(int openTime)
	{
		this.openTime = Math.max(openTime, 0);
	}
	
	public int getOpenTime()
	{
		return this.openTime;
	}
	
	public boolean isWormholeOpen()
	{
		return getOpenTime() > 0;
	}
	
	public boolean isWormholeUnstable()
	{
		return !energyStorage.hasEnergy(200 * CommonStargateConfig.interstellar_connection_energy_draw.get()); // Stargate does not have enough energy to maintain a stable wormhole
	}
	
	public void setTimeSinceLastTraveler(int timeSinceLastTraveler)
	{
		this.timeSinceLastTraveler = Math.max(timeSinceLastTraveler, 0);
	}
	
	public int getTimeSinceLastTraveler()
	{
		return this.timeSinceLastTraveler;
	}
	
	public int getTimesOpened()
	{
		return this.timesOpened;
	}
	
	public void setVariant(ResourceLocation variant)
	{
		this.variant = variant;
	}
	
	public ResourceLocation getVariant()
	{
		return this.variant;
	}
	
	public ResourceLocation defaultVariant()
	{
		return this.defaultVariant;
	}
	
	public void setAddress(Address address)
	{
		this.address = new Address.Mutable(address);
		this.setChanged();
	}
	
	public Address.Mutable getAddress()
	{
		return this.address;
	}
	
	public boolean isSymbolInAddress(int symbol)
	{
		return getAddress().containsRegularSymbol(this.symbolMap.getMappedSymbol(symbol));
	}
	
	public int getChevronsEngaged()
	{
		return this.address.getLength();
	}
	
	public void setEngagedChevrons(int[] engagedChevrons)
	{
		this.engagedChevrons = engagedChevrons;
	}
	
	public int[] getEngagedChevrons()
	{
		return this.engagedChevrons;
	}
	
	public int chevronsRendered()
	{
		return this.address.regularSymbolCount();
	}
	
	public int getRedstoneSymbolOutput()
	{
		return 0;
	}
	
	public int getRedstoneSegmentOutput()
	{
		return 0;
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
	
	public Orientation getOrientation()
	{
		if(this.orientation == null)
		{
			BlockState gateState = getBlockState();
			
			if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
				this.orientation = gateState.getValue(AbstractStargateBaseBlock.ORIENTATION);
			else
				StargateJourney.LOGGER.error("AbstractStargateEntity.getOrientation expected AbstractStargateBaseBlock at {} but found {} instead", getBlockPos(), gateState);
		}

		return this.orientation;
	}
	
	public Direction getDirection()
	{
		if(this.direction == null)
		{
			BlockState gateState = getBlockState();
			
			if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
				this.direction = gateState.getValue(AbstractStargateBaseBlock.FACING);
			else
				StargateJourney.LOGGER.error("AbstractStargateEntity.getDirection expected AbstractStargateBaseBlock at {} but found {} instead", getBlockPos(), gateState);
		}
		
		return this.direction;
	}
	
	public void setConnectionState(StargateConnection.State connectionState)
	{
		this.connectionState = connectionState;
	}
	
	public void setConnected(StargateConnection.State connectionState)
	{
		setConnectionState(connectionState);
		setStargateState();
		
		if(FORCE_LOAD_CHUNK)
		{
			if(connectionState != StargateConnection.State.IDLE)
				ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(), level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, true, true);
			else
				ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(), level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, false, true);
		}
	}
	
	public void setStargateState()
	{
		setStargateState(false, ShieldingState.OPEN);
		updateClient();
		
	}
	
	public void setStargateState(boolean updateIris, ShieldingState shieldingState)
	{
		BlockPos gatePos = getBlockPos();
		BlockState gateState = getBlockState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargate)
		{
			stargate.updateStargate(level, gatePos, gateState, shieldingState);
			
			if(updateIris)
				stargate.updateIris(level, gatePos, gateState, shieldingState);
			
			updateInterfaceBlocks(null);
		}
		else
			StargateJourney.LOGGER.error("AbstractStargateEntity.setStargateState expected AbstractStargateBaseBlock at {} but found {} instead", gatePos, gateState);
		setChanged();
		
	}
	
	public StargateConnection.State getConnectionState()
	{
		return this.connectionState;
	}
	
	public boolean isConnected()
	{
		return getConnectionState().isConnected();
	}
	
	public boolean isDialingOut()
	{
		return getConnectionState().isDialingOut();
	}

	protected int getMaxObstructiveBlocks()
	{
		return CommonStargateConfig.max_obstructive_blocks.get();
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
				
				if((!state.getMaterial().isReplaceable() && !(state.getBlock() instanceof AbstractStargateBlock) && !(state.getBlock() instanceof AbstractShieldingBlock)) || state.getMaterial() == Material.LAVA)
					obstructingBlocks++;
			}
		}
		return obstructingBlocks >= getMaxObstructiveBlocks();
	}
	
	public void markItemAsDropped()
	{
		this.isItemDropped = true;
	}
	
	public boolean isItemDropped()
	{
		return this.isItemDropped;
	}
	
	@Override
	public void saveToItem(ItemStack stack)
	{
		CompoundTag tag = new CompoundTag();
		BlockItem.setBlockEntityData(stack, this.getType(), this.serializeStargateInfo(tag));
	}
	
	
	
    public void updateBasicInterfaceBlocks(@Nullable String eventName, Object... objects)
    {
    	BlockPos gatePos = this.getBlockPos();
		BlockState gateState = getBlockState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			for(StargatePart part : stargateBlock.getParts())
			{
				BlockPos ringPos = part.getRingPos(gatePos, gateState.getValue(AbstractStargateBlock.FACING), gateState.getValue(AbstractStargateBlock.ORIENTATION));
				
				for(Direction direction : Direction.values())
		    	{
					BlockPos pos = ringPos.relative(direction);
					BlockState state = level.getBlockState(pos);
					
		    		if(level.getBlockEntity(pos) instanceof BasicInterfaceEntity interfaceEntity
		    				&& direction.getOpposite() == state.getValue(AbstractInterfaceBlock.FACING))
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
		BlockState gateState = getBlockState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			for(StargatePart part : stargateBlock.getParts())
			{
				BlockPos ringPos = part.getRingPos(gatePos, gateState.getValue(AbstractStargateBlock.FACING), gateState.getValue(AbstractStargateBlock.ORIENTATION));
				
				for(Direction direction : Direction.values())
		    	{
					BlockPos pos = ringPos.relative(direction);
					BlockState state = level.getBlockState(pos);
					
		    		if(level.getBlockEntity(pos) instanceof CrystalInterfaceEntity interfaceEntity
		    				&& direction.getOpposite() == state.getValue(AbstractInterfaceBlock.FACING))
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
		BlockState gateState = getBlockState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			for(StargatePart part : stargateBlock.getParts())
			{
				BlockPos ringPos = part.getRingPos(gatePos, gateState.getValue(AbstractStargateBlock.FACING), gateState.getValue(AbstractStargateBlock.ORIENTATION));
				
				for(Direction direction : Direction.values())
		    	{
					BlockPos pos = ringPos.relative(direction);
					BlockState state = level.getBlockState(pos);
					
		    		if(level.getBlockEntity(pos) instanceof AdvancedCrystalInterfaceEntity interfaceEntity
		    				&& direction.getOpposite() == state.getValue(AbstractInterfaceBlock.FACING))
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
	
	public void setOpenSoundLead(int openSoundLead)
	{
		this.openSoundLead = openSoundLead;
	}
	
	/**
	 * Stargates can make noises before the kawoosh itself starts (for example the WAH-WAH of the Milky Way Stargate)
	 * @return The number of ticks which the Stargate opening sound will get as a head-start before the actual kawoosh
	 */
	public int getOpenSoundLead()
	{
		return this.openSoundLead;
	}
	
	public abstract StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh); //TODO Get rid of this eventually
	
	@Override
	public List<Component> getStatus()
	{
		List<Component> status = new ArrayList<>();
		
		if(symbolInfo().pointOfOrigin() != null)
			status.add(Component.translatable("info.sgjourney.point_of_origin").append(": " + symbolInfo().pointOfOrigin().location()).withStyle(ChatFormatting.DARK_PURPLE));
		if(symbolInfo().symbols() != null)
			status.add(Component.translatable("info.sgjourney.symbols").append(": " + symbolInfo().symbols().location()).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		status.add(Component.translatable("info.sgjourney.times_opened").append(": " + timesOpened).withStyle(ChatFormatting.BLUE));
		if(dhdCache.isPresent())
			status.add(Component.translatable("info.sgjourney.dhd_connected").append(Component.literal(": ").append(ComponentHelper.coordinate(dhdCache.get().getBlockPos()))).withStyle(ChatFormatting.GOLD));
		else
			status.add(Component.translatable("info.sgjourney.no_dhd_connected").withStyle(ChatFormatting.GOLD));
		status.add(Component.translatable("info.sgjourney.autoclose").append(": " + Conversion.ticksToString(dhdCache.returnOrDefault(AbstractDHDEntity::autocloseTicks, 0))).withStyle(ChatFormatting.RED));
		status.add(Component.translatable("info.sgjourney.last_traveler_time").append(": " + getTimeSinceLastTraveler()).withStyle(ChatFormatting.DARK_PURPLE));
		status.add(Component.translatable("info.sgjourney.encoded_address").append(": ").append(address.toComponent(true)).withStyle(ChatFormatting.GREEN));
		status.add(Component.translatable("info.sgjourney.recent_feedback").append(Component.literal(": ").append(getRecentFeedback().getMessageComponent())).withStyle(ChatFormatting.WHITE));
		
		status.add(Component.translatable("info.sgjourney.9_chevron_address").append(": ").withStyle(ChatFormatting.AQUA).append(id9ChevronAddress.toComponent(true)));
		status.add(Component.translatable("info.sgjourney.add_to_network").append(": " + (generationStep == Step.GENERATED)).withStyle(ChatFormatting.YELLOW));
		if(isPrimary())
			status.add(Component.translatable("info.sgjourney.is_primary").withStyle(ChatFormatting.DARK_GREEN));
		status.add(ComponentHelper.tickTimer("info.sgjourney.open_time", getOpenTime(), SGJourneyStargate.MAX_OPEN_TIME, ChatFormatting.DARK_AQUA));
		status.add(ComponentHelper.energy("info.sgjourney.energy", energyStorage.getTrueEnergyStored()));
		status.add(Component.translatable("info.sgjourney.network_restrictions").append(": " + hasNetworkRestrictions()).withStyle(ChatFormatting.AQUA));
		status.add(Component.translatable("info.sgjourney.networks").append(": " + getNetworks()));
		
		return status;
	}
	
	@Override
	protected boolean canReceiveZeroPointEnergy()
	{
		return CommonZPMConfig.stargates_use_zero_point_energy.get();
	}
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return false;
	}

	@Override
	public long getCapacity()
	{
		return CommonStargateConfig.stargate_energy_capacity.get();
	}

	@Override
	public long getMaxReceive()
	{
		return CommonStargateConfig.stargate_energy_max_receive.get();
	}

	@Override
	public long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long getMaxDeplete()
	{
		return CommonStargateConfig.intergalactic_connection_energy_cost.get();
	}
	
	public boolean pushTraveler()
	{
		if(this.getOrientation() == Orientation.UPWARD)
			return true;
		
		return SpaceLocation.fromDimension(level.getServer(), level.dimension()).getParentGravity() > 0.0;
	}
	
	public float getVerticalCenterHeight()
	{
		return this.verticalCenterHeight;
	}
	
	public float getHorizontalCenterHeight()
	{
		return this.horizontalCenterHeight;
	}
	
	public abstract void registerInterfaceMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper);
	
	public void doWhileConnecting(boolean incoming, boolean doKawoosh, int kawooshStartTicks, int connectionTime)
	{
		if(!doKawoosh)
			return;
		
		if(connectionTime == kawooshStartTicks - getOpenSoundLead())
			openWormholeSound(incoming);
		
		if(connectionTime >= kawooshStartTicks)
			doKawoosh();
	}
	
	public void doWhileDialed(Address dialingAddress, int kawooshStartTicks, boolean doKawoosh, int connectionTime)
	{
		if(connectionTime > kawooshStartTicks)
			return;
		
		StargateInfo.ChevronLockSpeed chevronLockSpeed = getChevronLockSpeed(doKawoosh);
		
		if(connectionTime % chevronLockSpeed.getChevronWaitTicks() == 0)
		{
			int dialedAddressLength = getAddress().getLength();
			
			if(dialedAddressLength < dialingAddress.getLength())
			{
				if(connectionTime / chevronLockSpeed.getChevronWaitTicks() == 4 && dialingAddress.getType().below(Address.Type.ADDRESS_8_CHEVRON))
					return;
				else if(connectionTime / chevronLockSpeed.getChevronWaitTicks() == 5 && dialingAddress.getType().below(Address.Type.ADDRESS_9_CHEVRON))
					return;
				else
				{
					int symbol = dialingAddress.symbolAt(dialedAddressLength);
					encodeChevron(symbol, true, false);
					if(symbol == 0)
						updateInterfaceBlocks(EVENT_CHEVRON_ENGAGED, getAddress().getLength(), AbstractStargateEntity.getChevron(this, getAddress().getLength()), true, 0);
				}
			}
		}
	}
	
	public void doWhileConnected(boolean incoming, int connectionTime)
	{
		idleWormholeSound(incoming);
	}
	
	public List<Entity> findWormholeCandidates()
	{
		List<Entity> wormholeCandidates;
		Vec3 centerPos = getCenter();
		AABB localBox = new AABB(
				centerPos.x - 2.5, centerPos.y - 2.5, centerPos.z - 2.5,
				centerPos.x + 2.5, centerPos.y + 2.5, centerPos.z + 2.5);
		
		wormholeCandidates = getLevel().getEntitiesOfClass(Entity.class, localBox, entity -> entity.isAlive() && !entity.getType().is(TagInit.Entities.WORMHOLE_IGNORES));
		
		return wormholeCandidates;
	}
	
	public void spawnCoverParticles()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateParticleSpawnPacket(this.worldPosition, this.blockCover.blockStates));
	}
	
	public UUID getConnectionID()
	{
		return this.connectionID;
	}
	
	public void checkStargate()
	{
		if(isConnected())
		{
			// Will reset the Stargate if it incorrectly thinks it's connected
			if(!StargateNetwork.get(getLevel()).hasConnection(getConnectionID()))
				resetStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_NETWORK);
		}
	}
	
	public boolean sendStargateMessage(String message)
	{
		return StargateNetwork.get(level).sendStargateMessage(this, connectionID, message);
	}
	
	public void receiveStargateMessage(String message)
	{
		updateInterfaceBlocks(EVENT_MESSAGE_RECEIVED, message);
	}
	
	public float transmissionRadius()
	{
		return CommonTransmissionConfig.max_stargate_transmission_distance.get();
	}
	
	@Override
	public void receiveTransmission(int transmissionJumps, int frequency, String transmission)
	{
		if(transmissionJumps < CommonTransmissionConfig.max_transmission_jumps.get())
			StargateNetwork.get(level).sendStargateTransmission(this, connectionID, transmissionJumps + 1, frequency, transmission);
	}
	
	public void forwardTransmission(int transmissionJumps, int frequency, String transmission)
	{
		int roundedRadius = (int) Math.ceil(transmissionRadius() / 16);
		
		for(int x = -roundedRadius; x <= roundedRadius; x++)
		{
			for(int z = -roundedRadius; z <= roundedRadius; z++)
			{
				ChunkAccess chunk = level.getChunk(getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					BlockEntity blockEntity = level.getBlockEntity(pos);
					
					if(blockEntity instanceof ITransmissionReceiver receiver)
						receiver.receiveTransmission(transmissionJumps, frequency, transmission);
				});
			}
		}
	}
	
	public float checkConnectionShieldingState()
	{
		return StargateNetwork.get(level).checkStargateShieldingState(this, connectionID);
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractStargateEntity<?> stargate)
    {
		if(level.isClientSide())
		{
			if(stargate.isConnected())
			{
				stargate.increaseAnimationTicks();
				stargate.animateUnstableWormhole();
			}
			else if(stargate.getDisconnectTicks() > 0)
			{
				stargate.increaseAnimationTicks();
				stargate.increaseDisconnectTicks();
			}
		}
		else
			stargate.updateClient();

		//stargate.blockCover.canSinkGate = true; //TODO Implement a check for whether or not the Stargate can sink into the ground
    }
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void setGenerationStep(Step step)
	{
		System.out.println("Set Generation step: " + generationStep);
		this.generationStep = step;
	}
	
	@Override
	public Step generationStep()
	{
		return generationStep;
	}
	
	@Override
	public void generateInStructure(WorldGenLevel level, RandomSource randomSource)
	{
		if(generationStep == Step.SETUP)
			generationStep = Step.READY; // Marks the Stargate as ready for generation
	}
	
	private void trySetPrimary()
	{
		StargateNetwork stargateNetwork = StargateNetwork.get(level);
		AddressRegion addressRegion = Universe.get(level).getAddressRegionFromDimension(level.dimension());
		
		if(addressRegion == null || stargateNetwork.getPrimaryAddressFromAddressRegion(addressRegion.getResourceKey()) != null)
			return;
		
		stargateNetwork.setPrimaryAddressForAddressRegion(addressRegion.getResourceKey(), this.get9ChevronAddress());
	}
	
	public void generate()
	{
		addStargateToNetwork();
		generateAdditional(Step.READY);
		
		if(isPrimary())
			trySetPrimary();
		
		generationStep = Step.GENERATED;
	}
	
	public void generateAdditional(StructureGenEntity.Step generationStep) {}
	
	public void displayID()
	{
		displayID = true;
	}
	
	public void upgraded()
	{
		upgraded = true;
	}
	
	public void localPointOfOrigin()
	{
		localPointOfOrigin = true;
	}
	
	public void setPrimary()
	{
		isPrimary = true;
	}
	
	public boolean isPrimary()
	{
		return isPrimary;
	}
	
	@Override
	public void setProtected(boolean isProtected)
	{
		this.isProtected = isProtected;
	}
	
	@Override
	public boolean isProtected()
	{
		return isProtected;
	}
	
	@Override
	public boolean hasPermissions(Player player, boolean sendMessage)
	{
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_stargate_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return id9ChevronAddress.toString() + " at (" + getBlockPos().toShortString() + ')';
	}
}
