package net.povstalec.sgjourney.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class PegasusStargateModel extends GenericStargateModel
{
	private static final float DEFAULT_G = 100F / 255;
	private static final float DEFAULT_B = 200F / 255;
	
	private static final float ENGAGED_G = 200F / 255;
	private static final float ENGAGED_B = 255F / 255;
	
	private static final int symbolCount = 36;
	private int currentSymbol = 0;
	
	protected ResourceLocation alternateStargateTexture;
	protected ResourceLocation alternateEngagedTexture;
	
	public PegasusStargateModel()
	{
		super("pegasus", 36, 0, DEFAULT_G, DEFAULT_B);
		
		this.alternateStargateTexture = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/" + stargateName + "/" + stargateName +"_stargate_alternate.png");
		this.alternateEngagedTexture = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/" + stargateName + "/" + stargateName +"_stargate_alternate_engaged.png");
	}
	
	public void renderStargate(PegasusStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture()));
		this.renderOuterRing(stack, consumer, source, combinedLight);

		this.renderSymbolRing(stargate, stack, consumer, source, combinedLight, 0);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	public void setCurrentSymbol(int currentSymbol)
	{
		this.currentSymbol = currentSymbol;
	}
	
	@Override
	protected ResourceLocation getStargateTexture()
	{
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateStargateTexture : this.stargateTexture;
	}

	@Override
	protected ResourceLocation getEngagedTexture()
	{
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateEngagedTexture : this.engagedTexture;
	}

	protected void renderPrimaryChevron(PegasusStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean chevronEngaged, boolean isRaised)
	{
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.translate(0, 3.5F - 2.5F/16, 0);
		
		renderChevronLight(stack, consumer, source, light, isRaised);
		renderOuterChevronFront(stack, consumer, source, light, isRaised);
		renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();
	}
	
	protected boolean isPrimaryChevronEngaged(PegasusStargateEntity stargate)
	{
		if(stargate.isConnected())
			return stargate.isDialingOut() || stargate.getKawooshTickCount() > 0;
		
		return false;
	}
	
	protected void renderChevrons(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		// Renders Chevrons
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.pegasusChevron(getStargateTexture()));
				
		renderPrimaryChevron(stargate, stack, consumer, source, combinedLight, false, false);
		for(int chevronNumber = 1; chevronNumber < 9; chevronNumber++)
		{
			renderChevron(stargate, stack, consumer, source, combinedLight, chevronNumber, false, false, false);
		}
		
		// Renders lit up parts of Chevrons
		consumer = source.getBuffer(SGJourneyRenderTypes.engagedChevron(getEngagedTexture()));
		
		if(isPrimaryChevronEngaged(stargate))
			renderPrimaryChevron(stargate, stack, consumer, source, combinedLight, true, false);
		for(int chevronNumber = 1; chevronNumber < 9; chevronNumber++)
		{
			boolean isChevronEngaged = stargate.chevronsRendered() >= chevronNumber;
			if(isChevronEngaged)
				renderChevron(stargate, stack, consumer, source, combinedLight, chevronNumber, isChevronEngaged, false, false);
		}
	}
	
	protected void renderSpinningSymbol(PegasusStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		if(!stargate.isConnected() && stargate.symbolBuffer < stargate.addressBuffer.length)
	    {
			for(int i = 0; i < stargate.getAddress().length; i++)
	    	{
	    		// This makes sure the Symbol doesn't render over other existing symbols
	    		if(stargate.getChevronPosition(i + 1) == this.currentSymbol)
	    			return;
	    	}

    		int renderedSymbol = stargate.addressBuffer[stargate.symbolBuffer];
			renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, this.currentSymbol, renderedSymbol, 0, symbolR, ENGAGED_G, ENGAGED_B);
	    }
		
	}
	
	@Override
	protected void renderSymbols(AbstractStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		if((stargate.isDialingOut() && stargate.isConnected()) || (((PegasusStargateEntity) stargate).addressBuffer.length > 0 && !stargate.isConnected()))
		{
			// Spinning Symbol
			renderSpinningSymbol(((PegasusStargateEntity) stargate), stack, consumer, source, combinedLight, rotation);
			
			// Point of Origin when Stargate is connected
			if(stargate.isConnected())
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, 0, 0, 0, symbolR, ENGAGED_G, ENGAGED_B);
			
			// Locked Symbols
			for(int i = 0; i < stargate.getAddress().length; i++)
			{
				int symbolNumber = ((PegasusStargateEntity) stargate).getChevronPosition(i + 1);
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, symbolNumber, stargate.getAddress()[i], 0, symbolR, ENGAGED_G, ENGAGED_B);
			}
		}
		else
		{
			float green = DEFAULT_G;
			float blue = DEFAULT_B;
			int symbolNumber = symbolCount;
			
			if(stargate.isConnected())
			{
				green = ENGAGED_G;
				blue = ENGAGED_B;
				symbolNumber = ((PegasusStargateEntity) stargate).currentSymbol < symbolCount ? ((PegasusStargateEntity) stargate).currentSymbol : symbolNumber;
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, 0, 0, 0, symbolR, ENGAGED_G, ENGAGED_B);
			}
			
			// Idle Symbols
			for(int i = 0; i < symbolNumber; i++)
			{
				int renderedSymbol = (stargate.isConnected() ? i + 1 : i) % symbolCount;
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, renderedSymbol, renderedSymbol, 0, symbolR, green, blue);
			}
		}
	}
}
