package net.povstalec.sgjourney.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

/**
 * Dimension - Frequency - Rings
 * @author Povstalec
 *
 */
public final class TransporterNetwork extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-transporter_network";
	
	private static final String DIMENSIONS = "dimensions";
	private static final String CONNECTIONS = "connections";

	private static final String VERSION = "version";

	private static final int UPDATE_VERSION = 3;
	
	private MinecraftServer server;

	private Map<String, TransporterNetwork.Dimension> dimensions = new HashMap<String, TransporterNetwork.Dimension>();
	private HashMap<UUID, TransporterConnection> connections = new HashMap<UUID, TransporterConnection>();
	private int version = 0;
	
	//============================================================================================
	//******************************************Versions******************************************
	//============================================================================================
	
	public int getVersion()
	{
		return this.version;
	}
	
	private void updateVersion()
	{
		this.version = UPDATE_VERSION;
	}
	
	public void updateNetwork(MinecraftServer server)
	{
		if(getVersion() == UPDATE_VERSION)
		{
			StargateJourney.LOGGER.info("Transporter Network is up to date (Version: " + version + ")");
			return;
		}
		
		StargateJourney.LOGGER.info("Detected an incompatible Transporter Network version (Version: " + getVersion() + ") - updating to version " + UPDATE_VERSION);
		
		reloadNetwork(server, false);
	}
	
	public void reloadNetwork(MinecraftServer server, boolean updateInterfaces)
	{
		eraseNetwork();
		StargateJourney.LOGGER.info("Transporter Network erased");
		
		addTransporters();
		StargateJourney.LOGGER.info("Transporters added");
		
		updateVersion();
		StargateJourney.LOGGER.info("Version updated");
		
		this.setDirty();
	}
	
	public void eraseNetwork()
	{
		this.dimensions.clear();
		
		this.setDirty();
	}
	
	private void addTransporters()
	{
		HashMap<TransporterID, Transporter> transporters = BlockEntityList.get(server).getTransporters();
		
		transporters.entrySet().stream().forEach((transporterInfo) ->
		{
			Transporter transporter = transporterInfo.getValue();
			
			if(transporter != null)
				addTransporter(transporter);
		});
	}
	
	//============================================================================================
	//****************************************Transporters****************************************
	//============================================================================================
	
	public void addTransporter(Transporter transporter)
	{
		if(transporter != null)
			addTransporterToDimension(transporter.getDimension(), transporter);
		
		this.setDirty();
	}
	
	public void addTransporter(AbstractTransporterEntity transporterEntity)
	{
		Transporter transporter = BlockEntityList.get(server).addTransporter(transporterEntity);
		
		if(transporter != null && transporterEntity.getID() != null && transporterEntity.getID().equals(transporter.getID()))
			addTransporter(transporter);
	}
	
	public void removeTransporter(Level level, TransporterID transporterID)
	{
		if(transporterID == null)
			return;
		
		Transporter transporter = getTransporter(transporterID);
		
		if(transporter != null)
			removeTransporterFromDimension(level.dimension(), transporter);

		BlockEntityList.get(level).removeTransporter(transporterID);
		
		StargateJourney.LOGGER.info("Removed " + transporterID.toString() + " from Transporter Network");
		setDirty();
	}
	
	@Nullable
	public Transporter getTransporter(TransporterID transporterID)
	{
		return BlockEntityList.get(server).getTransporter(transporterID);
	}
	
	
	
	public void addTransporterToDimension(ResourceKey<Level> dimensionKey, Transporter transporter)
	{
		String dimensionString = dimensionKey.location().toString();
		
		if(!dimensions.containsKey(dimensionString))
			dimensions.put(dimensionString, new TransporterNetwork.Dimension(dimensionKey));
		
		dimensions.get(dimensionString).addTransporter(transporter);
	}
	
	public void removeTransporterFromDimension(ResourceKey<Level> dimensionKey, Transporter transporter)
	{
		String dimensionString = dimensionKey.location().toString();
		
		if(dimensions.containsKey(dimensionString))
			dimensions.get(dimensionString).removeTransporter(transporter);
	}
	
	public List<Transporter> getTransportersFromDimension(ResourceKey<Level> dimensionKey)
	{
		if(dimensions.containsKey(dimensionKey.location().toString()))
		{
			Dimension dimension = dimensions.get(dimensionKey.location().toString());
			
			if(dimension != null)
				return dimension.getTransporters();
		}
		
		return ImmutableList.of();
	}
	
	public final void printDimensions()
	{
		System.out.println("[Dimensions - Transporters]");
		this.dimensions.entrySet().stream().forEach(dimensionEntry ->
		{
			dimensionEntry.getValue().printDimension();
		});
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public void handleConnections()
	{
		Map<UUID, TransporterConnection> connections = new HashMap<>();
		connections.putAll(this.connections);
		
		connections.forEach((uuid, connection) -> connection.tick(server));
		this.setDirty();
	}
	
	//TODO Maybe replace booleans with Transporter Feedback
	public boolean createConnection(MinecraftServer server, Transporter transporterA, Transporter transporterB)
	{
		TransporterConnection connection = TransporterConnection.create(server, transporterA, transporterB);
		
		if(connection == null)
			return false;
		
		return addConnection(connection);
	}
	
	public boolean addConnection(TransporterConnection connection)
	{
		if(hasConnection(connection.getID()))
			return false;
		
		this.connections.put(connection.getID(), connection);
		return true;
	}
	
	public boolean hasConnection(UUID uuid)
	{
		if(this.connections.containsKey(uuid))
			return true;
		
		return false;
	}
	
	public void terminateConnection(UUID uuid)
	{
		TransporterConnection connection = this.connections.get(uuid);
		
		if(connection == null)
			return;
		
		connection.terminate(server);
	}
	
	public void removeConnection(UUID uuid)
	{
		if(hasConnection(uuid))
		{
			this.connections.remove(uuid);
			StargateJourney.LOGGER.debug("Removed connection " + uuid);
		}
		else
			StargateJourney.LOGGER.error("Could not find connection " + uuid);
		this.setDirty();
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(VERSION, this.version);
		tag.put(DIMENSIONS, serializeDimensions());
		tag.put(CONNECTIONS, serializeConnections());
		
		return tag;
	}
	
	private CompoundTag serializeDimensions()
	{
		CompoundTag dimensionsTag = new CompoundTag();
		
		this.dimensions.forEach((dimensionString, dimension) ->
		{
			dimensionsTag.put(dimensionString, dimension.serialize());
		});
		
		return dimensionsTag;
	}
	
	private CompoundTag serializeConnections()
	{
		CompoundTag connectionsTag = new CompoundTag();
		
		this.connections.forEach((connectionID, connection) ->
		{
			connectionsTag.put(connectionID.toString(), connection.serialize());
		});
		
		return connectionsTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		this.version = tag.getInt(VERSION);

		deserializeDimensions(tag.getCompound(DIMENSIONS));
		deserializeConnections(tag.getCompound(CONNECTIONS));
	}
	
	private void deserializeDimensions(CompoundTag tag)
	{
		tag.getAllKeys().forEach(dimensionString ->
		{
			this.dimensions.put(dimensionString, TransporterNetwork.Dimension.deserialize(server, tag.getCompound(dimensionString)));
		});
	}
	
	private void deserializeConnections(CompoundTag tag)
	{
		for(String connectionID : tag.getAllKeys())
		{
			try
			{
				UUID uuid = UUID.fromString(connectionID);
				TransporterConnection connection = TransporterConnection.deserialize(server, UUID.fromString(connectionID), tag.getCompound(connectionID));
				
				if(connection != null)
					this.connections.put(uuid, connection);
			}
			catch(IllegalArgumentException e)
			{
				StargateJourney.LOGGER.error(e.toString());
			}
		}
	}
	
//================================================================================================
	
	public TransporterNetwork(MinecraftServer server)
	{
		this.server = server;
	}

	public static TransporterNetwork create(MinecraftServer server)
	{
		return new TransporterNetwork(server);
	}
	
	public static TransporterNetwork load(MinecraftServer server, CompoundTag tag)
	{
		TransporterNetwork data = create(server);
		
		data.server = server;
		data.deserialize(tag);
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = serialize();
		
		return tag;
	}
	
	@Nonnull
	public static TransporterNetwork get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");
		
		return TransporterNetwork.get(level.getServer());
	}

    @Nonnull
	public static TransporterNetwork get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
    }
    
    
    
    private static class Dimension
    {
    	private static final String DIMENSION = "Dimension";
    	private static final String TRANSPORTERS = "Transporters";
    	
    	private final ResourceKey<Level> dimension;
    	private List<Transporter> transporters = new ArrayList<Transporter>();
    	
    	private Dimension(ResourceKey<Level> dimension)
    	{
    		this.dimension = dimension;
    	}
    	
    	private Dimension(ResourceKey<Level> dimension, List<Transporter> transporters)
    	{
    		this.dimension = dimension;
    		this.transporters = transporters;
    	}
    	
    	public void addTransporter(Transporter transporter)
    	{
    		if(transporters.contains(transporter))
    			return;
    		
    		transporters.add(transporter);
    	}
    	
    	public void removeTransporter(Transporter transporter)
    	{
    		if(transporters.contains(transporter))
    			transporters.remove(transporter);
    	}
    	
    	public List<Transporter> getTransporters()
    	{
    		return new ArrayList<Transporter>(transporters);
    	}
    	
    	public void printDimension()
    	{
			System.out.println("- [" + this.dimension.location().toString() + "]");
			transporters.stream().forEach(transporter ->
			{
				System.out.println("--- " + transporter.toString());;
			});
    	}
    	
    	
    	
    	public CompoundTag serialize()
		{
			CompoundTag dimensionTag = new CompoundTag();
			
			dimensionTag.putString(DIMENSION, this.dimension.location().toString());
			
			CompoundTag transportersTag = new CompoundTag();
			transporters.stream().forEach(transporter ->
			{
				transportersTag.putString(transporter.getID().toString(), transporter.getID().toString());
			});
			dimensionTag.put(TRANSPORTERS, transportersTag);
			
			return dimensionTag;
		}
		
		public static TransporterNetwork.Dimension deserialize(MinecraftServer server, CompoundTag dimensionTag)
		{
			ResourceKey<Level> dimension = Conversion.stringToDimension(dimensionTag.getString(DIMENSION));
			
	    	List<Transporter> transporters = new ArrayList<Transporter>();
			dimensionTag.getCompound(TRANSPORTERS).getAllKeys().forEach(transporterID ->
			{
				Transporter transporter = BlockEntityList.get(server).getTransporter(new TransporterID.Immutable(transporterID));
				
				if(transporter != null)
					transporters.add(transporter);
			});
			
			return new TransporterNetwork.Dimension(dimension, transporters);
		}
    }
}
