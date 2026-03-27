package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.TransporterPeripheral;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.misc.PDAStatus;
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
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.TransporterNetwork;

public abstract class AbstractTransporterEntity<T extends BlockEntityTransporter<?>> extends EnergyBlockEntity implements StructureGenEntity, Nameable, TransporterIDFilterInfo.Interface, ProtectedBlockEntity, PDAStatus
{
	protected static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String TRANSPORTER_ID = TransporterID.TRANSPORTER_ID;
	public static final String CUSTOM_NAME = "CustomName";
	
	private TransporterType<T> transporterType;
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected TransporterInfo.Feedback recentFeedback = TransporterInfo.Feedback.NONE;
	
	protected TransporterID.Immutable transporterID;
	@Nullable
	protected UUID connectionID = null;

	@Nullable
	private Component name;
	
	protected TransporterIDFilterInfo transporterIDFilterInfo;
	
	protected boolean isProtected = false;
	
	public AbstractTransporterEntity(BlockEntityType<?> blockEntityType, TransporterType<T> transporterType, BlockPos pos, BlockState state)
	{
		super(blockEntityType, pos, state);
		this.transporterType = transporterType;
		
		this.transporterIDFilterInfo = new TransporterIDFilterInfo();
	}
	
	@Override
    public void onLoad()
	{
		super.onLoad();
		
        if(this.level.isClientSide())
	        return;
		
		if(this.generationStep == StructureGenEntity.Step.READY)
			generate();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			this.generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		if(tag.contains(TRANSPORTER_ID))
			this.transporterID = new TransporterID.Immutable(tag.getIntArray(TRANSPORTER_ID));
		//TODO What about Transporters with old UUIDs?
    	
    	if(tag.contains(CUSTOM_NAME, 8))
	         this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(this.generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, this.generationStep.byteValue());
		
		if(transporterID != null)
			tag.putIntArray(TRANSPORTER_ID, this.transporterID.toArray());
		
		super.saveAdditional(tag);
		
		if(this.name != null)
	         tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name));
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
		
		TransporterNetwork.get(level).addTransporter(this);
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
		
		status.add(Component.literal("ID: " + this.transporterID).withStyle(ChatFormatting.AQUA));
		status.add(Component.translatable("info.sgjourney.add_to_network").append(Component.literal(": " + (generationStep == Step.GENERATED))).withStyle(ChatFormatting.YELLOW));
		
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
	
	
	
	public abstract boolean isConnected();
	
	public abstract boolean isObstructed();
	
	public boolean canTransport()
	{
		return !this.isConnected();
	}
	
	public int getTimeOffset()
	{
		return 0;
	}
	
	public TransporterInfo.Feedback dialTransporter(TransporterID otherID)
	{
		if(!level.isClientSide())
			return setRecentFeedback(transporterReturn(transporter -> transporter.dialTransporter(level.getServer(), otherID), TransporterInfo.Feedback.UNKNOWN_ERROR));
		return this.recentFeedback;
	}
	
	public TransporterInfo.Feedback dialTransporter(Vec3i coords)
	{
		if(!level.isClientSide())
			return setRecentFeedback(transporterReturn(transporter -> transporter.dialTransporter(level.getServer(), coords), TransporterInfo.Feedback.UNKNOWN_ERROR));
		return this.recentFeedback;
	}
	
	public boolean connectTransporter(UUID connectionID)
	{
		this.connectionID = connectionID;
		setConnected(true);
		
		return true;
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
	
	public TransporterInfo.Feedback resetTransporter(TransporterInfo.Feedback feedback)
	{
		setRecentFeedback(feedback);
		this.connectionID = null;
		setConnected(false);
		
		return this.recentFeedback;
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
	
	@Nullable
	public abstract List<Entity> entitiesToTransport();
	
	public abstract BlockPos transportPos();
	
	public abstract void updateTicks(int connectionTime);
	
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
	
	private <T> T transporterReturn(Function<Transporter, T> consumer, @Nullable T defaultValue)
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
