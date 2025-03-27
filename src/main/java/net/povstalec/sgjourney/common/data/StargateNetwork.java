package net.povstalec.sgjourney.common.data;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.SolarSystem;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateConnection;

public final class StargateNetwork extends SavedData
{
	private static boolean requireEnergy = !StargateJourneyConfig.disable_energy_use.get();
	
	private static final String FILE_NAME = StargateJourney.MODID + "-stargate_network";
	private static final String VERSION = "Version";

	private static final String CONNECTIONS = "Connections";

	//Should increase every time there's a significant change done to the Stargate Network or the way Stargates work
	private static final int updateVersion = 14;
	
	private MinecraftServer server;
	
	private Map<UUID, StargateConnection> connections = new HashMap<UUID, StargateConnection>();
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
			StargateJourney.LOGGER.info("Stargate Network is up to date (Version: " + version + ")");
			return;
		}
		
		StargateJourney.LOGGER.info("Detected an incompatible Stargate Network version (Version: " + getVersion() + ") - updating to version " + updateVersion);
		
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
			connection.terminate(server, Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK);
		}
		StargateJourney.LOGGER.info("Connections terminated");
		
		StargateNetworkSettings.get(server).updateSettings();
		
		Universe.get(server).eraseUniverseInfo();
		StargateJourney.LOGGER.info("Universe erased");
		
		Universe.get(server).generateUniverseInfo(server);
		StargateJourney.LOGGER.info("Universe regenerated");
		
		eraseNetwork();
		StargateJourney.LOGGER.info("Stargate Network erased");
		
		resetStargates(server, updateInterfaces);
		StargateJourney.LOGGER.info("Stargates reset");
		
		updateVersion();
		StargateJourney.LOGGER.info("Version updated");
		
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
			Stargate mapStargate = stargateInfo.getValue();
			
			if(mapStargate != null)
			{
				ResourceKey<Level> dimension = mapStargate.getDimension();
				
				BlockPos pos = mapStargate.getBlockPos();
				
				ServerLevel level = server.getLevel(dimension);
				
				if(level != null)
				{
					BlockEntity blockentity = server.getLevel(dimension).getBlockEntity(pos);
					
					if(blockentity instanceof AbstractStargateEntity stargate)
					{
						addStargate(stargate);
					}
				}
			}
		});
	}
	
	private final void resetStargates(MinecraftServer server, boolean updateInterfaces)
	{
		HashMap<Address.Immutable, Stargate> stargates = BlockEntityList.get(server).getStargates();
		
		stargates.entrySet().stream().forEach((stargateInfo) ->
		{
			Address.Immutable address = stargateInfo.getKey();
			Stargate mapStargate = stargateInfo.getValue();
			
			if(mapStargate != null)
			{
				ResourceKey<Level> dimension = mapStargate.getDimension();
				
				BlockPos pos = mapStargate.getBlockPos();
				
				ServerLevel level = server.getLevel(dimension);
				
				if(level != null)
				{
					BlockEntity blockentity = server.getLevel(dimension).getBlockEntity(pos);
					
					if(blockentity instanceof AbstractStargateEntity stargate)
					{
						if(!address.equals(stargate.get9ChevronAddress().immutable()))
							removeStargate(server.getLevel(dimension), address);
						
						stargate.resetStargate(Stargate.Feedback.CONNECTION_ENDED_BY_NETWORK, updateInterfaces);
						
						addStargate(stargate);
						stargate.updateStargate(updateInterfaces);//TODO Probably should look at this
					}
					else
					{
						removeStargate(server.getLevel(dimension), address);
						BlockEntityList.get(server).removeStargate(address);
					}
				}
			}
			else
				BlockEntityList.get(server).removeStargate(address);
		});
	}
	
	public final void addStargate(AbstractStargateEntity stargateEntity)
	{
		Optional<Stargate> stargateOptional = BlockEntityList.get(server).addStargate(stargateEntity);
		
		if(stargateOptional.isPresent())
		{
			Stargate stargate = stargateOptional.get();
			Universe.get(server).addStargateToDimension(stargate.getDimension(), stargate);
		}
		
		this.setDirty();
	}
	
	public final void removeStargate(Level level, Address.Immutable address)
	{
		if(address == null)
			return;
		
		Optional<Stargate> stargate = getStargate(address);
		
		if(stargate.isPresent())
			Universe.get(server).removeStargateFromDimension(level.dimension(), stargate.get());

		BlockEntityList.get(level).removeStargate(address);
		
		StargateJourney.LOGGER.info("Removed " + address.toString() + " from Stargate Network");
		setDirty();
	}
	
	public final void updateStargate(ServerLevel level, AbstractStargateEntity stargate)
	{
		Optional<Stargate> stargateOptional = getStargate(stargate.get9ChevronAddress().immutable());
		
		if(stargateOptional.isPresent())
		{
			Universe.get(server).removeStargateFromDimension(level.dimension(), stargateOptional.get());
			stargateOptional.get().update(stargate);
			Universe.get(server).addStargateToDimension(level.dimension(), stargateOptional.get());
		}
	}
	
	public final Optional<Stargate> getStargate(Address.Immutable address)
	{
		if(address.getLength() == 8)
		{
			Stargate stargate = BlockEntityList.get(server).getStargates().get(address);
			
			if(stargate != null)
				return Optional.of(stargate);
		}

		return Optional.empty();
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public final void handleConnections()
	{
		Map<UUID, StargateConnection> connections = new HashMap<>();
		connections.putAll(this.connections);
		
		connections.forEach((connectionID, connection) -> connection.tick(server));
		this.setDirty();
	}
	
	public final int getOpenTime(UUID uuid)
	{
		if(this.connections.containsKey(uuid))
			return connections.get(uuid).getConnectionTime();
		return 0;
	}
	
	public final int getTimeSinceLastTraveler(UUID uuid)
	{
		if(this.connections.containsKey(uuid))
			return connections.get(uuid).getTimeSinceLastTraveler();
		return 0;
	}
	
	public final Stargate.Feedback createConnection(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		StargateConnection.Type connectionType = StargateConnection.getType(server, dialingStargate, dialedStargate);
		
		// Event for Stargate connecting, can be cancelled - !!!NOTE That it does NOT reset the Stargate or actually change its feedback when cancelled!!!
		if(SGJourneyEvents.onStargateConnect(server, dialingStargate, dialedStargate, connectionType, addressType, doKawoosh))
			return Stargate.Feedback.NONE;
		
		// Will reset the Stargate if something's wrong
		if(!dialedStargate.checkStargateEntity(server))
			return dialingStargate.resetStargate(server, Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
		
		if(!CommonStargateConfig.allow_interstellar_8_chevron_addresses.get() &&
				addressType == Address.Type.ADDRESS_8_CHEVRON &&
				connectionType == StargateConnection.Type.INTERSTELLAR)
			return dialingStargate.resetStargate(server, Stargate.Feedback.INVALID_8_CHEVRON_ADDRESS, true);
		
		if(!CommonStargateConfig.allow_system_wide_connections.get() && connectionType == StargateConnection.Type.SYSTEM_WIDE)
			return dialingStargate.resetStargate(server, Stargate.Feedback.INVALID_SYSTEM_WIDE_CONNECTION, true);
		
		if(dialingStargate.equals(dialedStargate))
			return dialingStargate.resetStargate(server, Stargate.Feedback.SELF_DIAL, true);

		if(dialedStargate.isConnected(server))
			return dialingStargate.resetStargate(server, Stargate.Feedback.ALREADY_CONNECTED, true);
		else if(dialedStargate.isObstructed(server))
			return dialingStargate.resetStargate(server, Stargate.Feedback.TARGET_OBSTRUCTED, true);
		
		if(requireEnergy)
		{
			if(dialingStargate.canExtractEnergy(server, connectionType.getEstablishingPowerCost()))
				dialingStargate.depleteEnergy(server, connectionType.getEstablishingPowerCost(), false);
			else
				return dialingStargate.resetStargate(server, Stargate.Feedback.NOT_ENOUGH_POWER, true);
		}
		
		Optional<AbstractStargateEntity> outgoingStargate = dialingStargate.getStargateEntity(server);
		Optional<AbstractStargateEntity> incomingStargate = dialedStargate.getStargateEntity(server);
		
		if(outgoingStargate.isPresent() && incomingStargate.isPresent())
		{
			// Call Forwarding
			if(incomingStargate.get().dhdInfo().shouldCallForward())
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
						while(reroutedStargate.get().getStargateEntity(server).get().dhdInfo().shouldCallForward())
						{
							reroutedStargate = solarSystem.getRandomStargate(random.nextLong());
						}
						
						incomingStargate = reroutedStargate.get().getStargateEntity(server);
					}
				}
			}

			StargateConnection connection = StargateConnection.create(connectionType, outgoingStargate.get(), incomingStargate.get(), doKawoosh);
			if(connection != null)
			{
				addConnection(connection);
				
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
		}
		
		return Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE;
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
	
	public final void terminateConnection(UUID uuid, Stargate.Feedback feedback)
	{
		if(!hasConnection(uuid))
			return;
		
		StargateConnection connection = this.connections.get(uuid);
		
		SGJourneyEvents.onConnectionTerminated(server, connection);
		
		connection.terminate(server, feedback);
	}
	
	public final void removeConnection(UUID uuid, Stargate.Feedback feedback)
	{
		if(hasConnection(uuid))
		{
			this.connections.remove(uuid);
			StargateJourney.LOGGER.info("Removed connection " + uuid);
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
			this.connections.get(uuid).sendStargateMessage(sendingStargate, messsage);
			return true;
		}
		else
			return false;
	}
	
	public final void sendStargateTransmission(AbstractStargateEntity sendingStargate, UUID uuid, int transmissionJumps, int frequency, String transmission)
	{
		if(hasConnection(uuid))
			this.connections.get(uuid).sendStargateTransmission(sendingStargate, transmissionJumps, frequency, transmission);
	}
	
	public final float checkStargateShieldingState(AbstractStargateEntity sendingStargate, UUID uuid)
	{
		if(hasConnection(uuid))
			return this.connections.get(uuid).checkStargateShieldingState(sendingStargate);
		
		return 0;
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
		tag.getAllKeys().forEach(connectionID ->
		{
			try { this.connections.put(UUID.fromString(connectionID), StargateConnection.deserialize(server, UUID.fromString(connectionID), tag.getCompound(connectionID))); }
			catch(IllegalArgumentException e) {}
		});
	}
	
	public static final void findStargates(ServerLevel level)
	{
		StargateJourney.LOGGER.info("Attempting to locate the Stargate Structure in " + level.dimension().location().toString());
		
		int xOffset = CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
        int zOffset = CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
		//Nearest Structure that potentially has a Stargate
		BlockPos blockpos = ((ServerLevel) level).findNearestMapStructure(TagInit.Structures.HAS_STARGATE, new BlockPos(xOffset * 16, 0, zOffset * 16), 150, false);
		if(blockpos == null)
		{
			StargateJourney.LOGGER.info("Stargate Structure not found");
			return;
		}
		//Map of Block Entities that might contain a Stargate
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -2; x <= 2; x++)
		{
			for(int z = -2; z <= 2; z++)
			{
				ChunkAccess chunk = level.getChunk(blockpos.east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
						stargates.add(stargate);
				});
			}
		}
		
		if(stargates.isEmpty())
		{
			StargateJourney.LOGGER.info("No Stargates found in Stargate Structure");
			return;
		}
		
		stargates.stream().forEach(stargate -> stargate.onLoad());
		return;
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

	public CompoundTag save(CompoundTag tag)
	{
		tag = serialize();
		
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
