package net.povstalec.sgjourney.common.stargate;

import java.util.HashMap;
import java.util.List;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.GalaxyInit;

public class Galaxy
{
	public static final ResourceKey<Registry<Galaxy>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "galaxy"));
    
	private static final Codec<Pair<List<Integer>, Boolean>> ADDRESS = Codec.pair(Codec.INT.listOf().fieldOf("address").codec(), Codec.BOOL.fieldOf("randomizable").codec());
	private static final Codec<Pair<ResourceKey<SolarSystem>, Pair<List<Integer>, Boolean>>> SYSTEM_WITH_ADDRESS = Codec.pair(SolarSystem.RESOURCE_KEY_CODEC.fieldOf("solar_system").codec(), ADDRESS.fieldOf("address").codec());
	
    public static final Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Galaxy::getName),
    		GalaxyInit.CODEC.fieldOf("type").forGetter(Galaxy::getType),
			Symbols.RESOURCE_KEY_CODEC.fieldOf("default_symbols").forGetter(Galaxy::getDefaultSymbols),
			SYSTEM_WITH_ADDRESS.listOf().fieldOf("solar_systems").forGetter(Galaxy::getSystems)
			).apply(instance, Galaxy::new));

	private final String name;
	private final GalaxyType type;
	private final ResourceKey<Symbols> defaultSymbols;
	private final List<Pair<ResourceKey<SolarSystem>, Pair<List<Integer>, Boolean>>> systems;
	
	public Galaxy(String name, GalaxyType type, ResourceKey<Symbols> defaultSymbols, List<Pair<ResourceKey<SolarSystem>, Pair<List<Integer>, Boolean>>> systems)
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
	
	public GalaxyType getType()
	{
		return type;
	}
	
	public ResourceKey<Symbols> getDefaultSymbols()
	{
		return defaultSymbols;
	}
	
	public List<Pair<ResourceKey<SolarSystem>, Pair<List<Integer>, Boolean>>> getSystems()
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
	
	/**
	 * Version of Galaxy used for Stargate Network
	 * @author Povstalec
	 *
	 */
	public static class Serializable
	{
		private final String translationName;
		private final GalaxyType type;
		private final Symbols defaultSymbols;
		private final HashMap<Address, SolarSystem.Serializable> solarSystems;
		
		public Serializable(String translationName, GalaxyType type, Symbols defaultSymbols, 
				HashMap<Address, SolarSystem.Serializable> solarSystems)
		{
			this.translationName = translationName;
			this.type = type;
			this.defaultSymbols = defaultSymbols;
			this.solarSystems = solarSystems;
		}
		
		public Component getName()
		{
			return Component.translatable(translationName);
		}
		
		public Symbols getDefaultSymbols()
		{
			return defaultSymbols;
		}
		
		public GalaxyType getType()
		{
			return type;
		}
		
		public HashMap<Address, SolarSystem.Serializable> getSolarSystems()
		{
			return solarSystems;
		}
	}
}
