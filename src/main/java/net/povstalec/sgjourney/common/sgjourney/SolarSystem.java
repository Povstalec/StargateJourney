package net.povstalec.sgjourney.common.sgjourney;

import java.util.*;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import javax.annotation.Nullable;

public class SolarSystem
{
	public static final ResourceKey<Registry<SolarSystem>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "solar_system"));
	public static final Codec<ResourceKey<SolarSystem>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	private static final Codec<Pair<List<Integer>, Boolean>> ADDRESS = Codec.pair(Codec.INT.listOf().fieldOf("address").codec(), Codec.BOOL.fieldOf("randomizable").codec());
	private static final Codec<Pair<ResourceKey<Galaxy>, Pair<List<Integer>, Boolean>>> GALAXY_AND_ADDRESS = Codec.pair(Galaxy.RESOURCE_KEY_CODEC.fieldOf("galaxy").codec(), ADDRESS.fieldOf("address").codec());
	
    public static final Codec<SolarSystem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(SolarSystem::getName),
			Symbols.RESOURCE_KEY_CODEC.fieldOf("symbols").forGetter(SolarSystem::getSymbols),
			Codec.INT.fieldOf("symbol_prefix").forGetter(SolarSystem::getSymbolPrefix),
			ADDRESS.fieldOf("extragalactic_address").forGetter(SolarSystem::getExtragalacticAddress),
			GALAXY_AND_ADDRESS.listOf().optionalFieldOf("addresses").forGetter(SolarSystem::getAddresses),
			PointOfOrigin.RESOURCE_KEY_CODEC.fieldOf("point_of_origin").forGetter(SolarSystem::getPointOfOrigin),
			Level.RESOURCE_KEY_CODEC.listOf().fieldOf("dimensions").forGetter(SolarSystem::getDimensions)
			).apply(instance, SolarSystem::new));

	private final String name;
	private final ResourceKey<Symbols> symbols;
	private final int symbolPrefix;
	private final Pair<List<Integer>, Boolean> extragalactic_address;
	private final ResourceKey<PointOfOrigin> point_of_origin;
	private final Optional<List<Pair<ResourceKey<Galaxy>, Pair<List<Integer>, Boolean>>>> addresses;
	private final List<ResourceKey<Level>> dimensions;
	
	public SolarSystem(String name, ResourceKey<Symbols> symbols, int symbolPrefix, 
			Pair<List<Integer>, Boolean> extragalactic_address, 
			Optional<List<Pair<ResourceKey<Galaxy>, Pair<List<Integer>, Boolean>>>> addresses,
			ResourceKey<PointOfOrigin> point_of_origin, List<ResourceKey<Level>> dimensions)
	{
		this.name = name;
		this.symbols = symbols;
		this.symbolPrefix = symbolPrefix;
		this.extragalactic_address = extragalactic_address;
		this.point_of_origin = point_of_origin;
		this.addresses = addresses;
		this.dimensions = dimensions;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ResourceKey<Symbols> getSymbols()
	{
		return symbols;
	}
	
	public int getSymbolPrefix()
	{
		return symbolPrefix;
	}
	
	public Pair<List<Integer>, Boolean> getExtragalacticAddress()
	{
		return extragalactic_address;
	}
	
	public int[] getAddressArray()
	{
		return  extragalactic_address.getFirst().stream().mapToInt((integer) -> integer).toArray();
	}
	
	public boolean isAddressRandomizable()
	{
		return extragalactic_address.getSecond();
	}
	
	public Optional<List<Pair<ResourceKey<Galaxy>, Pair<List<Integer>, Boolean>>>> getAddresses()
	{
		return addresses;
	}
	
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		return point_of_origin;
	}
	
	public List<ResourceKey<Level>> getDimensions()
	{
		return dimensions;
	}
	
	public static SolarSystem getSolarSystem(Level level, String part1, String part2)
	{
        return getSolarSystem(level, new ResourceLocation(part1, part2));
	}
	
	public static SolarSystem getSolarSystem(Level level, ResourceLocation solarSystem)
	{
		RegistryAccess registries = level.getServer().registryAccess();
        Registry<SolarSystem> registry = registries.registryOrThrow(SolarSystem.REGISTRY_KEY);
        
        return registry.get(solarSystem);
	}
	
	/**
	 * Version of Solar System used for Stargate Network and Universe
	 * @author Povstalec
	 *
	 */
	public static class Serializable
	{
		public static final String PRIMARY_STARGATE = "primary_stargate";
		
		public static final String LOCATION = "location";
		
		public static final String NAME = "name";
		
		public static final String POINT_OF_ORIGIN = "point_of_origin";
		public static final String SYMBOLS = "symbols";
		public static final String SYMBOL_PREFIX = "symbol_prefix";
		public static final String EXTRAGALACTIC_ADDRESS = "extragalactic_address";
		public static final String DIMENSIONS = "dimensions";
	    
	    private static final boolean DHD_PREFERENCE = !CommonStargateNetworkConfig.disable_dhd_preference.get();

		private final ResourceLocation location;
		
		private final String name;
		private final boolean isGenerated;
		
		private final ResourceKey<PointOfOrigin> pointOfOrigin;
		private final ResourceKey<Symbols> symbols;
		private final int symbolPrefix;
		private final Address.Immutable extragalacticAddress;
		private final List<ResourceKey<Level>> dimensions;
		
		private HashMap<Galaxy.Serializable, Address.Immutable> galacticAddresses = new HashMap<Galaxy.Serializable, Address.Immutable>();
		private List<Stargate> stargates = new ArrayList<Stargate>();
		
		@Nullable
		private Address.Immutable primaryAddress = null;
		@Nullable
		private Stargate primaryStargate = null;
		
		public Serializable(ResourceLocation location, Address.Immutable extragalacticAddress, SolarSystem solarSystem)
		{
			this.location = location;
			
			this.name = solarSystem.getName();
			this.isGenerated = false;
			
			this.pointOfOrigin = solarSystem.getPointOfOrigin();
			this.symbols = solarSystem.getSymbols();
			this.symbolPrefix = solarSystem.getSymbolPrefix();
			this.extragalacticAddress = extragalacticAddress;
			this.dimensions = solarSystem.getDimensions();
		}
		
		public Serializable(ResourceLocation location, String name, Address.Immutable extragalacticAddress,
				ResourceKey<PointOfOrigin> pointOfOrigin, ResourceKey<Symbols> symbols, 
				int symbolPrefix, List<ResourceKey<Level>> dimensions)
		{
			this.location = location;
			
			this.name = name;
			this.isGenerated = true;

			this.extragalacticAddress = extragalacticAddress;
			this.pointOfOrigin = pointOfOrigin;
			this.symbols = symbols;
			this.symbolPrefix = symbolPrefix;
			
			this.dimensions = dimensions;
		}
		
		public ResourceLocation location()
		{
			return this.location;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public Component getTranslatedName()
		{
			if(!this.isGenerated)
				return Component.translatable(this.name);
			
			return Component.literal(this.name);
		}
		
		public ResourceKey<PointOfOrigin> getPointOfOrigin()
		{
			return pointOfOrigin;
		}
		
		public ResourceKey<Symbols> getSymbols()
		{
			return symbols;
		}
		
		public int getSymbolPrefix()
		{
			return symbolPrefix;
		}
		
		public Address.Immutable getExtragalacticAddress()
		{
			return extragalacticAddress;
		}
		
		public List<ResourceKey<Level>> getDimensions()
		{
			return dimensions;
		}
		
		public boolean isGenerated()
		{
			return this.isGenerated;
		}
		
		public HashMap<Galaxy.Serializable, Address.Immutable> getGalacticAddresses()
		{
			return this.galacticAddresses;
		}
		
		public void addToGalaxy(Galaxy.Serializable galaxy, Address.Immutable address)
		{
			this.galacticAddresses.put(galaxy, address);
		}
		
		@Nullable
		public Address.Immutable getAddressFromGalaxy(Galaxy.Serializable galaxy)
		{
			if(galaxy == null)
				return null;
			
			return this.galacticAddresses.get(galaxy);
		}
		
		/**
		 * Finds a common Galaxy for this Solar System and another specified Solar System
		 * @param other Other Solar System
		 * @return A common Galaxy in which both this and the other Solar System are located, otherwise null
		 */
		@Nullable
		public Galaxy.Serializable findCommonGalaxy(SolarSystem.Serializable other)
		{
			if(other == null)
				return null;
			
			for(Map.Entry<Galaxy.Serializable, Address.Immutable> entry : this.galacticAddresses.entrySet())
			{
				for(Map.Entry<Galaxy.Serializable, Address.Immutable> otherEntry : other.galacticAddresses.entrySet())
				{
					if(entry.getKey().equals(otherEntry.getKey()))
						return entry.getKey();
				}
			}
			
			return null;
		}
		
		/**
		 * Returns a Solar System that's in the same galaxy as this Solar System and uses the specified Address
		 * @param address
		 * @return
		 */
		@Nullable
		public SolarSystem.Serializable getSolarSystemFromAddress(Address address)
		{
			for(Map.Entry<Galaxy.Serializable, Address.Immutable> entry : this.galacticAddresses.entrySet())
			{
				if(entry.getKey().containsSolarSystem(address))
					return entry.getKey().getSolarSystem(address);
			}
			
			return null;
		}
		
		public List<Stargate> getStargates()
		{
			return this.stargates;
		}
		
		/** Adds Stargate to an ordered list based on the following preferences:
		 * Stargate Preferences:
		 * 1. Has DHD
		 * 2. Stargate Generation
		 * 3. The amount of times the Stargate was used
		 */
		public void addStargate(MinecraftServer server, Stargate addedStargate)
		{
			if(this.primaryAddress == null)
			{
				if(addedStargate.isPrimary(server))
				{
					this.primaryAddress = addedStargate.get9ChevronAddress().clone();
					this.primaryStargate = addedStargate;
				}
			}
			else if(this.primaryStargate == null && this.primaryAddress.equals(addedStargate.get9ChevronAddress()))
				this.primaryStargate = addedStargate;
			
			if(!this.stargates.contains(addedStargate))
			{
				int i = 0;
				for(; i < this.stargates.size(); i++)
				{
					Stargate existingStargate = this.stargates.get(i);
					
					// If the gate has a DHD and the other one doesn't, end right there
					if(DHD_PREFERENCE && Boolean.compare(addedStargate.hasDHD(), existingStargate.hasDHD()) > 0)
						break;
					// If either both gates have a DHD or neither one does, continue choosing
					else if(!DHD_PREFERENCE || Boolean.compare(addedStargate.hasDHD(), existingStargate.hasDHD()) == 0)
					{
						// If the gate is of a newer generation, end right there
						if(addedStargate.getGeneration().isNewer(existingStargate.getGeneration()))
							break;
						// If the gate's generation is the same as the other one's, continue choosing
						else if(addedStargate.getGeneration() == existingStargate.getGeneration())
						{
							// If the gate has been used more times than the other gate, end right here
							if(addedStargate.getTimesOpened() > existingStargate.getTimesOpened())
								break;
						}
					}
				}
				
				this.stargates.add(i, addedStargate);
			}
		}
		
		public void removeStargate(Stargate stargate)
		{
			if(stargate == this.primaryStargate)
				this.primaryStargate = null;
			
			if(this.stargates.contains(stargate))
				this.stargates.remove(stargate);
		}
		
		@Nullable
		private Stargate findStargate(Address.Immutable primaryAddress)
		{
			for(Stargate stargate : this.stargates)
			{
				if(primaryAddress.equals(stargate.get9ChevronAddress()))
					return stargate;
			}
			
			return null;
		}
		
		@Nullable
		public boolean setPrimaryStargate(@Nullable Address.Immutable primaryAddress)
		{
			if(primaryAddress != null && primaryAddress.getType() != Address.Type.ADDRESS_9_CHEVRON)
				return false;
			
			if(primaryAddress == null)
			{
				this.primaryStargate = null;
				this.primaryAddress = null;
				
			}
			else if(primaryAddress.equals(this.primaryAddress))
				return false;
			else
			{
				this.primaryStargate = findStargate(primaryAddress);
				this.primaryAddress = primaryAddress;
			}
			
			return true;
		}
		
		@Nullable
		public Address.Immutable primaryAddress()
		{
			return this.primaryAddress;
		}
		
		@Nullable
		public Stargate primaryStargate()
		{
			return this.primaryStargate;
		}
		
		@Nullable
		public Stargate getRandomStargate(long seed)
		{
			int size = this.stargates.size();
			
			if(size < 1)
				return null;
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			Stargate randomStargate = this.stargates.get(randomValue);
			
			return randomStargate;
		}
		
		
		
		public CompoundTag serialize()
		{
			CompoundTag solarSystemTag = new CompoundTag();
			
			solarSystemTag.putString(LOCATION, this.location.toString());
			
			if(isGenerated())
			{
				solarSystemTag.putString(NAME, this.name);
				
				solarSystemTag.putString(POINT_OF_ORIGIN, this.pointOfOrigin.location().toString());
				solarSystemTag.putString(SYMBOLS, this.symbols.location().toString());
				solarSystemTag.putInt(SYMBOL_PREFIX, this.symbolPrefix);
				
				CompoundTag dimensionsTag = new CompoundTag();
				dimensions.stream().forEach(dimension ->
				{
					dimensionsTag.putString(dimension.location().toString(), dimension.location().toString());
				});
				solarSystemTag.put(DIMENSIONS, dimensionsTag);
			}
			
			// Extragalactic Address may be randomized, so it needs to always be saved
			solarSystemTag.putIntArray(EXTRAGALACTIC_ADDRESS, this.extragalacticAddress.toArray());
			
			if(this.primaryAddress != null)
				solarSystemTag.putIntArray(PRIMARY_STARGATE, this.primaryAddress.toArray());
			
			return solarSystemTag;
		}
		
		public static SolarSystem.Serializable deserialize(MinecraftServer server, Registry<SolarSystem> solarSystemRegistry, CompoundTag solarSystemTag)
		{
			SolarSystem.Serializable solarSystem;
			
			if(!solarSystemTag.contains(NAME, CompoundTag.TAG_STRING))
			{
				ResourceKey<SolarSystem> solarSystemKey = Conversion.stringToSolarSystemKey(solarSystemTag.getString(LOCATION));
				
				Address.Immutable extragalacticAddress = new Address.Immutable(solarSystemTag.getIntArray(EXTRAGALACTIC_ADDRESS));
				
				solarSystem = new SolarSystem.Serializable(solarSystemKey.location(), extragalacticAddress, solarSystemRegistry.get(solarSystemKey));
			}
			else
			{
				String translationName = solarSystemTag.getString(NAME);
				ResourceLocation location = ResourceLocation.tryParse(solarSystemTag.getString(LOCATION));
				
				ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(solarSystemTag.getString(POINT_OF_ORIGIN));
				ResourceKey<Symbols> symbols = Conversion.stringToSymbols(solarSystemTag.getString(SYMBOLS));
				int symbolPrefix = solarSystemTag.getInt(SYMBOL_PREFIX);
				Address.Immutable extragalacticAddress = new Address.Immutable(solarSystemTag.getIntArray(EXTRAGALACTIC_ADDRESS));
				
				List<ResourceKey<Level>> dimensions = new ArrayList<ResourceKey<Level>>();
				solarSystemTag.getCompound(DIMENSIONS).getAllKeys().forEach(dimensionString ->
				{
					dimensions.add(Conversion.stringToDimension(dimensionString));
				});
				
				solarSystem = new SolarSystem.Serializable(location, translationName, extragalacticAddress, pointOfOrigin, symbols, symbolPrefix, dimensions);
			}
			
			if(solarSystemTag.contains(PRIMARY_STARGATE, CompoundTag.TAG_INT_ARRAY))
				solarSystem.setPrimaryStargate(new Address.Immutable(solarSystemTag.getIntArray(PRIMARY_STARGATE)));
			
			return solarSystem;
		}
	}
}
