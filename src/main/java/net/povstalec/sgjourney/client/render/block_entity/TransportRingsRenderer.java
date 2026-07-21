package net.povstalec.sgjourney.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.block_entity.TransportRingModel;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AncientTransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.GoauldTransportRingsEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class TransportRingsRenderer<T extends AbstractTransportRingsEntity<?>> implements BlockEntityRenderer<T>
{
	// Transport Rings move at a speed of 0.25m/tick
	
	protected final List<TransportRingModel<T>> transportRings = new ArrayList<>(5);
	
	private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
	
	public TransportRingsRenderer(ResourceLocation texture, BlockEntityRendererProvider.Context context)
	{
		for(int i = 0; i < 5; i++)
		{
			transportRings.add(new TransportRingModel<>(texture, 36, 5F / 16F, 2.498F, 2F));
		}
	}
	
	// Ring height ignoring direction
	protected float getAbsoluteRingHeight(int hoverDuration, int transportHeight, float progress, int ringNumber)
	{
		if(progress < 6 * ringNumber) // Idle height
			return 0;
		
		// While hovering, each ring's center should be located half a meter above the previous one
		int hoverHeight = AbstractTransportRingsEntity.getRingHoverHeight(transportHeight, ringNumber);
		int hoverStartTicks = AbstractTransportRingsEntity.getRingHoverStartTicks(transportHeight, ringNumber); // Progress at which the ring will start hovering
		
		if(progress < hoverStartTicks) // Height while Rings are rising into position
			return progress - 6 * ringNumber;
		
		int hoverEndTicks = hoverStartTicks + hoverDuration + 6 * (4 - ringNumber);
		
		if(progress < hoverEndTicks) // Height while Rings are hovering in place
			return hoverHeight;
		
		int totalTicks = hoverEndTicks + hoverHeight;
		
		if(progress < totalTicks) // Height while Rings are descending back to idle position
			return totalTicks - progress;
		
		return 0;
	}
	
	protected float getRingHeight(AbstractTransportRingsEntity<?> rings, float partialTick, int ringNumber)
	{
		float progress = rings.getProgress(partialTick);
		int transportHeight = rings.getTransportHeight();
		int hoverDuration = AbstractTransportRingsEntity.HOVER_TICKS;
		
		if(rings.emptySpace >= 0)
			return 4 * getAbsoluteRingHeight(hoverDuration, transportHeight, progress, ringNumber);
		else
			return -4 * getAbsoluteRingHeight(hoverDuration, transportHeight, progress, ringNumber);
	}
	
	@Override
	public void render(@NotNull T transportRings, float partialTick, PoseStack stack,
					   @NotNull MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		stack.pushPose();
		stack.translate(0.5, 0.5, 0.5);
		
		for(int i = 0; i < this.transportRings.size(); i++)
		{
			float ringHeight = getRingHeight(transportRings, partialTick, i) / 16F;
			if(ringHeight == 0 && i != 4) // Don't render overlapping rings when Transport Rings are idle
				continue;
			
			mutablePos.set(transportRings.getBlockPos().getX(), transportRings.getBlockPos().getY() + Math.round(ringHeight), transportRings.getBlockPos().getZ());
			int transportLight = transportRings.getLevel() != null ? LevelRenderer.getLightColor(transportRings.getLevel(), mutablePos) : combinedLight;
			
			stack.pushPose();
			stack.translate(0, ringHeight, 0);
			
			this.transportRings.get(i).render(transportRings, partialTick, stack, source, transportLight, combinedOverlay);
			
			stack.popPose();
		}
		
	    stack.popPose();
	}
	
	@Override
	public @NotNull AABB getRenderBoundingBox(T transportRings)
	{
		BlockPos pos = transportRings.getBlockPos();
		return new AABB(pos.getX() - 3, pos.getY() - (3 + AbstractTransportRingsEntity.MAX_TRANSPORT_HEIGHT), pos.getZ() - 3,
				pos.getX() + 4, pos.getY() + (4 + AbstractTransportRingsEntity.MAX_TRANSPORT_HEIGHT), pos.getZ() + 4);
	}
	
	
	
	public static class Ancient extends TransportRingsRenderer<AncientTransportRingsEntity>
	{
		public Ancient(BlockEntityRendererProvider.Context context)
		{
			super(StargateJourney.sgjourneyLocation("textures/entity/transport_rings/ancient_transport_rings.png"), context);
		}
	}
	
	public static class Goauld extends TransportRingsRenderer<GoauldTransportRingsEntity>
	{
		public Goauld(BlockEntityRendererProvider.Context context)
		{
			super(StargateJourney.sgjourneyLocation("textures/entity/transport_rings/goauld_transport_rings.png"), context);
		}
	}
}
