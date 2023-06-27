package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class Conversion
{
	public static ResourceKey<Level> stringToDimension(String dimensionString)
	{
		String[] split = dimensionString.split(":");
		return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation(split[0], split[1]));
	}
	
	public static BlockPos intArrayToBlockPos(int[] coordinates)
	{
		return new BlockPos(coordinates[0], coordinates[1], coordinates[2]);
	}
}
