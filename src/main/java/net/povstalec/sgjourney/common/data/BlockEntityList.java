package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.Conversion;

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
	
	protected CompoundTag blockEntityList = new CompoundTag();
	
	//protected Map<String, Tuple<ResourceKey<Level>, BlockPos>> stargateMap = new HashMap<>();
	//protected Map<String, Tuple<ResourceKey<Level>, BlockPos>> transportRingsMap = new HashMap<>();
	
	public CompoundTag addBlockEntity(Level level, BlockPos pos, String listName, String id)
	{
		String dimension = level.dimension().location().toString();
		CompoundTag localList = blockEntityList.getCompound(listName);
		CompoundTag blockEntity = new CompoundTag();
		
		blockEntity.putString(DIMENSION, dimension);
		blockEntity.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		localList.put(id, blockEntity);
		blockEntityList.put(listName, localList);
		
		this.setDirty();
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
	
	/*public CompoundTag serialize()
	{
		CompoundTag blockEntityList = new CompoundTag();
		CompoundTag stargates = serializeStargates();
		CompoundTag transportRings = serializeTransportRings();
		
		blockEntityList.put(STARGATES, stargates);
		blockEntityList.put(TRANSPORT_RINGS, transportRings);
		
		return blockEntityList;
	}
	
	public CompoundTag serializeStargates()
	{
		CompoundTag stargates = new CompoundTag();
		
		this.stargateMap.forEach((stargateID, info) -> 
		{
			CompoundTag stargate = new CompoundTag();
			ResourceKey<Level> level = info.getA();
			BlockPos pos = info.getB();
			
			stargate.putString(DIMENSION, level.location().toString());
			stargate.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
			stargates.put(stargateID, stargate);
		});
		
		return stargates;
	}
	
	public CompoundTag serializeTransportRings()
	{
		CompoundTag transportRings = new CompoundTag();
		
		this.transportRingsMap.forEach((ringsID, info) -> 
		{
			CompoundTag rings = new CompoundTag();
			ResourceKey<Level> level = info.getA();
			BlockPos pos = info.getB();
			
			rings.putString(DIMENSION, level.location().toString());
			rings.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
			transportRings.put(ringsID, rings);
		});
		
		return transportRings;
	}
	
	public void deserialize(CompoundTag tag)
	{
		CompoundTag blockEntityList = tag.copy();
		
		deserializeStargates(blockEntityList);
		deserializeTransportRings(blockEntityList);
	}
	
	protected void deserializeStargates(CompoundTag blockEntityList)
	{
		CompoundTag stargates = blockEntityList.getCompound(STARGATES);
		
		stargates.getAllKeys().stream().forEach(stargate ->
		{
			ResourceKey<Level> level = Conversion.stringToDimension(stargates.getCompound(stargate).getString(DIMENSION));
			BlockPos pos = Conversion.intArrayToBlockPos(stargates.getCompound(stargate).getIntArray(COORDINATES));
			
			Tuple<ResourceKey<Level>, BlockPos> dimensionAndPos = new Tuple<ResourceKey<Level>, BlockPos>(level, pos);
			this.stargateMap.put(stargate, dimensionAndPos);
		});
	}
	
	protected void deserializeTransportRings(CompoundTag blockEntityList)
	{
		CompoundTag transportRings = blockEntityList.getCompound(TRANSPORT_RINGS);
		
		transportRings.getAllKeys().stream().forEach(stargate ->
		{
			ResourceKey<Level> level = Conversion.stringToDimension(transportRings.getCompound(stargate).getString(DIMENSION));
			BlockPos pos = Conversion.intArrayToBlockPos(transportRings.getCompound(stargate).getIntArray(COORDINATES));
			
			Tuple<ResourceKey<Level>, BlockPos> dimensionAndPos = new Tuple<ResourceKey<Level>, BlockPos>(level, pos);
			this.transportRingsMap.put(stargate, dimensionAndPos);
		});
	}*/
	
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
