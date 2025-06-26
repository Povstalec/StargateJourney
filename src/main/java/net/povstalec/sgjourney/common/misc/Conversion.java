package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.sgjourney.Galaxy;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.SolarSystem;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import javax.annotation.Nullable;

public class Conversion
{
	public static final ResourceLocation DIMENSION_LOCATION = new ResourceLocation("minecraft", "dimension");
	public static final ResourceKey<Registry<Level>> DIMENSION_KEY = ResourceKey.createRegistryKey(DIMENSION_LOCATION);
	
	@Nullable
	public static ResourceKey<Level> locationToDimension(ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(DIMENSION_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<Level> stringToDimension(String dimensionString)
	{
		ResourceLocation location = ResourceLocation.tryParse(dimensionString);
		
		if(location != null)
			return locationToDimension(location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<PointOfOrigin> locationToPointOfOrigin(ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(PointOfOrigin.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<PointOfOrigin> stringToPointOfOrigin(String pointOfOriginString)
	{
		ResourceLocation location = ResourceLocation.tryParse(pointOfOriginString);
		return locationToPointOfOrigin(location);
	}
	
	@Nullable
	public static ResourceKey<Symbols> locationToSymbols(ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(Symbols.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<Symbols> stringToSymbols(String symbolsString)
	{
		ResourceLocation location = ResourceLocation.tryParse(symbolsString);
		return locationToSymbols(location);
	}
	
	@Nullable
	public static ResourceKey<Galaxy> locationToGalaxyKey(ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(Galaxy.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<Galaxy> stringToGalaxyKey(String galaxyString)
	{
		ResourceLocation location = ResourceLocation.tryParse(galaxyString);
		return locationToGalaxyKey(location);
	}
	
	@Nullable
	public static ResourceKey<SolarSystem> locationToSolarSystemKey(ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(SolarSystem.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<SolarSystem> stringToSolarSystemKey(String solarSystemString)
	{
		ResourceLocation location = ResourceLocation.tryParse(solarSystemString);
		return locationToSolarSystemKey(location);
	}
	
	public static Vec3i intArrayToVec(int[] coordinates)
	{
		if(coordinates.length == 3)
			return new Vec3i(coordinates[0], coordinates[1], coordinates[2]);
		
		return null;
	}
	
	public static BlockPos intArrayToBlockPos(int[] coordinates)
	{
		Vec3i vec3i = intArrayToVec(coordinates);
		
		if(vec3i != null)
			return new BlockPos(vec3i);
		
		return null;
	}
	
	public static int[] vecToIntArray(Vec3i coordinates)
	{
		return new int[] {coordinates.getX(), coordinates.getY(), coordinates.getZ()};
	}
	
	public static int[] blockPosToIntArray(BlockPos coordinates)
	{
		return vecToIntArray(coordinates);
	}
}
