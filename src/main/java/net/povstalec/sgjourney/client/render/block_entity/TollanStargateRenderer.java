package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.TollanStargateModel;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.TollanStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;

@OnlyIn(Dist.CLIENT)
public class TollanStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<TollanStargateEntity>
{
	protected final TollanStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 50; 
	public static final int WORMHOLE_G = 100;
	public static final int WORMHOLE_B = 240;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_event_horizon.png");
	private static final ResourceLocation SHINY_EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_event_horizon_shiny.png");

	public TollanStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, EVENT_HORIZON_TEXTURE, SHINY_EVENT_HORIZON_TEXTURE, 0.125F, true, 40F);
		this.stargateModel = new TollanStargateModel();
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
        
        double shiftBase = orientation.getIndex() *  0.5;
		double shiftX = center.x();
        double shiftY = center.y();
		double shiftZ = center.z();
        
		if(orientation != Orientation.REGULAR)
		{
			if(facing.getAxis() == Direction.Axis.X)
				shiftX += facing.getAxisDirection().getStep() * shiftBase;
			else
				shiftZ += facing.getAxisDirection().getStep() * shiftBase;
			
		}
        stack.translate(shiftX, shiftY, shiftZ);
        
        stack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Axis.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Axis.XP.rotationDegrees(90));
        
        this.stargateModel.renderStargate(stargate, partialTick, stack, source, combinedLight, combinedOverlay);

		irisModel.renderIris(stargate, stack, source, combinedLight, combinedOverlay, stargate.getIrisProgress(partialTick));
		
        this.renderWormhole(stargate, stack, source, this.stargateModel, combinedLight, combinedOverlay);
		
	    stack.popPose();
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
