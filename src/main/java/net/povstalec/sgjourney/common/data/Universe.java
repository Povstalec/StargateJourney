package net.povstalec.sgjourney.common.data;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public class Universe extends SavedData
{
	/*
	 * What's needed:
	 * References
	 * + Solar Systems <-> Galaxy
	 * + Solar System <-> Extragalactic Address
	 * + Solar System <-> Dimensions
	 * + Solar System <-> Address
	 * Generation
	 * + Solar Systems from Data Packs
	 * + Solar Systems randomized
	 * + Randomization based on the name of the dimension
	 * - Randomization based on the seed + name of the dimension
	 * 
	 * So it should work like this:
	 * 1. We register Solar Systems from Data Packs
	 * 2. We register Solar Systems from dimensions not specified in Data Packs
	 * 3. Add Solar Systems to Galaxies (auto-generated Solar Systems should be added to a default Galaxy, a.k.a. Milky Way)
	 * 
	 */

	private static final ResourceLocation MILKY_WAY = new ResourceLocation(StargateJourney.MODID, "milky_way");
	public static final int MILKY_WAY_SYMBOL_PREFIX = 1;
	
	private static final String FILE_NAME = StargateJourney.MODID + "-universe";
	
	public static final String GALAXIES = "galaxies";
	public static final String ADDRESS_REGIONS = "address_regions";
	
	private static final String NUMBERS_AND_LETTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private MinecraftServer server;
	
	private final Map<ResourceKey<Galaxy>, Galaxy.Serializable> galaxies = new HashMap<>();
	private final Map<Integer, List<Galaxy.Serializable>> galacticPrefixes = new HashMap<>();

	private final Map<Address, AddressRegion.Serializable> addressRegions = new HashMap<>();
	private final Map<ResourceKey<AddressRegion>, AddressRegion.Serializable> addressRegionKeys = new HashMap<>();
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	public void generateUniverseInfo(MinecraftServer server)
	{
		registerGalaxies(server);
		
		registerPointsOfOrigin(server);
		
		registerAddressRegionsFromDataPacks(server);
		if(generateRandomSolarSystems(server))
			generateAndRegisterAddressRegions(server);
		
		assignSpaceLocationsToAddressRegions();
		
		this.setDirty();
	}
	
	public void eraseUniverseInfo()
	{
		this.galaxies.clear();
		
		this.addressRegions.clear();
		this.addressRegionKeys.clear();
		SpaceLocation.clearAddressRegions();
		
		this.setDirty();
	}
	
	private boolean randomizeAddresses(MinecraftServer server)
	{
		return StargateNetworkSettings.get(server).randomizeAddresses();
	}
	
	private boolean generateRandomSolarSystems(MinecraftServer server)
	{
		return StargateNetworkSettings.get(server).generateRandomSolarSystems();
	}
	
	private boolean randomAddressFromSeed(MinecraftServer server)
	{
		return StargateNetworkSettings.get(server).randomAddressFromSeed();
	}
	
	//============================================================================================
	//*******************************************Galaxy*******************************************
	//============================================================================================
	
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	public void addGalaxy(ResourceKey<Galaxy> galaxyKey, Galaxy.Serializable galaxy)
	{
		this.galaxies.put(galaxyKey, galaxy);
		List<Galaxy.Serializable> prefixedGalaxies = this.galacticPrefixes.getOrDefault(galaxy.getSymbolPrefix(), new ArrayList<>());
		prefixedGalaxies.add(galaxy);
	}
	
	public List<Galaxy.Serializable> getGalaxiesWithPrefix(int symbolPrefix)
	{
		List<Galaxy.Serializable> prefixedGalaxies = this.galacticPrefixes.get(symbolPrefix);
		
		if(prefixedGalaxies != null)
			return prefixedGalaxies;
		
		return List.of();
	}
	
	private void registerGalaxies(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		Set<Entry<ResourceKey<Galaxy>, Galaxy>> galaxySet = galaxyRegistry.entrySet();
		galaxySet.forEach((galaxyEntry) ->
        {
        	ResourceKey<Galaxy> galaxyKey = galaxyEntry.getKey();
        	
        	Galaxy.Serializable galaxy = new Galaxy.Serializable(galaxyKey, galaxyEntry.getValue(), new HashMap<>(), new ArrayList<>());
			
			addGalaxy(galaxyEntry.getKey(), galaxy);
        });
		StargateJourney.LOGGER.info("Galaxies registered");
	}
	
	//============================================================================================
	//**************************************Point of Origin***************************************
	//============================================================================================
	
	public void addPointOfOrigin(Galaxy.Serializable galaxy, ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		if(galaxy != null)
			galaxy.addPointOfOrigin(pointOfOrigin);
	}
	
	private void registerPointsOfOrigin(MinecraftServer server)
	{
		RegistryAccess registries = server.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		Set<Entry<ResourceKey<PointOfOrigin>, PointOfOrigin>> pointOfOriginSet = pointOfOriginRegistry.entrySet();
		
		pointOfOriginSet.forEach((pointOfOriginEntry) -> 
		{
			PointOfOrigin pointOfOrigin = pointOfOriginEntry.getValue();
			ResourceKey<PointOfOrigin> pointOfOriginKey = pointOfOriginEntry.getKey();
			
			Optional<List<ResourceKey<Galaxy>>> galaxiesOptional = pointOfOrigin.generatedGalaxies();
			
			galaxiesOptional.ifPresent(resourceKeys -> resourceKeys.forEach(galaxyKey ->
			{
				addPointOfOrigin(this.galaxies.get(galaxyKey), pointOfOriginKey);
			}));
		});
	}
	
	public ResourceKey<PointOfOrigin> getRandomPointOfOriginFromDimension(ResourceKey<Level> dimension, long seed)
	{
		Galaxy.Serializable galaxy = getGalaxyFromDimension(dimension);
		
		if(galaxy != null)
			return galaxy.getRandomPointOfOrigin(seed);
		
		return PointOfOrigin.defaultPointOfOrigin();
	}
	
	//============================================================================================
	//***************************************Space Location***************************************
	//============================================================================================
	
	public void assignSpaceLocationsToAddressRegions()
	{
		SpaceLocation.getDimensionSpaceLocations().forEach((dimension, spaceLocation) ->
		{
			AddressRegion.Serializable addressRegion = this.addressRegionKeys.get(spaceLocation.getAddressRegionKey());
			if(addressRegion != null)
				spaceLocation.setAddressRegion(addressRegion);
		});
		
		this.setDirty();
	}
	
	//============================================================================================
	//***************************************Address Region***************************************
	//============================================================================================
	
	public boolean addAddressRegion(Address.Immutable extragalacticAddress, AddressRegion.Serializable addressRegion)
	{
		if(this.addressRegions.containsKey(extragalacticAddress))
		{
			StargateJourney.LOGGER.error("Failed to add Address Region " + addressRegion.getName() + " as it is already contained in the Stargate Network");
			return false;
		}
		
		this.addressRegions.put(extragalacticAddress, addressRegion);
		this.addressRegionKeys.put(addressRegion.getResourceKey(), addressRegion);
		
		StargateJourney.LOGGER.debug("Added Address Region " + addressRegion);
		return true;
	}
	
	public void removeAddressRegion(AddressRegion.Serializable addressRegion)
	{
		if(addressRegion == null)
			return;
		
		this.addressRegions.remove(addressRegion.getExtragalacticAddress());
		this.addressRegionKeys.remove(addressRegion.getResourceKey());
	}
	
	public void addAddressRegionToGalaxy(Galaxy.Serializable galaxy, Address.Immutable galacticAddress, AddressRegion.Serializable addressRegion)
	{
		galaxy.addAddressRegion(galacticAddress, addressRegion);
		addressRegion.addToGalaxy(galaxy, galacticAddress);
	}
	
	public void removeAddressRegionFromGalaxy(Galaxy.Serializable galaxy, AddressRegion.Serializable addressRegion)
	{
		galaxy.removeAddressRegion(addressRegion.removeFromGalaxy(galaxy));
	}
	
	private void registerAddressRegionsFromDataPacks(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<AddressRegion> addressRegionRegistry = registries.registryOrThrow(AddressRegion.REGISTRY_KEY);
		
		for(Map.Entry<ResourceKey<AddressRegion>, AddressRegion> addressRegionEntry : addressRegionRegistry.entrySet())
		{
			addAddressRegionFromDataPack(server, addressRegionEntry.getKey(), addressRegionEntry.getValue());
		}
		StargateJourney.LOGGER.info("Datapack Address Regions registered");
	}
	
	private void generateAndRegisterAddressRegions(MinecraftServer server)
	{
		for(ResourceKey<Level> dimension : server.levelKeys())
		{
			SpaceLocation spaceLocation = SpaceLocation.fromDimension(dimension);
			if(spaceLocation.getAddressRegionKey() != null && !this.addressRegionKeys.containsKey(spaceLocation.getAddressRegionKey()))
				generateNewAddressRegion(server, dimension);
		}
		StargateJourney.LOGGER.info("Address Regions generated");
	}
	
	private void addAddressRegionFromDataPack(MinecraftServer server, ResourceKey<AddressRegion> addressRegionKey, AddressRegion addressRegion)
	{
		Address.Immutable extragalacticAddress;
		
		if(randomizeAddresses(server) && addressRegion.extragalacticAddress.getSecond())
			extragalacticAddress = generateExtragalacticAddress(addressRegion.symbolPrefix <= 0 ? 1 : addressRegion.symbolPrefix, generateRandomAddressSeed(server, addressRegionKey.location().toString()));
		else
			extragalacticAddress = addressRegion.extragalacticAddress.getFirst();
		
		AddressRegion.Serializable networkAddressRegion = new AddressRegion.Serializable(Conversion.locationToAddressRegionKey(addressRegionKey.location()), extragalacticAddress, addressRegion);
		if(addAddressRegion(extragalacticAddress, networkAddressRegion))
		{
			// Cycle through all Galaxies this Address Region should be in and add it to each one
			for(Pair<ResourceKey<Galaxy>, Pair<Address.Immutable, Boolean>> galaxyAndAddress : addressRegion.galacticAddresses)
			{
				ResourceKey<Galaxy> galaxyKey = galaxyAndAddress.getFirst();
				Galaxy.Serializable galaxy = this.galaxies.get(galaxyKey);
				
				if(galaxy != null)
				{
					Pair<Address.Immutable, Boolean> randomizableAddress = galaxyAndAddress.getSecond();
					
					Address.Immutable address;
					
					// Either use the Datapack Address or generate a new Address
					if(randomizeAddresses(server) && randomizableAddress.getSecond())
					{
						long systemValue = generateRandomAddressSeed(server, addressRegionKey.location().toString());
						address = generateAddressInGalaxy(galaxyKey, galaxy.getSize(), systemValue);
					}
					else
						address = randomizableAddress.getFirst();
					
					addAddressRegionToGalaxy(galaxy, address, networkAddressRegion);
				}
			}
		}
	}
	
	private void generateNewAddressRegion(MinecraftServer server, ResourceKey<Level> dimension)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		String dimensionName = dimension.location().toString();
		long seed = generateRandomAddressSeed(server, dimensionName);
		
		String systemName = generateDesignation(seed);
		Address.Immutable extragalacticAddress = generateExtragalacticAddress(MILKY_WAY_SYMBOL_PREFIX, seed);
		
		Galaxy milkyWayGalaxy = galaxyRegistry.get(MILKY_WAY); // Milky Way is the default galaxy
		ResourceKey<Galaxy> milkyWayKey = Conversion.locationToGalaxyKey(MILKY_WAY);
		Galaxy.Serializable galaxy = this.galaxies.get(milkyWayKey);
		
		ResourceKey<PointOfOrigin> pointOfOrigin;
		if(galaxy != null)
			pointOfOrigin = galaxy.getRandomPointOfOrigin(seed);
		else
			pointOfOrigin = PointOfOrigin.defaultPointOfOrigin();
		
		AddressRegion.Serializable addressRegion = new AddressRegion.Serializable(designationToResourceKey(systemName), systemName, pointOfOrigin, milkyWayGalaxy.getDefaultSymbols(), MILKY_WAY_SYMBOL_PREFIX, extragalacticAddress);
		
		if(addAddressRegion(extragalacticAddress, addressRegion) && galaxy != null)
		{
			// Generates a random address for the Address Region and adds it to Milky Way under that address
			long systemValue = generateRandomAddressSeed(server, addressRegion.getName());
			Address.Immutable address = generateAddressInGalaxy(milkyWayKey, milkyWayGalaxy.getType().getSize(), systemValue);
			
			addAddressRegionToGalaxy(galaxy, address, addressRegion);
		}
	}
	
	public static String generateDesignation(long seed)
	{
		int maxRandom = NUMBERS_AND_LETTERS.length();
		
		Random random = new Random(seed);
		int prefixAValue = random.nextInt(0, maxRandom);
		int prefixBValue = random.nextInt(0, maxRandom);
		
		char prefixA = NUMBERS_AND_LETTERS.charAt(prefixAValue);
		char prefixB = NUMBERS_AND_LETTERS.charAt(prefixBValue);
		
		int suffixValue = random.nextInt(1, 10000);
		
		return "P" + prefixA + prefixB + "-" + suffixValue;
	}
	
	public static ResourceKey<AddressRegion> designationToResourceKey(String designation)
	{
		return Conversion.locationToAddressRegionKey(new ResourceLocation(StargateJourney.MODID, designation.toLowerCase()));
	}
	
	public long generateRandomAddressSeed(MinecraftServer server, String name)
	{
		// Creates a number from the dimension name that works as a seed for the Address Region
		long seed = randomAddressFromSeed(server) ? server.getWorldData().worldGenOptions().seed() : 0;
		return seed + name.hashCode();
	}
	
	public Address.Immutable generateExtragalacticAddress(int prefix, long seed)
	{
		Address.Immutable extragalacticAddress;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			extragalacticAddress = Address.Immutable.randomAddress(prefix, 7, 36, seed);
			
			if(!this.addressRegions.containsKey(extragalacticAddress))
				break;
		}
		
		return extragalacticAddress;
	}
	
	public Address.Immutable generateAddressInGalaxy(ResourceKey<Galaxy> galaxyKey, int galaxySize, long seed)
	{
		Address.Immutable address;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			address = Address.Immutable.randomAddress(6, galaxySize, seed);
			
			if(!this.galaxies.get(galaxyKey).containsAddressRegion(address))
				break;
		}
		
		return address;
	}
	
	public void addStargateToDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		AddressRegion.Serializable addressRegion = getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
		{
			addressRegion.addStargate(server, stargate);
			this.setDirty();
		}
	}
	
	public void removeStargateFromAddressRegion(AddressRegion.Serializable addressRegion, Stargate stargate)
	{
		if(addressRegion != null)
		{
			addressRegion.removeStargate(stargate);
			this.setDirty();
		}
	}
	
	public void removeStargateFromDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		AddressRegion.Serializable addressRegion = getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
			removeStargateFromAddressRegion(addressRegion, stargate);
	}
	
	//============================================================================================
	//********************************************Print*******************************************
	//============================================================================================
	
	public void printAddressRegions()
	{
		System.out.println("[Address Regions]");
		for(Map.Entry<Address, AddressRegion.Serializable> addressRegionEntry : this.addressRegions.entrySet())
		{
			AddressRegion.Serializable addressRegion = addressRegionEntry.getValue();
			System.out.println("- [Generated: " + addressRegion.isGenerated() + "] " + addressRegionEntry.getKey().toString() + " " + addressRegion.getName());
			
			for(Stargate stargate : addressRegion.getStargates())
			{
				System.out.println("--- " + stargate.toString());
			}
		}
	}
	
	public void printGalaxies()
	{
		System.out.println("[Galaxies]");
		this.galaxies.entrySet().forEach(galaxyEntry ->
		{
			Galaxy.Serializable galaxy = galaxyEntry.getValue();
			
			System.out.println("- " + galaxyEntry.getKey().location().toString());
			galaxy.printAddressRegions();
		});
	}
	
	//============================================================================================
	//*******************************************Getters******************************************
	//============================================================================================
	
	@Nullable
	public AddressRegion.Serializable getAddressRegionFromDimension(ResourceKey<Level> dimension)
	{
		if(dimension == null)
			return null;
		
		return SpaceLocation.fromDimension(dimension).getAddressRegion();
	}
	
	@Nullable
	public AddressRegion.Serializable getAddressRegionFromExtragalacticAddress(Address extragalacticAddress)
	{
		return this.addressRegions.get(extragalacticAddress);
	}
	
	public List<ResourceKey<Level>> getDimensionsWithGeneratedAddressRegions()
	{
		List<ResourceKey<Level>> dimensions = new ArrayList<ResourceKey<Level>>();
		for(SpaceLocation spaceLocation : SpaceLocation.getGeneratedAddressSpaceLocations())
		{
			if(spaceLocation.appearAmongGeneratedAddresses() && spaceLocation.getAddressRegion() != null)
				dimensions.add(spaceLocation.getDimension());
		}
		
		return dimensions;
	}
	
	@Nullable
	public AddressRegion.Serializable getAddressRegionInGalaxy(ResourceKey<Galaxy> galaxyKey, Address.Immutable address)
	{
		if(!this.galaxies.containsKey(galaxyKey))
			return null;
		
		return this.galaxies.get(galaxyKey).getAddressRegion(address);
	}
	
	/**
	 * Gets Address Region in the same Galaxy as input Address Region
	 * @param addressRegion
	 * @param address
	 * @return Returns an Address Region in the same galaxy as the input Address Region based on the input Address, null if there is no such Address Region
	 */
	@Nullable
	public AddressRegion.Serializable getAddressRegionFromAddress(AddressRegion.Serializable addressRegion, Address address)
	{
		if(addressRegion != null)
			return addressRegion.getAddressRegionFromAddress(address);
		
		return null;
	}
	
	@Nullable
	public HashMap<Galaxy.Serializable, Address.Immutable> getGalaxiesFromDimension(ResourceKey<Level> dimension)
	{
		AddressRegion.Serializable addressRegion = getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
			return addressRegion.getGalacticAddresses();
		
		return null;
	}
	
	@Nullable
	public Galaxy.Serializable getGalaxy(ResourceKey<Galaxy> galaxyKey)
	{
		return this.galaxies.get(galaxyKey);
	}
	
	@Nullable
	public Galaxy.Serializable getGalaxyFromDimension(ResourceKey<Level> dimension)
	{
		HashMap<Galaxy.Serializable, Address.Immutable> galaxies = getGalaxiesFromDimension(dimension);
		
		if(galaxies != null && !galaxies.isEmpty())
			return galaxies.entrySet().iterator().next().getKey();
		
		return null;
	}
	
	@Nullable
	public Address.Immutable getAddressInGalaxyFromAddressRegion(ResourceKey<Galaxy> galaxyKey, AddressRegion.Serializable addressRegion)
	{
		if(this.galaxies.containsKey(galaxyKey) && addressRegion != null)
		{
			Galaxy.Serializable galaxy = this.galaxies.get(galaxyKey);
			
			return addressRegion.getAddressInGalaxy(galaxy);
		}
		
		return null;
	}
	
	@Nullable
	public Address.Immutable getAddressInGalaxyFromDimension(ResourceKey<Galaxy> galaxyKey, ResourceKey<Level> dimension)
	{
		return getAddressInGalaxyFromAddressRegion(galaxyKey, getAddressRegionFromDimension(dimension));
	}
	
	@Nullable
	public Address.Immutable getExtragalacticAddressFromDimension(ResourceKey<Level> dimension)
	{
		AddressRegion.Serializable addressRegion = getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
			return addressRegion.getExtragalacticAddress();
		
		return null;
	}
	
	public ResourceKey<PointOfOrigin> getPointOfOrigin(ResourceKey<Level> dimension)
	{
		SpaceLocation spaceLocation = SpaceLocation.fromDimension(dimension);
		
		if(spaceLocation.getPointOfOrigin() != null)
			return spaceLocation.getPointOfOrigin();
		
		if(spaceLocation.getAddressRegion() != null && spaceLocation.getAddressRegion().getPointOfOrigin() != null)
			return spaceLocation.getAddressRegion().getPointOfOrigin();
		
		return PointOfOrigin.defaultPointOfOrigin();
	}
	
	public ResourceKey<Symbols> getSymbols(ResourceKey<Level> dimension)
	{
		SpaceLocation spaceLocation = SpaceLocation.fromDimension(dimension);
		
		if(spaceLocation.getSymbols() != null)
			return spaceLocation.getSymbols();
		
		if(spaceLocation.getAddressRegion() != null && spaceLocation.getAddressRegion().getSymbols() != null)
			return spaceLocation.getAddressRegion().getSymbols();
		
		return Symbols.defaultSymbols();
	}
	
	public HashMap<ResourceKey<AddressRegion>, Address.Immutable> getPrimaryStargateAddresses()
	{
		HashMap<ResourceKey<AddressRegion>, Address.Immutable> primaryStargates = new HashMap<>();
		for(HashMap.Entry<Address, AddressRegion.Serializable> entry : this.addressRegions.entrySet())
		{
			Address.Immutable address = entry.getValue().primaryAddress();
			if(address != null)
				primaryStargates.put(entry.getValue().getResourceKey(), address);
		}
		
		return primaryStargates;
	}
	
	public void setPrimaryStargateAddresses(HashMap<ResourceKey<AddressRegion>, Address.Immutable> primaryStargates)
	{
		for(HashMap.Entry<ResourceKey<AddressRegion>, Address.Immutable> entry : primaryStargates.entrySet())
		{
			AddressRegion.Serializable addressRegion = this.addressRegionKeys.get(entry.getKey());
			
			if(addressRegion != null)
				addressRegion.setPrimaryStargate(entry.getValue());
		}
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private void serialize(CompoundTag tag)
	{
		tag.put(ADDRESS_REGIONS, serializeAddressRegions());
		tag.put(GALAXIES, serializeGalaxies());
	}
	
	private CompoundTag serializeAddressRegions()
	{
		CompoundTag addressRegionsTag = new CompoundTag();
		
		this.addressRegions.forEach((extragalacticAddress, addressRegion) ->
				addressRegionsTag.put(extragalacticAddress.toString(), addressRegion.serialize()));
		
		return addressRegionsTag;
	}
	
	private CompoundTag serializeGalaxies()
	{
		CompoundTag galaxiesTag = new CompoundTag();
		
		this.galaxies.forEach((galaxyKey, galaxy) ->
			galaxiesTag.put(galaxyKey.location().toString(), galaxy.serialize()));
		
		return galaxiesTag;
	}
	
	private void deserialize(MinecraftServer server, CompoundTag tag)
	{
		deserializeAddressRegions(tag.getCompound(ADDRESS_REGIONS));
		deserializeGalaxies(server, tag.getCompound(GALAXIES));
	}
	
	private void deserializeAddressRegions(CompoundTag tag)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<AddressRegion> addressRegionRegistry = registries.registryOrThrow(AddressRegion.REGISTRY_KEY);
		
		tag.getAllKeys().forEach(addressRegionString ->
		{
			AddressRegion.Serializable addressRegion = AddressRegion.Serializable.deserialize(addressRegionRegistry, tag.getCompound(addressRegionString));
			
			this.addressRegions.put(addressRegion.getExtragalacticAddress(), addressRegion);
			this.addressRegionKeys.put(addressRegion.getResourceKey(), addressRegion);
		});
	}
	
	private void deserializeGalaxies(MinecraftServer server, CompoundTag tag)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		tag.getAllKeys().forEach(galaxyString ->
		{
			ResourceKey<Galaxy> galaxyKey = Conversion.stringToGalaxyKey(galaxyString);
			Galaxy.Serializable galaxy = Galaxy.Serializable.deserialize(this.addressRegions, galaxyRegistry, galaxyKey, tag.getCompound(galaxyString));
			
			this.galaxies.put(galaxy.getKey(), galaxy);
		});
	}
	
	//============================================================================================
	//********************************************Data********************************************
	//============================================================================================
	
	public Universe(MinecraftServer server)
	{
		this.server = server;
	}
	
	public static Universe create(MinecraftServer server)
	{
		return new Universe(server);
	}
	
	public static Universe load(MinecraftServer server, CompoundTag tag)
	{
		Universe data = create(server);

		data.server = server;
		data.deserialize(server, tag);
		
		return data;
	}
	
	public CompoundTag save(CompoundTag tag)
	{
		serialize(tag);
		return tag;
	}
	
	@Nonnull
	public static Universe get(Level level)
	{
		if(level.isClientSide())
			throw new RuntimeException("Don't access this client-side!");
		
		return Universe.get(level.getServer());
	}
	
	@Nonnull
	public static Universe get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent((tag) -> load(server, tag), () -> create(server), FILE_NAME);
    }
}
