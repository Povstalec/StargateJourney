package net.povstalec.sgjourney.common.data;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public final class StargateNetwork extends SavedData
{
	private static boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	private static final String FILE_NAME = StargateJourney.MODID + "-stargate_network";
	private static final String VERSION = "version";

	private static final String CONNECTIONS = "connections";

	// Should increase every time there's a significant change done to the Stargate Network or the way Stargates work
	private static final int updateVersion = 17;
	
	private MinecraftServer server;
	
	private HashMap<UUID, StargateConnection> connections = new HashMap<UUID, StargateConnection>();
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
			StargateJourney.LOGGER.debug("Stargate Network is up to date (Version: " + version + ")");
			return;
		}
		
		StargateJourney.LOGGER.debug("Detected an incompatible Stargate Network version (Version: " + getVersion() + ") - updating to version " + updateVersion);
		
		stellarUpdate(server, false);
	}
	
	public final void eraseNetwork()
	{
		this.connections.clear();
		
		this.setDirty();
	}
	
	public final boolean shouldUseDatapackAddresses()
	{
		return StargateNetworkSettings.get(server).useDatapackAddresses();
	}
	
	public final void stellarUpdate(MinecraftServer server, boolean updateInterfaces)
	{
		Iterator<Entry<UUID, StargateConnection>> iterator = this.connections.entrySet().iterator();
		
		while(iterator.hasNext())
		{
			Entry<UUID, StargateConnection> nextConnection = iterator.next();
			StargateConnection connection = nextConnection.getValue();
			connection.terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_NETWORK);
		}
		StargateJourney.LOGGER.debug("Connections terminated");
		
		StargateNetworkSettings.get(server).updateSettings();
		
		HashMap<ResourceLocation, Address.Immutable> primaryStargates = Universe.get(server).getPrimaryStargateAddresses();
		Universe.get(server).eraseUniverseInfo();
		StargateJourney.LOGGER.debug("Universe erased");
		
		Universe.get(server).generateUniverseInfo(server);
		Universe.get(server).setPrimaryStargateAddresses(primaryStargates);
		StargateJourney.LOGGER.debug("Universe regenerated");
		
		eraseNetwork();
		StargateJourney.LOGGER.debug("Stargate Network erased");
		
		resetStargates(server, updateInterfaces);
		StargateJourney.LOGGER.debug("Stargates reset");
		
		updateVersion();
		StargateJourney.LOGGER.debug("Version updated");
		
		this.setDirty();
	}
	
	//============================================================================================
	//*****************************************Stargates******************************************
	//============================================================================================
	
	public final void addStargates(MinecraftServer server)
	{
		HashMap<Address.Immutable, Stargate> stargates = BlockEntityList.get(server).getStargates();
		
		stargates.entrySet().stream().forEach((stargateInfo) ->
		{
			Stargate stargate = stargateInfo.getValue();
			
			if(stargate != null)
				addStargate(stargate);
		});
	}
	
	private final void resetStargates(MinecraftServer server, boolean updateInterfaces)
	{
		HashMap<Address.Immutable, Stargate> stargates = BlockEntityList.get(server).getStargates();
		
		stargates.entrySet().stream().forEach((stargateInfo) ->
		{
			Address.Immutable address = stargateInfo.getKey();
			Stargate stargate = stargateInfo.getValue();
			
			if(stargate != null)
			{
				if(stargate.isValid(server))
				{
					if(!address.equals(stargate.get9ChevronAddress()))
						removeStargate(stargate);
					
					stargate.resetStargate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_NETWORK, updateInterfaces);
					
					addStargate(stargate);
					stargate.update(server);
					stargate.updateInterfaceBlocks(server, null, null);
				}
				else
					removeStargate(stargate);
			}
			else
				BlockEntityList.get(server).removeStargate(address);
		});
	}
	
	public final void addStargate(Stargate stargate)
	{
		if(stargate == null)
			return;
		
		Universe.get(server).addStargateToDimension(stargate.getDimension(), stargate);
		this.setDirty();
	}
	
	public final void addStargate(AbstractStargateEntity stargateEntity)
	{
		addStargate(BlockEntityList.get(server).addStargate(stargateEntity));
	}
	
	public final void removeStargate(Stargate stargate)
	{
		if(stargate != null)
		{
			Universe.get(server).removeStargateFromSolarSystem(stargate.getSolarSystem(server), stargate);
			BlockEntityList.get(server).removeStargate(stargate.get9ChevronAddress());
			
			StargateJourney.LOGGER.debug("Removed " + stargate.get9ChevronAddress().toString() + " from Stargate Network");
			setDirty();
		}
		else
			StargateJourney.LOGGER.error("Could not remove Stargate because it's null");
	}
	
	public final void removeStargate(Address.Immutable address)
	{
		if(address == null)
			return;
		
		Stargate stargate = getStargate(address);
		removeStargate(stargate);
	}
	
	public final void updateStargate(ServerLevel level, AbstractStargateEntity stargateEntity)
	{
		Stargate stargate = getStargate(stargateEntity.get9ChevronAddress());
		
		if(stargate != null)
		{
			Universe.get(server).removeStargateFromDimension(level.dimension(), stargate);
			stargate.update(server);
			Universe.get(server).addStargateToDimension(level.dimension(), stargate);
		}
	}
	
	@Nullable
	public final Stargate getStargate(Address address)
	{
		return BlockEntityList.get(server).getStargate(address);
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public final void handleConnections()
	{
		Map<UUID, StargateConnection> connections = new HashMap<>();
		connections.putAll(this.connections);
		
		connections.forEach((uuid, connection) -> connection.tick(server));
		this.setDirty();
	}
	
	public final int getOpenTime(UUID uuid)
	{
		if(this.connections.containsKey(uuid))
			return connections.get(uuid).getOpenTime();
		return 0;
	}
	
	public final int getTimeSinceLastTraveler(UUID uuid)
	{
		if(this.connections.containsKey(uuid))
			return connections.get(uuid).getTimeSinceLastTraveler();
		return 0;
	}
	
	public final StargateInfo.Feedback createConnection(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		StargateConnection.Type connectionType = StargateConnection.getType(server, dialingStargate, dialedStargate);
		
		// Event for Stargate connecting, can be cancelled - !!!NOTE That it does NOT reset the Stargate or actually change its feedback when cancelled!!!
		if(SGJourneyEvents.onStargateConnect(server, dialingStargate, dialedStargate, connectionType, addressType, doKawoosh))
			return StargateInfo.Feedback.NONE;
		
		// Will reset the Stargate if something's wrong
		if(!dialedStargate.isValid(server))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE, true);
		
		if(!CommonStargateConfig.allow_interstellar_8_chevron_addresses.get() &&
				addressType == Address.Type.ADDRESS_8_CHEVRON &&
				connectionType == StargateConnection.Type.INTERSTELLAR)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_8_CHEVRON_ADDRESS, true);
		
		if(!CommonStargateConfig.allow_system_wide_connections.get() && connectionType == StargateConnection.Type.SYSTEM_WIDE)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_SYSTEM_WIDE_CONNECTION, true);
		
		if(dialingStargate.equals(dialedStargate))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.SELF_DIAL, true);

		if(dialedStargate.isConnected(server))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.ALREADY_CONNECTED, true);
		else if(dialedStargate.isObstructed(server))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.TARGET_OBSTRUCTED, true);
		
		if(requireEnergy)
		{
			if(StargateConnection.canExtract(server, dialingStargate, connectionType.getEstablishingPowerCost()))
				dialingStargate.extractEnergy(server, connectionType.getEstablishingPowerCost(), false);
			else
				return dialingStargate.resetStargate(server, StargateInfo.Feedback.NOT_ENOUGH_POWER, true);
		}
		
		//TODO Make better call forwarding
		/*AbstractStargateEntity outgoingStargate = dialingStargate.getStargateEntity(server);
		AbstractStargateEntity incomingStargate = dialedStargate.getStargateEntity(server);
		
		if(outgoingStargate != null && incomingStargate != null)
		{
			// Call Forwarding
			if(incomingStargate.dhdInfo().shouldCallForward())
			{
				// Chooses a random Stargate to connect to
				Random random = new Random();
				
				Optional<SolarSystem.Serializable> solarSystemOptional = Universe.get(server).getSolarSystemFromDimension(dialedStargate.getDimension());
				
				if(solarSystemOptional.isPresent())
				{
					SolarSystem.Serializable solarSystem = solarSystemOptional.get();
					
					Optional<Stargate> reroutedStargate = solarSystem.getRandomStargate(random.nextLong());
					
					if(reroutedStargate.isPresent())
					{
						while(reroutedStargate.get().getStargateEntity(server).dhdInfo().shouldCallForward())
						{
							reroutedStargate = solarSystem.getRandomStargate(random.nextLong());
						}
						
						incomingStargate = reroutedStargate.get().getStargateEntity(server);
					}
				}
			}
		}*/
		
		StargateConnection connection = StargateConnection.create(server, connectionType, dialingStargate, dialedStargate, doKawoosh);
		if(connection != null)
		{
			addConnection(connection);
			
			switch(connectionType)
			{
				case SYSTEM_WIDE:
					return StargateInfo.Feedback.CONNECTION_ESTABLISHED_SYSTEM_WIDE;
				case INTERSTELLAR:
					return StargateInfo.Feedback.CONNECTION_ESTABLISHED_INTERSTELLAR;
				default:
					return StargateInfo.Feedback.CONNECTION_ESTABLISHED_INTERGALACTIC;
			}
		}
		
		return StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE;
	}
	
	public final boolean addConnection(StargateConnection connection)
	{
		if(!this.connections.containsKey(connection.getID()))
		{
			this.connections.put(connection.getID(), connection);
			
			SGJourneyEvents.onConnectionEstablished(server, connection);
			
			return true;
		}
		
		return false;
	}
	
	public final boolean hasConnection(UUID uuid)
	{
		if(this.connections.containsKey(uuid))
			return true;
		
		return false;
	}
	
	public final void terminateConnection(UUID uuid, StargateInfo.Feedback feedback)
	{
		if(!hasConnection(uuid))
			return;
		
		this.connections.get(uuid).terminate(server, feedback);
	}
	
	public final void removeConnection(UUID uuid)
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
	
	public final void printConnections()
	{
		System.out.println("[Connections]");
		this.connections.entrySet().stream().forEach(connectionEntry ->
		{
			connectionEntry.getValue().printConnection();
		});
	}
	
	public final boolean sendStargateMessage(AbstractStargateEntity sendingStargate, UUID uuid, String messsage)
	{
		if(hasConnection(uuid))
		{
			this.connections.get(uuid).sendStargateMessage(server, sendingStargate, messsage);
			return true;
		}
		else
			return false;
	}
	
	public final void sendStargateTransmission(AbstractStargateEntity sendingStargate, UUID uuid, int transmissionJumps, int frequency, String transmission)
	{
		if(hasConnection(uuid))
			this.connections.get(uuid).sendStargateTransmission(server, sendingStargate, transmissionJumps, frequency, transmission);
	}
	
	public final float checkStargateShieldingState(AbstractStargateEntity sendingStargate, UUID uuid)
	{
		if(hasConnection(uuid))
			return this.connections.get(uuid).checkStargateShieldingState(server, sendingStargate);
		
		return 0;
	}
	
	
	
	private static boolean addStargatesFromChunk(ServerLevel level, int x, int z, List<AbstractStargateEntity> stargates)
	{
		ChunkAccess chunk = level.getChunk(x, z);
		for(BlockPos pos : chunk.getBlockEntitiesPos())
		{
			if(level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
			{
				stargates.add(stargate);
				return true;
			}
		}
		
		return false;
	}
	
	private static void searchEdges(ServerLevel level, int xCenter, int yCenter, int radius, List<AbstractStargateEntity> stargates)
	{
		if(radius == 0)
			addStargatesFromChunk(level, xCenter, yCenter, stargates);
		else
		{
			int xMin = xCenter - radius;
			int xMax = xCenter + radius;
			int yMin = yCenter - radius;
			int yMax = yCenter + radius;
			
			// Top
			for(int x = xMin; x <= xMax; x++)
			{
				if(addStargatesFromChunk(level, x, yMax, stargates))
					return;
			}
			// Right
			for(int y = yMax - 1; y >= yMin + 1; y--)
			{
				if(addStargatesFromChunk(level, xMax, y, stargates))
					return;
			}
			// Bottom
			for(int x = xMax; x >= xMin; x--)
			{
				if(addStargatesFromChunk(level, x, yMin, stargates))
					return;
			}
			// Left
			for(int y = yMin + 1; y <= yMax - 1; y++)
			{
				if(addStargatesFromChunk(level, xMin, y, stargates))
					return;
			}
		}
	}
	
	public static void findStargates(ServerLevel level)
	{
		StargateJourney.LOGGER.debug("Attempting to locate the Stargate Structure in " + level.dimension().location());
		
		int xOffset = CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
		int zOffset = CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
		// Nearest Structure that potentially has a Stargate
		BlockPos blockpos = level.findNearestMapStructure(CommonGenerationConfig.common_stargate_search.get() ? TagInit.Structures.HAS_STARGATE : TagInit.Structures.NETWORK_STARGATE,
				new BlockPos(xOffset * 16, 0, zOffset * 16), 150, false);
		if(blockpos == null)
		{
			StargateJourney.LOGGER.debug("Stargate Structure not found");
			return;
		}
		// Map of Block Entities that might contain a Stargate
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		int xCenter = SectionPos.blockToSectionCoord(blockpos.getX());
		int zCenter = SectionPos.blockToSectionCoord(blockpos.getZ());
		for(int radius = 0; radius <= 2; radius++)
		{
			searchEdges(level, xCenter, zCenter, radius, stargates);
		}
		
		if(stargates.isEmpty())
		{
			StargateJourney.LOGGER.debug("No Stargates found in Stargate Structure");
			return;
		}
		
		for(AbstractStargateEntity stargate : stargates)
		{
			stargate.onLoad();
		}
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private final CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(VERSION, this.version);
		tag.put(CONNECTIONS, serializeConnections());
		
		return tag;
	}
	
	private final CompoundTag serializeConnections()
	{
		CompoundTag connectionsTag = new CompoundTag();
		
		this.connections.forEach((connectionID, connection) ->
		{
			connectionsTag.put(connectionID.toString(), connection.serialize());
		});
		
		return connectionsTag;
	}
	
	private final void deserialize(CompoundTag tag)
	{
		this.version = tag.getInt(VERSION);
		deserializeConnections(tag.getCompound(CONNECTIONS));
	}
	
	private final void deserializeConnections(CompoundTag tag)
	{
		for(String connectionID : tag.getAllKeys())
		{
			try
			{
				UUID uuid = UUID.fromString(connectionID);
				StargateConnection connection = StargateConnection.deserialize(server, uuid, tag.getCompound(connectionID));
				
				if(connection != null)
					this.connections.put(uuid, connection);
			}
			catch(IllegalArgumentException e) {}
		}
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
		data.deserialize(tag);
		
		return data;
	}

	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
	{
		tag = serialize();
		
		return tag;
	}

	public static SavedData.Factory<StargateNetwork> dataFactory(MinecraftServer server)
	{
		return new SavedData.Factory<>(() -> create(server), (tag, provider) -> load(server, tag));
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
        
        return storage.computeIfAbsent(dataFactory(server), FILE_NAME);
    }
}
