package net.povstalec.sgjourney.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

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
import net.povstalec.sgjourney.common.stargate.GalaxyType;
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

	private static final ResourceLocation MILKY_WAY = new ResourceLocation(StargateJourney.MODID, "milky_way");
	
	private static final String FILE_NAME = StargateJourney.MODID + "-universe";
	
	private static final String DIMENSIONS = "Dimensions";
	private static final String SOLAR_SYSTEMS = "SolarSystems";
	private static final String GALAXIES = "Galaxies";

	private MinecraftServer server;

	private HashMap<Address, SolarSystem.Serializable> solarSystems = new HashMap<Address, SolarSystem.Serializable>();
	private HashMap<ResourceKey<Level>, SolarSystem.Serializable> dimensions = new HashMap<ResourceKey<Level>, SolarSystem.Serializable>();
	private HashMap<String, Galaxy.Serializable> galaxies = new HashMap<String, Galaxy.Serializable>();
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	public void generateUniverseInfo(MinecraftServer server)
	{
		registerSolarSystemsFromDataPacks(server);
		if(generateRandomSolarSystems(server))
			generateAndRegisterSolarSystems(server);
		
		addSolarSystemsToGalaxies(server);
		addGeneratedSolarSystemsToGalaxies(server);
		
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
		Address extragalacticAddress;
		
		if(useDatapackAddresses(server))
			extragalacticAddress = new Address(solarSystem.getAddressArray());
		else
		{
			int prefix = solarSystem.getSymbolPrefix();
			long seed = generateRandomAddressSeed(server, solarSystemKey.location().toString());
			extragalacticAddress = generateExtragalacticAddress(prefix <= 0 ? 1 : prefix, seed);
		}
		
		saveSolarSystem(extragalacticAddress, new SolarSystem.Serializable(extragalacticAddress, solarSystemKey, solarSystem));
	}
	
	private void generateNewSolarSystem(MinecraftServer server, ResourceKey<Level> dimension)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		Galaxy defaultGalaxy = galaxyRegistry.get(MILKY_WAY);
		
		ResourceKey<Symbols> defaultSymbols = defaultGalaxy.getDefaultSymbols();
		
		String dimensionName = dimension.location().toString();
		long seed = generateRandomAddressSeed(server, dimensionName);

		Random random = new Random(seed);
		
		int prefixValue = random.nextInt(1, 100);
		int suffixValue = random.nextInt(1, 1000);
		
		String prefix = prefixValue < 10 ? "P0" + prefixValue : "P" + prefixValue;
		String systemName = prefix + "-" + suffixValue;
		
		int milkyWayPrefix = 1;
		
		Address extragalacticAddress = generateExtragalacticAddress(milkyWayPrefix, seed);
		
		ResourceKey<PointOfOrigin> pointOfOrigin = PointOfOrigin.getRandomPointOfOrigin(server, seed);
		
		SolarSystem.Serializable solarSystem = new SolarSystem.Serializable(systemName, extragalacticAddress, 
				pointOfOrigin, defaultSymbols, milkyWayPrefix, List.of(dimension));
		
		saveSolarSystem(extragalacticAddress, solarSystem);
	}
	
	protected long generateRandomAddressSeed(MinecraftServer server, String name)
	{
		// Creates a number from the dimension name that works as a seed for the Solar System
		long seed = randomAddressFromSeed(server) ? server.getWorldData().worldGenOptions().seed() : 0;
		for(int i = 0; i < name.length(); i++)
		{
			seed = seed + Character.valueOf(name.charAt(i));
		}
		
		return seed;
	}
	
	protected Address generateExtragalacticAddress(int prefix, long seed)
	{
		Address extragalacticAddress;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			extragalacticAddress = new Address().randomAddress(prefix, 7, 36, seed);
			
			if(!this.solarSystems.containsKey(extragalacticAddress))
				break;
		}
		
		return extragalacticAddress;
	}
	
	private void saveSolarSystem(Address extragalacticAddress, SolarSystem.Serializable solarSystem)
	{
		String solarSystemName = solarSystem.getName();
		
		if(this.solarSystems.containsKey(extragalacticAddress))
		{
			StargateJourney.LOGGER.info("Failed to save Solar System " + solarSystemName + " as it is already saved in the Stargate Network");
			return;
		}
		
		this.solarSystems.put(extragalacticAddress, solarSystem);
		
		solarSystem.getDimensions().forEach((dimension) ->this.dimensions.put(dimension, solarSystem));
		
		String pointOfOrigin = solarSystem.getPointOfOrigin().location().toString();
		String symbols = solarSystem.getSymbols().location().toString();
		
		StargateJourney.LOGGER.info("Saved Solar System: " + solarSystemName + "[PoO: " + pointOfOrigin + " Symbols: " + symbols + "]");
	}
	
	//============================================================================================
	//*******************************************Galaxy*******************************************
	//============================================================================================
	
	private void addSolarSystemsToGalaxies(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		final Registry<SolarSystem> solarSystemRegistry = registries.registryOrThrow(SolarSystem.REGISTRY_KEY);
		
		Set<Entry<ResourceKey<Galaxy>, Galaxy>> galaxySet = galaxyRegistry.entrySet();
		galaxySet.forEach((galaxyEntry) -> 
        {
        	ResourceKey<Galaxy> galaxyKey = galaxyEntry.getKey();
        	
        	Galaxy.Serializable galaxy = new Galaxy.Serializable(galaxyKey, galaxyEntry.getValue(), new HashMap<Address, SolarSystem.Serializable>());
        	
        	this.galaxies.put(galaxyEntry.getKey().location().toString(), galaxy);
        	
        	galaxyEntry.getValue().getSystems().forEach((systemEntry) ->
        	{
        		ResourceKey<SolarSystem> solarSystemKey = systemEntry.getFirst();
        		SolarSystem solarSystem = solarSystemRegistry.get(solarSystemKey);
        		Address extragalacticAddress = new Address(solarSystem.getAddressArray());
        		
        		if(this.solarSystems.containsKey(extragalacticAddress))
        		{
            		SolarSystem.Serializable networkSolarSystem = this.solarSystems.get(extragalacticAddress);
            		
            		// Either use the Datapack Address or generate a new Address
            		Address address;
            		if(useDatapackAddresses(server))
        				address = new Address(systemEntry.getSecond().getFirst().stream().mapToInt((integer) -> integer).toArray());
        			else
        			{
        				int size = galaxy.getSize();
        				long systemValue = generateRandomAddressSeed(server, solarSystemKey.location().toString());
        				
        				address = generateAddress(galaxyKey.location().toString(), size, systemValue);
        			}
            		
            		// Add Solar System to Galaxy
            		this.galaxies.get(galaxyKey.location().toString()).addSolarSystem(address, networkSolarSystem);
            		networkSolarSystem.addToGalaxy(galaxy, address);
        		}
        	});
        });
	}
	
	private void addGeneratedSolarSystemsToGalaxies(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		String galaxyID = MILKY_WAY.toString();
		
		Galaxy milkyWayGalaxy = galaxyRegistry.get(MILKY_WAY);
		GalaxyType type = milkyWayGalaxy.getType();
		int size = type.getSize();
		
		getDimensionsWithGeneratedSolarSystems().stream().forEach(dimension ->
		{
			Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
			
			if(solarSystemOptional.isPresent())
			{
				SolarSystem.Serializable solarSystem = solarSystemOptional.get();
				
	    		long systemValue = generateRandomAddressSeed(server, solarSystem.getName());
				Address address = generateAddress(galaxyID, size, systemValue);
				
				this.galaxies.get(galaxyID).addSolarSystem(address, solarSystem);
			}
		});
	}
	
	protected Address generateAddress(String galaxyID, int galaxySize, long seed)
	{
		Address address;
		
		for(int i = 0; true; i++)
		{
			seed += i;
			address = new Address().randomAddress(6, galaxySize, seed);
			
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
	
	public Optional<SolarSystem.Serializable> getSolarSystemFromExtragalacticAddress(Address extragalacticAddress)
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
	
	public Optional<List<ResourceKey<Level>>> getDimensionsFromSolarSystem(Address extragalacticAddress)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystemOptional.isPresent())
			return Optional.of(solarSystemOptional.get().getDimensions());
		
		return Optional.empty();
	}
	
	public Optional<SolarSystem.Serializable> getSolarSystemInGalaxy(String galaxyID, Address address)
	{
		if(!this.galaxies.containsKey(galaxyID))
			return Optional.empty();
		
		return this.galaxies.get(galaxyID).getSolarSystem(address);
	}
	
	public Optional<SolarSystem.Serializable> getSolarSystemFromAddress(ResourceKey<Level> dimension, Address address)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
			return solarSystemOptional.get().getSolarSystemFromAddress(address);
		
		return Optional.empty();
	}
	
	public Optional<HashMap<Galaxy.Serializable, Address>> getGalaxiesFromDimension(ResourceKey<Level> dimension)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
			return Optional.of(solarSystemOptional.get().getGalacticAddresses());
		
		return Optional.empty();
	}
	
	public Optional<Galaxy.Serializable> getGalaxyFromDimension(ResourceKey<Level> dimension)
	{
		Optional<HashMap<Galaxy.Serializable, Address>> galaxiesOptional = getGalaxiesFromDimension(dimension);
		
		if(galaxiesOptional.isPresent())
		{
			HashMap<Galaxy.Serializable, Address> galaxies = galaxiesOptional.get();
			
			if(!galaxies.isEmpty())
				return Optional.of(galaxies.entrySet().iterator().next().getKey());
		}
		
		return Optional.empty();
	}
	
	public Optional<Address> getAddressInGalaxyFromSolarSystem(String galaxyID, SolarSystem.Serializable solarSystem)
	{
		if(this.galaxies.containsKey(galaxyID))
		{
			Galaxy.Serializable galaxy = this.galaxies.get(galaxyID);
			
			return solarSystem.getAddressFromGalaxy(galaxy);
		}
		
		return Optional.empty();
	}
	
	public Optional<Address> getAddressInGalaxyFromDimension(String galaxyID, ResourceKey<Level> dimension)
	{
		Optional<SolarSystem.Serializable> solarSystemOptional = getSolarSystemFromDimension(dimension);
		
		if(solarSystemOptional.isPresent())
			return getAddressInGalaxyFromSolarSystem(galaxyID, solarSystemOptional.get());
		
		return Optional.empty();
	}
	
	public Optional<Address> getExtragalacticAddressFromDimension(ResourceKey<Level> dimension)
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
			Address extragalacticAddress = new Address(tag.getIntArray(dimensionString));
			
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
			Galaxy.Serializable galaxy = Galaxy.Serializable.deserialize(server, this.solarSystems, galaxyRegistry, tag.getCompound(galaxyString));
			
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
