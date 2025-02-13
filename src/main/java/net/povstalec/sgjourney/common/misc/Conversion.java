package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.stargate.Galaxy;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.SolarSystem;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class Conversion
{
	public static ResourceKey<Level> locationToDimension(ResourceLocation location)
	{
		return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), location);
	}
	
	public static ResourceKey<Level> stringToDimension(String dimensionString)
	{
		ResourceLocation location = ResourceLocation.tryParse(dimensionString);
		
		if(location != null)
			return locationToDimension(location);
		
		return null;
	}
	
	public static ResourceKey<PointOfOrigin> stringToPointOfOrigin(String pointOfOriginString)
	{
		ResourceLocation location = ResourceLocation.tryParse(pointOfOriginString);
		
		if(location != null)
			return ResourceKey.create(PointOfOrigin.REGISTRY_KEY, location);
		
		return null;
	}
	
	public static ResourceKey<Symbols> stringToSymbols(String symbolsString)
	{
		ResourceLocation location = ResourceLocation.tryParse(symbolsString);
		
		if(location != null)
			return ResourceKey.create(Symbols.REGISTRY_KEY, location);
		
		return null;
	}
	
	public static ResourceKey<Galaxy> stringToGalaxyKey(String galaxyString)
	{
		ResourceLocation location = ResourceLocation.tryParse(galaxyString);
		
		if(location != null)
			return ResourceKey.create(Galaxy.REGISTRY_KEY, location);
		
		return null;
	}
	
	public static ResourceKey<SolarSystem> stringToSolarSystemKey(String solarSystemString)
	{
		ResourceLocation location = ResourceLocation.tryParse(solarSystemString);
		
		if(location != null)
			return ResourceKey.create(SolarSystem.REGISTRY_KEY, location);
		
		return null;
	}
	
	public static BlockPos intArrayToBlockPos(int[] coordinates)
	{
		if(coordinates.length >= 3)
			return new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
		
		return null;
	}
}
