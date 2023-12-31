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

/**
 * This class is designed to save all Block Entities along with their coordinates and dimensions. 
 * @author Povstalec
 *
 */
public class BlockEntityList extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-block_entities";
	private static final String INCORRECT_FILE_NAME = StargateJourney.MODID + "-block_enties"; //I wish there was a way to replace this
	
	public static final String STARGATES = "Stargates";
	public static final String TRANSPORT_RINGS = "TransportRings";
	public static final String TRANSPORTERS = "Transporters"; //TODO Replace TransportRings with this

	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";
	
	protected CompoundTag blockEntityList = new CompoundTag();
	
	protected Map<String, Tuple<ResourceKey<Level>, BlockPos>> stargateMap = new HashMap<>();
	protected Map<String, Tuple<ResourceKey<Level>, BlockPos>> transporterMap = new HashMap<>();
	
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
	
	/*public void addStargate(Level level, BlockPos pos, String id)
	{
		Tuple<ResourceKey<Level>, BlockPos> dimensionAndCoords = new Tuple<ResourceKey<Level>, BlockPos>(level.dimension(), pos);
		
		this.stargateMap.put(id, dimensionAndCoords);
		
		this.setDirty();
	}
	
	public void addTransporter(Level level, BlockPos pos, String id)
	{
		Tuple<ResourceKey<Level>, BlockPos> dimensionAndCoords = new Tuple<ResourceKey<Level>, BlockPos>(level.dimension(), pos);
		
		this.transporterMap.put(id, dimensionAndCoords);
		
		this.setDirty();
	}*/
	
	public void removeBlockEntity(String type, String id)
	{
		if(!getBlockEntities(type).contains(id))
		{
			StargateJourney.LOGGER.error(type + " does not contain " + id);
			return;
		}
		blockEntityList.getCompound(type).remove(id);
		StargateJourney.LOGGER.info("Removed " + id + " from " + type);
		setDirty();
	}
	
	/*public void removeStargate(String id)
	{
		if(!this.stargateMap.containsKey(id))
		{
			StargateJourney.LOGGER.error(id + " not found in BlockEntityList");
			return;
		}
		this.stargateMap.remove(id);
		StargateJourney.LOGGER.info("Removed " + id + " from BlockEntityList");
		setDirty();
	}
	
	public void removeTransporter(String id)
	{
		if(!this.stargateMap.containsKey(id))
		{
			StargateJourney.LOGGER.error(id + " not found in BlockEntityList");
			return;
		}
		this.stargateMap.remove(id);
		StargateJourney.LOGGER.info("Removed " + id + " from BlockEntityList");
		setDirty();
	}*/
	
	public CompoundTag getBlockEntities(String blockEntities)
	{
		return blockEntityList.copy().getCompound(blockEntities);
	}
	
	/*public Map<String, Tuple<ResourceKey<Level>, BlockPos>> getStargates()
	{
		return this.stargateMap;
	}
	
	public Map<String, Tuple<ResourceKey<Level>, BlockPos>> getTransporters()
	{
		return this.transporterMap;
	}*/
	
	//================================================================================================
	
	/*public CompoundTag serialize()
	{
		CompoundTag blockEntityList = new CompoundTag();
		CompoundTag stargates = serializeStargates();
		CompoundTag transportRings = serializeTransporters();
		
		blockEntityList.put(STARGATES, stargates);
		blockEntityList.put(TRANSPORT_RINGS, transportRings);
		
		return blockEntityList;
	}
	
	private CompoundTag serializeStargates()
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
	
	private CompoundTag serializeTransporters()
	{
		CompoundTag transportRings = new CompoundTag();
		
		this.transporterMap.forEach((ringsID, info) -> 
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
		deserializeTransporters(blockEntityList);
	}
	
	private void deserializeStargates(CompoundTag blockEntityList)
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
	
	private void deserializeTransporters(CompoundTag blockEntityList)
	{
		CompoundTag transportRings = blockEntityList.getCompound(TRANSPORT_RINGS);
		
		transportRings.getAllKeys().stream().forEach(stargate ->
		{
			ResourceKey<Level> level = Conversion.stringToDimension(transportRings.getCompound(stargate).getString(DIMENSION));
			BlockPos pos = Conversion.intArrayToBlockPos(transportRings.getCompound(stargate).getIntArray(COORDINATES));
			
			Tuple<ResourceKey<Level>, BlockPos> dimensionAndPos = new Tuple<ResourceKey<Level>, BlockPos>(level, pos);
			this.transporterMap.put(stargate, dimensionAndPos);
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
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");
		
		return BlockEntityList.get(level.getServer());
	}

    @Nonnull
	public static BlockEntityList get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(BlockEntityList::load, BlockEntityList::create, INCORRECT_FILE_NAME);
    }
}
