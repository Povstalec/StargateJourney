package net.povstalec.sgjourney.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
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
import net.povstalec.sgjourney.config.ServerStargateNetworkConfig;
import net.povstalec.sgjourney.stargate.Addressing;
import net.povstalec.sgjourney.stargate.Galaxy;
import net.povstalec.sgjourney.stargate.SolarSystem;
import net.povstalec.sgjourney.stargate.PointOfOrigin;
import net.povstalec.sgjourney.stargate.Symbols;

/**
 * 
 * @author Povstalec
 *
 */
public class StargateNetwork extends SavedData
{
	private CompoundTag stargateNetwork = new CompoundTag();
	private int version = 1;
	
//================================================================================================
	
	/**
	 * Returns the version of Stargate Network
	 * @return
	 */
	public int getVersion()
	{
		CompoundTag network = stargateNetwork.copy();
		
		if(network.contains("Version"))
			return network.getInt("Version");
		
		return 0;
	}
	
	/**
	 * Updates the Stargate Network to the current version
	 */
	public void updateVersion()
	{
		stargateNetwork.putInt("Version", version);
	}
	
	public void updateNetwork(Level level)
	{
		// Don't do anything if the Stargate Network is up to date
		if(getVersion() == version)
		{
			StargateJourney.LOGGER.info("Stargate Network is up to date (Version: " + version + ")");
			return;
		}
		
		StargateJourney.LOGGER.info("Detected an older Stargate Network version (Version: " + getVersion() + ") - updating to version " + version);
		
		regenerateNetwork(level, false);
		updateVersion();
	}
	
	/**
	 * Regenerates the Stargate Network, including the Addresses.
	 * @param level
	 */
	public void regenerateNetwork(Level level, boolean forceRegen)
	{
		if(ServerStargateNetworkConfig.auto_regenerate_network.get() || forceRegen)
		{
			stargateNetwork.remove("Dimensions");
			stargateNetwork.remove("Planets");
		}
		
		loadDimensions(level);
		registerPlanets(level);
		
		reloadNetwork(level);
	}
	
	/**
	 * Reloads all Stargates in the Stargate Network. Keeps random Addresses.
	 * @param level
	 */
	public void reloadNetwork(Level level)
	{
		stargateNetwork.remove("Stargates");
		
		CompoundTag stargates = BlockEntityList.get(level).getBlockEntities("Stargates");
		
		List<String> stargateList = stargates.getAllKeys().stream().toList();
		
		for(int i = 0; i < stargateList.size(); i++)
		{
			addToNetwork(stargateList.get(i), stargates.getCompound(stargateList.get(i)));
		}
	}
	
	//================================================================================================
	
	/**
	 * @return A CompoundTag containing all registered Stargates.
	 */
	public CompoundTag getStargates()
	{
		return stargateNetwork.getCompound("Stargates").copy();
	}
	
	public void addToNetwork(String stargateAddress, CompoundTag stargate)
	{
		String dimension = stargate.getString("Dimension");
		String planetAddress = getLocalAddress(dimension);
		String galaxyNumber = "Galaxy" + getPlanets().getCompound(dimension).getInt("Galaxy");
		if(!getPlanets().contains(dimension))
		{
			StargateJourney.LOGGER.info("Could not add Stargate to Network, because dimension is not registered in Stargate Network");
			return;
		}
		
		CompoundTag stargates = getStargates();
		CompoundTag galaxy = stargates.getCompound(galaxyNumber);
		CompoundTag localAddress = galaxy.getCompound(planetAddress);
		CompoundTag localStargate = new CompoundTag();
		
		if(!localAddress.contains("PrimaryStargate"))
			localAddress = setPrimaryStargate(galaxyNumber, planetAddress, stargateAddress);
		
		//Saves info about the Stargate
		localStargate.putIntArray("Coordinates", stargate.getIntArray("Coordinates"));
		localStargate.putString("Dimension", stargate.getString("Dimension"));
		
		//Saves Stargate to planet
		localAddress.put(stargateAddress, localStargate);
		
		//Saves planet to galaxy
		galaxy.put(planetAddress, localAddress);
		
		
		//Saves galaxy to Stargates
		stargates.put(galaxyNumber, galaxy);
		
		//Saves Stargates to Network
		stargateNetwork.put("Stargates", stargates);
		setDirty();
		
		StargateJourney.LOGGER.info("Added Stargate " + stargateAddress + " to Stargate Network");
			
	}
	
	public CompoundTag setPrimaryStargate(String galaxyNumber, String planetAddress, String stargateAddress)
	{
		CompoundTag stargates = getStargates();
		CompoundTag galaxy = stargates.getCompound(galaxyNumber);
		CompoundTag localAddress = galaxy.getCompound(planetAddress);
		
		localAddress.putString("PrimaryStargate", stargateAddress);

		galaxy.put(planetAddress, localAddress);

		stargates.put(galaxyNumber, galaxy);

		stargateNetwork.put("Stargates", stargates);
		setDirty();
		System.out.println(localAddress);
		
		StargateJourney.LOGGER.info("Registered " + stargateAddress + " as the Primary Stargate for " + planetAddress);
		
		return localAddress;
	}
	
	public String getPrimaryStargate(String galaxyNumber, String planetAddress)
	{
		CompoundTag stargates = getStargates();
		CompoundTag galaxy = stargates.getCompound(galaxyNumber);
		CompoundTag localAddress = galaxy.getCompound(planetAddress);
		System.out.println(localAddress);
		return localAddress.getString("PrimaryStargate");
	}
	
	public void removePrimaryStargate(String galaxyNumber, String planetAddress)
	{
		CompoundTag stargates = getStargates();
		CompoundTag galaxy = stargates.getCompound(galaxyNumber);
		CompoundTag localAddress = galaxy.getCompound(planetAddress);
		
		localAddress.remove("PrimaryStargate");
		
		galaxy.put(planetAddress, localAddress);
		
		stargates.put(galaxyNumber, galaxy);

		stargateNetwork.put("Stargates", stargates);
		setDirty();
		
		StargateJourney.LOGGER.info("Removed Primary Stargate for " + planetAddress);
	}
	
	public CompoundTag getStargatesInDimension(Level level, String dimension)
	{
		String address = getLocalAddress(dimension);
		String galaxyNumber = "Galaxy" + getPlanets().getCompound(dimension).getInt("Galaxy");
		if(!getStargates().getCompound(galaxyNumber).contains(address))
			return new CompoundTag(); //Returns an empty CompoundTag
		return getStargates().getCompound(galaxyNumber).getCompound(address);
	}
	
	public String getLocalAddress(String dimension)
	{
		return Addressing.addressIntArrayToString(getPlanets().getCompound(dimension).getIntArray("Address"));
	}
	
	public void removeFromNetwork(Level level, String address)
	{
		String galaxyNumber = "Galaxy" + getPlanets().getCompound(level.dimension().location().toString()).getInt("Galaxy");
		String localAddress = getLocalAddress(level.dimension().location().toString());
		CompoundTag planet = getStargates().getCompound(galaxyNumber).getCompound(localAddress);
		
		if(!planet.contains(address))
		{
			StargateJourney.LOGGER.info("Address " + address + " is not registered in the Stargate Network");
			return;
		}
		
		if(getPrimaryStargate(galaxyNumber, localAddress).equals(address))
		{
			planet.remove("PrimaryStargate");
			
			Set<String> stargateKeys = planet.getAllKeys();
			List<String> stargateList = new ArrayList<>(stargateKeys);
			if(stargateList.size() > 1)
			{
				String newPrimary = stargateList.get(1);
				setPrimaryStargate(galaxyNumber, localAddress, newPrimary);
				StargateJourney.LOGGER.info("Added Primary Stargate " + newPrimary);
			}
		}
		stargateNetwork.getCompound("Stargates").getCompound(galaxyNumber).getCompound(localAddress).remove(address);
		setDirty();
		
		StargateJourney.LOGGER.info("Removed " + address + " from Stargate Network");
	}
	
	//================================================================================================
	
	public CompoundTag getPlanets()
	{
		return stargateNetwork.getCompound("Planets").copy();
	}
	
	public String getPointOfOrigin(String dimension)
	{
		return getPlanets().getCompound(dimension).getString("PointOfOrigin");
	}
	
	public String getSymbols(String dimension)
	{
		return getPlanets().getCompound(dimension).getString("GalaxySymbols");
	}
	
	public String getSymbolCount(String dimension)
	{
		return getPlanets().getCompound(dimension).getString("GalaxySymbols");
	}
	
	public void registerPlanets(Level level)
	{
		Set<ResourceKey<Level>> levelKeys = level.getServer().levelKeys();
		levelKeys.forEach((dimension) ->
		{
			String dimensionString = dimension.location().toString();
			if(!getPlanets().contains(dimensionString))
			{
				if(ServerStargateNetworkConfig.use_datapack_addresses.get() && dataPackDimensions().contains(dimensionString))
					addPlanet(level, dimensionString);
				else if(ServerStargateNetworkConfig.generate_random_addresses.get())
					generatePlanet(level, dimensionString);
			}
		});
	}
	
	private void addPlanet(Level level, String dimension)
	{
		CompoundTag planets = getPlanets();
		CompoundTag planet = new CompoundTag();
		
		int galaxy = getGalaxy(level, dimension).getSymbol();
        int[] address = getPlanet(level, dimension).getAddressArray();
        
		planet.putIntArray("Address", address);
		planet.putString("PointOfOrigin", getPlanet(level, dimension).getPointOfOrigin().location().toString());
		
		planet.putInt("Galaxy", galaxy);
		planet.putString("GalaxySymbols", getGalaxy(level, dimension).getSymbols().location().toString());
		
		planets.put(dimension, planet);
		stargateNetwork.put("Planets", planets);
		
		addAddress(galaxy, address, dimension);
		setDirty();

		StargateJourney.LOGGER.info("Registered " + dimension + 
				" [Address: " + Addressing.addressIntArrayToString(planet.getIntArray("Address")) + 
				", Point of Origin: " + planet.getString("PointOfOrigin") +
				", Galaxy: " + planet.getInt("Galaxy") +
				", Galaxy Symbols: " + planet.getString("GalaxySymbols") +
				"]");
	}
	
	private void generatePlanet(Level level, String dimension)
	{
		CompoundTag planets = getPlanets();
		CompoundTag planet = new CompoundTag();
		
        int dimensionValue = 0;
		for(int i = 0; i < dimension.length(); i++)
		{
			dimensionValue = dimensionValue + Character.valueOf(dimension.charAt(i));
		}
        int[] address = Addressing.randomAddress(6, dimensionValue);
		planet.putIntArray("Address", address);
		planet.putString("PointOfOrigin", PointOfOrigin.getRandomPointOfOrigin(level, dimensionValue).location().toString());
		
		planet.putInt("Galaxy", 1);
		planet.putString("GalaxySymbols", "sgjourney:milky_way");
		
		planets.put(dimension, planet);
		stargateNetwork.put("Planets", planets);
		
		addAddress(1, address, dimension);
		setDirty();
		
		StargateJourney.LOGGER.info("Registered " + dimension + 
				" [Address: " + Addressing.addressIntArrayToString(planet.getIntArray("Address")) + 
				", Point of Origin: " + planet.getString("PointOfOrigin") +
				", Galaxy: " + planet.getInt("Galaxy") +
				", Galaxy Symbols: " + planet.getString("GalaxySymbols") +
				"]");
	}
	
	private CompoundTag getAddresses()
	{
		return stargateNetwork.getCompound("Addresses").copy();
	}
	
	public ResourceKey<Level> getDimensionFromAddress(int galaxyNumber, String addressString)
	{
		if(!getAddresses().getCompound("Galaxy" + galaxyNumber).contains(addressString))
			return null;
		
		String dimension = getAddresses().getCompound("Galaxy" + galaxyNumber).getString(addressString);
		return stringToDimension(dimension);
	}
	
	private void addAddress(int galaxyNumber, int[] address, String dimension)
	{
		CompoundTag addresses = getAddresses();
		CompoundTag galaxy = addresses.getCompound("Galaxy" + galaxyNumber);
		
		String addressString = Addressing.addressIntArrayToString(address);
		
		if(!galaxy.contains(addressString))
			galaxy.putString(addressString, dimension);
		
		addresses.put("Galaxy" + galaxyNumber, galaxy);
		
		stargateNetwork.put("Addresses", addresses);
		setDirty();
		
		StargateJourney.LOGGER.info("Address: " + addressString + " " + getAddresses().getString(addressString));
	}
	
	
	
	public CompoundTag dataPackDimensions()
	{
		return stargateNetwork.copy().getCompound("Dimensions");
	}
	
	public SolarSystem getPlanet(Level level, String dimension)
	{
		String[] split = dataPackDimensions().getCompound(dimension).getString("Planet").split(":");
		return SolarSystem.getPlanet(level, split[0], split[1]);
	}
	
	public Galaxy getGalaxy(Level level, String dimension)
	{
		String[] split = dataPackDimensions().getCompound(dimension).getString("Galaxy").split(":");
		return Galaxy.getGalaxy(level, split[0], split[1]);
	}
	
	/**
	 * Registers all of the immediately important information about each dimension mentioned in the datapack. Those mostly serve as a shortcut.
	 */
	public void loadDimensions(Level level)
	{
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
        final Registry<SolarSystem> planetRegistry = registries.registryOrThrow(SolarSystem.REGISTRY_KEY);
        Set<Entry<ResourceKey<Galaxy>, Galaxy>> set = galaxyRegistry.entrySet();
        
        set.forEach((galaxy) -> 
        {
        	galaxy.getValue().getPlanets().forEach((planet) ->
        	{
        		planetRegistry.get(planet).getDimensions().forEach((dimension) -> loadDimension(galaxy.getKey(), planet, dimension));
        	});
        });
        StargateJourney.LOGGER.info("Datapack dimensions loaded");
	}
	
	private void loadDimension(ResourceKey<Galaxy> galaxy, ResourceKey<SolarSystem> planet, ResourceKey<Level> dimension)
	{
		CompoundTag dimensions = dataPackDimensions();
		CompoundTag dimensionTag = new CompoundTag();
		String dimensionString = dimension.location().toString();

		dimensionTag.putString("Galaxy", galaxy.location().toString());
		dimensionTag.putString("Planet", planet.location().toString());
		
		dimensions.put(dimensionString, dimensionTag);
		
		stargateNetwork.put("Dimensions", dimensions);
		setDirty();
	}
	
	
	
	//================================================================================================
	
	public static StargateNetwork create()
	{
		return new StargateNetwork();
	}
	
	public static StargateNetwork load(CompoundTag tag)
	{
		StargateNetwork data = create();

		data.stargateNetwork = tag.copy();
		
		return data;
	}

	public CompoundTag save(CompoundTag tag)
	{
		tag = this.stargateNetwork.copy();
		
		return tag;
	}

    @Nonnull
	public static StargateNetwork get(Level level)
    {
    	MinecraftServer server = level.getServer();
    	
        if (level.isClientSide)
            throw new RuntimeException("Don't access this client-side!");
        
        DimensionDataStorage storage = server.overworld().getDataStorage();
        
        return storage.computeIfAbsent(StargateNetwork::load, StargateNetwork::create, "sgjourney-stargate_network");
    }
    
//================================================================================================
	
	public ResourceKey<Level> stringToDimension(String dimensionString)
	{
		String[] split = dimensionString.split(":");
		return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation(split[0], split[1]));
	}
	
	public ResourceKey<Level> localAddressToDimension(String address)
	{
		return stringToDimension(getStargates().getString(address));
	}
	
	public String addressUnicode(String address)
	{
		String unicode = "";
		
		String[] symbols = address.split("-");
		
		for(int i = 0; i < symbols.length; i++)
		{
			if(!symbols[i].equals(""))
			{
				int symbolNumber = Integer.parseInt(symbols[i]);
			
				unicode = unicode + Symbols.unicode(symbolNumber);
			}
		}
		
		return unicode;
	}
}