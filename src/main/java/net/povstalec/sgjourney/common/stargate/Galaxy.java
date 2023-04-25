package net.povstalec.sgjourney.common.stargate;

import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class Galaxy
{
	public static final ResourceKey<Registry<Galaxy>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "galaxy"));
    
	private static final Codec<Pair<ResourceKey<SolarSystem>, List<Integer>>> SYSTEM_WITH_ADDRESS = Codec.pair(SolarSystem.RESOURCE_KEY_CODEC.fieldOf("solar_system").codec(), Codec.INT.listOf().fieldOf("address").codec());
	
    public static final Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Galaxy::getName),
    		GalaxyType.RESOURCE_KEY_CODEC.fieldOf("type").forGetter(Galaxy::getType),
			Symbols.RESOURCE_KEY_CODEC.fieldOf("default_symbols").forGetter(Galaxy::getDefaultSymbols),
			SYSTEM_WITH_ADDRESS.listOf().fieldOf("solar_systems").forGetter(Galaxy::getSystems)
			).apply(instance, Galaxy::new));

	private final String name;
	private final ResourceKey<GalaxyType> type;
	private final ResourceKey<Symbols> defaultSymbols;
	
	private final List<Pair<ResourceKey<SolarSystem>, List<Integer>>> systems;
	
	public Galaxy(String name, ResourceKey<GalaxyType> type, ResourceKey<Symbols> defaultSymbols, List<Pair<ResourceKey<SolarSystem>, List<Integer>>> systems)
	{
		this.name = name;
		this.type = type;
		this.defaultSymbols = defaultSymbols;
		this.systems = systems;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ResourceKey<GalaxyType> getType()
	{
		return type;
	}
	
	public ResourceKey<Symbols> getDefaultSymbols()
	{
		return defaultSymbols;
	}
	
	public List<Pair<ResourceKey<SolarSystem>, List<Integer>>> getSystems()
	{
		return systems;
	}
	
	public static Galaxy getGalaxy(Level level, String part1, String part2)
	{
        return getGalaxy(level, new ResourceLocation(part1, part2));
	}
	
	public static Galaxy getGalaxy(Level level, ResourceLocation galaxy)
	{
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<Galaxy> registry = registries.registryOrThrow(Galaxy.REGISTRY_KEY);
        
        return registry.get(galaxy);
	}
}
