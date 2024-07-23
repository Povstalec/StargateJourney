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
import net.povstalec.sgjourney.client.models.UniverseStargateModel;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.UniverseStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;

public class UniverseStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<UniverseStargateEntity>
{
	protected final UniverseStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 200; 
	public static final int WORMHOLE_G = 220;
	public static final int WORMHOLE_B = 255;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_event_horizon.png");
	private static final ResourceLocation SHINY_EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_event_horizon_shiny.png");
	
	public UniverseStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, EVENT_HORIZON_TEXTURE, SHINY_EVENT_HORIZON_TEXTURE, 0.25F, false, 84F);
		this.stargateModel = new UniverseStargateModel();
	}
	
	@Override
	public void render(UniverseStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(UniverseStargateBlock.FACING).toYRot();
		Vec3 center = stargate.getRelativeCenter();
		Orientation orientation = blockstate.getValue(AbstractStargateBaseBlock.ORIENTATION);
	    
	    this.renderCover(stargate, stack, source, combinedLight, combinedOverlay);
		
        stack.pushPose();
		stack.translate(center.x(), center.y(), center.z());
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Axis.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Axis.XP.rotationDegrees(90));
        
        this.stargateModel.setRotation(stargate.getRotation(partialTick) / UniverseStargateEntity.MAX_ROTATION * 360);
        this.stargateModel.renderStargate(stargate, partialTick, stack, source, combinedLight, combinedOverlay);
        
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(stargate.getRotation(partialTick) / UniverseStargateEntity.MAX_ROTATION * 360));
		irisModel.renderIris(stargate, stack, source, combinedLight, combinedOverlay, stargate.getIrisProgress(partialTick));
        stack.popPose();
		
        this.renderWormhole(stargate, stack, source, this.stargateModel, combinedLight, combinedOverlay);
	    stack.popPose();
	    
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
