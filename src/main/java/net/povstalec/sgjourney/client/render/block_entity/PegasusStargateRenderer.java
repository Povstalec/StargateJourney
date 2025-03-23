package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.client.models.block_entity.PegasusStargateModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.PegasusStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.PegasusStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class PegasusStargateRenderer extends AbstractStargateRenderer<PegasusStargateEntity, PegasusStargateVariant>
{
	protected final PegasusStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 25; 
	public static final int WORMHOLE_G = 25;
	public static final int WORMHOLE_B = 255;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	public PegasusStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, 0.25F, false, 84F);
		this.stargateModel = new PegasusStargateModel();
	}
	
	@Override
	protected PegasusStargateVariant getClientVariant(PegasusStargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
		{
			if(stargateVariant.get().isFound())
				return ClientStargateVariants.getPegasusStargateVariant(stargateVariant.get().clientVariant());
			else if(!stargateVariant.get().isMissing())
				stargateVariant.get().handleLocation(ClientStargateVariants.hasPegasusStargateVariant(stargateVariant.get().clientVariant()));
		}
		
		return ClientStargateVariants.getPegasusStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void render(PegasusStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		PegasusStargateVariant stargateVariant = getClientVariant(stargate);
		
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(PegasusStargateBlock.FACING).toYRot();
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
        
		//stack.translate(0, -0.15, 0);
        
        this.stargateModel.setCurrentSymbol(stargate.currentSymbol);
        this.stargateModel.renderStargate(stargate, stargateVariant, partialTick, stack, source, combinedLight, combinedOverlay);

		irisModel.renderIris(stargate, stack, source, combinedLight, combinedOverlay, stargate.irisInfo().getIrisProgress(partialTick));
		
        this.renderWormhole(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
		
        //shieldModel.renderShield(stargate, stack, source, combinedLight, combinedOverlay); //TODO Check if these things render correctly with Oculus
		
	    stack.popPose();
	}
	
}
