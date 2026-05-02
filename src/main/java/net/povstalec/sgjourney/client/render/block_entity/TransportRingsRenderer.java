package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.block_entity.TransportRingModel;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;

public class TransportRingsRenderer implements BlockEntityRenderer<AbstractTransportRingsEntity<?>>
{
	protected final TransportRingModel[] transportRings = new TransportRingModel[5];
	
	private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
	
	public TransportRingsRenderer(BlockEntityRendererProvider.Context context)
	{
		for(int i = 0; i < transportRings.length; i++)
		{
			transportRings[i] = new TransportRingModel(context.bakeLayer(Layers.TRANSPORT_RING_LAYER));
		}
	}
	
	private float getProgress(AbstractTransportRingsEntity<?> rings, float partialTick)
	{
		float progress = rings.getProgress(partialTick);
		
		int maxProgress = rings.getTransportHeight() + 2 * TransporterConnection.TRANSPORT_TICKS;
		if(progress > maxProgress)
			return 2 * maxProgress - TransporterConnection.TRANSPORT_TICKS - progress;
		
		return progress;
	}
	
	private float ringHeight(AbstractTransportRingsEntity<?> rings, int ringNumber, float partialTick)
	{
		float height = getProgress(rings, partialTick) - 6 * ringNumber;
		
		return 4 * (rings.emptySpace >= 0 ? height : -height);
	}
	
	private float stopHeight(AbstractTransportRingsEntity<?> rings, int ringNumber)
	{
		float height = rings.getTransportHeight() - 2 * ringNumber;
		
		return 4 * (rings.emptySpace >= 0 ? height : -height);
	}
	
	private float getHeight(AbstractTransportRingsEntity<?> rings, int ringNumber, float partialTick)
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
	public void render(AbstractTransportRingsEntity transportRings, float partialTick, PoseStack stack,
					   MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(0.5, 0.5, 0.5);
		
		for(int i = 0; i < this.transportRings.length; i++)
		{
			float ringHeight = getHeight(transportRings, i, partialTick);
			
			mutablePos.set(transportRings.getBlockPos().getX(), transportRings.getBlockPos().getY() + Math.round(ringHeight / 16F), transportRings.getBlockPos().getZ());
			int transportLight = LevelRenderer.getLightColor(transportRings.getLevel(), mutablePos);
			
			stack.pushPose();
			stack.translate(0, ringHeight / 16D, 0);
			
			this.transportRings[i].render(transportRings, partialTick, stack, source, transportLight, combinedOverlay, ringHeight);
			
			stack.popPose();
		}
		
	    stack.popPose();
	}
}
