package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergySlotBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.blocks.tech_interface.AbstractInterfaceBlock;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.TransporterPeripheral;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.misc.*;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.info.TransporterIDFilterInfo;
import net.povstalec.sgjourney.common.sgjourney.transporter.BlockEntityTransporter;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import net.povstalec.sgjourney.common.sgjourney.transporter.TransporterType;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.TransporterNetwork;

public abstract class AbstractTransporterEntity<T extends BlockEntityTransporter<?>> extends EnergySlotBlockEntity implements StructureGenEntity,
		Nameable, TransporterIDFilterInfo.Interface, ProtectedBlockEntity, PDAStatus, AutoCache.IReceiver<TransporterControllerEntity, AbstractTransporterEntity<?>>
{
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String TRANSPORTER_ID = TransporterID.TRANSPORTER_ID;
	public static final String CUSTOM_NAME = "CustomName";
	
	public static final String NETWORKS = "networks";
	public static final String RESTRICT_NETWORK = "restrict_network";
	
	public static final String CONTROLLER_POS = "controller_pos";
	
	public static final long MIN_CONTROLLER_SEARCH_DISTANCE = 64;
	
	private final TransporterType<T> transporterType;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected TransporterInfo.Feedback recentFeedback = TransporterInfo.Feedback.NONE;
	
	protected TransporterID.Immutable transporterID;
	
	protected int defaultNetwork;
	protected Set<Integer> networks = new HashSet<>();
	protected Trinary restrictNetwork = Trinary.DEFAULT;
	
	@Nullable
	protected UUID connectionID = null;

	@Nullable
	private Component name;
	
	@Nullable
	protected Vec3i controllerRelativePos = null;
	protected long controllerSearchDistance = MIN_CONTROLLER_SEARCH_DISTANCE;
	public final AutoCache.Controller<TransporterControllerEntity, AbstractTransporterEntity<?>> controllerCache = new AutoCache.Controller<>(this);
	
	protected TransporterIDFilterInfo transporterIDFilterInfo = new TransporterIDFilterInfo();
	
	protected boolean isProtected = false;
	
	public AbstractTransporterEntity(BlockEntityType<?> blockEntityType, TransporterType<T> transporterType, BlockPos pos, BlockState state, int defaultNetwork)
	{
		super(blockEntityType, pos, state);
		this.transporterType = transporterType;
		
		this.defaultNetwork = defaultNetwork;
	}
	
	@Override
    public void onLoad()
	{
		super.onLoad();
		
        if(this.level.isClientSide())
	        return;
		
		if(this.generationStep == Step.READY)
			generate();
		
		//=====Setting up cache logic=====
		controllerCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(TransporterControllerEntity.class, level, worldPosition, 16,
				controller -> !controller.transporterCache.isCached()));
		//==========
		
		
		
		
		if(level.isClientSide())
		{
			// Anything goes, DHD is responsible for taking care of everything on client
			controllerCache.setRevalidate(() -> true);
			controllerCache.setFetch(controllerCache::getCached);
		}
		else
		{
			//=====Setting up cache logic=====
			controllerCache.setRevalidate(() ->
			{
				if(controllerRelativePos == null)
					return false;
				
				BlockPos controllerPos = CoordinateHelper.Relative.getOffsetPos(getDirection(), getBlockPos(), controllerRelativePos);
				if(controllerPos != null && level.getBlockEntity(controllerPos) instanceof TransporterControllerEntity dhd)
					return controllerCache.getCached() == dhd && CoordinateHelper.Relative.distanceSqr(controllerPos, getBlockPos()) <= dhd.getMaxConnectionDistanceSqr(); // Check if the DHD at the saved pos is the same DHD
				
				return false;
			});
			controllerCache.setFetch(() -> LocatorHelper.getNearestBlockEntityOfClass(TransporterControllerEntity.class, level, worldPosition, controllerSearchDistance,
					controller -> !controller.transporterCache.isCached()));
			
			controllerCache.setOnChanged((oldDHD, newDHD) ->
			{
				if(newDHD != null)
				{
					controllerRelativePos = CoordinateHelper.Relative.getRelativeOffset(getDirection(), getBlockPos(), newDHD.getBlockPos());
					controllerSearchDistance = Math.round(Math.sqrt(CoordinateHelper.Relative.distanceSqr(newDHD.getBlockPos(), getBlockPos())));
					// Transporter will search at a distance equal to the distance of the last Controller it was connected to (or 64 if there was no Controller connected to it previously)
					if(controllerSearchDistance < MIN_CONTROLLER_SEARCH_DISTANCE)
						controllerSearchDistance = MIN_CONTROLLER_SEARCH_DISTANCE; // Make sure the distance is at least 64
				}
				else
					controllerRelativePos = null;
				
				updateTransporter();
				updateClient();
			});
			//==========
			
			checkTransporter();
			
			if(generationStep == Step.READY)
				generate();
		}
		
		super.onLoad();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(TRANSPORTER_ID, Tag.TAG_INT_ARRAY))
			transporterID = new TransporterID.Immutable(tag.getIntArray(TRANSPORTER_ID));
		//TODO What about Transporters with old UUIDs?
    	
    	if(tag.contains(CUSTOM_NAME, 8))
	         name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME));
		
		if(tag.contains(NETWORKS, Tag.TAG_INT_ARRAY))
			networks = new HashSet<>(Arrays.stream(tag.getIntArray(NETWORKS)).boxed().toList());
		restrictNetwork = Trinary.fromInt(tag.getByte(RESTRICT_NETWORK));
		
		if(tag.contains(CONTROLLER_POS, Tag.TAG_INT_ARRAY))
			controllerRelativePos = Conversion.intArrayToVec(tag.getIntArray(CONTROLLER_POS));
		else
			controllerRelativePos = null;
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(transporterID != null)
			tag.putIntArray(TRANSPORTER_ID, transporterID.toArray());
		
		super.saveAdditional(tag);
		
		if(name != null)
	         tag.putString(CUSTOM_NAME, Component.Serializer.toJson(name));
		
		if(!networks.isEmpty())
			tag.putIntArray(NETWORKS, networks.stream().toList());
		tag.putByte(RESTRICT_NETWORK, restrictNetwork.value);
		
		if(controllerRelativePos != null)
			tag.putIntArray(CONTROLLER_POS, Conversion.vecToIntArray(controllerRelativePos));
	}
	
	@Override
	public AutoCache.Controller<TransporterControllerEntity, AbstractTransporterEntity<?>> controllerCache()
	{
		return controllerCache;
	}
	
	public final TransporterType<T> getTransporterType()
	{
		return transporterType;
	}
	
	public void setID(TransporterID.Immutable transporterID)
	{
    	this.transporterID = transporterID;
		setChanged();
		StargateJourney.LOGGER.info("Set ID to " + transporterID);
	}
	
	public TransporterID.Immutable getID()
	{
		return this.transporterID;
	}
	
	public void addTransporterToNetwork()
	{
		if(this.transporterID == null)
			setID(BlockEntityList.get(level).generateTransporterID());
		
		TransporterNetwork.get(level).addTransporterEntity(this);
		this.setChanged();
	}
	
	public void removeTransporterFromNetwork()
	{
		TransporterNetwork.get(level).removeTransporter(this.transporterID);
	}
	
	@Override
	public List<Component> getStatus()
	{
		List<Component> status = new ArrayList<>();
		
		status.add(Component.translatable("info.sgjourney.transporter_id").append(": ").withStyle(ChatFormatting.AQUA).append(this.transporterID.toComponent(true)));
		status.add(Component.translatable("info.sgjourney.add_to_network").append(": " + (generationStep == Step.GENERATED)).withStyle(ChatFormatting.YELLOW));
		
		return status;
	}
	
	public void setCustomName(Component name)
	{
		this.name = name;
	}
	
	public Component getName()
	{
		return this.name != null ? this.name : this.getDefaultName();
	}
	
	public Component getDisplayName()
	{
		return this.getName();
	}
	
	@Nullable
	public Component getCustomName()
	{
		return this.name;
	}
	
	protected abstract Component getDefaultName();
	
	
	public TransporterInfo.Feedback setRecentFeedback(TransporterInfo.Feedback feedback)
	{
		if(feedback != TransporterInfo.Feedback.NONE)
			this.recentFeedback = feedback;
		
		return feedback;
	}
	
	public TransporterInfo.Feedback getRecentFeedback()
	{
		return this.recentFeedback;
	}
	
	public void setNetwork(int defaultNetwork)
	{
		this.defaultNetwork = defaultNetwork;
		this.updateTransporter();
	}
	
	public Set<Integer> getNetworks()
	{
		Set<Integer> networks = new HashSet<>(this.networks);
		networks.addAll(controllerCache.returnOrDefault(TransporterControllerEntity::getNetworks, Set.of()));
		
		if(!networks.isEmpty())
			return networks;
		
		return Set.of(defaultNetwork);
	}
	
	public boolean addNetwork(int network)
	{
		boolean result = this.networks.add(network);
		this.updateTransporter();
		return result;
	}
	
	public boolean removeNetwork(int network)
	{
		boolean result = this.networks.remove(network);
		this.updateTransporter();
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
		
		return false;
	}
	
	public void setRestrictNetwork(Trinary restrictNetwork)
	{
		this.restrictNetwork = restrictNetwork;
	}
	
	
	
	public Direction getDirection()
	{
		return Direction.NORTH; // Hardcoded to North just so the Transporter has some Direction to store its relative Controller position
	}
	
	public abstract boolean isConnected();
	
	public abstract boolean isObstructed();
	
	public boolean canTransport()
	{
		return !this.isConnected();
	}
	
	public TransporterInfo.Feedback dialTransporter(TransporterID otherID)
	{
		if(!level.isClientSide())
		{
			setRecentFeedback(transporterReturn(transporter -> transporter.dialTransporter(level.getServer(), otherID), TransporterInfo.Feedback.UNKNOWN_ERROR));
			onDialAttempt(this.recentFeedback, otherID);
		}
		return this.recentFeedback;
	}
	
	public TransporterInfo.Feedback dialTransporter(Vec3i coords)
	{
		if(!level.isClientSide())
		{
			setRecentFeedback(transporterReturn(transporter -> transporter.dialTransporter(level.getServer(), coords), TransporterInfo.Feedback.UNKNOWN_ERROR));
			onDialAttempt(this.recentFeedback, coords);
		}
		return this.recentFeedback;
	}
	
	public void onDialAttempt(TransporterInfo.Feedback feedback, TransporterID otherID) {}
	
	public void onDialAttempt(TransporterInfo.Feedback feedback, Vec3i coords) {}
	
	public boolean connectTransporter(UUID connectionID)
	{
		this.connectionID = connectionID;
		setConnected(true);
		
		return true;
	}
	
	@Nullable
	public UUID getConnectionID()
	{
		return this.connectionID;
	}
	
	public void checkTransporter()
	{
		if(isConnected())
		{
			// Will reset the Transporter if it incorrectly thinks it's connected
			if(!TransporterNetwork.get(getLevel()).hasConnection(getConnectionID()))
				resetTransporter(TransporterInfo.Feedback.CONNECTION_ENDED_BY_NETWORK);
		}
	}
	
	public TransporterInfo.Feedback disconnectTransporter(TransporterInfo.Feedback feedback)
	{
		//TODO Extra checks for disconnect?
		
		return bypassDisconnectTransporter(feedback);
	}
	
	public TransporterInfo.Feedback bypassDisconnectTransporter(TransporterInfo.Feedback feedback)
	{
		if(connectionID != null)
			TransporterNetwork.get(level).terminateConnection(this.connectionID, feedback);
		return resetTransporter(TransporterInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
	}
	
	public void updateBasicInterfaceBlocks(@Nullable String eventName, Object... objects)
	{
		for(Direction direction : Direction.values())
		{
			BlockPos pos = this.getBlockPos().relative(direction);
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
	
	public void updateCrystalInterfaceBlocks(@Nullable String eventName, Object... objects)
	{
		for(Direction direction : Direction.values())
		{
			BlockPos pos = this.getBlockPos().relative(direction);
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
	
	public void updateAdvancedCrystalInterfaceBlocks(@Nullable String eventName, Object... objects)
	{
		for(Direction direction : Direction.values())
		{
			BlockPos pos = this.getBlockPos().relative(direction);
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
	
	public void updateInterfaceBlocks(@Nullable String eventName, Object... objects)
	{
		updateBasicInterfaceBlocks(eventName, objects);
		updateCrystalInterfaceBlocks(eventName, objects);
		updateAdvancedCrystalInterfaceBlocks(eventName, objects);
	}
	
	public void updateTransporter()
	{
		if(level.isClientSide())
			return;
		
		TransporterNetwork.get(level).updateTransporterEntity(this);
	}
	
	public TransporterInfo.Feedback resetTransporter(TransporterInfo.Feedback feedback)
	{
		this.connectionID = null;
		setConnected(false);
		
		try
		{
			if(feedback == TransporterInfo.Feedback.UNKNOWN_ERROR)
				throw new RuntimeException("Unknown Transporter Error");
			else
				StargateJourney.LOGGER.debug("Reset Transporter {} at {} {} {}", transporterID, getBlockPos().toShortString(), getLevel().dimension().location(), feedback.getMessage());
		}
		catch(RuntimeException e)
		{
			StargateJourney.LOGGER.error("Reset Transporter {} at {} {} {}", transporterID, getBlockPos().toShortString(), getLevel().dimension().location(), feedback.getMessage(), e);
			return setRecentFeedback(feedback);
		}
		
		return setRecentFeedback(feedback);
	}
	
	protected void loadChunk(boolean load)
	{
		if(!level.isClientSide())
			ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(),
					level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, load, true);
	}
	
	public abstract void registerInterfaceMethods(SGJourneyPeripheralWrapper<TransporterPeripheral> wrapper);
	
	//============================================================================================
	//********************************************Info********************************************
	//============================================================================================
	
	@Override
	public TransporterIDFilterInfo transporterIDFilterInfo()
	{
		return this.transporterIDFilterInfo;
	}
	
	//========================================================================================================
	//**********************************************Transporting**********************************************
	//========================================================================================================
	
	public int getTimeUntilTransport()
	{
		return 0; // Based on hoverStartTicks in the renderer
	}
	
	@Nullable
	public abstract List<Entity> entitiesToTransport();
	
	public abstract BlockPos transportPos();
	
	public abstract void updateTicks(int transportTicks, int connectionTime);
	
	public abstract void setConnected(boolean connected);
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void generateInStructure(WorldGenLevel level, RandomSource randomSource)
	{
		if(generationStep == Step.SETUP)
			generationStep = Step.READY; // Marks the Transporter as ready for generation
	}
	
	public void generate()
	{
		addTransporterToNetwork();
		
		generationStep = Step.GENERATED;
	}
	
	//========================================================================================================
	//**********************************************Transporter***********************************************
	//========================================================================================================
	
	@Nullable
	public Transporter getTransporter()
	{
		//TODO Maybe start caching it?
		return TransporterNetwork.get(level).getTransporter(this.transporterID);
	}
	
	private void transporterRun(Consumer<Transporter> consumer)
	{
		Transporter transporter = getTransporter();
		
		if(transporter != null)
			consumer.accept(transporter);
	}
	
	private <R> R transporterReturn(Function<Transporter, R> consumer, @Nullable R defaultValue)
	{
		Transporter transporter = getTransporter();
		
		if(transporter != null)
			return consumer.apply(transporter);
		
		return defaultValue;
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
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_transporter_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
