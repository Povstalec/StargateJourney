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
import net.povstalec.sgjourney.StargateJourney;

import java.util.function.Function;

public class CableModelLoader implements IGeometryLoader<CableModelLoader.CableModelGeometry>
{
	public static final String CABLE_LOADER = "cable_loader";
	
	@Override
	public CableModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
	{
		double thickness;
		if(jsonObject.has("thickness"))
			thickness = jsonObject.get("thickness").getAsDouble();
		else
			thickness = 0.4;
		
		return new CableModelGeometry(thickness);
	}
	
	public static void register(ModelEvent.RegisterGeometryLoaders event)
	{
		event.register(CABLE_LOADER, new CableModelLoader());
	}
	
	
	
	public static class CableModelGeometry implements IUnbakedGeometry<CableModelGeometry>
	{
		double thickness;
		
		public CableModelGeometry(double thickness)
		{
			this.thickness = thickness;
		}
		
		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			return new CableBakedModel(context, this.thickness);
		}
	}
}
