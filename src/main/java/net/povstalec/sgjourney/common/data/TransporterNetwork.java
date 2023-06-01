package net.povstalec.sgjourney.common.data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.stargate.Dialing;

/**
 * Dimension - Frequency - Rings
 * @author Povstalec
 *
 */
public class TransporterNetwork extends SavedData
{
	private static final String FILE_NAME = "sgjourney-rings_network";

	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";

	private static final String CONNECTIONS = "Connections";
	private static final String RINGS_A = "RingsA";
	private static final String RINGS_B = "RingsB";
	private static final String CONNECTION_TIME = "ConnectionTime";
	
	private CompoundTag ringsNetwork = new CompoundTag();
	
	public CompoundTag getRings(String dimension)
	{
		return ringsNetwork.copy().getCompound(dimension);
	}
	
	public void addToNetwork(String ringsID, CompoundTag rings)
	{
		CompoundTag dimension = getRings(rings.getString(DIMENSION));
		CompoundTag localRings = new CompoundTag();
		
		localRings.putIntArray(COORDINATES, rings.getIntArray(COORDINATES));
		dimension.put(ringsID, localRings);
		ringsNetwork.put(rings.getString(DIMENSION), dimension);
		
		setDirty();
		StargateJourney.LOGGER.info("Added Rings " + ringsID + " to Rings Network");
		//System.out.println(ringsNetwork);
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
		CompoundTag close = new CompoundTag();
		
		List<String> ids = dim.getAllKeys().stream().collect(Collectors.toList());
		
		for(int i = 0; i < ids.size(); i++)
		{
			String id = ids.get(i);
			BlockPos pos2 = intToPos(dim.getCompound(id).getIntArray(COORDINATES));
			double dist = distance(pos, pos2);
			
			if(dist <= distance)
			{
				distance = dist;
				close = new CompoundTag();
				close.put(id, dim.getCompound(id));
			}
		}
		rings.merge(close);
		
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
		
		//System.out.println("RINGS " + rings.size() + " " + rings);
		return rings;
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public void handleConnections(MinecraftServer server)
	{
		if(!getConnections().isEmpty())
			getConnections().getAllKeys().forEach(uuid -> handleConnection(server, uuid));
	}
	
	private void handleConnection(MinecraftServer server, String uuid)
	{
		CompoundTag connections = getConnections();
		CompoundTag connection = connections.getCompound(uuid);
		
		TransportRingsEntity ringsA = loadRings(server, connection.getCompound(RINGS_A));
		TransportRingsEntity ringsB = loadRings(server, connection.getCompound(RINGS_B));
		
		int connectionTime = connection.getInt(CONNECTION_TIME);
		connectionTime++;
		
		if(ringsA == null || ringsB == null)
		{
			terminateConnection(server, uuid);
			return;
		}
		
		connection.putInt(CONNECTION_TIME, connectionTime);
		connections.put(uuid, connection);
		this.ringsNetwork.put(CONNECTIONS, connections);
		this.setDirty();
		
		int heightA = ringsA.getTransportHeight();
		int heightB = ringsB.getTransportHeight();
		
		int greaterHeight = heightA >= heightB ? heightA : heightB;
		
		ringsA.setProgress(synchronizeProgress(greaterHeight, heightA, connectionTime));
		ringsA.setProgress(synchronizeProgress(greaterHeight, heightB, connectionTime));
	}
	
	public CompoundTag getConnections()
	{
		return this.ringsNetwork.getCompound(CONNECTIONS).copy();
	}
	
	public CompoundTag getConnection(String uuid)
	{
		return getConnections().getCompound(uuid);
	}
	
	public ResourceKey<Level> getDimension(String uuid, String rings)
	{
		return Conversion.stringToDimension(getConnection(uuid).getCompound(rings).getString(DIMENSION));
	}
	
	public TransportRingsEntity getRings(MinecraftServer server, String uuid, String rings)
	{
		return loadRings(server, getConnection(uuid).getCompound(rings));
	}
	
	public String startConnection(TransportRingsEntity ringsA, TransportRingsEntity ringsB)
	{
		if(ringsA.equals(ringsB))
		{
			StargateJourney.LOGGER.info("Rings cannot create connection with itself");
			return "INVALID_SELF";
		}

		String uuid = UUID.randomUUID().toString();
		CompoundTag connections = getConnections();
		CompoundTag connection = new CompoundTag();
		CompoundTag ringsAInfo = saveRings(ringsA);
		CompoundTag ringsBInfo = saveRings(ringsB);
		
		connection.put(RINGS_A, ringsAInfo);
		connection.put(RINGS_B, ringsBInfo);
		connections.put(uuid, connection);
		
		this.ringsNetwork.put(CONNECTIONS, connections);
		this.setDirty();
		StargateJourney.LOGGER.info("Created connection " + uuid);
		
		return uuid;
	}
	
	private int synchronizeProgress(int greaterHeight, int height, int progress)
	{
		int sync = height - greaterHeight + progress;
		
		// If the rings have take less time to raise, they won't start progressing until the progress is great enough
		return sync > 0 ? sync : 0;
	}
	
	private CompoundTag saveRings(TransportRingsEntity rings)
	{
		CompoundTag ringsInfo = new CompoundTag();
		
		ringsInfo.putString(DIMENSION, rings.getLevel().dimension().location().toString());
		ringsInfo.putIntArray(COORDINATES, new int[] {rings.getBlockPos().getX(), rings.getBlockPos().getY(), rings.getBlockPos().getZ()});
		
		return ringsInfo;
	}
	
	private TransportRingsEntity loadRings(MinecraftServer server, CompoundTag ringsInfo)
	{
		ResourceKey<Level> dimension = Conversion.stringToDimension(ringsInfo.getString(DIMENSION));
		BlockPos pos = Conversion.intArrayToBlockPos(ringsInfo.getIntArray(COORDINATES));
		
		BlockEntity blockEntity = server.getLevel(dimension).getBlockEntity(pos);
		
		if(blockEntity instanceof TransportRingsEntity rings)
			return rings;
		
		return null;
	}
	
	public void terminateConnection(MinecraftServer server, String uuid)
	{
		if(!getConnections().contains(uuid))
		{
			StargateJourney.LOGGER.info("Could not find connection " + uuid);
			return;
		}
		
		TransportRingsEntity ringsA = loadRings(server, getConnections().getCompound(uuid).getCompound(RINGS_A));
		TransportRingsEntity ringsB = loadRings(server, getConnections().getCompound(uuid).getCompound(RINGS_B));
		
		/*if(dialingStargate != null)
			dialingStargate.resetStargate(false);
		if(dialedStargate != null)
			dialedStargate.resetStargate(false);*/ //TODO Rewrite this to make sense for Rings
		
		this.ringsNetwork.getCompound(CONNECTIONS).remove(uuid);
		this.setDirty();
		StargateJourney.LOGGER.info("Ended connection " + uuid);
	}
	
//================================================================================================

	public static TransporterNetwork create()
	{
		return new TransporterNetwork();
	}
	
	public static TransporterNetwork load(CompoundTag tag)
	{
		TransporterNetwork data = create();

		data.ringsNetwork = tag.copy();
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = this.ringsNetwork.copy();
		
		return tag;
	}
	
	@Nonnull
	public static TransporterNetwork get(Level level)
	{
		if(level.isClientSide)
			throw new RuntimeException("Don't access this client-side!");
		
		return TransporterNetwork.get(level.getServer());
	}

    @Nonnull
	public static TransporterNetwork get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(TransporterNetwork::load, TransporterNetwork::create, FILE_NAME);
    }
}
