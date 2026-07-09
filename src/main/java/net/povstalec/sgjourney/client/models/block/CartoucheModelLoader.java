package net.povstalec.sgjourney.client.models.block;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CartoucheModelLoader extends SymbolModelLoader<CartoucheModelLoader.CartoucheModelGeometry>
{
	public static final String CARTOUCHE_LOADER = "cartouche_loader";
	
	@Override
	public CartoucheModelGeometry getGeometry(JsonObject jsonObject, JsonDeserializationContext deserializationContext, List<BlockElement> elements, int symbolTint)
	{
		return new CartoucheModelGeometry(elements, symbolTint);
	}
	
	public static void register(ModelEvent.RegisterGeometryLoaders event)
	{
		event.register(CARTOUCHE_LOADER, new CartoucheModelLoader());
	}
	
	
	
	public static class CartoucheModelGeometry extends SymbolModelGeometry<CartoucheModelGeometry>
	{
		public CartoucheModelGeometry(List<BlockElement> elements, int symbolTint)
		{
			super(elements, symbolTint);
		}
		
		@Override
		protected IModelBuilder<?> getBuilder(boolean useAmbientOcclusion, boolean canUseBlockLight, boolean isGui3d,
											  ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											  RenderTypeGroup renderTypes, int symbolTint)
		{
			return SGJourneyModelBuilder.ofCartouche(useAmbientOcclusion, canUseBlockLight, isGui3d, transforms, overrides, particle, renderTypes, symbolTint);
		}
		
		@Override
		public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
		{
			Set<Material> textures = Sets.newHashSet();
			if(context.hasMaterial("particle"))
				textures.add(context.getMaterial("particle"));
			for(BlockElement part : elements)
			{
				for(BlockElementFace face : part.faces.values())
				{
					Material texture = context.getMaterial(face.texture);
					if(texture.texture().equals(MissingTextureAtlasSprite.getLocation()))
						missingTextureErrors.add(Pair.of(face.texture, context.getModelName()));
					textures.add(texture);
				}
			}
			
			return textures;
		}
	}
}
