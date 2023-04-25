package net.povstalec.sgjourney.common.block_entities;

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
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;

public abstract class SGJourneyBlockEntity extends EnergyBlockEntity
{
	protected static final boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	protected static final String ID = "ID";
	protected static final String ADD_TO_NETWORK = "AddToNetwork";
	protected static final String EMPTY = StargateJourney.EMPTY;
	
	private String id = EMPTY;
	protected Type type;
	protected boolean addToNetwork = true;
	
	public SGJourneyBlockEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state, Type type) 
	{
		super(blockEntity, pos, state);
		this.type = type;
	}
	
	public static enum Type
	{
		STARGATE("Stargates"),
		TRANSPORT_RINGS("TransportRings");
		
		public String id;
		
		Type(String id)
		{
			this.id = id;
		}
	}
	
	@Override
	public void onLoad()
	{
        super.onLoad();
        
        if(level.isClientSide())
	        return;
        
        if(!addToNetwork)
    		addNewToBlockEntityList();
	}
	
	@Override
    public void load(CompoundTag tag)
    {
    	super.load(tag);
    	
    	id = tag.getString(ID);
    	addToNetwork = tag.getBoolean(ADD_TO_NETWORK);
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putString(ID, id);
		tag.putBoolean(ADD_TO_NETWORK, addToNetwork);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	protected boolean outputsEnergy()
	{
		return false;
	}
	
	//============================================================================================
	//*****************************************Other Stuff****************************************
	//============================================================================================
	
	public CompoundTag addToBlockEntityList()
	{
		CompoundTag blockEntity;
		if(BlockEntityList.get(level).getBlockEntities(type.id).contains(id))
		{
			StargateJourney.LOGGER.info("Block Entity List already contains " + id);
			blockEntity = addNewToBlockEntityList();
		}
        else
        {
        	blockEntity = BlockEntityList.get(level).addBlockEntity(level, worldPosition, type.id, id);
			StargateJourney.LOGGER.info("Block Entity " + id + " added.");
        }
		
		addToNetwork = true;
		this.setChanged();
		return blockEntity;
	}
	
	public CompoundTag addNewToBlockEntityList()
	{
    	setID(generateID());
		CompoundTag blockEntity = BlockEntityList.get(level).addBlockEntity(level, worldPosition, type.id, id);
		StargateJourney.LOGGER.info("Block Entity " + id + " added.");
		
		addToNetwork = true;
		this.setChanged();
		
		return blockEntity;
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
	
	@Override
	public void getStatus(Player player)
	{
		super.getStatus(player);
		
		if(level.isClientSide)
			return;
		
		player.sendSystemMessage(Component.literal("ID: " + id).withStyle(ChatFormatting.AQUA));
		player.sendSystemMessage(Component.literal("AddToNetwork: " + addToNetwork).withStyle(ChatFormatting.YELLOW));
	}

}
