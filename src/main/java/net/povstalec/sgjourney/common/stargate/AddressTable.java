package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;

public class AddressTable
{
	private static final String GENERATED = "Generated";
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public static final ResourceLocation ADDRESS_TABLES_LOCATION = new ResourceLocation(StargateJourney.MODID, "address_table");
	public static final ResourceKey<Registry<AddressTable>> REGISTRY_KEY = ResourceKey.createRegistryKey(ADDRESS_TABLES_LOCATION);
	public static final Codec<ResourceKey<AddressTable>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	private static final Codec<Pair<ResourceKey<Level>, Integer>> DIMENSION_WEIGHT = Codec.pair(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").codec(), Codec.INT.fieldOf("weight").codec());
	
	public static final Codec<AddressTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("include_generated_addresses").forGetter(AddressTable::includeGeneratedAddresses),
			DIMENSION_WEIGHT.listOf().fieldOf("dimensions").forGetter(AddressTable::getDimensions)
			).apply(instance, AddressTable::new));
	
	private final boolean includeGeneratedAddresses;
	private final List<Pair<ResourceKey<Level>, Integer>> dimensions;
	
	public AddressTable(boolean includeGeneratedAddresses, List<Pair<ResourceKey<Level>, Integer>> dimensions)
	{
		this.includeGeneratedAddresses = includeGeneratedAddresses;
		this.dimensions = dimensions;
	}
	
	public boolean includeGeneratedAddresses()
	{
		return includeGeneratedAddresses;
	}
	
	public List<Pair<ResourceKey<Level>, Integer>> getDimensions()
	{
		return dimensions;
	}
	
	public static AddressTable getAddressTable(Level level, ResourceLocation addressTable)
	{
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<AddressTable> registry = registries.registryOrThrow(AddressTable.REGISTRY_KEY);
        
        return registry.get(addressTable);
	}
	
	public static List<String> getRandomGeneratedDimension(Level level)
	{
		List<String> dimensions = new ArrayList<String>();
		CompoundTag solarSystems = Universe.get(level).getSolarSystems();
		solarSystems.getAllKeys().stream().forEach(systemID ->
		{
			boolean generated = Universe.get(level).getSolarSystem(systemID).getBoolean(GENERATED);
			if(generated)
			{
				ListTag dimensionList = Universe.get(level).getDimensionsFromSolarSystem(systemID);
				
				for(int i = 0; i < dimensionList.size(); i++)
				{
					dimensions.add(dimensionList.getString(i));
				}
			}
		});
		
		return dimensions;
	}
	
	public static String getRandomDimension(Level level, AddressTable addressTable)
	{
		if(addressTable == null)
			return null;
		
		List<String> dimensions = new ArrayList<String>();
		
		if(addressTable.includeGeneratedAddresses())
		{
			List<String> generatedDimensions = getRandomGeneratedDimension(level);
			dimensions.addAll(generatedDimensions);
			
			System.out.println("Added all generated Dimension");
		}
		
		List<Pair<ResourceKey<Level>, Integer>> tableDimensions = addressTable.getDimensions();
		Iterator<Pair<ResourceKey<Level>, Integer>> iterator = tableDimensions.iterator();
		
		while(iterator.hasNext())
		{
			Pair<ResourceKey<Level>, Integer> dimension = iterator.next();
			String dimensionString = dimension.getFirst().location().toString();
			int repeats = dimension.getSecond();
			
			for(int i = 0; i < repeats; i++)
			{
				dimensions.add(dimensionString);
			}
		}
		
		if(dimensions.size() > 0)
		{
			Random random = new Random();
			int randomNumber = random.nextInt(dimensions.size());
			
			return dimensions.get(randomNumber);
		}
		
		System.out.println("!!! 0 Dimensions !!!");
		return EMPTY;
	}
}
