package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.povstalec.sgjourney.StargateJourney;

import javax.annotation.Nullable;
import java.util.*;

public class PointOfOriginTable
{
	public static final ResourceLocation SYMBOL_TABLES_LOCATION = StargateJourney.sgjourneyLocation("point_of_origin_table");
	public static final ResourceKey<Registry<PointOfOriginTable>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOL_TABLES_LOCATION);
	public static final Codec<ResourceKey<PointOfOriginTable>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<PointOfOriginTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RESOURCE_KEY_CODEC.optionalFieldOf("copy_from").forGetter(table -> Optional.ofNullable(table.copyFrom)),
		WeightedPointOfOrigin.CODEC.listOf().fieldOf("points_of_origin").forGetter(PointOfOriginTable::pointsOfOrigin)
	).apply(instance, PointOfOriginTable::new));
	
	private static final Map<ResourceKey<PointOfOriginTable>, PointOfOriginTable> POINT_OF_ORIGIN_TABLES = new HashMap<>();
	
	@Nullable
	private final ResourceKey<PointOfOriginTable> copyFrom;
	private final List<WeightedPointOfOrigin> pointsOfOrigin;
	
	public PointOfOriginTable(Optional<ResourceKey<PointOfOriginTable>> copyFrom, List<WeightedPointOfOrigin> pointsOfOrigin)
	{
		this(copyFrom.orElse(null), pointsOfOrigin);
	}
	
	public PointOfOriginTable(@Nullable ResourceKey<PointOfOriginTable> copyFrom, List<WeightedPointOfOrigin> pointsOfOrigin)
	{
		this.copyFrom = copyFrom;
		this.pointsOfOrigin = pointsOfOrigin;
	}
	
	public List<WeightedPointOfOrigin> pointsOfOrigin()
	{
		return this.pointsOfOrigin;
	}
	
	@Nullable
	public static PointOfOriginTable getPointOfOriginTable(@Nullable ResourceKey<PointOfOriginTable> pointOfOriginTable)
	{
		if(pointOfOriginTable == null)
			return null;
		
		return POINT_OF_ORIGIN_TABLES.get(pointOfOriginTable);
	}
	
	@Nullable
	public static ResourceKey<PointOfOrigin> randomPointOfOrigin(RandomSource randomSource, PointOfOriginTable pointOfOriginTable)
	{
		if(pointOfOriginTable == null)
			return null;
		
		WeightedPointOfOrigin output = null;
		int totalWeight = 0;
		
		for(WeightedPointOfOrigin weightedPointOfOrigin : pointOfOriginTable.pointsOfOrigin())
		{
			totalWeight += weightedPointOfOrigin.weight();
			if(randomSource.nextFloat() <= (float) weightedPointOfOrigin.weight() / totalWeight)
				output = weightedPointOfOrigin;
		}
		
		if(output == null)
			return null;
		
		return output.pointOfOrigin();
	}
	
	public static void registerPointOfOriginTables(MinecraftServer server)
	{
		POINT_OF_ORIGIN_TABLES.clear();
		
		final RegistryAccess registries = server.registryAccess();
		final Registry<PointOfOriginTable> registry = registries.registryOrThrow(PointOfOriginTable.REGISTRY_KEY);
		
		for(Map.Entry<ResourceKey<PointOfOriginTable>, PointOfOriginTable> entry : registry.entrySet())
		{
			POINT_OF_ORIGIN_TABLES.put(entry.getKey(), new PointOfOriginTable(entry.getValue().copyFrom, new ArrayList<>(entry.getValue().pointsOfOrigin)));
		}
		
		addPointsOfOriginToTables(server);
		
		Set<ResourceKey<PointOfOriginTable>> discovered = new HashSet<>();
		for(Map.Entry<ResourceKey<PointOfOriginTable>, PointOfOriginTable> entry : POINT_OF_ORIGIN_TABLES.entrySet())
		{
			recursiveAddPointsOfOrigin(discovered, entry.getKey(), entry.getValue());
			discovered.clear();
		}
	}
	
	private static void addPointsOfOriginToTables(MinecraftServer server)
	{
		RegistryAccess registries = server.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		Set<Map.Entry<ResourceKey<PointOfOrigin>, PointOfOrigin>> pointOfOriginSet = pointOfOriginRegistry.entrySet();
		
		pointOfOriginSet.forEach((pointOfOriginEntry) ->
		{
			PointOfOrigin pointOfOrigin = pointOfOriginEntry.getValue();
			ResourceKey<PointOfOrigin> pointOfOriginKey = pointOfOriginEntry.getKey();
			
			for(PointOfOrigin.WeightedTable weightedTable : pointOfOrigin.pointOfOriginTables())
			{
				if(POINT_OF_ORIGIN_TABLES.containsKey(weightedTable.table()))
					POINT_OF_ORIGIN_TABLES.get(weightedTable.table()).pointsOfOrigin.add(new WeightedPointOfOrigin(pointOfOriginKey, weightedTable.weight()));
			}
		});
	}
	
	private static void recursiveAddPointsOfOrigin(Set< ResourceKey<PointOfOriginTable>> discovered, ResourceKey<PointOfOriginTable> key, PointOfOriginTable table)
	{
		if(!discovered.add(key))
			throw new RuntimeException("Recursive reference detected in PointOfOriginTable " + key);
		
		if(table.copyFrom != null)
		{
			PointOfOriginTable copyFrom = POINT_OF_ORIGIN_TABLES.get(table.copyFrom);
			if(copyFrom != null)
			{
				recursiveAddPointsOfOrigin(discovered, table.copyFrom, copyFrom);
				table.pointsOfOrigin.addAll(copyFrom.pointsOfOrigin);
			}
		}
	}
	
	
	
	public record WeightedPointOfOrigin(ResourceKey<PointOfOrigin> pointOfOrigin, int weight)
	{
		public static final Codec<WeightedPointOfOrigin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PointOfOrigin.RESOURCE_KEY_CODEC.fieldOf("point_of_origin").forGetter(weightedPointOfOrigin -> weightedPointOfOrigin.pointOfOrigin),
			Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("weight", 1).forGetter(weightedPointOfOrigin -> weightedPointOfOrigin.weight)
		).apply(instance, WeightedPointOfOrigin::new));
	}
}
