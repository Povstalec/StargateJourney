package net.povstalec.sgjourney.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

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
	
	private static final String FILE_NAME = StargateJourney.MODID + "-universe";
	
	private static final String DIMENSIONS = "Dimensions";
	private static final String SOLAR_SYSTEMS = "SolarSystems";
	private static final String GALAXIES = "Galaxies";
	
	private static final String NUMBERS_AND_LETTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private MinecraftServer server;

	private HashMap<Address, SolarSystem.Serializable> solarSystems = new HashMap<Address, SolarSystem.Serializable>();
	private HashMap<ResourceLocation, SolarSystem.Serializable> solarSystemLocations = new HashMap<ResourceLocation, SolarSystem.Serializable>();
	private HashMap<ResourceKey<Level>, SolarSystem.Serializable> dimensions = new HashMap<ResourceKey<Level>, SolarSystem.Serializable>();
	private HashMap<ResourceLocation, Galaxy.Serializable> galaxies = new HashMap<ResourceLocation, Galaxy.Serializable>();
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	public void generateUniverseInfo(MinecraftServer server)
	{
		registerGalaxies(server);
		
		registerPointsOfOrigin(server);
		
		registerSolarSystemsFromDataPacks(server);
		if(generateRandomSolarSystems(server))
			generateAndRegisterSolarSystems(server);
		
		//addSolarSystemsToGalaxies(server);
		//addGeneratedSolarSystemsToGalaxies(server);
		
		this.setDirty();
	}
	
	public void eraseUniverseInfo()
	{
		this.galaxies.clear();
		this.dimensions.clear();
		this.solarSystems.clear();
		this.solarSystemLocations.clear();
		
		this.setDirty();
	}
	
	private boolean useDatapackAddresses(MinecraftServer server)
	{
		return StargateNetworkSettings.get(server).useDatapackAddresses();
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
	//************************************Registering Galaxies************************************
	//============================================================================================
	
	private void registerGalaxies(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		Set<Entry<ResourceKey<Galaxy>, Galaxy>> galaxySet = galaxyRegistry.entrySet();
		galaxySet.forEach((galaxyEntry) -> 
        {
        	ResourceKey<Galaxy> galaxyKey = galaxyEntry.getKey();
        	
        	Galaxy.Serializable galaxy = new Galaxy.Serializable(galaxyKey, galaxyEntry.getValue(), new HashMap<Address.Immutable, SolarSystem.Serializable>(), new ArrayList<ResourceKey<PointOfOrigin>>());
        	
        	this.galaxies.put(galaxyEntry.getKey().location(), galaxy);
        });
		StargateJourney.LOGGER.info("Galaxies registered");
	}
	
	//============================================================================================
	//********************************Registering Points of Origin********************************
	//============================================================================================
	
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
			
			if(galaxiesOptional.isPresent())
			{
				galaxiesOptional.get().stream().forEach(galaxyKey ->
				{
					Galaxy.Serializable galaxy = this.galaxies.get(galaxyKey.location());
					
					if(galaxy != null)
						galaxy.addPointOfOrigin(pointOfOriginKey);
				});
			}
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
	//*********************************Registering Solar Systems**********************************
	//============================================================================================
	
	private void registerSolarSystemsFromDataPacks(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<SolarSystem> solarSystemRegistry = registries.registryOrThrow(SolarSystem.REGISTRY_KEY);
		
		Set<Entry<ResourceKey<SolarSystem>, SolarSystem>> solarSystemSet = solarSystemRegistry.entrySet();
		
		//Goes through all datapack Solar Systems
		solarSystemSet.forEach((solarSystem) -> 
			addSolarSystemFromDataPack(server, solarSystem.getKey(), solarSystem.getValue()));
		StargateJourney.LOGGER.info("Datapack Solar Systems registered");
	}
	
	private void generateAndRegisterSolarSystems(MinecraftServer server)
	{
		Set<ResourceKey<Level>> levelSet = server.levelKeys();
		levelSet.forEach((dimension) ->
		{
			if(!this.dimensions.containsKey(dimension))
				generateNewSolarSystem(server, dimension);
		});
		StargateJourney.LOGGER.info("Solar Systems generated");
	}
	
	//============================================================================================
	//****************************************Solar System****************************************
	//============================================================================================
	
	private void addSolarSystemFromDataPack(MinecraftServer server, ResourceKey<SolarSystem> solarSystemKey, SolarSystem solarSystem)
	{
		Address.Immutable extragalacticAddress;
		
		if(useDatapackAddresses(server))
			extragalacticAddress = new Address.Immutable(solarSystem.getAddressArray());
		else
		{
			int prefix = solarSystem.getSymbolPrefix();
			long seed = generateRandomAddressSeed(server, solarSystemKey.location().toString());
			extragalacticAddress = generateExtragalacticAddress(prefix <= 0 ? 1 : prefix, seed);
		}
		
		SolarSystem.Serializable networkSolarSystem = new SolarSystem.Serializable(solarSystemKey.location(), extragalacticAddress, solarSystem);
		if(saveSolarSystem(extragalacticAddress, networkSolarSystem))
		{
			//Cycle through all Galaxies this Solar System should be in and add it to each one
			if(solarSystem.getAddresses().isPresent())
			{
				solarSystem.getAddresses().get().stream().forEach(galaxyAndAddress ->
				{
					ResourceKey<Galaxy> galaxyKey = galaxyAndAddress.getFirst();
					Galaxy.Serializable galaxy = this.galaxies.get(galaxyKey.location());
					
					if(galaxy != null)
					{
						Pair<List<Integer>,Boolean> randomizableAddress = galaxyAndAddress.getSecond();
						
						Address.Immutable address;
						boolean isRandomizable = randomizableAddress.getSecond();
						
						// Either use the Datapack Address or generate a new Address
						if(!useDatapackAddresses(server) && isRandomizable)
						{
							long systemValue = generateRandomAddressSeed(server, solarSystemKey.location().toString());
							address = generateAddress(galaxyKey.location(), galaxy.getSize(), systemValue);
						}
						else
							address = new Address.Immutable(randomizableAddress.getFirst());
						
						galaxy.addSolarSystem(address, networkSolarSystem);
			    		networkSolarSystem.addToGalaxy(galaxy, address);
					}
				});
			}
		}
	}
	
	private void generateNewSolarSystem(MinecraftServer server, ResourceKey<Level> dimension)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		Galaxy defaultGalaxy = galaxyRegistry.get(MILKY_WAY);
		
		ResourceKey<Symbols> defaultSymbols = defaultGalaxy.getDefaultSymbols();
		
		String dimensionName = dimension.location().toString();
		long seed = generateRandomAddressSeed(server, dimensionName);
		
		String systemName = generateSolarSystemName(seed);
		
		int milkyWayPrefix = 1;
		
		Address.Immutable extragalacticAddress = generateExtragalacticAddress(milkyWayPrefix, seed);
		
		ResourceLocation galaxyID = new ResourceLocation(StargateJourney.MODID, "milky_way");
		Galaxy.Serializable galaxy = this.galaxies.get(galaxyID);
		
		ResourceKey<PointOfOrigin> pointOfOrigin;
		if(galaxy != null)
			pointOfOrigin = galaxy.getRandomPointOfOrigin(seed);
		else
			pointOfOrigin = PointOfOrigin.defaultPointOfOrigin();
		
		SolarSystem.Serializable solarSystem = new SolarSystem.Serializable(systemNameToResourceLocation(systemName), systemName, extragalacticAddress,
				pointOfOrigin, defaultSymbols, milkyWayPrefix, List.of(dimension));
		
		if(saveSolarSystem(extragalacticAddress, solarSystem))
		{
			// Generates a random address for the Solar System and adds it to Milky Way under that address
			long systemValue = generateRandomAddressSeed(server, solarSystem.getName());
			
			Address.Immutable address = generateAddress(galaxyID, defaultGalaxy.getType().getSize(), systemValue);
			
			if(galaxy != null)
			{
				galaxy.addSolarSystem(address, solarSystem);
				solarSystem.addToGalaxy(galaxy, address);
			}
		}
	}
	
	private String generateSolarSystemName(long seed)
	{
		int maxRandom = NUMBERS_AND_LETTERS.length();
		
		Random random = new Random(seed);
		int prefixAValue = random.nextInt(0, maxRandom);
		int prefixBValue = random.nextInt(0, maxRandom);
		
		char prefixA = NUMBERS_AND_LETTERS.charAt(prefixAValue);
		char prefixB = NUMBERS_AND_LETTERS.charAt(prefixBValue);
		
		int suffixValue = random.nextInt(1, 10000);
		
		String systemName = "P" + prefixA + prefixB + "-" + suffixValue;
		
		return systemName;
	}
	
	private ResourceLocation systemNameToResourceLocation(String systemName)
	{
		return new ResourceLocation(StargateJourney.MODID, systemName.toLowerCase());
	}
	
	private long generateRandomAddressSeed(MinecraftServer server, String name)
	{
		// Creates a number from the dimension name that works as a seed for the Solar System
		long seed = randomAddressFromSeed(server) ? server.getWorldData().worldGenOptions().seed() : 0;
		for(int i = 0; i < name.length(); i++)
		{
			seed = seed + Character.valueOf(name.charAt(i));
		}
		
		return seed;
	}
	
	private Address.Immutable generateExtragalacticAddress(int prefix, long seed)
	{
		Address.Immutable extragalacticAddress;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			extragalacticAddress = Address.Immutable.randomAddress(prefix, 7, 36, seed);
			
			if(!this.solarSystems.containsKey(extragalacticAddress))
				break;
		}
		
		return extragalacticAddress;
	}
	
	private boolean saveSolarSystem(Address.Immutable extragalacticAddress, SolarSystem.Serializable solarSystem)
	{
		String solarSystemName = solarSystem.getName();
		
		if(this.solarSystems.containsKey(extragalacticAddress))
		{
			StargateJourney.LOGGER.info("Failed to save Solar System " + solarSystemName + " as it is already saved in the Stargate Network");
			return false;
		}
		
		this.solarSystems.put(extragalacticAddress, solarSystem);
		this.solarSystemLocations.put(solarSystem.location(), solarSystem);
		
		solarSystem.getDimensions().forEach((dimension) ->this.dimensions.put(dimension, solarSystem));
		
		String pointOfOrigin = solarSystem.getPointOfOrigin().location().toString();
		String symbols = solarSystem.getSymbols().location().toString();
		
		StargateJourney.LOGGER.info("Saved Solar System: " + solarSystemName + "[PoO: " + pointOfOrigin + " Symbols: " + symbols + "]");
		return true;
	}
	
	private Address.Immutable generateAddress(ResourceLocation galaxyID, int galaxySize, long seed)
	{
		Address.Immutable address;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			address = Address.Immutable.randomAddress(6, galaxySize, seed);
			
			if(!this.galaxies.get(galaxyID).containsSolarSystem(address))
				break;
		}
		
		return address;
	}
	
	public void addStargateToDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		SolarSystem.Serializable solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem != null)
		{
			solarSystem.addStargate(server, stargate);
			this.setDirty();
		}
	}
	
	public void removeStargateFromSolarSystem(SolarSystem.Serializable solarSystem, Stargate stargate)
	{
		if(solarSystem != null)
		{
			solarSystem.removeStargate(stargate);
			this.setDirty();
		}
	}
	
	public void removeStargateFromDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		SolarSystem.Serializable solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem != null)
			removeStargateFromSolarSystem(solarSystem, stargate);
	}
	
	//============================================================================================
	//********************************************Print*******************************************
	//============================================================================================
	
	public void printDimensions()
	{
		System.out.println("[Dimensions - Solar Systems]");
		this.dimensions.entrySet().stream().forEach(dimensionEntry ->
		{
			System.out.println("- |" + dimensionEntry.getKey().location().toString() + "| = |" + dimensionEntry.getValue().getName() + "|");
		});
	}
	
	public void printSolarSystems()
	{
		System.out.println("[Solar Systems]");
		this.solarSystems.entrySet().stream().forEach(solarSystemEntry ->
		{
			SolarSystem.Serializable solarSystem = solarSystemEntry.getValue();
			System.out.println("- [Generated: " + solarSystem.isGenerated() + "] " + solarSystemEntry.getKey().toString() + " " + solarSystem.getName());
			
			solarSystem.getStargates().stream().forEach(stargate ->
			{
				System.out.println("--- " + stargate.toString());
			});
		});
	}
	
	public void printGalaxies()
	{
		System.out.println("[Galaxies]");
		this.galaxies.entrySet().stream().forEach(galaxyEntry ->
		{
			Galaxy.Serializable galaxy = galaxyEntry.getValue();
			
			System.out.println("- " + galaxyEntry.getKey().toString());
			galaxy.printSolarSystems();
		});
	}
	
	//============================================================================================
	//*******************************************Getters******************************************
	//============================================================================================
	
	@Nullable
	public SolarSystem.Serializable getSolarSystemFromDimension(ResourceKey<Level> dimension)
	{
		if(dimension == null)
			return null;
		
		return this.dimensions.get(dimension);
	}
	
	@Nullable
	public SolarSystem.Serializable getSolarSystemFromExtragalacticAddress(Address extragalacticAddress)
	{
		return this.solarSystems.get(extragalacticAddress);
	}
	
	public List<ResourceKey<Level>> getDimensionsWithGeneratedSolarSystems()
	{
		List<ResourceKey<Level>> dimensions = new ArrayList<ResourceKey<Level>>();
		
		this.dimensions.entrySet().forEach(dimensionEntry ->
		{
			if(dimensionEntry.getValue().isGenerated())
				dimensions.add(dimensionEntry.getKey());
		});
		
		return dimensions;
	}
	
	@Nullable
	public List<ResourceKey<Level>> getDimensionsFromSolarSystem(Address.Immutable extragalacticAddress)
	{
		SolarSystem.Serializable solarSystem = getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystem != null)
			return solarSystem.getDimensions();
		
		return new ArrayList<ResourceKey<Level>>();
	}
	
	@Nullable
	public SolarSystem.Serializable getSolarSystemInGalaxy(ResourceLocation galaxyID, Address.Immutable address)
	{
		if(!this.galaxies.containsKey(galaxyID))
			return null;
		
		return this.galaxies.get(galaxyID).getSolarSystem(address);
	}
	
	/**
	 * Gets Solar System in the same Galaxy as input Solar System
	 * @param solarSystem
	 * @param address
	 * @return Returns a Solar System in the same galaxy as the input Solar System based on the input Address, null if there is no such Solar System
	 */
	@Nullable
	public SolarSystem.Serializable getSolarSystemFromAddress(SolarSystem.Serializable solarSystem, Address address)
	{
		if(solarSystem != null)
			return solarSystem.getSolarSystemFromAddress(address);
		
		return null;
	}
	
	@Nullable
	public HashMap<Galaxy.Serializable, Address.Immutable> getGalaxiesFromDimension(ResourceKey<Level> dimension)
	{
		SolarSystem.Serializable solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem != null)
			return solarSystem.getGalacticAddresses();
		
		return null;
	}
	
	@Nullable
	public Galaxy.Serializable getGalaxy(ResourceLocation galaxyID)
	{
		return this.galaxies.get(galaxyID);
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
	public Address.Immutable getAddressInGalaxyFromSolarSystem(ResourceLocation galaxyID, SolarSystem.Serializable solarSystem)
	{
		if(this.galaxies.containsKey(galaxyID) && solarSystem != null)
		{
			Galaxy.Serializable galaxy = this.galaxies.get(galaxyID);
			
			return solarSystem.getAddressFromGalaxy(galaxy);
		}
		
		return null;
	}
	
	@Nullable
	public Address.Immutable getAddressInGalaxyFromDimension(ResourceLocation galaxyID, ResourceKey<Level> dimension)
	{
		SolarSystem.Serializable solarSystem = getSolarSystemFromDimension(dimension);
		
		return getAddressInGalaxyFromSolarSystem(galaxyID, solarSystem);
	}
	
	@Nullable
	public Address.Immutable getExtragalacticAddressFromDimension(ResourceKey<Level> dimension)
	{
		SolarSystem.Serializable solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem != null)
			return solarSystem.getExtragalacticAddress();
		
		return null;
	}
	
	public ResourceKey<PointOfOrigin> getPointOfOrigin(ResourceKey<Level> dimension)
	{
		
		SolarSystem.Serializable solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem != null)
			return solarSystem.getPointOfOrigin();
		
		return PointOfOrigin.defaultPointOfOrigin();
	}
	
	public ResourceKey<Symbols> getSymbols(ResourceKey<Level> dimension)
	{
		
		SolarSystem.Serializable solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem != null)
			return solarSystem.getSymbols();
		
		return Symbols.defaultSymbols();
	}
	
	public HashMap<ResourceLocation, Address.Immutable> getPrimaryStargateAddresses()
	{
		HashMap<ResourceLocation, Address.Immutable> primaryStargates = new HashMap<>();
		for(HashMap.Entry<Address, SolarSystem.Serializable> entry : solarSystems.entrySet())
		{
			Address.Immutable address = entry.getValue().primaryAddress();
			if(address != null)
				primaryStargates.put(entry.getValue().location(), address);
		}
		
		return primaryStargates;
	}
	
	public void setPrimaryStargateAddresses(HashMap<ResourceLocation, Address.Immutable> primaryStargates)
	{
		for(HashMap.Entry<ResourceLocation, Address.Immutable> entry : primaryStargates.entrySet())
		{
			SolarSystem.Serializable solarSystem = solarSystemLocations.get(entry.getKey());
			
			if(solarSystem != null)
				solarSystem.setPrimaryStargate(entry.getValue());
		}
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	private CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();

		tag.put(SOLAR_SYSTEMS, serializeSolarSystems());
		tag.put(DIMENSIONS, serializeDimensions());
		tag.put(GALAXIES, serializeGalaxies());
		
		return tag;
	}
	
	private CompoundTag serializeDimensions()
	{
		CompoundTag dimensionsTag = new CompoundTag();
		
		this.dimensions.forEach((dimension, solarSystem) ->
		{
			dimensionsTag.putIntArray(dimension.location().toString(), solarSystem.getExtragalacticAddress().toArray());
		});
		
		return dimensionsTag;
	}
	
	private CompoundTag serializeSolarSystems()
	{
		CompoundTag solarSystemsTag = new CompoundTag();
		
		this.solarSystems.forEach((extragalacticAddress, solarSystem) ->
		{
			solarSystemsTag.put(extragalacticAddress.toString(), solarSystem.serialize());
		});
		
		return solarSystemsTag;
	}
	
	private CompoundTag serializeGalaxies()
	{
		CompoundTag galaxiesTag = new CompoundTag();
		
		this.galaxies.forEach((galaxyID, galaxy) ->
		{
			galaxiesTag.put(galaxyID.toString(), galaxy.serialize());
		});
		
		return galaxiesTag;
	}
	
	private void deserialize(MinecraftServer server, CompoundTag tag)
	{
		deserializeSolarSystems(tag.getCompound(SOLAR_SYSTEMS));
		deserializeDimensions(tag.getCompound(DIMENSIONS));
		deserializeGalaxies(server, tag.getCompound(GALAXIES));
	}
	
	private void deserializeSolarSystems(CompoundTag tag)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<SolarSystem> solarSystemRegistry = registries.registryOrThrow(SolarSystem.REGISTRY_KEY);
		
		tag.getAllKeys().forEach(solarSystemString ->
		{
			SolarSystem.Serializable solarSystem = SolarSystem.Serializable.deserialize(server, solarSystemRegistry, tag.getCompound(solarSystemString));
			
			this.solarSystems.put(solarSystem.getExtragalacticAddress(), solarSystem);
		});
	}
	
	private void deserializeDimensions(CompoundTag tag)
	{
		tag.getAllKeys().forEach(dimensionString ->
		{
			Address.Immutable extragalacticAddress = new Address.Immutable(tag.getIntArray(dimensionString));
			
			if(this.solarSystems.containsKey(extragalacticAddress))
			{
				SolarSystem.Serializable solarSystem = this.solarSystems.get(extragalacticAddress);
				this.dimensions.put(Conversion.stringToDimension(dimensionString), solarSystem);
			}
		});
	}
	
	private void deserializeGalaxies(MinecraftServer server, CompoundTag tag)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		tag.getAllKeys().forEach(galaxyString ->
		{
			ResourceKey<Galaxy> galaxyKey = Conversion.stringToGalaxyKey(galaxyString);
			Galaxy.Serializable galaxy = Galaxy.Serializable.deserialize(server, this.solarSystems, galaxyRegistry, galaxyKey, tag.getCompound(galaxyString));
			
			this.galaxies.put(galaxy.getKey().location(), galaxy);
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
		//data.universe = tag.copy();
		data.deserialize(server, tag);
		
		return data;
	}
	
	public CompoundTag save(CompoundTag tag)
	{
		//tag = this.universe.copy();
		tag = serialize();
		
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
