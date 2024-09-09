package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.client.models.TollanStargateModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.TollanStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.TollanStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

@OnlyIn(Dist.CLIENT)
public class TollanStargateRenderer extends AbstractStargateRenderer<TollanStargateEntity, TollanStargateVariant>
{
	protected final TollanStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 50; 
	public static final int WORMHOLE_G = 100;
	public static final int WORMHOLE_B = 240;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	public TollanStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, 0.125F, true, 38F);
		this.stargateModel = new TollanStargateModel();
	}

	@Override
	protected TollanStargateVariant getClientVariant(TollanStargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
		{
			if(stargateVariant.get().isFound())
				return ClientStargateVariants.getTollanStargateVariant(stargateVariant.get().clientVariant());
			else if(!stargateVariant.get().isMissing())
				stargateVariant.get().handleLocation(ClientStargateVariants.hasTollanStargateVariant(stargateVariant.get().clientVariant()));
		}
		
		return ClientStargateVariants.getTollanStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void render(TollanStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		TollanStargateVariant stargateVariant = getClientVariant(stargate);
		
		BlockState blockstate = stargate.getBlockState();
		Direction facing = blockstate.getValue(TollanStargateBlock.FACING);
		Vec3 center = stargate.getRelativeCenter();
		Orientation orientation = blockstate.getValue(AbstractStargateBaseBlock.ORIENTATION);
	    
	    this.renderCover(stargate, stack, source, combinedLight, combinedOverlay);
		
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
        
        this.stargateModel.renderStargate(stargate, stargateVariant, partialTick, stack, source, combinedLight, combinedOverlay);

		irisModel.renderIris(stargate, stack, source, combinedLight, combinedOverlay, stargate.getIrisProgress(partialTick));
		
        this.renderWormhole(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
		
	    stack.popPose();
	}
	
}
