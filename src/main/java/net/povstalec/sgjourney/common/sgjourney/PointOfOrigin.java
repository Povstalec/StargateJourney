package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;

public class PointOfOrigin
{
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	
	public static final ResourceLocation UNIVERSAL_LOCATION = new ResourceLocation(StargateJourney.MODID, "universal");
	
	public static final ResourceLocation POINT_OF_ORIGIN_LOCATION = new ResourceLocation(StargateJourney.MODID, "point_of_origin");
	public static final ResourceKey<Registry<PointOfOrigin>> REGISTRY_KEY = ResourceKey.createRegistryKey(POINT_OF_ORIGIN_LOCATION);
	public static final Codec<ResourceKey<PointOfOrigin>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<PointOfOrigin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(PointOfOrigin::getName),
			ResourceLocation.CODEC.fieldOf("texture").forGetter(PointOfOrigin::getTexture),
			Galaxy.RESOURCE_KEY_CODEC.listOf().optionalFieldOf("generated_galaxies").forGetter(PointOfOrigin::generatedGalaxies)
			).apply(instance, PointOfOrigin::new));
	
	private final String name;
	private final ResourceLocation texture;
	private final Optional<List<ResourceKey<Galaxy>>> generatedGalaxies;
	
	public PointOfOrigin(String name, ResourceLocation texture, Optional<List<ResourceKey<Galaxy>>> generatedGalaxies)
	{
		this.name = name;
		this.texture = texture;
		this.generatedGalaxies = generatedGalaxies;
	}
	
	public String getName()
	{
		return name;
	}
	
	private ResourceLocation getTexture()
	{
		return texture;
	}
	
	public ResourceLocation texture()
	{
		ResourceLocation path = getTexture();
		ResourceLocation texture = new ResourceLocation(path.getNamespace(), "textures/symbols/" + path.getPath());
		
		if(Minecraft.getInstance().getResourceManager().getResource(texture).isPresent())
			return texture;
		return ERROR_LOCATION;
	}
	
	public Optional<List<ResourceKey<Galaxy>>> generatedGalaxies()
	{
		return generatedGalaxies;
	}
	
	public static PointOfOrigin getPointOfOrigin(Level level, String name)
	{
		String[] split = name.split(":");
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		return registry.get(new ResourceLocation(split[0], split[1]));
	}
	
	public static ResourceKey<PointOfOrigin> defaultPointOfOrigin()
	{
		return Conversion.stringToPointOfOrigin(StargateJourney.MODID + ":universal");
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
