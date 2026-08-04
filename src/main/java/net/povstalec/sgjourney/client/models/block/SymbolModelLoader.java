package net.povstalec.sgjourney.client.models.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.ISimpleModelGeometry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Basically a copy of {@link ElementsModel.Loader}
 */
public abstract class SymbolModelLoader<T extends ISimpleModelGeometry<T>> implements IModelLoader<T>
{
	public abstract T getGeometry(JsonObject jsonObject, JsonDeserializationContext deserializationContext, List<BlockElement> elements, int symbolTint);
	
	@Override
	public T read(JsonDeserializationContext deserializationContext, JsonObject jsonObject) throws JsonParseException
	{
		if(!jsonObject.has("elements"))
			throw new JsonParseException("An element model must have an \"elements\" member.");
		
		List<BlockElement> elements = new ArrayList<>();
		for(JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements"))
		{
			elements.add(deserializationContext.deserialize(element, BlockElement.class));
		}
		
		int symbolTint;
		if(jsonObject.has("symbol_tint"))
			symbolTint = GsonHelper.getAsInt(jsonObject, "symbol_tint");
		else
			symbolTint = 0xFFFFFFFF;
		
		
		return getGeometry(jsonObject, deserializationContext, elements, symbolTint);
	}
	
	
	/**
	 * Basically a copy of {@link ElementsModel}
	 */
	public static abstract class SymbolModelGeometry<T extends SymbolModelGeometry<T>> implements ISimpleModelGeometry<T>
	{
		protected final List<BlockElement> elements;
		protected final int symbolTint;
		
		public SymbolModelGeometry(List<BlockElement> elements, int symbolTint)
		{
			this.elements = elements;
			this.symbolTint = symbolTint;
		}
		
		protected abstract IModelBuilder<?> getBuilder(boolean useAmbientOcclusion, boolean canUseBlockLight, boolean isGui3d,
													   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
													   int symbolTint);
		
		@Override
		public BakedModel bake(IModelConfiguration context, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			TextureAtlasSprite particle = spriteGetter.apply(context.resolveTexture("particle"));
			
			IModelBuilder<?> builder = getBuilder(false/*Disabled ambient occlusion because the game insisted on making quads larger than 10x10 dark*/, context.isSideLit(), context.isShadedInGui(),
					context.getCameraTransforms(), overrides, particle, symbolTint);
			
			addQuads(context, builder, baker, spriteGetter, modelState, modelLocation);
			
			return builder.build();
		}
		
		@Override
		public void addQuads(IModelConfiguration context, IModelBuilder<?> modelBuilder, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation)
		{
			for(BlockElement blockpart : elements)
			{
				for(Direction direction : blockpart.faces.keySet())
				{
					BlockElementFace blockpartface = blockpart.faces.get(direction);
					TextureAtlasSprite textureatlassprite1 = spriteGetter.apply(context.resolveTexture(blockpartface.texture));
					if(blockpartface.cullForDirection == null)
						modelBuilder.addGeneralQuad(BlockModel.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, modelState, modelLocation));
					else
					{
						modelBuilder.addFaceQuad(
							modelState.getRotation().rotateTransform(blockpartface.cullForDirection),
							BlockModel.makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, modelState, modelLocation));
					}
				}
			}
		}
	}
}
