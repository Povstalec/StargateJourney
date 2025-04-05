package net.povstalec.sgjourney.common.sgjourney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

import javax.annotation.Nullable;

public class Galaxy
{
	public static final ResourceKey<Registry<Galaxy>> REGISTRY_KEY = ResourceKey.createRegistryKey(StargateJourney.sgjourneyLocation("galaxy"));
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
        return getGalaxy(level, StargateJourney.location(part1, part2));
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
	public static final class Serializable
	{
		public static final String SOLAR_SYSTEMS = "solar_systems";
		public static final String POINTS_OF_ORIGIN = "points_of_origin";
		
		private final ResourceKey<Galaxy> galaxyKey;
		private final Galaxy galaxy;
		
		private HashMap<Address.Immutable, SolarSystem.Serializable> solarSystems;
		private List<ResourceKey<PointOfOrigin>> pointsOfOrigin;
		
		public Serializable(ResourceKey<Galaxy> galaxyKey, Galaxy galaxy, 
				HashMap<Address.Immutable, SolarSystem.Serializable> solarSystems, List<ResourceKey<PointOfOrigin>> pointsOfOrigin)
		{
			this.galaxyKey = galaxyKey;
			this.galaxy = galaxy;
			
			this.solarSystems = solarSystems;
			this.pointsOfOrigin = pointsOfOrigin;
			
			this.solarSystems.entrySet().stream().forEach(solarSystemEntry ->
			{
				solarSystemEntry.getValue().addToGalaxy(this, solarSystemEntry.getKey());
			});
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
		
		public boolean containsSolarSystem(Address.Immutable address)
		{
			return this.solarSystems.containsKey(address);
		}
		
		@Nullable
		public SolarSystem.Serializable getSolarSystem(Address.Immutable address)
		{
			return this.solarSystems.get(address);
		}
		
		public void addSolarSystem(Address.Immutable address, SolarSystem.Serializable solarSystem)
		{
			this.solarSystems.put(address, solarSystem);
			//System.out.println("Added " + solarSystem.getName() + " to " + this.getKey().location().toString() + " as " + address.toString());
		}
		
		public void removeSolarSystem(Address.Immutable address)
		{
			if(containsSolarSystem(address))
				this.solarSystems.remove(address);
		}
		
		@Nullable
		public SolarSystem.Serializable getRandomSolarSystem(long seed)
		{
			int size = this.solarSystems.size();
			
			if(size < 1)
				return null;
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			SolarSystem.Serializable randomSolarSystem = (SolarSystem.Serializable) this.solarSystems.entrySet().stream().toArray()[randomValue];
			
			return randomSolarSystem;
		}
		
		public void addPointOfOrigin(ResourceKey<PointOfOrigin> pointOfOrigin)
		{
			if(!this.pointsOfOrigin.contains(pointOfOrigin))
				this.pointsOfOrigin.add(pointOfOrigin);
		}
		
		public ResourceKey<PointOfOrigin> getRandomPointOfOrigin(long seed)
		{
			int size = this.pointsOfOrigin.size();
			
			if(size < 1)
				return PointOfOrigin.defaultPointOfOrigin();
			
			Random random = new Random(seed);
			
			int randomValue = random.nextInt(0, size);
			
			return this.pointsOfOrigin.get(randomValue);
		}
		
		
		
		public CompoundTag serialize()
		{
			CompoundTag galaxyTag = new CompoundTag();
			//galaxyTag.putString(GALAXY_KEY, galaxyKey.location().toString());

			//System.out.println("Galaxy: " + galaxyKey.location().toString());
			CompoundTag solarSystemsTag = new CompoundTag();
			solarSystems.entrySet().forEach(solarSystem ->
			{
				solarSystemsTag.putIntArray(solarSystem.getKey().toString(), solarSystem.getValue().getExtragalacticAddress().toArray());
				//System.out.println("Serializing: " + solarSystem.getValue().getName() + " " + solarSystem.getKey().toString());
			});
			
			galaxyTag.put(SOLAR_SYSTEMS, solarSystemsTag);

			CompoundTag pointOfOriginTag = new CompoundTag();
			pointsOfOrigin.stream().forEach(pointOfOrigin ->
			{
				String pointOfOriginString = pointOfOrigin.location().toString();
				pointOfOriginTag.putString(pointOfOriginString, pointOfOriginString);
			});
			
			galaxyTag.put(POINTS_OF_ORIGIN, pointOfOriginTag);
			
			return galaxyTag;
		}
		
		public static Galaxy.Serializable deserialize(MinecraftServer server, HashMap<Address.Immutable, SolarSystem.Serializable> solarSystems,
				Registry<Galaxy> galaxyRegistry, ResourceKey<Galaxy> galaxyKey, CompoundTag galaxyTag)
		{
			Galaxy galaxy = galaxyRegistry.get(galaxyKey);
			
			HashMap<Address.Immutable, SolarSystem.Serializable> galaxySolarSystems = new HashMap<Address.Immutable, SolarSystem.Serializable>();
			
			CompoundTag solarSystemsTag = galaxyTag.getCompound(SOLAR_SYSTEMS);

			//System.out.println("Galaxy: " + galaxyKey.location().toString());
			solarSystemsTag.getAllKeys().forEach(addressString ->
			{
				Address.Immutable extragalacticAddress = new Address(solarSystemsTag.getIntArray(addressString)).immutable(); // 8-chevron address
				Address.Immutable address = new Address(addressString).immutable(); // 7-chevron address
				
				if(solarSystems.containsKey(extragalacticAddress))
				{
					SolarSystem.Serializable solarSystem = solarSystems.get(extragalacticAddress);
					galaxySolarSystems.put(address, solarSystem);
					//System.out.println("Deserializing: " + solarSystems.get(extragalacticAddress).getName() + " " + address.toString());
				}
			});
			
			CompoundTag pointOfOriginTag = galaxyTag.getCompound(POINTS_OF_ORIGIN);
			List<ResourceKey<PointOfOrigin>> pointsOfOrigin = new ArrayList<ResourceKey<PointOfOrigin>>();
			
			pointOfOriginTag.getAllKeys().forEach(pointOfOriginString ->
			{
				pointsOfOrigin.add(Conversion.stringToPointOfOrigin(pointOfOriginString));
			});
			
			return new Galaxy.Serializable(galaxyKey, galaxy, galaxySolarSystems, pointsOfOrigin);
		}
	}
}
