package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
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
import net.minecraft.world.level.material.Material;
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

public abstract class AbstractStargateEntity extends EnergyBlockEntity implements ITransmissionReceiver
{
	public static final String EMPTY = StargateJourney.EMPTY;
	public static final String ADD_TO_NETWORK = "AddToNetwork";
	public static final String ID = "ID"; //TODO For legacy reasons
	public static final String ID_9_CHEVRON_ADDRESS = "9ChevronAddress";
	
	private static final String EVENT_CHEVRON_ENGAGED = "stargate_chevron_engaged";
	private static final String EVENT_RESET = "stargate_reset";
	private static final String EVENT_MESSAGE_RECEIVED = "stargate_message_received";

	public static final String ADDRESS = "Address";
	public static final String DHD_POS = "DHDPos";
	public static final String ENERGY = "Energy";
	
	public static final String IRIS_PROGRESS = "IrisProgress";
	public static final String SHIELD_PROGRESS = "ShieldProgress";
	
	// Connections
	public static final String CONNECTION_ID = "ConnectionID";
	public static final String NETWORK = "Network";
	public static final String RESTRICT_NETWORK = "RestrictNetwork";
	public static final String TIMES_OPENED = "TimesOpened";
	public static final String AUTOCLOSE = "Autoclose";
	
	public static final String FILTER_TYPE = "FilterType";
	public static final String WHITELIST = "Whitelist";
	public static final String BLACKLIST = "Blacklist";
	
	// Upgrading and variants
	public static final String UPGRADED = "Upgraded";
	public static final String DISPLAY_ID = "DisplayID";
	public static final String VARIANT = "Variant";
	
	public static final String POINT_OF_ORIGIN = "PointOfOrigin";
	public static final String SYMBOLS = "Symbols";
	
	public static final String COVER_BLOCKS = "CoverBlocks";
	public static final String IRIS_INVENTORY = "IrisInventory";
	public static final String SHIELD_INVENTORY = "ShieldInventory";
	
	public static final boolean FORCE_LOAD_CHUNK = CommonStargateConfig.stargate_loads_chunk_when_connected.get();

	public static final float STANDARD_THICKNESS = 9.0F;
	public static final float VERTICAL_CENTER_STANDARD_HEIGHT = 0.5F;
	public static final float HORIZONTAL_CENTER_STANDARD_HEIGHT = (STANDARD_THICKNESS / 2) / 16;
	
	// Basic Info
	protected Address id9ChevronAddress = new Address();
	protected boolean addToNetwork = true;
	
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
	private final ResourceLocation defaultVariant;
	
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
	
	private boolean initialClientSync = false;
	
	private Stargate.FilterType filter = Stargate.FilterType.NONE;
	private ArrayList<Address.Immutable> whitelist = new ArrayList<Address.Immutable>();
	private ArrayList<Address.Immutable> blacklist = new ArrayList<Address.Immutable>();
	
	public StargateBlockCover blockCover = new StargateBlockCover(StargatePart.DEFAULT_PARTS);
	
	// Shielding
	protected short irisProgress = 0;
	protected short oldIrisProgress = 0;
	protected final ItemStackHandler irisItemHandler = createIrisHandler();
	
	/*protected short shieldProgress = 0;
	protected short oldShieldProgress = 0;
	protected final ItemStackHandler shieldItemHandler = createStargateShieldHandler();*/

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state, Stargate.Gen gen, int defaultNetwork,
			float verticalCenterHeight, float horizontalCenterHeight)
	{
		super(blockEntity, pos, state);
		
		this.defaultVariant = defaultVariant;
		
		this.generation = gen;
		this.network = defaultNetwork;
		
		this.verticalCenterHeight = verticalCenterHeight;
		this.horizontalCenterHeight = horizontalCenterHeight;
	}

	public AbstractStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state, Stargate.Gen gen, int defaultNetwork)
	{
		this(blockEntity, defaultVariant, pos, state, gen, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT);
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
        
        loadDHD();
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
		
		variant = tag.getString(VARIANT);
		
		if(tag.contains(DHD_POS))
		{
			int[] pos = tag.getIntArray(DHD_POS);
			dhdRelativePos = Optional.of(new Vec3i(pos[0], pos[1], pos[2]));
		}
		autoclose = tag.getInt(AUTOCLOSE);
		
		deserializeFilters(tag);
		
		blockCover.deserializeNBT(tag.getCompound(COVER_BLOCKS));
		
		irisProgress = tag.getShort(IRIS_PROGRESS);
		oldIrisProgress = irisProgress;
		irisItemHandler.deserializeNBT(tag.getCompound(IRIS_INVENTORY));
		
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

		tag.putString(VARIANT, variant);
		
		if(dhdRelativePos.isPresent())
		{
			Vec3i pos = dhdRelativePos.get();
			tag.putIntArray(DHD_POS, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		}
		tag.putInt(AUTOCLOSE, autoclose);
		
		serializeFilters(tag);
		
		tag.put(COVER_BLOCKS, blockCover.serializeNBT());
		
		tag.putShort(IRIS_PROGRESS, irisProgress);
		tag.put(IRIS_INVENTORY, irisItemHandler.serializeNBT());
		
		/*tag.putShort(SHIELD_PROGRESS, shieldProgress);
		tag.put(SHIELD_INVENTORY, shieldItemHandler.serializeNBT());*/
		
		super.saveAdditional(tag);
		
		return tag;
	}
	
	public void deserializeFilters(CompoundTag tag)
	{
		if(tag.contains(FILTER_TYPE))
			this.filter = Stargate.FilterType.getFilterType(tag.getInt(FILTER_TYPE));
		
		if(tag.contains(WHITELIST))
		{
			CompoundTag whitelistTag = tag.getCompound(WHITELIST);
			
			whitelistTag.getAllKeys().forEach(addressString ->
			{
				this.whitelist.add(new Address.Immutable(addressString));
			});
		}
		
		if(tag.contains(BLACKLIST))
		{
			CompoundTag blacklistTag = tag.getCompound(BLACKLIST);
			
			blacklistTag.getAllKeys().forEach(addressString ->
			{
				this.blacklist.add(new Address.Immutable(addressString));
			});
		}
	}
	
	public void serializeFilters(CompoundTag tag)
	{
		tag.putInt(FILTER_TYPE, this.filter.getIntegerValue());
		
		CompoundTag whitelistTag = new CompoundTag();
		CompoundTag blacklistTag = new CompoundTag();
		
		this.whitelist.forEach(address ->
		{
			whitelistTag.putBoolean(address.toString(), true);
		});
		
		this.blacklist.forEach(address ->
		{
			blacklistTag.putBoolean(address.toString(), true);
		});

		tag.put(WHITELIST, whitelistTag);
		tag.put(BLACKLIST, blacklistTag);
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
	
	public int getSymbolBounds()
	{
		return this.symbolBounds;
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
	
	public Stargate.Feedback engageSymbol(int symbol)
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(isSymbolOutOfBounds(symbol))
			return Stargate.Feedback.SYMBOL_OUT_OF_BOUNDS;
		
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
		
		return Stargate.Feedback.SYMBOL_ENCODED;
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
		
		if(this.getFilterType().shouldFilter())
		{
			if(this.getFilterType().isBlacklist() && this.isAddressBlacklisted(immutableAddress))
				return this.resetStargate(Stargate.Feedback.BLACKLISTED_TARGET);
			
			else if(this.getFilterType().isWhitelist() && !this.isAddressWhitelisted(immutableAddress))
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
		//updateClient();
		
		if(kawooshTime > StargateConnection.KAWOOSH_TICKS || this.isIrisClosed())
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
	
	private void updateStargate(Level level, boolean updateInterfaces)
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
		engagedChevrons = Dialing.DEFAULT_CHEVRON_CONFIGURATION;
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
				Optional<Address.Immutable> address = Universe.get(level).getAddressInGalaxyFromDimension(galaxy.get().getKey().location().toString(), dimension);
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
	//******************************************Symbols*******************************************
	//============================================================================================
	
	public Stargate.Feedback setRecentFeedback(Stargate.Feedback feedback)
	{
		//TODO Is this a good idea?
		if(feedback != Stargate.Feedback.NONE)
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
	public void setPointOfOriginFromDimension(ResourceKey<Level> dimension)
	{
		pointOfOrigin = Universe.get(level).getPointOfOrigin(dimension).location().toString();
		this.setChanged();
	}
	
	
	public void setRandomPointOfOrigin(ResourceKey<Level> dimension)
	{
		Random random = new Random();
		pointOfOrigin = Universe.get(level).getRandomPointOfOriginFromDimension(dimension, random.nextLong()).location().toString();
	}
	
	protected boolean isPointOfOriginValid(Level level)
	{
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		if(!isLocationValid(pointOfOrigin))
			return false;
		
		return pointOfOriginRegistry.containsKey(new ResourceLocation(pointOfOrigin));
	}
	
	public void setSymbolsFromDimension(ResourceKey<Level> dimension)
	{
		symbols = Universe.get(level).getSymbols(level.dimension()).location().toString();
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

	//TODO Finish Iris
	public boolean hasIris()
	{
		return irisItemHandler.getStackInSlot(0).getItem() instanceof StargateIrisItem;
	}
	
	public boolean setIris(ItemStack stack)
	{
		if(irisItemHandler.getStackInSlot(0).isEmpty())
		{
			irisItemHandler.setStackInSlot(0, stack.copy());
			return true;
		}
		
		return false;
	}
	
	@Nonnull
	public ItemStack getIris()
	{
		return irisItemHandler.getStackInSlot(0).copy();
	}
	
	/**
	 * Removes iris from the Stargate
	 * @return true if the Iris has been removed, false if there was no Iris to remove
	 */
	public boolean unsetIris()
	{
		if(!irisItemHandler.getStackInSlot(0).isEmpty())
		{
			irisItemHandler.setStackInSlot(0, ItemStack.EMPTY);
			return true;
		}
		
		return false;
	}
	
	public Optional<ResourceLocation> getIrisTexture()
	{
		if(!hasIris())
			return Optional.empty();
		
		return Optional.ofNullable(StargateIrisItem.getIrisTexture(irisItemHandler.getStackInSlot(0)));
	}
	
	public void setIrisProgress(short irisProgress)
	{
		this.oldIrisProgress = this.irisProgress;
		this.irisProgress = irisProgress;
	}
	
	public short getIrisProgress()
	{
		return hasIris() ? this.irisProgress : 0;
	}
	
	public float getIrisProgress(float partialTick)
	{
		//if(irisProgress != 0)
		//	System.out.println("Iris Progress: " + irisProgress);
		
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) getIrisProgress() : Mth.lerp(partialTick, this.oldIrisProgress, this.irisProgress);
	}
	
	public boolean isIrisClosed()
	{
		return hasIris() && this.irisProgress == ShieldingState.MAX_PROGRESS;
	}
	
	public ShieldingState getShieldingState()
	{
		return ShieldingState.fromProgress(irisProgress);
	}
	
	private void setIrisState()
	{
		if(irisProgress == ShieldingState.CLOSED.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_4);
			else if(oldIrisProgress < irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.CLOSED);
			return;
		}

		if(irisProgress == ShieldingState.MOVING_4.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_3);
			else if(oldIrisProgress < irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_4);
			return;
		}

		if(irisProgress == ShieldingState.MOVING_3.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_2);
			else if(oldIrisProgress < irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_3);
			return;
		}

		if(irisProgress == ShieldingState.MOVING_2.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_1);
			else if(oldIrisProgress < irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_2);
			return;
		}

		if(irisProgress == ShieldingState.MOVING_1.getProgress())
		{
			if(oldIrisProgress > irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.OPEN);
			else if(oldIrisProgress < irisProgress)
				setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.MOVING_1);
			return;
		}

		if(irisProgress == 0 && oldIrisProgress > irisProgress)
			setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.OPEN);
	}
	
	public boolean addIris(ItemStack stack)
	{
		if(setIris(stack))
		{
			irisProgress = ShieldingState.MAX_PROGRESS;
			oldIrisProgress = ShieldingState.MAX_PROGRESS;
			
			setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.CLOSED);
			
			return true;
		}
		else
			return false;
	}
	
	public void removeIris()
	{
		if(unsetIris())
		{
			ShieldingState shieldingState = ShieldingState.OPEN;
			
			irisProgress = shieldingState.getProgress();
			oldIrisProgress = shieldingState.getProgress();
			
			setStargateState(this.getConnectionState(), this.getChevronsEngaged(), true, true, ShieldingState.OPEN);
		}
	}
	
	public short increaseIrisProgress()
	{
		oldIrisProgress = irisProgress;

		if(hasIris() && irisProgress < ShieldingState.MAX_PROGRESS)
		{
			irisProgress++;
			
			setIrisState();
		}
		
		return irisProgress;
	}
	
	public short decreaseIrisProgress()
	{
		oldIrisProgress = irisProgress;
		
		if(hasIris() && irisProgress > 0)
		{
			irisProgress--;
			
			setIrisState();
		}
		
		return irisProgress;
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
				updateDHD();
			}
			
			this.autoclose = autoclose;
		}
		
		updateStargate(this.level, false);
		this.setChanged();
	}
	
	public void unsetDHD(boolean notifyDHD)
	{
		if(notifyDHD && this.dhd.isPresent())
			this.dhd.get().unsetStargate();
		
		this.dhd = Optional.empty();
		this.dhdRelativePos = Optional.empty();
		this.autoclose = 0;
		
		updateStargate(this.level, false);
		updateDHD();
		
		this.setChanged();
	}
	
	public Optional<BlockPos> getDHDPos()
	{
		if(this.dhdRelativePos.isEmpty())
			return Optional.empty();
		
		BlockPos dhdPos = CoordinateHelper.Relative.getOffsetPos(this.getDirection(), this.getBlockPos(), this.dhdRelativePos.get());
		
		if(dhdPos != null)
			return Optional.of(dhdPos);
		
		return Optional.empty();
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
	
	public boolean shouldCallForward()
	{
		if(hasDHD())
		{
			if(this.dhd.get().callForwardingEnabled())
				return true;
		}
		
		return false;
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
	
	public Stargate.FilterType setFilterType(int integerValue)
	{
		this.filter = Stargate.FilterType.getFilterType(integerValue);
		
		return this.filter;
	}
	
	public Stargate.FilterType getFilterType()
	{
		return this.filter;
	}
	
	public boolean isAddressWhitelisted(Address.Immutable address)
	{
		return this.whitelist.contains(address);
	}
	
	public boolean addToWhitelist(Address.Immutable address)
	{
		if(this.whitelist.contains(address))
			return false;
		
		this.whitelist.add(address);
		
		return true;
	}
	
	public boolean removeFromWhitelist(Address.Immutable address)
	{
		if(!this.whitelist.contains(address))
			return false;
		
		this.whitelist.remove(address);
		
		return true;
	}
	
	public void clearWhitelist()
	{
		this.whitelist.clear();
	}
	
	public boolean isAddressBlacklisted(Address.Immutable address)
	{
		return this.blacklist.contains(address);
	}
	
	public boolean addToBlacklist(Address.Immutable address)
	{
		if(this.blacklist.contains(address))
			return false;
		
		this.blacklist.add(address);
		
		return true;
	}
	
	public boolean removeFromBlacklist(Address.Immutable address)
	{
		if(!this.blacklist.contains(address))
			return false;
		
		this.blacklist.remove(address);
		
		return true;
	}
	
	public void clearBlacklist()
	{
		this.blacklist.clear();
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
		setStargateState(connectionState, chevronsEngaged, updateInterfaces, false, getShieldingState());
		updateClientState(); //TODO
		
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
				
				if((!state.getMaterial().isReplaceable() && !(state.getBlock() instanceof AbstractStargateBlock) && !(state.getBlock() instanceof AbstractShieldingBlock)) || state.getMaterial() == Material.LAVA)
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
		    			System.out.println("Updating");
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
		
		player.sendSystemMessage(Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": " + pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.open_time").append(Component.literal(": " + getOpenTime() + "/" + getMaxGateOpenTime())).withStyle(ChatFormatting.DARK_AQUA));
		player.sendSystemMessage(Component.translatable("info.sgjourney.times_opened").append(Component.literal(": " + timesOpened)).withStyle(ChatFormatting.BLUE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.has_dhd").append(Component.literal(": " + hasDHD())).withStyle(ChatFormatting.GOLD));
		player.sendSystemMessage(Component.translatable("info.sgjourney.autoclose").append(Component.literal(": " + autoclose())).withStyle(ChatFormatting.RED));
		player.sendSystemMessage(Component.translatable("info.sgjourney.last_traveler_time").append(Component.literal(": " + getTimeSinceLastTraveler())).withStyle(ChatFormatting.DARK_PURPLE));
		player.sendSystemMessage(Component.translatable("info.sgjourney.encoded_address").append(Component.literal(": ").append(address.toComponent(true))).withStyle(ChatFormatting.GREEN));
		player.sendSystemMessage(Component.translatable("info.sgjourney.recent_feedback").append(Component.literal(": ").append(getRecentFeedback().getFeedbackMessage())).withStyle(ChatFormatting.WHITE));

		player.sendSystemMessage(Component.translatable("info.sgjourney.9_chevron_address").append(": ").withStyle(ChatFormatting.AQUA).append(id9ChevronAddress.toComponent(true)));
		player.sendSystemMessage(Component.translatable("info.sgjourney.add_to_network").append(Component.literal(": " + addToNetwork)).withStyle(ChatFormatting.YELLOW));
		
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
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateUpdatePacket(this.worldPosition, this.address.toArray(), this.engagedChevrons, this.kawooshTick, this.animationTick, this.irisProgress, this.pointOfOrigin, this.symbols, this.variant, this.irisItemHandler.getStackInSlot(0)));
	}
	
	public void updateClientState()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundStargateStateUpdatePacket(this.worldPosition, this.blockCover.canSinkGate, this.blockCover.blockStates));
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
	
	protected ItemStackHandler createIrisHandler()
	{
		return new ItemStackHandler(1)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return stack.getItem() instanceof StargateIrisItem;
				}
				
				// Limits the number of items per slot
				public int getSlotLimit(int slot)
				{
					return 1;
				}
				
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					if(!isItemValid(slot, stack))
						return stack;
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
	}
	
	protected ItemStackHandler createStargateShieldHandler()
	{
		return new ItemStackHandler(1)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return stack.getItem() instanceof StargateShieldItem;
				}
				
				// Limits the number of items per slot
				public int getSlotLimit(int slot)
				{
					return 1;
				}
				
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					if(!isItemValid(slot, stack))
						return stack;
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
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
