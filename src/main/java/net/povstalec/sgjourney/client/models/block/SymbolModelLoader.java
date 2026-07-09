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
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.ElementsModel;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Basically a copy of {@link ElementsModel.Loader}
 */
public abstract class SymbolModelLoader<T extends IUnbakedGeometry<T>> implements IGeometryLoader<T>
{
	public abstract T getGeometry(JsonObject jsonObject, JsonDeserializationContext deserializationContext, List<BlockElement> elements, int symbolTint);
	
	@Override
	public T read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
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
	public static abstract class SymbolModelGeometry<T extends SymbolModelGeometry<T>> extends SimpleUnbakedGeometry<T>
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
													   RenderTypeGroup renderTypes, int symbolTint);
		
		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
			
			ResourceLocation renderTypeHint = context.getRenderTypeHint();
			RenderTypeGroup renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
			IModelBuilder<?> builder = getBuilder(false/*Disabled ambient occlusion because the game insisted on making quads larger than 10x10 dark*/, context.useBlockLight(), context.isGui3d(),
					context.getTransforms(), overrides, particle, renderTypes, symbolTint);
			
			addQuads(context, builder, baker, spriteGetter, modelState, modelLocation);
			
			return builder.build();
		}
		
		@Override
		protected void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation)
		{
			// If there is a root transform, undo the ModelState transform, apply it, then re-apply the ModelState transform.
			// This is necessary because of things like UV locking, which should only respond to the ModelState, and as such
			// that is the only transform that should be applied during face bake.
			var postTransform = context.getRootTransform().isIdentity() ? QuadTransformers.empty() :
					QuadTransformers.applying(modelState.getRotation().compose(context.getRootTransform()).compose(modelState.getRotation().inverse()));
			
			for(BlockElement element : elements)
			{
				for(Direction direction : element.faces.keySet())
				{
					BlockElementFace face = element.faces.get(direction);
					TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.texture));
					BakedQuad quad = BlockModel.bakeFace(element, face, sprite, direction, modelState, modelLocation);
					postTransform.processInPlace(quad);
					
					if(face.cullForDirection == null)
						modelBuilder.addUnculledFace(quad);
					else
						modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection), quad);
				}
			}
		}
	}
}
