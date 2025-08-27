package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.TransporterNetwork;

public abstract class AbstractTransporterEntity extends EnergyBlockEntity implements StructureGenEntity, Nameable
{
	protected static final boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String ADD_TO_NETWORK = "add_to_network";
	public static final String ID = "transporter_id";
	public static final String CUSTOM_NAME = "custom_name";
	
	protected StructureGenEntity.Step generationStep = Step.GENERATED;
	
	protected UUID id;
	@Nullable
	protected UUID connectionID = null;

	@Nullable
	private Component name;
	
	public AbstractTransporterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
    public void onLoad()
	{
		super.onLoad();
		
        if(level.isClientSide())
	        return;
		
		if(generationStep == StructureGenEntity.Step.READY)
			generate();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		if(tag.contains(GENERATION_STEP, CompoundTag.TAG_BYTE))
			generationStep = StructureGenEntity.Step.fromByte(tag.getByte(GENERATION_STEP));
		
		try
		{
    		if(tag.contains(ID))
        		id = UUID.fromString(tag.getString(ID));
		}
		catch(IllegalArgumentException e)
		{
			this.setID(this.generateID());
		}
    	
    	if(tag.contains(CUSTOM_NAME, 8))
	         this.name = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME), registries);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		if(generationStep != Step.GENERATED)
			tag.putByte(GENERATION_STEP, generationStep.byteValue());
		
		if(id != null)
			tag.putString(ID, id.toString());
		
		super.saveAdditional(tag, registries);
		
		if(this.name != null)
	         tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, registries));
	}
	
	public UUID generateID()
	{
		return UUID.randomUUID();
	}
	
	public void setID(UUID id)
	{
    	this.id = id;
		setChanged();
		StargateJourney.LOGGER.info("Set ID to " + id);
	}
	
	public UUID getID()
	{
		return id;
	}
	
	@Nullable
	public Transporter getTransporter()
	{
		//TODO Maybe start caching it?
		return TransporterNetwork.get(level).getTransporter(id);
	}
	
	public void addTransporterToNetwork()
	{
		if(this.id == null)
			setID(generateID());
		
		TransporterNetwork.get(level).addTransporter(this);
		this.setChanged();
	}
	
	public void removeTransporterFromNetwork()
	{
		TransporterNetwork.get(level).removeTransporter(level, this.id);
	}
	
	@Override
	public void getStatus(Player player)
	{
		super.getStatus(player);
		
		if(level.isClientSide())
			return;
		
		player.sendSystemMessage(Component.literal("ID: " + id).withStyle(ChatFormatting.AQUA));
		player.sendSystemMessage(Component.translatable("info.sgjourney.add_to_network").append(Component.literal(": " + (generationStep == Step.GENERATED))).withStyle(ChatFormatting.YELLOW));
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
	
	
	
	public abstract boolean isConnected();
	
	public boolean canTransport()
	{
		return !this.isConnected();
	}
	
	public int getTimeOffset()
	{
		return 0;
	}
	
	public boolean connectTransporter(UUID connectionID)
	{
		this.connectionID = connectionID;
		setConnected(true);
		
		return true;
	}
	
	public void disconnectTransporter()
	{
		if(connectionID != null)
			TransporterNetwork.get(level).terminateConnection(this.connectionID);
		resetTransporter();
	}
	
	public void resetTransporter()
	{
		this.connectionID = null;
		setConnected(false);
	}
	
	protected void loadChunk(boolean load)
	{
		if(!level.isClientSide())
			ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(),
					level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, load, true);
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
			generationStep = Step.READY;
	}
	
	public void generate()
	{
		addTransporterToNetwork();
		
		generationStep = Step.GENERATED;
	}
}
