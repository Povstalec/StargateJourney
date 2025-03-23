package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.client.models.block_entity.ClassicStargateModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClassicStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.ClassicStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class ClassicStargateRenderer extends AbstractStargateRenderer<ClassicStargateEntity, ClassicStargateVariant>
{
	protected final ClassicStargateModel stargateModel;
	
	/*public static final int WORMHOLE_R = 39; 
	public static final int WORMHOLE_G = 113;
	public static final int WORMHOLE_B = 255;
	public static final int WORMHOLE_ALPHA = 255;*/
	
	public ClassicStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context, 0.25F, true, 84F);
		this.stargateModel = new ClassicStargateModel();
	}

	@Override
	protected ClassicStargateVariant getClientVariant(ClassicStargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
		{
			if(stargateVariant.get().isFound())
				return ClientStargateVariants.getClassicStargateVariant(stargateVariant.get().clientVariant());
			else if(!stargateVariant.get().isMissing())
				stargateVariant.get().handleLocation(ClientStargateVariants.hasClassicStargateVariant(stargateVariant.get().clientVariant()));
		}
		
		return ClientStargateVariants.getClassicStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void render(ClassicStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		ClassicStargateVariant stargateVariant = getClientVariant(stargate);
		
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(ClassicStargateBlock.FACING).toYRot();
		Vec3 center = stargate.getRelativeCenter();
		Orientation orientation = blockstate.getValue(AbstractStargateBaseBlock.ORIENTATION);
	    
	    this.renderCover(stargate, stack, source, combinedLight, combinedOverlay);
		
        stack.pushPose();
		stack.translate(center.x(), center.y(), center.z());
        stack.mulPose(Vector3f.YP.rotationDegrees(-facing));
        
        if(orientation == Orientation.UPWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(-90));
        else if(orientation == Orientation.DOWNWARD)
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
		
        this.stargateModel.setRotation(stargate.getRotationDegrees(partialTick));
		this.stargateModel.renderStargate(stargate, stargateVariant, partialTick, stack, source, combinedLight, combinedOverlay);

		irisModel.renderIris(stargate, stack, source, combinedLight, combinedOverlay, stargate.irisInfo().getIrisProgress(partialTick));
		
	    this.renderWormhole(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	    
	    stack.popPose();
	    
	}
	
}
