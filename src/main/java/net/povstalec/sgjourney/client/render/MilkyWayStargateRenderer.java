package net.povstalec.sgjourney.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.MilkyWayStargateEntity;
import net.povstalec.sgjourney.blocks.stargate.MilkyWayStargateBlock;
import net.povstalec.sgjourney.config.ClientStargateConfig;

@OnlyIn(Dist.CLIENT)
public class MilkyWayStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<MilkyWayStargateEntity>
{
	private static final ResourceLocation OUTER_RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_outer_ring.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_chevron.png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_chevron_lit.png");
	private static final ResourceLocation INNER_RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_inner_ring.png");
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/block/event_horizon.png");
	
	public MilkyWayStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
	}
	
	private void renderChevronLight(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, int chevronNumber)
	{
		if(stargate.chevronsActive == chevronNumber || stargate.chevronsActive > chevronNumber)
		{
			VertexConsumer engaged_chevron_texture = source.getBuffer(RenderType.entitySolid(ENGAGED_CHEVRON_TEXTURE));
		    chevronLight(chevronNumber).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
		else
		{
			VertexConsumer chevron_texture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
			chevronLight(chevronNumber).render(stack, chevron_texture, combinedLight, combinedOverlay);
		}
	}
	
	private void engageChevron(MilkyWayStargateEntity stargate, int chevron)
	{
		if(stargate.chevronsActive <= 2)
		{
			chevronLight(chevron).x = (float) (2 * Math.cos(Math.toRadians(90 - 40 * (stargate.chevronsActive + 1))));
			chevronLight(chevron).y = (float) (2 * Math.sin(Math.toRadians(90 - 40 * (stargate.chevronsActive + 1))));
			chevronFront(chevron).x = (float) (-2 * Math.cos(Math.toRadians(90 - 40 * (stargate.chevronsActive + 1))));
			chevronFront(chevron).y = (float) (-2 * Math.sin(Math.toRadians(90 - 40 * (stargate.chevronsActive + 1))));
		}
		else if(stargate.chevronsActive >= 3 && stargate.chevronsActive <= 5)
		{
			chevronLight(chevron).x = (float) (2 * Math.cos(Math.toRadians(10 - 40 * (stargate.chevronsActive + 1))));
			chevronLight(chevron).y = (float) (2 * Math.sin(Math.toRadians(10 - 40 * (stargate.chevronsActive + 1))));
			chevronFront(chevron).x = (float) (-2 * Math.cos(Math.toRadians(10 - 40 * (stargate.chevronsActive + 1))));
			chevronFront(chevron).y = (float) (-2 * Math.sin(Math.toRadians(10 - 40 * (stargate.chevronsActive + 1))));
		}
		else if(stargate.chevronsActive >= 6 && stargate.chevronsActive <= 7)
		{
			chevronLight(chevron).x = (float) (2 * Math.cos(Math.toRadians(90 - 40 * (stargate.chevronsActive - 2))));
			chevronLight(chevron).y = (float) (2 * Math.sin(Math.toRadians(90 - 40 * (stargate.chevronsActive - 2))));
			chevronFront(chevron).x = (float) (-2 * Math.cos(Math.toRadians(90 - 40 * (stargate.chevronsActive - 2))));
			chevronFront(chevron).y = (float) (-2 * Math.sin(Math.toRadians(90 - 40 * (stargate.chevronsActive - 2))));
		}
	}
	
	private void disengageChevrons()
	{
		for(int i = 1; i <= 9; i++)
		{
			chevronFront(i).x = 0;
			chevronFront(i).y = 0;
			chevronLight(i).x = 0;
			chevronLight(i).y = 0;
		}
	}
	
	private void renderMovieChevron(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		if(stargate.currentSymbol == 0 && stargate.isChevronRaised)
			chevronLight(9).y = 2;
		else
			chevronLight(9).y = 0;
		
		if(!stargate.isChevronRaised && stargate.isBusy())
		{
			VertexConsumer engaged_chevron_texture = source.getBuffer(RenderType.entitySolid(ENGAGED_CHEVRON_TEXTURE));
			chevronLight(9).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
		else
		{
			VertexConsumer chevron_texture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
			chevronLight(9).render(stack, chevron_texture, combinedLight, combinedOverlay);
		}
	}
	
	private void renderMilkyWayPrimaryChevron(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		if(stargate.isChevronRaised)
		{
			chevronLight(9).y = 2;
			chevronFront(9).y = -2;
			VertexConsumer engaged_chevron_texture = source.getBuffer(RenderType.entitySolid(ENGAGED_CHEVRON_TEXTURE));
		    chevronLight(9).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
		else if(stargate.isBusy())
		{
			chevronLight(9).y = 0;
			chevronFront(9).y = 0;
			VertexConsumer engaged_chevron_texture = source.getBuffer(RenderType.entitySolid(ENGAGED_CHEVRON_TEXTURE));
			chevronLight(9).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
		else
		{
			chevronLight(9).y = 0;
			chevronFront(9).y = 0;
			VertexConsumer chevron_texture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
		    chevronLight(9).render(stack, chevron_texture, combinedLight, combinedOverlay);
		    chevronFront(9).render(stack, chevron_texture, combinedLight, combinedOverlay);
		}
	}
	
	@Override
	public void render(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(MilkyWayStargateBlock.FACING).toYRot();
		stack.translate(0.5D, 3.5D, 0.5D);
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
		
		if(ClientStargateConfig.use_movie_stargate_model.get())
		{
	        if(stargate.isChevronRaised && stargate.currentSymbol != 0)
	        	engageChevron(stargate, stargate.chevronsActive + 1);
	        else
	        	disengageChevrons();
			renderMovieChevron(stargate, stack, source, combinedLight, combinedOverlay);
		}
		else
			renderMilkyWayPrimaryChevron(stargate, stack, source, combinedLight, combinedOverlay);
		
		for(int i = 1; i <= 8; i++)
		{
			renderChevronLight(stargate, stack, source, combinedLight, combinedOverlay, i);
		}
		
		VertexConsumer chevron_texture = source.getBuffer(SGJourneyRenderTypes.stargate(CHEVRON_TEXTURE));
		for(int i = 1; i <= 9; i++)
		{
			chevronFront(i).render(stack, chevron_texture, combinedLight, combinedOverlay);
			chevronBack(i).render(stack, chevron_texture, combinedLight, combinedOverlay);
		}
	    
	    if(stargate.isBusy())
	    {
	    	float tickCount = (float)stargate.tick;
			
		    VertexConsumer event_horizon_texture = source.getBuffer(SGJourneyRenderTypes.eventHorizon(EVENT_HORIZON_TEXTURE, 0.0F, tickCount * 0.03125F));
			this.event_horizon.render(stack, event_horizon_texture, 255, combinedOverlay);
	    }

		VertexConsumer ring_texture = source.getBuffer(SGJourneyRenderTypes.stargate(INNER_RING_TEXTURE));
	    
		// Renders the spinny ring
	    for(int i = 0; i < stargate.symbolCount; i++)
	    {
	    	symbols(stargate).getChild("symbol" + i).render(stack, ring_texture, combinedLight, combinedOverlay);
	    }
		
	    // Renders Symbols
	    for(int i = 0; i < stargate.symbolCount; i++)
	    {
	    	symbols(stargate).getChild("symbol" + i).render(stack, source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbol(stargate, i))), combinedLight, combinedOverlay, 48.0F/255.0F, 49.0F/255.0F, 63.0F/255.0F, 1.0F);
	    }
	    
	    // Rotates the spinny ring
	    for(int i = 0; i < stargate.symbolCount; i++)
	    {
	    	symbols(stargate).getChild("symbol" + i).zRot = (float) Math.toRadians(180 - stargate.angle() * i + stargate.degrees * 2);
	    }
		
	    VertexConsumer ring = source.getBuffer(RenderType.entitySolid(OUTER_RING_TEXTURE));
	    this.ring.render(stack, ring, combinedLight, combinedOverlay);
	    this.dividers(stargate).render(stack, ring, combinedLight, combinedOverlay);
	    this.dividers(stargate).zRot = (float) Math.toRadians(stargate.degrees * 2);
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
