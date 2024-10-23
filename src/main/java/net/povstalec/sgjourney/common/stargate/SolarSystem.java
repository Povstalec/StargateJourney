package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

public class SolarSystem
{
	public static final ResourceKey<Registry<SolarSystem>> REGISTRY_KEY = ResourceKey.createRegistryKey(StargateJourney.sgjourneyLocation("solar_system"));
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
        return getSolarSystem(level, StargateJourney.location(part1, part2));
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
		public static final String SOLAR_SYSTEM_KEY = "SolarSystemKey";
		
		public static final String SYSTEMATIC_NAME = "SystematicName";
		
		public static final String POINT_OF_ORIGIN = "PointOfOrigin";
		public static final String SYMBOLS = "Sybmols";
		public static final String SYMBOL_PREFIX = "SybmolPrefix";
		public static final String EXTRAGALACTIC_ADDRESS = "ExtragalacticAddress";
		public static final String DIMENSIONS = "Dimensions";
	    
	    private static final boolean DHD_PREFERENCE = !CommonStargateNetworkConfig.disable_dhd_preference.get();

		private final Optional<ResourceKey<SolarSystem>> solarSystemKey;
		
		private final Optional<String> translationName;
		private final Optional<String> systematicName;
		
		private final ResourceKey<PointOfOrigin> pointOfOrigin;
		private final ResourceKey<Symbols> symbols;
		private final int symbolPrefix;
		private final Address.Immutable extragalacticAddress;
		private final List<ResourceKey<Level>> dimensions;
		
		private HashMap<Galaxy.Serializable, Address.Immutable> galacticAddresses = new HashMap<Galaxy.Serializable, Address.Immutable>();
		private List<Stargate> stargates = new ArrayList<Stargate>();
		
		public Serializable(Address.Immutable extragalacticAddress, ResourceKey<SolarSystem> solarSystemKey, SolarSystem solarSystem)
		{
			this.solarSystemKey = Optional.of(solarSystemKey);
			
			this.translationName = Optional.of(solarSystem.getName());
			this.systematicName = Optional.empty();
			
			this.pointOfOrigin = solarSystem.getPointOfOrigin();
			this.symbols = solarSystem.getSymbols();
			this.symbolPrefix = solarSystem.getSymbolPrefix();
			this.extragalacticAddress = extragalacticAddress;
			this.dimensions = solarSystem.getDimensions();
		}
		
		public Serializable(String translationName, Address.Immutable extragalacticAddress, 
				ResourceKey<PointOfOrigin> pointOfOrigin, ResourceKey<Symbols> symbols, 
				int symbolPrefix, List<ResourceKey<Level>> dimensions)
		{
			this.solarSystemKey = Optional.empty();
			
			this.translationName = Optional.empty();
			this.systematicName = Optional.of(translationName);

			this.extragalacticAddress = extragalacticAddress;
			this.pointOfOrigin = pointOfOrigin;
			this.symbols = symbols;
			this.symbolPrefix = symbolPrefix;
			
			this.dimensions = dimensions;
		}
		
		public String getName()
		{
			if(this.translationName.isPresent())
				return this.translationName.get();
			
			return this.systematicName.get();
		}
		
		public Component getTranslatedName()
		{
			if(this.translationName.isPresent())
				return Component.translatable(this.translationName.get());
			
			return Component.literal(this.systematicName.get());
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
			return this.solarSystemKey.isEmpty();
		}
		
		public HashMap<Galaxy.Serializable, Address.Immutable> getGalacticAddresses()
		{
			return this.galacticAddresses;
		}
		
		public void addToGalaxy(Galaxy.Serializable galaxy, Address.Immutable address)
		{
			this.galacticAddresses.put(galaxy, address);
		}
		
		public Optional<Address.Immutable> getAddressFromGalaxy(Galaxy.Serializable galaxy)
		{
			if(this.galacticAddresses.containsKey(galaxy))
				return Optional.of(this.galacticAddresses.get(galaxy));
			
			return Optional.empty();
		}
		
		/**
		 * Returns a Solar System that's in the same galaxy as this Solar System and uses the specified Address
		 * @param address
		 * @return
		 */
		public Optional<SolarSystem.Serializable> getSolarSystemFromAddress(Address.Immutable address)
		{
			List<SolarSystem.Serializable> solarSystems = new ArrayList<SolarSystem.Serializable>();

			this.galacticAddresses.entrySet().stream().forEach(galaxyEntry ->
			{
				Galaxy.Serializable galaxy = galaxyEntry.getKey();

				if(galaxy.containsSolarSystem(address))
				{
					Optional<SolarSystem.Serializable> solarSystemOptional = galaxy.getSolarSystem(address);
					SolarSystem.Serializable solarSystem = solarSystemOptional.get();
					solarSystems.add(solarSystem);
				}
			});
			
			if(solarSystems.size() > 0)
				return Optional.of(solarSystems.get(0));
			
			return Optional.empty();
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
		public void addStargate(Stargate addedStargate)
		{
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
			if(this.stargates.contains(stargate))
				this.stargates.remove(stargate);
		}
		
		public Optional<Stargate> getRandomStargate(long seed)
		{
			int size = this.stargates.size();
			
			if(size < 1)
				return Optional.empty();
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			Stargate randomStargate = this.stargates.get(randomValue);
			
			return Optional.of(randomStargate);
		}
		
		
		
		public CompoundTag serialize()
		{
			CompoundTag solarSystemTag = new CompoundTag();
			
			if(this.solarSystemKey.isPresent())
				solarSystemTag.putString(SOLAR_SYSTEM_KEY, this.solarSystemKey.get().location().toString());
			else
			{
				solarSystemTag.putString(SYSTEMATIC_NAME, this.systematicName.get());
				
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
			
			return solarSystemTag;
		}
		
		public static SolarSystem.Serializable deserialize(MinecraftServer server, Registry<SolarSystem> solarSystemRegistry, CompoundTag solarSystemTag)
		{
			if(solarSystemTag.contains(SOLAR_SYSTEM_KEY))
			{
				ResourceKey<SolarSystem> solarSystemKey = Conversion.stringToSolarSystemKey(solarSystemTag.getString(SOLAR_SYSTEM_KEY));
				
				SolarSystem solarSystem = solarSystemRegistry.get(solarSystemKey);
				Address.Immutable extragalacticAddress = new Address(solarSystemTag.getIntArray(EXTRAGALACTIC_ADDRESS)).immutable();
				
				return new SolarSystem.Serializable(extragalacticAddress, solarSystemKey, solarSystem);
			}
			else
			{
				String translationName = solarSystemTag.getString(SYSTEMATIC_NAME);
				
				ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(solarSystemTag.getString(POINT_OF_ORIGIN));
				ResourceKey<Symbols> symbols = Conversion.stringToSymbols(solarSystemTag.getString(SYMBOLS));
				int symbolPrefix = solarSystemTag.getInt(SYMBOL_PREFIX);
				Address.Immutable extragalacticAddress = new Address(solarSystemTag.getIntArray(EXTRAGALACTIC_ADDRESS)).immutable();
				
				List<ResourceKey<Level>> dimensions = new ArrayList<ResourceKey<Level>>();
				solarSystemTag.getCompound(DIMENSIONS).getAllKeys().forEach(dimensionString ->
				{
					dimensions.add(Conversion.stringToDimension(dimensionString));
				});
				
				return new SolarSystem.Serializable(translationName, extragalacticAddress, pointOfOrigin, symbols, symbolPrefix, dimensions);
			}
		}
	}
}
