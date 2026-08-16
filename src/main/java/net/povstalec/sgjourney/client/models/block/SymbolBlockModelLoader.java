package net.povstalec.sgjourney.client.models.block;

import com.google.gson.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.item.SymbolBlockItemOverrides;
import net.povstalec.sgjourney.common.blockstates.Orientation;

import java.util.List;
import java.util.function.Function;

public class SymbolBlockModelLoader extends SymbolModelLoader<SymbolBlockModelLoader.SymbolBlockModelGeometry>
{
	public static final ResourceLocation SYMBOL_BLOCK_LOADER = StargateJourney.sgjourneyLocation("symbol_block_loader");
	
	@Override
	public SymbolBlockModelGeometry getGeometry(JsonObject jsonObject, JsonDeserializationContext deserializationContext, List<BlockElement> elements, int symbolTint)
	{
		return new SymbolBlockModelGeometry(elements, symbolTint);
	}
	
	public static void register(ModelEvent.RegisterGeometryLoaders event)
	{
		event.register(SYMBOL_BLOCK_LOADER, new SymbolBlockModelLoader());
	}
	
	
	
	public static class SymbolBlockModelGeometry extends SymbolModelGeometry<SymbolBlockModelGeometry>
	{
		protected int symbolNumber;
		protected ResourceLocation symbol;
		
		public SymbolBlockModelGeometry(List<BlockElement> elements, int symbolTint)
		{
			super(elements, symbolTint);
			
			this.symbolNumber = -1;
		}
		
		public SymbolBlockModelGeometry(List<BlockElement> elements, int symbolTint, int symbolNumber, ResourceLocation symbol)
		{
			super(elements, symbolTint);
			
			this.symbolNumber = symbolNumber;
			this.symbol = symbol;
		}
		
		public SymbolBlockModelGeometry withSymbolNumberAndTexture(int symbolNumber, ResourceLocation symbol)
		{
			if(symbolNumber >= 0 && symbol != null)
				return new SymbolBlockModelGeometry(this.elements, this.symbolTint, symbolNumber, symbol);
			else
				return new SymbolBlockModelGeometry(this.elements, this.symbolTint);
		}
		
		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
		{
			if(symbolNumber >= 0)
			{
				TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
				
				var itemContext = StandaloneGeometryBakingContext.builder(context).build(SymbolBlockBakedModel.ID);
				var modelBuilder = CompositeModel.Baked.builder(context, particle, new SymbolBlockItemOverrides(overrides, baker, itemContext, this), context.getTransforms());
				
				// Symbol Layer
				TextureAtlasSprite symbolSprite = SymbolBlockBakedModel.getSymbolSprite(symbolNumber, symbol);
				if(symbolSprite != null)
					modelBuilder.addQuads(new RenderTypeGroup(RenderType.translucent(), Sheets.translucentCullBlockSheet()), SymbolBlockBakedModel.makeSymbolQuad(Direction.NORTH, Orientation.REGULAR, symbolSprite, symbolTint));
				
				// Block Layer
				modelBuilder.addLayer(super.bake(context, baker, spriteGetter, modelState, new SymbolBlockItemOverrides(overrides, baker, context, this)));
				
				return modelBuilder.build();
			}
			else
				return super.bake(context, baker, spriteGetter, modelState, new SymbolBlockItemOverrides(overrides, baker, context, this));
		}
		
		@Override
		protected IModelBuilder<?> getBuilder(boolean useAmbientOcclusion, boolean canUseBlockLight, boolean isGui3d,
											  ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											  RenderTypeGroup renderTypes, int symbolTint)
		{
			if(symbolNumber >= 0)
				return SGJourneyModelBuilder.ofSymbol(useAmbientOcclusion, canUseBlockLight, isGui3d, transforms, overrides, particle, renderTypes, symbolTint, symbolNumber, symbol);
			else
				return SGJourneyModelBuilder.ofSymbol(useAmbientOcclusion, canUseBlockLight, isGui3d, transforms, overrides, particle, renderTypes, symbolTint);
		}
	}
}
