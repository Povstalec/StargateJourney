package net.povstalec.sgjourney.common.data;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
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
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.AddressRegion;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public final class StargateNetwork extends SavedData
{
	private static final boolean REQUIRE_ENERGY = !StargateJourneyConfig.disable_energy_use.get();
	
	private static final String FILE_NAME = StargateJourney.MODID + "-stargate_network";
	private static final String VERSION = "Version";

	private static final String CONNECTIONS = "Connections";

	// Should increase every time there's a significant change done to the Stargate Network or the way Stargates work
	private static final int updateVersion = 18;
	
	private MinecraftServer server;
	private final Map<ResourceKey<AddressRegion>, RegionStargates> regionStargates = new HashMap<>();
	private final Map<UUID, StargateConnection> connections = new HashMap<UUID, StargateConnection>();
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
	
	public final void updateNetwork()
	{
		if(getVersion() == updateVersion)
		{
			StargateJourney.LOGGER.debug("Stargate Network is up to date (Version: " + version + ")");
			return;
		}
		
		StargateJourney.LOGGER.debug("Detected an incompatible Stargate Network version (Version: " + getVersion() + ") - updating to version " + updateVersion);
		
		stellarUpdate();
	}
	
	public final void eraseNetwork()
	{
		this.connections.clear();
		
		this.setDirty();
	}
	
	public final boolean randomizeAddresses()
	{
		return StargateNetworkSettings.get(server).randomizeAddresses();
	}
	
	public final void stellarUpdate()
	{
		for(Entry<UUID, StargateConnection> connectionEntry : getConnections().entrySet())
		{
			connectionEntry.getValue().terminate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_NETWORK);
		}
		
		StargateJourney.LOGGER.debug("Connections terminated");
		
		Universe.get(server).eraseUniverseInfo();
		StargateJourney.LOGGER.debug("Universe erased");
		
		Universe.get(server).generateUniverseInfo();
		StargateJourney.LOGGER.debug("Universe regenerated");
		
		eraseNetwork();
		StargateJourney.LOGGER.debug("Stargate Network erased");
		
		resetStargates();
		StargateJourney.LOGGER.debug("Stargates reset");
		
		updateVersion();
		StargateJourney.LOGGER.debug("Version updated");
		
		this.setDirty();
	}
	
	//============================================================================================
	//*****************************************Stargates******************************************
	//============================================================================================
	
	public void addStargates()
	{
		BlockEntityList.get(server).getStargates().forEach((address, stargate) ->
		{
			if(stargate != null)
				addStargate(stargate);
		});
	}
	
	public void resetStargates()
	{
		HashMap<Address.Immutable, Stargate> stargates = BlockEntityList.get(server).getStargates();
		
		stargates.entrySet().forEach((stargateInfo) ->
		{
			Address.Immutable address = stargateInfo.getKey();
			Stargate stargate = stargateInfo.getValue();
			
			if(stargate != null)
			{
				if(stargate.checkValidity(server))
				{
					if(!address.equals(stargate.get9ChevronAddress()))
						removeStargate(stargate);
					
					stargate.resetStargate(server, StargateInfo.Feedback.CONNECTION_ENDED_BY_NETWORK);
					
					stargate.update(server);
					addStargate(stargate);
					stargate.updateInterfaceBlocks(server, null, null);
				}
				else
					removeStargate(stargate);
			}
			else
				BlockEntityList.get(server).removeStargate(address);
		});
	}
	
	public boolean addStargateEntity(AbstractStargateEntity<?> stargateEntity)
	{
		return addStargate(BlockEntityList.get(server).addStargate(stargateEntity));
	}
	
	public boolean addStargate(Stargate stargate)
	{
		if(stargate == null)
			return false;
		
		return addStargateToDimension(stargate.getDimension(), stargate);
	}
	
	public boolean addStargateToDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		AddressRegion addressRegion = Universe.get(server).getAddressRegionFromDimension(dimension);
		if(addressRegion == null)
			return false;
		
		return addStargateToAddressRegion(addressRegion.getResourceKey(), stargate);
	}
	
	public boolean addStargateToAddressRegion(@Nullable ResourceKey<AddressRegion> addressRegionKey, Stargate stargate)
	{
		if(addressRegionKey == null)
			return false;
		
		RegionStargates region = regionStargates.get(addressRegionKey);
		if(region == null)
		{
			region = new RegionStargates(addressRegionKey);
			region.addStargate(server, stargate);
			regionStargates.put(addressRegionKey, region);
			return true;
		}
		else
		{
			boolean result = region.addStargate(server, stargate);
			if(result)
				this.setDirty();
			
			return result;
		}
	}
	
	public boolean removeStargate(Address.Immutable address)
	{
		if(address == null)
			return false;
		
		Stargate stargate = getStargate(address);
		return removeStargate(stargate);
	}
	
	public boolean removeStargate(Stargate stargate)
	{
		if(stargate != null)
		{
			boolean result = false;
			
			AddressRegion addressRegion = stargate.getAddressRegion(server);
			if(addressRegion != null)
				result = removeStargateFromAddressRegion(addressRegion.getResourceKey(), stargate);
			if(result)
			{
				setDirty();
				StargateJourney.LOGGER.debug("Removed " + stargate.get9ChevronAddress().toString() + " from Stargate Network");
			}
			result |= BlockEntityList.get(server).removeStargate(stargate.get9ChevronAddress());
			return result;
		}
		else
		{
			StargateJourney.LOGGER.error("Could not remove Stargate because it's null");
			return false;
		}
	}
	
	public boolean removeStargateFromDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		AddressRegion addressRegion = Universe.get(server).getAddressRegionFromDimension(dimension);
		if(addressRegion == null)
			return false;
		
		return removeStargateFromAddressRegion(addressRegion.getResourceKey(), stargate);
	}
	
	public boolean removeStargateFromAddressRegion(@Nullable ResourceKey<AddressRegion> addressRegionKey, Stargate stargate)
	{
		if(addressRegionKey == null)
			return false;
		
		RegionStargates region = regionStargates.get(addressRegionKey);
		if(region == null)
			return false;
		
		boolean result = region.removeStargate(stargate);
		if(result)
			this.setDirty();
		
		return result;
	}
	
	public void updateStargateEntity(AbstractStargateEntity<?> stargateEntity)
	{
		Stargate stargate = getStargate(stargateEntity.get9ChevronAddress());
		
		if(stargate != null)
		{
			removeStargateFromDimension(stargateEntity.getLevel().dimension(), stargate);
			stargate.update(server);
			addStargateToDimension(stargateEntity.getLevel().dimension(), stargate);
		}
	}
	
	public int getStargateCount()
	{
		return BlockEntityList.get(server).getStargateCount();
	}
	
	@Nullable
	public Stargate getStargate(Address address)
	{
		return BlockEntityList.get(server).getStargate(address);
	}
	
	@Nullable
	public Stargate getRandomStargate(RandomSource randomSource)
	{
		return BlockEntityList.get(server).getRandomStargate(randomSource);
	}
	
	//============================================================================================
	//**************************************Region Stargates**************************************
	//============================================================================================
	
	private void regionRun(ResourceKey<AddressRegion> addressRegionKey, Consumer<RegionStargates> consumer)
	{
		RegionStargates regionStargates = this.regionStargates.get(addressRegionKey);
		if(regionStargates != null)
			consumer.accept(regionStargates);
	}
	
	private <T> T regionReturn(ResourceKey<AddressRegion> addressRegionKey, Function<RegionStargates, T> function, T defaultValue)
	{
		RegionStargates regionStargates = this.regionStargates.get(addressRegionKey);
		if(regionStargates == null)
			return defaultValue;
		
		return function.apply(regionStargates);
	}
	
	public boolean hasStargatesInRegion(ResourceKey<AddressRegion> addressRegionKey)
	{
		return regionReturn(addressRegionKey, regionStargates -> !regionStargates.stargates.isEmpty(), false);
	}
	
	public List<Stargate> getStargatesInRegion(ResourceKey<AddressRegion> addressRegionKey)
	{
		return regionReturn(addressRegionKey, regionStargates -> regionStargates.stargates, List.of());
	}
	
	public List<Stargate> getStargatesInRegion(ResourceKey<AddressRegion> addressRegionKey, Predicate<Stargate> predicate)
	{
		return regionReturn(addressRegionKey, regionStargates -> regionStargates.stargates.stream().filter(predicate).toList(), List.of());
	}
	
	public List<Stargate> getShuffledStargatesInRegion(ResourceKey<AddressRegion> addressRegionKey, RandomSource randomSource)
	{
		return regionReturn(addressRegionKey, regionStargates -> Util.toShuffledList(regionStargates.stargates.stream(), randomSource), List.of());
	}
	
	public void sortStargatesInRegion(ResourceKey<AddressRegion> addressRegionKey)
	{
		regionRun(addressRegionKey, regionStargates -> regionStargates.stargates.sort(null));
	}
	
	@Nullable
	public Stargate findStargateInRegion(ResourceKey<AddressRegion> addressRegionKey, Address address)
	{
		return regionReturn(addressRegionKey, regionStargates -> regionStargates.findStargate(address), null);
	}
	
	@Nullable
	public Stargate getRandomStargateInRegion(ResourceKey<AddressRegion> addressRegionKey, long seed)
	{
		return regionReturn(addressRegionKey, regionStargates -> regionStargates.getRandomStargate(seed), null);
	}
	
	//============================================================================================
	//**************************************Primary Stargate**************************************
	//============================================================================================
	
	public boolean setPrimaryAddressForDimension(ResourceKey<Level> dimension, @Nullable Address.Immutable primaryAddress)
	{
		AddressRegion addressRegion = Universe.get(server).getAddressRegionFromDimension(dimension);
		if(addressRegion == null)
			return false;
		
		return setPrimaryAddressForAddressRegion(addressRegion.getResourceKey(), primaryAddress);
	}
	
	/**
	 * Sets the Stargate with the specified Address as the Primary Stargate of the specified Address Region
	 * @param addressRegionKey Key of the specified Address Region
	 * @param primaryAddress Address of the Primary Stargate
	 * @return True if the Primary Stargate was set successfully, otherwise false
	 */
	public boolean setPrimaryAddressForAddressRegion(ResourceKey<AddressRegion> addressRegionKey, @Nullable Address.Immutable primaryAddress)
	{
		if(primaryAddress != null && primaryAddress.getType() != Address.Type.ADDRESS_9_CHEVRON)
			return false;
		
		RegionStargates regionStargates = this.regionStargates.get(addressRegionKey);
		if(regionStargates == null)
			return false;
		
		StargateNetworkSettings settings = StargateNetworkSettings.get(server);
		
		if(primaryAddress == null)
		{
			regionStargates.primaryStargate = null;
			settings.setPrimaryAddress(addressRegionKey, null);
			
		}
		else if(primaryAddress.equals(settings.getPrimaryAddress(addressRegionKey)))
			return false;
		else
		{
			regionStargates.primaryStargate = regionStargates.findStargate(primaryAddress);
			settings.setPrimaryAddress(addressRegionKey, primaryAddress);
		}
		
		return true;
	}
	
	@Nullable
	public Address.Immutable getPrimaryAddressFromDimension(ResourceKey<Level> dimension)
	{
		AddressRegion addressRegion = Universe.get(server).getAddressRegionFromDimension(dimension);
		if(addressRegion == null)
			return null;
		
		return getPrimaryAddressFromAddressRegion(addressRegion.getResourceKey());
	}
	
	@Nullable
	public Address.Immutable getPrimaryAddressFromAddressRegion(ResourceKey<AddressRegion> addressRegionKey)
	{
		return StargateNetworkSettings.get(server).getPrimaryAddress(addressRegionKey);
	}
	
	@Nullable
	public Stargate getPrimaryStargateFromDimension(ResourceKey<Level> dimension)
	{
		AddressRegion addressRegion = Universe.get(server).getAddressRegionFromDimension(dimension);
		if(addressRegion == null)
			return null;
		
		return getPrimaryStargateFromAddressRegion(addressRegion.getResourceKey());
	}
	
	@Nullable
	public Stargate getPrimaryStargateFromAddressRegion(ResourceKey<AddressRegion> addressRegionKey)
	{
		RegionStargates regionStargates = this.regionStargates.get(addressRegionKey);
		if(regionStargates != null)
			return regionStargates.primaryStargate;
		
		return null;
	}
	
	//============================================================================================
	//****************************************Connections*****************************************
	//============================================================================================
	
	public Map<UUID, StargateConnection> getConnections()
	{
		return new HashMap<>(this.connections);
	}
	
	public final void handleConnections()
	{
		Map<UUID, StargateConnection> connections = new HashMap<>(this.connections);
		
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
	
	public final StargateInfo.Feedback createConnection(Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		StargateConnection.Type connectionType = StargateConnection.getType(server, dialingStargate, dialedStargate);
		
		// Event for Stargate connecting, can be canceled - !!!NOTE That it does NOT reset the Stargate or actually change its feedback when canceled!!!
		if(SGJourneyEvents.onStargateConnect(server, dialingStargate, dialedStargate, connectionType, addressType, doKawoosh))
			return StargateInfo.Feedback.NONE;
		
		// Will reset the Stargate if something's wrong
		if(!dialedStargate.checkValidity(server))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
		
		if(!CommonStargateConfig.allow_interstellar_8_chevron_addresses.get() &&
				addressType == Address.Type.ADDRESS_8_CHEVRON &&
				connectionType == StargateConnection.Type.INTERSTELLAR)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_8_CHEVRON_ADDRESS);
		
		if(!CommonStargateConfig.allow_system_wide_connections.get() && connectionType == StargateConnection.Type.SYSTEM_WIDE)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_SYSTEM_WIDE_CONNECTION);
		
		if(dialingStargate.equals(dialedStargate))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.SELF_DIAL);
		
		if(dialedStargate.isConnected(server))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.ALREADY_CONNECTED);
		else if(dialedStargate.isObstructed(server))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.TARGET_OBSTRUCTED);
		
		if(REQUIRE_ENERGY)
		{
			if(StargateConnection.canExtract(server, dialingStargate, connectionType.getEstablishingPowerCost()))
				dialingStargate.extractEnergy(server, connectionType.getEstablishingPowerCost(), false);
			else
				return dialingStargate.resetStargate(server, StargateInfo.Feedback.NOT_ENOUGH_POWER);
		}
		
		List<Stargate> dialedStargates = dialedStargate.getDialedStargates(server, dialingStargate, connectionType);
		if(dialedStargates.isEmpty())
			return StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE;
		
		StargateConnection connection = StargateConnection.create(server, connectionType, dialingStargate, dialedStargates, doKawoosh);
		if(connection != null)
		{
			addConnection(connection);
			
			return switch(connectionType)
			{
				case SYSTEM_WIDE -> StargateInfo.Feedback.CONNECTION_ESTABLISHED_SYSTEM_WIDE;
				case INTERSTELLAR -> StargateInfo.Feedback.CONNECTION_ESTABLISHED_INTERSTELLAR;
				default -> StargateInfo.Feedback.CONNECTION_ESTABLISHED_INTERGALACTIC;
			};
		}
		
		return StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE;
	}
	
	public final boolean addConnection(StargateConnection connection)
	{
		if(!hasConnection(connection.getID()))
		{
			this.connections.put(connection.getID(), connection);
			SGJourneyEvents.onStargateConnectionEstablished(server, connection);
			
			return true;
		}
		
		return false;
	}
	
	public final boolean hasConnection(UUID uuid)
	{
		return this.connections.containsKey(uuid);
	}
	
	public final void terminateConnection(UUID uuid, StargateInfo.Feedback feedback)
	{
		StargateConnection connection = this.connections.get(uuid);
		
		if(connection != null)
			connection.terminate(server, feedback);
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
	
	public final void printRegionStargates()
	{
		System.out.println("[Stargates in Address Regions]");
		this.regionStargates.forEach((key, regionStargates) ->
				regionStargates.stargates.forEach(stargate -> System.out.println("--- " + stargate.toString())));
	}
	
	public final void printConnections()
	{
		System.out.println("[Connections]");
		this.connections.forEach((uuid, connection) -> connection.printConnection());
	}
	
	public final boolean sendStargateMessage(AbstractStargateEntity<?> sendingStargate, UUID uuid, String messsage)
	{
		if(hasConnection(uuid))
		{
			this.connections.get(uuid).sendStargateMessage(server, sendingStargate.get9ChevronAddress(), messsage);
			return true;
		}
		else
			return false;
	}
	
	public final void sendStargateTransmission(AbstractStargateEntity<?> sendingStargate, UUID uuid, int transmissionJumps, int frequency, String transmission)
	{
		if(hasConnection(uuid))
			this.connections.get(uuid).sendStargateTransmission(server, sendingStargate.get9ChevronAddress(), transmissionJumps, frequency, transmission);
	}
	
	public final float checkStargateShieldingState(AbstractStargateEntity<?> sendingStargate, UUID uuid)
	{
		if(hasConnection(uuid))
			return this.connections.get(uuid).checkStargateShieldingState(server, sendingStargate.get9ChevronAddress());
		
		return 0;
	}
	
	
	
	private static boolean addStargatesFromChunk(ServerLevel level, int x, int z, List<AbstractStargateEntity<?>> stargates)
	{
		ChunkAccess chunk = level.getChunk(x, z);
		for(BlockPos pos : chunk.getBlockEntitiesPos())
		{
			if(level.getBlockEntity(pos) instanceof AbstractStargateEntity<?> stargate)
			{
				stargates.add(stargate);
				return true;
			}
		}
		
		return false;
	}
	
	private static void searchEdges(ServerLevel level, int xCenter, int yCenter, int radius, List<AbstractStargateEntity<?>> stargates)
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
		if(level == null)
			return;
		
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
		List<AbstractStargateEntity<?>> stargates = new ArrayList<>();
		
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
		
		for(AbstractStargateEntity<?> stargate : stargates)
		{
			stargate.onLoad();
		}
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
			connectionsTag.put(connectionID.toString(), connection.serialize());
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
		for(String connectionID : tag.getAllKeys())
		{
			try
			{
				UUID uuid = UUID.fromString(connectionID);
				StargateConnection connection = StargateConnection.deserialize(server, uuid, tag.getCompound(connectionID));
				
				if(connection != null)
					this.connections.put(uuid, connection);
			}
			catch(IllegalArgumentException ignored) {}
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
	
	
	
	private static class RegionStargates
	{
		private final ResourceKey<AddressRegion> addressRegionKey;
		
		private final List<Stargate> stargates = new ArrayList<>();
		@Nullable
		private Stargate primaryStargate = null;
		
		private RegionStargates(ResourceKey<AddressRegion> addressRegionKey)
		{
			this.addressRegionKey = addressRegionKey;
		}
		
		/** Adds Stargate to an ordered list based on the following preferences:
		 * Stargate Preferences:
		 * 1. Has DHD
		 * 2. Stargate Generation
		 * 3. The number of times the Stargate was used
		 */
		private boolean addStargate(MinecraftServer server, Stargate addedStargate)
		{
			StargateNetworkSettings settings = StargateNetworkSettings.get(server);
			Address.Immutable primaryAddress = settings.getPrimaryAddress(addressRegionKey);
			
			if(primaryAddress == null)
			{
				if(addedStargate.isPrimary(server))
				{
					settings.setPrimaryAddress(addressRegionKey,  addedStargate.get9ChevronAddress());
					this.primaryStargate = addedStargate;
				}
			}
			else if(this.primaryStargate == null && primaryAddress.equals(addedStargate.get9ChevronAddress()))
				this.primaryStargate = addedStargate;
			
			int index = Collections.binarySearch(this.stargates, addedStargate);
			if(index < 0) // Stargate was not found
			{
				this.stargates.add(-index - 1, addedStargate);
				return true;
			}
			
			return false;
		}
		
		private boolean removeStargate(Stargate stargate)
		{
			if(stargate == this.primaryStargate)
				this.primaryStargate = null;
			
			return this.stargates.remove(stargate);
		}
		
		/**
		 * @param address Address to use for the search
		 * @return Returns the Stargate based on the specified Address, or null if there is no Stargate with this Address
		 */
		@Nullable
		private Stargate findStargate(Address address)
		{
			for(Stargate stargate : this.stargates)
			{
				if(address.equals(stargate.get9ChevronAddress()))
					return stargate;
			}
			
			return null;
		}
		
		@Nullable
		private Stargate getRandomStargate(long seed)
		{
			int size = this.stargates.size();
			
			if(size < 1)
				return null;
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			return this.stargates.get(randomValue);
		}
	}
}
