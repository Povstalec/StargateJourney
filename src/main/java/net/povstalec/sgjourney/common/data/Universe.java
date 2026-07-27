package net.povstalec.sgjourney.common.data;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.*;
import org.jetbrains.annotations.NotNull;

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
	
	private static final String FILE_NAME = StargateJourney.MODID + "-universe";
	
	public static final String GALAXIES = "galaxies";
	public static final String ADDRESS_REGIONS = "address_regions";
	
	private static final String NUMBERS_AND_LETTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private MinecraftServer server;
	
	private final Map<ResourceKey<Galaxy>, Galaxy> galaxyKeys = new HashMap<>();
	private final Map<Integer, List<Galaxy>> galacticPrefixes = new HashMap<>();
	private final List<Galaxy> galaxiesWithGeneratedRegions = new ArrayList<>();
	
	private final Map<ResourceKey<AddressRegion>, AddressRegion> addressRegionKeys = new HashMap<>();
	private final Map<Address, AddressRegion> addressRegions = new HashMap<>();
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	public void generateUniverseInfo()
	{
		registerGalaxies();
		SpaceLocation.registerSpaceLocations(server);
		
		registerPointsOfOrigin();
		
		registerAddressRegionsFromDataPacks();
		generateAndRegisterSpaceLocationsAndAddressRegions();
		
		assignSpaceLocationsToAddressRegions();
		
		this.setDirty();
	}
	
	public void eraseUniverseInfo()
	{
		this.galaxyKeys.clear();
		this.galacticPrefixes.clear();
		this.galaxiesWithGeneratedRegions.clear();
		
		this.addressRegions.clear();
		this.addressRegionKeys.clear();
		SpaceLocation.clear();
		
		this.setDirty();
	}
	
	public boolean randomizeAddresses()
	{
		return StargateNetworkSettings.get(server).randomizeAddresses();
	}
	
	public boolean generateRandomAddressRegions()
	{
		return StargateNetworkSettings.get(server).generateRandomAddressRegions();
	}
	
	public boolean randomAddressFromSeed()
	{
		return StargateNetworkSettings.get(server).randomAddressFromSeed();
	}
	
	//============================================================================================
	//*******************************************Galaxy*******************************************
	//============================================================================================
	
	public void addGalaxy(ResourceKey<Galaxy> galaxyKey, Galaxy galaxy)
	{
		this.galaxyKeys.put(galaxyKey, galaxy);
		List<Galaxy> prefixedGalaxies = this.galacticPrefixes.computeIfAbsent(galaxy.symbolPrefix, prefix -> new ArrayList<>());
		prefixedGalaxies.add(galaxy);
		
		if(galaxy.canGenerateAddressRegions)
			galaxiesWithGeneratedRegions.add(galaxy);
	}
	
	public List<Galaxy> getGalaxiesWithPrefix(int symbolPrefix)
	{
		List<Galaxy> prefixedGalaxies = this.galacticPrefixes.get(symbolPrefix);
		
		if(prefixedGalaxies != null)
			return prefixedGalaxies;
		
		return List.of();
	}
	
	public List<Galaxy> getGalaxiesWithGeneratedRegions()
	{
		return new ArrayList<>(this.galaxiesWithGeneratedRegions);
	}
	
	private void registerGalaxies()
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		Set<Entry<ResourceKey<Galaxy>, Galaxy>> galaxySet = galaxyRegistry.entrySet();
		galaxySet.forEach((galaxyEntry) ->
				addGalaxy(galaxyEntry.getKey(), galaxyEntry.getValue().copyTemplateWithKey(galaxyEntry.getKey(), Map.of(), List.of())));
		StargateJourney.LOGGER.info("Galaxies registered");
	}
	
	//============================================================================================
	//**************************************Point of Origin***************************************
	//============================================================================================
	
	public void addPointOfOrigin(Galaxy galaxy, ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		if(galaxy != null)
			galaxy.addPointOfOrigin(pointOfOrigin);
	}
	
	private void registerPointsOfOrigin()
	{
		RegistryAccess registries = server.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		Set<Entry<ResourceKey<PointOfOrigin>, PointOfOrigin>> pointOfOriginSet = pointOfOriginRegistry.entrySet();
		
		pointOfOriginSet.forEach((pointOfOriginEntry) -> 
		{
			PointOfOrigin pointOfOrigin = pointOfOriginEntry.getValue();
			ResourceKey<PointOfOrigin> pointOfOriginKey = pointOfOriginEntry.getKey();
			
			for(ResourceKey<Galaxy> galaxyKey : pointOfOrigin.generatedGalaxies())
			{
				addPointOfOrigin(this.galaxyKeys.get(galaxyKey), pointOfOriginKey);
			}
		});
	}
	
	public ResourceKey<PointOfOrigin> getRandomPointOfOriginFromDimension(ResourceKey<Level> dimension, long seed)
	{
		Galaxy galaxy = getGalaxyFromDimension(dimension);
		
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
			AddressRegion addressRegion = this.addressRegionKeys.get(spaceLocation.getAddressRegionKey());
			if(addressRegion != null)
				spaceLocation.setAddressRegion(addressRegion);
		});
		
		this.setDirty();
	}
	
	//============================================================================================
	//***************************************Address Region***************************************
	//============================================================================================
	
	public boolean addAddressRegion(Address.Immutable extragalacticAddress, AddressRegion addressRegion)
	{
		if(this.addressRegions.containsKey(extragalacticAddress))
		{
			StargateJourney.LOGGER.error("Failed to add Address Region {} as it is already contained in the Stargate Network", addressRegion.getName());
			return false;
		}
		
		this.addressRegions.put(extragalacticAddress, addressRegion);
		this.addressRegionKeys.put(addressRegion.getResourceKey(), addressRegion);
		
		StargateJourney.LOGGER.debug("Added Address Region {}", addressRegion);
		return true;
	}
	
	public void removeAddressRegion(AddressRegion addressRegion)
	{
		if(addressRegion == null)
			return;
		
		this.addressRegions.remove(addressRegion.getExtragalacticAddress());
		this.addressRegionKeys.remove(addressRegion.getResourceKey());
	}
	
	public void removeAddressRegionFromGalaxy(ResourceKey<Galaxy> galaxyKey, AddressRegion addressRegion)
	{
		Galaxy galaxy = getGalaxy(galaxyKey);
		if(galaxy != null)
			galaxy.removeAddressRegion(addressRegion.removeFromGalaxy(galaxyKey));
	}
	
	private void registerAddressRegionsFromDataPacks()
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<AddressRegion> addressRegionRegistry = registries.registryOrThrow(AddressRegion.REGISTRY_KEY);
		
		for(Map.Entry<ResourceKey<AddressRegion>, AddressRegion> addressRegionEntry : addressRegionRegistry.entrySet())
		{
			addAddressRegionFromDataPack(addressRegionEntry.getKey(), addressRegionEntry.getValue());
		}
		StargateJourney.LOGGER.info("Datapack Address Regions registered");
	}
	
	private void generateAndRegisterSpaceLocationsAndAddressRegions()
	{
		for(ResourceKey<Level> dimension : server.levelKeys())
		{
			SpaceLocation spaceLocation = SpaceLocation.fromDimensionNullable(dimension);
			if(spaceLocation == null) // No Space Location registered for this Dimension yet
				SpaceLocation.addSpaceLocation(dimension, SpaceLocation.createNewSpaceLocation(server, dimension));
			
		}
		StargateJourney.LOGGER.info("Address Regions generated");
	}
	
	public void addAddressRegionFromDataPack(ResourceKey<AddressRegion> addressRegionKey, AddressRegion templateRegion)
	{
		Address.Immutable extragalacticAddress;
		
		if(randomizeAddresses() && templateRegion.extragalacticAddress.isRandomizable())
			extragalacticAddress = generateExtragalacticAddress(templateRegion.symbolPrefix <= 0 ? Galaxy.DEFAULT_SYMBOL_PREFIX : templateRegion.symbolPrefix, generateRandomAddressSeed(addressRegionKey.location().toString()));
		else
			extragalacticAddress = templateRegion.extragalacticAddress.address();
		
		AddressRegion addressRegion = templateRegion.copyTemplateWithKey(addressRegionKey, extragalacticAddress);
		if(addAddressRegion(extragalacticAddress, addressRegion))
		{
			// Cycle through all Galaxies this Address Region should be in and add it to each one
			for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galaxyAndAddress : addressRegion.galacticAddresses.entrySet())
			{
				Galaxy galaxy = this.galaxyKeys.get(galaxyAndAddress.getKey());
				
				if(galaxy != null)
				{
					Address.Randomizable<Address.Immutable> randomizableAddress = galaxyAndAddress.getValue();
					
					Address.Immutable galacticAddress;
					
					// Either use the Datapack Address or generate a new Address
					if(randomizeAddresses() && randomizableAddress.isRandomizable())
					{
						long systemValue = generateRandomAddressSeed(addressRegionKey.location().toString());
						galacticAddress = generateAddressInGalaxy(galaxy, galaxy.type.size(), systemValue);
					}
					else
						galacticAddress = randomizableAddress.address();
					
					galaxy.addAddressRegion(galacticAddress, addressRegion);
				}
			}
		}
	}
	
	public AddressRegion generateNewAddressRegion(ResourceKey<Level> dimension, List<Galaxy> galaxies)
	{
		long dimensionSeed = server.getWorldData().worldGenOptions().seed() + dimension.hashCode();
		Galaxy galaxy = null;
		if(!galaxies.isEmpty()) // If the list of Galaxies is not empty, choose a random Galaxy to assign this Solar System to
		{
			Random random = new Random(server.getWorldData().worldGenOptions().seed() + dimension.hashCode());
			galaxy = galaxies.get(random.nextInt(0, galaxies.size()));
		}
		
		int symbolPrefix = Galaxy.getOrGenerateSymbolPrefix(galaxy, dimensionSeed);
		
		String dimensionName = dimension.location().toString();
		
		long addressSeed = generateRandomAddressSeed(dimensionName);
		String systemName = generateDesignation(addressSeed);
		Address.Immutable extragalacticAddress = generateExtragalacticAddress(symbolPrefix, addressSeed);
		
		// Create Address Region
		AddressRegion addressRegion = new AddressRegion(designationToResourceKey(systemName), systemName, Galaxy.randomOrDefaultPointOfOrigin(galaxy, dimensionSeed), Galaxy.getOrDefaultSymbols(galaxy), symbolPrefix, extragalacticAddress);
		
		// Try assigning Address Region to a Galaxy
		if(addAddressRegion(extragalacticAddress, addressRegion) && galaxy != null)
		{
			// Generates a random address for the Address Region and adds it to Milky Way under that address
			long systemValue = generateRandomAddressSeed(addressRegion.getName());
			Address.Immutable galacticAddress = generateAddressInGalaxy(galaxy, galaxy.type.size(), systemValue);
			
			galaxy.addAddressRegion(galacticAddress, addressRegion);
		}
		
		return addressRegion;
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
		return Conversion.locationToAddressRegionKey(StargateJourney.sgjourneyLocation(designation.toLowerCase()));
	}
	
	public long generateRandomAddressSeed(String name)
	{
		// Creates a number from the dimension name that works as a seed for the Address Region
		long seed = randomAddressFromSeed() ? server.getWorldData().worldGenOptions().seed() : 0;
		return seed + name.hashCode();
	}
	
	public Address.Immutable generateExtragalacticAddress(int prefix, long seed)
	{
		Address.Immutable extragalacticAddress;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			extragalacticAddress = Address.Immutable.randomAddress(prefix, 7, Address.ADDRESS_GENERATION_SYMBOLS, seed);
			
			if(!this.addressRegions.containsKey(extragalacticAddress))
				break;
		}
		
		return extragalacticAddress;
	}
	
	public Address.Immutable generateAddressInGalaxy(Galaxy galaxy, int maxAddressSymbol, long seed)
	{
		Address.Immutable address;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			address = Address.Immutable.randomAddress(6, maxAddressSymbol, seed);
			
			if(!galaxy.containsAddressRegion(address))
				break;
		}
		
		return address;
	}
	
	//============================================================================================
	//********************************************Print*******************************************
	//============================================================================================
	
	public void printAddressRegions()
	{
		System.out.println("[Address Regions]");
		for(Map.Entry<Address, AddressRegion> addressRegionEntry : this.addressRegions.entrySet())
		{
			AddressRegion addressRegion = addressRegionEntry.getValue();
			System.out.println("- [Generated: " + addressRegion.isGenerated + "] " + addressRegionEntry.getKey().toString() + " " + addressRegion.getName());
		}
	}
	
	public void printGalaxies()
	{
		System.out.println("[Galaxies]");
		this.galaxyKeys.forEach((key, galaxy) ->
		{
			System.out.println("- " + key.location());
			galaxy.printAddressRegions();
		});
	}
	
	//============================================================================================
	//*******************************************Getters******************************************
	//============================================================================================
	
	@Nullable
	public AddressRegion getAddressRegionFromKey(ResourceKey<AddressRegion> addressRegionKey)
	{
		return this.addressRegionKeys.get(addressRegionKey);
	}
	
	@Nullable
	public ResourceKey<AddressRegion> getAddressRegionKeyFromDimension(ResourceKey<Level> dimension)
	{
		if(dimension == null)
			return null;
		
		AddressRegion addressRegion = SpaceLocation.fromDimension(server, dimension).getAddressRegion();
		if(addressRegion == null)
			return null;
			
		return addressRegion.getResourceKey();
	}
	
	@Nullable
	public AddressRegion getAddressRegionFromDimension(ResourceKey<Level> dimension)
	{
		if(dimension == null)
			return null;
		
		return SpaceLocation.fromDimension(server, dimension).getAddressRegion();
	}
	
	@Nullable
	public AddressRegion getAddressRegionFromExtragalacticAddress(Address extragalacticAddress)
	{
		return this.addressRegions.get(extragalacticAddress);
	}
	
	public List<SpaceLocation> getSpaceLocationsInAddressRegion(ResourceKey<AddressRegion> addressRegionKey)
	{
		if(this.addressRegionKeys.containsKey(addressRegionKey))
			return this.addressRegionKeys.get(addressRegionKey).getSpaceLocations();
		
		return List.of();
	}
	
	public List<ResourceKey<Level>> getDimensionsWithGeneratedAddressRegions()
	{
		List<ResourceKey<Level>> dimensions = new ArrayList<>();
		for(SpaceLocation spaceLocation : SpaceLocation.getGeneratedAddressSpaceLocations())
		{
			if(spaceLocation.generateInAddressTables() && spaceLocation.getAddressRegion() != null)
				dimensions.add(spaceLocation.getDimension());
		}
		
		return dimensions;
	}
	
	@Nullable
	public AddressRegion getAddressRegionInGalaxy(ResourceKey<Galaxy> galaxyKey, Address.Immutable address)
	{
		if(!this.galaxyKeys.containsKey(galaxyKey))
			return null;
		
		return this.galaxyKeys.get(galaxyKey).getAddressRegion(address);
	}
	
	/**
	 * Gets Address Region in the same Galaxy as input Address Region
	 * @param addressRegion Address Region to start the search from
	 * @param address Address of the Region being searched for
	 * @return Returns an Address Region in the same galaxy as the input Address Region based on the input Address, null if there is no such Address Region
	 */
	@Nullable
	public AddressRegion getSameGalaxyAddressRegion(AddressRegion addressRegion, Address address)
	{
		if(addressRegion != null)
			return addressRegion.getSameGalaxyAddressRegion(server, address);
		
		return null;
	}
	
	@Nullable
	public Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> getGalaxiesFromAddressRegionKey(ResourceKey<AddressRegion> addressRegionKey)
	{
		AddressRegion addressRegion = getAddressRegionFromKey(addressRegionKey);
		
		if(addressRegion != null)
			return addressRegion.getGalacticAddresses();
		
		return null;
	}
	
	@Nullable
	public Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> getGalaxiesFromDimension(ResourceKey<Level> dimension)
	{
		AddressRegion addressRegion = getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
			return addressRegion.getGalacticAddresses();
		
		return null;
	}
	
	public boolean hasGalaxy(ResourceKey<Galaxy> galaxyKey)
	{
		return this.galaxyKeys.containsKey(galaxyKey);
	}
	
	@Nullable
	public Galaxy getGalaxy(ResourceKey<Galaxy> galaxyKey)
	{
		return this.galaxyKeys.get(galaxyKey);
	}
	
	@Nullable
	public Galaxy getGalaxyFromAddressRegionKey(ResourceKey<AddressRegion> addressRegionKey)
	{
		Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galaxies = getGalaxiesFromAddressRegionKey(addressRegionKey);
		
		if(galaxies != null && !galaxies.isEmpty())
			return getGalaxy(galaxies.entrySet().iterator().next().getKey());
		
		return null;
	}
	
	@Nullable
	public Galaxy getGalaxyFromDimension(ResourceKey<Level> dimension)
	{
		Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galaxies = getGalaxiesFromDimension(dimension);
		
		if(galaxies != null && !galaxies.isEmpty())
			return getGalaxy(galaxies.entrySet().iterator().next().getKey());
		
		return null;
	}
	
	@Nullable
	public Address.Immutable getAddressInGalaxyFromAddressRegionKey(ResourceKey<Galaxy> galaxyKey, ResourceKey<AddressRegion> addressRegionKey)
	{
		if(!this.galaxyKeys.containsKey(galaxyKey) || addressRegionKey == null)
			return null;
		
		AddressRegion addressRegion = getAddressRegionFromKey(addressRegionKey);
		if(addressRegion == null)
			return null;
		
		return addressRegion.getAddressInGalaxy(galaxyKey);
	}
	
	@Nullable
	public Address.Immutable getAddressInGalaxyFromDimension(ResourceKey<Galaxy> galaxyKey, ResourceKey<Level> dimension)
	{
		return getAddressInGalaxyFromAddressRegionKey(galaxyKey, getAddressRegionKeyFromDimension(dimension));
	}
	
	@Nullable
	public Address.Immutable getExtragalacticAddressFromDimension(ResourceKey<Level> dimension)
	{
		AddressRegion addressRegion = getAddressRegionFromDimension(dimension);
		
		if(addressRegion != null)
			return addressRegion.getExtragalacticAddress();
		
		return null;
	}
	
	public ResourceKey<PointOfOrigin> getPointOfOrigin(ResourceKey<Level> dimension)
	{
		SpaceLocation spaceLocation = SpaceLocation.fromDimension(server, dimension);
		
		if(spaceLocation.getPointOfOrigin() != null)
			return spaceLocation.getPointOfOrigin();
		
		if(spaceLocation.getAddressRegion() != null && spaceLocation.getAddressRegion().getPointOfOrigin() != null)
			return spaceLocation.getAddressRegion().getPointOfOrigin();
		
		return PointOfOrigin.defaultPointOfOrigin();
	}
	
	public ResourceKey<Symbols> getSymbols(ResourceKey<Level> dimension)
	{
		SpaceLocation spaceLocation = SpaceLocation.fromDimension(server, dimension);
		
		if(spaceLocation.getSymbols() != null)
			return spaceLocation.getSymbols();
		
		if(spaceLocation.getAddressRegion() != null && spaceLocation.getAddressRegion().getSymbols() != null)
			return spaceLocation.getAddressRegion().getSymbols();
		
		return Symbols.defaultSymbols();
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
		
		this.galaxyKeys.forEach((galaxyKey, galaxy) -> galaxiesTag.put(galaxyKey.location().toString(), galaxy.serialize()));
		
		return galaxiesTag;
	}
	
	private void deserialize(CompoundTag tag)
	{
		deserializeAddressRegions(tag.getCompound(ADDRESS_REGIONS));
		deserializeGalaxies(tag.getCompound(GALAXIES));
	}
	
	private void deserializeAddressRegions(CompoundTag tag)
	{
		tag.getAllKeys().forEach(addressRegionString ->
		{
			AddressRegion addressRegion = AddressRegion.deserialize(server, tag.getCompound(addressRegionString));
			
			this.addressRegions.put(addressRegion.getExtragalacticAddress(), addressRegion);
			this.addressRegionKeys.put(addressRegion.getResourceKey(), addressRegion);
		});
	}
	
	private void deserializeGalaxies(CompoundTag tag)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		tag.getAllKeys().forEach(galaxyString ->
		{
			ResourceKey<Galaxy> galaxyKey = Conversion.stringToGalaxyKey(galaxyString);
			Galaxy galaxy = Galaxy.deserialize(this.addressRegions, galaxyRegistry, galaxyKey, tag.getCompound(galaxyString));
			
			if(galaxy != null)
				addGalaxy(galaxy.getResourceKey(), galaxy);
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
		data.deserialize(tag);
		
		return data;
	}
	
	public @NotNull CompoundTag save(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider)
	{
		serialize(tag);
		return tag;
	}

	public static SavedData.Factory<Universe> dataFactory(MinecraftServer server)
	{
		return new SavedData.Factory<>(() -> create(server), (tag, provider) -> load(server, tag));
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
        
        return storage.computeIfAbsent(dataFactory(server), FILE_NAME);
    }
}
