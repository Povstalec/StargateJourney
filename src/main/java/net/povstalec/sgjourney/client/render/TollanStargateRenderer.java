package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.TollanStargateModel;
import net.povstalec.sgjourney.client.models.WormholeModel;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.TollanStargateBlock;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.Orientation;

@OnlyIn(Dist.CLIENT)
public class TollanStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<TollanStargateEntity>
{
	protected static final int r = ClientStargateConfig.tollan_r.get();
	protected static final int g = ClientStargateConfig.tollan_g.get();
	protected static final int b = ClientStargateConfig.tollan_b.get();

	protected final WormholeModel wormholeModel;
	protected final TollanStargateModel stargateModel;

	public TollanStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
		this.wormholeModel = new WormholeModel(context.bakeLayer(Layers.EVENT_HORIZON_LAYER), r, g, b);
		this.stargateModel = new TollanStargateModel(
				context.bakeLayer(Layers.TOLLAN_RING_LAYER),
				context.bakeLayer(Layers.TOLLAN_SYMBOL_RING_LAYER),
				context.bakeLayer(Layers.TOLLAN_CHEVRON_LAYER));
	}
	
	@Override
	public void render(TollanStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		Direction facing = blockstate.getValue(TollanStargateBlock.FACING);
		Vec3 center = stargate.getRelativeCenter();
		Orientation orientation = blockstate.getValue(AbstractStargateBaseBlock.ORIENTATION);
		
        stack.pushPose();

		if(orientation == Orientation.REGULAR)
			stack.translate(center.x(), center.y() - 0.5D, center.z());
		else {
			double shiftBase = orientation == Orientation.UPWARD ? 0.5D : -0.5D;
			double shiftY = 0.125D;
			switch (facing) {
				case NORTH -> stack.translate(center.x(), center.y() - shiftY, center.z() - shiftBase);
				case SOUTH -> stack.translate(center.x(), center.y() - shiftY, center.z() + shiftBase);
				case EAST -> stack.translate(center.x() + shiftBase, center.y() - shiftY, center.z());
				case WEST -> stack.translate(center.x() - shiftBase, center.y() - shiftY, center.z());
//				default -> stack.translate(center.x(), center.y(), center.z());
			}
		}
        stack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Axis.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Axis.XP.rotationDegrees(90));
        
        this.stargateModel.renderStargate(stargate, partialTick, stack, source, combinedLight, combinedOverlay);
		
        if(stargate.isConnected())
	    	this.wormholeModel.renderEventHorizon(stack, source, combinedLight, combinedOverlay, stargate.getTickCount());
		
	    stack.popPose();
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
