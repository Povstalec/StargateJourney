package net.povstalec.sgjourney.client.models.block;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IModelBuilder;
import org.jetbrains.annotations.NotNull;

public class SGJourneyModelBuilder
{
	public static IModelBuilder<?> ofSymbol(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
											ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											RenderTypeGroup renderTypes, int symbolTint)
	{
		return new SymbolBlockBuilder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, renderTypes, symbolTint);
	}
	
	public static IModelBuilder<?> ofSymbol(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
											ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											RenderTypeGroup renderTypes, int symbolTint, int symbolNumber, ResourceLocation symbol)
	{
		return new SymbolBlockBuilder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, renderTypes, symbolTint, symbolNumber, symbol);
	}
	
	public static IModelBuilder<?> ofCartouche(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
											   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											   RenderTypeGroup renderTypes, int symbolTint)
	{
		return new CartoucheBuilder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, renderTypes, symbolTint);
	}
	
	static abstract class SymbolBuilder<T extends SymbolBakedModel.Builder<?>> implements IModelBuilder<SymbolBuilder<T>>
	{
		private final T builder;
		private final RenderTypeGroup renderTypes;
		
		private SymbolBuilder(T builder, TextureAtlasSprite particle, RenderTypeGroup renderTypes)
		{
			this.builder = builder;
			this.renderTypes = renderTypes;
			
			builder.particle(particle);
		}
		
		@Override
		public @NotNull SGJourneyModelBuilder.SymbolBuilder<T> addCulledFace(Direction facing, BakedQuad quad)
		{
			builder.addCulledFace(facing, quad);
			return this;
		}
		
		@Override
		public @NotNull SGJourneyModelBuilder.SymbolBuilder<T> addUnculledFace(BakedQuad quad)
		{
			builder.addUnculledFace(quad);
			return this;
		}
		
		@Override
		public @NotNull BakedModel build()
		{
			return builder.build(renderTypes);
		}
	}
	
	
	
	static class SymbolBlockBuilder extends SymbolBuilder<SymbolBlockBakedModel.Builder>
	{
		private SymbolBlockBuilder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
								   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
								   RenderTypeGroup renderTypes, int symbolTint)
		{
			super(new SymbolBlockBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint), particle, renderTypes);
		}
		
		private SymbolBlockBuilder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
								   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
								   RenderTypeGroup renderTypes, int symbolTint, int symbolNumber, ResourceLocation symbol)
		{
			super(new SymbolBlockBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint, symbolNumber, symbol), particle, renderTypes);
		}
	}
	
	
	
	static class CartoucheBuilder extends SymbolBuilder<CartoucheBakedModel.Builder>
	{
		private CartoucheBuilder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
								   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
								   RenderTypeGroup renderTypes, int symbolTint)
		{
			super(new CartoucheBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint), particle, renderTypes);
		}
	}
}
