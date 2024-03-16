package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class Conversion
{
	public static ResourceKey<Level> stringToDimension(String dimensionString)
	{
		String[] split = dimensionString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation(split[0], split[1]));
		
		return null;
	}
	
	public static ResourceKey<PointOfOrigin> stringToPointOfOrigin(String pointOfOriginString)
	{
		String[] split = pointOfOriginString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(PointOfOrigin.REGISTRY_KEY, new ResourceLocation(split[0], split[1]));
		
		return null;
	}
	
	public static ResourceKey<Symbols> stringToSymbols(String symbolsString)
	{
		String[] split = symbolsString.split(":");
		
		if(split.length > 1)
			return ResourceKey.create(Symbols.REGISTRY_KEY, new ResourceLocation(split[0], split[1]));
		
		return null;
	}
	
	public static BlockPos intArrayToBlockPos(int[] coordinates)
	{
		if(coordinates.length >= 3)
			return new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
		
		return null;
	}
}
