package net.povstalec.sgjourney.client.models.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.data.ModelData;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.ModelProperties;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.blocks.CartoucheBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class CartoucheBakedModel extends SymbolBakedModel
{
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	
	public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("symbol_block_model");
	
	protected static final float MAX_WIDTH = 10F;
	protected static final float MAX_HEIGHT = 26F;
	
	public CartoucheBakedModel(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight,
							   TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, RenderTypeGroup renderTypes, int symbolTint)
	{
		super(unculledFaces, culledFaces, hasAmbientOcclusion, isGui3d, usesBlockLight, particleIcon, transforms, overrides, renderTypes, symbolTint);
	}
	
	protected BakedQuad makeSymbolQuad(Direction direction, Orientation orientation, TextureAtlasSprite symbolSprite, float yPos, float symbolSize, boolean divideSymbol, DoubleBlockHalf half)
	{
		float minX = 8F - symbolSize / 2F;
		float maxX = 8F + symbolSize / 2F;
		float minY = divideSymbol && half == DoubleBlockHalf.UPPER ? yPos : yPos - symbolSize / 2F;
		float maxY = divideSymbol && half == DoubleBlockHalf.LOWER ? yPos : yPos + symbolSize / 2F;
		
		float[] uvs = divideSymbol ? (half == DoubleBlockHalf.UPPER ? new float[]{0, 0, 16, 8} : new float[]{0, 8, 16, 16}) : new float[]{0, 0, 16, 16};
		
		return switch(orientation)
		{
			case UPWARD -> FACE_BAKERY.bakeQuad(new Vector3f(minX, 16 + SYMBOL_OFFSET, minY), new Vector3f(maxX, 16 + SYMBOL_OFFSET, maxY), new BlockElementFace(Direction.UP, 0, "#symbol", new BlockFaceUV(uvs, 180),
					new ForgeFaceData(symbolTint, 0, 0, true)), symbolSprite, Direction.UP, new ModelState(){}, getRotation(direction, 180), true, ID);
			
			case DOWNWARD -> FACE_BAKERY.bakeQuad(new Vector3f(minX, 0 - SYMBOL_OFFSET, minY), new Vector3f(maxX, 0 - SYMBOL_OFFSET, maxY), new BlockElementFace(Direction.DOWN, 0, "#symbol", new BlockFaceUV(uvs, 0),
					new ForgeFaceData(symbolTint, 0, 0, true)), symbolSprite, Direction.DOWN, new ModelState(){}, getRotation(direction), true, ID);
			
			default -> FACE_BAKERY.bakeQuad(new Vector3f(minX, minY, 16 + SYMBOL_OFFSET), new Vector3f(maxX, maxY, 16 + SYMBOL_OFFSET), new BlockElementFace(Direction.SOUTH, 0, "#symbol", new BlockFaceUV(uvs, 0),
					new ForgeFaceData(symbolTint, 0, 0, true)), symbolSprite, Direction.SOUTH, new ModelState(){}, getRotation(direction), true, ID);
		};
	}
	
	public void addSymbolQuads(List<BakedQuad> quads, BlockState state, Direction side, @NotNull RandomSource randomSource, @NotNull ModelData extraData, @Nullable RenderType layer)
	{
		Address address = extraData.get(ModelProperties.ADDRESS_PROPERTY);
		ResourceKey<Symbols> symbolKey = extraData.get(ModelProperties.SYMBOLS_PROPERTY);
		if(address == null || symbolKey == null)
			return;
		
		ClientSymbols symbols = ClientSymbols.getSymbols(symbolKey);
		if(symbols == null)
			return;
		
		// For checking if it should be culled
		Direction direction = state.getValue(CartoucheBlock.FACING);
		Orientation orientation = state.getValue(CartoucheBlock.ORIENTATION);
		
		if(side == Orientation.getForwardDirection(direction, orientation) && (layer == null || RenderType.translucent().equals(layer)))
		{
			DoubleBlockHalf half = state.getValue(CartoucheBlock.HALF);
			
			int symbolCount = address.regularSymbolCount();
			
			float symbolSize = MAX_HEIGHT / symbolCount;
			if(symbolSize > MAX_WIDTH)
				symbolSize = MAX_WIDTH;
			
			int symbolStartIndex = half == DoubleBlockHalf.UPPER ? 0 : (int) Math.floor(symbolCount / 2D);
			int symbolEndIndex = half == DoubleBlockHalf.UPPER ? (int) Math.ceil(symbolCount / 2D) : symbolCount;
			for(int i = symbolStartIndex; i < symbolEndIndex; i++)
			{
				float yStart = symbolSize * symbolCount / 2F;
				if(yStart > MAX_HEIGHT / 2)
					yStart = MAX_HEIGHT / 2;
				
				float yPos = yStart - symbolSize / 2 - symbolSize * i;
				if(half == DoubleBlockHalf.LOWER)
					yPos += 16F;
				
				boolean divideSymbol = symbolCount % 2 == 1 && ( (half == DoubleBlockHalf.UPPER && i == symbolEndIndex - 1) || (half == DoubleBlockHalf.LOWER && i == symbolStartIndex) );
				quads.add(makeSymbolQuad(direction, orientation, ClientSymbols.getSprite(symbols, address.symbolAt(i)), yPos, symbolSize, divideSymbol, half));
			}
		}
	}
	
	
	
	public static class Builder extends SymbolBakedModel.Builder<CartoucheBakedModel>
	{
		public Builder(BlockModel model, ItemOverrides overrides, boolean isGui3d)
		{
			this(model.hasAmbientOcclusion(), model.getGuiLight().lightLikeBlock(), isGui3d, model.getTransforms(), overrides, 0xFFFFFFFF);
		}
		
		public Builder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, int symbolTint)
		{
			super(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint);
		}
		
		public CartoucheBakedModel build(RenderTypeGroup renderTypes)
		{
			if(this.particleIcon == null)
				throw new RuntimeException("Missing particle!");
			else
				return new CartoucheBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides, renderTypes, this.symbolTint);
		}
	}
}
