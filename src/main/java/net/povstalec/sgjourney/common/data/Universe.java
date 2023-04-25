package net.povstalec.sgjourney.common.data;

import java.util.Set;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.init.GalaxyInit;
import net.povstalec.sgjourney.common.stargate.Addressing;
import net.povstalec.sgjourney.common.stargate.Galaxy;
import net.povstalec.sgjourney.common.stargate.GalaxyType;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.SolarSystem;

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
	
	private static final String FILE_NAME = "sgjourney-universe";
	
	private static final String DIMENSIONS = "Dimensions";
	private static final String SOLAR_SYSTEMS = "SolarSystems";
	private static final String GALAXIES = "Galaxies";
	private static final String EXTRAGALACTIC_ADDRESS_INFO = "ExtragalacticAddressInfo";
	private static final String SOLAR_SYSTEM_DIMENSIONS = "SolarSystemDimensions";
	private static final String SOLAR_SYSTEM_GALAXIES = "SolarSystemGalaxies";
	private static final String EXTRAGALACTIC_ADDRESS = "ExtragalacticAddress";
	private static final String SYMBOLS = "Symbols";
	private static final String POINT_OF_ORIGIN = "PointOfOrigin";
	private static final String GENERATED = "Generated";
	
	private static final String EMPTY = StargateJourney.EMPTY;
	
	private CompoundTag universe = new CompoundTag();
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	public void generateUniverseInfo(MinecraftServer server)
	{
		if(CommonStargateNetworkConfig.use_datapack_addresses.get())
			registerSolarSystemsFromDataPacks(server);
		if(CommonStargateNetworkConfig.generate_random_addresses.get())
			generateAndRegisterSolarSystems(server);
		addSolarSystemsToGalaxies(server);
		addGeneratedSolarSystemsToGalaxies(server);
		this.setDirty();
	}
	
	public void eraseUniverseInfo()
	{
		universe = new CompoundTag();
		this.setDirty();
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
			addSolarSystemFromDataPack(server, solarSystem.getValue(), solarSystem.getKey().location().toString()));
		StargateJourney.LOGGER.info("Datapack Solar Systems registered");
	}
	
	private void generateAndRegisterSolarSystems(MinecraftServer server)
	{
		Set<ResourceKey<Level>> levelSet = server.levelKeys();
		levelSet.forEach((dimension) ->
		{
			if(!getDimensions().contains(dimension.location().toString()))
				generateNewSolarSystem(server, dimension);
		});
		StargateJourney.LOGGER.info("Solar Systems generated");
	}
	
	//============================================================================================
	//****************************************Solar System****************************************
	//============================================================================================
	
	private void addSolarSystemFromDataPack(MinecraftServer server, SolarSystem system, String systemID)
	{
		String extragalacticAddress = Addressing.addressIntArrayToString(system.getAddressArray());
		String pointOfOrigin = system.getPointOfOrigin().location().toString();
		String symbols = system.getSymbols().location().toString();
		List<ResourceKey<Level>> dimensions = system.getDimensions();
		
		saveSolarSystem(systemID, extragalacticAddress, pointOfOrigin, symbols, dimensions, false);
	}
	
	private void generateNewSolarSystem(MinecraftServer server, ResourceKey<Level> dimension)
	{
		String name = dimension.location().toString();
		
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		Galaxy defaultGalaxy = galaxyRegistry.get(MILKY_WAY);
		String defaultSymbols = defaultGalaxy.getDefaultSymbols().location().toString();
		
		// Creates a number from the dimension name that works as a seed for the Solar System
		long seed = CommonStargateNetworkConfig.random_addresses_from_seed.get() ? server.getWorldData().worldGenOptions().seed() : 0;
		for(int i = 0; i < name.length(); i++)
		{
			seed = seed + Character.valueOf(name.charAt(i));
		}

		Random random = new Random(seed);
		
		int prefixValue = random.nextInt(1, 100);
		String prefix = prefixValue < 10 ? "P0" + prefixValue : "P" + prefixValue;
		String systemID = prefix + "-" + seed;
		
		String extragalacticAddress = "";
		
		for(int i = 0; true; i++)
		{
			seed += i;
			extragalacticAddress = Addressing.addressIntArrayToString(Addressing.randomAddress(1, 7, 39, seed));// Added prefix 1 to indicate they're in Milky Way
			
			if(!getExtragalacticAddressInfo().contains(extragalacticAddress))
				break;
		}
		
		String pointOfOrigin = PointOfOrigin.getRandomPointOfOrigin(server, seed).location().toString();
		
		List<ResourceKey<Level>> dimensions = List.of(dimension);
		
		saveSolarSystem(systemID, extragalacticAddress, pointOfOrigin, defaultSymbols, dimensions, true);
	}
	
	private void saveSolarSystem(String systemID, String extragalacticAddress, String pointOfOrigin, String symbols, List<ResourceKey<Level>> dimensions, boolean generated)
	{
		saveSolarSystemInfo(systemID, extragalacticAddress, pointOfOrigin, symbols, generated);
		saveDimensionInfo(systemID, dimensions);
		StargateJourney.LOGGER.info("Saved Solar System: " + systemID + " PoO: " + pointOfOrigin + " Symbols: " + symbols);
	}
	
	private void saveSolarSystemInfo(String systemID, String extragalacticAddress, String pointOfOrigin, String symbols, boolean generated)
	{
		CompoundTag solarSystems = getSolarSystems();
		CompoundTag solarSystem = new CompoundTag();
		ListTag dimensionList = new ListTag();
		
		//Saves an Extragalactic Address reference to the Solar System
		CompoundTag extragalacticAddresses = getExtragalacticAddressInfo();
		extragalacticAddresses.putString(extragalacticAddress, systemID);
		this.universe.put(EXTRAGALACTIC_ADDRESS_INFO, extragalacticAddresses);
		
		//Saves important info to the Solar System
		solarSystem.putString(POINT_OF_ORIGIN, pointOfOrigin);
		solarSystem.putString(SYMBOLS, symbols);
		solarSystem.putString(EXTRAGALACTIC_ADDRESS, extragalacticAddress);
		solarSystem.put(SOLAR_SYSTEM_DIMENSIONS, dimensionList);
		solarSystem.putBoolean(GENERATED, generated);
		
		solarSystems.put(systemID, solarSystem);
		this.universe.put(SOLAR_SYSTEMS, solarSystems);
		
	}
	
	private void saveDimensionInfo(String systemID, List<ResourceKey<Level>> dimensions)
	{
		dimensions.forEach(level ->
		{
			CompoundTag dimensionList = getDimensions();
			String dimension = level.location().toString();
			
			if(!dimensionList.contains(dimension))
			{
				//Save Dimension reference to the Solar System
				CompoundTag solarSystems = getSolarSystems();
				CompoundTag solarSystem = getSolarSystem(systemID);
				ListTag solarSystemDimensionList = solarSystem.getList(SOLAR_SYSTEM_DIMENSIONS, Tag.TAG_STRING);
				
				solarSystemDimensionList.add(StringTag.valueOf(dimension));
				solarSystem.put(SOLAR_SYSTEM_DIMENSIONS, solarSystemDimensionList);
				solarSystems.put(systemID, solarSystem);
				this.universe.put(SOLAR_SYSTEMS, solarSystems);
				
				//Save Solar System reference to DimensionList
				dimensionList.putString(dimension, systemID);
				this.universe.put(DIMENSIONS, dimensionList);
				StargateJourney.LOGGER.info("Registered Dimension " + dimension + " under Solar System " + systemID);
			}
			else
				StargateJourney.LOGGER.info(dimension + " is already registered in a Solar System");
		});
	}
	
	//============================================================================================
	//*******************************************Galaxy*******************************************
	//============================================================================================
	
	private void addSolarSystemsToGalaxies(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		
		Set<Entry<ResourceKey<Galaxy>, Galaxy>> galaxySet = galaxyRegistry.entrySet();
		galaxySet.forEach((galaxy) -> 
        {
        	galaxy.getValue().getSystems().forEach((system) ->
        	{
        		String galaxyID = galaxy.getKey().location().toString();
        		String systemID = system.getFirst().location().toString();
        		int[] address = system.getSecond().stream().mapToInt((integer) -> integer).toArray();
        		
        		registerGalaxyReferences(galaxyID, systemID, Addressing.addressIntArrayToString(address));
        	});
        });
	}
	
	private void addGeneratedSolarSystemsToGalaxies(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
		CompoundTag solarSystems = getSolarSystems();
		String galaxyID = MILKY_WAY.toString();
		
		Galaxy defaultGalaxy = galaxyRegistry.get(MILKY_WAY);
		ResourceKey<GalaxyType> type = defaultGalaxy.getType();
		int size = GalaxyInit.getGalaxyType(type.location()).getSize();
		
		solarSystems.getAllKeys().stream().forEach(systemID ->
		{
			boolean generated = getSolarSystem(systemID).getBoolean(GENERATED);
			if(generated)
			{
				int systemValue = 0;
				for(int i = 0; i < systemID.length(); i++)
				{
					systemValue = systemValue + Character.valueOf(systemID.charAt(i));
				}
				
				String address = "";
				
				for(int i = 0; true; i++)
				{
					systemValue += i;
					address = Addressing.addressIntArrayToString(Addressing.randomAddress(6, size, systemValue));
					
					if(!getGalaxy(galaxyID).contains(address))
						break;
				}
				
				registerGalaxyReferences(galaxyID, systemID, address);
			}
		});
	}
	
	private void registerGalaxyReferences(String galaxyID, String systemID, String address)
	{
		CompoundTag galaxies = getGalaxies();
		CompoundTag galaxy = getGalaxy(galaxyID);
		CompoundTag solarSystems = getSolarSystems();
		CompoundTag solarSystem = getSolarSystem(systemID);

		//Saves Address reference to Solar System
		ListTag galaxyList = solarSystem.getList(SOLAR_SYSTEM_GALAXIES, Tag.TAG_COMPOUND);
		CompoundTag galaxyTag = new CompoundTag();
		galaxyTag.putString(galaxyID, address);
		galaxyList.add(galaxyTag);
		solarSystem.put(SOLAR_SYSTEM_GALAXIES, galaxyList);
		solarSystems.put(systemID, solarSystem);
		this.universe.put(SOLAR_SYSTEMS, solarSystems);

		//Saves Solar System reference to Address
		galaxy.putString(address, systemID);
		galaxies.put(galaxyID, galaxy);
		this.universe.put(GALAXIES, galaxies);
		StargateJourney.LOGGER.info("Registered Solar System " + systemID + " under Galaxy " + galaxyID);
	}
	
	//============================================================================================
	//*******************************************Getters******************************************
	//============================================================================================
	
	// Dimensions
	public CompoundTag getDimensions()
	{
		return universe.copy().getCompound(DIMENSIONS);
	}
	
	public String getSolarSystemFromDimension(String dimension)
	{
		if(!getDimensions().contains(dimension))
			return EMPTY;
		return getDimensions().getString(dimension);
	}
	
	// Solar Systems
	public CompoundTag getSolarSystems()
	{
		return universe.copy().getCompound(SOLAR_SYSTEMS);
	}
	
	private CompoundTag getSolarSystem(String systemID)
	{
		if(!getSolarSystems().contains(systemID))
			return new CompoundTag();
		return getSolarSystems().getCompound(systemID);
	}
	
	public ListTag getDimensionsFromSolarSystem(String systemID)
	{
		if(getSolarSystem(systemID).isEmpty() || !getSolarSystem(systemID).contains(SOLAR_SYSTEM_DIMENSIONS))
			return new ListTag();
		return getSolarSystem(systemID).getList(SOLAR_SYSTEM_DIMENSIONS, Tag.TAG_STRING);
	}
	
	public ListTag getGalaxiesFromSolarSystem(String systemID)
	{
		if(getSolarSystem(systemID).isEmpty() || !getSolarSystem(systemID).contains(SOLAR_SYSTEM_GALAXIES))
			return new ListTag();
		return getSolarSystem(systemID).getList(SOLAR_SYSTEM_GALAXIES, Tag.TAG_COMPOUND);
	}
	
	// Galaxies
	public CompoundTag getGalaxies()
	{
		return universe.copy().getCompound(GALAXIES);
	}
	
	private CompoundTag getGalaxy(String galaxyID)
	{
		if(!getGalaxies().contains(galaxyID))
			return new CompoundTag();
		return getGalaxies().getCompound(galaxyID);
	}
	
	public String getSolarSystemInGalaxy(String galaxyID, String address)
	{
		if(getGalaxy(galaxyID).isEmpty() || !getGalaxy(galaxyID).contains(address))
			return EMPTY;
		return getGalaxy(galaxyID).getString(address);
	}
	
	public ListTag getGalaxiesFromDimension(String dimension)
	{
		String solarSystem = getSolarSystemFromDimension(dimension);
		
		if(solarSystem.equals(EMPTY))
			return new ListTag();
		return getGalaxiesFromSolarSystem(solarSystem);
	}
	
	public String getAddressInGalaxyFromDimension(String galaxy, String dimension)
	{
		String solarSystem = getSolarSystemFromDimension(dimension);
		return getAddressInGalaxyFromSolarSystem(galaxy, solarSystem);
	}
	
	public String getAddressInGalaxyFromSolarSystem(String galaxy, String solarSystem)
	{
		ListTag galaxyList = getGalaxiesFromSolarSystem(solarSystem);
		
		if(!galaxyList.isEmpty())
		{
			for(int i = 0; i < galaxyList.size(); i++)
			{
				String entry = galaxyList.getCompound(i).getAllKeys().iterator().next();
				if(entry.equals(galaxy))
				{
					String address = galaxyList.getCompound(i).getString(entry);
					return address;
				}
			}
		}
		
		return EMPTY;
	}
	
	// Extragalactic Address Info
	public CompoundTag getExtragalacticAddressInfo()
	{
		return universe.copy().getCompound(EXTRAGALACTIC_ADDRESS_INFO);
	}
	
	public String getSolarSystemFromExtragalacticAddress(String extragalacticAddress)
	{
		if(!getExtragalacticAddressInfo().contains(extragalacticAddress))
			return EMPTY;
		return getExtragalacticAddressInfo().getString(extragalacticAddress);
	}
	
	public String getExtragalacticAddressFromDimension(String dimension)
	{
		if(!getSolarSystem(getSolarSystemFromDimension(dimension)).contains(EXTRAGALACTIC_ADDRESS))
			return EMPTY;
		return getSolarSystem(getSolarSystemFromDimension(dimension)).getString(EXTRAGALACTIC_ADDRESS);
	}
	
	public String getPointOfOrigin(String dimension)
	{
		if(!getSolarSystem(getSolarSystemFromDimension(dimension)).contains(POINT_OF_ORIGIN))
			return EMPTY;
		return getSolarSystem(getSolarSystemFromDimension(dimension)).getString(POINT_OF_ORIGIN);
	}
	
	public String getSymbols(String dimension)
	{
		if(!getSolarSystem(getSolarSystemFromDimension(dimension)).contains(SYMBOLS))
			return EMPTY;
		return getSolarSystem(getSolarSystemFromDimension(dimension)).getString(SYMBOLS);
	}
	
	//============================================================================================
	//********************************************Data********************************************
	//============================================================================================

	public static Universe create()
	{
		return new Universe();
	}
	
	public static Universe load(CompoundTag tag)
	{
		Universe data = create();

		data.universe = tag.copy();
		
		return data;
	}
	
	public CompoundTag save(CompoundTag tag)
	{
		tag = this.universe.copy();
		
		return tag;
	}
	
	@Nonnull
	public static Universe get(Level level)
	{
		if(level.isClientSide)
			throw new RuntimeException("Don't access this client-side!");
		
		return Universe.get(level.getServer());
	}
	
	@Nonnull
	public static Universe get(MinecraftServer server)
    {
    	DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(Universe::load, Universe::create, FILE_NAME);
    }
}
