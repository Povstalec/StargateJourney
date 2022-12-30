package net.povstalec.sgjourney.data;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;

/**
 * Dimension - Frequency - Rings
 * @author Povstalec
 *
 */
public class RingsNetwork extends SavedData
{
	private CompoundTag ringsNetwork = new CompoundTag();
	
	public CompoundTag getRings(String dimension)
	{
		return ringsNetwork.copy().getCompound(dimension);
	}
	
	public void addToNetwork(String ringsID, CompoundTag rings)
	{
		CompoundTag dimension = getRings(rings.getString("Dimension"));
		CompoundTag localRings = new CompoundTag();
		
		localRings.putIntArray("Coordinates", rings.getIntArray("Coordinates"));
		dimension.put(ringsID, localRings);
		ringsNetwork.put(rings.getString("Dimension"), dimension);
		
		setDirty();
		StargateJourney.LOGGER.info("Added Rings " + ringsID + " to Rings Network");
		System.out.println(ringsNetwork);
	}
	
	public void removeFromNetwork(Level level, String ringsID)
	{
		removeFromNetwork(level.dimension().location().toString(), ringsID);
	}
	
	public void removeFromNetwork(String dimension, String ringsID)
	{
		this.ringsNetwork.getCompound(dimension).remove(ringsID);
		setDirty();
		StargateJourney.LOGGER.info("Removing from network " + ringsID);
	}
	
	
	
	private BlockPos intToPos(int[] coords)
	{
		return new BlockPos(coords[0], coords[1], coords[2]);
	}
	
	private double distance(BlockPos pos1, BlockPos pos2)
	{
		return Math.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2) + Math.pow(pos1.getY() - pos2.getY(), 2) + Math.pow(pos1.getZ() - pos2.getZ(), 2));
	}
	
	private CompoundTag closestRings(CompoundTag dim, CompoundTag rings, BlockPos pos, double maxDistance)
	{
		double distance = maxDistance;
		
		List<String> ids = dim.getAllKeys().stream().collect(Collectors.toList());
		
		for(int i = 0; i < ids.size(); i++)
		{
			String id = ids.get(i);
			BlockPos pos2 = intToPos(dim.getCompound(id).getIntArray("Coordinates"));
			double dist = distance(pos, pos2);
			
			if(dist <= distance)
			{
				distance = dist;
				rings.put(id, dim.getCompound(id));
				return rings;
			}
		}
		
		return rings;
	}
	
	public CompoundTag getClosestRingsFromTag(String dimension, BlockPos pos, CompoundTag rings, double maxDistance)
	{
		CompoundTag dim = getRings(dimension);
		
		return getClosestRingsFromTag(dimension, pos, rings, maxDistance, dim);
	}
	
	public CompoundTag getClosestRingsFromTag(String dimension, BlockPos pos, CompoundTag rings, double maxDistance, String excludedID)
	{
		CompoundTag dim = getRings(dimension);
		dim.remove(excludedID);
		
		return getClosestRingsFromTag(dimension, pos, rings, maxDistance, dim);
	}
	
	private CompoundTag getClosestRingsFromTag(String dimension, BlockPos pos, CompoundTag rings, double maxDistance, CompoundTag dim)
	{
		//Removes repeating IDs
		if(!rings.isEmpty())
			rings.getAllKeys().stream().forEach((id) -> dim.remove(id));
		
		rings = closestRings(dim, rings, pos, maxDistance);
		
		return rings;
	}
	
	
	
	public CompoundTag get6ClosestRingsFromTag(String dimension, BlockPos pos, double maxDistance, String excludedID)
	{
		CompoundTag rings = new CompoundTag();
		
		if(!ringsNetwork.contains(dimension))
			return rings;
		
		for(int i = 0; i < 6; i++)
		{
			rings = getClosestRingsFromTag(dimension, pos, rings, maxDistance, excludedID);
		}
		
		System.out.println("RINGS " + rings.size() + " " + rings);
		return rings;
	}
	
//================================================================================================

	public static RingsNetwork create()
	{
		return new RingsNetwork();
	}
	
	public static RingsNetwork load(CompoundTag tag)
	{
		RingsNetwork data = create();

		data.ringsNetwork = tag.copy();
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = this.ringsNetwork.copy();
		
		return tag;
	}

    @Nonnull
	public static RingsNetwork get(Level level)
    {
    	MinecraftServer server = level.getServer();
    	
        if (level.isClientSide)
            throw new RuntimeException("Don't access this client-side!");
        
        DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(RingsNetwork::load, RingsNetwork::create, "sgjourney-rings_network");
    }
}
