package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Class that represents a region of space with one Extragalactic Address, which may consist of multiple Space Locations, intended as a replacement for the SolarSystem class
 */
public class AddressRegion
{
	public static final ResourceKey<Registry<AddressRegion>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "address_region"));
	public static final Codec<ResourceKey<AddressRegion>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final String NAME = "name";
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	public static final String SYMBOL_PREFIX = "symbol_prefix";
	public static final String EXTRAGALACTIC_ADDRESS = "extragalactic_address";
	public static final String GALACTIC_ADDRESSES = "galactic_addresses";
	
	private static final Codec<Pair<Address.Immutable, Boolean>> ADDRESS = Codec.pair(Address.Immutable.CODEC, Codec.BOOL.fieldOf("randomizable").codec());
	private static final Codec<Pair<ResourceKey<Galaxy>, Pair<Address.Immutable, Boolean>>> GALAXY_AND_ADDRESS = Codec.pair(Galaxy.RESOURCE_KEY_CODEC.fieldOf("galaxy").codec(), ADDRESS.fieldOf("address").codec());
	
	public static final Codec<AddressRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf(NAME).forGetter(addressRegion -> addressRegion.name),
			PointOfOrigin.RESOURCE_KEY_CODEC.optionalFieldOf(POINT_OF_ORIGIN).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.pointOfOrigin)),
			Symbols.RESOURCE_KEY_CODEC.optionalFieldOf(SYMBOLS).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.symbols)),
			
			Codec.intRange(1, Address.MAX_SYMBOL).fieldOf(SYMBOL_PREFIX).forGetter(addressRegion -> addressRegion.symbolPrefix),
			ADDRESS.fieldOf(EXTRAGALACTIC_ADDRESS).forGetter(addressRegion -> addressRegion.extragalacticAddress),
			GALAXY_AND_ADDRESS.listOf().optionalFieldOf(GALACTIC_ADDRESSES, List.of()).forGetter(addressRegion -> addressRegion.galacticAddresses)
	).apply(instance, AddressRegion::new));
	
	public final String name; // Translation name for this Address Region
	@Nullable
	private ResourceKey<PointOfOrigin> pointOfOrigin; // Point of Origin any Stargates generated in this Address Region will use in case the Space Location doesn't have any set
	@Nullable
	private ResourceKey<Symbols> symbols; // Symbols any Stargates generated in this Address Region will use in case the Space Location doesn't have any set
	
	public final int symbolPrefix; // Prefix this Region's Extragalactic Address uses when generating randomly
	public final Pair<Address.Immutable, Boolean> extragalacticAddress; // Address of this Region independent of any Galaxy
	public final List<Pair<ResourceKey<Galaxy>, Pair<Address.Immutable, Boolean>>> galacticAddresses; // Galaxies this Address Region is a part of and its address within each
	
	public AddressRegion(String name, Optional<ResourceKey<PointOfOrigin>> pointOfOrigin, Optional<ResourceKey<Symbols>> symbols,
						 int symbolPrefix,  Pair<Address.Immutable, Boolean> extragalacticAddress, List<Pair<ResourceKey<Galaxy>, Pair<Address.Immutable, Boolean>>> galacticAddresses)
	{
		this.name = name;
		this.pointOfOrigin = pointOfOrigin.orElse(null);
		this.symbols = symbols.orElse(null);
		
		this.symbolPrefix = symbolPrefix;
		if(extragalacticAddress.getFirst().getType() != Address.Type.ADDRESS_8_CHEVRON)
			throw new IllegalArgumentException("Invalid Extragalactic Address (should be 7 symbols long)");
		this.extragalacticAddress = extragalacticAddress;
		for(Pair<ResourceKey<Galaxy>, Pair<Address.Immutable, Boolean>> galacticAddress : galacticAddresses)
		{
			if(galacticAddress.getSecond().getFirst().getType() != Address.Type.ADDRESS_7_CHEVRON)
				throw new IllegalArgumentException("Invalid Galactic Address (should be 6 symbols long)");
		}
		this.galacticAddresses = galacticAddresses;
	}
	
	
	
	public static class Serializable
	{
		public static final String LOCATION = "location";
		public static final String NAME = "name";
		public static final String SYMBOL_PREFIX = "symbol_prefix";
		public static final String EXTRAGALACTIC_ADDRESS = "extragalactic_address";
		public static final String SPACE_LOCATIONS = "space_locations";
		
		public static final String PRIMARY_STARGATE = "primary_stargate";
		
		private ResourceKey<AddressRegion> resourceKey;
		private String name; // Translation name for this Address Region
		@Nullable
		private ResourceKey<PointOfOrigin> pointOfOrigin; // Point of Origin any Stargates generated in this Address Region will use in case the Space Location doesn't have any set
		@Nullable
		private ResourceKey<Symbols> symbols; // Symbols any Stargates generated in this Address Region will use in case the Space Location doesn't have any set
		
		private final boolean isGenerated;
		
		private int symbolPrefix; // Prefix this Region's Extragalactic Address uses when generating randomly
		private Address.Immutable extragalacticAddress; // Address of this Region independent of any Galaxy
		
		private HashMap<Galaxy.Serializable, Address.Immutable> galacticAddresses = new HashMap<Galaxy.Serializable, Address.Immutable>(); // Galaxies this Address Region is a part of and its address within each
		private List<SpaceLocation> spaceLocations = new ArrayList<SpaceLocation>();
		
		private List<Stargate> stargates = new ArrayList<Stargate>();
		
		@Nullable
		private Address.Immutable primaryAddress = null;
		@Nullable
		private Stargate primaryStargate = null;
		
		//TODO List of Transporters
		
		public Serializable(ResourceKey<AddressRegion> resourceKey, Address.Immutable extragalacticAddress, AddressRegion addressRegion)
		{
			this.resourceKey = resourceKey;
			
			this.name = addressRegion.name;
			this.pointOfOrigin = addressRegion.pointOfOrigin;
			this.symbols = addressRegion.symbols;
			
			this.isGenerated = false;
			
			this.symbolPrefix = addressRegion.symbolPrefix;
			this.extragalacticAddress = extragalacticAddress;
		}
		
		public Serializable(ResourceKey<AddressRegion> resourceKey, String name, @Nullable ResourceKey<PointOfOrigin> pointOfOrigin, @Nullable ResourceKey<Symbols> symbols, int symbolPrefix, Address.Immutable extragalacticAddress)
		{
			this.resourceKey = resourceKey;
			
			this.name = name;
			this.pointOfOrigin = pointOfOrigin;
			this.symbols = symbols;
			
			this.isGenerated = true;
			
			this.symbolPrefix = symbolPrefix;
			this.extragalacticAddress = extragalacticAddress;
		}
		
		public ResourceKey<AddressRegion> getResourceKey()
		{
			return this.resourceKey;
		}
		
		public String getName()
		{
			return this.name;
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
				return Component.translatable(this.name);
			
			return Component.literal(this.name);
		}
		
		public int getSymbolPrefix()
		{
			return symbolPrefix;
		}
		
		public Address.Immutable getExtragalacticAddress()
		{
			return extragalacticAddress;
		}
		
		public List<SpaceLocation> getSpaceLocations()
		{
			return spaceLocations;
		}
		
		public void addSpaceLocation(SpaceLocation spaceLocation)
		{
			spaceLocations.add(spaceLocation);
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
		
		/**
		 * @param galaxy Galaxy to use for the search
		 * @return The address of this Solar System in the specified galaxy, or null if it's not located in the specified galaxy
		 */
		@Nullable
		public Address.Immutable getAddressInGalaxy(Galaxy.Serializable galaxy)
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
		public Galaxy.Serializable findCommonGalaxy(AddressRegion.Serializable other)
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
		public AddressRegion.Serializable getAddressRegionFromAddress(Address address)
		{
			for(Map.Entry<Galaxy.Serializable, Address.Immutable> entry : this.galacticAddresses.entrySet())
			{
				if(entry.getKey().containsAddressRegion(address))
					return entry.getKey().getAddressRegion(address);
			}
			
			return null;
		}
		
		//============================================================================================
		//*****************************************Stargates******************************************
		//============================================================================================
		
		public List<Stargate> getStargates()
		{
			return this.stargates;
		}
		
		/** Adds Stargate to an ordered list based on the following preferences:
		 * Stargate Preferences:
		 * 1. Has DHD
		 * 2. Stargate Generation
		 * 3. The number of times the Stargate was used
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
			
			int index = Collections.binarySearch(this.stargates, addedStargate);
			if(index < 0) // Stargate was not found
				this.stargates.add(-index - 1, addedStargate);
		}
		
		public void sortStargates()
		{
			this.stargates.sort(null);
		}
		
		public void removeStargate(Stargate stargate)
		{
			if(stargate == this.primaryStargate)
				this.primaryStargate = null;
			
			this.stargates.remove(stargate);
		}
		
		/**
		 * @param address Address to use for the search
		 * @return Returns the Stargate based on the specified Address, or null if there is no Stargate with this Address
		 */
		@Nullable
		public Stargate findStargate(Address.Immutable address)
		{
			for(Stargate stargate : this.stargates)
			{
				if(address.equals(stargate.get9ChevronAddress()))
					return stargate;
			}
			
			return null;
		}
		
		/**
		 * Sets the Stargate with the specified address as the Primary Stargate of this Address Region
		 * @param primaryAddress Address of the Primary Stargate
		 * @return True if the Primary Stargate was set successfully, otherwise false
		 */
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
		
		public List<Stargate> getShuffledStargates(RandomSource randomSource)
		{
			return Util.toShuffledList(this.stargates.stream(), randomSource);
		}
		
		@Nullable
		public Stargate getRandomStargate(long seed)
		{
			int size = this.stargates.size();
			
			if(size < 1)
				return null;
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			return this.stargates.get(randomValue);
		}
		
		//============================================================================================
		//*************************************Saving and Loading*************************************
		//============================================================================================
		
		public CompoundTag serialize()
		{
			CompoundTag addressRegionTag = new CompoundTag();
			
			addressRegionTag.putString(LOCATION, this.resourceKey.location().toString());
			
			if(isGenerated())
			{
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
			}
			
			// Extragalactic Address may be randomized, so it needs to always be saved
			addressRegionTag.putIntArray(EXTRAGALACTIC_ADDRESS, this.extragalacticAddress.toArray());
			
			if(this.primaryAddress != null)
				addressRegionTag.putIntArray(PRIMARY_STARGATE, this.primaryAddress.toArray());
			
			return addressRegionTag;
		}
		
		public static AddressRegion.Serializable deserialize(Registry<AddressRegion> addressRegionRegistry, CompoundTag addressRegionTag)
		{
			AddressRegion.Serializable addressRegion;
			
			if(!addressRegionTag.contains(NAME, CompoundTag.TAG_STRING))
			{
				ResourceKey<AddressRegion> addressRegionKey = Conversion.stringToAddressRegionKey(addressRegionTag.getString(LOCATION));
				
				Address.Immutable extragalacticAddress = new Address.Immutable(addressRegionTag.getIntArray(EXTRAGALACTIC_ADDRESS));
				
				addressRegion = new AddressRegion.Serializable(Conversion.locationToAddressRegionKey(addressRegionKey.location()), extragalacticAddress, addressRegionRegistry.get(addressRegionKey));
			}
			else
			{
				ResourceLocation location = ResourceLocation.tryParse(addressRegionTag.getString(LOCATION));
				
				String translationName = addressRegionTag.getString(NAME);
				ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(addressRegionTag.getString(POINT_OF_ORIGIN));
				ResourceKey<Symbols> symbols = Conversion.stringToSymbols(addressRegionTag.getString(SYMBOLS));
				
				int symbolPrefix = addressRegionTag.getInt(SYMBOL_PREFIX);
				Address.Immutable extragalacticAddress = new Address.Immutable(addressRegionTag.getIntArray(EXTRAGALACTIC_ADDRESS));
				
				addressRegion = new AddressRegion.Serializable(Conversion.locationToAddressRegionKey(location), translationName, pointOfOrigin, symbols, symbolPrefix, extragalacticAddress);
				
				ListTag spaceLocationsTag = addressRegionTag.getList(SPACE_LOCATIONS, StringTag.TAG_END);
				for(int i = 0; i < spaceLocationsTag.size(); i++)
				{
					SpaceLocation.fromDimension(Conversion.stringToDimension(spaceLocationsTag.get(i).toString())).setAddressRegion(addressRegion);
				}
				
			}
			
			if(addressRegionTag.contains(PRIMARY_STARGATE, CompoundTag.TAG_INT_ARRAY))
				addressRegion.setPrimaryStargate(new Address.Immutable(addressRegionTag.getIntArray(PRIMARY_STARGATE)));
			
			return addressRegion;
		}
	}
}
