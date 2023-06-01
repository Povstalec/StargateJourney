package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.TransportRingsModel;
import net.povstalec.sgjourney.common.block_entities.TransportRingsEntity;

public class TransportRingsRenderer implements BlockEntityRenderer<TransportRingsEntity>
{
	protected final TransportRingsModel transportRings;
	
	public TransportRingsRenderer(BlockEntityRendererProvider.Context context)
	{
		transportRings = new TransportRingsModel(context.bakeLayer(Layers.TRANSPORT_RING_LAYER));
	}
	
	private float getHeight(TransportRingsEntity rings, int ringNumber, float partialTick)
	{
		float ringHeight = 0;
		
		int startTicks = 6 * (ringNumber - 1);
		float movingHeight = rings.getProgress(partialTick) - 6 * (ringNumber - 1);
		int staticHeight = rings.getTransportHeight() - 2 * (ringNumber - 1);
		
		int stopHeight = rings.getTransportHeight() + 17 - 4 * (5 - ringNumber);
		
		if(rings.ticks == rings.progress && rings.progress > startTicks && rings.progress < stopHeight)
			ringHeight = movingHeight * 4;
		else if(rings.progress >= stopHeight)
			ringHeight = staticHeight * 4;
		else if(rings.ticks != rings.progress && rings.progress > startTicks && rings.progress < stopHeight)
			ringHeight = movingHeight * 4;
		
		if(rings.emptySpace > 0)
				return ringHeight;
		else if(rings.emptySpace < 0)
			return -ringHeight;
		
		return 0;
	}
	
	@Override
	public void render(TransportRingsEntity rings, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(0.5, 0.5, 0.5);
		//BlockPos transportPos = rings.getBlockPos().above(rings.emptySpace);
		//int transportLight = LevelRenderer.getLightColor(rings.getLevel(), transportPos);
		this.transportRings.setRingHeight(1, getHeight(rings, 1, partialTick));
		this.transportRings.setRingHeight(2, getHeight(rings, 2, partialTick));
		this.transportRings.setRingHeight(3, getHeight(rings, 3, partialTick));
		this.transportRings.setRingHeight(4, getHeight(rings, 4, partialTick));
		this.transportRings.setRingHeight(5, getHeight(rings, 5, partialTick));
		this.transportRings.renderTransportRings(rings, partialTick, stack, source, combinedLight, combinedOverlay);
	    stack.popPose();
	}
}
