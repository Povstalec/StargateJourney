package net.povstalec.sgjourney.common.stargate;

import java.util.HashMap;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.GalaxyInit;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Galaxy
{
	public static final ResourceKey<Registry<Galaxy>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "galaxy"));
	public static final Codec<ResourceKey<Galaxy>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
    public static final Codec<Galaxy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Galaxy::getName),
    		GalaxyInit.CODEC.fieldOf("type").forGetter(Galaxy::getType),
			Symbols.RESOURCE_KEY_CODEC.fieldOf("default_symbols").forGetter(Galaxy::getDefaultSymbols)
			).apply(instance, Galaxy::new));

	private final String name;
	private final GalaxyType type;
	private final ResourceKey<Symbols> defaultSymbols;
	
	public Galaxy(String name, GalaxyType type, ResourceKey<Symbols> defaultSymbols)
	{
		this.name = name;
		this.type = type;
		this.defaultSymbols = defaultSymbols;
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
	 * Version of Galaxy used for Stargate Network and Universe
	 * @author Povstalec
	 *
	 */
	public static class Serializable
	{
		public static final String GALAXY_KEY = "GalaxyKey";
		public static final String SOLAR_SYSTEMS = "SolarSystems";
		
		private final ResourceKey<Galaxy> galaxyKey;
		private final Galaxy galaxy;
		
		private HashMap<Address, SolarSystem.Serializable> solarSystems;
		
		public Serializable(ResourceKey<Galaxy> galaxyKey, Galaxy galaxy, 
				HashMap<Address, SolarSystem.Serializable> solarSystems)
		{
			this.galaxyKey = galaxyKey;
			this.galaxy = galaxy;
			
			this.solarSystems = solarSystems;
		}
		
		public ResourceKey<Galaxy> getKey()
		{
			return this.galaxyKey;
		}
		
		public Component getTranslationName()
		{
			return Component.translatable(galaxy.getName());
		}
		
		public ResourceKey<Symbols> getDefaultSymbols()
		{
			return galaxy.getDefaultSymbols();
		}
		
		public int getSize()
		{
			return galaxy.getType().getSize();
		}
		
		public void printSolarSystems()
		{
			this.solarSystems.entrySet().stream().forEach(solarSystemEntry ->
			{
				System.out.println("--- " + solarSystemEntry.getKey().toString() + " " + solarSystemEntry.getValue().getName());
			});
		}
		
		public boolean containsSolarSystem(Address address)
		{
			return this.solarSystems.containsKey(address);
		}
		
		public Optional<SolarSystem.Serializable> getSolarSystem(Address address)
		{
			if(!containsSolarSystem(address))
				return Optional.empty();
			
			return Optional.of(this.solarSystems.get(address));
		}
		
		public void addSolarSystem(Address address, SolarSystem.Serializable solarSystem)
		{
			this.solarSystems.put(address, solarSystem);
		}
		
		public void removeSolarSystem(Address address)
		{
			if(containsSolarSystem(address))
				this.solarSystems.remove(address);
		}
		
		
		
		public CompoundTag serialize()
		{
			CompoundTag galaxyTag = new CompoundTag();
			
			galaxyTag.putString(GALAXY_KEY, galaxyKey.location().toString());
			
			CompoundTag solarSystemsTag = new CompoundTag();
			solarSystems.entrySet().forEach(solarSystem ->
			{
				solarSystemsTag.putIntArray(solarSystem.getKey().toString(), solarSystem.getValue().getExtragalacticAddress().toArray());
			});
			
			galaxyTag.put(SOLAR_SYSTEMS, solarSystemsTag);
			
			return galaxyTag;
		}
		
		public static Galaxy.Serializable deserialize(MinecraftServer server, HashMap<Address, SolarSystem.Serializable> solarSystems, 
				Registry<Galaxy> galaxyRegistry, CompoundTag galaxyTag)
		{
			ResourceKey<Galaxy> galaxyKey = Conversion.stringToGalaxyKey(galaxyTag.getString(GALAXY_KEY));
			
			Galaxy galaxy = galaxyRegistry.get(galaxyKey);
			
			HashMap<Address, SolarSystem.Serializable> galaxySolarSystems = new HashMap<Address, SolarSystem.Serializable>();
			
			CompoundTag solarSystemsTag = galaxyTag.getCompound(SOLAR_SYSTEMS);
			
			solarSystemsTag.getAllKeys().forEach(addressString ->
			{
				Address extragalacticAddress = new Address(solarSystemsTag.getIntArray(addressString)); // 8-chevron address
				Address address = new Address(addressString); // 7-chevron address
				
				if(solarSystems.containsKey(extragalacticAddress))
					galaxySolarSystems.put(address, solarSystems.get(extragalacticAddress));
			});
			
			return new Galaxy.Serializable(galaxyKey, galaxy, solarSystems);
		}
	}
}
