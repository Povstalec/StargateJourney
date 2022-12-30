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
import net.povstalec.sgjourney.init.LayerInit;

@OnlyIn(Dist.CLIENT)
public class TransportRingsRenderer implements BlockEntityRenderer<TransportRingsEntity>
{
	private static final ResourceLocation TRANSPORT_RINGS_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/block/transport_rings.png");
	private final ModelPart first_ring;
	private final ModelPart second_ring;
	private final ModelPart third_ring;
	private final ModelPart fourth_ring;
	private final ModelPart fifth_ring;
	
	public TransportRingsRenderer(BlockEntityRendererProvider.Context p_173554_)
	{
		ModelPart modelpart = p_173554_.bakeLayer(LayerInit.TRANSPORT_RING_LAYER);
		this.first_ring = modelpart.getChild("first_ring");
		this.second_ring = modelpart.getChild("second_ring");
		this.third_ring = modelpart.getChild("third_ring");
		this.fourth_ring = modelpart.getChild("fourth_ring");
		this.fifth_ring = modelpart.getChild("fifth_ring");
	}
	
	private float getHeight(int ringNumber, int emptySpace, int transportHeight, int ticks, int progress, float partialTicks)
	{
		float ringHeight = 0;
		
		int startTicks = 6 * (ringNumber - 1);
		int movingHeight = progress - 6 * (ringNumber - 1);
		int staticHeight = transportHeight - 2 * (ringNumber - 1);
		
		int stopHeight = transportHeight + 17 - 4 * (5 - ringNumber);
		
		if(ticks == progress && progress > startTicks && progress < stopHeight)
			ringHeight = (movingHeight/* + partialTicks*/) * 4;
		else if(progress >= stopHeight)
			ringHeight = staticHeight * 4;
		else if(ticks != progress && progress > startTicks && progress < stopHeight)
			ringHeight = (movingHeight/* - partialTicks*/) * 4;
		
		if(emptySpace > 0)
				return ringHeight;
		else if(emptySpace < 0)
			return -ringHeight;
		
		return 0;
	}
	
	@Override
	public void render(TransportRingsEntity rings, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		VertexConsumer vertexconsumer = source.getBuffer(RenderType.entitySolid(TRANSPORT_RINGS_TEXTURE));
		// It takes the rings 29 ticks to get into position if there is empty space right above them
		
		if(rings.progress > 0)
		{
			this.first_ring.y = getHeight(1, rings.emptySpace, rings.transportHeight, rings.ticks, rings.progress, partialTick);
			this.first_ring.render(stack, vertexconsumer, rings.transportLight, combinedOverlay);
		}
		if(rings.progress > 6)
		{
			this.second_ring.y = getHeight(2, rings.emptySpace, rings.transportHeight, rings.ticks, rings.progress, partialTick);
		    this.second_ring.render(stack, vertexconsumer, rings.transportLight, combinedOverlay);
		}
		if(rings.progress > 12)
		{
			this.third_ring.y = getHeight(3, rings.emptySpace, rings.transportHeight, rings.ticks, rings.progress, partialTick);
		    this.third_ring.render(stack, vertexconsumer, rings.transportLight, combinedOverlay);
		}
		if(rings.progress > 18)
		{
			this.fourth_ring.y = getHeight(4, rings.emptySpace, rings.transportHeight, rings.ticks, rings.progress, partialTick);
		    this.fourth_ring.render(stack, vertexconsumer, rings.transportLight, combinedOverlay);
		}
		this.fifth_ring.y = getHeight(5, rings.emptySpace, rings.transportHeight, rings.ticks, rings.progress, partialTick);
	    if(rings.progress <= 24)
	    	this.fifth_ring.render(stack, vertexconsumer, combinedLight, combinedOverlay);
	    else
	    	this.fifth_ring.render(stack, vertexconsumer, rings.transportLight, combinedOverlay);
	}
}
