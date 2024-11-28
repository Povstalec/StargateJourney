package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.povstalec.sgjourney.client.models.IrisModel;
import net.povstalec.sgjourney.client.models.ShieldModel;
import net.povstalec.sgjourney.client.models.WormholeModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public abstract class AbstractStargateRenderer<StargateEntity extends AbstractStargateEntity, Variant extends ClientStargateVariant> implements BlockEntityRenderer<StargateEntity>
{
	protected final WormholeModel wormholeModel;
	protected final ShieldModel shieldModel;
	protected final IrisModel irisModel;
	
	private final RandomSource randomsource = RandomSource.create();
	
	public AbstractStargateRenderer(BlockEntityRendererProvider.Context context,
			float maxDefaultDistortion, boolean renderWhenOpen, float maxOpenIrisDegrees)
	{
		this.shieldModel = new ShieldModel();
		this.irisModel = new IrisModel(renderWhenOpen, maxOpenIrisDegrees);
		this.wormholeModel = new WormholeModel(maxDefaultDistortion);
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
	/**
	 * Method for getting the client variant of the Stargate
	 * @param stargate
	 * @return
	 */
	protected abstract Variant getClientVariant(StargateEntity stargate);
	
	protected void renderWormhole(AbstractStargateEntity stargate, Variant stargateVariant, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		if(stargate.isConnected())
	    	this.wormholeModel.renderWormhole(stargate, stack, source, stargateVariant.getWormhole(), combinedLight, combinedOverlay);
	}
	
	protected void renderCover(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
	    for(Map.Entry<StargatePart, BlockState> entry : stargate.blockCover.blockStates.entrySet())
	    {
	    	renderCoverBlock(stargate, entry.getValue(), entry.getKey(), stack, source, combinedOverlay);
	    }
	}
	
	protected void renderCoverBlock(AbstractStargateEntity stargate, BlockState state, StargatePart part, PoseStack stack, MultiBufferSource source, int combinedOverlay)
	{
		Level level = stargate.getLevel();
		Direction direction = stargate.getDirection();
		Orientation orientation = stargate.getOrientation();
		
		if(direction != null && orientation != null)
		{
			Vec3 relativeBlockPos = part.getRelativeRingPos(stargate.getBlockPos(), direction, orientation);
			BlockPos absolutePos = part.getRingPos(stargate.getBlockPos(), stargate.getDirection(), stargate.getOrientation());
			
			stack.pushPose();
			
			stack.translate(relativeBlockPos.x(), relativeBlockPos.y(), relativeBlockPos.z());
			BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();//Minecraft.getInstance().getBlockColors().
			//dispatcher.renderSingleBlock(state, stack, source, LevelRenderer.getLightColor(level, absolutePos), combinedOverlay, ModelData.EMPTY, null);
			
			
			BakedModel model = dispatcher.getBlockModel(state);
			for(RenderType renderType : model.getRenderTypes(state, randomsource, ModelData.EMPTY))
			{
				dispatcher.renderBatched(state, absolutePos, level, stack, source.getBuffer(renderType), true, randomsource, model.getModelData(level, absolutePos, state, ModelData.EMPTY), null);
			}
			
			stack.popPose();
		}
	}
	
	protected boolean canSink(AbstractStargateEntity stargate)
	{
	    return stargate.blockCover.canSinkGate;
	}
	
	@Override
	public AABB getRenderBoundingBox(AbstractStargateEntity stargate)
	{
		return new AABB(stargate.getCenterPos().getX() - 3, stargate.getCenterPos().getY() - 3, stargate.getCenterPos().getZ() - 3, stargate.getCenterPos().getX() + 4, stargate.getCenterPos().getY() + 4, stargate.getCenterPos().getZ() + 4);
	}
}
