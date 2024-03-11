package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Transporter;

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
	
	protected HashMap<Address, Stargate> stargateMap = new HashMap<>();
	protected HashMap<String, Transporter> transporterMap = new HashMap<>();
	
	/*public CompoundTag addBlockEntity(Level level, BlockPos pos, String listName, String id)
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
	}*/
	
	/**
	 * Adds Stargate to Stargate Network
	 * @param stargate
	 * @return Optional containing Stargate that got added if successful, empty optional if unsuccessful
	 */
	public Optional<Stargate> addStargate(AbstractStargateEntity stargate)
	{
		Address address = stargate.get9ChevronAddress();
		
		if(address.getLength() != 8)
			return Optional.empty();
		
		if(this.stargateMap.containsKey(address))
			return Optional.empty();
		
		if(stargate.getLevel() == null)
			return Optional.empty();
		
		if(stargate.getBlockPos() == null)
			return Optional.empty();
		
		Stargate savedStargate = new Stargate(stargate);
		
		this.stargateMap.put(address, savedStargate);
		
		this.setDirty();
		
		StargateJourney.LOGGER.info("Stargate " + address.toString() + " to BlockEntityList");
		
		return Optional.of(savedStargate);
	}
	
	public void addTransporter(Level level, BlockPos pos, String id)
	{
		Transporter transporter = new Transporter(level.dimension(), pos);
		
		this.transporterMap.put(id, transporter);
		
		this.setDirty();
	}
	
	/*public void removeBlockEntity(String type, String id)
	{
		if(!getBlockEntities(type).contains(id))
		{
			StargateJourney.LOGGER.error(type + " does not contain " + id);
			return;
		}
		blockEntityList.getCompound(type).remove(id);
		StargateJourney.LOGGER.info("Removed " + id + " from " + type);
		setDirty();
	}*/
	
	public void removeStargate(Address id)
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
		if(!this.transporterMap.containsKey(id))
		{
			StargateJourney.LOGGER.error(id + " not found in BlockEntityList");
			return;
		}
		this.transporterMap.remove(id);
		StargateJourney.LOGGER.info("Removed " + id + " from BlockEntityList");
		setDirty();
	}
	
	/*public CompoundTag getBlockEntities(String blockEntities)
	{
		return blockEntityList.copy().getCompound(blockEntities);
	}*/

    @SuppressWarnings("unchecked")
	public HashMap<Address, Stargate> getStargates()
	{
		return (HashMap<Address, Stargate>) stargateMap.clone();
	}

    @SuppressWarnings("unchecked")
	public HashMap<String, Transporter> getTransporters()
	{
		return (HashMap<String, Transporter>) transporterMap.clone();
	}
	
	public Optional<Stargate> getStargate(Address address)
	{
		if(address.getLength() == 8)
		{
			Stargate stargate = stargateMap.get(address);
			
			if(stargate!= null)
				return Optional.of(stargate);
		}
		
		return Optional.empty();
	}
	
	public Optional<Transporter> getTransporter(String id)
	{
		Transporter transporter = transporterMap.get(id);
		
		if(transporter!= null)
			return Optional.of(transporter);
		
		return Optional.empty();
	}
	
	//================================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag blockEntityList = new CompoundTag();
		CompoundTag stargates = serializeStargates();
		CompoundTag transportRings = serializeTransporters();
		
		blockEntityList.put(STARGATES, stargates);
		blockEntityList.put(TRANSPORTERS, transportRings);
		
		return blockEntityList;
	}
	
	private CompoundTag serializeStargates()
	{
		CompoundTag stargates = new CompoundTag();
		
		this.stargateMap.forEach((stargateID, stargate) -> 
		{
			stargates.put(stargateID.toString(), stargate.serialize());
		});
		
		return stargates;
	}
	
	private CompoundTag serializeTransporters()
	{
		CompoundTag transportersTag = new CompoundTag();
		
		this.transporterMap.forEach((ringsID, transporter) -> 
		{
			transportersTag.put(ringsID, transporter.serialize());
		});
		
		return transportersTag;
	}
	
	public void deserialize(CompoundTag tag)
	{
		CompoundTag blockEntityList = tag.copy();
		
		deserializeStargates(blockEntityList);
		deserializeTransporters(blockEntityList);
	}
	
	private void deserializeStargates(CompoundTag blockEntityList)
	{
		StargateJourney.LOGGER.info("Deserializing Stargates");
		CompoundTag stargates = blockEntityList.getCompound(STARGATES);
		
		stargates.getAllKeys().stream().forEach(stargate ->
		{
			StargateJourney.LOGGER.info("Deserializing Stargate " + stargate);
			Address address = new Address(stargate);
			this.stargateMap.put(address, Stargate.deserialize(stargates.getCompound(stargate), address));
		});
	}
	
	private void deserializeTransporters(CompoundTag blockEntityList)
	{
		// Transport Rings deserialization for legacy reasons
		if(blockEntityList.contains(TRANSPORT_RINGS))
		{
			CompoundTag transportRingsTag = blockEntityList.getCompound(TRANSPORT_RINGS);
			
			transportRingsTag.getAllKeys().stream().forEach(transportRings ->
			{
				this.transporterMap.put(transportRings, Transporter.deserialize(transportRingsTag.getCompound(transportRings)));
			});
		}
		
		CompoundTag transportersTag = blockEntityList.getCompound(TRANSPORTERS);
		
		transportersTag.getAllKeys().stream().forEach(transporter ->
		{
			this.transporterMap.put(transporter, Transporter.deserialize(transportersTag.getCompound(transporter)));
		});
	}
	
	//================================================================================================

	public static BlockEntityList create()
	{
		return new BlockEntityList();
	}
	
	public static BlockEntityList load(CompoundTag tag)
	{
		BlockEntityList data = create();
		
		data.deserialize(tag);
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = serialize();
		
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
    
    /*public static final class Stargate
    {
    	private final ResourceKey<Level> dimension;
    	private final BlockPos blockPos;
    	
    	public Stargate(ResourceKey<Level> dimension, BlockPos blockPos)
    	{
    		this.dimension = dimension;
    		this.blockPos = blockPos;
    	}
    	
    	public ResourceKey<Level> getDimension()
    	{
    		return dimension;
    	}
    	
    	public BlockPos getBlockPos()
    	{
    		return blockPos;
    	}
    	
    	public CompoundTag serialize()
    	{
    		CompoundTag stargateTag = new CompoundTag();
			ResourceKey<Level> level = this.getDimension();
			BlockPos pos = this.getBlockPos();
			
			stargateTag.putString(DIMENSION, level.location().toString());
			stargateTag.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
			
			return stargateTag;
    	}
    	
    	public static Stargate deserialize(CompoundTag tag)
    	{
    		ResourceKey<Level> dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
    		BlockPos blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
    		
    		if(dimension != null && blockPos != null)
    			return new Stargate(dimension, blockPos);
    		
    		return null;
    	}
    }*/
}
