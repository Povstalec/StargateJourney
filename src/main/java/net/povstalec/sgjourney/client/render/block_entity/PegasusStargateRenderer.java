package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.PegasusStargateModel;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.PegasusStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class PegasusStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<PegasusStargateEntity>
{
	protected final PegasusStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 25; 
	public static final int WORMHOLE_G = 25;
	public static final int WORMHOLE_B = 255;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_event_horizon.png");
	
	public PegasusStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, EVENT_HORIZON_TEXTURE, 0.25F);
		this.wormholeModel.setRGBConfigValue(ClientStargateConfig.pegasus_rgba);
		this.stargateModel = new PegasusStargateModel();
	}
	
	@Override
	public void render(PegasusStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(PegasusStargateBlock.FACING).toYRot();
		Vec3 center = stargate.getRelativeCenter();
		Orientation orientation = blockstate.getValue(AbstractStargateBaseBlock.ORIENTATION);
		
        stack.pushPose();
		stack.translate(center.x(), center.y(), center.z());
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Axis.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Axis.XP.rotationDegrees(90));
        
        this.stargateModel.setCurrentSymbol(stargate.currentSymbol);
        this.stargateModel.renderStargate(stargate, partialTick, stack, source, combinedLight, combinedOverlay);
		
        this.renderWormhole(stargate, stack, source, this.stargateModel, combinedLight, combinedOverlay);
		
	    stack.popPose();
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
