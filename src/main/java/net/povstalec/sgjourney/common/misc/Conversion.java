package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.stargate.Galaxy;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.SolarSystem;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class Conversion
{
	public static ResourceKey<Level> stringToDimension(String dimensionString)
	{
		String[] split = dimensionString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(ResourceKey.createRegistryKey(StargateJourney.location("minecraft", "dimension")), StargateJourney.location(split[0], split[1]));
		
		return null;
	}
	
	public static ResourceKey<PointOfOrigin> stringToPointOfOrigin(String pointOfOriginString)
	{
		String[] split = pointOfOriginString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(PointOfOrigin.REGISTRY_KEY, StargateJourney.location(split[0], split[1]));
		
		return null;
	}
	
	public static ResourceKey<Symbols> stringToSymbols(String symbolsString)
	{
		String[] split = symbolsString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(Symbols.REGISTRY_KEY, StargateJourney.location(split[0], split[1]));
		
		return null;
	}
	
	public static ResourceKey<Galaxy> stringToGalaxyKey(String galaxyString)
	{
		String[] split = galaxyString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(Galaxy.REGISTRY_KEY, StargateJourney.location(split[0], split[1]));
		
		return null;
	}
	
	public static ResourceKey<SolarSystem> stringToSolarSystemKey(String solarSystemString)
	{
		String[] split = solarSystemString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(SolarSystem.REGISTRY_KEY, StargateJourney.location(split[0], split[1]));
		
		return null;
	}
	
	public static BlockPos intArrayToBlockPos(int[] coordinates)
	{
		if(coordinates.length >= 3)
			return new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
		
		return null;
	}
}
