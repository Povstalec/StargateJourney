package net.povstalec.sgjourney.common.data;

import java.util.*;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.transporter.BlockEntityTransporter;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;
import org.jetbrains.annotations.NotNull;

/**
 * Dimension - Frequency - Rings
 * @author Povstalec
 *
 */
public final class TransporterNetwork extends SavedData
{
	private static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	private static final String FILE_NAME = StargateJourney.MODID + "-transporter_network";
	private static final String VERSION = "version";
	
	private static final String CONNECTIONS = "connections";

	private static final int UPDATE_VERSION = 3;
	
	private MinecraftServer server;

	private final Map<ResourceKey<Level>, List<Transporter>> dimensionTransporters = new HashMap<>();
	private final Map<ResourceKey<AddressRegion>, List<Transporter>> regionTransporters = new HashMap<>();
	private final HashMap<UUID, TransporterConnection> connections = new HashMap<>();
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
	
	public void updateNetwork()
	{
		if(getVersion() == UPDATE_VERSION)
		{
			StargateJourney.LOGGER.info("Transporter Network is up to date (Version: {})", version);
			return;
		}
		
		StargateJourney.LOGGER.info("Detected an incompatible Transporter Network version (Version: {}) - updating to version {}", getVersion(), UPDATE_VERSION);
		
		reloadNetwork();
	}
	
	public void reloadNetwork()
	{
		eraseNetwork();
		StargateJourney.LOGGER.info("Transporter Network erased");
		
		resetTransporters();
		StargateJourney.LOGGER.info("Transporters reset");
		
		updateVersion();
		StargateJourney.LOGGER.info("Version updated");
		
		this.setDirty();
	}
	
	public void eraseNetwork()
	{
		this.regionTransporters.clear();
		this.dimensionTransporters.clear();
		this.connections.clear();
		
		this.setDirty();
	}
	
	//============================================================================================
	//****************************************Transporters****************************************
	//============================================================================================
	
	public void addTransporters()
	{
		BlockEntityList.get(server).getTransporters().forEach((address, transporter) ->
		{
			if(transporter != null)
				addTransporter(transporter);
		});
	}
	
	public void resetTransporters()
	{
		HashMap<TransporterID, Transporter> transporters = BlockEntityList.get(server).getTransporters();
		
		transporters.forEach((transporterID, transporter) ->
		{
			if(transporter != null)
			{
				if(transporter.checkValidity())
				{
					if(!transporterID.equals(transporter.getID()))
						removeTransporter(transporter);
					
					transporter.resetTransporter(TransporterInfo.Feedback.CONNECTION_ENDED_BY_NETWORK);
					
					addTransporter(transporter);
				}
				else
					removeTransporter(transporter);
			}
		});
	}
	
	public void addTransporterEntity(AbstractTransporterEntity<?> transporterEntity)
	{
		Transporter transporter = BlockEntityList.get(server).addTransporter(transporterEntity);
		
		if(transporter != null && transporterEntity.getID() != null && transporterEntity.getID().equals(transporter.getID()))
			addTransporter(transporter);
	}
	
	public boolean addTransporter(Transporter transporter)
	{
		if(transporter == null)
		{
			StargateJourney.LOGGER.error("Could not add Transporter to Transporter Network because it's null");
			return false;
		}
		
		// Using | instead of || because both need to execute
		boolean added = addTransporterToDimension(transporter.getDimension(), transporter) | addTransporterToAddressRegion(transporter.getAddressRegionKey(), transporter);
		
		if(added)
			setDirty();
		
		return added;
	}
	
	public boolean addTransporterToDimension(ResourceKey<Level> dimension, Transporter transporter)
	{
		if(dimension == null)
			return false;
		
		List<Transporter> transportersInDimension = dimensionTransporters.get(dimension);
		if(transportersInDimension == null)
		{
			transportersInDimension = new ArrayList<>();
			transportersInDimension.add(transporter);
			dimensionTransporters.put(dimension, transportersInDimension);
			return true;
		}
		else
		{
			int index = Collections.binarySearch(transportersInDimension, transporter);
			if(index < 0) // Transporter was not found
			{
				transportersInDimension.add(-index - 1, transporter);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean addTransporterToAddressRegion(@Nullable ResourceKey<AddressRegion> addressRegionKey, Transporter transporter)
	{
		if(addressRegionKey == null)
			return false;
		
		List<Transporter> transportersInRegion = regionTransporters.get(addressRegionKey);
		if(transportersInRegion == null)
		{
			transportersInRegion = new ArrayList<>();
			transportersInRegion.add(transporter);
			regionTransporters.put(addressRegionKey, transportersInRegion);
			return true;
		}
		else
		{
			int index = Collections.binarySearch(transportersInRegion, transporter);
			if(index < 0) // Transporter was not found
			{
				transportersInRegion.add(-index - 1, transporter);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean removeTransporter(TransporterID transporterID)
	{
		if(transporterID == null)
			return false;
		
		return removeTransporter(getTransporter(transporterID));
	}
	
	public boolean removeTransporter(Transporter transporter)
	{
		if(transporter == null)
		{
			StargateJourney.LOGGER.error("Could not remove Transporter from Transporter Network because it's null");
			return false;
		}
		
		// Using | instead of || because both need to execute
		boolean removed = removeTransporterFromAddressRegion(transporter.getAddressRegionKey(), transporter) | removeTransporterFromDimension(transporter.getDimension(), transporter);
		if(removed)
		{
			setDirty();
			StargateJourney.LOGGER.debug("Removed {} from Stargate Network", transporter.getID().toString());
		}
		
		if(transporter instanceof BlockEntityTransporter<?>) // Attempt to remove it from the Block Entity List
			removed |= BlockEntityList.get(server).removeTransporter(transporter.getID());
		
		return removed;
	}
	
	private boolean removeTransporterFromDimension(ResourceKey<Level> dimension, Transporter transporter)
	{
		List<Transporter> transportersInDimension = dimensionTransporters.get(dimension);
		if(transportersInDimension == null)
			return false;
		
		return transportersInDimension.remove(transporter);
	}
	
	private boolean removeTransporterFromAddressRegion(ResourceKey<AddressRegion> dimension, Transporter transporter)
	{
		List<Transporter> transportersInRegion = regionTransporters.get(dimension);
		if(transportersInRegion == null)
			return false;
		
		return transportersInRegion.remove(transporter);
	}
	
	public void updateTransporterEntity(AbstractTransporterEntity<?> transporterEntity)
	{
		Transporter transporter = getTransporter(transporterEntity.getID());
		
		if(transporter != null)
			transporter.update();
	}
	
	public int getTransporterCount()
	{
		return BlockEntityList.get(server).getTransporterCount();
	}
	
	@Nullable
	public Transporter getTransporter(TransporterID transporterID)
	{
		return BlockEntityList.get(server).getTransporter(transporterID);
	}
	
	//============================================================================================
	//***********************************Dimension Transporters***********************************
	//============================================================================================
	
	/**
	 * @param dimension Dimension we want to get the Transporters from
	 * @return List of Transporters in the specified Dimension, or an empty list, in case the Dimension is not found in the Transporter Network
	 */
	public List<Transporter> getTransportersInDimension(ResourceKey<Level> dimension)
	{
		List<Transporter> transportersInDimension = this.dimensionTransporters.get(dimension);
		
		if(transportersInDimension == null)
			return List.of();
		
		return transportersInDimension;
	}
	
	/**
	 * @param dimension Dimension we want to get the Transporters from
	 * @param predicate Predicate that limits which Transporters can appear in the list
	 * @return List of Transporters in the specified Dimension, or an empty list, in case the Dimension is not found in the Transporter Network
	 */
	public List<Transporter> getTransportersInDimension(ResourceKey<Level> dimension, Predicate<Transporter> predicate)
	{
		List<Transporter> transportersInDimension = this.dimensionTransporters.get(dimension);
		
		if(transportersInDimension == null)
			return List.of();
		
		return transportersInDimension.stream().filter(predicate).toList();
	}
	
	public void printDimensions()
	{
		System.out.println("[Transporters in Dimensions]");
		this.dimensionTransporters.forEach((dimension, transportersInDimension) ->
		{
			System.out.println("Dimension: " + dimension.location());
			transportersInDimension.forEach(transporter -> System.out.println("--- " + transporter.toString()));
		});
	}
	
	//============================================================================================
	//************************************Region Transporters*************************************
	//============================================================================================
	
	/**
	 * @param addressRegionKey Address Region we want to get the Transporters from
	 * @return List of Transporters in the specified Dimension, or an empty list, in case the Dimension is not found in the Transporter Network
	 */
	public List<Transporter> getTransportersInRegion(ResourceKey<AddressRegion> addressRegionKey)
	{
		List<Transporter> regionTransporters = this.regionTransporters.get(addressRegionKey);
		
		if(regionTransporters == null)
			return List.of();
		
		return regionTransporters;
	}
	
	/**
	 * @param addressRegionKey Address Region we want to get the Transporters from
	 * @param predicate Predicate that limits which Transporters can appear in the list
	 * @return List of Transporters in the specified Dimension, or an empty list, in case the Dimension is not found in the Transporter Network
	 */
	public List<Transporter> getTransportersInRegion(ResourceKey<Level> addressRegionKey, Predicate<Transporter> predicate)
	{
		List<Transporter> regionTransporters = this.regionTransporters.get(addressRegionKey);
		
		if(regionTransporters == null)
			return List.of();
		
		return regionTransporters.stream().filter(predicate).toList();
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public Map<UUID, TransporterConnection> getConnections()
	{
		return new HashMap<>(this.connections);
	}
	
	@Nullable
	public TransporterConnection getConnection(UUID connectionID)
	{
		return this.connections.get(connectionID);
	}
	
	public void handleConnections()
	{
		Map<UUID, TransporterConnection> connections = new HashMap<>(this.connections);
		
		connections.forEach((uuid, connection) -> connection.tick(server));
		this.setDirty();
	}
	
	public TransporterInfo.FeedbackMessage createConnection(Transporter initiatingTransporter, Transporter targetTransporter)
	{
		TransporterConnection.Type connectionType = TransporterConnection.getType(server, initiatingTransporter, targetTransporter);
		
		// Event for Transporter connecting, can be canceled - !!!NOTE That it does NOT reset the Transporter or actually change its feedback when canceled!!!
		if(SGJourneyEvents.onTransporterConnect(server, initiatingTransporter, targetTransporter, connectionType))
			return TransporterInfo.Feedback.NONE.withInfo();
		
		// Will reset the Transporter if something's wrong
		if(!targetTransporter.checkValidity())
			return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.COULD_NOT_REACH_TARGET_TRANSPORTER);
		
		if(initiatingTransporter.equals(targetTransporter))
			return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.SELF_CONNECT);
		
		if(targetTransporter.isConnected())
			return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.ALREADY_CONNECTED);
		else if(targetTransporter.isObstructed())
			return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.TARGET_OBSTRUCTED);
		
		if(connectionType == null)
			return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.NO_INTERSTELLAR_TRANSPORT);
		else if(connectionType == TransporterConnection.Type.SYSTEM_WIDE)
		{
			if(!initiatingTransporter.allowInterdimensionalTransport())
				return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.SELF_NO_INTERDIMENSIONAL_TRANSPORT);
			else if(!targetTransporter.allowInterdimensionalTransport())
				return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.TARGET_NO_INTERDIMENSIONAL_TRANSPORT);
		}
		
		if(!initiatingTransporter.isInRange(targetTransporter))
			return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.TARGET_OUT_OF_RANGE); //TODO Range message
		else if(!targetTransporter.isInRange(initiatingTransporter))
			return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.OUT_OF_RANGE_OF_TARGET); //TODO Range message
		
		if(REQUIRE_ENERGY)
		{
			double distance = initiatingTransporter.distanceFrom(targetTransporter);
			long transportEnergyCostA = connectionType.getTransportEnergyCost(distance, initiatingTransporter.getTransferEfficiency());
			long transportEnergyCostB = connectionType.getTransportEnergyCost(distance, targetTransporter.getTransferEfficiency());
			
			if(!TransporterConnection.canExtract(initiatingTransporter, transportEnergyCostA))
				return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.NOT_ENOUGH_POWER, SGJourneyEnergy.energyToString(transportEnergyCostA));
			else if(!TransporterConnection.canExtract(targetTransporter, transportEnergyCostB))
				return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.NOT_ENOUGH_POWER_IN_TARGET, SGJourneyEnergy.energyToString(transportEnergyCostB));
			
			initiatingTransporter.extractEnergy(transportEnergyCostA, false);
			targetTransporter.extractEnergy(transportEnergyCostB, false);
		}
		
		TransporterConnection connection = TransporterConnection.create(connectionType, initiatingTransporter, targetTransporter);
		
		//TODO New errors relating to the problems with relaying the connection through Stargates
		//if(connection.getRelayID() == null && connectionType.isRelayed)
		//	return TransporterInfo.Feedback.
		
		if(connection != null)
		{
			addConnection(connection);
			
			return switch(connectionType)
			{
				case DIMENSIONAL -> TransporterInfo.Feedback.CONNECTION_ESTABLISHED_DIMENSIONAL.withInfo();
				case SYSTEM_WIDE -> TransporterInfo.Feedback.CONNECTION_ESTABLISHED_SYSTEM_WIDE.withInfo();
				case RELAYED_DIMENSIONAL -> TransporterInfo.Feedback.CONNECTION_ESTABLISHED_RELAYED_DIMENSIONAL.withInfo();
				case RELAYED_SYSTEM_WIDE -> TransporterInfo.Feedback.CONNECTION_ESTABLISHED_RELAYED_SYSTEM_WIDE.withInfo();
				case RELAYED_INTERSTELLAR -> TransporterInfo.Feedback.CONNECTION_ESTABLISHED_RELAYED_INTERSTELLAR.withInfo();
				default -> TransporterInfo.Feedback.CONNECTION_ESTABLISHED_RELAYED_INTERGALACTIC.withInfo();
			};
		}
		
		return TransporterInfo.Feedback.COULD_NOT_REACH_TARGET_TRANSPORTER.withInfo();
	}
	
	public boolean addConnection(TransporterConnection connection)
	{
		if(!hasConnection(connection.getID()))
		{
			this.connections.put(connection.getID(), connection);
			SGJourneyEvents.onTransporterConnectionEstablished(server, connection);
			
			return true;
		}
		
		return false;
	}
	
	public boolean hasConnection(UUID uuid)
	{
		return this.connections.containsKey(uuid);
	}
	
	public void terminateConnection(UUID uuid, TransporterInfo.Feedback feedback)
	{
		TransporterConnection connection = this.connections.get(uuid);
		
		if(connection != null)
			connection.terminate(server, feedback);
	}
	
	public void removeConnection(UUID uuid)
	{
		if(hasConnection(uuid))
		{
			this.connections.remove(uuid);
			StargateJourney.LOGGER.debug("Removed connection {}", uuid);
		}
		else
			StargateJourney.LOGGER.error("Could not find connection {}", uuid);
		this.setDirty();
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private void serialize(CompoundTag tag)
	{
		tag.putInt(VERSION, this.version);
		tag.put(CONNECTIONS, serializeConnections());
	}
	
	private CompoundTag serializeConnections()
	{
		CompoundTag connectionsTag = new CompoundTag();
		
		this.connections.forEach((connectionID, connection) -> connectionsTag.put(connectionID.toString(), connection.serialize()));
		
		return connectionsTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		this.version = tag.getInt(VERSION);

		deserializeConnections(tag.getCompound(CONNECTIONS));
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
				else
					StargateJourney.LOGGER.error("Could not deserialize Transporter Connection {}", connectionID);
			}
			catch(IllegalArgumentException e)
			{
				StargateJourney.LOGGER.error("Could not deserialize Transporter Connection {}", connectionID, e);
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

	public @NotNull CompoundTag save(@NotNull CompoundTag tag)
	{
		serialize(tag);
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
}
