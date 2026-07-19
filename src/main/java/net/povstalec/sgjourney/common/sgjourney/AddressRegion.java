package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Class that represents a region of space with one Extragalactic Address, which may consist of multiple Space Locations, intended as a replacement for the SolarSystem class
 */
public class AddressRegion
{
	public static final ResourceKey<Registry<AddressRegion>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "address_region"));
	public static final Codec<ResourceKey<AddressRegion>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final String LOCATION = "location";
	public static final String IS_GENERATED = "is_generated";
	
	public static final String NAME = "name";
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	public static final String SYMBOL_PREFIX = "symbol_prefix";
	public static final String EXTRAGALACTIC_ADDRESS = "extragalactic_address";
	public static final String GALACTIC_ADDRESSES = "galactic_addresses";
	
	public static final String SPACE_LOCATIONS = "space_locations";
	
	public static final Codec<AddressRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf(NAME).forGetter(addressRegion -> addressRegion.name),
			PointOfOrigin.RESOURCE_KEY_CODEC.optionalFieldOf(POINT_OF_ORIGIN).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.pointOfOrigin)),
			Symbols.RESOURCE_KEY_CODEC.optionalFieldOf(SYMBOLS).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.symbols)),
			
			Codec.intRange(1, Address.MAX_SYMBOL).optionalFieldOf(SYMBOL_PREFIX, Galaxy.DEFAULT_SYMBOL_PREFIX).forGetter(addressRegion -> addressRegion.symbolPrefix),
			Address.Randomizable.codec(Address.Immutable.CODEC).fieldOf(EXTRAGALACTIC_ADDRESS).forGetter(addressRegion -> addressRegion.extragalacticAddress),
			Codec.unboundedMap(Galaxy.RESOURCE_KEY_CODEC, Address.Randomizable.codec(Address.Immutable.CODEC).fieldOf(Address.ADDRESS).codec()).fieldOf(GALACTIC_ADDRESSES).forGetter(addressRegion -> addressRegion.galacticAddresses)
			
	).apply(instance, AddressRegion::new));
	
	public final boolean isGenerated;
	private final ResourceKey<AddressRegion> addressRegionKey;
	
	public final String name; // Translation name for this Address Region
	@Nullable
	private final ResourceKey<PointOfOrigin> pointOfOrigin; // Point of Origin any Stargates generated in this Address Region will use in case the Space Location doesn't have any set
	@Nullable
	private final ResourceKey<Symbols> symbols; // Symbols any Stargates generated in this Address Region will use in case the Space Location doesn't have any set
	
	public final int symbolPrefix; // Prefix this Region's Extragalactic Address uses when generating randomly
	public final Address.Randomizable<Address.Immutable> extragalacticAddress; // Address of this Region independent of any Galaxy
	public final Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galacticAddresses; // Galaxies this Address Region is a part of and its address within each of them
	
	private final List<SpaceLocation> spaceLocations = new ArrayList<>();
	
	// Constructor made specifically for the codec
	private AddressRegion(String name, Optional<ResourceKey<PointOfOrigin>> pointOfOrigin, Optional<ResourceKey<Symbols>> symbols,
						 int symbolPrefix,  Address.Randomizable<Address.Immutable> extragalacticAddress, Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galacticAddresses)
	{
		this(null, false, name, pointOfOrigin.orElse(null), symbols.orElse(null), symbolPrefix, extragalacticAddress, galacticAddresses);
	}
	
	public AddressRegion(ResourceKey<AddressRegion> addressRegionKey, boolean isGenerated, String name, @Nullable ResourceKey<PointOfOrigin> pointOfOrigin, @Nullable ResourceKey<Symbols> symbols,
						 int symbolPrefix,  Address.Randomizable<Address.Immutable> extragalacticAddress, Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galacticAddresses)
	{
		this.addressRegionKey = addressRegionKey;
		this.isGenerated = isGenerated;
		
		this.name = name;
		this.pointOfOrigin = pointOfOrigin;
		this.symbols = symbols;
		
		this.symbolPrefix = symbolPrefix;
		if(extragalacticAddress.address().getType() != Address.Type.ADDRESS_8_CHEVRON)
			throw new IllegalArgumentException("Invalid Extragalactic Address " + extragalacticAddress.address() + " (should be 7 symbols long)");
		this.extragalacticAddress = extragalacticAddress;
		for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> galacticAddress : galacticAddresses.entrySet())
		{
			if(galacticAddress.getValue().address().getType() != Address.Type.ADDRESS_7_CHEVRON)
				throw new IllegalArgumentException("Invalid Galactic Address " + galacticAddress.getValue().address() + " (should be 6 symbols long)");
		}
		this.galacticAddresses = new HashMap<>(galacticAddresses);
	}
	
	public AddressRegion(ResourceKey<AddressRegion> addressRegionKey, String name, @Nullable ResourceKey<PointOfOrigin> pointOfOrigin, @Nullable ResourceKey<Symbols> symbols, int symbolPrefix, Address.Immutable extragalacticAddress)
	{
		this(addressRegionKey, true, name, pointOfOrigin, symbols, symbolPrefix, new Address.Randomizable<>(extragalacticAddress), Map.of());
	}
	
	public AddressRegion copyTemplateWithKey(ResourceKey<AddressRegion> addressRegionKey, Address.Immutable extragalacticAddress)
	{
		return new AddressRegion(addressRegionKey, this.isGenerated, this.name, this.pointOfOrigin, this.symbols, this.symbolPrefix, new Address.Randomizable<>(extragalacticAddress), this.galacticAddresses);
	}
		
	public ResourceKey<AddressRegion> getResourceKey()
	{
		return addressRegionKey;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Nullable
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		return pointOfOrigin;
	}
	
	@Nullable
	public ResourceKey<Symbols> getSymbols()
	{
		return symbols;
	}
	
	public Component getTranslatedName()
	{
		if(!this.isGenerated)
			return Component.translatable(name);
		
		return Component.literal(name);
	}
	
	public Address.Immutable getExtragalacticAddress()
	{
		return extragalacticAddress.address();
	}
	
	public List<SpaceLocation> getSpaceLocations()
	{
		return spaceLocations;
	}
	
	public void addSpaceLocation(SpaceLocation spaceLocation)
	{
		spaceLocations.add(spaceLocation);
	}
	
	public Map<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> getGalacticAddresses()
	{
		return this.galacticAddresses;
	}
	
	public void addToGalaxy(Galaxy galaxy, Address.Immutable address)
	{
		this.galacticAddresses.put(galaxy.getResourceKey(), new Address.Randomizable<>(address));
	}
	
	@Nullable
	public Address.Immutable removeFromGalaxy(ResourceKey<Galaxy> galaxyKey)
	{
		Address.Randomizable<Address.Immutable> randomizableAddress = this.galacticAddresses.remove(galaxyKey);
		if(randomizableAddress != null)
			return randomizableAddress.address();
		
		return null;
	}
	
	/**
	 * @param galaxyKey Galaxy to use for the search
	 * @return The address of this Solar System in the specified galaxy, or null if it's not located in the specified galaxy
	 */
	@Nullable
	public Address.Immutable getAddressInGalaxy(ResourceKey<Galaxy> galaxyKey)
	{
		if(galaxyKey == null || !this.galacticAddresses.containsKey(galaxyKey))
			return null;
		
		return this.galacticAddresses.get(galaxyKey).address();
	}
	
	/**
	 * Finds a common Galaxy for this Solar System and another specified Solar System
	 * @param other Other Solar System
	 * @return A common Galaxy in which both this and the other Solar System are located, otherwise null
	 */
	@Nullable
	public Galaxy findCommonGalaxy(MinecraftServer server, AddressRegion other)
	{
		if(other == null)
			return null;
		
		for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> entry : this.galacticAddresses.entrySet())
		{
			for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> otherEntry : other.galacticAddresses.entrySet())
			{
				if(entry.getKey().equals(otherEntry.getKey()))
					return Universe.get(server).getGalaxy(entry.getKey());
			}
		}
		
		return null;
	}
	
	/**
	 * Returns an Address Region that's in the same galaxy as this Address Region and uses the specified Address
	 * @param server Current Minecraft Server
	 * @param address Address of the Address Region being searched for
	 * @return Address Region that has the same Galactic Address within one of the Galaxies this Address Region is located in
	 */
	@Nullable
	public AddressRegion getSameGalaxyAddressRegion(MinecraftServer server, Address address)
	{
		Universe universe = Universe.get(server);
		for(Map.Entry<ResourceKey<Galaxy>, Address.Randomizable<Address.Immutable>> entry : this.galacticAddresses.entrySet())
		{
			if(universe.hasGalaxy(entry.getKey()))
				return universe.getGalaxy(entry.getKey()).getAddressRegion(address);
		}
		
		return null;
	}
	
	@Override
	public String toString()
	{
		return extragalacticAddress + " " + name;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag addressRegionTag = new CompoundTag();
		
		addressRegionTag.putString(LOCATION, this.addressRegionKey.location().toString());
		addressRegionTag.putBoolean(IS_GENERATED, this.isGenerated);
		
		addressRegionTag.putString(NAME, this.name);
		if(this.pointOfOrigin != null)
			addressRegionTag.putString(POINT_OF_ORIGIN, this.pointOfOrigin.location().toString());
		if(this.symbols != null)
			addressRegionTag.putString(SYMBOLS, this.symbols.location().toString());
		
		addressRegionTag.putInt(SYMBOL_PREFIX, this.symbolPrefix);
		
		ListTag spaceLocationsTag = new ListTag();
		for(SpaceLocation spaceLocation : this.spaceLocations)
		{
			spaceLocationsTag.add(StringTag.valueOf(spaceLocation.getDimension().location().toString()));
		}
		addressRegionTag.put(SPACE_LOCATIONS, spaceLocationsTag);
		
		getExtragalacticAddress().saveToCompoundTag(addressRegionTag, EXTRAGALACTIC_ADDRESS);
		
		return addressRegionTag;
	}
	
	public static AddressRegion deserialize(MinecraftServer server, CompoundTag addressRegionTag)
	{
		ResourceLocation location = ResourceLocation.tryParse(addressRegionTag.getString(LOCATION));
		boolean isGenerated = addressRegionTag.getBoolean(IS_GENERATED);
		
		String translationName = addressRegionTag.getString(NAME);
		ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(addressRegionTag.getString(POINT_OF_ORIGIN));
		ResourceKey<Symbols> symbols = Conversion.stringToSymbols(addressRegionTag.getString(SYMBOLS));
		
		int symbolPrefix = addressRegionTag.getInt(SYMBOL_PREFIX);
		Address.Immutable extragalacticAddress = new Address.Immutable(addressRegionTag.getIntArray(EXTRAGALACTIC_ADDRESS));
		
		AddressRegion addressRegion = new AddressRegion(Conversion.locationToAddressRegionKey(location), isGenerated, translationName, pointOfOrigin, symbols, symbolPrefix, new Address.Randomizable<>(extragalacticAddress), Map.of());
		
		// Add Space Locations
		ListTag spaceLocationsTag = addressRegionTag.getList(SPACE_LOCATIONS, StringTag.TAG_END);
		for(Tag tag : spaceLocationsTag)
		{
			SpaceLocation.fromDimension(server, Conversion.stringToDimension(tag.toString())).setAddressRegion(addressRegion);
		}
		
		return addressRegion;
	}
}
