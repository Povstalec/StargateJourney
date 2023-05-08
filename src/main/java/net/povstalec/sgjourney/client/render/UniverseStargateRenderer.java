package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.UniverseStargateModel;
import net.povstalec.sgjourney.client.models.WormholeModel;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.UniverseStargateBlock;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.Orientation;

@OnlyIn(Dist.CLIENT)
public class UniverseStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<UniverseStargateEntity>
{
	protected static final int r = ClientStargateConfig.universe_r.get();
	protected static final int g = ClientStargateConfig.universe_g.get();
	protected static final int b = ClientStargateConfig.universe_b.get();
	
	protected final WormholeModel wormholeModel;
	protected final UniverseStargateModel stargateModel;
	
	public UniverseStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
		this.wormholeModel = new WormholeModel(context.bakeLayer(Layers.EVENT_HORIZON_LAYER), r, g, b);

		this.stargateModel = new UniverseStargateModel(
				context.bakeLayer(Layers.UNIVERSE_RING_LAYER), 
				context.bakeLayer(Layers.UNIVERSE_SYMBOL_RING_LAYER), 
				context.bakeLayer(Layers.UNIVERSE_DIVIDER_LAYER), 
				context.bakeLayer(Layers.UNIVERSE_CHEVRON_LAYER));
	}
	
	@Override
	public void render(UniverseStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(UniverseStargateBlock.FACING).toYRot();
		Vec3 center = stargate.getRelativeCenter();
		Orientation orientation = blockstate.getValue(AbstractStargateBaseBlock.ORIENTATION);
		
        stack.pushPose();
		stack.translate(center.x(), center.y(), center.z());
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Axis.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Axis.XP.rotationDegrees(90));
        
        this.stargateModel.setRotation(stargate.getRotation(partialTick));
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
