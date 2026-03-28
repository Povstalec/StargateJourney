package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;

public record PointOfOrigin(String name, ResourceLocation texture, Optional<List<ResourceKey<Galaxy>>> generatedGalaxies)
{
	public static final ResourceLocation UNIVERSAL_LOCATION = new ResourceLocation(StargateJourney.MODID, "universal");
	
	public static final ResourceLocation POINT_OF_ORIGIN_LOCATION = new ResourceLocation(StargateJourney.MODID, "point_of_origin");
	public static final ResourceKey<Registry<PointOfOrigin>> REGISTRY_KEY = ResourceKey.createRegistryKey(POINT_OF_ORIGIN_LOCATION);
	public static final Codec<ResourceKey<PointOfOrigin>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<PointOfOrigin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(PointOfOrigin::name),
			ResourceLocation.CODEC.fieldOf("texture").forGetter(PointOfOrigin::texture),
			Galaxy.RESOURCE_KEY_CODEC.listOf().optionalFieldOf("generated_galaxies").forGetter(PointOfOrigin::generatedGalaxies)
	).apply(instance, PointOfOrigin::new));
	
	
	public static ResourceKey<PointOfOrigin> defaultPointOfOrigin()
	{
		return Conversion.locationToPointOfOrigin(UNIVERSAL_LOCATION);
	}
	
	public static boolean validLocation(MinecraftServer server, ResourceLocation pointOfOrigin)
	{
		if(pointOfOrigin == null || StargateJourney.EMPTY_LOCATION.equals(pointOfOrigin))
			return false;
		
		RegistryAccess registries = server.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		return pointOfOriginRegistry.containsKey(pointOfOrigin);
	}
	
	public static ResourceLocation fromDimension(MinecraftServer server, ResourceKey<Level> dimension)
	{
		return Universe.get(server).getPointOfOrigin(dimension).location();
	}
	
	public static ResourceLocation randomPointOfOrigin(MinecraftServer server, ResourceKey<Level> dimension)
	{
		Random random = new Random();
		return Universe.get(server).getRandomPointOfOriginFromDimension(dimension, random.nextLong()).location();
	}
}
