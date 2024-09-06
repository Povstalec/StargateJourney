package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.client.models.MilkyWayStargateModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.MilkyWayStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.MilkyWayStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class MilkyWayStargateRenderer extends AbstractStargateRenderer<MilkyWayStargateEntity, MilkyWayStargateVariant>
{
	protected final MilkyWayStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 55; 
	public static final int WORMHOLE_G = 55;
	public static final int WORMHOLE_B = 255;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	public MilkyWayStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, 0.25F, false, 84F);
		this.stargateModel = new MilkyWayStargateModel();
	}
	
	@Override
	protected MilkyWayStargateVariant getClientVariant(MilkyWayStargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.get().clientVariant());
		
		return ClientStargateVariants.getMilkyWayStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void render(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		MilkyWayStargateVariant stargateVariant = getClientVariant(stargate);
		
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(MilkyWayStargateBlock.FACING).toYRot();
		Vec3 center = stargate.getRelativeCenter();
		Orientation orientation = blockstate.getValue(AbstractStargateBaseBlock.ORIENTATION);
	    
	    this.renderCover(stargate, stack, source, combinedLight, combinedOverlay);
		
        stack.pushPose();
		stack.translate(center.x(), center.y() - (canSink(stargate) ? 0.25 : 0), center.z());
        stack.mulPose(Vector3f.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
        
        this.stargateModel.setRotation(stargate.getRotation(partialTick) / (float) MilkyWayStargateEntity.MAX_ROTATION * 360F);
        this.stargateModel.renderStargate(stargate, stargateVariant, partialTick, stack, source, combinedLight, combinedOverlay);
        

		//stack.mulPose(Axis.ZP.rotationDegrees(90));
		//stack.translate(2.5, -2.5, 0);
		
		irisModel.renderIris(stargate, stack, source, combinedLight, combinedOverlay, stargate.getIrisProgress(partialTick));
        
        this.renderWormhole(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
		
        //shieldModel.renderShield(stargate, stack, source, combinedLight, combinedOverlay); //TODO Check if these things render correctly with Oculus
	    
	    stack.popPose();
	}
	
}
