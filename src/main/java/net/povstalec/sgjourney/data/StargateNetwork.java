package net.povstalec.sgjourney.data;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.stargate.Dialing;

public class StargateNetwork extends SavedData
{
	private static final String FILE_NAME = "sgjourney-stargate_network";
	private static final String VERSION = "Version";

	private static final String STARGATES = "Stargates";
	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";

	private static final String SOLAR_SYSTEMS = "SolarSystems";

	private static final String CONNECTIONS = "Connections";
	private static final String DIALING_STARGATE = "DialingStargate";
	private static final String DIALED_STARGATE = "DialedStargate";
	
	private static final String INVALID_SELF = StargateJourney.MODID + ":invalid_self";
	
	private CompoundTag stargateNetwork = new CompoundTag();
	private int version = 2;
	
	//============================================================================================
	//******************************************Versions******************************************
	//============================================================================================
	
	public int getVersion()
	{
		CompoundTag network = stargateNetwork.copy();
		
		if(network.contains(VERSION))
			return network.getInt(VERSION);
		
		return 0;
	}
	
	private void updateVersion()
	{
		stargateNetwork.putInt(VERSION, version);
		this.setDirty();
	}
	
	public void updateNetwork(MinecraftServer server)
	{
		if(getVersion() == version)
		{
			StargateJourney.LOGGER.info("Stargate Network is up to date (Version: " + version + ")");
			return;
		}
		
		StargateJourney.LOGGER.info("Detected an incompatible Stargate Network version (Version: " + getVersion() + ") - updating to version " + version);
		
		stellarUpdate(server);
		this.setDirty();
	}
	
	public void eraseNetwork()
	{
		this.stargateNetwork = new CompoundTag();
		this.setDirty();
	}
	
	public void stellarUpdate(MinecraftServer server)
	{
		getConnections().getAllKeys().forEach(connection -> terminateConnection(server, connection));
		Universe.get(server).eraseUniverseInfo();
		Universe.get(server).generateUniverseInfo(server);
		eraseNetwork();
		resetStargates(server);
		updateVersion();
		this.setDirty();
	}
	
	//============================================================================================
	//*****************************************Stargates******************************************
	//============================================================================================
	
	private void resetStargates(MinecraftServer server)
	{
		CompoundTag stargates = BlockEntityList.get(server).getBlockEntities(SGJourneyBlockEntity.Type.STARGATE.id);
		
		stargates.getAllKeys().forEach((stargateID) ->
		{
			String dimensionString = stargates.getCompound(stargateID).getString(DIMENSION);
			ResourceKey<Level> dimension = stringToDimension(dimensionString);
			int[] coordinates = stargates.getCompound(stargateID).getIntArray(COORDINATES);
			
			BlockPos pos = new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
			BlockEntity blockentity = server.getLevel(dimension).getBlockEntity(pos);
			
			if(blockentity instanceof AbstractStargateEntity stargate)
			{
				stargate.resetStargate(false);
				
				if(!getStargates().contains(stargateID))
					addStargate(server, stargateID, BlockEntityList.get(server).getBlockEntities(SGJourneyBlockEntity.Type.STARGATE.id).getCompound(stargateID));
			}
			else
			{
				removeStargate(server.getLevel(dimension), stargateID);
				BlockEntityList.get(server).removeBlockEntity(SGJourneyBlockEntity.Type.STARGATE.id, stargateID);
			}
		});
		this.setDirty();
	}
	
	public void addStargate(MinecraftServer server, String stargateID, CompoundTag stargateInfo)
	{
		String dimension = stargateInfo.getString(DIMENSION);
		String stargateDimension = stargateInfo.getString(DIMENSION);
		int[] stargateCoordinates = stargateInfo.getIntArray(COORDINATES);
		CompoundTag stargates = getStargates();
		CompoundTag stargate = new CompoundTag();

		stargate.putString(DIMENSION, stargateDimension);
		stargate.putIntArray(COORDINATES, stargateCoordinates);
		
		//Adds the Stargate reference to a Solar System (Will only work if the Dimension is inside a Solar System)
		if(Universe.get(server).getDimensions().contains(dimension))
		{
			String systemID = Universe.get(server).getSolarSystemFromDimension(dimension);
			CompoundTag solarSystems = getSolarSystems();
			CompoundTag solarSystem = getSolarSystem(systemID);
			
			solarSystem.put(stargateID, stargate);
			solarSystems.put(systemID, solarSystem);
			this.stargateNetwork.put(SOLAR_SYSTEMS, solarSystems);
		}
		
		//Adds the Stargate reference to the list of Stargates
		stargates.put(stargateID, stargate);
		this.stargateNetwork.put(STARGATES, stargates);
		this.setDirty();
		StargateJourney.LOGGER.info("Added Stargate " + stargateID);
	}
	
	public void removeStargate(Level level, String stargateID)
	{
		String dimension = level.dimension().location().toString();
		
		//Removes the Stargate reference from the Solar System
		this.stargateNetwork.getCompound(SOLAR_SYSTEMS).getCompound(Universe.get(level).getSolarSystemFromDimension(dimension)).remove(stargateID);
		
		//Removes the Stargate reference from the list of Stargates
		this.stargateNetwork.getCompound(STARGATES).remove(stargateID);
		this.setDirty();
		StargateJourney.LOGGER.info("Removed Stargate " + stargateID);
	}
	
	public CompoundTag getSolarSystems()
	{
		return this.stargateNetwork.copy().getCompound(SOLAR_SYSTEMS);
	}
	
	public CompoundTag getSolarSystem(String systemID)
	{
		return getSolarSystems().getCompound(systemID);
	}
	
	public CompoundTag getStargates()
	{
		return this.stargateNetwork.copy().getCompound(STARGATES);
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
		AbstractStargateEntity dialingStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALING_STARGATE));
		AbstractStargateEntity dialedStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALED_STARGATE));
		
		if(dialingStargate == null || dialedStargate == null)
		{
			terminateConnection(server, uuid);
			return;
		}
		
		dialingStargate.wormhole();
		dialedStargate.wormhole();
		
		//TODO Add this back in:
		//AbstractStargateEntity.depleteEnergy(dialingStargate, dialedStargate);
	}
	
	public CompoundTag getConnections()
	{
		return this.stargateNetwork.copy().getCompound(CONNECTIONS);
	}
	
	public CompoundTag getConnection(String uuid)
	{
		return getConnections().getCompound(uuid);
	}
	
	public ResourceKey<Level> getDialedDimension(String uuid)
	{
		return Dialing.stringToDimension(getConnection(uuid).getCompound(DIALED_STARGATE).getString(DIMENSION));
	}
	
	public AbstractStargateEntity getDialedStargate(MinecraftServer server, String uuid)
	{
		return loadStargate(server, getConnection(uuid).getCompound(DIALED_STARGATE));
	}
	
	public AbstractStargateEntity getDialingStargate(MinecraftServer server, String uuid)
	{
		return loadStargate(server, getConnection(uuid).getCompound(DIALING_STARGATE));
	}
	
	public String startConnection(AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		if(dialingStargate.equals(dialedStargate))
		{
			StargateJourney.LOGGER.info("Stargate cannot create connection with itself");
			dialingStargate.resetStargate(isDirty());
			return INVALID_SELF;
		}

		String uuid = UUID.randomUUID().toString();
		CompoundTag connections = getConnections();
		CompoundTag connection = new CompoundTag();
		CompoundTag dialingStargateInfo = saveStargate(dialingStargate);
		CompoundTag dialedStargateInfo = saveStargate(dialedStargate);
		
		connection.put(DIALING_STARGATE, dialingStargateInfo);
		connection.put(DIALED_STARGATE, dialedStargateInfo);
		connections.put(uuid, connection);
		
		this.stargateNetwork.put(CONNECTIONS, connections);
		this.setDirty();
		StargateJourney.LOGGER.info("Created connection " + uuid);
		
		dialingStargate.connectStargate(uuid, true, dialingStargate.getAddress());
		dialedStargate.connectStargate(uuid, false, dialingStargate.getAddress());
		
		return uuid;
	}
	
	private CompoundTag saveStargate(AbstractStargateEntity stargate)
	{
		CompoundTag stargateInfo = new CompoundTag();
		
		//stargateInfo.putString(ADDRESS, stargate.getID());
		stargateInfo.putString(DIMENSION, stargate.getLevel().dimension().location().toString());
		stargateInfo.putIntArray(COORDINATES, new int[] {stargate.getBlockPos().getX(), stargate.getBlockPos().getY(), stargate.getBlockPos().getZ()});
		
		return stargateInfo;
	}
	
	private AbstractStargateEntity loadStargate(MinecraftServer server, CompoundTag stargateInfo)
	{
		ResourceKey<Level> dimension = Dialing.stringToDimension(stargateInfo.getString(DIMENSION));
		BlockPos pos = Dialing.intArrayToBlockPos(stargateInfo.getIntArray(COORDINATES));
		
		BlockEntity blockEntity = server.getLevel(dimension).getBlockEntity(pos);
		
		if(blockEntity instanceof AbstractStargateEntity stargate)
			return stargate;
		
		return null;
	}
	
	public void terminateConnection(MinecraftServer server, String uuid)
	{
		if(!getConnections().contains(uuid))
		{
			StargateJourney.LOGGER.info("Could not find connection " + uuid);
			return;
		}
		
		AbstractStargateEntity dialingStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALING_STARGATE));
		AbstractStargateEntity dialedStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALED_STARGATE));
		
		if(dialingStargate != null)
			dialingStargate.resetStargate(false);
		if(dialedStargate != null)
			dialedStargate.resetStargate(false);
		
		this.stargateNetwork.getCompound(CONNECTIONS).remove(uuid);
		this.setDirty();
		StargateJourney.LOGGER.info("Ended connection " + uuid);
	}
	
	public void rerouteConnection(MinecraftServer server, String uuid, AbstractStargateEntity newDialedStargate)
	{
		AbstractStargateEntity dialingStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALING_STARGATE));
		AbstractStargateEntity dialedStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALED_STARGATE));
		
		dialedStargate.resetStargate(false);
		newDialedStargate.connectStargate(uuid, false, dialingStargate.getAddress());
		
		this.setDirty();
		StargateJourney.LOGGER.info("Rerouted connection " + uuid + " to " + newDialedStargate.getID());
	}
	
	//============================================================================================
	//******************************************Utility*******************************************
	//============================================================================================
	
	public ResourceKey<Level> stringToDimension(String dimensionString)
	{
		String[] split = dimensionString.split(":");
		return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation(split[0], split[1]));
	}
	
	public ResourceKey<Level> localAddressToDimension(String address)
	{
		return stringToDimension(getStargates().getString(address));
	}
	
	//============================================================================================
	//**************************************Stargate Network**************************************
	//============================================================================================
	
	public static StargateNetwork create()
	{
		return new StargateNetwork();
	}
	
	public static StargateNetwork load(CompoundTag tag)
	{
		StargateNetwork data = create();

		data.stargateNetwork = tag.copy();
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = this.stargateNetwork.copy();
		
		return tag;
	}

    @Nonnull
	public static StargateNetwork get(Level level)
    {
        if (level.isClientSide)
            throw new RuntimeException("Don't access this client-side!");
    	
    	return StargateNetwork.get(level.getServer());
    }

    @Nonnull
	public static StargateNetwork get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(StargateNetwork::load, StargateNetwork::create, FILE_NAME);
    }
}
