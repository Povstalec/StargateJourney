package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.client.models.UniverseStargateModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.UniverseStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.UniverseStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class UniverseStargateRenderer extends AbstractStargateRenderer<UniverseStargateEntity, UniverseStargateVariant>
{
	protected final UniverseStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 200; 
	public static final int WORMHOLE_G = 220;
	public static final int WORMHOLE_B = 255;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	public UniverseStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, 0.25F, false, 84F);
		this.stargateModel = new UniverseStargateModel();
	}

	@Override
	protected UniverseStargateVariant getClientVariant(UniverseStargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
		{
			if(stargateVariant.get().isFound())
				return ClientStargateVariants.getUniverseStargateVariant(stargateVariant.get().clientVariant());
			else if(!stargateVariant.get().isMissing())
				stargateVariant.get().handleLocation(ClientStargateVariants.hasUniverseStargateVariant(stargateVariant.get().clientVariant()));
		}
		
		return ClientStargateVariants.getUniverseStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void render(UniverseStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		UniverseStargateVariant stargateVariant = getClientVariant(stargate);
		
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
        this.stargateModel.renderStargate(stargate, stargateVariant, partialTick, stack, source, combinedLight, combinedOverlay);
        
        stack.pushPose();
        stack.mulPose(Axis.ZP.rotationDegrees(stargate.getRotation(partialTick) / UniverseStargateEntity.MAX_ROTATION * 360));
		irisModel.renderIris(stargate, stack, source, combinedLight, combinedOverlay, stargate.irisInfo().getIrisProgress(partialTick));
        stack.popPose();
		
        this.renderWormhole(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	    stack.popPose();
	    
	}
	
}
