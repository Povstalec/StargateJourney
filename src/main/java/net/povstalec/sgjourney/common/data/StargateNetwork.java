package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.stargate.Connection;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class StargateNetwork extends SavedData
{
	private static boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	private static final String FILE_NAME = "sgjourney-stargate_network";
	private static final String VERSION = "Version";

	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";
	private static final String TIMES_OPENED = "TimesOpened";
	private static final String GENERATION = "Generation";
	private static final String HAS_DHD = "HasDHD";

	private static final String STARGATES = "Stargates";
	private static final String SOLAR_SYSTEMS = "SolarSystems";

	private static final String CONNECTIONS = "Connections";
	
	private static final String EMPTY = StargateJourney.EMPTY;
	
	private static final int updateVersion = 4;
	
	private MinecraftServer server;
	private Map<String, Connection> connections = new HashMap<String, Connection>();
	private CompoundTag stargateNetwork = new CompoundTag();
	private int version = 4;
	
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
	}
	
	public void eraseNetwork()
	{
		this.stargateNetwork = new CompoundTag();
		this.setDirty();
	}
	
	public void stellarUpdate(MinecraftServer server)
	{
		//getConnections().getAllKeys().forEach(connection -> terminateConnection(server, connection, Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK));
		this.connections.forEach((connectionID, connection) -> connection.terminate(server, Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK));
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
			ResourceKey<Level> dimension = Conversion.stringToDimension(dimensionString);
			int[] coordinates = stargates.getCompound(stargateID).getIntArray(COORDINATES);
			
			BlockPos pos = new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
			BlockEntity blockentity = server.getLevel(dimension).getBlockEntity(pos);
			
			if(blockentity instanceof AbstractStargateEntity stargate)
			{
				if(!stargateID.equals(stargate.getID()))
					removeStargate(server.getLevel(dimension), stargateID);
				
				stargate.resetStargate(Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK);
				
				if(!getStargates().contains(stargateID))
				{
					addStargate(server, stargateID, BlockEntityList.get(server).getBlockEntities(SGJourneyBlockEntity.Type.STARGATE.id).getCompound(stargateID), stargate.getGeneration().getGen());
					stargate.updateStargate();
				}
			}
			else
			{
				removeStargate(server.getLevel(dimension), stargateID);
				BlockEntityList.get(server).removeBlockEntity(SGJourneyBlockEntity.Type.STARGATE.id, stargateID);
			}
		});
	}
	
	public void addStargate(MinecraftServer server, String stargateID, CompoundTag stargateInfo, int generation)
	{
		String dimension = stargateInfo.getString(DIMENSION);
		String stargateDimension = stargateInfo.getString(DIMENSION);
		int[] stargateCoordinates = stargateInfo.getIntArray(COORDINATES);
		CompoundTag stargates = getStargates();
		CompoundTag stargate = new CompoundTag();

		stargate.putString(DIMENSION, stargateDimension);
		stargate.putIntArray(COORDINATES, stargateCoordinates);
		stargate.putInt(GENERATION, generation);
		
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
	
	public void updateStargate(Level level, String stargateID, int timesOpened, boolean hasDHD)
	{
		CompoundTag solarSystems = getSolarSystems();
		String systemID = Universe.get(level).getSolarSystemFromDimension(level.dimension().location().toString());
		CompoundTag solarSystem = getSolarSystem(systemID);
		
		if(systemID.equals(EMPTY) || !solarSystem.contains(stargateID))
			return;
		
		CompoundTag stargate = solarSystem.getCompound(stargateID);
		
		stargate.putInt(TIMES_OPENED, timesOpened);
		stargate.putBoolean(HAS_DHD, hasDHD);
		
		solarSystem.put(stargateID, stargate);
		solarSystems.put(systemID, solarSystem);
		this.stargateNetwork.put(SOLAR_SYSTEMS, solarSystems);
		this.setDirty();
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
	
	/*
	 * Stargate Preferences:
	 * 1. Has DHD
	 * 2. Stargate Generation
	 * 3. The amount of times the Stargate was used
	 */
	public String getPreferredStargate(CompoundTag solarSystem)
	{
		if(solarSystem.isEmpty())
			return EMPTY;
		
		Iterator<String> iterator = solarSystem.getAllKeys().iterator();
		
		boolean bestDHD = false;
		int bestGen = 0;
		int bestTimesOpened = 0;
		String preferredStargate = EMPTY;
		
		while(iterator.hasNext())
		{
			String stargateID = iterator.next();
			boolean hasDHD = solarSystem.getCompound(stargateID).getBoolean(HAS_DHD);
			int generation = solarSystem.getCompound(stargateID).getInt(GENERATION);
			int timesOpened = solarSystem.getCompound(stargateID).getInt(TIMES_OPENED);
			//System.out.println(stargateID + " Has DHD: " + hasDHD + " Gen: " + generation + " Times Opened: " + timesOpened);
			
			if(Boolean.compare(hasDHD, bestDHD) > 0)
			{
				preferredStargate = stargateID;
				bestDHD = hasDHD;
				bestGen = generation;
				bestTimesOpened = timesOpened;
			}
			else if(Boolean.compare(hasDHD, bestDHD) == 0)
			{
				if(generation > bestGen)
				{
					preferredStargate = stargateID;
					bestDHD = hasDHD;
					bestGen = generation;
					bestTimesOpened = timesOpened;
				}
				else if(generation == bestGen)
				{
					if(timesOpened >= bestTimesOpened)
					{
						preferredStargate = stargateID;
						bestDHD = hasDHD;
						bestGen = generation;
						bestTimesOpened = timesOpened;
					}
				}
			}
		}

		//System.out.println("Chose: " + preferredStargate + " Has DHD: " + bestDHD + " Gen: " + bestGen + " Times Opened: " + bestTimesOpened);
		return preferredStargate;
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public void handleConnections()
	{
		Map<String, Connection> connections = new HashMap<>();
		connections.putAll(this.connections);
		
		connections.forEach((connectionID, connection) -> connection.tick(server));
		this.setDirty();
	}
	
	public int getOpenTime(String uuid)
	{
		if(this.connections.containsKey(uuid))
			return connections.get(uuid).getOpenTime();
		return 0;
	}
	
	public int getTimeSinceLastTraveler(String uuid)
	{
		if(this.connections.containsKey(uuid))
			return connections.get(uuid).getTimeSinceLastTraveler();
		return 0;
	}
	
	public Stargate.Feedback createConnection(MinecraftServer server, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		Stargate.ConnectionType connectionType = getConnectionType(server, dialingStargate, dialedStargate);
		
		if(dialingStargate.equals(dialedStargate))
			return dialingStargate.resetStargate(Stargate.Feedback.SELF_DIAL);
		
		if(requireEnergy)
		{
			if(dialingStargate.hasEnergy(dialedStargate))
				dialingStargate.depleteEnergy(connectionType.getEstabilishingPowerCost(), false);
			else
				return dialingStargate.resetStargate(Stargate.Feedback.NOT_ENOUGH_POWER);
		}
		
		if(dialedStargate.isConnected())
			return dialingStargate.resetStargate(Stargate.Feedback.ALREADY_CONNECTED);
		else if(dialedStargate.isObstructed())
			return dialingStargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED);
		
		Connection connection = Connection.create(connectionType, dialingStargate, dialedStargate);
		if(connection != null)
		{
			this.connections.put(connection.getID(), connection);
			
			switch(connectionType)
			{
			case SYSTEM_WIDE:
				return Stargate.Feedback.CONNECTION_ESTABILISHED_SYSTEM_WIDE;
			case INTERSTELLAR:
				return Stargate.Feedback.CONNECTION_ESTABILISHED_INTERSTELLAR;
			default:
				return Stargate.Feedback.CONNECTION_ESTABILISHED_INTERGALACTIC;
			}
		}
		return Stargate.Feedback.UNKNOWN_ERROR;
	}
	
	public void terminateConnection(MinecraftServer server, String uuid, Stargate.Feedback feedback)
	{
		if(this.connections.containsKey(uuid))
			this.connections.get(uuid).terminate(server, feedback);
	}
	
	public void removeConnection(MinecraftServer server, String uuid, Stargate.Feedback feedback)
	{
		if(this.connections.containsKey(uuid))
		{
			this.connections.remove(uuid);
			StargateJourney.LOGGER.info("Removed connection " + uuid);
		}
		else
			StargateJourney.LOGGER.info("Could not find connection " + uuid);
		this.setDirty();
	}
	
	public void rerouteConnection(MinecraftServer server, String uuid, AbstractStargateEntity newDialedStargate)
	{
		if(this.connections.containsKey(uuid))
		{
			this.connections.get(uuid).reroute(newDialedStargate);
			StargateJourney.LOGGER.info("Rerouted connection " + uuid + " to " + newDialedStargate.getID());
		}
		else
			StargateJourney.LOGGER.info("Could not find connection " + uuid);
		this.setDirty();
	}
	
	//============================================================================================
	//******************************************Utility*******************************************
	//============================================================================================
	
	public static Stargate.ConnectionType getConnectionType(MinecraftServer server, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		String dialingSystem = Universe.get(server).getSolarSystemFromDimension(dialingStargate.getLevel().dimension().location().toString());
		String dialedSystem = Universe.get(server).getSolarSystemFromDimension(dialedStargate.getLevel().dimension().location().toString());
		
		if(dialingSystem.equals(dialedSystem))
			return Stargate.ConnectionType.SYSTEM_WIDE;
		
		ListTag dialingGalaxyCandidates = Universe.get(server).getGalaxiesFromSolarSystem(dialingSystem);
		ListTag dialedGalaxyCandidates = Universe.get(server).getGalaxiesFromSolarSystem(dialedSystem);
		
		if(!dialingGalaxyCandidates.isEmpty())
		{
			for(int i = 0; i < dialingGalaxyCandidates.size(); i++)
			{
				for(int j = 0; j < dialedGalaxyCandidates.size(); j++)
				{
					String dialingGalaxy = dialingGalaxyCandidates.getCompound(i).getAllKeys().iterator().next();
					String dialedGalaxy = dialedGalaxyCandidates.getCompound(j).getAllKeys().iterator().next();
					if(dialingGalaxy.equals(dialedGalaxy))
						return Stargate.ConnectionType.INTERSTELLAR;
				}
			}
		}
		
		return Stargate.ConnectionType.INTERGALACTIC;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	protected CompoundTag serialize() //TODO Use this
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(VERSION, this.version);
		tag.put(CONNECTIONS, serializeConnections());
		
		return tag;
	}
	
	protected CompoundTag serializeConnections()
	{
		CompoundTag tag = new CompoundTag();
		
		this.connections.forEach((connectionID, connection) ->
		{
			tag.put(connectionID, connection.serialize());
		});
		
		return tag;
	}
	
	protected void deserialize(CompoundTag tag) //TODO Use this
	{
		this.version = tag.getInt(VERSION);
		deserializeConnections(tag);
	}
	
	protected void deserializeConnections(CompoundTag tag)
	{
		tag.getAllKeys().forEach(connectionID ->
		{
			this.connections.put(connectionID, Connection.deserialize(server, connectionID, tag.getCompound(connectionID)));
		});
	}
	
	//============================================================================================
	//**************************************Stargate Network**************************************
	//============================================================================================
	public StargateNetwork(MinecraftServer server)
	{
		this.server = server;
	}
	
	public static StargateNetwork create(MinecraftServer server)
	{
		return new StargateNetwork(server);
	}
	
	public static StargateNetwork load(MinecraftServer server, CompoundTag tag)
	{
		StargateNetwork data = create(server);
		
		data.server = server;
		data.stargateNetwork = tag.copy();
		data.deserializeConnections(tag.getCompound(CONNECTIONS));
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		this.stargateNetwork.put(CONNECTIONS, serializeConnections());
		
		tag = this.stargateNetwork.copy();
		
		return tag;
	}

    @Nonnull
	public static StargateNetwork get(Level level)
    {
        if(level.isClientSide())
            throw new RuntimeException("Don't access this client-side!");
    	
    	return StargateNetwork.get(level.getServer());
    }

    @Nonnull
	public static StargateNetwork get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
    }
}
