package net.povstalec.sgjourney.stargate;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class Planet
{
	public static final ResourceKey<Registry<Planet>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "planet"));
	public static final Codec<ResourceKey<Planet>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
    public static final Codec<Planet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Planet::getName),
			Codec.INT.listOf().fieldOf("address").forGetter(Planet::getAddress),
			PointOfOrigin.RESOURCE_KEY_CODEC.fieldOf("point_of_origin").forGetter(Planet::getPointOfOrigin),
			Level.RESOURCE_KEY_CODEC.listOf().fieldOf("dimensions").forGetter(Planet::getDimensions)
			).apply(instance, Planet::new));

	private final String name;
	private final List<Integer> address;
	private final ResourceKey<PointOfOrigin> point_of_origin;
	private final List<ResourceKey<Level>> dimensions;
	
	public Planet(String name, List<Integer> address, ResourceKey<PointOfOrigin> point_of_origin, List<ResourceKey<Level>> dimensions)
	{
		this.name = name;
		this.address = address;
		this.point_of_origin = point_of_origin;
		this.dimensions = dimensions;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<Integer> getAddress()
	{
		return address;
	}
	
	public int[] getAddressArray()
	{
		return address.stream().mapToInt((integer) -> integer).toArray();
	}
	
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		return point_of_origin;
	}
	
	public List<ResourceKey<Level>> getDimensions()
	{
		return dimensions;
	}
	
	public static Planet getPlanet(Level level, String part1, String part2)
	{
        return getPlanet(level, new ResourceLocation(part1, part2));
	}
	
	public static Planet getPlanet(Level level, ResourceLocation planet)
	{
		RegistryAccess registries = level.getServer().registryAccess();
        Registry<Planet> registry = registries.registryOrThrow(Planet.REGISTRY_KEY);
        
        return registry.get(planet);
	}
}
