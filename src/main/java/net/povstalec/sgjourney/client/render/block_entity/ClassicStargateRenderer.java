package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.client.models.ClassicStargateModel;
import net.povstalec.sgjourney.client.models.WormholeModel;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.ClassicStargateBlock;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.Orientation;

public class ClassicStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<ClassicStargateEntity>
{
	protected final WormholeModel wormholeModel;
	protected final ClassicStargateModel stargateModel;
	
	public ClassicStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
		this.wormholeModel = new WormholeModel(ClientStargateConfig.classic_rgba, 0.25F);
		this.stargateModel = new ClassicStargateModel();
	}
	
	@Override
	public void render(ClassicStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(ClassicStargateBlock.FACING).toYRot();
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
	    	this.wormholeModel.renderEventHorizon((AbstractStargateEntity) stargate, stack, source, combinedLight, combinedOverlay);
	    
	    stack.popPose();
	    
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
