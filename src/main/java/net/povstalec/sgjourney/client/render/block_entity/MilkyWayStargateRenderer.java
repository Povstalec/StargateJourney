package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.client.models.MilkyWayStargateModel;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.MilkyWayStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class MilkyWayStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<MilkyWayStargateEntity>
{
	protected final MilkyWayStargateModel stargateModel;
	
	public static final int WORMHOLE_R = 55; 
	public static final int WORMHOLE_G = 55;
	public static final int WORMHOLE_B = 255;
	public static final int WORMHOLE_ALPHA = 255;
	
	public MilkyWayStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, WORMHOLE_R, WORMHOLE_G, WORMHOLE_B, WORMHOLE_ALPHA, 0.25F);
		this.wormholeModel.setRGBConfigValue(ClientStargateConfig.milky_way_rgba);
		this.stargateModel = new MilkyWayStargateModel();
	}
	
	@Override
	public void render(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(MilkyWayStargateBlock.FACING).toYRot();
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
        
        this.renderWormhole(stargate, stack, source, this.stargateModel, combinedLight, combinedOverlay);
        
        //this.shieldModel.renderShield((AbstractStargateEntity) stargate, stack, source, combinedLight, combinedOverlay);
	    
	    stack.popPose();
	    
	    //this.renderCover(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
