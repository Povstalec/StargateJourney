package net.povstalec.sgjourney.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.HolderLookup;
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
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Galaxy;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.SolarSystem;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Symbols;

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

	private static final ResourceLocation MILKY_WAY = StargateJourney.sgjourneyLocation("milky_way");
	
	private static final String FILE_NAME = StargateJourney.MODID + "-universe";
	
	private static final String DIMENSIONS = "dimensions";
	private static final String SOLAR_SYSTEMS = "solar_systems";
	private static final String GALAXIES = "galaxies";
	
	private static final String NUMBERS_AND_LETTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private MinecraftServer server;

	private HashMap<Address.Immutable, SolarSystem.Serializable> solarSystems = new HashMap<Address.Immutable, SolarSystem.Serializable>();
	private HashMap<ResourceKey<Level>, SolarSystem.Serializable> dimensions = new HashMap<ResourceKey<Level>, SolarSystem.Serializable>();
	private HashMap<String, Galaxy.Serializable> galaxies = new HashMap<String, Galaxy.Serializable>();
	
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
        	
        	this.galaxies.put(galaxyEntry.getKey().location().toString(), galaxy);
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
					Galaxy.Serializable galaxy = this.galaxies.get(galaxyKey.location().toString());
					
					if(galaxy != null)
						galaxy.addPointOfOrigin(pointOfOriginKey);
				});
			}
		});
	}
	
	public ResourceKey<PointOfOrigin> getRandomPointOfOriginFromDimension(ResourceKey<Level> dimension, long seed)
	{
		Optional<Galaxy.Serializable> galaxyOptional = getGalaxyFromDimension(dimension);
		
		if(galaxyOptional.isPresent())
		{
			Galaxy.Serializable galaxy = galaxyOptional.get();
			return galaxy.getRandomPointOfOrigin(seed);
		}
		
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
			extragalacticAddress = new Address(solarSystem.getAddressArray()).immutable();
		else
		{
			int prefix = solarSystem.getSymbolPrefix();
			long seed = generateRandomAddressSeed(server, solarSystemKey.location().toString());
			extragalacticAddress = generateExtragalacticAddress(prefix <= 0 ? 1 : prefix, seed);
		}
		
		SolarSystem.Serializable networkSolarSystem = new SolarSystem.Serializable(extragalacticAddress, solarSystemKey, solarSystem);
		if(saveSolarSystem(extragalacticAddress, networkSolarSystem))
		{
			//Cycle through all Galaxies this Solar System should be in and add it to each one
			if(solarSystem.getAddresses().isPresent())
			{
				solarSystem.getAddresses().get().stream().forEach(galaxyAndAddress ->
				{
					ResourceKey<Galaxy> galaxyKey = galaxyAndAddress.getFirst();
					Galaxy.Serializable galaxy = this.galaxies.get(galaxyKey.location().toString());
					
					if(galaxy != null)
					{
						Pair<List<Integer>,Boolean> randomizableAddress = galaxyAndAddress.getSecond();
						
						Address.Immutable address;
						boolean isRandomizable = randomizableAddress.getSecond();
						
						// Either use the Datapack Address or generate a new Address
						if(!useDatapackAddresses(server) && isRandomizable)
						{
							long systemValue = generateRandomAddressSeed(server, solarSystemKey.location().toString());
							address = generateAddress(galaxyKey.location().toString(), galaxy.getSize(), systemValue);
						}
						else
							address = new Address(Address.integerListToArray(randomizableAddress.getFirst())).immutable();
						
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

		String galaxyID = StargateJourney.MODID + ":milky_way";
		Galaxy.Serializable galaxy = this.galaxies.get(galaxyID);
		
		ResourceKey<PointOfOrigin> pointOfOrigin;
		if(galaxy != null)
			pointOfOrigin = galaxy.getRandomPointOfOrigin(seed);
		else
			pointOfOrigin = PointOfOrigin.defaultPointOfOrigin();
		
		SolarSystem.Serializable solarSystem = new SolarSystem.Serializable(systemName, extragalacticAddress, 
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
			extragalacticAddress = new Address().randomAddress(prefix, 7, 36, seed).immutable();
			
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
		
		solarSystem.getDimensions().forEach((dimension) ->this.dimensions.put(dimension, solarSystem));
		
		String pointOfOrigin = solarSystem.getPointOfOrigin().location().toString();
		String symbols = solarSystem.getSymbols().location().toString();
		
		StargateJourney.LOGGER.info("Saved Solar System: " + solarSystemName + "[PoO: " + pointOfOrigin + " Symbols: " + symbols + "]");
		return true;
	}
	
	private Address.Immutable generateAddress(String galaxyID, int galaxySize, long seed)
	{
		Address.Immutable address;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			address = new Address().randomAddress(6, galaxySize, seed).immutable();
			
			if(!this.galaxies.get(galaxyID).containsSolarSystem(address))
				break;
		}
		
		return address;
	}
	
	public void addStargateToDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		Optional<SolarSystem.Serializable> solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem.isPresent())
		{
			solarSystem.get().addStargate(stargate);
			
			this.setDirty();
		}
	}
	
	public void removeStargateFromDimension(ResourceKey<Level> dimension, Stargate stargate)
	{
		Optional<SolarSystem.Serializable> solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem.isPresent())
		{
			solarSystem.get().removeStargate(stargate);
			
			this.setDirty();
		}
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
	
	public Optional<SolarSystem.Serializable> getSolarSystemFromDimension(ResourceKey<Level> dimension)
	{
		if(!this.dimensions.containsKey(dimension))
			return Optional.empty();
		
		return Optional.of(this.dimensions.get(dimension));
	}
	
	public Optional<SolarSystem.Serializable> getSolarSystemFromExtragalacticAddress(Address.Immutable extragalacticAddress)
	{
		if(!this.solarSystems.containsKey(extragalacticAddress))
			return Optional.empty();
		
		return Optional.of(this.solarSystems.get(extragalacticAddress));
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
	
	public Optional<List<ResourceKey<Level>>> getDimensionsFromSolarSystem(Address.Immutable extragalacticAddress)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystemOptional.isPresent())
			return Optional.of(solarSystemOptional.get().getDimensions());
		
		return Optional.empty();
	}
	
	public Optional<SolarSystem.Serializable> getSolarSystemInGalaxy(String galaxyID, Address.Immutable address)
	{
		if(!this.galaxies.containsKey(galaxyID))
			return Optional.empty();
		
		return this.galaxies.get(galaxyID).getSolarSystem(address);
	}
	
	public Optional<SolarSystem.Serializable> getSolarSystemFromAddress(ResourceKey<Level> dimension, Address.Immutable address)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
			return solarSystemOptional.get().getSolarSystemFromAddress(address);
		
		return Optional.empty();
	}
	
	public Optional<HashMap<Galaxy.Serializable, Address.Immutable>> getGalaxiesFromDimension(ResourceKey<Level> dimension)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
			return Optional.of(solarSystemOptional.get().getGalacticAddresses());
		
		return Optional.empty();
	}
	
	public Optional<Galaxy.Serializable> getGalaxyFromDimension(ResourceKey<Level> dimension)
	{
		Optional<HashMap<Galaxy.Serializable, Address.Immutable>> galaxiesOptional = getGalaxiesFromDimension(dimension);
		
		if(galaxiesOptional.isPresent())
		{
			HashMap<Galaxy.Serializable, Address.Immutable> galaxies = galaxiesOptional.get();
			
			if(!galaxies.isEmpty())
				return Optional.of(galaxies.entrySet().iterator().next().getKey());
		}
		
		return Optional.empty();
	}
	
	public Optional<Address.Immutable> getAddressInGalaxyFromSolarSystem(String galaxyID, SolarSystem.Serializable solarSystem)
	{
		if(this.galaxies.containsKey(galaxyID))
		{
			Galaxy.Serializable galaxy = this.galaxies.get(galaxyID);
			
			return solarSystem.getAddressFromGalaxy(galaxy);
		}
		
		return Optional.empty();
	}
	
	public Optional<Address.Immutable> getAddressInGalaxyFromDimension(String galaxyID, ResourceKey<Level> dimension)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
			return getAddressInGalaxyFromSolarSystem(galaxyID, solarSystemOptional.get());
		
		return Optional.empty();
	}
	
	public Optional<Address.Immutable> getExtragalacticAddressFromDimension(ResourceKey<Level> dimension)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
		{
			SolarSystem.Serializable solarSystem = solarSystemOptional.get();
			return Optional.of(solarSystem.getExtragalacticAddress());
		}
		
		return Optional.empty();
	}
	
	public ResourceKey<PointOfOrigin> getPointOfOrigin(ResourceKey<Level> dimension)
	{
		
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
		{
			SolarSystem.Serializable solarSystem = solarSystemOptional.get();
			return solarSystem.getPointOfOrigin();
		}
		
		return PointOfOrigin.defaultPointOfOrigin();
	}
	
	public ResourceKey<Symbols> getSymbols(ResourceKey<Level> dimension)
	{
		
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
		{
			SolarSystem.Serializable solarSystem = solarSystemOptional.get();
			return solarSystem.getSymbols();
		}
		
		return Symbols.defaultSymbols();
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
			galaxiesTag.put(galaxyID, galaxy.serialize());
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
			Address.Immutable extragalacticAddress = new Address(tag.getIntArray(dimensionString)).immutable();
			
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
			
			this.galaxies.put(galaxy.getKey().location().toString(), galaxy);
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
	
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
	{
		//tag = this.universe.copy();
		tag = serialize();
		
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
