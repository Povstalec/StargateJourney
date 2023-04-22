package net.povstalec.sgjourney.stargate;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class AddressTable
{
	public static final ResourceLocation ADDRESS_TABLES_LOCATION = new ResourceLocation(StargateJourney.MODID, "address_tables");
	public static final ResourceKey<Registry<AddressTable>> REGISTRY_KEY = ResourceKey.createRegistryKey(ADDRESS_TABLES_LOCATION);
	public static final Codec<ResourceKey<AddressTable>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	//TODO Add probability/rolls
	private static final Codec<Pair<ResourceKey<Level>, Integer>> ADDRESS_WITH_PROBABILITY = Codec.pair(Level.RESOURCE_KEY_CODEC.fieldOf("address").codec(), Codec.INT.fieldOf("probability").codec());
	
	public static final Codec<AddressTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("include_generated_addresses").forGetter(AddressTable::includeGeneratedAddresses),
			Level.RESOURCE_KEY_CODEC.listOf().fieldOf("dimensions").forGetter(AddressTable::getDimensions)
			).apply(instance, AddressTable::new));

	private final boolean includeGeneratedAddresses;
	private final List<ResourceKey<Level>> dimensions;
	
	public AddressTable(boolean includeGeneratedAddresses, List<ResourceKey<Level>> dimensions)
	{
		this.includeGeneratedAddresses = includeGeneratedAddresses;
		this.dimensions = dimensions;
	}
	
	public boolean includeGeneratedAddresses()
	{
		return includeGeneratedAddresses;
	}
	
	public List<ResourceKey<Level>> getDimensions()
	{
		return dimensions;
	}
}
