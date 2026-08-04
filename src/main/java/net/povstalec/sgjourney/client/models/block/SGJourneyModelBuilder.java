package net.povstalec.sgjourney.client.models.block;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import org.jetbrains.annotations.NotNull;

public class SGJourneyModelBuilder
{
	public static IModelBuilder<?> ofSymbol(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
											ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											int symbolTint)
	{
		return new SymbolBlockBuilder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, symbolTint);
	}
	
	public static IModelBuilder<?> ofSymbol(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
											ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											int symbolTint, int symbolNumber, ResourceLocation symbol)
	{
		return new SymbolBlockBuilder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, symbolTint, symbolNumber, symbol);
	}
	
	public static IModelBuilder<?> ofCartouche(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
											   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											   int symbolTint)
	{
		return new CartoucheBuilder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, symbolTint);
	}
	
	static abstract class SymbolBuilder<T extends SymbolBakedModel.Builder<?>> implements IModelBuilder<SymbolBuilder<T>>
	{
		protected final T builder;
		
		private SymbolBuilder(T builder, TextureAtlasSprite particle)
		{
			this.builder = builder;
			
			builder.particle(particle);
		}
		
		@Override
		public @NotNull BakedModel build()
		{
			return builder.build();
		}
	}
	
	
	
	static class SymbolBlockBuilder extends SymbolBuilder<SymbolBlockBakedModel.Builder>
	{
		private SymbolBlockBuilder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
								   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
								   int symbolTint)
		{
			super(new SymbolBlockBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint), particle);
		}
		
		private SymbolBlockBuilder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
								   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
								   int symbolTint, int symbolNumber, ResourceLocation symbol)
		{
			super(new SymbolBlockBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint, symbolNumber, symbol), particle);
		}
		
		@Override
		public SymbolBuilder<SymbolBlockBakedModel.Builder> addFaceQuad(Direction facing, BakedQuad quad)
		{
			builder.addCulledFace(facing, quad);
			return this;
		}
		
		@Override
		public SymbolBuilder<SymbolBlockBakedModel.Builder> addGeneralQuad(BakedQuad quad)
		{
			builder.addUnculledFace(quad);
			return this;
		}
	}
	
	
	
	static class CartoucheBuilder extends SymbolBuilder<CartoucheBakedModel.Builder>
	{
		private CartoucheBuilder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d,
								   ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
								   int symbolTint)
		{
			super(new CartoucheBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint), particle);
		}
		
		@Override
		public SymbolBuilder<CartoucheBakedModel.Builder> addFaceQuad(Direction facing, BakedQuad quad)
		{
			builder.addCulledFace(facing, quad);
			return this;
		}
		
		@Override
		public SymbolBuilder<CartoucheBakedModel.Builder> addGeneralQuad(BakedQuad quad)
		{
			builder.addUnculledFace(quad);
			return this;
		}
	}
}
