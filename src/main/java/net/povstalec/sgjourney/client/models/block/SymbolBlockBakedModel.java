package net.povstalec.sgjourney.client.models.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SymbolBlockBakedModel implements IDynamicBakedModel
{
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	
	private final IGeometryBakingContext context;
	private final ResourceLocation texture;
	private TextureAtlasSprite sprite;
	private final ResourceLocation particleTexture;
	private TextureAtlasSprite particleSprite;
	
	public SymbolBlockBakedModel(IGeometryBakingContext context)
	{
		this.context = context;
		
		this.texture = StargateJourney.sgjourneyLocation("symbol/milky_way/point_of_origin/centauri");
		this.particleTexture = new ResourceLocation("minecraft", "block/sandstone_top");
	}
	
	private void initTexture()
	{
		if(sprite == null)
			sprite = ClientUtil.getTexture(texture);
		if(particleSprite == null)
			particleSprite = ClientUtil.getTexture(particleTexture);
	}
	
	@NotNull
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource randomSource, @NotNull ModelData extraData, @Nullable RenderType layer)
	{
		initTexture();
		List<BakedQuad> quads = new ArrayList<>();
		
		/*ResourceLocation location = extraData.get(ModelProperties.POINT_OF_ORIGIN_TEXTURE_PROPERTY);
		if(location != null)
		{
			PointOfOrigin pointOfOrigin = ClientUtil.getPointOfOrigin(location);
			pointOfOrigin.getTexture();
		}*/
		
		if(side == null && (layer == null || layer.equals(RenderType.translucent())))
		{
			//TODO Check out BlockModel.bakeFace()
			quads.add(FACE_BAKERY.bakeQuad(new Vector3f(0, 0, 0), new Vector3f(16, 16, 0), new BlockElementFace(Direction.NORTH, 0, "#north", new BlockFaceUV(new float[] {0, 0, 16, 16}, 0)), sprite, Direction.NORTH, BlockModelRotation.X0_Y0, null, true, texture));
			//quads.add(ClientUtil.bakeQuad(sprite, new Vec3(1, 1, 0), new Vec3(0, 1, 0), new Vec3(0, 1, 1), new Vec3(1, 1, 1), 0F, 0F, 16F, 16F));
			//quads.add(ClientUtil.bakeQuad(sprite, new Vec3(1, 1, 0), new Vec3(1, 0, 0), new Vec3(0, 0, 0), new Vec3(0, 1, 0), 0F, 0F, 16F, 16F));
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
		return true;
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
		return particleSprite == null ? Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation()) : particleSprite;
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
