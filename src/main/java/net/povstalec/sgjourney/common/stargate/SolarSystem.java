package net.povstalec.sgjourney.common.stargate;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class SolarSystem
{
	public static final ResourceKey<Registry<SolarSystem>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "solar_system"));
	public static final Codec<ResourceKey<SolarSystem>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
    public static final Codec<SolarSystem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(SolarSystem::getName),
			Symbols.RESOURCE_KEY_CODEC.fieldOf("symbols").forGetter(SolarSystem::getSymbols),
			Codec.INT.listOf().fieldOf("extragalactic_address").forGetter(SolarSystem::getExtragalacticAddress),
			PointOfOrigin.RESOURCE_KEY_CODEC.fieldOf("point_of_origin").forGetter(SolarSystem::getPointOfOrigin),
			Level.RESOURCE_KEY_CODEC.listOf().fieldOf("dimensions").forGetter(SolarSystem::getDimensions)
			).apply(instance, SolarSystem::new));

	private final String name;
	private final ResourceKey<Symbols> symbols;
	private final List<Integer> extragalactic_address;
	private final ResourceKey<PointOfOrigin> point_of_origin;
	private final List<ResourceKey<Level>> dimensions;
	
	public SolarSystem(String name, ResourceKey<Symbols> symbols, List<Integer> extragalactic_address, ResourceKey<PointOfOrigin> point_of_origin, List<ResourceKey<Level>> dimensions)
	{
		this.name = name;
		this.symbols = symbols;
		this.extragalactic_address = extragalactic_address;
		this.point_of_origin = point_of_origin;
		this.dimensions = dimensions;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ResourceKey<Symbols> getSymbols()
	{
		return symbols;
	}
	
	public List<Integer> getExtragalacticAddress()
	{
		return extragalactic_address;
	}
	
	public int[] getAddressArray()
	{
		return extragalactic_address.stream().mapToInt((integer) -> integer).toArray();
	}
	
	public ResourceKey<PointOfOrigin> getPointOfOrigin()
	{
		return point_of_origin;
	}
	
	public List<ResourceKey<Level>> getDimensions()
	{
		return dimensions;
	}
	
	public static SolarSystem getSolarSystem(Level level, String part1, String part2)
	{
        return getSolarSystem(level, new ResourceLocation(part1, part2));
	}
	
	public static SolarSystem getSolarSystem(Level level, ResourceLocation solarSystem)
	{
		RegistryAccess registries = level.getServer().registryAccess();
        Registry<SolarSystem> registry = registries.registryOrThrow(SolarSystem.REGISTRY_KEY);
        
        return registry.get(solarSystem);
	}
}
