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

public class Galaxy
{
	public static final ResourceKey<Registry<Galaxy>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "galaxy"));
    
    public static final Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Galaxy::getName),
    		GalaxyType.RESOURCE_KEY_CODEC.fieldOf("type").forGetter(Galaxy::getType),
			Symbols.RESOURCE_KEY_CODEC.fieldOf("symbols").forGetter(Galaxy::getSymbols),
			Codec.INT.fieldOf("symbol").forGetter(Galaxy::getSymbol),
			Planet.RESOURCE_KEY_CODEC.listOf().fieldOf("planets").forGetter(Galaxy::getPlanets)
			).apply(instance, Galaxy::new));

	private final String name;
	private final ResourceKey<GalaxyType> type;
	private final ResourceKey<Symbols> symbols;
	private final int symbol;
	private final List<ResourceKey<Planet>> planets;
	
	public Galaxy(String name, ResourceKey<GalaxyType> type, ResourceKey<Symbols> symbols, int symbol, List<ResourceKey<Planet>> planets)
	{
		this.name = name;
		this.type = type;
		this.symbols = symbols;
		this.symbol = symbol;
		this.planets = planets;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ResourceKey<GalaxyType> getType()
	{
		return type;
	}
	
	public ResourceKey<Symbols> getSymbols()
	{
		return symbols;
	}
	
	public int getSymbol()
	{
		return symbol;
	}
	
	public List<ResourceKey<Planet>> getPlanets()
	{
		return planets;
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
