package net.povstalec.sgjourney.data;

import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.povstalec.sgjourney.config.ServerStargateConfig;
import net.povstalec.sgjourney.config.StargateJourneyConfig;
import net.povstalec.sgjourney.stargate.Dialing;

public class StargateNetwork extends SavedData
{
	private static final long systemWideConnectionCost = ServerStargateConfig.system_wide_connection_energy_cost.get();
	private static final long interstellarConnectionCost = ServerStargateConfig.interstellar_connection_energy_cost.get();
	private static final long intergalacticConnectionCost = ServerStargateConfig.intergalactic_connection_energy_cost.get();

	private static final long systemWideConnectionDraw = ServerStargateConfig.system_wide_connection_energy_draw.get();
	private static final long interstellarConnectionDraw = ServerStargateConfig.interstellar_connection_energy_draw.get();
	private static final long intergalacticConnectionDraw = ServerStargateConfig.intergalactic_connection_energy_draw.get();
	
	private static final int maxOpenTime = ServerStargateConfig.max_wormhole_open_time.get() * 20;
	private static final boolean energyBypassEnabled = ServerStargateConfig.enable_energy_bypass.get();
	private static final int energyBypassMultiplier = ServerStargateConfig.energy_bypass_multiplier.get();

	private static final boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
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
	private static final String DIALING_STARGATE = "DialingStargate";
	private static final String DIALED_STARGATE = "DialedStargate";
	
	private static final String INVALID_SELF = StargateJourney.MODID + ":invalid_self";
	private static final String CONNECTION_TIME = "ConnectionTime";
	private static final String ENERGY_DRAW = "EnergyDraw";
	
	private static final String EMPTY = StargateJourney.EMPTY;
	
	private CompoundTag stargateNetwork = new CompoundTag();
	private int version = 3;
	
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
				if(!stargateID.equals(stargate.getID()))
					removeStargate(server.getLevel(dimension), stargateID);
				
				stargate.resetStargate(false);
				
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
			System.out.println(stargateID + " Has DHD: " + hasDHD + " Gen: " + generation + " Times Opened: " + timesOpened);
			
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

		System.out.println("Chose: " + preferredStargate + " Has DHD: " + bestDHD + " Gen: " + bestGen + " Times Opened: " + bestTimesOpened);
		return preferredStargate;
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public void handleConnections(MinecraftServer server)
	{
		if(!getConnections().isEmpty())
			getConnections().getAllKeys().forEach(uuid -> handleConnection(server, uuid));
		this.setDirty();
	}
	
	private void handleConnection(MinecraftServer server, String uuid)
	{
		increaseOpenTime(uuid);
		
		if(getOpenTime(uuid) >= maxOpenTime && !energyBypassEnabled)
		{
			StargateJourney.LOGGER.info("Exceeded max connection time");
			terminateConnection(server, uuid);
			return;
		}
		
		AbstractStargateEntity dialingStargate = getDialingStargate(server, uuid);
		AbstractStargateEntity dialedStargate = getDialedStargate(server, uuid);
		
		if(dialingStargate == null || dialedStargate == null)
		{
			terminateConnection(server, uuid);
			return;
		}
		
		if(requireEnergy)
		{
			long energyDraw = drawEnergy(uuid, getOpenTime(uuid) >= maxOpenTime);
			
			if(dialingStargate.getEnergyStored() < energyDraw && dialedStargate.getEnergyStored() < energyDraw)
			{
				StargateJourney.LOGGER.info("Stargates ran out of power");
				terminateConnection(server, uuid);
			}
			
			if(dialedStargate.getEnergyStored() > dialingStargate.getEnergyStored())
				dialedStargate.depleteEnergy(energyDraw, false);
			else
				dialingStargate.depleteEnergy(energyDraw, false);
		}
		
		dialingStargate.wormhole();
		dialedStargate.wormhole();
	}
	
	private long drawEnergy(String uuid, boolean exceededConnectionTime)
	{
		CompoundTag connection = getConnection(uuid);
		
		long energyDraw = connection.getLong(ENERGY_DRAW);
		
		energyDraw = exceededConnectionTime ? energyDraw * energyBypassMultiplier : energyDraw;
		
		return energyDraw;
	}
	
	public int getOpenTime(String uuid)
	{
		if(!getConnections().contains(uuid))
			return 0;
		
		return getConnection(uuid).getInt(CONNECTION_TIME);
	}
	
	private void increaseOpenTime(String uuid)
	{
		CompoundTag connections = getConnections();
		CompoundTag connection = getConnection(uuid);
		int openTime = getOpenTime(uuid);
		openTime++;
		
		connection.putInt(CONNECTION_TIME, openTime);
		connections.put(uuid, connection);
		this.stargateNetwork.put(CONNECTIONS, connections);
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
	
	public void requestConnection(Level level, AbstractStargateEntity dialingStargate, int[] address)
	{
		AbstractStargateEntity targetStargate = Dialing.dialStargate(level, address);
		
		if(requireEnergy && !dialingStargate.hasEnergy(targetStargate))
		{
			StargateJourney.LOGGER.info("Stargate does not have enough power to estabilish a stable connection");
			dialingStargate.resetStargate(true);
			return;
		}
		
		if(targetStargate != null)
		{
			if(targetStargate.isConnected())
			{
				StargateJourney.LOGGER.info("Stargate is already connected");
				dialingStargate.resetStargate(true);
				return;
			}
			
			createConnection(level.getServer(), dialingStargate, targetStargate);
		}
		else
			dialingStargate.resetStargate(true);
	}
	
	public static boolean hasEnergy(MinecraftServer server, AbstractStargateEntity dialingStargate, AbstractStargateEntity targetStargate)
	{
		return dialingStargate.getEnergyStored() >= getConnectionCost(server, dialingStargate, targetStargate, false);
	}
	
	public String createConnection(MinecraftServer server, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		if(requireEnergy)
			dialingStargate.depleteEnergy(getConnectionCost(server, dialingStargate, dialedStargate, false), false);
		
		if(dialingStargate.equals(dialedStargate))
		{
			StargateJourney.LOGGER.info("Stargate cannot create connection with itself");
			dialingStargate.resetStargate(true);
			return INVALID_SELF;
		}
		
		long energyDraw = getConnectionCost(server, dialingStargate, dialedStargate, true);

		String uuid = UUID.randomUUID().toString();
		CompoundTag connections = getConnections();
		CompoundTag connection = new CompoundTag();
		CompoundTag dialingStargateInfo = saveStargate(dialingStargate);
		CompoundTag dialedStargateInfo = saveStargate(dialedStargate);
		
		connection.put(DIALING_STARGATE, dialingStargateInfo);
		connection.put(DIALED_STARGATE, dialedStargateInfo);
		connection.putInt(CONNECTION_TIME, 0);
		connection.putLong(ENERGY_DRAW, energyDraw);
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
		
		AbstractStargateEntity dialingStargate = getDialingStargate(server, uuid);
		AbstractStargateEntity dialedStargate = getDialedStargate(server, uuid);
		
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
	
	public static long getConnectionCost(MinecraftServer server, AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate, boolean draw)
	{
		String dialingSystem = Universe.get(server).getSolarSystemFromDimension(dialingStargate.getLevel().dimension().location().toString());
		String dialedSystem = Universe.get(server).getSolarSystemFromDimension(dialedStargate.getLevel().dimension().location().toString());
		
		if(dialingSystem.equals(dialedSystem))
		{
			if(draw)
				return systemWideConnectionDraw;
			else
				return systemWideConnectionCost;
		}
		
		ListTag dialingGalaxyCandidates = Universe.get(server).getGalaxiesFromSolarSystem(dialingSystem);
		ListTag dialedGalaxyCandidates = Universe.get(server).getGalaxiesFromSolarSystem(dialedSystem);
		
		if(!dialingGalaxyCandidates.isEmpty())
		{
			for(int i = 0; i < dialingGalaxyCandidates.size(); i++)
			{
				for(int j = 0; j< dialedGalaxyCandidates.size(); j++)
				{
					String dialingEntry = dialingGalaxyCandidates.getCompound(i).getAllKeys().iterator().next();
					String dialedEntry = dialedGalaxyCandidates.getCompound(j).getAllKeys().iterator().next();
					if(dialingEntry.equals(dialedEntry))
					{
						if(draw)
							return interstellarConnectionDraw;
						else
							return interstellarConnectionCost;
					}
				}
			}
		}
		
		if(draw)
			return intergalacticConnectionDraw;
		else
			return intergalacticConnectionCost;
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
