package net.povstalec.sgjourney.client.models.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.common.blocks.tech.CableBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CableBakedModel implements IDynamicBakedModel
{
	public static final ResourceLocation MISSING = new ResourceLocation("missingno");
	
	private static Minecraft minecraft = Minecraft.getInstance();
	
	private final IGeometryBakingContext context;
	
	private TextureAtlasSprite sprite;
	
	private final Vec3 x0y0z0, x1y0z0, x1y0z1, x0y0z1, x0y1z0, x1y1z0, x1y1z1, x0y1z1; // Vectors defining the edges of the center cube that makes up the cable
	private final Vec3 xSpace, ySpace, zSpace; // Vectors defining the space between the center cube and the side of the block
	
	public CableBakedModel(IGeometryBakingContext context, double thickness)
	{
		this.context = context;
		
		double sideSpace = (1 - thickness) / 2; // Empty space on one side of the cable
		
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
	
	private static TextureAtlasSprite getTexture(String path)
	{
		return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation(StargateJourney.MODID, path));
	}
	
	private void initTexture()
	{
		if(sprite == null)
			sprite = getTexture("block/naquadah_cable");
	}
	
	@NotNull
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource randomSource, @NotNull ModelData extraData, @Nullable RenderType layer)
	{
		initTexture();
		List<BakedQuad> quads = new ArrayList<>();
		
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
				quads.add(ClientUtil.bakeQuad(sprite, x0y1z0.subtract(zSpace), x0y1z0, x1y1z0, x1y1z0.subtract(zSpace))); // Up
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z0, x1y0z0.subtract(zSpace), x1y1z0.subtract(zSpace), x1y1z0)); // East
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z0.subtract(zSpace), x1y0z0, x0y0z0, x0y0z0.subtract(zSpace))); // Down
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z0.subtract(zSpace), x0y0z0, x0y1z0, x0y1z0.subtract(zSpace))); // West
			}
			else
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z0, x0y0z0, x0y1z0, x1y1z0)); // North
			
			if(east != CableBlock.ConnectorType.NONE)
			{
				quads.add(ClientUtil.bakeQuad(sprite, x1y1z0, x1y1z1, x1y1z1.add(xSpace), x1y1z0.add(xSpace))); // Up
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z0.add(xSpace), x1y0z0, x1y1z0, x1y1z0.add(xSpace))); // North
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z0.add(xSpace), x1y0z1.add(xSpace), x1y0z1, x1y0z0)); // Down
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z1, x1y0z1.add(xSpace), x1y1z1.add(xSpace), x1y1z1)); // South
			}
			else
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z1, x1y0z0, x1y1z0, x1y1z1)); // East
			
			if(south != CableBlock.ConnectorType.NONE)
			{
				quads.add(ClientUtil.bakeQuad(sprite, x0y1z1, x0y1z1.add(zSpace), x1y1z1.add(zSpace), x1y1z1)); // Up
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z1.add(zSpace), x1y0z1, x1y1z1, x1y1z1.add(zSpace))); // East
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z1, x1y0z1.add(zSpace), x0y0z1.add(zSpace), x0y0z1)); // Down
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z1, x0y0z1.add(zSpace), x0y1z1.add(zSpace), x0y1z1)); // West
			}
			else
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z1, x1y0z1, x1y1z1, x0y1z1)); // South
			
			if(west != CableBlock.ConnectorType.NONE)
			{
				quads.add(ClientUtil.bakeQuad(sprite, x0y1z0.subtract(xSpace), x0y1z1.subtract(xSpace), x0y1z1, x0y1z0)); // Up
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z0, x0y0z0.subtract(xSpace), x0y1z0.subtract(xSpace), x0y1z0)); // North
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z0, x0y0z1, x0y0z1.subtract(xSpace), x0y0z0.subtract(xSpace))); // Down
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z1.subtract(xSpace), x0y0z1, x0y1z1, x0y1z1.subtract(xSpace))); // South
			}
			else
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z0, x0y0z1, x0y1z1, x0y1z0)); // West
			
			if(up != CableBlock.ConnectorType.NONE)
			{
				quads.add(ClientUtil.bakeQuad(sprite, x1y1z0, x0y1z0, x0y1z0.add(ySpace), x1y1z0.add(ySpace))); // North
				quads.add(ClientUtil.bakeQuad(sprite, x1y1z1, x1y1z0, x1y1z0.add(ySpace), x1y1z1.add(ySpace))); // East
				quads.add(ClientUtil.bakeQuad(sprite, x0y1z1, x1y1z1, x1y1z1.add(ySpace), x0y1z1.add(ySpace))); // South
				quads.add(ClientUtil.bakeQuad(sprite, x0y1z0, x0y1z1, x0y1z1.add(ySpace), x0y1z0.add(ySpace))); // West
			}
			else
				quads.add(ClientUtil.bakeQuad(sprite, x0y1z0, x0y1z1, x1y1z1, x1y1z0)); // Up
			
			if(down != CableBlock.ConnectorType.NONE)
			{
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z0.subtract(ySpace), x0y0z0.subtract(ySpace), x0y0z0, x1y0z0)); // North
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z1.subtract(ySpace), x1y0z0.subtract(ySpace), x1y0z0, x1y0z1)); // East
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z1.subtract(ySpace), x1y0z1.subtract(ySpace), x1y0z1, x0y0z1)); // South
				quads.add(ClientUtil.bakeQuad(sprite, x0y0z0.subtract(ySpace), x0y0z1.subtract(ySpace), x0y0z1, x0y0z0)); // West
			}
			else
				quads.add(ClientUtil.bakeQuad(sprite, x1y0z0, x1y0z1, x0y0z1, x0y0z0)); // Down
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
