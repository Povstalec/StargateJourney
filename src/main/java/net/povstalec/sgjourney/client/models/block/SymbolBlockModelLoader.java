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

public class SymbolBlockModelLoader implements IGeometryLoader<SymbolBlockModelLoader.SymbolBlockModelGeometry>
{
	public static final String SYMBOL_BLOCK_LOADER = "symbol_block_loader";
	
	@Override
	public SymbolBlockModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
	{
		return new SymbolBlockModelGeometry();
	}
	
	public static void register(ModelEvent.RegisterGeometryLoaders event)
	{
		event.register(SYMBOL_BLOCK_LOADER, new SymbolBlockModelLoader());
	}
	
	
	
	public static class SymbolBlockModelGeometry implements IUnbakedGeometry<SymbolBlockModelGeometry>
	{
		public SymbolBlockModelGeometry()
		{
		
		}
		
		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			return new SymbolBlockBakedModel(context);
		}
	}
}
