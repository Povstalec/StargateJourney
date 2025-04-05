package net.povstalec.sgjourney.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.Transporter;

/**
 * Dimension - Frequency - Rings
 * @author Povstalec
 *
 */
public final class TransporterNetwork extends SavedData
{
	private static final String FILE_NAME = StargateJourney.MODID + "-transporter_network";

	/*private static final String COORDINATES = "Coordinates";

	private static final String RINGS_A = "RingsA";
	private static final String RINGS_B = "RingsB";
	private static final String CONNECTION_TIME = "ConnectionTime";*/
	private static final String DIMENSIONS = "Dimensions";
	private static final String CONNECTIONS = "Connections";

	private static final String VERSION = "Version";

	private static final int updateVersion = 1;
	
	private MinecraftServer server;

	private Map<String, TransporterNetwork.Dimension> dimensions = new HashMap<String, TransporterNetwork.Dimension>();
	private int version = 0;
	
	//============================================================================================
	//******************************************Versions******************************************
	//============================================================================================
	
	public final int getVersion()
	{
		return this.version;
	}
	
	private final void updateVersion()
	{
		this.version = updateVersion;
	}
	
	public final void updateNetwork(MinecraftServer server)
	{
		if(getVersion() == updateVersion)
		{
			StargateJourney.LOGGER.info("Transporter Network is up to date (Version: " + version + ")");
			return;
		}
		
		StargateJourney.LOGGER.info("Detected an incompatible Transporter Network version (Version: " + getVersion() + ") - updating to version " + updateVersion);
		
		reloadNetwork(server, false);
	}
	
	public final void reloadNetwork(MinecraftServer server, boolean updateInterfaces)
	{
		eraseNetwork();
		StargateJourney.LOGGER.info("Transporter Network erased");
		
		addTransporters();
		StargateJourney.LOGGER.info("Transporters added");
		
		updateVersion();
		StargateJourney.LOGGER.info("Version updated");
		
		this.setDirty();
	}
	
	public final void eraseNetwork()
	{
		this.dimensions.clear();
		
		this.setDirty();
	}
	
	private final void addTransporters()
	{
		HashMap<UUID, Transporter> transporters = BlockEntityList.get(server).getTransporters();
		
		transporters.entrySet().stream().forEach((transporterInfo) ->
		{
			Transporter transporter = transporterInfo.getValue();
			
			BlockEntity blockentity = server.getLevel(transporter.getDimension()).getBlockEntity(transporter.getBlockPos());
			
			if(blockentity instanceof AbstractTransporterEntity transporterEntity)
			{
				if(transporterEntity.getID() != null && transporterEntity.getID().equals(transporter.getID()))
				{
					addTransporterToDimension(transporter.getDimension(), transporter);
				}
				else
				{
					BlockEntityList.get(server).removeTransporter(transporter.getID());
					addTransporter(transporterEntity);
				}
			}
			else
				BlockEntityList.get(server).removeTransporter(transporter.getID());
			
		});
	}
	
	//============================================================================================
	//****************************************Transporters****************************************
	//============================================================================================
	
	public final void addTransporter(AbstractTransporterEntity transporterEntity)
	{
		Optional<Transporter> transporterOptional = BlockEntityList.get(server).addTransporter(transporterEntity);
		
		if(transporterOptional.isPresent())
		{
			Transporter transporter = transporterOptional.get();
			
			if(transporterEntity.getID() != null && transporterEntity.getID().equals(transporter.getID()))
				addTransporterToDimension(transporter.getDimension(), transporter);
		}
		
		this.setDirty();
	}
	
	public final void removeTransporter(Level level, UUID id)
	{
		if(id == null)
			return;
		
		Transporter transporter = getTransporter(id);
		
		if(transporter != null)
			removeTransporterFromDimension(level.dimension(), transporter);

		BlockEntityList.get(level).removeTransporter(id);
		
		StargateJourney.LOGGER.info("Removed " + id.toString() + " from Transporter Network");
		setDirty();
	}
	
	@Nullable
	public final Transporter getTransporter(UUID id)
	{
		return BlockEntityList.get(server).getTransporters().get(id);
	}
	
	
	
	public final void addTransporterToDimension(ResourceKey<Level> dimensionKey, Transporter transporter)
	{
		String dimensionString = dimensionKey.location().toString();
		
		if(!dimensions.containsKey(dimensionString))
			dimensions.put(dimensionString, new TransporterNetwork.Dimension(dimensionKey));
		
		dimensions.get(dimensionString).addTransporter(transporter);
	}
	
	public final void removeTransporterFromDimension(ResourceKey<Level> dimensionKey, Transporter transporter)
	{
		String dimensionString = dimensionKey.location().toString();
		
		if(dimensions.containsKey(dimensionString))
			dimensions.get(dimensionString).removeTransporter(transporter);
	}
	
	public final Optional<List<Transporter>> getTransportersFromDimension(ResourceKey<Level> dimensionKey)
	{
		if(dimensions.containsKey(dimensionKey.location().toString()))
		{
			Dimension dimension = dimensions.get(dimensionKey.location().toString());
			
			return Optional.of(dimension.getTransporters());
		}
		
		return Optional.empty();
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
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private final CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(VERSION, this.version);
		tag.put(DIMENSIONS, serializeDimensions());
		tag.put(CONNECTIONS, serializeConnections());
		
		return tag;
	}
	
	private final CompoundTag serializeDimensions()
	{
		CompoundTag dimensionsTag = new CompoundTag();
		
		this.dimensions.forEach((dimensionString, dimension) ->
		{
			dimensionsTag.put(dimensionString, dimension.serialize());
		});
		
		return dimensionsTag;
	}
	
	private final CompoundTag serializeConnections()
	{
		CompoundTag connectionsTag = new CompoundTag();
		
		/*this.connections.forEach((connectionID, connection) ->
		{
			connectionsTag.put(connectionID, connection.serialize());
		});*/
		
		return connectionsTag;
	}
	
	private final void deserialize(CompoundTag tag)
	{
		this.version = tag.getInt(VERSION);

		deserializeDimensions(tag.getCompound(DIMENSIONS));
		deserializeConnections(tag.getCompound(CONNECTIONS));
	}
	
	private final void deserializeDimensions(CompoundTag tag)
	{
		tag.getAllKeys().forEach(dimensionString ->
		{
			this.dimensions.put(dimensionString, TransporterNetwork.Dimension.deserialize(server, tag.getCompound(dimensionString)));
		});
	}
	
	private final void deserializeConnections(CompoundTag tag)
	{
		/*tag.getAllKeys().forEach(connectionID ->
		{
			this.connections.put(connectionID, StargateConnection.deserialize(server, connectionID, tag.getCompound(connectionID)));
		});*/
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
				Transporter transporter = BlockEntityList.get(server).getTransporter(UUID.fromString(transporterID));
				
				if(transporter != null)
					transporters.add(transporter);
			});
			
			return new TransporterNetwork.Dimension(dimension, transporters);
		}
    }
}
