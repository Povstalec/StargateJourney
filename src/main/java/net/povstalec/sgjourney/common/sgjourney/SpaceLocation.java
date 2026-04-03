package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.packets.ClientboundUpdatePlayerGravityPacket;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Class that is intended to represent a single Minecraft Dimension's attributes that Stargate Journey can work with
 */
public class SpaceLocation
{
	//TODO Only add space locations if corresponding dimension exists
	//TODO Add generated addresses
	
	public static final ResourceKey<Registry<SpaceLocation>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "space_location"));
	public static final Codec<ResourceKey<SpaceLocation>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final String TEMLPATE_INFO = "template_info";
	public static final String IN_STARGATE_NETWORK = "in_stargate_network";
	public static final String PARENT_GRAVITY = "parent_gravity";
	//public static final String ALLOW_FACTION_PRESENCE = "allow_faction_presence";
	public static final String GENERATED_ADDRESS = "generate_in_address_tables";
	public static final String UNITY_CRYSTALS_GROW = "unity_crystals_grow";
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	public static final String ADDRESS_REGION = "address_region";
	public static final String DIMENSION = "dimension";
	
	public static final Codec<SpaceLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			TemplateInfo.CODEC.optionalFieldOf(TEMLPATE_INFO).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.templateInfo)),
			Codec.BOOL.optionalFieldOf(IN_STARGATE_NETWORK, true).forGetter(spaceLocation -> spaceLocation.inStargateNetwork),
			Codec.doubleRange(0.0, Double.MAX_VALUE).optionalFieldOf(PARENT_GRAVITY, 0.0).forGetter(spaceLocation -> spaceLocation.parentGravity),
			//Codec.BOOL.optionalFieldOf(ALLOW_FACTION_PRESENCE, false).forGetter(spaceLocation -> spaceLocation.allowFactionPresence), //TODO Maybe change this to a codec for a faction info class
			Codec.BOOL.optionalFieldOf(GENERATED_ADDRESS, false).forGetter(spaceLocation -> spaceLocation.generateInAddressTables),
			Codec.BOOL.optionalFieldOf(UNITY_CRYSTALS_GROW, false).forGetter(spaceLocation -> spaceLocation.unityCrystalsGrow),
			
			PointOfOrigin.RESOURCE_KEY_CODEC.optionalFieldOf(POINT_OF_ORIGIN).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.pointOfOrigin)),
			Symbols.RESOURCE_KEY_CODEC.optionalFieldOf(SYMBOLS).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.symbols)),
			//TODO Coordinates
			AddressRegion.RESOURCE_KEY_CODEC.optionalFieldOf(ADDRESS_REGION).forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.addressRegionKey))
	).apply(instance, SpaceLocation::new));
	
	private static final TreeMap<String, SpaceLocation> TEMPLATES = new TreeMap<>(); // Templates for how to create a Space Location for a Dimension based on its prefix
	private static final Map<ResourceKey<Level>, SpaceLocation> DIMENSION_SPACE_LOCATIONS = new HashMap<>(); // Map of Dimensions with Space Locations assigned to them
	private static final List<SpaceLocation> GENERATED_ADDRESS_DIMENSIONS = new ArrayList<>(); // List of Dimensions that allow their Addresses to be found on Cartouches //TODO Make this Galaxy dependant
	
	public static double currentGravity = 0; // For controlling gravity on the Client side
	
	@Nullable
	private final TemplateInfo templateInfo; // If not null, this Space Location can be used as a template for Space Locations (for example, ones added at runtime)
	private final boolean inStargateNetwork; // If false, any Stargate located in this location will not be able to establish any sort of connection
	private final double parentGravity; // Gravity of the parent body this location might be orbiting - 0.07 for Cavum Tenebrae
	//private boolean allowFactionPresence; // If true, factions will be able to appear in this Dimension
	private final boolean generateInAddressTables; // If true, dimension address will appear in Address Tables with generated addresses
	private final boolean unityCrystalsGrow; // If true, Unity Crystals can grow in this location
	
	@Nullable
	private final ResourceKey<PointOfOrigin> pointOfOrigin; // Point of Origin any Stargates generated in this Space Location will use
	@Nullable
	private final ResourceKey<Symbols> symbols; // Symbols any Stargates generated in this Space Location will use
	
	private ResourceKey<Level> dimension; // Dimension this Space Location represents
	@Nullable
	private ResourceKey<AddressRegion> addressRegionKey = null; // Address Region this Space Location is a part of
	@Nullable
	private AddressRegion.Serializable addressRegion = null;
	
	public SpaceLocation(Optional<TemplateInfo> templateInfo, boolean inStargateNetwork, double parentGravity, /*boolean allowFactionPresence, */boolean generateInAddressTables, boolean unityCrystalsGrow,
						 Optional<ResourceKey<PointOfOrigin>> pointOfOrigin, Optional<ResourceKey<Symbols>> symbols, Optional<ResourceKey<AddressRegion>> addressRegionKey)
	{
		this.templateInfo = templateInfo.orElse(null);
		this.inStargateNetwork = inStargateNetwork;
		this.parentGravity = parentGravity;
		//this.allowFactionPresence = allowFactionPresence;
		this.generateInAddressTables = generateInAddressTables;
		this.unityCrystalsGrow = unityCrystalsGrow;
		
		this.pointOfOrigin = pointOfOrigin.orElse(null);
		this.symbols = symbols.orElse(null);
		this.addressRegionKey = addressRegionKey.orElse(null);
	}
	
	public SpaceLocation createCopy()
	{
		return new SpaceLocation(Optional.empty(), this.inStargateNetwork, this.parentGravity, this.generateInAddressTables, this.unityCrystalsGrow, Optional.ofNullable(this.pointOfOrigin), Optional.ofNullable(this.symbols), Optional.ofNullable(this.addressRegionKey));
	}
	
	@Nullable
	public TemplateInfo getTemplateInfo()
	{
		return this.templateInfo;
	}
	
	public boolean isInStargateNetwork()
	{
		return this.inStargateNetwork;
	}
	
	public double getParentGravity()
	{
		return this.parentGravity;
	}
	
	/*public boolean allowFactionPresence()
	{
		return this.allowFactionPresence;
	}*/
	
	public boolean generateInAddressTables()
	{
		return this.generateInAddressTables;
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
	
	@Nullable
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
	
	@Nullable
	public AddressRegion.Serializable getAddressRegion()
	{
		return this.addressRegion;
	}
	
	
	
	public CompoundTag serialize()
	{
		CompoundTag spaceLocationTag = new CompoundTag();
		
		spaceLocationTag.putBoolean(IN_STARGATE_NETWORK, this.inStargateNetwork);
		spaceLocationTag.putDouble(PARENT_GRAVITY, this.parentGravity);
		//spaceLocationTag.putBoolean(ALLOW_FACTION_PRESENCE, this.allowFactionPresence);
		spaceLocationTag.putBoolean(GENERATED_ADDRESS, this.generateInAddressTables);
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
		boolean inStargateNetwork = spaceLocationTag.getBoolean(IN_STARGATE_NETWORK);
		double parentGravity = spaceLocationTag.getDouble(PARENT_GRAVITY);
		//boolean allowFactionPresence = spaceLocationTag.getBoolean(ALLOW_FACTION_PRESENCE);
		boolean appearAmongGeneratedAddresses = spaceLocationTag.getBoolean(GENERATED_ADDRESS);
		boolean unityCrystalsGrow = spaceLocationTag.getBoolean(UNITY_CRYSTALS_GROW);
		
		ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(spaceLocationTag.getString(POINT_OF_ORIGIN));
		ResourceKey<Symbols> symbols = Conversion.stringToSymbols(spaceLocationTag.getString(SYMBOLS));
		ResourceKey<AddressRegion> addressRegionKey = Conversion.stringToAddressRegionKey(spaceLocationTag.getString(ADDRESS_REGION));
		
		SpaceLocation spaceLocation = new SpaceLocation(Optional.empty(), inStargateNetwork, parentGravity, /*allowFactionPresence, */appearAmongGeneratedAddresses, unityCrystalsGrow, Optional.ofNullable(pointOfOrigin), Optional.ofNullable(symbols), Optional.ofNullable(addressRegionKey));
		spaceLocation.dimension = Conversion.stringToDimension(spaceLocationTag.getString(DIMENSION));
		return spaceLocation;
	}
	
	
	
	public static SpaceLocation defaultSpaceLocation(ResourceKey<Level> dimension)
	{
		SpaceLocation spaceLocation = new SpaceLocation(Optional.empty(), true, 0.0, /*false, */true, false, Optional.empty(), Optional.empty(), Optional.empty());
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
	
	/**
	 * @param dimension Dimension the corresponding Space Location of which is being searched for
	 * @return Space Location if Dimension is tied to any or null
	 */
	@Nullable
	public static SpaceLocation fromDimensionNullable(ResourceKey<Level> dimension)
	{
		return DIMENSION_SPACE_LOCATIONS.get(dimension);
	}
	
	/**
	 * If no Space Location is registered for the provided Dimension yet, it registers one
	 * @param server Current Minecraft Server
	 * @param dimension Dimension the corresponding Space Location of which is being searched for
	 * @return Space Location tied to the provided Dimension
	 */
	public static SpaceLocation fromDimension(MinecraftServer server, ResourceKey<Level> dimension)
	{
		return DIMENSION_SPACE_LOCATIONS.computeIfAbsent(dimension, dimensionKey -> addSpaceLocation(dimensionKey, createNewSpaceLocation(server, dimensionKey)));
	}
	
	public static SpaceLocation createNewSpaceLocation(MinecraftServer server, ResourceKey<Level> dimension)
	{
		// Try finding a template for this Space Location
		SpaceLocation template = null;
		for(Map.Entry<String, SpaceLocation> templateEntry : TEMPLATES.subMap("", dimension.location().toString()).entrySet()) // <- Submap so we're only searching through possible prefixes
		{
			if(dimension.location().toString().startsWith(templateEntry.getKey()))
				template = templateEntry.getValue(); // Update with template that fits the prefix the best
		}
		
		SpaceLocation spaceLocation;
		if(template != null)
		{
			// Create a new Space Location based on an existing template
			spaceLocation = template.createCopy();
			// Generate a new Address Region for the Space Location
			if(template.templateInfo.generateAddressRegion)
			{
				// Assign Space Location to a Galaxy if possible (The only case in which it doesn't get assigned to at least one Galaxy is when the provided list of galaxies is empty)
				if(template.templateInfo.galaxies != null)
				{
					List<Galaxy.Serializable> galaxyList = new ArrayList<>();
					for(ResourceKey<Galaxy> galaxyKey : template.templateInfo.galaxies)
					{
						Galaxy.Serializable galaxy = Universe.get(server).getGalaxy(galaxyKey);
						if(galaxy != null)
							galaxyList.add(galaxy);
						else
							StargateJourney.LOGGER.error("Could not assign Space Location {} to Galaxy {} because it is not a registered Galaxy", dimension.location(), galaxyKey.location());
					}
					
					// Assign Space Location to Address Region
					AddressRegion.Serializable addressRegion = Universe.get(server).generateNewAddressRegion(server, dimension, galaxyList);
					spaceLocation.setAddressRegion(addressRegion);
				}
				else
				{
					AddressRegion.Serializable addressRegion = Universe.get(server).generateNewAddressRegion(server, dimension, Universe.get(server).getGalaxiesWithGeneratedRegions());
					spaceLocation.setAddressRegion(addressRegion);
				}
			}
			// Assign Space Location to an existing Address Region
			else if(spaceLocation.addressRegionKey != null)
			{
				AddressRegion.Serializable addressRegion = Universe.get(server).getAddressRegionFromKey(spaceLocation.addressRegionKey);
				if(addressRegion != null)
					spaceLocation.setAddressRegion(addressRegion);
				else
					StargateJourney.LOGGER.error("Could not assign Space Location {} to Address Region {} because it is not a registered Address Region", dimension.location(), spaceLocation.addressRegionKey.location());
			}
			
		}
		else
		{
			// Create a default Space Location
			spaceLocation = defaultSpaceLocation(dimension);
			AddressRegion.Serializable addressRegion = Universe.get(server).generateNewAddressRegion(server, dimension, Universe.get(server).getGalaxiesWithGeneratedRegions());
			spaceLocation.setAddressRegion(addressRegion);
		}
		
		return spaceLocation;
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
	
	@Override
	public String toString()
	{
		if(dimension != null)
			return dimension.location().toString();
		
		return super.toString();
	}
	
	//============================================================================================
	//********************************Registering Space Locations*********************************
	//============================================================================================
	
	public static SpaceLocation addSpaceLocation(ResourceKey<Level> dimension, SpaceLocation spaceLocation)
	{
		// Assign Space Location to Dimension
		spaceLocation.dimension = dimension;
		DIMENSION_SPACE_LOCATIONS.put(dimension, spaceLocation);
		
		if(spaceLocation.generateInAddressTables())
			GENERATED_ADDRESS_DIMENSIONS.add(spaceLocation);
		
		return spaceLocation;
	}
	
	public static void registerSpaceLocation(ResourceKey<SpaceLocation> key, SpaceLocation spaceLocation)
	{
		ResourceKey<Level> dimension = Conversion.locationToDimension(key.location());
		
		if(spaceLocation.templateInfo != null)
			TEMPLATES.put(spaceLocation.templateInfo.prefix, spaceLocation); // Separately because we don't want a generated Space Location to be a template
		
		addSpaceLocation(dimension, spaceLocation);
	}
	
	public static void registerSpaceLocations(MinecraftServer server)
	{
		final RegistryAccess registries = server.registryAccess();
		final Registry<SpaceLocation> spaceLocationRegistry = registries.registryOrThrow(SpaceLocation.REGISTRY_KEY);
		
		Set<Map.Entry<ResourceKey<SpaceLocation>, SpaceLocation>> spaceLocationSet = spaceLocationRegistry.entrySet();
		spaceLocationSet.forEach((spaceLocationEntry) ->
				SpaceLocation.registerSpaceLocation(spaceLocationEntry.getKey(), spaceLocationEntry.getValue()));
		StargateJourney.LOGGER.info("Space Locations registered");
	}
	
	
	
	public static void updatePlayerClientGravity(ServerPlayer player)
	{
		// Updates player gravity on the client
		PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundUpdatePlayerGravityPacket(SpaceLocation.fromDimension(player.getServer(), player.getLevel().dimension()).getParentGravity()));
	}
	
	
	/**
	 * @param prefix                Space Location for any Dimension that wasn't defined by a datapack containing this prefix will be made using this Space Location as a template
	 * @param generateAddressRegion If true, the Space Location will generate a new Address Region when being dynamically assigned
	 * @param galaxies              Galaxies this Dimension will be located in (in the case that it has an Address Region); if the list is null, default set of galaxies will be used instead
	 */
	public record TemplateInfo(String prefix, boolean generateAddressRegion, @Nullable List<ResourceKey<Galaxy>> galaxies)
	{
		public static final String PREFIX = "prefix";
		public static final String GENERATE_ADDERSS_REGION = "generate_address_region";
		public static final String GALAXIES = "galaxies";
		
		public static final Codec<TemplateInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf(PREFIX).forGetter(templateInfo -> templateInfo.prefix),
				Codec.BOOL.fieldOf(GENERATE_ADDERSS_REGION).forGetter(templateInfo -> templateInfo.generateAddressRegion),
				Galaxy.RESOURCE_KEY_CODEC.listOf().optionalFieldOf(GALAXIES).forGetter(templateInfo -> Optional.ofNullable(templateInfo.galaxies))
		).apply(instance, TemplateInfo::new));
		
		public TemplateInfo(String prefix, boolean generateAddressRegion, Optional<List<ResourceKey<Galaxy>>> galaxies)
		{
			this(prefix, generateAddressRegion, galaxies.orElse(null));
		}
	}
}
