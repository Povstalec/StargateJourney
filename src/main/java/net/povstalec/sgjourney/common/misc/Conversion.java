package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.sgjourney.*;

import javax.annotation.Nullable;

public class Conversion
{
	public static final ResourceLocation DIMENSION_LOCATION = new ResourceLocation("minecraft", "dimension");
	public static final ResourceKey<Registry<Level>> DIMENSION_KEY = ResourceKey.createRegistryKey(DIMENSION_LOCATION);
	
	@Nullable
	public static ResourceKey<Level> locationToDimension(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(DIMENSION_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<Level> stringToDimension(@Nullable String dimensionString)
	{
		if(dimensionString == null || dimensionString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(dimensionString);
		return locationToDimension(location);
	}
	
	@Nullable
	public static ResourceKey<PointOfOrigin> locationToPointOfOrigin(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(PointOfOrigin.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<PointOfOrigin> stringToPointOfOrigin(@Nullable String pointOfOriginString)
	{
		if(pointOfOriginString == null || pointOfOriginString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(pointOfOriginString);
		return locationToPointOfOrigin(location);
	}
	
	@Nullable
	public static ResourceKey<Symbols> locationToSymbols(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(Symbols.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<Symbols> stringToSymbols(@Nullable String symbolsString)
	{
		if(symbolsString == null || symbolsString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(symbolsString);
		return locationToSymbols(location);
	}
	
	@Nullable
	public static ResourceKey<Galaxy> locationToGalaxyKey(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(Galaxy.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<Galaxy> stringToGalaxyKey(@Nullable String galaxyString)
	{
		if(galaxyString == null || galaxyString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(galaxyString);
		return locationToGalaxyKey(location);
	}
	
	@Nullable
	public static ResourceKey<AddressRegion> locationToAddressRegionKey(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(AddressRegion.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<AddressRegion> stringToAddressRegionKey(@Nullable String addressRegionString)
	{
		if(addressRegionString == null || addressRegionString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(addressRegionString);
		return locationToAddressRegionKey(location);
	}
	
	@Nullable
	public static ResourceKey<AddressTable> locationToAddressTableKey(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(AddressTable.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<AddressTable> stringToAddressTableKey(@Nullable String addressTableString)
	{
		if(addressTableString == null || addressTableString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(addressTableString);
		return locationToAddressTableKey(location);
	}
	
	@Nullable
	public static ResourceKey<SymbolTable> locationToSymbolTableKey(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(SymbolTable.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<SymbolTable> stringToSymbolTableKey(@Nullable String symbolTableString)
	{
		if(symbolTableString == null || symbolTableString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(symbolTableString);
		return locationToSymbolTableKey(location);
	}
	
	@Nullable
	public static ResourceKey<PointOfOriginTable> locationToPointOfOriginTableKey(@Nullable ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(PointOfOriginTable.REGISTRY_KEY, location);
		
		return null;
	}
	
	@Nullable
	public static ResourceKey<PointOfOriginTable> stringToPointOfOriginTableKey(@Nullable String pointOfOriginTableString)
	{
		if(pointOfOriginTableString == null || pointOfOriginTableString.isEmpty())
			return null;
		
		ResourceLocation location = ResourceLocation.tryParse(pointOfOriginTableString);
		return locationToPointOfOriginTableKey(location);
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
	
	public static int ticksToSeconds(int ticks)
	{
		return ticks / 20;
	}
	
	public static int secondsToTicks(int seconds)
	{
		return seconds * 20;
	}
	
	public static String secondsToString(int seconds)
	{
		int s = seconds % 60;
		int m = seconds / 60;
		int h = m / 60;
		m %= 60;
		
		String result = s + "s";
		if(m > 0)
			result = m + "m " + result;
		if(h > 0)
			result = h + "h " + result;
		
		return result;
	}
	
	public static String ticksToString(int ticks)
	{
		return secondsToString(ticksToSeconds(ticks));
	}
	
	public static Vec3i vec3ToVec3i(Vec3 vec3)
	{
		return new Vec3i((int) Math.floor(vec3.x), (int) Math.floor(vec3.y), (int) Math.floor(vec3.z));
	}
	
	public static String vec3iToString(Vec3i coords)
	{
		return "[X:" + coords.getX() + ", Y: " + coords.getY() + ", Z: " + coords.getZ() + ']';
	}
}
