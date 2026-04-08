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
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.data.ModelData;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.ModelProperties;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.blocks.SymbolBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class SymbolBlockBakedModel extends SymbolBakedModel
{
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	
	public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("symbol_block_model");
	
	public static final Vector3f SYMBOL_FRONT_START = new Vector3f(0, 0, 16 + SYMBOL_OFFSET);
	public static final Vector3f SYMBOL_FRONT_END = new Vector3f(16, 16, 16 + SYMBOL_OFFSET);
	public static final Vector3f SYMBOL_TOP_START = new Vector3f(0, 16 + SYMBOL_OFFSET, 0);
	public static final Vector3f SYMBOL_TOP_END = new Vector3f(16, 16 + SYMBOL_OFFSET, 16);
	public static final Vector3f SYMBOL_BOTTOM_START = new Vector3f(0, 0 - SYMBOL_OFFSET, 0);
	public static final Vector3f SYMBOL_BOTTOM_END = new Vector3f(16, 0 - SYMBOL_OFFSET, 16);
	
	protected int symbolNumber;
	protected ResourceLocation symbol;
	
	public SymbolBlockBakedModel(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight,
								 TextureAtlasSprite particleIcon,ItemTransforms transforms,ItemOverrides overrides, RenderTypeGroup renderTypes, int symbolTint)
	{
		super(unculledFaces, culledFaces, hasAmbientOcclusion, isGui3d, usesBlockLight, particleIcon, transforms, overrides, renderTypes, symbolTint);
		
		this.symbolNumber = -1;
	}
	
	public SymbolBlockBakedModel(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight,
								 TextureAtlasSprite particleIcon,ItemTransforms transforms,ItemOverrides overrides, RenderTypeGroup renderTypes, int symbolTint, int symbolNumber, ResourceLocation symbol)
	{
		super(unculledFaces, culledFaces, hasAmbientOcclusion, isGui3d, usesBlockLight, particleIcon, transforms, overrides, renderTypes, symbolTint);
		
		this.symbolNumber = symbolNumber;
		this.symbol = symbol;
	}
	
	public static TextureAtlasSprite getSymbolSprite(int symbolNumber, ResourceLocation symbol)
	{
		if(symbolNumber == 0 && symbol != null) // Point of Origin
		{
			ClientPointOfOrigin pointOfOrigin = ClientPointOfOrigin.getPointOfOrigin(Conversion.locationToPointOfOrigin(symbol));
			if(pointOfOrigin != null)
				return ClientPointOfOrigin.getSprite(pointOfOrigin);
		}
		else if(symbol != null) // Symbols
		{
			ClientSymbols symbols = ClientSymbols.getSymbols(Conversion.locationToSymbols(symbol));
			if(symbols != null)
				return ClientSymbols.getSprite(symbols, symbolNumber);
		}
		
		return null;
	}
	
	public TextureAtlasSprite getSymbolSprite(@NotNull ModelData extraData)
	{
		Integer symbolNumber = extraData.get(ModelProperties.SYMBOL_INDEX_PROPERTY);
		if(symbolNumber == null) // No symbol number somehow
			return null;
		
		// Show Point of Origin
		ResourceKey<PointOfOrigin> pointOfOriginKey = extraData.get(ModelProperties.POINT_OF_ORIGIN_PROPERTY);
		if(symbolNumber == 0 && pointOfOriginKey != null)
		{
			ClientPointOfOrigin pointOfOrigin = ClientPointOfOrigin.getPointOfOrigin(pointOfOriginKey);
			if(pointOfOrigin != null)
				return ClientPointOfOrigin.getSprite(pointOfOrigin);
		}
		
		// Show symbols
		ResourceKey<Symbols> symbolLocation = extraData.get(ModelProperties.SYMBOLS_PROPERTY);
		if(symbolLocation != null)
		{
			ClientSymbols symbols = ClientSymbols.getSymbols(symbolLocation);
			if(symbols != null)
				return ClientSymbols.getSprite(symbols, symbolNumber);
		}
		
		return null;
	}
	
	public static BakedQuad makeSymbolQuad(Direction direction, Orientation orientation, TextureAtlasSprite symbolSprite, int symbolTint)
	{
		return switch(orientation)
		{
			case UPWARD -> FACE_BAKERY.bakeQuad(SYMBOL_TOP_START, SYMBOL_TOP_END, new BlockElementFace(Direction.UP, 0, "#symbol", new BlockFaceUV(new float[]{0, 0, 16, 16}, 0),
					new ForgeFaceData(symbolTint, 0, 0, true)), symbolSprite, Direction.UP, new ModelState(){}, getRotation(direction), true, ID);
			
			case DOWNWARD -> FACE_BAKERY.bakeQuad(SYMBOL_BOTTOM_START, SYMBOL_BOTTOM_END, new BlockElementFace(Direction.DOWN, 0, "#symbol", new BlockFaceUV(new float[]{0, 0, 16, 16}, 0),
					new ForgeFaceData(symbolTint, 0, 0, true)), symbolSprite, Direction.DOWN, new ModelState(){}, getRotation(direction), true, ID);
			
			default -> FACE_BAKERY.bakeQuad(SYMBOL_FRONT_START, SYMBOL_FRONT_END, new BlockElementFace(Direction.SOUTH, 0, "#symbol", new BlockFaceUV(new float[]{0, 0, 16, 16}, 0),
					new ForgeFaceData(symbolTint, 0, 0, true)), symbolSprite, Direction.SOUTH, new ModelState(){}, getRotation(direction), true, ID);
		};
	}
	
	public void addSymbolQuads(List<BakedQuad> quads, BlockState state, Direction side, @NotNull RandomSource randomSource, @NotNull ModelData extraData, @Nullable RenderType layer)
	{
		TextureAtlasSprite symbolSprite = symbolNumber >= 0 ? getSymbolSprite(symbolNumber, symbol) : getSymbolSprite(extraData);
		
		// For checking if it should be culled
		Direction direction = state.getValue(SymbolBlock.FACING);
		Orientation orientation = state.getValue(SymbolBlock.ORIENTATION);
		
		if(symbolSprite != null && side == Orientation.getForwardDirection(direction, orientation) && (layer == null || RenderType.translucent().equals(layer)))
			quads.add(makeSymbolQuad(direction, orientation, symbolSprite, symbolTint));
	}
	
	
	
	public static class Builder extends SymbolBakedModel.Builder<SymbolBlockBakedModel>
	{
		protected int symbolNumber;
		protected ResourceLocation symbol;
		
		public Builder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, int symbolTint)
		{
			super(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint);
			
			this.symbolNumber = -1;
		}
		
		public Builder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, int symbolTint, int symbolNumber, ResourceLocation symbol)
		{
			super(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, symbolTint);
			
			this.symbolNumber = symbolNumber;
			this.symbol = symbol;
		}
		
		public SymbolBlockBakedModel build(RenderTypeGroup renderTypes)
		{
			if(this.particleIcon == null)
				throw new RuntimeException("Missing particle!");
			else if(this.symbolNumber >= 0)
				return new SymbolBlockBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides, renderTypes, this.symbolTint, this.symbolNumber, this.symbol);
			else
				return new SymbolBlockBakedModel(this.unculledFaces, this.culledFaces, this.hasAmbientOcclusion, this.usesBlockLight, this.isGui3d, this.particleIcon, this.transforms, this.overrides, renderTypes, this.symbolTint);
		}
	}
}
