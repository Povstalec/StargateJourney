package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Connection;
import net.povstalec.sgjourney.common.stargate.SolarSystem;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class StargateNetwork extends SavedData
{
	private static boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	private static final String FILE_NAME = StargateJourney.MODID + "-stargate_network";
	private static final String VERSION = "Version";
	private static final String USE_DATAPACK_ADDRESSES = "UseDatapackAddresses";

	private static final String DIMENSION = "Dimension";
	private static final String COORDINATES = "Coordinates";
	private static final String TIMES_OPENED = "TimesOpened";
	private static final String GENERATION = "Generation";
	private static final String HAS_DHD = "HasDHD";

	private static final String STARGATES = "Stargates";
	private static final String SOLAR_SYSTEMS = "SolarSystems";

	private static final String CONNECTIONS = "Connections";
	
	private static final String EMPTY = StargateJourney.EMPTY;
	
	//Should increase every time there's a significant change done to the Stargate Network or the way Stargates work
	private static final int updateVersion = 7;//TODO change to 8
	
	private MinecraftServer server;
	private Map<String, Connection> connections = new HashMap<String, Connection>();
	private CompoundTag stargateNetwork = new CompoundTag();
	private int version = updateVersion;

	private HashMap<Address, Stargate> stargates = new HashMap<Address, Stargate>();

	private HashMap<ResourceKey<Level>, Address> dimensions = new HashMap<ResourceKey<Level>, Address>();
	private HashMap<Address, SolarSystem.Serializable> solarSystems = new HashMap<Address, SolarSystem.Serializable>();
	
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
		
		stellarUpdate(server, false);
	}
	
	public void eraseNetwork()
	{
		this.stargateNetwork = new CompoundTag();
		this.setDirty();
	}
	
	public boolean shouldUseDatapackAddresses()
	{
		CompoundTag network = stargateNetwork.copy();
		
		if(network.contains(USE_DATAPACK_ADDRESSES))
			return network.getBoolean(USE_DATAPACK_ADDRESSES);
		return CommonStargateNetworkConfig.use_datapack_addresses.get();
	}
	
	public void stellarUpdate(MinecraftServer server, boolean updateInterfaces)
	{
		Iterator<Entry<String, Connection>> iterator = this.connections.entrySet().iterator();
		
		while(iterator.hasNext())
		{
			Entry<String, Connection> nextConnection = iterator.next();
			Connection connection = nextConnection.getValue();
			connection.terminate(server, Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK);
		}
		StargateJourney.LOGGER.info("Connections terminated");
		
		StargateNetworkSettings.get(server).updateSettings();
		
		Universe.get(server).eraseUniverseInfo();
		StargateJourney.LOGGER.info("Universe erased");
		Universe.get(server).generateUniverseInfo(server);
		StargateJourney.LOGGER.info("Universe regenerated");
		eraseNetwork();
		StargateJourney.LOGGER.info("Network erased");
		resetStargates(server, updateInterfaces);
		StargateJourney.LOGGER.info("Stargates reset");
		updateVersion();
		StargateJourney.LOGGER.info("Version updated");
		this.setDirty();
		StargateJourney.LOGGER.info("Changes applied");
	}
	
	//============================================================================================
	//*****************************************Stargates******************************************
	//============================================================================================
	
	private void resetStargates(MinecraftServer server, boolean updateInterfaces)
	{
		HashMap<Address, Stargate> stargates = BlockEntityList.get(server).getStargates();
		
		stargates.entrySet().stream().forEach((stargateInfo) ->
		{
			Address address = stargateInfo.getKey();
			Stargate mapStargate = stargateInfo.getValue();
			
			ResourceKey<Level> dimension = mapStargate.getDimension();
			
			BlockPos pos = mapStargate.getBlockPos();
			
			ServerLevel level = server.getLevel(dimension);
			
			if(level != null)
			{
				BlockEntity blockentity = server.getLevel(dimension).getBlockEntity(pos);
				
				if(blockentity instanceof AbstractStargateEntity stargate)
				{
					if(!address.equals(stargate.get9ChevronAddress()))
						removeStargate(server.getLevel(dimension), address);
					
					stargate.resetStargate(Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK, updateInterfaces);
					
					if(getStargate(stargate.get9ChevronAddress()).isEmpty())
					{
						addStargate(server, new Stargate(stargate));
						stargate.updateStargate(updateInterfaces);
					}
				}
				else
				{
					removeStargate(server.getLevel(dimension), address);
					BlockEntityList.get(server).removeStargate(address);
				}
			}
		});
	}
	
	public void addStargate(MinecraftServer server, Stargate stargate)
	{
		//TODO Add other info, like Generation
		this.stargates.put(stargate.getAddress(), stargate);
		System.out.println("Size: " + stargates.size());
		
		/*CompoundTag stargates = getStargates();
		CompoundTag stargateTag = new CompoundTag();

		stargateTag.putString(DIMENSION, dimensionString);
		stargateTag.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		stargateTag.putInt(GENERATION, stargate.getGeneration().getGen());*/
		
		
		
		//Adds the Stargate reference to a Solar System (Will only work if the Dimension is inside a Solar System)
		/*if(Universe.get(server).getDimensions().contains(dimensionString))
		{
			String systemID = Universe.get(server).getSolarSystemFromDimension(dimensionString);
			CompoundTag solarSystems = getSolarSystems();
			CompoundTag solarSystem = getSolarSystem(systemID);
			
			solarSystem.put(stargate.getID(), stargateTag);
			solarSystems.put(systemID, solarSystem);
			this.stargateNetwork.put(SOLAR_SYSTEMS, solarSystems);
		}*/
		
		//Adds the Stargate reference to the list of Stargates
		//stargates.put(stargate.getID(), stargateTag);
		//this.stargateNetwork.put(STARGATES, stargates);
		this.setDirty();
		StargateJourney.LOGGER.info("Added Stargate " + stargate.getAddress().toString());
	}
	
	public void removeStargate(Level level, Address address)
	{
		/*String dimension = level.dimension().location().toString();
		
		//Removes the Stargate reference from the Solar System
		this.stargateNetwork.getCompound(SOLAR_SYSTEMS).getCompound(Universe.get(level).getSolarSystemFromDimension(dimension)).remove(stargateID);
		
		//Removes the Stargate reference from the list of Stargates
		this.stargateNetwork.getCompound(STARGATES).remove(stargateID);*/
		if(!this.stargates.containsKey(address))
		{
			StargateJourney.LOGGER.error("Failed to remove " + address.toString() + " from Stargate Network - key not found");
			return;
		}
		
		this.stargates.remove(address);
		
		StargateJourney.LOGGER.info("Removed " + address.toString() + " from Stargate Network");
		setDirty();
	}
	
	public void updateStargate(ServerLevel level, AbstractStargateEntity stargate)
	{
		String stargateID = stargate.getID();
		int timesOpened = stargate.getTimesOpened();
		boolean hasDHD = stargate.hasDHD();
		
		CompoundTag solarSystems = getSolarSystems();
		String systemID = Universe.get(level).getSolarSystemFromDimension(level.dimension().location().toString());
		CompoundTag solarSystem = getSolarSystem(systemID);
		
		if(systemID.equals(EMPTY) || !solarSystem.contains(stargateID))
			return;
		
		CompoundTag stargateTag = solarSystem.getCompound(stargateID);
		
		stargateTag.putInt(TIMES_OPENED, timesOpened);
		stargateTag.putBoolean(HAS_DHD, hasDHD);
		
		solarSystem.put(stargateID, stargateTag);
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
	
	@SuppressWarnings("unchecked")
	public HashMap<Address, Stargate> getStargates()
	{
		return (HashMap<Address, Stargate>) this.stargates.clone();
	}
	
	public Optional<Stargate> getStargate(Address address)
	{
		if(address.getLength() == 8)
		{
			Stargate stargate = stargates.get(address);
			
			if(stargate != null)
				return Optional.of(stargate);
		}
		
		return Optional.empty();
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
			
			if(!CommonStargateNetworkConfig.disable_dhd_preference.get() && Boolean.compare(hasDHD, bestDHD) > 0)
			{
				preferredStargate = stargateID;
				bestDHD = hasDHD;
				bestGen = generation;
				bestTimesOpened = timesOpened;
			}
			else if(CommonStargateNetworkConfig.disable_dhd_preference.get() || Boolean.compare(hasDHD, bestDHD) == 0)
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
			return connections.get(uuid).getConnectionTime();
		return 0;
	}
	
	public int getTimeSinceLastTraveler(String uuid)
	{
		if(this.connections.containsKey(uuid))
			return connections.get(uuid).getTimeSinceLastTraveler();
		return 0;
	}
	
	public Stargate.Feedback createConnection(MinecraftServer server, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		Connection.Type connectionType = Connection.getType(server, dialingStargate, dialedStargate);
		
		if(!CommonStargateConfig.allow_interstellar_8_chevron_addresses.get() &&
				addressType == Address.Type.ADDRESS_8_CHEVRON &&
				connectionType == Connection.Type.INTERSTELLAR)
			return dialingStargate.resetStargate(Stargate.Feedback.INVALID_8_CHEVRON_ADDRESS, true);
		
		if(!CommonStargateConfig.allow_system_wide_connections.get() && connectionType == Connection.Type.SYSTEM_WIDE)
			return dialingStargate.resetStargate(Stargate.Feedback.INVALID_SYSTEM_WIDE_CONNECTION, true);
		
		if(dialingStargate.equals(dialedStargate))
			return dialingStargate.resetStargate(Stargate.Feedback.SELF_DIAL, true);
		
		if(requireEnergy)
		{
			if(dialingStargate.canExtractEnergy(connectionType.getEstablishingPowerCost()))
				dialingStargate.depleteEnergy(connectionType.getEstablishingPowerCost(), false);
			else
				return dialingStargate.resetStargate(Stargate.Feedback.NOT_ENOUGH_POWER, true);
		}
		
		if(dialedStargate.isConnected())
			return dialingStargate.resetStargate(Stargate.Feedback.ALREADY_CONNECTED, true);
		else if(dialedStargate.isObstructed())
			return dialingStargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED, true);
		
		Connection connection = Connection.create(connectionType, dialingStargate, dialedStargate, doKawoosh);
		if(connection != null)
		{
			this.connections.put(connection.getID(), connection);
			
			switch(connectionType)
			{
			case SYSTEM_WIDE:
				return Stargate.Feedback.CONNECTION_ESTABLISHED_SYSTEM_WIDE;
			case INTERSTELLAR:
				return Stargate.Feedback.CONNECTION_ESTABLISHED_INTERSTELLAR;
			default:
				return Stargate.Feedback.CONNECTION_ESTABLISHED_INTERGALACTIC;
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
			StargateJourney.LOGGER.error("Could not find connection " + uuid);
		this.setDirty();
	}
	
	// TODO make this work
	/*public void rerouteConnection(MinecraftServer server, String uuid, AbstractStargateEntity newDialedStargate)
	{
		if(this.connections.containsKey(uuid))
		{
			this.connections.get(uuid).reroute(newDialedStargate);
			StargateJourney.LOGGER.info("Rerouted connection " + uuid + " to " + newDialedStargate.getID());
		}
		else
			StargateJourney.LOGGER.info("Could not find connection " + uuid);
		this.setDirty();
	}*/
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(VERSION, this.version);
		tag.put(CONNECTIONS, serializeConnections());
		tag.put(STARGATES, serializeStargates());
		
		return tag;
	}
	
	private CompoundTag serializeStargates()
	{
		CompoundTag stargatesTag = new CompoundTag();
		
		this.stargates.forEach((address, stargate) ->
		{
			stargatesTag.put(address.toString(), stargate.serialize());
		});
		
		return stargatesTag;
	}
	
	private CompoundTag serializeConnections()
	{
		CompoundTag connectionsTag = new CompoundTag();
		
		this.connections.forEach((connectionID, connection) ->
		{
			connectionsTag.put(connectionID, connection.serialize());
		});
		
		return connectionsTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		this.version = tag.getInt(VERSION);
		deserializeConnections(tag.getCompound(CONNECTIONS));
		deserializeStargates(tag.getCompound(STARGATES));
	}
	
	private void deserializeConnections(CompoundTag tag)
	{
		tag.getAllKeys().forEach(connectionID ->
		{
			this.connections.put(connectionID, Connection.deserialize(server, connectionID, tag.getCompound(connectionID)));
		});
	}
	
	private void deserializeStargates(CompoundTag tag)
	{
		tag.getAllKeys().forEach(stargateID ->
		{
			this.stargates.put(new Address(stargateID), Stargate.deserialize(tag.getCompound(stargateID), new Address(stargateID)));
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
		//data.stargateNetwork = tag.copy();
		//data.deserializeConnections(tag.getCompound(CONNECTIONS));
		data.deserialize(tag);
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		//this.stargateNetwork.put(CONNECTIONS, serializeConnections());
		tag = serialize();
		
		//tag = this.stargateNetwork.copy();
		
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
