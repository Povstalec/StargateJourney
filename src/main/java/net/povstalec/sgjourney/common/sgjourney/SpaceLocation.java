package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Class that is intended to represent a single Minecraft Dimension's attributes that Stargate Journey can work with
 */
public class SpaceLocation
{
	public static final ResourceKey<Registry<SpaceLocation>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "space_location"));
	public static final Codec<ResourceKey<SpaceLocation>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<SpaceLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.optionalFieldOf("parent_gravity", 0.0).forGetter(spaceLocation -> spaceLocation.parentGravity),
			Codec.BOOL.optionalFieldOf("allow_faction_presence", false).forGetter(spaceLocation -> spaceLocation.allowFactionPresence), //TODO Maybe change this to a codec for a faction info class
			Codec.BOOL.optionalFieldOf("generated_address", false).forGetter(spaceLocation -> spaceLocation.appearAmongGeneratedAddresses),
			Codec.BOOL.optionalFieldOf("unity_crystals_grow", false).forGetter(spaceLocation -> spaceLocation.unityCrystalsGrow),
			
			PointOfOrigin.RESOURCE_KEY_CODEC.optionalFieldOf("point_of_origin").forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.pointOfOrigin)),
			Symbols.RESOURCE_KEY_CODEC.optionalFieldOf("symbols").forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.symbols)),
			//TODO Coordinates
			AddressRegion.RESOURCE_KEY_CODEC.optionalFieldOf("address_region").forGetter(spaceLocation -> Optional.ofNullable(spaceLocation.addressRegion))
	).apply(instance, SpaceLocation::new));
	
	//TODO Something to control whether Stargates work there or not
	
	private double parentGravity; // Gravity of the parent body this location might be orbiting - 0.07 for Cavum Tenebrae
	private boolean allowFactionPresence; // If true, factions will be able to appear in this Dimension
	private boolean appearAmongGeneratedAddresses; // If true, dimension address will appear in Address Tables with generated addresses
	private boolean unityCrystalsGrow; // If true, Unity Crystals can grow in this location
	
	@Nullable
	private ResourceKey<PointOfOrigin> pointOfOrigin; // Point of Origin any Stargates generated in this Space Location will use
	@Nullable
	private ResourceKey<Symbols> symbols; // Symbols any Stargates generated in this Space Location will use
	@Nullable
	private ResourceKey<AddressRegion> addressRegion; // Address Region this Space Location is a part of
	
	public SpaceLocation(double parentGravity, boolean allowFactionPresence, boolean appearAmongGeneratedAddresses, boolean unityCrystlasGrow,
						 Optional<ResourceKey<PointOfOrigin>> pointOfOrigin, Optional<ResourceKey<Symbols>> symbols, Optional<ResourceKey<AddressRegion>> addressRegion)
	{
		this.parentGravity = parentGravity;
		this.allowFactionPresence = allowFactionPresence;
		this.appearAmongGeneratedAddresses = appearAmongGeneratedAddresses;
		this.unityCrystalsGrow = unityCrystlasGrow;
		
		this.pointOfOrigin = pointOfOrigin.orElse(null);
		this.symbols = symbols.orElse(null);
		this.addressRegion = addressRegion.orElse(null);
	}
}
