package net.povstalec.sgjourney.client.models.block;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.povstalec.sgjourney.client.models.item.SymbolBlockItemOverrides;
import net.povstalec.sgjourney.common.blockstates.Orientation;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class SymbolBlockModelLoader extends SymbolModelLoader<SymbolBlockModelLoader.SymbolBlockModelGeometry>
{
	public static final String SYMBOL_BLOCK_LOADER = "symbol_block_loader";
	
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
		
		/*@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			if(symbolNumber >= 0)
			{
				TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
				
				var itemContext = StandaloneGeometryBakingContext.builder(context).build(modelLocation);
				var modelBuilder = CompositeModel.Baked.builder(context, particle, new SymbolBlockItemOverrides(overrides, baker, itemContext, this), context.getTransforms());
				
				// Symbol Layer
				TextureAtlasSprite symbolSprite = SymbolBlockBakedModel.getSymbolSprite(symbolNumber, symbol);
				if(symbolSprite != null)
					modelBuilder.addQuads(new RenderTypeGroup(RenderType.translucent(), Sheets.translucentCullBlockSheet()), SymbolBlockBakedModel.makeSymbolQuad(Direction.NORTH, Orientation.REGULAR, symbolSprite, symbolTint));
				
				// Block Layer
				modelBuilder.addLayer(super.bake(context, baker, spriteGetter, modelState, new SymbolBlockItemOverrides(overrides, baker, context, this), modelLocation));
				
				return modelBuilder.build();
			}
			else
				return super.bake(context, baker, spriteGetter, modelState, new SymbolBlockItemOverrides(overrides, baker, context, this), modelLocation);
		}*/
		
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
