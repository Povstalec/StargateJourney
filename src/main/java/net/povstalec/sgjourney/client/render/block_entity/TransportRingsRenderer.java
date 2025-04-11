package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.block_entity.TransportRingsModel;
import net.povstalec.sgjourney.common.block_entities.tech.TransportRingsEntity;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;

public class TransportRingsRenderer implements BlockEntityRenderer<TransportRingsEntity>
{
	protected final TransportRingsModel transportRings;
	
	public TransportRingsRenderer(BlockEntityRendererProvider.Context context)
	{
		transportRings = new TransportRingsModel(context.bakeLayer(Layers.TRANSPORT_RING_LAYER));
	}
	
	private float getProgress(TransportRingsEntity rings, float partialTick)
	{
		float progress = rings.getProgress(partialTick);
		
		int maxProgress = rings.getTransportHeight() + 2 * TransporterConnection.TRANSPORT_TICKS;
		if(progress > maxProgress)
			return 2 * maxProgress - TransporterConnection.TRANSPORT_TICKS - progress;
		
		return progress;
	}
	
	private float ringHeight(TransportRingsEntity rings, int ringNumber, float partialTick)
	{
		float height = getProgress(rings, partialTick) - 6 * ringNumber;
		
		return 4 * (rings.emptySpace >= 0 ? height : -height);
	}
	
	private float stopHeight(TransportRingsEntity rings, int ringNumber)
	{
		float height = rings.getTransportHeight() - 2 * ringNumber;
		
		return 4 * (rings.emptySpace >= 0 ? height : -height);
	}
	
	private float getHeight(TransportRingsEntity rings, int ringNumber, float partialTick)
	{
		float progress = getProgress(rings, partialTick);
		int startTicks = 6 * ringNumber;
		
		if(progress <= startTicks)
			return 0;
		
		float ringHeight = ringHeight(rings, ringNumber, partialTick);
		float stopHeight = stopHeight(rings, ringNumber);
		
		if((rings.emptySpace >= 0 && ringHeight >= stopHeight) || (rings.emptySpace < 0 && ringHeight <= stopHeight))
			return stopHeight;
		
		return ringHeight;
	}
	
	@Override
	public void render(TransportRingsEntity rings, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(0.5, 0.5, 0.5);
		//BlockPos transportPos = rings.getBlockPos().above(rings.emptySpace);
		//int transportLight = LevelRenderer.getLightColor(rings.getLevel(), transportPos);
		this.transportRings.setRingHeight(1, getHeight(rings, 0, partialTick));
		this.transportRings.setRingHeight(2, getHeight(rings, 1, partialTick));
		this.transportRings.setRingHeight(3, getHeight(rings, 2, partialTick));
		this.transportRings.setRingHeight(4, getHeight(rings, 3, partialTick));
		this.transportRings.setRingHeight(5, getHeight(rings, 4, partialTick));
		this.transportRings.renderTransportRings(rings, partialTick, stack, source, combinedLight, combinedOverlay);
	    stack.popPose();
	}
}
