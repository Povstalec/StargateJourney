package net.povstalec.sgjourney.client.models.block;

import com.google.gson.JsonParseException;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.common.blocks.tech.CableBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CableBakedModel implements IDynamicBakedModel
{
	public static final ResourceLocation MISSING = new ResourceLocation("missingno");
	
	private static final byte DEFAULT_OFFSET = 15;
	private static final byte CONNECTED_OFFSET = 16;
	
	private static Minecraft minecraft = Minecraft.getInstance();
	
	private final IGeometryBakingContext context;
	private final double thickness; // Thickness of the cable
	private final double sideSpace; // Free space between block and cable
	private final ResourceLocation texture;
	private TextureAtlasSprite sprite;
	
	private final Vec3 x0y0z0, x1y0z0, x1y0z1, x0y0z1, x0y1z0, x1y1z0, x1y1z1, x0y1z1; // Vectors defining the edges of the center cube that makes up the cable
	private final Vec3 xSpace, ySpace, zSpace; // Vectors defining the space between the center cube and the side of the block
	
	public CableBakedModel(IGeometryBakingContext context, ResourceLocation texture, double thickness)
	{
		this.context = context;
		this.thickness = thickness;
		this.texture = texture;
		
		this.sideSpace = (1 - thickness) / 2; // Empty space on one side of the cable
		
		this.x0y0z0 = new Vec3(sideSpace, sideSpace, sideSpace);
		this.x1y0z0 = new Vec3(sideSpace + thickness, sideSpace, sideSpace);
		this.x1y0z1 = new Vec3(sideSpace + thickness, sideSpace, sideSpace + thickness);
		this.x0y0z1 = new Vec3(sideSpace, sideSpace, sideSpace + thickness);
		
		this.x0y1z0 = new Vec3(sideSpace, sideSpace + thickness, sideSpace);
		this.x1y1z0 = new Vec3(sideSpace + thickness, sideSpace + thickness, sideSpace);
		this.x1y1z1 = new Vec3(sideSpace + thickness, sideSpace + thickness, sideSpace + thickness);
		this.x0y1z1 = new Vec3(sideSpace, sideSpace + thickness, sideSpace + thickness);
		
		this.xSpace = new Vec3(sideSpace, 0, 0);
		this.ySpace = new Vec3(0, sideSpace, 0);
		this.zSpace = new Vec3(0, 0, sideSpace);
	}
	
	private static TextureAtlasSprite getTexture(ResourceLocation texture)
	{
		return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
	}
	
	private void initTexture()
	{
		if(sprite == null)
			sprite = getTexture(texture);
	}
	
	private static byte getOffset(CableBlock.ConnectorType top, CableBlock.ConnectorType left, CableBlock.ConnectorType bottom, CableBlock.ConnectorType right)
	{
		byte mask = 0;
		
		if(top != CableBlock.ConnectorType.NONE)
			mask |= 0b000001;
		if(left != CableBlock.ConnectorType.NONE)
			mask |= 0b000010;
		if(bottom != CableBlock.ConnectorType.NONE)
			mask |= 0b000100;
		if(right != CableBlock.ConnectorType.NONE)
			mask |= 0b001000;
		return mask;
	}
	
	private static BakedQuad bakeQuad(TextureAtlasSprite sprite, Vec3 vec1, Vec3 vec2, Vec3 vec3, Vec3 vec4, double uStart, double vStart, double uEnd, double vEnd, byte offset)
	{
		return ClientUtil.bakeQuad(sprite, vec1, vec2, vec3, vec4, (float) (16 * uStart), (float) (16 / 17F * (vStart + offset)), (float) (16 * uEnd), (float) (16 / 17F * (vEnd + offset)));
	}
	
	@NotNull
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource randomSource, @NotNull ModelData extraData, @Nullable RenderType layer)
	{
		initTexture();
		List<BakedQuad> quads = new ArrayList<>();
		byte offset;
		
		if(side == null && (layer == null || layer.equals(RenderType.solid())))
		{
			CableBlock.ConnectorType north, south, west, east, up, down;
			if(state != null)
			{
				north = state.getValue(CableBlock.NORTH);
				south = state.getValue(CableBlock.SOUTH);
				west = state.getValue(CableBlock.WEST);
				east = state.getValue(CableBlock.EAST);
				up = state.getValue(CableBlock.UP);
				down = state.getValue(CableBlock.DOWN);
			}
			else // If state is null, it's probably being rendered inside the inventory
			{
				north = CableBlock.ConnectorType.NONE;
				east = CableBlock.ConnectorType.NONE;
				south = CableBlock.ConnectorType.NONE;
				west = CableBlock.ConnectorType.NONE;
				up = CableBlock.ConnectorType.NONE;
				down = CableBlock.ConnectorType.NONE;
			}
			
			if(north != CableBlock.ConnectorType.NONE)
			{
				offset = north == CableBlock.ConnectorType.BLOCK ? CONNECTED_OFFSET : DEFAULT_OFFSET;
				quads.add(bakeQuad(sprite, x0y1z0.subtract(zSpace), x0y1z0, x1y1z0, x1y1z0.subtract(zSpace), 0, sideSpace, sideSpace, sideSpace + thickness, offset)); // Up
				quads.add(bakeQuad(sprite, x1y0z0, x1y0z0.subtract(zSpace), x1y1z0.subtract(zSpace), x1y1z0, sideSpace + thickness, sideSpace, 1, sideSpace + thickness, offset)); // East
				quads.add(bakeQuad(sprite, x1y0z0.subtract(zSpace), x1y0z0, x0y0z0, x0y0z0.subtract(zSpace), 0, sideSpace, sideSpace, sideSpace + thickness, offset)); // Down
				quads.add(bakeQuad(sprite, x0y0z0.subtract(zSpace), x0y0z0, x0y1z0, x0y1z0.subtract(zSpace), 0, sideSpace, sideSpace, sideSpace + thickness, offset)); // West
				
				if(north == CableBlock.ConnectorType.BLOCK)
					quads.add(bakeQuad(sprite, x1y0z0.subtract(zSpace), x0y0z0.subtract(zSpace), x0y1z0.subtract(zSpace), x1y1z0.subtract(zSpace), sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, (byte) 0)); // North
			}
			else
				quads.add(bakeQuad(sprite, x1y0z0, x0y0z0, x0y1z0, x1y1z0, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, getOffset(up, east, down, west))); // North
			
			if(east != CableBlock.ConnectorType.NONE)
			{
				offset = east == CableBlock.ConnectorType.BLOCK ? CONNECTED_OFFSET : DEFAULT_OFFSET;
				quads.add(bakeQuad(sprite, x1y1z0, x1y1z1, x1y1z1.add(xSpace), x1y1z0.add(xSpace), sideSpace, 0, sideSpace + thickness, sideSpace, offset)); // Up
				quads.add(bakeQuad(sprite, x1y0z0.add(xSpace), x1y0z0, x1y1z0, x1y1z0.add(xSpace), 0, sideSpace, sideSpace, sideSpace + thickness, offset)); // North
				quads.add(bakeQuad(sprite, x1y0z0.add(xSpace), x1y0z1.add(xSpace), x1y0z1, x1y0z0, sideSpace, sideSpace + thickness, sideSpace + thickness, 1, offset)); // Down
				quads.add(bakeQuad(sprite, x1y0z1, x1y0z1.add(xSpace), x1y1z1.add(xSpace), x1y1z1, sideSpace + thickness, sideSpace, 1, sideSpace + thickness, offset)); // South
				
				if(east == CableBlock.ConnectorType.BLOCK)
					quads.add(bakeQuad(sprite, x1y0z1.add(xSpace), x1y0z0.add(xSpace), x1y1z0.add(xSpace), x1y1z1.add(xSpace), sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, (byte) 0)); // East
			}
			else
				quads.add(bakeQuad(sprite, x1y0z1, x1y0z0, x1y1z0, x1y1z1, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, getOffset(up, south, down, north))); // East
			
			if(south != CableBlock.ConnectorType.NONE)
			{
				offset = south == CableBlock.ConnectorType.BLOCK ? CONNECTED_OFFSET : DEFAULT_OFFSET;
				quads.add(bakeQuad(sprite, x0y1z1, x0y1z1.add(zSpace), x1y1z1.add(zSpace), x1y1z1, sideSpace + thickness, sideSpace, 1, sideSpace + thickness, offset)); // Up
				quads.add(bakeQuad(sprite, x1y0z1.add(zSpace), x1y0z1, x1y1z1, x1y1z1.add(zSpace), 0, sideSpace, sideSpace, sideSpace + thickness, offset)); // East
				quads.add(bakeQuad(sprite, x1y0z1, x1y0z1.add(zSpace), x0y0z1.add(zSpace), x0y0z1, sideSpace + thickness, sideSpace, 1, sideSpace + thickness, offset)); // Down
				quads.add(bakeQuad(sprite, x0y0z1, x0y0z1.add(zSpace), x0y1z1.add(zSpace), x0y1z1, sideSpace + thickness, sideSpace, 1, sideSpace + thickness, offset)); // West
				
				if(south == CableBlock.ConnectorType.BLOCK)
					quads.add(bakeQuad(sprite, x0y0z1.add(zSpace), x1y0z1.add(zSpace), x1y1z1.add(zSpace), x0y1z1.add(zSpace), sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, (byte) 0)); // South
			}
			else
				quads.add(bakeQuad(sprite, x0y0z1, x1y0z1, x1y1z1, x0y1z1, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, getOffset(up, west, down, east))); // South
			
			if(west != CableBlock.ConnectorType.NONE)
			{
				offset = west == CableBlock.ConnectorType.BLOCK ? CONNECTED_OFFSET : DEFAULT_OFFSET;
				quads.add(bakeQuad(sprite, x0y1z0.subtract(xSpace), x0y1z1.subtract(xSpace), x0y1z1, x0y1z0, sideSpace, sideSpace + thickness, sideSpace + thickness, 1, offset)); // Up
				quads.add(bakeQuad(sprite, x0y0z0, x0y0z0.subtract(xSpace), x0y1z0.subtract(xSpace), x0y1z0, sideSpace + thickness, sideSpace, 1, sideSpace + thickness, offset)); // North
				quads.add(bakeQuad(sprite, x0y0z0, x0y0z1, x0y0z1.subtract(xSpace), x0y0z0.subtract(xSpace), sideSpace, 0, sideSpace + thickness, sideSpace, offset)); // Down
				quads.add(bakeQuad(sprite, x0y0z1.subtract(xSpace), x0y0z1, x0y1z1, x0y1z1.subtract(xSpace), 0, sideSpace, sideSpace, sideSpace + thickness, offset)); // South
				
				if(west == CableBlock.ConnectorType.BLOCK)
					quads.add(bakeQuad(sprite, x0y0z0.subtract(xSpace), x0y0z1.subtract(xSpace), x0y1z1.subtract(xSpace), x0y1z0.subtract(xSpace), sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, (byte) 0)); // West
			}
			else
				quads.add(bakeQuad(sprite, x0y0z0, x0y0z1, x0y1z1, x0y1z0, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, getOffset(up, north, down, south))); // West
			
			if(up != CableBlock.ConnectorType.NONE)
			{
				offset = up == CableBlock.ConnectorType.BLOCK ? CONNECTED_OFFSET : DEFAULT_OFFSET;
				quads.add(bakeQuad(sprite, x1y1z0, x0y1z0, x0y1z0.add(ySpace), x1y1z0.add(ySpace), sideSpace, 0, sideSpace + thickness, sideSpace, offset)); // North
				quads.add(bakeQuad(sprite, x1y1z1, x1y1z0, x1y1z0.add(ySpace), x1y1z1.add(ySpace), sideSpace, 0, sideSpace + thickness, sideSpace, offset)); // East
				quads.add(bakeQuad(sprite, x0y1z1, x1y1z1, x1y1z1.add(ySpace), x0y1z1.add(ySpace), sideSpace, 0, sideSpace + thickness, sideSpace, offset)); // South
				quads.add(bakeQuad(sprite, x0y1z0, x0y1z1, x0y1z1.add(ySpace), x0y1z0.add(ySpace), sideSpace, 0, sideSpace + thickness, sideSpace, offset)); // West
				
				if(up == CableBlock.ConnectorType.BLOCK)
					quads.add(bakeQuad(sprite, x0y1z0.add(ySpace), x0y1z1.add(ySpace), x1y1z1.add(ySpace), x1y1z0.add(ySpace), sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, (byte) 0)); // Up
			}
			else
				quads.add(bakeQuad(sprite, x0y1z0, x0y1z1, x1y1z1, x1y1z0, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, getOffset(east, north, west, south))); // Up
			
			if(down != CableBlock.ConnectorType.NONE)
			{
				offset = down == CableBlock.ConnectorType.BLOCK ? CONNECTED_OFFSET : DEFAULT_OFFSET;
				quads.add(bakeQuad(sprite, x1y0z0.subtract(ySpace), x0y0z0.subtract(ySpace), x0y0z0, x1y0z0, sideSpace, sideSpace + thickness, sideSpace + thickness, 1, offset)); // North
				quads.add(bakeQuad(sprite, x1y0z1.subtract(ySpace), x1y0z0.subtract(ySpace), x1y0z0, x1y0z1, sideSpace, sideSpace + thickness, sideSpace + thickness, 1, offset)); // East
				quads.add(bakeQuad(sprite, x0y0z1.subtract(ySpace), x1y0z1.subtract(ySpace), x1y0z1, x0y0z1, sideSpace, sideSpace + thickness, sideSpace + thickness, 1, offset)); // South
				quads.add(bakeQuad(sprite, x0y0z0.subtract(ySpace), x0y0z1.subtract(ySpace), x0y0z1, x0y0z0, sideSpace, sideSpace + thickness, sideSpace + thickness, 1, offset)); // West
				
				if(down == CableBlock.ConnectorType.BLOCK)
					quads.add(bakeQuad(sprite, x1y0z0.subtract(ySpace), x1y0z1.subtract(ySpace), x0y0z1.subtract(ySpace), x0y0z0.subtract(ySpace), sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, (byte) 0)); // Down
			}
			else
				quads.add(bakeQuad(sprite, x1y0z0, x1y0z1, x0y0z1, x0y0z0, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, getOffset(west, north, east, south))); // Down
		}
		
		return quads;
	}
	
	@Override
	public boolean useAmbientOcclusion()
	{
		return true;
	}
	
	@Override
	public boolean isGui3d()
	{
		return false;
	}
	
	@Override
	public boolean usesBlockLight()
	{
		return false;
	}
	
	@Override
	public boolean isCustomRenderer()
	{
		return false;
	}
	
	@Override
	public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
	{
		return ChunkRenderTypeSet.all();
	}
	
	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return sprite == null ? minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply((MISSING)) : sprite;
	}
	
	@Override
	public ItemTransforms getTransforms()
	{
		return context.getTransforms();
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return ItemOverrides.EMPTY;
	}
}
