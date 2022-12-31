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

public class StarSystem
{
	public static final ResourceKey<Registry<StarSystem>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "star_system"));
	public static final Codec<ResourceKey<StarSystem>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
    public static final Codec<StarSystem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(StarSystem::getName),
			Codec.INT.listOf().fieldOf("address").forGetter(StarSystem::getAddress),
			PointOfOrigin.RESOURCE_KEY_CODEC.fieldOf("point_of_origin").forGetter(StarSystem::getPointOfOrigin),
			Level.RESOURCE_KEY_CODEC.listOf().fieldOf("dimensions").forGetter(StarSystem::getDimensions)
			).apply(instance, StarSystem::new));

	private final String name;
	private final List<Integer> address;
	private final ResourceKey<PointOfOrigin> point_of_origin;
	private final List<ResourceKey<Level>> dimensions;
	
	public StarSystem(String name, List<Integer> address, ResourceKey<PointOfOrigin> point_of_origin, List<ResourceKey<Level>> dimensions)
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
	
	public static StarSystem getPlanet(Level level, String part1, String part2)
	{
        return getPlanet(level, new ResourceLocation(part1, part2));
	}
	
	public static StarSystem getPlanet(Level level, ResourceLocation planet)
	{
		RegistryAccess registries = level.getServer().registryAccess();
        Registry<StarSystem> registry = registries.registryOrThrow(StarSystem.REGISTRY_KEY);
        
        return registry.get(planet);
	}
}
