package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.povstalec.sgjourney.common.stargate.info.AddressFilterInfo;
import net.povstalec.sgjourney.common.stargate.info.DHDInfo;
import net.povstalec.sgjourney.common.stargate.info.IrisInfo;
import net.povstalec.sgjourney.common.stargate.info.SymbolInfo;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.sound.SoundWrapper;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blocks.tech.AbstractInterfaceBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.CommonTransmissionConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.StatisticsInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.items.StargateIrisItem;
import net.povstalec.sgjourney.common.items.StargateShieldItem;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundStargateParticleSpawnPacket;
import net.povstalec.sgjourney.common.packets.ClientboundStargateStateUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Dialing;
import net.povstalec.sgjourney.common.stargate.Galaxy;
import net.povstalec.sgjourney.common.stargate.ITransmissionReceiver;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateBlockCover;
import net.povstalec.sgjourney.common.stargate.StargateConnection;
import net.povstalec.sgjourney.common.stargate.StargateConnection.State;
import net.povstalec.sgjourney.common.stargate.Symbols;
import net.povstalec.sgjourney.common.stargate.Wormhole;

public abstract class AbstractStargateEntity extends EnergyBlockEntity implements SymbolInfo.Interface, DHDInfo.Interface, AddressFilterInfo.Interface, ITransmissionReceiver
{
	public static final String EMPTY = StargateJourney.EMPTY;
	public static final String ADD_TO_NETWORK = "AddToNetwork";
	public static final String ID = "ID"; //TODO For legacy reasons
	public static final String ID_9_CHEVRON_ADDRESS = "9ChevronAddress";
	
	public static final String EVENT_STARGATE_ROTATION_STARTED = "stargate_rotation_started";
	public static final String EVENT_STARGATE_ROTATION_STOPPED = "stargate_rotation_stopped";
	public static final String EVENT_CHEVRON_ENGAGED = "stargate_chevron_engaged";
	public static final String EVENT_RESET = "stargate_reset";
	public static final String EVENT_MESSAGE_RECEIVED = "stargate_message_received";

	public static final String ADDRESS = "Address";
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
	
	public static final String COVER_BLOCKS = "CoverBlocks";
	
	public static final boolean FORCE_LOAD_CHUNK = CommonStargateConfig.stargate_loads_chunk_when_connected.get();
	
	public static final int MAX_SYMBOLS = 48;

	public static final float STANDARD_THICKNESS = 9.0F;
	public static final float VERTICAL_CENTER_STANDARD_HEIGHT = 0.5F;
	public static final float HORIZONTAL_CENTER_STANDARD_HEIGHT = (STANDARD_THICKNESS / 2) / 16;
	
	// Basic Info
	protected Address id9ChevronAddress = new Address();
	protected boolean addToNetwork = true;
	
	protected final Stargate.Gen generation;
	protected int totalSymbols;
	protected int[] symbolMap;
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
	
	protected ResourceLocation variant = StargateJourney.EMPTY_LOCATION;
	private final ResourceLocation defaultVariant;
	
	// Dialing and memory
	protected Address address = new Address();
	protected String connectionID = EMPTY;
	protected Wormhole wormhole = new Wormhole();

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
	
	private boolean initialClientSync = false;
	
	public StargateBlockCover blockCover = new StargateBlockCover(StargatePart.DEFAULT_PARTS);
	
	protected SymbolInfo symbolInfo;
	protected DHDInfo dhdInfo;
	protected AddressFilterInfo addressFilterInfo;
	//protected ShieldInfo shieldInfo;

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
								  int totalSymbols,  Stargate.Gen gen, int defaultNetwork, float verticalCenterHeight, float horizontalCenterHeight)
	{
		super(blockEntity, pos, state);
		
		this.defaultVariant = defaultVariant;
		
		this.totalSymbols = totalSymbols;
		this.symbolMap = newSymbolMap();
		
		this.generation = gen;
		this.network = defaultNetwork;
		
		this.verticalCenterHeight = verticalCenterHeight;
		this.horizontalCenterHeight = horizontalCenterHeight;
		
		this.symbolInfo = new SymbolInfo();
		this.dhdInfo = new DHDInfo(this);
		this.addressFilterInfo = new AddressFilterInfo();
	}

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
								  int totalSymbols, Stargate.Gen gen, int defaultNetwork)
	{
		this(blockEntity, defaultVariant, pos, state, totalSymbols, gen, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
	}
	
	@Override
    public void onLoad()
	{
		super.onLoad();
		
        if(level.isClientSide())
	        return;
        
        if(!addToNetwork)
    		addStargateToNetwork();
        
        updateClientState();
        
        dhdInfo.loadDHD();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		deserializeStargateInfo(tag, false);
	}
	
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		super.load(tag);
		
		timesOpened = tag.getInt(TIMES_OPENED);
		address.fromArray(tag.getIntArray(ADDRESS));
		network = tag.getInt(NETWORK);
		restrictNetwork = tag.getBoolean(RESTRICT_NETWORK);
		
		connectionID = tag.getString(CONNECTION_ID);
		
		if(tag.contains(ID)) //TODO Keeping this here for the time being for legacy reasons
			id9ChevronAddress.fromString(tag.getString(ID));
		else
			id9ChevronAddress.fromArray(tag.getIntArray(ID_9_CHEVRON_ADDRESS));
    	addToNetwork = tag.getBoolean(ADD_TO_NETWORK);
		
		displayID = tag.getBoolean(DISPLAY_ID);
		upgraded = isUpgraded ? true : tag.getBoolean(UPGRADED);
		
		variant = new ResourceLocation(tag.getString(VARIANT));
		
		if(tag.contains(DHD_POS))
		{
			int[] pos = tag.getIntArray(DHD_POS);
			dhdInfo().setRelativePos(new Vec3i(pos[0], pos[1], pos[2]));
		}
		dhdInfo().setAutoclose(tag.getInt(AUTOCLOSE));
		
		addressFilterInfo().deserializeFilters(tag);
		
		blockCover.deserializeNBT(tag.getCompound(COVER_BLOCKS));
		
		/*shieldProgress = tag.getShort(SHIELD_PROGRESS);
		oldShieldProgress = shieldProgress;
		shieldItemHandler.deserializeNBT(tag.getCompound(SHIELD_INVENTORY));*/
		
    	this.setChanged();
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		serializeStargateInfo(tag);
	}
	
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		tag.putInt(TIMES_OPENED, timesOpened);
		tag.putIntArray(ADDRESS, address.toArray());
		tag.putInt(NETWORK, network);
		tag.putBoolean(RESTRICT_NETWORK, restrictNetwork);
		
		tag.putString(CONNECTION_ID, connectionID);
		
		tag.putIntArray(ID_9_CHEVRON_ADDRESS, id9ChevronAddress.toArray());
		tag.putBoolean(ADD_TO_NETWORK, addToNetwork);

		tag.putLong(ENERGY, this.getEnergyStored());

		tag.putBoolean(DISPLAY_ID, displayID);
		tag.putBoolean(UPGRADED, upgraded);

		tag.putString(VARIANT, variant.toString());
		
		if(dhdInfo().relativePos() != null)
		{
			Vec3i pos = dhdInfo().relativePos();
			tag.putIntArray(DHD_POS, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		}
		tag.putInt(AUTOCLOSE, dhdInfo().autoclose());
		
		addressFilterInfo().serializeFilters(tag);
		
		tag.put(COVER_BLOCKS, blockCover.serializeNBT());
		
		/*tag.putShort(SHIELD_PROGRESS, shieldProgress);
		tag.put(SHIELD_INVENTORY, shieldItemHandler.serializeNBT());*/
		
		super.saveAdditional(tag);
		
		return tag;
	}
	
	public void addStargateToNetwork()
	{
		if(id9ChevronAddress.isEmpty() || BlockEntityList.get(level).getStargate(id9ChevronAddress.immutable()).isPresent())
			set9ChevronAddress(generate9ChevronAddress());
		
		StargateNetwork.get(level).addStargate(this);
		
		addToNetwork = true;
		this.setChanged();
	}
	
	public void removeStargateFromNetwork()
	{
		StargateNetwork.get(level).removeStargate(level, id9ChevronAddress.immutable());
	}
	
	public void set9ChevronAddress(Address address)
	{
		this.id9ChevronAddress = address;
		setChanged();
		StargateJourney.LOGGER.info("Set 9-Chevron Address to " + this.id9ChevronAddress);
	}
	
	public Address get9ChevronAddress()
	{
		return id9ChevronAddress;
	}
	
	protected Address generate9ChevronAddress()
	{
		Random random = new Random();
		Address address;
		while(true)
		{
			address = new Address().randomAddress(8, 36, random.nextLong());
			
			if(BlockEntityList.get(level).getStargate(address.immutable()).isEmpty())
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
	
	private int[] newSymbolMap()
	{
		int[] symbolMap = new int[this.totalSymbols];
		
		for(int i = 0; i < this.totalSymbols; i++)
		{
			symbolMap[i] = i;
		}
		
		return symbolMap;
	}
	
	public boolean remapSymbol(int symbol1, int symbol2)
	{
		if(isSymbolOutOfBounds(symbol1))
			return false;
		
		if(symbol2 < 0 || symbol2 > MAX_SYMBOLS)
			return false;
		
		this.symbolMap[symbol1] = symbol2;
		
		return true;
	}
	
	public int getMappedSymbol(int symbol)
	{
		if(symbol < 0 || symbol >= this.symbolMap.length)
			return -1;
		
		return this.symbolMap[symbol];
	}
	
	public int totalSymbols()
	{
		return this.totalSymbols;
	}
	
	public int getSymbolBounds()
	{
		return this.totalSymbols - 1;
	}
	
	public boolean isSymbolOutOfBounds(int symbol)
	{
		if(symbol < 0)
			return true;
		
		if(symbol > getSymbolBounds())
			return true;
		
		return false;
	}
	
	public static int getChevron(AbstractStargateEntity stargate, int chevronNumber)
	{
		chevronNumber--;
		if(chevronNumber < 0 || chevronNumber >= 8)
			return 0;
		else
			return stargate.getEngagedChevrons()[chevronNumber];
	}
	
	public Stargate.Feedback dhdEngageSymbol(int symbol)
	{
		return engageSymbol(symbol);
	}
	
	public Stargate.Feedback engageSymbol(int symbol)
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(isSymbolOutOfBounds(symbol))
			return setRecentFeedback(Stargate.Feedback.SYMBOL_OUT_OF_BOUNDS);
		
		return encodeSymbol(getMappedSymbol(symbol));
	}
	
	public Stargate.Feedback encodeSymbol(int symbol)
	{
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT, true);
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
		
		chevronSound((short) getAddress().getLength(), incoming, false, encodeSound); //TODO Is this address length thing right?
		
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
		
		return setRecentFeedback(Stargate.Feedback.SYMBOL_ENCODED);
	}
	
	protected Stargate.Feedback lockPrimaryChevron()
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(!address.isComplete())
		{
			chevronSound((short) 0, false, false, false);
			return resetStargate(Stargate.Feedback.INCOMPLETE_ADDRESS);
		}
		else if(!isConnected())
		{
			if(!isObstructed())
			{
				chevronSound((short) 0, false, false, false);
				updateInterfaceBlocks(EVENT_CHEVRON_ENGAGED, this.address.getLength() + 1, 0, false, 0);
				return setRecentFeedback(engageStargate(this.getAddress(), true));
			}
			else
				return resetStargate(Stargate.Feedback.SELF_OBSTRUCTED, false);
		}
		else
			return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT, true);
		
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
	
	public Stargate.Feedback engageStargate(Address address, boolean doKawoosh)
	{
		Address.Immutable immutableAddress = address.immutable();
		
		if(addressFilterInfo().getFilterType().shouldFilter())
		{
			if(addressFilterInfo().getFilterType().isBlacklist() && addressFilterInfo().isAddressBlacklisted(immutableAddress))
				return this.resetStargate(Stargate.Feedback.BLACKLISTED_TARGET);
			
			else if(addressFilterInfo().getFilterType().isWhitelist() && !addressFilterInfo().isAddressWhitelisted(immutableAddress))
				return this.resetStargate(Stargate.Feedback.WHITELISTED_TARGET);
		}
		
		Address dialingAddress = this.getConnectionAddress(address.getLength());
		Optional<Stargate> stargate = StargateNetwork.get(level).getStargate(this.get9ChevronAddress().immutable());
		
		if(stargate.isPresent())
			return Dialing.dialStargate((ServerLevel) this.level, stargate.get(), immutableAddress, dialingAddress.immutable(), doKawoosh);
		
		return resetStargate(Stargate.Feedback.UNKNOWN_ERROR);
	}
	
	public void connectStargate(String connectionID, StargateConnection.State connectionState)
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
		return 8 * Math.sin(Math.PI * (double) kawooshTime / StargateConnection.KAWOOSH_TICKS);
	}
	
	public void doKawoosh(int kawooshTime)
	{
		setKawooshTickCount(kawooshTime);
		
		if(kawooshTime > StargateConnection.KAWOOSH_TICKS)
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
			if(shouldDisintegrate(entity) && entity.isAlive())
			{
				if(entity instanceof Player player)
					player.awardStat(StatisticsInit.TIMES_KILLED_BY_KAWOOSH.get());
				
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
	
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback, boolean updateInterfaces)
	{
		if(isConnected())
		{
			closeWormholeSound(!isDialingOut());
			setConnected(StargateConnection.State.IDLE);
		}

		resetAddress(updateInterfaces);
		this.connectionID = EMPTY;
		setKawooshTickCount(0);
		setTickCount(0);
		//updateClient();
		
		if(feedback.playFailSound() && !level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Fail(this.worldPosition));
		
		if(updateInterfaces)
		{
			updateBasicInterfaceBlocks(EVENT_RESET, feedback.getCode());
			updateCrystalInterfaceBlocks(EVENT_RESET, feedback.getCode(), feedback.getMessage());
			updateAdvancedCrystalInterfaceBlocks(EVENT_RESET, feedback.getCode(), feedback.getMessage());
		}
		
		dhdInfo().revalidateDHD();
		
		setChanged();
		if(feedback == Stargate.Feedback.UNKNOWN_ERROR)
			StargateJourney.LOGGER.error("Reset Stargate at " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ() + " " + this.getLevel().dimension().location().toString() + " " + feedback.getMessage());
		else
			StargateJourney.LOGGER.debug("Reset Stargate at " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ() + " " + this.getLevel().dimension().location().toString() + " " + feedback.getMessage());
		return setRecentFeedback(feedback);
	}
	
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback)
	{
		return resetStargate(feedback, true);
	}
	
	public Stargate.Feedback disconnectStargate(Stargate.Feedback feedback, boolean updateInterfaces)
	{
		if(this.isConnected())
		{
			if(!CommonStargateConfig.end_connection_from_both_ends.get() && !this.isDialingOut())
				return Stargate.Feedback.WRONG_DISCONNECT_SIDE;
			else if(this.getOpenTime() <= 0)
				return Stargate.Feedback.CONNECTION_FORMING;
		}
		
		return bypassDisconnectStargate(feedback, updateInterfaces);
	}
	
	public Stargate.Feedback bypassDisconnectStargate(Stargate.Feedback feedback, boolean updateInterfaces)
	{
		if(connectionID != null && !connectionID.equals(EMPTY))
			StargateNetwork.get(level).terminateConnection(connectionID, feedback);
		return resetStargate(feedback, updateInterfaces);
	}
	
	public void updateStargate(boolean updateInterfaces)
	{
		updateStargate(this.level, updateInterfaces);
	}
	
	public void updateStargate(Level level, boolean updateInterfaces)
	{
		if(level.isClientSide())
			return;
			
		StargateNetwork.get(level).updateStargate((ServerLevel) level, this);
		setStargateState(this.getConnectionState(), this.getChevronsEngaged(), updateInterfaces);
	}
	
	protected void growAddress(int symbol)
	{
		this.address.addSymbol(symbol);
		setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true);
		//updateClient();
	}
	
	protected void resetAddress(boolean updateInterfaces)
	{
		this.address.reset();
		this.engagedChevrons = Dialing.DEFAULT_CHEVRON_CONFIGURATION;
		this.symbolMap = newSymbolMap();
		setStargateState(StargateConnection.State.IDLE, 0, updateInterfaces);
	}
	
	public Address getConnectionAddress(int addressLength)
	{
		ResourceKey<Level> dimension = this.level.dimension();
		
		if(addressLength == 6)
		{
			Optional<Galaxy.Serializable> galaxy = Universe.get(this.level).getGalaxyFromDimension(dimension);
			if(galaxy.isPresent())
			{
				Optional<Address.Immutable> address = Universe.get(level).getAddressInGalaxyFromDimension(galaxy.get().getKey().location(), dimension);
				if(address.isPresent())
					return address.get().mutable();
			}
		}
		else if(addressLength == 7)
		{
			Optional<Address.Immutable> address = Universe.get(level).getExtragalacticAddressFromDimension(dimension);
			if(address.isPresent())
				return address.get().mutable();
		}
		
		return this.get9ChevronAddress();
		// This setup basically means that a 9-chevron Address is returned for a Connection when a Stargate isn't in any Solar System
	}
	
	//============================================================================================
	//********************************************Info********************************************
	//============================================================================================
	
	@Override
	public SymbolInfo symbolInfo()
	{
		return this.symbolInfo;
	}
	
	@Override
	public DHDInfo dhdInfo()
	{
		return this.dhdInfo;
	}
	
	@Override
	public AddressFilterInfo addressFilterInfo()
	{
		return this.addressFilterInfo;
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public Stargate.Feedback setRecentFeedback(Stargate.Feedback feedback)
	{
		if(feedback != Stargate.Feedback.NONE)
			this.recentFeedback = feedback;
		
		dhdInfo().sendDHDFeedback(feedback);
		dhdInfo().updateDHD();
		
		return feedback;
	}
	
	public Stargate.Feedback getRecentFeedback()
	{
		return this.recentFeedback;
	}
	
	public int getNetwork()
	{
		return this.network;
	}
	
	public void setNetwork(int network)
	{
		this.network = network;
		this.updateStargate(false);
	}
	
	public boolean getRestrictNetwork()
	{
		return this.restrictNetwork;
	}
	
	public void setRestrictNetwork(boolean restrictNetwork)
	{
		this.restrictNetwork = restrictNetwork;
	}
	
	public boolean isRestricted(int network)
	{
		if(this.getRestrictNetwork())
			return network != this.getNetwork();
		
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
	
	public int getOpenTime()
	{
		if(this.level.isClientSide())
			return 0;
		return StargateNetwork.get(this.level).getOpenTime(this.connectionID);
	}
	
	public boolean isWormholeOpen()
	{
		return getOpenTime() > 0;
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
				StargateJourney.LOGGER.error("Couldn't find Stargate Orientation " + this.getBlockPos().toString());
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
				StargateJourney.LOGGER.error("Couldn't find Stargate Direction " + this.getBlockPos().toString());
		}
		
		return this.direction;
	}
	
	public void setConnected(StargateConnection.State connectionState)
	{
		setStargateState(connectionState, this.getChevronsEngaged(), true);
		
		if(FORCE_LOAD_CHUNK)
		{
			if(connectionState != State.IDLE)
				ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(), level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, true, true);
			else
				ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(), level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, false, true);
		}
	}
	
	public void setStargateState(StargateConnection.State connectionState, int chevronsEngaged, boolean updateInterfaces)
	{
		setStargateState(connectionState, chevronsEngaged, updateInterfaces, false, ShieldingState.OPEN);
		updateClientState();
		
	}
	
	public void setStargateState(StargateConnection.State connectionState, int chevronsEngaged, boolean updateInterfaces, boolean updateIris, ShieldingState shieldingState)
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = getState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock stargate)
		{
			stargate.updateStargate(level, gatePos, gateState, connectionState, chevronsEngaged, shieldingState);
			
			if(updateIris)
				stargate.updateIris(level, gatePos, gateState, shieldingState);
			
			if(updateInterfaces)
				updateInterfaceBlocks(null);
		}
		else
			StargateJourney.LOGGER.error("Couldn't find Stargate");
		setChanged();
		
	}
	
	public StargateConnection.State getConnectionState()
	{
		BlockState gateState = getState();
		
		if(gateState.getBlock() instanceof AbstractStargateBaseBlock)
			return gateState.getValue(AbstractStargateBaseBlock.CONNECTION_STATE);
		
		return StargateConnection.State.IDLE;
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
				
				if((!state.canBeReplaced() && !(state.getBlock() instanceof AbstractStargateBlock)) && !(state.getBlock() instanceof AbstractShieldingBlock) || state.getFluidState().is(Fluids.LAVA))
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
		BlockState gateState = getState();
		
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
		BlockState gateState = getState();
		
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
	
	@Override
	public void getStatus(Player player)
	{
		if(level.isClientSide())
			return;
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": " + symbolInfo().pointOfOrigin())).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + symbolInfo().symbols())).withStyle(ChatFormatting.LIGHT_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.times_opened").append(Component.literal(": " + timesOpened)).withStyle(ChatFormatting.BLUE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.has_dhd").append(Component.literal(": " + dhdInfo().hasDHD())).withStyle(ChatFormatting.GOLD));
		player.sendSystemMessage(Component.translatable("info.sgjourney.autoclose").append(Component.literal(": " + dhdInfo().autoclose())).withStyle(ChatFormatting.RED));
		player.sendSystemMessage(Component.translatable("info.sgjourney.last_traveler_time").append(Component.literal(": " + getTimeSinceLastTraveler())).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.encoded_address").append(Component.literal(": ").append(address.toComponent(true))).withStyle(ChatFormatting.GREEN));
		player.sendSystemMessage(Component.translatable("info.sgjourney.recent_feedback").append(Component.literal(": ").append(getRecentFeedback().getFeedbackMessage())).withStyle(ChatFormatting.WHITE));

		player.sendSystemMessage(Component.translatable("info.sgjourney.9_chevron_address").append(": ").withStyle(ChatFormatting.AQUA).append(id9ChevronAddress.toComponent(true)));
		player.sendSystemMessage(Component.translatable("info.sgjourney.add_to_network").append(Component.literal(": " + addToNetwork)).withStyle(ChatFormatting.YELLOW));
		player.sendSystemMessage(Component.translatable("info.sgjourney.open_time").append(Component.literal(": " + getOpenTime() + "/" + getMaxGateOpenTime())).withStyle(ChatFormatting.DARK_AQUA));
		
		player.sendSystemMessage(Component.literal("Energy: " + this.getEnergyStored() + " FE").withStyle(ChatFormatting.DARK_RED));
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
	
	public boolean updateClient()
	{
		if(level.isClientSide())
			return false;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateUpdatePacket(this.worldPosition, this.address.toArray(), this.engagedChevrons, this.kawooshTick, this.animationTick, (short) 0, symbolInfo().pointOfOrigin(), symbolInfo().symbols(), this.variant, ItemStack.EMPTY));
		return true;
	}
	
	public boolean updateClientState()
	{
		if(level.isClientSide())
			return false;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateStateUpdatePacket(this.worldPosition, this.blockCover.canSinkGate, this.blockCover.blockStates));
		return true;
	}
	
	public void spawnCoverParticles()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateParticleSpawnPacket(this.worldPosition, this.blockCover.blockStates));
	}
	
	public String getConnectionID()
	{
		return this.connectionID;
	}
	
	public void checkStargate()
	{
		if(isConnected())
		{
			// Will reset the Stargate if it incorrectly thinks it's connected
			if(!StargateNetwork.get(getLevel()).hasConnection(getConnectionID()) || getConnectionID().equals(StargateJourney.EMPTY))
				resetStargate(Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK);
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
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractStargateEntity stargate)
    {
		if(stargate.isConnected())
			stargate.increaseTickCount();
		
		if(level.isClientSide())
			return;

		stargate.updateClient();

		//stargate.blockCover.canSinkGate = true; //TODO Implement a check for whether or not the Stargate can sink into the ground
		if(!stargate.initialClientSync) // Syncs to client on the first tick
			stargate.updateClientState();
    }
}
