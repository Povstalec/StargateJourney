package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.GalaxyInit;
import net.povstalec.sgjourney.common.misc.Conversion;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class Galaxy
{
	public static final ResourceKey<Registry<Galaxy>> REGISTRY_KEY = ResourceKey.createRegistryKey(StargateJourney.sgjourneyLocation("galaxy"));
	public static final Codec<ResourceKey<Galaxy>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String DEFAULT_SYMBOLS = "default_symbols";
	public static final String SYMBOL_PREFIX = "symbol_prefix";
	public static final String CAN_GENERATE_ADDRESS_REGIONS = "can_generate_address_regions";
	
	public static final String ADDRESS_REGIONS = "address_region";
	public static final String POINTS_OF_ORIGIN = "points_of_origin";
	
	public static final int DEFAULT_SYMBOL_PREFIX = 3; // Symbol 3 (Virgo) as a reference to the Virgo cluster of galaxies
	
    public static final Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf(NAME).forGetter(galaxy -> galaxy.name),
    		GalaxyInit.CODEC.fieldOf(TYPE).forGetter(galaxy -> galaxy.type),
			Symbols.RESOURCE_KEY_CODEC.fieldOf(DEFAULT_SYMBOLS).forGetter(galaxy -> galaxy.defaultSymbols),
			Codec.intRange(1, Address.MAX_SYMBOL).optionalFieldOf(SYMBOL_PREFIX, DEFAULT_SYMBOL_PREFIX).forGetter(galaxy -> galaxy.symbolPrefix),
			Codec.BOOL.optionalFieldOf(CAN_GENERATE_ADDRESS_REGIONS, false).forGetter(galaxy -> galaxy.canGenerateAddressRegions)
			).apply(instance, Galaxy::new));
	
	private final ResourceKey<Galaxy> galaxyKey;
	
	public final String name;
	public final GalaxyType type;
	public final ResourceKey<Symbols> defaultSymbols;
	public final int symbolPrefix;
	public final boolean canGenerateAddressRegions;
	
	private final Map<Address.Immutable, AddressRegion> addressRegions;
	private final List<ResourceKey<PointOfOrigin>> pointsOfOrigin;
	
	// Constructor made specifically for the codec
	private Galaxy(String name, GalaxyType type, ResourceKey<Symbols> defaultSymbols, int symbolPrefix, boolean canGenerateAddressRegions)
	{
		this(null, name, type, defaultSymbols, symbolPrefix, canGenerateAddressRegions, new HashMap<>(), new ArrayList<>());
	}
	
	public Galaxy(ResourceKey<Galaxy> galaxyKey, String name, GalaxyType type, ResourceKey<Symbols> defaultSymbols, int symbolPrefix, boolean canGenerateAddressRegions,
				   Map<Address.Immutable, AddressRegion> addressRegions, List<ResourceKey<PointOfOrigin>> pointsOfOrigin)
	{
		this.galaxyKey = galaxyKey;
		
		this.name = name;
		this.type = type;
		this.defaultSymbols = defaultSymbols;
		this.symbolPrefix = symbolPrefix;
		this.canGenerateAddressRegions = canGenerateAddressRegions;
		
		this.addressRegions = new HashMap<>(addressRegions);
		this.pointsOfOrigin = new ArrayList<>(pointsOfOrigin);
		
		this.addressRegions.forEach((address, addressRegion) -> addressRegion.addToGalaxy(this, address));
	}
	
	// Always copy the ones actually being used so they have a ResourceKey
	public Galaxy copyTemplateWithKey(ResourceKey<Galaxy> galaxyKey, Map<Address.Immutable, AddressRegion> addressRegions, List<ResourceKey<PointOfOrigin>> pointsOfOrigin)
	{
		return new Galaxy(galaxyKey, this.name, this.type, this.defaultSymbols, this.symbolPrefix, this.canGenerateAddressRegions, addressRegions, pointsOfOrigin);
	}
	
	public ResourceKey<Galaxy> getResourceKey()
	{
		return this.galaxyKey;
	}
	
	public MutableComponent toComponent()
	{
		return Component.translatable(name).withStyle(ChatFormatting.LIGHT_PURPLE);
	}
	
	public ResourceKey<Symbols> getDefaultSymbols()
	{
		return defaultSymbols;
	}
	
	// Address Region
	
	public boolean containsAddressRegion(Address address)
	{
		return this.addressRegions.containsKey(address);
	}
	
	public List<Map.Entry<Address.Immutable, AddressRegion>> getAddressRegions(Predicate<Map.Entry<Address.Immutable, AddressRegion>> predicate)
	{
		return this.addressRegions.entrySet().stream().filter(predicate).toList();
	}
	
	@Nullable
	public AddressRegion getAddressRegion(Address address)
	{
		return this.addressRegions.get(address);
	}
	
	@Nullable
	public ResourceKey<AddressRegion> getAddressRegionKey(Address address)
	{
		AddressRegion addressRegion = this.addressRegions.get(address);
		if(addressRegion == null)
			return null;
		
		return addressRegion.getResourceKey();
	}
	
	public void addAddressRegion(Address.Immutable galacticAddress, AddressRegion addressRegion)
	{
		this.addressRegions.put(galacticAddress, addressRegion);
		addressRegion.addToGalaxy(this, galacticAddress);
	}
	
	public void removeAddressRegion(Address address)
	{
		if(containsAddressRegion(address))
		{
			AddressRegion removedRegion = this.addressRegions.remove(address);
			if(removedRegion != null)
				removedRegion.removeFromGalaxy(this.getResourceKey());
		}
	}
	
	public List<AddressRegion> getShuffledAddressRegions(RandomSource randomSource)
	{
		return Util.toShuffledList(this.addressRegions.values().stream(), randomSource);
	}
	
	@Nullable
	public AddressRegion getRandomAddressRegion(long seed)
	{
		int size = this.addressRegions.size();
		
		if(size < 1)
			return null;
		
		Random random = new Random(seed);
		
		int randomValue = random.nextInt(0, size);
		return (AddressRegion) this.addressRegions.entrySet().stream().toArray()[randomValue];
	}
	
	public void printAddressRegions()
	{
		for(Map.Entry<Address.Immutable, AddressRegion> addressRegionEntry : this.addressRegions.entrySet())
		{
			System.out.println("--- " + addressRegionEntry.getKey().toString() + " " + addressRegionEntry.getValue().getName());
		}
	}
	
	// Points of Origin
	
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
	
	// Other
	
	@Override
	public boolean equals(Object other)
	{
		if(other == this)
			return true;
		
		if(other instanceof Galaxy otherGalaxy)
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
	
	public static Galaxy deserialize(Map<Address, AddressRegion> addressRegions, Registry<Galaxy> galaxyRegistry, ResourceKey<Galaxy> galaxyKey, CompoundTag galaxyTag)
	{
		final Galaxy galaxyTemplate = galaxyRegistry.get(galaxyKey);
		Galaxy galaxy;
		
		if(galaxyTemplate == null) // Galaxy always has to be defined in a datapack //TODO Maybe stop requiring it to be defined in a datapack
			return null;
		
		Map<Address.Immutable, AddressRegion> galaxyAddressRegions = new HashMap<>();
		
		CompoundTag addressRegionsTag = galaxyTag.getCompound(ADDRESS_REGIONS);
		
		for(String addressString : addressRegionsTag.getAllKeys())
		{
			Address.Immutable extragalacticAddress = new Address.Immutable(addressRegionsTag.getIntArray(addressString)); // 8-chevron address
			Address.Immutable address = new Address.Immutable(addressString); // 7-chevron address
			
			if(addressRegions.containsKey(extragalacticAddress))
			{
				AddressRegion addressRegion = addressRegions.get(extragalacticAddress);
				galaxyAddressRegions.put(address, addressRegion);
			}
		}
		
		CompoundTag pointOfOriginTag = galaxyTag.getCompound(POINTS_OF_ORIGIN);
		List<ResourceKey<PointOfOrigin>> pointsOfOrigin = new ArrayList<>();
		for(String pointOfOriginString : pointOfOriginTag.getAllKeys())
		{
			pointsOfOrigin.add(Conversion.stringToPointOfOrigin(pointOfOriginString));
		}
		
		galaxy = galaxyTemplate.copyTemplateWithKey(galaxyKey, galaxyAddressRegions, pointsOfOrigin);
		
		return galaxy;
	}
	
	// Static functions
	
	public static ResourceKey<Symbols> getOrDefaultSymbols(@Nullable Galaxy galaxy)
	{
		return galaxy != null ? galaxy.getDefaultSymbols() : Symbols.defaultSymbols();
	}
	
	public static ResourceKey<PointOfOrigin> randomOrDefaultPointOfOrigin(@Nullable Galaxy galaxy, long seed)
	{
		return galaxy != null ? galaxy.getRandomPointOfOrigin(seed) : PointOfOrigin.defaultPointOfOrigin();
	}
	
	public static int getOrGenerateSymbolPrefix(@Nullable Galaxy galaxy, long seed)
	{
		if(galaxy != null)
			return galaxy.type.size();
		
		return new Random(seed).nextInt(1, Address.ADDRESS_GENERATION_SYMBOLS);
	}
}
