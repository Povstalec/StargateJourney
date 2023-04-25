package net.povstalec.sgjourney.common.data;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;

/**
 * This class is designed to save all Block Entities along with their coordinates and dimensions. 
 * @author Povstalec
 *
 */
public class BlockEntityList extends SavedData
{
	private static final String FILE_NAME = "sgjourney-block_enties";
	
	public static final String STARGATES = "Stargates";
	public static final String TRANSPORT_RINGS = "TransportRings";

	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";
	
	private CompoundTag blockEntityList = new CompoundTag();
	
	public CompoundTag addBlockEntity(Level level, BlockPos pos, String listName, String id)
	{
		String dimension = level.dimension().location().toString();
		CompoundTag localList = blockEntityList.getCompound(listName);
		CompoundTag blockEntity = new CompoundTag();
		
		blockEntity.putString(DIMENSION, dimension);
		blockEntity.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		localList.put(id, blockEntity);
		blockEntityList.put(listName, localList);
		
		setDirty();
		return blockEntity;
	}
	
	public void removeBlockEntity(String type, String id)
	{
		if(!getBlockEntities(type).contains(id))
		{
			StargateJourney.LOGGER.info(type + " does not contain " + id);
			return;
		}
		blockEntityList.getCompound(type).remove(id);
		StargateJourney.LOGGER.info("Removed " + id + " from " + type);
		setDirty();
	}
	
	public CompoundTag getBlockEntities(String blockEntities)
	{
		return blockEntityList.copy().getCompound(blockEntities);
	}
	
//================================================================================================

	public static BlockEntityList create()
	{
		return new BlockEntityList();
	}
	
	public static BlockEntityList load(CompoundTag tag)
	{
		BlockEntityList data = create();
		
		data.blockEntityList = tag.copy();
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = this.blockEntityList.copy();
		
		return tag;
	}
	
	@Nonnull
	public static BlockEntityList get(Level level)
	{
		if(level.isClientSide)
			throw new RuntimeException("Don't access this client-side!");
		
		return BlockEntityList.get(level.getServer());
	}

    @Nonnull
	public static BlockEntityList get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(BlockEntityList::load, BlockEntityList::create, FILE_NAME);
    }
    
//================================================================================================
}
