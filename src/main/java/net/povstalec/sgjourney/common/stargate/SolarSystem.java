package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.Conversion;

public class SolarSystem
{
	public static final ResourceKey<Registry<SolarSystem>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "solar_system"));
	public static final Codec<ResourceKey<SolarSystem>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	private static final Codec<Pair<List<Integer>, Boolean>> ADDRESS = Codec.pair(Codec.INT.listOf().fieldOf("address").codec(), Codec.BOOL.fieldOf("randomizable").codec());
	
    public static final Codec<SolarSystem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(SolarSystem::getName),
			Symbols.RESOURCE_KEY_CODEC.fieldOf("symbols").forGetter(SolarSystem::getSymbols),
			Codec.INT.fieldOf("symbol_prefix").forGetter(SolarSystem::getSymbolPrefix),
			ADDRESS.fieldOf("extragalactic_address").forGetter(SolarSystem::getExtragalacticAddress),
			PointOfOrigin.RESOURCE_KEY_CODEC.fieldOf("point_of_origin").forGetter(SolarSystem::getPointOfOrigin),
			Level.RESOURCE_KEY_CODEC.listOf().fieldOf("dimensions").forGetter(SolarSystem::getDimensions)
			).apply(instance, SolarSystem::new));

	private final String name;
	private final ResourceKey<Symbols> symbols;
	private final int symbolPrefix;
	private final Pair<List<Integer>, Boolean> extragalactic_address;
	private final ResourceKey<PointOfOrigin> point_of_origin;
	private final List<ResourceKey<Level>> dimensions;
	
	public SolarSystem(String name, ResourceKey<Symbols> symbols, int symbolPrefix, Pair<List<Integer>, Boolean> extragalactic_address, ResourceKey<PointOfOrigin> point_of_origin, List<ResourceKey<Level>> dimensions)
	{
		this.name = name;
		this.symbols = symbols;
		this.symbolPrefix = symbolPrefix;
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
	
	public int getSymbolPrefix()
	{
		return symbolPrefix;
	}
	
	public Pair<List<Integer>, Boolean> getExtragalacticAddress()
	{
		return extragalactic_address;
	}
	
	public int[] getAddressArray()
	{
		return  extragalactic_address.getFirst().stream().mapToInt((integer) -> integer).toArray();
	}
	
	public boolean isAddressRandomizable()
	{
		return extragalactic_address.getSecond();
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
	
	/**
	 * Version of Solar System used for Stargate Network
	 * @author Povstalec
	 *
	 */
	public static class Serializable
	{
		public static final String TRANSLATION_NAME = "TranslationName";
		public static final String POINT_OF_ORIGIN = "PointOfOrigin";
		public static final String SYMBOLS = "Sybmols";
		public static final String SYMBOL_PREFIX = "SybmolPrefix";
		public static final String EXTRAGALACTIC_ADDRESS = "ExtragalacticAddress";
		public static final String DIMENSIONS = "Dimensions";
		
		private final String translationName;
		private final ResourceKey<PointOfOrigin> pointOfOrigin;
		private final ResourceKey<Symbols> symbols;
		private final int symbolPrefix;
		private final Address extragalacticAddress;
		private final List<ResourceKey<Level>> dimensions;
		
		private List<Stargate> stargates = new ArrayList<Stargate>();//TODO
		
		public Serializable(String translationName, ResourceKey<PointOfOrigin> pointOfOrigin, ResourceKey<Symbols> symbols, int symbolPrefix, 
				Address extragalacticAddress, List<ResourceKey<Level>> dimensions)
		{
			this.translationName = translationName;
			this.pointOfOrigin = pointOfOrigin;
			this.symbols = symbols;
			this.symbolPrefix = symbolPrefix;
			this.extragalacticAddress = extragalacticAddress;
			this.dimensions = dimensions;
		}
		
		public Component getName()
		{
			return Component.translatable(translationName);
		}
		
		public ResourceKey<PointOfOrigin> getPointOfOrigin() //TODO
		{
			return pointOfOrigin;
		}
		
		public ResourceKey<Symbols> getSymbols() //TODO
		{
			return symbols;
		}
		
		public int getSymbolPrefix()
		{
			return symbolPrefix;
		}
		
		public Address getExtragalacticAddress()
		{
			return extragalacticAddress;
		}
		
		public List<ResourceKey<Level>> getDimensions()
		{
			return dimensions;
		}
		
		public CompoundTag serialize()
		{
			CompoundTag solarSystemTag = new CompoundTag();
			
			solarSystemTag.putString(TRANSLATION_NAME, this.translationName);
			solarSystemTag.putString(POINT_OF_ORIGIN, this.pointOfOrigin.location().toString());
			solarSystemTag.putString(SYMBOLS, this.symbols.location().toString());
			solarSystemTag.putInt(SYMBOL_PREFIX, this.symbolPrefix);
			solarSystemTag.putIntArray(EXTRAGALACTIC_ADDRESS, this.extragalacticAddress.toArray());
			
			//TODO Dimensions
			CompoundTag dimensionsTag = new CompoundTag();
			dimensions.stream().forEach(dimension ->
			{
				dimensionsTag.putString(dimension.location().toString(), dimension.location().toString());
			});
			solarSystemTag.put(DIMENSIONS, dimensionsTag);
			
			//TODO Stargates
			
			return solarSystemTag;
		}
		
		public static SolarSystem.Serializable deserialize(CompoundTag solarSystemTag)
		{
			String translationName = solarSystemTag.getString(TRANSLATION_NAME);
			ResourceKey<PointOfOrigin> pointOfOrigin = Conversion.stringToPointOfOrigin(solarSystemTag.getString(POINT_OF_ORIGIN));
			ResourceKey<Symbols> symbols = Conversion.stringToSymbols(solarSystemTag.getString(SYMBOLS));
			int symbolPrefix = solarSystemTag.getInt(SYMBOL_PREFIX);
			Address extragalacticAddress = new Address(solarSystemTag.getIntArray(EXTRAGALACTIC_ADDRESS));
			
			List<ResourceKey<Level>> dimensions = new ArrayList<ResourceKey<Level>>();
			solarSystemTag.getCompound(DIMENSIONS).getAllKeys().forEach(dimensionString ->
			{
				dimensions.add(Conversion.stringToDimension(dimensionString));
			});
			
			//TODO Stargates
			
			return new SolarSystem.Serializable(translationName, pointOfOrigin, symbols, symbolPrefix, extragalacticAddress, dimensions);
		}
	}
}
