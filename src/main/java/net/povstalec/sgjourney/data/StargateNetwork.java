package net.povstalec.sgjourney.data;

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
import net.povstalec.sgjourney.config.ServerAddressConfig;
import net.povstalec.sgjourney.stargate.Addressing;
import net.povstalec.sgjourney.stargate.Galaxy;
import net.povstalec.sgjourney.stargate.StarSystem;
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
	
//================================================================================================
	
	//TODO Reloading the Network
	/**
	 * Regenerates the Stargate Network, including the Addresses.
	 * @param level
	 */
	public void regenerateNetwork(Level level)
	{
		stargateNetwork.remove("Dimensions");
		stargateNetwork.remove("Planets");
		
		registerDimensions(level);
		registerPlanets(level);
		
		reloadNetwork(level);
	}
	
	//TODO Reloading the Network
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
		String addressString = getLocalAddress(dimension);
		String galaxyNumber = "Galaxy" + getPlanets().getCompound(dimension).getInt("Galaxy");
		if(!getPlanets().contains(dimension))
		{
			StargateJourney.LOGGER.info("Could not add Stargate to Network, because dimension is not registered in Stargate Network");
			return;
		}
		
		CompoundTag stargates = getStargates();
		CompoundTag galaxy = stargates.getCompound(galaxyNumber);
		CompoundTag localAddress = galaxy.getCompound(addressString);
		CompoundTag localStargate = new CompoundTag();
		
		//Saves info about the Stargate
		localStargate.putIntArray("Coordinates", stargate.getIntArray("Coordinates"));
		localStargate.putString("Dimension", stargate.getString("Dimension"));
		
		//Saves Stargate to planet
		localAddress.put(stargateAddress, localStargate);
		
		//Saves planet to galaxy
		galaxy.put(addressString, localAddress);
		
		
		//Saves galaxy to Stargates
		stargates.put(galaxyNumber, galaxy);
		
		//Saves Stargates to Network
		stargateNetwork.put("Stargates", stargates);
		setDirty();
		
		StargateJourney.LOGGER.info("Added Stargate " + stargateAddress + " to Stargate Network");
	}
	
	public CompoundTag getStargatesInDimension(Level level, String dimension)
	{
		String address = getLocalAddress(dimension);
		String galaxy = "Galaxy" + getPlanets().getCompound(dimension).getInt("Galaxy");
		if(!getStargates().getCompound(galaxy).contains(address))
			return new CompoundTag(); //Returns an empty CompoundTag
		return getStargates().getCompound(galaxy).getCompound(address);
	}
	
	public String getLocalAddress(String dimension)
	{
		return Addressing.addressIntArrayToString(getPlanets().getCompound(dimension).getIntArray("Address"));
	}
	
	public void removeFromNetwork(Level level, String address)
	{
		String galaxy = "Galaxy" + getPlanets().getCompound(level.dimension().location().toString()).getInt("Galaxy");
		String localAddress = getLocalAddress(level.dimension().location().toString());
		if(!getStargates().getCompound(galaxy).getCompound(localAddress).contains(address))
		{
			StargateJourney.LOGGER.info("Address " + address + " is not registered in the Stargate Network");
			return;
		}
		stargateNetwork.getCompound("Stargates").getCompound(galaxy).getCompound(localAddress).remove(address);
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
				if(ServerAddressConfig.use_datapack_addresses.get() && dataPackDimensions().contains(dimensionString))
					addPlanet(level, dimensionString);
				else if(ServerAddressConfig.generate_random_addresses.get())
					generatePlanet(level, dimensionString);
			}
		});
	}
	
	private void addPlanet(Level level, String dimension)
	{
		CompoundTag planets = getPlanets();
		CompoundTag planet = new CompoundTag();
		
        int[] address = getPlanet(level, dimension).getAddressArray();
        
		planet.putIntArray("Address", address);
		planet.putString("PointOfOrigin", getPlanet(level, dimension).getPointOfOrigin().location().toString());
		
		planet.putInt("Galaxy", getGalaxy(level, dimension).getSymbol());
		planet.putString("GalaxySymbols", getGalaxy(level, dimension).getSymbols().location().toString());
		
		planets.put(dimension, planet);
		stargateNetwork.put("Planets", planets);
		
		addAddress(address, dimension);
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
		planet.putString("PointOfOrigin", PointOfOrigin.getRandomPointOfOrigin(level).location().toString());
		
		planet.putInt("Galaxy", 11);
		planet.putString("GalaxySymbols", "sgjourney:milky_way");
		
		planets.put(dimension, planet);
		stargateNetwork.put("Planets", planets);
		
		addAddress(address, dimension);
		setDirty();
		
		StargateJourney.LOGGER.info("Registered " + dimension + 
				" [Address: " + Addressing.addressIntArrayToString(planet.getIntArray("Address")) + 
				", Point of Origin: " + planet.getString("PointOfOrigin") +
				", Galaxy: " + planet.getInt("Galaxy") +
				", Galaxy Symbols: " + planet.getString("GalaxySymbols") +
				"]");
	}
	
	public CompoundTag getAddresses()
	{
		return stargateNetwork.getCompound("Addresses").copy();
	}
	
	private void addAddress(int[] address, String dimension)
	{
		CompoundTag addresses = getAddresses();
		String addressString = Addressing.addressIntArrayToString(address);
		
		if(!addresses.contains(addressString))
			addresses.putString(addressString, dimension);
		
		stargateNetwork.put("Addresses", addresses);
		setDirty();
		
		StargateJourney.LOGGER.info("Address: " + addressString + " " + getAddresses().getString(addressString));
	}
	
	
	
	public CompoundTag dataPackDimensions()
	{
		return stargateNetwork.copy().getCompound("Dimensions");
	}
	
	public StarSystem getPlanet(Level level, String dimension)
	{
		String[] split = dataPackDimensions().getCompound(dimension).getString("Planet").split(":");
		return StarSystem.getPlanet(level, split[0], split[1]);
	}
	
	public Galaxy getGalaxy(Level level, String dimension)
	{
		String[] split = dataPackDimensions().getCompound(dimension).getString("Galaxy").split(":");
		return Galaxy.getGalaxy(level, split[0], split[1]);
	}
	
	/**
	 * Registers all of the immediately important information about each dimension mentioned in the datapack. Those mostly serve as a shortcut.
	 */
	public void registerDimensions(Level level)
	{
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<Galaxy> galaxyRegistry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
        final Registry<StarSystem> planetRegistry = registries.registryOrThrow(StarSystem.REGISTRY_KEY);
        Set<Entry<ResourceKey<Galaxy>, Galaxy>> set = galaxyRegistry.entrySet();
        
        set.forEach((galaxy) -> 
        {
        	galaxy.getValue().getPlanets().forEach((planet) ->
        	{
        		planetRegistry.get(planet).getDimensions().forEach((dimension) -> registerDimension(galaxy.getKey(), planet, dimension));
        	});
        });
        StargateJourney.LOGGER.info("Datapack dimensions loaded");
	}
	
	private void registerDimension(ResourceKey<Galaxy> galaxy, ResourceKey<StarSystem> planet, ResourceKey<Level> dimension)
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