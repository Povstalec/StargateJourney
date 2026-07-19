package net.povstalec.sgjourney.client.resourcepack.symbols;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public record ClientPointOfOrigin(String name, ResourceLocation texture)
{
	public static final ResourceLocation UNIVERSAL_LOCATION = new ResourceLocation(StargateJourney.MODID, "universal");
	
	public static final ResourceLocation POINT_OF_ORIGIN_LOCATION = new ResourceLocation(StargateJourney.MODID, "point_of_origin");
	public static final ResourceKey<Registry<ClientPointOfOrigin>> REGISTRY_KEY = ResourceKey.createRegistryKey(POINT_OF_ORIGIN_LOCATION);
	public static final Codec<ResourceKey<ClientPointOfOrigin>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<ClientPointOfOrigin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(ClientPointOfOrigin::name),
			ResourceLocation.CODEC.fieldOf("texture").forGetter(ClientPointOfOrigin::texture)
	).apply(instance, ClientPointOfOrigin::new));
	
	private static final Map<ResourceKey<PointOfOrigin>, ClientPointOfOrigin> POINTS_OF_ORIGIN = new HashMap<>();
	
	public static TextureAtlasSprite getSprite(@Nullable ClientPointOfOrigin pointOfOrigin)
	{
		if(pointOfOrigin == null)
			return ClientUtil.getTexture(MissingTextureAtlasSprite.getLocation());
		
		return ClientUtil.getTexture(pointOfOrigin.texture());
	}
	
	
	
	public static void addPointOfOrigin(ResourceKey<PointOfOrigin> key, ClientPointOfOrigin symbolSet)
	{
		POINTS_OF_ORIGIN.put(key, symbolSet);
	}
	
	@Nullable
	public static ClientPointOfOrigin getPointOfOrigin(ResourceKey<PointOfOrigin> key)
	{
		return POINTS_OF_ORIGIN.get(key);
	}
	
	public static String translationName(@Nullable ClientPointOfOrigin pointOfOrigin, String alternative)
	{
		if(pointOfOrigin != null)
			return pointOfOrigin.name();
		
		return alternative;
	}
	
	public static void clearPointsOfOrigin()
	{
		POINTS_OF_ORIGIN.clear();
	}
}
