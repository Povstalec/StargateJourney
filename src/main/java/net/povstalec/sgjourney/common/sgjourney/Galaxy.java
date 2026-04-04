package net.povstalec.sgjourney.common.sgjourney;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.GalaxyInit;
import net.povstalec.sgjourney.common.misc.Conversion;

import javax.annotation.Nullable;

public class Galaxy
{
	public static final ResourceKey<Registry<Galaxy>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "galaxy"));
	public static final Codec<ResourceKey<Galaxy>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String DEFAULT_SYMBOLS = "default_symbols";
	public static final String SYMBOL_PREFIX = "symbol_prefix";
	public static final String CAN_GENERATE_ADDRESS_REGIONS = "can_generate_address_regions";
	
    public static final Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf(NAME).forGetter(Galaxy::getName),
    		GalaxyInit.CODEC.fieldOf(TYPE).forGetter(Galaxy::getType),
			Symbols.RESOURCE_KEY_CODEC.fieldOf(DEFAULT_SYMBOLS).forGetter(Galaxy::getDefaultSymbols),
			Codec.intRange(1, Address.MAX_SYMBOL).optionalFieldOf(SYMBOL_PREFIX, 3).forGetter(galaxy -> galaxy.symbolPrefix), // Symbol 3 (Virgo) as a reference to the Virgo cluster of galaxies
			Codec.BOOL.optionalFieldOf(CAN_GENERATE_ADDRESS_REGIONS, false).forGetter(galaxy -> galaxy.canGenerateAddressRegions)
			).apply(instance, Galaxy::new));

	private final String name;
	private final GalaxyType type;
	private final ResourceKey<Symbols> defaultSymbols;
	private final int symbolPrefix;
	private final boolean canGenerateAddressRegions;
	
	public Galaxy(String name, GalaxyType type, ResourceKey<Symbols> defaultSymbols, int symbolPrefix, boolean canGenerateAddressRegions)
	{
		this.name = name;
		this.type = type;
		this.defaultSymbols = defaultSymbols;
		this.symbolPrefix = symbolPrefix;
		this.canGenerateAddressRegions = canGenerateAddressRegions;
	}
	
	public String getName()
	{
		return name;
	}
	
	public GalaxyType getType()
	{
		return type;
	}
	
	public ResourceKey<Symbols> getDefaultSymbols()
	{
		return defaultSymbols;
	}
	
	public int getSymbolPrefix()
	{
		return symbolPrefix;
	}
	
	public boolean canGenerateAddressRegions()
	{
		return canGenerateAddressRegions;
	}
	
	public static Galaxy getGalaxy(Level level, ResourceLocation galaxy)
	{
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<Galaxy> registry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
        
        return registry.get(galaxy);
	}
	
	public static ResourceKey<Symbols> getOrDefaultSymbols(@Nullable Galaxy.Serializable galaxy)
	{
		return galaxy != null ? galaxy.getDefaultSymbols() : Symbols.defaultSymbols();
	}
	
	public static ResourceKey<PointOfOrigin> randomOrDefaultPointOfOrigin(@Nullable Galaxy.Serializable galaxy, long seed)
	{
		return galaxy != null ? galaxy.getRandomPointOfOrigin(seed) : PointOfOrigin.defaultPointOfOrigin();
	}
	
	public static int getOrGenerateSymbolPrefix(@Nullable Galaxy.Serializable galaxy, long seed)
	{
		if(galaxy != null)
			return galaxy.getSize();
		
		return new Random(seed).nextInt(1, Address.ADDRESS_GENERATION_SYMBOLS);
	}
	
	/**
	 * Version of Galaxy used for Stargate Network and Universe
	 * @author Povstalec
	 *
	 */
	public static final class Serializable
	{
		public static final String ADDRESS_REGIONS = "address_region";
		public static final String POINTS_OF_ORIGIN = "points_of_origin";
		
		private final ResourceKey<Galaxy> galaxyKey;
		private final Galaxy galaxy;
		
		private Map<Address.Immutable, AddressRegion.Serializable> addressRegions;
		private List<ResourceKey<PointOfOrigin>> pointsOfOrigin;
		
		public Serializable(ResourceKey<Galaxy> galaxyKey, Galaxy galaxy, 
				Map<Address.Immutable, AddressRegion.Serializable> addressRegions, List<ResourceKey<PointOfOrigin>> pointsOfOrigin)
		{
			this.galaxyKey = galaxyKey;
			this.galaxy = galaxy;
			
			this.addressRegions = addressRegions;
			this.pointsOfOrigin = pointsOfOrigin;
			
			this.addressRegions.forEach((address, addressRegion) -> addressRegion.addToGalaxy(this, address));
		}
		
		public ResourceKey<Galaxy> getKey()
		{
			return this.galaxyKey;
		}
		
		public MutableComponent toComponent()
		{
			return Component.translatable(galaxy.getName()).withStyle(ChatFormatting.LIGHT_PURPLE);
		}
		
		public ResourceKey<Symbols> getDefaultSymbols()
		{
			return galaxy.getDefaultSymbols();
		}
		
		public int getSymbolPrefix()
		{
			return galaxy.getSymbolPrefix();
		}
		
		public int getSize()
		{
			return galaxy.getType().getSize();
		}
		
		public boolean canGenerateAddressRegions()
		{
			return galaxy.canGenerateAddressRegions();
		}
		
		public boolean containsAddressRegion(Address address)
		{
			return this.addressRegions.containsKey(address);
		}
		
		public List<Map.Entry<Address.Immutable, AddressRegion.Serializable>> getAddressRegions(Predicate<Map.Entry<Address.Immutable, AddressRegion.Serializable>> predicate)
		{
			return this.addressRegions.entrySet().stream().filter(predicate).toList();
		}
		
		@Nullable
		public AddressRegion.Serializable getAddressRegion(Address address)
		{
			return this.addressRegions.get(address);
		}
		
		public void addAddressRegion(Address.Immutable galacticAddress, AddressRegion.Serializable addressRegion)
		{
			this.addressRegions.put(galacticAddress, addressRegion);
			addressRegion.addToGalaxy(this, galacticAddress);
		}
		
		public void removeAddressRegion(Address address)
		{
			if(containsAddressRegion(address))
			{
				AddressRegion.Serializable removedRegion = this.addressRegions.remove(address);
				if(removedRegion != null)
					removedRegion.removeFromGalaxy(this);
			}
		}
		
		public List<AddressRegion.Serializable> getShuffledAddressRegions(RandomSource randomSource)
		{
			return Util.toShuffledList(this.addressRegions.values().stream(), randomSource);
		}
		
		@Nullable
		public AddressRegion.Serializable getRandomAddressRegion(long seed)
		{
			int size = this.addressRegions.size();
			
			if(size < 1)
				return null;
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			AddressRegion.Serializable randomSolarSystem = (AddressRegion.Serializable) this.addressRegions.entrySet().stream().toArray()[randomValue];
			
			return randomSolarSystem;
		}
		
		public void printAddressRegions()
		{
			for(Map.Entry<Address.Immutable, AddressRegion.Serializable> addressRegionEntry : this.addressRegions.entrySet())
			{
				System.out.println("--- " + addressRegionEntry.getKey().toString() + " " + addressRegionEntry.getValue().getName());
			}
		}
		
		public void addPointOfOrigin(ResourceKey<PointOfOrigin> pointOfOrigin)
		{
			if(!this.pointsOfOrigin.contains(pointOfOrigin))
				this.pointsOfOrigin.add(pointOfOrigin);
		}
		
		public ResourceKey<PointOfOrigin> getRandomPointOfOrigin(long seed)
		{
			int size = this.pointsOfOrigin.size();
			
			if(size < 1)
				return PointOfOrigin.defaultPointOfOrigin();
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			return this.pointsOfOrigin.get(randomValue);
		}
		
		@Override
		public boolean equals(Object other)
		{
			if(other == this)
				return true;
			
			if(other instanceof Galaxy.Serializable otherGalaxy)
				return this.galaxyKey.equals(otherGalaxy.galaxyKey);
			
			return false;
		}
		
		
		
		public CompoundTag serialize()
		{
			CompoundTag galaxyTag = new CompoundTag();

			CompoundTag addressRegionsTag = new CompoundTag();
			this.addressRegions.forEach((address, addressRegion) ->
				addressRegionsTag.putIntArray(address.toString(), addressRegion.getExtragalacticAddress().toArray()));
			
			galaxyTag.put(ADDRESS_REGIONS, addressRegionsTag);

			CompoundTag pointOfOriginTag = new CompoundTag();
			for(ResourceKey<PointOfOrigin> pointOfOrigin : this.pointsOfOrigin)
			{
				String pointOfOriginString = pointOfOrigin.location().toString();
				pointOfOriginTag.putString(pointOfOriginString, pointOfOriginString);
			}
			
			galaxyTag.put(POINTS_OF_ORIGIN, pointOfOriginTag);
			
			return galaxyTag;
		}
		
		public static Galaxy.Serializable deserialize(Map<Address, AddressRegion.Serializable> addressRegions,
				Registry<Galaxy> galaxyRegistry, ResourceKey<Galaxy> galaxyKey, CompoundTag galaxyTag)
		{
			Galaxy galaxy = galaxyRegistry.get(galaxyKey);
			
			Map<Address.Immutable, AddressRegion.Serializable> galaxyAddressRegions = new HashMap<>();
			
			CompoundTag addressRegionsTag = galaxyTag.getCompound(ADDRESS_REGIONS);

			for(String addressString : addressRegionsTag.getAllKeys())
			{
				Address.Immutable extragalacticAddress = new Address.Immutable(addressRegionsTag.getIntArray(addressString)); // 8-chevron address
				Address.Immutable address = new Address.Immutable(addressString); // 7-chevron address
				
				if(addressRegions.containsKey(extragalacticAddress))
				{
					AddressRegion.Serializable addressRegion = addressRegions.get(extragalacticAddress);
					galaxyAddressRegions.put(address, addressRegion);
				}
			}
			
			CompoundTag pointOfOriginTag = galaxyTag.getCompound(POINTS_OF_ORIGIN);
			List<ResourceKey<PointOfOrigin>> pointsOfOrigin = new ArrayList<ResourceKey<PointOfOrigin>>();
			for(String pointOfOriginString : pointOfOriginTag.getAllKeys())
			{
				pointsOfOrigin.add(Conversion.stringToPointOfOrigin(pointOfOriginString));
			}
			
			return new Galaxy.Serializable(galaxyKey, galaxy, galaxyAddressRegions, pointsOfOrigin);
		}
	}
}
