package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.models.TransportRingsModel;

public class TransportRingsRenderer implements BlockEntityRenderer<TransportRingsEntity>
{
	/*private static final ResourceLocation TRANSPORT_RINGS_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/block/transport_rings.png");
	private final ModelPart first_ring;
	private final ModelPart second_ring;
	private final ModelPart third_ring;
	private final ModelPart fourth_ring;
	private final ModelPart fifth_ring;*/
	protected final TransportRingsModel transportRings;
	
	public TransportRingsRenderer(BlockEntityRendererProvider.Context context)
	{
		/*ModelPart modelpart = context.bakeLayer(Layers.TRANSPORT_RING_LAYER);
		this.first_ring = modelpart.getChild("first_ring");
		this.second_ring = modelpart.getChild("second_ring");
		this.third_ring = modelpart.getChild("third_ring");
		this.fourth_ring = modelpart.getChild("fourth_ring");
		this.fifth_ring = modelpart.getChild("fifth_ring");*/
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
		this.transportRings.setRingHeight(1, getHeight(rings, 1, partialTick));
		this.transportRings.setRingHeight(2, getHeight(rings, 2, partialTick));
		this.transportRings.setRingHeight(3, getHeight(rings, 3, partialTick));
		this.transportRings.setRingHeight(4, getHeight(rings, 4, partialTick));
		this.transportRings.setRingHeight(5, getHeight(rings, 5, partialTick));
		this.transportRings.renderTransportRings(rings, partialTick, stack, source, combinedLight, combinedOverlay);
	}
}
