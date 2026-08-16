package net.povstalec.sgjourney.client.models.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class CableModelLoader implements IGeometryLoader<CableModelLoader.CableModelGeometry>
{
	public static final String CABLE_LOADER = "cable_loader";
	
	public static final String TEXTURE = "texture";
	public static final String PARTICLE_TEXTURE = "particle_texture";
	public static final String THICKNESS = "thickness";
	
	@Override
	public CableModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
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
	
	public static void register(ModelEvent.RegisterGeometryLoaders event)
	{
		event.register(CABLE_LOADER, new CableModelLoader());
	}
	
	
	
	public static class CableModelGeometry implements IUnbakedGeometry<CableModelGeometry>
	{
		private ResourceLocation texture;
		private ResourceLocation particleTexture;
		private double thickness;
		
		public CableModelGeometry(ResourceLocation texture, ResourceLocation particleTexture, double thickness)
		{
			this.texture = texture;
			this.particleTexture = particleTexture;
			this.thickness = thickness;
		}
		
		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			return new CableBakedModel(context, this.texture, this.particleTexture, this.thickness);
		}
	}
}
