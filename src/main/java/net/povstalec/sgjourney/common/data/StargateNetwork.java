package net.povstalec.sgjourney.common.data;

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
import net.povstalec.sgjourney.common.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.stargate.Dialing;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class StargateNetwork extends SavedData
{
	private static int maxOpenTime = CommonStargateConfig.max_wormhole_open_time.get() * 20;
	private static boolean energyBypassEnabled = CommonStargateConfig.enable_energy_bypass.get();
	private static int energyBypassMultiplier = CommonStargateConfig.energy_bypass_multiplier.get();

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
	private static final String DIALING_STARGATE = "DialingStargate";
	private static final String DIALED_STARGATE = "DialedStargate";
	
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
		getConnections().getAllKeys().forEach(connection -> terminateConnection(server, connection, Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK));
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
			terminateConnection(server, uuid, Stargate.Feedback.EXCEEDED_CONNECTION_TIME);
			return;
		}
		
		AbstractStargateEntity dialingStargate = getDialingStargate(server, uuid);
		AbstractStargateEntity dialedStargate = getDialedStargate(server, uuid);
		
		if(dialingStargate == null || dialedStargate == null)
		{
			terminateConnection(server, uuid,  Stargate.Feedback.TARGET_STARGATE_DOES_NOT_EXIST);
			return;
		}
		
		if(requireEnergy)
		{
			long energyDraw = drawEnergy(uuid, getOpenTime(uuid) >= maxOpenTime);
			
			if(dialingStargate.getEnergyStored() < energyDraw && dialedStargate.getEnergyStored() < energyDraw)
			{
				terminateConnection(server, uuid, Stargate.Feedback.RAN_OUT_OF_POWER);
				return;
			}
			
			if(dialedStargate.getEnergyStored() > dialingStargate.getEnergyStored())
				dialedStargate.depleteEnergy(energyDraw, false);
			else
				dialingStargate.depleteEnergy(energyDraw, false);
		}
		
		dialingStargate.wormhole(dialedStargate, Stargate.WormholeTravel.ENABLED);
		dialedStargate.wormhole(dialingStargate, CommonStargateConfig.two_way_wormholes.get());
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
	
	public static boolean hasEnergy(MinecraftServer server, AbstractStargateEntity dialingStargate, AbstractStargateEntity targetStargate)
	{
		return dialingStargate.getEnergyStored() >= getConnectionType(server, dialingStargate, targetStargate).getEstabilishingPowerCost();
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
		
		long energyDraw = connectionType.getPowerDraw();

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
	
	public void terminateConnection(MinecraftServer server, String uuid, Stargate.Feedback feedback)
	{
		if(!getConnections().contains(uuid))
		{
			StargateJourney.LOGGER.info("Could not find connection " + uuid);
			return;
		}
		
		AbstractStargateEntity dialingStargate = getDialingStargate(server, uuid);
		AbstractStargateEntity dialedStargate = getDialedStargate(server, uuid);
		
		if(dialingStargate != null)
			dialingStargate.resetStargate(feedback);
		if(dialedStargate != null)
			dialedStargate.resetStargate(feedback);
		
		this.stargateNetwork.getCompound(CONNECTIONS).remove(uuid);
		this.setDirty();
		StargateJourney.LOGGER.info("Ended connection " + uuid);
	}
	
	public void rerouteConnection(MinecraftServer server, String uuid, AbstractStargateEntity newDialedStargate)
	{
		AbstractStargateEntity dialingStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALING_STARGATE));
		AbstractStargateEntity dialedStargate = loadStargate(server, getConnections().getCompound(uuid).getCompound(DIALED_STARGATE));
		
		dialedStargate.resetStargate(Stargate.Feedback.CONNECTION_REROUTED);
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
				for(int j = 0; j< dialedGalaxyCandidates.size(); j++)
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
