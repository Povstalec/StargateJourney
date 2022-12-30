package net.povstalec.sgjourney.block_entities;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.data.BlockEntityList;

public abstract class SGJourneyBlockEntity extends BlockEntity
{
	protected String id;
	protected Type type;
	protected boolean addToNetwork = true;
	protected CompoundTag blockEntity;
	
	public SGJourneyBlockEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, Type type) 
	{
		super(blockEntity, pos, state);
		this.type = type;
	}
	
	public static enum Type
	{
		STARGATE("Stargates"),
		TRANSPORT_RINGS("TransportRings");
		
		String id;
		
		Type(String id)
		{
			this.id = id;
		}
	}
	
	@Override
	public void onLoad()
	{
        super.onLoad();
        
        if(level.isClientSide)
	        return;
        
        if(!addToNetwork)
    		addNewToBlockEntityList();
	}
	
	@Override
    public void load(CompoundTag tag)
    {
    	super.load(tag);
    	
    	if(tag.contains("ID"))
    		id = tag.getString("ID");
    	if(tag.contains("AddToNetwork"))
    		addToNetwork = tag.getBoolean("AddToNetwork");
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		if(id != null)
			tag.putString("ID", id);
		
		tag.putBoolean("AddToNetwork", addToNetwork);
		
		super.saveAdditional(tag);
	}
	
	public void addToBlockEntityList()
	{
		if(BlockEntityList.get(level).getBlockEntities(type.id).contains(id))
		{
			StargateJourney.LOGGER.info("Block Entity List already contains " + id);
			addNewToBlockEntityList();
		}
        else
        {
        	blockEntity = BlockEntityList.get(level).addBlockEntity(level, worldPosition, type.id, id);
			StargateJourney.LOGGER.info("Block Entity " + id + " added.");
        }
		
		addToNetwork = true;
	}
	
	public void addNewToBlockEntityList()
	{
    	setID(generateID());
    	blockEntity = BlockEntityList.get(level).addBlockEntity(level, worldPosition, type.id, id);
		StargateJourney.LOGGER.info("Block Entity " + id + " added.");
		
		addToNetwork = true;
	}
	
	public void removeFromBlockEntityList()
	{
		BlockEntityList.get(level).removeBlockEntity(type.id, id);
	}
	
	protected String generateID()
	{
		return UUID.randomUUID().toString();
	}
	
	protected void setID(String blockentityID)
	{
    	this.id = blockentityID;
		setChanged();
		StargateJourney.LOGGER.info("Set ID to " + id);
	}
	
	public String getID()
	{
		return id;
	}
	
	public void getStatus(Player player)
	{
		if(level.isClientSide)
			return;
		
		player.sendSystemMessage(Component.literal("ID: " + id).withStyle(ChatFormatting.AQUA));
		player.sendSystemMessage(Component.literal("AddToNetwork: " + addToNetwork).withStyle(ChatFormatting.YELLOW));
	}

}
