package net.povstalec.sgjourney.common.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

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
	private static final int updateVersion = 8;
	
	private MinecraftServer server;
	
	private Map<String, Connection> connections = new HashMap<String, Connection>();
	private CompoundTag stargateNetwork = new CompoundTag();
	private int version = updateVersion;

	//private HashMap<Address, Stargate> stargates = new HashMap<Address, Stargate>();
	
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
	
	public void addStargates(MinecraftServer server)
	{
		HashMap<Address, Stargate> stargates = BlockEntityList.get(server).getStargates();
		
		stargates.entrySet().stream().forEach((stargateInfo) ->
		{
			Stargate mapStargate = stargateInfo.getValue();
			
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
		});
	}
	
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
					
					addStargate(stargate);
					stargate.updateStargate(updateInterfaces);//TODO Probably should look at this
				}
				else
				{
					removeStargate(server.getLevel(dimension), address);
					BlockEntityList.get(server).removeStargate(address);
				}
			}
		});
	}
	
	public void addStargate(AbstractStargateEntity stargateEntity)
	{
		Optional<Stargate> stargateOptional = BlockEntityList.get(server).addStargate(stargateEntity);
		
		if(stargateOptional.isPresent())
		{
			Stargate stargate = stargateOptional.get();
			Universe.get(server).addStargateToDimension(stargate.getDimension(), stargate);
		}
		
		this.setDirty();
	}
	
	public void removeStargate(Level level, Address address)
	{
		Optional<Stargate> stargate = getStargate(address);
		
		if(stargate.isPresent())
			Universe.get(server).removeStargateFromDimension(level.dimension(), stargate.get());

		BlockEntityList.get(level).removeStargate(address);
		
		StargateJourney.LOGGER.info("Removed " + address.toString() + " from Stargate Network");
		setDirty();
	}
	
	public void updateStargate(ServerLevel level, AbstractStargateEntity stargate)
	{
		Optional<Stargate> stargateOptional = getStargate(stargate.get9ChevronAddress());
		
		if(stargateOptional.isPresent())
		{
			Universe.get(server).removeStargateFromDimension(level.dimension(), stargateOptional.get());
			stargateOptional.get().update(stargate);
			Universe.get(server).addStargateToDimension(level.dimension(), stargateOptional.get());
		}
	}
	
	public Optional<Stargate> getStargate(Address address)
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
		// Will reset the Stargate if something's wrong
		AbstractStargateEntity.checkStargate(dialedStargate);
		
		Connection.Type connectionType = Connection.getType(server, dialingStargate, dialedStargate);
		
		if(!CommonStargateConfig.allow_interstellar_8_chevron_addresses.get() &&
				addressType == Address.Type.ADDRESS_8_CHEVRON &&
				connectionType == Connection.Type.INTERSTELLAR)
			return dialingStargate.resetStargate(Stargate.Feedback.INVALID_8_CHEVRON_ADDRESS, true);
		
		if(!CommonStargateConfig.allow_system_wide_connections.get() && connectionType == Connection.Type.SYSTEM_WIDE)
			return dialingStargate.resetStargate(Stargate.Feedback.INVALID_SYSTEM_WIDE_CONNECTION, true);
		
		if(dialingStargate.equals(dialedStargate))
			return dialingStargate.resetStargate(Stargate.Feedback.SELF_DIAL, true);
		
		if(dialedStargate.isConnected())
			return dialingStargate.resetStargate(Stargate.Feedback.ALREADY_CONNECTED, true);
		else if(dialedStargate.isObstructed())
			return dialingStargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED, true);
		
		if(requireEnergy)
		{
			if(dialingStargate.canExtractEnergy(connectionType.getEstablishingPowerCost()))
				dialingStargate.depleteEnergy(connectionType.getEstablishingPowerCost(), false);
			else
				return dialingStargate.resetStargate(Stargate.Feedback.NOT_ENOUGH_POWER, true);
		}
		
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
	
	public boolean hasConnection(String uuid)
	{
		if(this.connections.containsKey(uuid))
			return true;
		
		return false;
	}
	
	public void terminateConnection(String uuid, Stargate.Feedback feedback)
	{
		if(hasConnection(uuid))
			this.connections.get(uuid).terminate(server, feedback);
	}
	
	public void removeConnection(String uuid, Stargate.Feedback feedback)
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
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(VERSION, this.version);
		tag.put(CONNECTIONS, serializeConnections());
		
		return tag;
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
	}
	
	private void deserializeConnections(CompoundTag tag)
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
