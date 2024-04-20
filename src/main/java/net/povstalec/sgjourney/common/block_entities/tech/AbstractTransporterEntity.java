package net.povstalec.sgjourney.common.block_entities.tech;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.TransporterNetwork;

public abstract class AbstractTransporterEntity extends EnergyBlockEntity
{
	protected static final boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	public static final String ADD_TO_NETWORK = "AddToNetwork";
	public static final String ID = "ID";
	
	protected boolean addToNetwork = true;
	
	protected String id;
	protected String connectionID = StargateJourney.EMPTY;
	
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
        
        if(!addToNetwork)
    		addTransporterToNetwork();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		
    	addToNetwork = tag.getBoolean(ADD_TO_NETWORK);
    	
    	if(tag.contains(ID))
    		id = tag.getString(ID);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.putBoolean(ADD_TO_NETWORK, addToNetwork);
		
		if(id != null)
			tag.putString(ID, id);
		
		super.saveAdditional(tag);
	}
	
	public String generateID()
	{
		return UUID.randomUUID().toString();
	}
	
	public void setID(String id)
	{
    	this.id = id;
		setChanged();
		StargateJourney.LOGGER.info("Set ID to " + id);
	}
	
	public String getID()
	{
		return id;
	}
	
	public void addTransporterToNetwork()
	{
		if(id == null || BlockEntityList.get(level).getTransporter(UUID.fromString(id)).isPresent())
			setID(generateID());
		
		TransporterNetwork.get(level).addTransporter(this);
		
		addToNetwork = true;
		this.setChanged();
	}
	
	public void removeTransporterFromNetwork()
	{
		TransporterNetwork.get(level).removeTransporter(level, UUID.fromString(this.id));
	}
	
	@Override
	public void getStatus(Player player)
	{
		super.getStatus(player);
		
		if(level.isClientSide())
			return;
		
		player.sendSystemMessage(Component.literal("ID: " + id).withStyle(ChatFormatting.AQUA));
		player.sendSystemMessage(Component.translatable("info.sgjourney.add_to_network").append(Component.literal(": " + addToNetwork)).withStyle(ChatFormatting.YELLOW));
	}
}
