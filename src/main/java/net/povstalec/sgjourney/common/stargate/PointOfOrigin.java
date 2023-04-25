package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class PointOfOrigin
{
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	
	public static final ResourceLocation POINT_OF_ORIGIN_LOCATION = new ResourceLocation(StargateJourney.MODID, "point_of_origin");
	public static final ResourceKey<Registry<PointOfOrigin>> REGISTRY_KEY = ResourceKey.createRegistryKey(POINT_OF_ORIGIN_LOCATION);
	public static final Codec<ResourceKey<PointOfOrigin>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<PointOfOrigin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(PointOfOrigin::getName),
			ResourceLocation.CODEC.fieldOf("texture").forGetter(PointOfOrigin::getTexture),
			Codec.BOOL.fieldOf("generates_randomly").forGetter(PointOfOrigin::generatesRandomly)
			).apply(instance, PointOfOrigin::new));
	
	private final String name;
	private final ResourceLocation texture;
	private final boolean generatesRandomly;
	
	public PointOfOrigin(String name, ResourceLocation texture, boolean generatesRandomly)
	{
		this.name = name;
		this.texture = texture;
		this.generatesRandomly = generatesRandomly;
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
	
	public boolean generatesRandomly()
	{
		return generatesRandomly;
	}
	
	public static ResourceKey<PointOfOrigin> getRandomPointOfOrigin(MinecraftServer server, long seed)
	{
		Random random = new Random(seed);
		RegistryAccess registries = server.registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		Set<Entry<ResourceKey<PointOfOrigin>, PointOfOrigin>> set = registry.entrySet();
		
		List<ResourceKey<PointOfOrigin>> list = new ArrayList<ResourceKey<PointOfOrigin>>();
		
		set.forEach((pointOfOrigin) -> 
		{
			PointOfOrigin PoO = pointOfOrigin.getValue();
			if(PoO.generatesRandomly)
				list.add(pointOfOrigin.getKey());
		});
		
		return list.get(random.nextInt(0, list.size()));
		
	}
	
	public static PointOfOrigin getPointOfOrigin(Level level, String name)
	{
		String[] split = name.split(":");
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		return registry.get(new ResourceLocation(split[0], split[1]));
	}
	
}
