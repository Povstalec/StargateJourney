package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Class that represents a region of space with one Extragalactic Address, which may consist of multiple Space Locations, intended as a replacement for the SolarSystem class
 */
public class AddressRegion
{
	public static final ResourceKey<Registry<AddressRegion>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "address_region"));
	public static final Codec<ResourceKey<AddressRegion>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	private static final Codec<Pair<List<Integer>, Boolean>> ADDRESS = Codec.pair(Codec.INT.listOf().fieldOf("address").codec(), Codec.BOOL.fieldOf("randomizable").codec());
	private static final Codec<Pair<ResourceKey<Galaxy>, Pair<List<Integer>, Boolean>>> GALAXY_AND_ADDRESS = Codec.pair(Galaxy.RESOURCE_KEY_CODEC.fieldOf("galaxy").codec(), ADDRESS.fieldOf("address").codec());
	
	public static final Codec<AddressRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(addressRegion -> addressRegion.name),
			Codec.INT.fieldOf("symbol_prefix").forGetter(addressRegion -> addressRegion.symbolPrefix),
			ADDRESS.fieldOf("extragalactic_address").forGetter(addressRegion -> addressRegion.extragalacticAddress),
			GALAXY_AND_ADDRESS.listOf().optionalFieldOf("galactic_addresses").forGetter(addressRegion -> Optional.ofNullable(addressRegion.galacticAddresses))
	).apply(instance, AddressRegion::new));
	
	private String name; // Translation name for this Address Region
	private int symbolPrefix; // Prefix this Region's Extragalactic Address uses when generating randomly
	private Pair<List<Integer>, Boolean> extragalacticAddress; // Address of this Region independent of any Galaxy
	@Nullable
	private List<Pair<ResourceKey<Galaxy>, Pair<List<Integer>, Boolean>>> galacticAddresses; // Galaxies this Address Region is a part of
	
	//TODO List of Stargates, Primary Address/Stargate
	//TODO List of Transporters
	
	public AddressRegion(String name, int symbolPrefix,  Pair<List<Integer>, Boolean> extragalacticAddress, Optional<List<Pair<ResourceKey<Galaxy>, Pair<List<Integer>, Boolean>>>> addresses)
	{
		this.name = name;
		this.symbolPrefix = symbolPrefix;
		this.extragalacticAddress = extragalacticAddress; //TODO Check Address length and throw if too short or long
		this.galacticAddresses = addresses.orElse(null); //TODO Check Address length and throw if too short or long
	}
}
