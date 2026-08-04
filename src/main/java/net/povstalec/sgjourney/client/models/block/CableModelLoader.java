package net.povstalec.sgjourney.client.models.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.povstalec.sgjourney.StargateJourney;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CableModelLoader implements IModelLoader<CableModelLoader.CableModelGeometry>
{
	public static final String CABLE_LOADER = "cable_loader";
	
	public static final String TEXTURE = "texture";
	public static final String PARTICLE_TEXTURE = "particle_texture";
	public static final String THICKNESS = "thickness";
	
	@Override
	public CableModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) throws JsonParseException
	{
		ResourceLocation texture;
		if(jsonObject.has(TEXTURE))
		{
			texture = ResourceLocation.tryParse(jsonObject.get(TEXTURE).getAsString());
			if(texture == null)
				throw new JsonParseException("Texture is not a valid Resource Location");
		}
		else
			throw new JsonParseException("Missing texture field in cable model");
		
		ResourceLocation particleTexture;
		if(jsonObject.has(PARTICLE_TEXTURE))
		{
			particleTexture = ResourceLocation.tryParse(jsonObject.get(PARTICLE_TEXTURE).getAsString());
			if(particleTexture == null)
				throw new JsonParseException("Particle Texture is not a valid Resource Location");
		}
		else
			particleTexture = texture;
		
		double thickness;
		if(jsonObject.has(THICKNESS))
			thickness = jsonObject.get(THICKNESS).getAsDouble();
		else
			throw new JsonParseException("Missing thickness field in cable model");
		
		return new CableModelGeometry(texture, particleTexture, thickness);
	}
	
	public static void register(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(StargateJourney.sgjourneyLocation(CABLE_LOADER), new CableModelLoader());
	}
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
	
	}
	
	
	public static class CableModelGeometry implements IModelGeometry<CableModelGeometry>
	{
		private ResourceLocation texture;
		private Material material;
		private ResourceLocation particleTexture;
		private double thickness;
		
		public CableModelGeometry(ResourceLocation texture, ResourceLocation particleTexture, double thickness)
		{
			this.texture = texture;
			this.material = ForgeHooksClient.getBlockMaterial(texture);
			this.particleTexture = particleTexture;
			this.thickness = thickness;
		}
		
		@Override
		public BakedModel bake(IModelConfiguration context, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			return new CableBakedModel(modelState, overrides, context.getCameraTransforms(), this.texture, this.particleTexture, this.thickness);
		}
		
		@Override
		public Collection<Material> getTextures(IModelConfiguration context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
		{
			return List.of(material);
		}
	}
}
