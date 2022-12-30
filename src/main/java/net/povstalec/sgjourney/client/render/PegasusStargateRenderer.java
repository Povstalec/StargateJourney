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
import net.povstalec.sgjourney.block_entities.PegasusStargateEntity;
import net.povstalec.sgjourney.blocks.stargate.MilkyWayStargateBlock;

@OnlyIn(Dist.CLIENT)
public class PegasusStargateRenderer extends AbstractStargateRenderer implements BlockEntityRenderer<PegasusStargateEntity>
{
	private static final ResourceLocation RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_outer_ring.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_chevron.png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_chevron_lit.png");
	private static final ResourceLocation SYMBOLS_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_ring.png");
	private static final ResourceLocation EMPTY = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/empty_slot.png");
	//private static final ResourceLocation SYMBOL = new ResourceLocation(StargateJourney.MODID, "textures/symbols/pegasus.png");
	
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/block/event_horizon.png");
	
	public PegasusStargateRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
	}
	
	private void renderChevron(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
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
	
	private void renderPegasusPrimaryChevron(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		if(stargate.isBusy())
		{
			VertexConsumer engaged_chevron_texture = source.getBuffer(RenderType.entitySolid(ENGAGED_CHEVRON_TEXTURE));
		    this.chevronLight(9).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
		else
		{
			VertexConsumer chevron_texture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
		    this.chevronLight(9).render(stack, chevron_texture, combinedLight, combinedOverlay);
		    this.chevronFront(9).render(stack, chevron_texture, combinedLight, combinedOverlay);
		}
	}
	
	@Override
	public void render(PegasusStargateEntity stargate, float partialTick, PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		BlockState blockstate = stargate.getBlockState();
		float facing = blockstate.getValue(MilkyWayStargateBlock.FACING).toYRot();
		VertexConsumer chevron_texture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
		
		stack.translate(0.5D, 3.5D, 0.5D);
        stack.mulPose(Axis.YP.rotationDegrees(-facing));
		
		
		renderPegasusPrimaryChevron(stargate, stack, source, combinedLight, combinedOverlay);
		for(int i = 1; i < 9; i++)
		{
			renderChevron(stargate, stack, source, combinedLight, combinedOverlay, i);
		}
		
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
	    
	    /*VertexConsumer empty = source.getBuffer(RenderType.entitySolid(EMPTY));
	    //this.point_of_origin.render(stack, empty, combinedLight, combinedOverlay);
		
		VertexConsumer ring_texture = source.getBuffer(RenderType.entitySolid(SYMBOLS_TEXTURE));
	    this.symbols_36.render(stack, ring_texture, combinedLight, combinedOverlay);
	    
	    VertexConsumer symbol = source.getBuffer(RenderType.entityNoOutline(SYMBOL));
	    
	    for(int i = 0; i < stargate.inputAddress.length; i++)
	    {
	    	this.symbols_36.getChild("symbol" + stargate.inputAddress[i]).render(stack, symbol, 255, combinedOverlay, 42.0F/255.0F, 201.0F/255.0F, 207.0F/255.0F, 1.0F);
	    }
		
	    if(stargate.currentSymbol == 0 && stargate.symbolBuffer > 0)
	    {
	    	if(!stargate.pointOfOrigin.equals("placeholder"))
		    {
				ResourceLocation PoO_TEXTURE = new ResourceLocation(stargate.pointOfOrigin);
				VertexConsumer poo_texture = source.getBuffer(RenderType.entityNoOutline(PoO_TEXTURE));
			    //this.point_of_origin.render(stack, poo_texture, 255, combinedOverlay, 42.0F/255.0F, 201.0F/255.0F, 207.0F/255.0F, 1.0F);
		    }
	    }
	    else
	    {
	    	if(stargate.currentSymbol != 0)
	    		this.symbols_36.getChild("symbol" + stargate.currentSymbol).render(stack, symbol, 255, combinedOverlay, 42.0F/255.0F, 201.0F/255.0F, 207.0F/255.0F, 1.0F);
	    }*/
	    
	    

		VertexConsumer ring_texture = source.getBuffer(RenderType.entitySolid(EMPTY));
	    for(int i = 0; i < 36; i++)
	    {
	    	symbols_36.getChild("symbol" + i).render(stack, ring_texture, combinedLight, combinedOverlay);
	    }
	    
	    if(stargate.addressBuffer.length > 0)
	    	this.symbols_36.getChild("symbol" + stargate.currentSymbol).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbols(stargate).texture(stargate.addressBuffer[stargate.addressBuffer.length - 1]))), 255, combinedOverlay, 42.0F/255.0F, 201.0F/255.0F, 207.0F/255.0F, 1.0F);
	    for(int i = 0; i < stargate.inputAddress.length; i++)
	    {
	    	this.symbols_36.getChild("symbol" + stargate.getLitSymbol(i + 1)).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbols(stargate).texture(stargate.inputAddress[i]))), 255, combinedOverlay, 42.0F/255.0F, 201.0F/255.0F, 207.0F/255.0F, 1.0F);
	    }
	    
	    
	    
	    
		
	    VertexConsumer ring = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
	    this.ring.render(stack, ring, combinedLight, combinedOverlay);
		
	    this.dividers_36.render(stack, ring, combinedLight, combinedOverlay);
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
}
