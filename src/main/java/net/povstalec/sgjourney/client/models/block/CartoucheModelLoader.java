package net.povstalec.sgjourney.client.models.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.povstalec.sgjourney.StargateJourney;

import java.util.List;

public class CartoucheModelLoader extends SymbolModelLoader<CartoucheModelLoader.CartoucheModelGeometry>
{
	public static final ResourceLocation CARTOUCHE_LOADER = StargateJourney.sgjourneyLocation("cartouche_loader");
	
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
	}
}
