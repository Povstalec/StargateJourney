package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;

import javax.annotation.Nullable;

public record PointOfOrigin(ResourceKey<ClientPointOfOrigin> clientPointOfOrigin, List<ResourceKey<Galaxy>> generatedGalaxies)
{
	public static final ResourceLocation UNIVERSAL_LOCATION = new ResourceLocation(StargateJourney.MODID, "universal");
	
	public static final ResourceLocation POINT_OF_ORIGIN_LOCATION = new ResourceLocation(StargateJourney.MODID, "point_of_origin");
	public static final ResourceKey<Registry<PointOfOrigin>> REGISTRY_KEY = ResourceKey.createRegistryKey(POINT_OF_ORIGIN_LOCATION);
	public static final Codec<ResourceKey<PointOfOrigin>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<PointOfOrigin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ClientPointOfOrigin.RESOURCE_KEY_CODEC.fieldOf("client_point_of_origin").forGetter(pointOfOrigin -> pointOfOrigin.clientPointOfOrigin),
			Galaxy.RESOURCE_KEY_CODEC.listOf().optionalFieldOf("generated_galaxies", List.of()).forGetter(pointOfOrigin -> pointOfOrigin.generatedGalaxies)
	).apply(instance, PointOfOrigin::new));
	
	public static ResourceKey<PointOfOrigin> defaultPointOfOrigin()
	{
		return Conversion.locationToPointOfOrigin(UNIVERSAL_LOCATION);
	}
	
	public static boolean isValid(MinecraftServer server, ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		if(pointOfOrigin == null)
			return false;
		
		RegistryAccess registries = server.registryAccess();
		Registry<PointOfOrigin> pointOfOriginRegistry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		return pointOfOriginRegistry.containsKey(pointOfOrigin);
	}
	
	public static ResourceKey<PointOfOrigin> fromDimension(MinecraftServer server, ResourceKey<Level> dimension)
	{
		return Universe.get(server).getPointOfOrigin(dimension);
	}
	
	public static ResourceKey<PointOfOrigin> randomPointOfOrigin(MinecraftServer server, ResourceKey<Level> dimension)
	{
		return Universe.get(server).getRandomPointOfOriginFromDimension(dimension, new Random().nextLong());
	}
	
	public static MutableComponent makeComponent(@Nullable ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		return Component.literal(pointOfOrigin != null ? pointOfOrigin.location().toString() : "-");
	}
}
