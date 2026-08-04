package net.povstalec.sgjourney.client.models.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class SymbolBakedModel extends SimpleBakedModel
{
	protected final int symbolTint;
	
	protected static final float SYMBOL_OFFSET = 0.01F;
	
	public static final Vector3f CENTER = new Vector3f(0.5F, 0.5F, 0.5F);
	
	public SymbolBakedModel(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight,
							TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, int symbolTint)
	{
		super(unculledFaces, culledFaces, hasAmbientOcclusion, isGui3d, usesBlockLight, particleIcon, transforms, overrides);
		this.symbolTint = symbolTint;
	}
	
	public static int toABGR(int argb)
	{
		return (argb & 0xFF00FF00) | ((argb >> 16) & 0x000000FF) | ((argb << 16) & 0x00FF0000);
	}
	
	public static void applyColorToQuad(int color, BakedQuad quad)
	{
		final int fixedColor = toABGR(color);
		int[] vertices = quad.getVertices();
		
		int stride = DefaultVertexFormat.BLOCK.getIntegerSize();
		int colorIndex = DefaultVertexFormat.BLOCK.getElements().indexOf(DefaultVertexFormat.ELEMENT_COLOR);
		int elementColor = colorIndex < 0 ? -1 : DefaultVertexFormat.BLOCK.getOffset(colorIndex) / 4;
		
		for(int i = 0; i < 4; i++)
		{
			vertices[i * stride + elementColor] = fixedColor;
		}
	}
	
	@NotNull
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull Random randomSource, @NotNull IModelData extraData)
	{
		List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, randomSource, extraData));
		addSymbolQuads(quads, state, side, randomSource, extraData);
		return quads;
	}
	
	public abstract void addSymbolQuads(List<BakedQuad> quads, BlockState state, Direction side, @NotNull Random randomSource, @NotNull IModelData extraData);
	
	public static BlockElementRotation getRotation(Direction direction)
	{
		return getRotation(direction, 0);
	}
	
	public static BlockElementRotation getRotation(Direction direction, int startAt)
	{
		return switch(direction)
		{
			case EAST -> new BlockElementRotation(CENTER, Direction.Axis.Y, (startAt + 90) % 360, false);
			case NORTH -> new BlockElementRotation(CENTER, Direction.Axis.Y, (startAt + 180) % 360, false);
			case WEST -> new BlockElementRotation(CENTER, Direction.Axis.Y, (startAt + 270) % 360, false);
			default -> new BlockElementRotation(CENTER, Direction.Axis.Y, startAt % 360, false);
		};
	}
	
	
	
	/**
	 * Basically a copy of {@link SimpleBakedModel.Builder}
	 */
	public static abstract class Builder<T extends SymbolBakedModel>
	{
		protected final List<BakedQuad> unculledFaces = Lists.newArrayList();
		protected final Map<Direction, List<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
		protected final ItemOverrides overrides;
		protected final boolean hasAmbientOcclusion;
		protected TextureAtlasSprite particleIcon;
		protected final boolean usesBlockLight;
		protected final boolean isGui3d;
		protected final ItemTransforms transforms;
		protected final int symbolTint;
		
		public Builder(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, int symbolTint)
		{
			for(Direction direction : Direction.values())
			{
				this.culledFaces.put(direction, Lists.newArrayList());
			}
			
			this.overrides = overrides;
			this.hasAmbientOcclusion = hasAmbientOcclusion;
			this.usesBlockLight = usesBlockLight;
			this.isGui3d = isGui3d;
			this.transforms = transforms;
			this.symbolTint = symbolTint;
		}
		
		public SymbolBakedModel.Builder<T> addCulledFace(Direction direction, BakedQuad quad)
		{
			this.culledFaces.get(direction).add(quad);
			return this;
		}
		
		public SymbolBakedModel.Builder<T> addUnculledFace(BakedQuad quad)
		{
			this.unculledFaces.add(quad);
			return this;
		}
		
		public SymbolBakedModel.Builder<T> particle(TextureAtlasSprite particleIcon)
		{
			this.particleIcon = particleIcon;
			return this;
		}
		
		public SymbolBakedModel.Builder<T> item()
		{
			return this;
		}
		
		public abstract T build();
	}
}
