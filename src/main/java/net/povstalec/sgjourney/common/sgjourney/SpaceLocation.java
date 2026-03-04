package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.Conversion;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Class that is intended to represent a single Minecraft Dimension's attributes that Stargate Journey can work with
 */
public class SpaceLocation
{
	public static final ResourceKey<Registry<SpaceLocation>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "space_location"));
	public static final Codec<ResourceKey<SpaceLocation>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final String PARENT_GRAVITY = "parent_gravity";
	//public static final String ALLOW_FACTION_PRESENCE = "allow_faction_presence";
	public static final String GENERATED_ADDRESS = "generated_address";
	public static final String UNITY_CRYSTALS_GROW = "unity_crystals_grow";
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	public static final String ADDRESS_REGION = "address_region";
	public static final String DIMENSION = "dimension";
	
	public static final Codec<SpaceLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.doubleRange(0.0, Double.MAX_VALUE).optionalFieldOf(PARENT_GRAVITY, 0.0).forGetter(spaceLocation -> spaceLocation.parentGravity),
			//Codec.BOOL.optionalFieldOf(ALLOW_FACTION_PRESENCE, false).forGetter(spaceLocation -> spaceLocation.allowFactionPresence), //TODO Maybe change this to a codec for a faction info class
			Codec.BOOL.optionalFieldOf(GENERATED_ADDRESS, false).forGetter(spaceLocation -> spaceLocation.appearAmongGeneratedAddresses),
			Codec.BOOL.optionalFieldOf(UNITY_CRYSTALS_GROW, false).forGetter(spaceLocation -> spaceLocation.unityCrystalsGrow),
			
			PointOfOrigin.RESOURCE_KEY_CODEC.optionalFieldOf(POINT_OF_ORIGIN).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.pointOfOrigin)),
			Symbols.RESOURCE_KEY_CODEC.optionalFieldOf(SYMBOLS).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.symbols)),
			//TODO Coordinates
			AddressRegion.RESOURCE_KEY_CODEC.optionalFieldOf(ADDRESS_REGION).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.addressRegionKey))
	).apply(instance, SpaceLocation::new));
	
	//TODO Something to control whether Stargates work there or not
	
	//TODO private static final Map<String, SpaceLocation> DEFAULTS = new HashMap<>(); // Templates for how to create a Space Location for a Dimension based on its namespace
	private static final Map<ResourceKey<Level>, SpaceLocation> DIMENSION_SPACE_LOCATIONS = new HashMap<>();
	private static final List<SpaceLocation> GENERATED_ADDRESS_DIMENSIONS = new ArrayList<>();
	
	private double parentGravity; // Gravity of the parent body this location might be orbiting - 0.07 for Cavum Tenebrae
	//private boolean allowFactionPresence; // If true, factions will be able to appear in this Dimension
	private boolean appearAmongGeneratedAddresses; // If true, dimension address will appear in Address Tables with generated addresses
	private boolean unityCrystalsGrow; // If true, Unity Crystals can grow in this location
	
	@Nullable
	private ResourceKey<PointOfOrigin> pointOfOrigin; // Point of Origin any Stargates generated in this Space Location will use
	@Nullable
	private ResourceKey<Symbols> symbols; // Symbols any Stargates generated in this Space Location will use
	
	private ResourceKey<Level> dimension; // Dimension this Space Location represents
	@Nullable
	private ResourceKey<AddressRegion> addressRegionKey = null; // Address Region this Space Location is a part of
	@Nullable
	private AddressRegion.Serializable addressRegion = null;
	
	public SpaceLocation(double parentGravity, /*boolean allowFactionPresence, */boolean appearAmongGeneratedAddresses, boolean unityCrystalsGrow,
						 Optional<ResourceKey<PointOfOrigin>> pointOfOrigin, Optional<ResourceKey<Symbols>> symbols, Optional<ResourceKey<AddressRegion>> addressRegionKey)
	{
		this.parentGravity = parentGravity;
		//this.allowFactionPresence = allowFactionPresence;
		this.appearAmongGeneratedAddresses = appearAmongGeneratedAddresses;
		this.unityCrystalsGrow = unityCrystalsGrow;
		
		this.pointOfOrigin = pointOfOrigin.orElse(null);
		this.symbols = symbols.orElse(null);
		this.addressRegionKey = addressRegionKey.orElse(null);
	}
	
	public double getParentGravity()
	{
		return this.parentGravity;
	}
	
	/*public boolean allowFactionPresence()
	{
		return this.allowFactionPresence;
	}*/
	
	public boolean appearAmongGeneratedAddresses()
	{
		return this.appearAmongGeneratedAddresses;
	}
	
	public boolean unityCrystalsGrow()
	{
		return this.unityCrystalsGrow;
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
	
	public ResourceKey<Level> getDimension()
	{
		return dimension;
	}
	
	public ResourceKey<AddressRegion> getAddressRegionKey()
	{
		return this.addressRegionKey;
	}
	
	public void setAddressRegion(AddressRegion.Serializable addressRegion)
	{
		if(addressRegion != null)
			addressRegion.addSpaceLocation(this);
		this.addressRegion = addressRegion;
	}
	
	public AddressRegion.Serializable getAddressRegion()
	{
		return this.addressRegion;
	}
	
	
	
	public CompoundTag serialize()
	{
		CompoundTag spaceLocationTag = new CompoundTag();
		
		spaceLocationTag.putDouble(PARENT_GRAVITY, this.parentGravity);
		//spaceLocationTag.putBoolean(ALLOW_FACTION_PRESENCE, this.allowFactionPresence);
		spaceLocationTag.putBoolean(GENERATED_ADDRESS, this.appearAmongGeneratedAddresses);
		spaceLocationTag.putBoolean(UNITY_CRYSTALS_GROW, this.unityCrystalsGrow);
		
		if(this.pointOfOrigin != null)
			spaceLocationTag.putString(POINT_OF_ORIGIN, this.pointOfOrigin.location().toString());
		if(this.symbols != null)
			spaceLocationTag.putString(SYMBOLS, this.symbols.location().toString());
		
		spaceLocationTag.putString(DIMENSION, this.dimension.location().toString());
		if(this.addressRegionKey != null)
			spaceLocationTag.putString(ADDRESS_REGION, this.addressRegionKey.location().toString());
		
		return spaceLocationTag;
	}
	
	public static SpaceLocation deserialize(CompoundTag spaceLocationTag)
	{
		double parentGravity = spaceLocationTag.getDouble(PARENT_GRAVITY);
		//boolean allowFactionPresence = spaceLocationTag.getBoolean(ALLOW_FACTION_PRESENCE);
		boolean appearAmongGeneratedAddresses = spaceLocationTag.getBoolean(GENERATED_ADDRESS);
		boolean unityCrystalsGrow = spaceLocationTag.getBoolean(UNITY_CRYSTALS_GROW);
		
		ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(spaceLocationTag.getString(POINT_OF_ORIGIN));
		ResourceKey<Symbols> symbols = Conversion.stringToSymbols(spaceLocationTag.getString(SYMBOLS));
		ResourceKey<AddressRegion> addressRegionKey = Conversion.stringToAddressRegionKey(spaceLocationTag.getString(ADDRESS_REGION));
		
		SpaceLocation spaceLocation = new SpaceLocation(parentGravity, /*allowFactionPresence, */appearAmongGeneratedAddresses, unityCrystalsGrow, Optional.ofNullable(pointOfOrigin), Optional.ofNullable(symbols), Optional.ofNullable(addressRegionKey));
		spaceLocation.dimension = Conversion.stringToDimension(spaceLocationTag.getString(DIMENSION));
		return spaceLocation;
	}
	
	
	
	public static SpaceLocation defaultSpaceLocation(ResourceKey<Level> dimension)
	{
		SpaceLocation spaceLocation = new SpaceLocation(0.0, /*false, */true, false, Optional.empty(), Optional.empty(), Optional.empty());
		spaceLocation.dimension = dimension;
		return spaceLocation;
	}
	
	public static Map<ResourceKey<Level>, SpaceLocation> getDimensionSpaceLocations()
	{
		return DIMENSION_SPACE_LOCATIONS;
	}
	
	public static boolean contains(ResourceKey<Level> dimension)
	{
		return DIMENSION_SPACE_LOCATIONS.containsKey(dimension);
	}
	
	public static SpaceLocation fromDimension(ResourceKey<Level> dimension)
	{
		return DIMENSION_SPACE_LOCATIONS.computeIfAbsent(dimension, dimensionKey ->
		{
			SpaceLocation spaceLocation = defaultSpaceLocation(dimension);
			spaceLocation.dimension = dimensionKey;
			return spaceLocation;
		});
	}
	
	public static void clearAddressRegions()
	{
		for(Map.Entry<ResourceKey<Level>, SpaceLocation> spaceLocationEntry : DIMENSION_SPACE_LOCATIONS.entrySet())
		{
			spaceLocationEntry.getValue().setAddressRegion(null);
		}
	}
	
	public static void printSpaceLocations()
	{
		System.out.println("[Space Location - AddressRegion]");
		
		for(Map.Entry<ResourceKey<Level>, SpaceLocation> spaceLocationEntry : DIMENSION_SPACE_LOCATIONS.entrySet())
		{
			System.out.println("- |" + spaceLocationEntry.getKey().location() + "|");
			if(spaceLocationEntry.getValue().addressRegion != null)
				System.out.print(" = |" + spaceLocationEntry.getValue().addressRegion.getName() + "|");
		}
	}
	
	public static List<SpaceLocation> getGeneratedAddressSpaceLocations()
	{
		return GENERATED_ADDRESS_DIMENSIONS;
	}
	
	//============================================================================================
	//********************************Registering Space Locations*********************************
	//============================================================================================
	
	public static void registerSpaceLocations(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<SpaceLocation> spaceLocationRegistry = registries.registryOrThrow(SpaceLocation.REGISTRY_KEY);
		
		Set<Map.Entry<ResourceKey<SpaceLocation>, SpaceLocation>> spaceLocationSet = spaceLocationRegistry.entrySet();
		spaceLocationSet.forEach((spaceLocationEntry) ->
		{
			ResourceKey<Level> dimension = Conversion.locationToDimension(spaceLocationEntry.getKey().location());
			spaceLocationEntry.getValue().dimension = dimension;
			DIMENSION_SPACE_LOCATIONS.put(dimension, spaceLocationEntry.getValue());
			if(spaceLocationEntry.getValue().appearAmongGeneratedAddresses())
				GENERATED_ADDRESS_DIMENSIONS.add(spaceLocationEntry.getValue());
		});
		StargateJourney.LOGGER.info("Space Locations registered");
	}
}
