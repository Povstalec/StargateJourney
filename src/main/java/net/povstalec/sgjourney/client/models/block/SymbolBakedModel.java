package net.povstalec.sgjourney.client.models.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SymbolBakedModel extends SimpleBakedModel
{
	protected final int symbolTint;
	
	protected final ChunkRenderTypeSet chunkRenderTypeSet;
	
	protected static final float SYMBOL_OFFSET = 0.01F;
	
	public static final Vector3f CENTER = new Vector3f(0.5F, 0.5F, 0.5F);
	
	public SymbolBakedModel(List<BakedQuad> unculledFaces, Map<Direction, List<BakedQuad>> culledFaces, boolean hasAmbientOcclusion, boolean isGui3d, boolean usesBlockLight,
							TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, RenderTypeGroup renderTypes, int symbolTint)
	{
		super(unculledFaces, culledFaces, hasAmbientOcclusion, isGui3d, usesBlockLight, particleIcon, transforms, overrides, renderTypes);
		this.symbolTint = symbolTint;
		
		ArrayList<RenderType> list = new ArrayList<>(blockRenderTypes.asList());
		if(!list.contains(RenderType.translucent()))
			list.add(RenderType.translucent()); // Adding translucent render type in order to render the symbol overlay
		this.chunkRenderTypeSet = ChunkRenderTypeSet.of(list);
	}
	
	@Override
	public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
	{
		return chunkRenderTypeSet;
	}
	
	@NotNull
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource randomSource, @NotNull ModelData extraData, @Nullable RenderType layer)
	{
		List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, randomSource, extraData, layer));
		addSymbolQuads(quads, state, side, randomSource, extraData, layer);
		return quads;
	}
	
	public abstract void addSymbolQuads(List<BakedQuad> quads, BlockState state, Direction side, @NotNull RandomSource randomSource, @NotNull ModelData extraData, @Nullable RenderType layer);
	
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
		
		public abstract T build(RenderTypeGroup renderTypes);
	}
}
