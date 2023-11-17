package net.povstalec.sgjourney.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class PegasusStargateModel extends GenericStargateModel<PegasusStargateEntity>
{
	private static final float R = 0;
	private static final float DEFAULT_G = 100F / 255;
	private static final float DEFAULT_B = 200F / 255;
	
	private static final float ENGAGED_G = 200F / 255;
	private static final float ENGAGED_B = 255F / 255;
	
	protected int currentSymbol = 0;
	
	protected final ResourceLocation alternateStargateTexture;
	protected final ResourceLocation alternateEngagedTexture;
	
	public PegasusStargateModel()
	{
		super(new ResourceLocation(StargateJourney.MODID, "pegasus"), 36, R, DEFAULT_G, DEFAULT_B);
		
		this.alternateStargateTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate.png");
		this.alternateEngagedTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate_engaged.png");
	}
	
	public void renderStargate(PegasusStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture(stargate)));
		this.renderOuterRing(stack, consumer, source, combinedLight);

		this.renderSymbolRing(stargate, stack, consumer, source, combinedLight, 0);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	public void setCurrentSymbol(int currentSymbol)
	{
		this.currentSymbol = currentSymbol;
	}
	
	@Override
	protected ResourceLocation getStargateTexture(PegasusStargateEntity stargate)
	{
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateStargateTexture : this.stargateTexture;
	}

	@Override
	protected ResourceLocation getEngagedTexture(PegasusStargateEntity stargate)
	{
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateEngagedTexture : this.engagedTexture;
	}
	
	protected void renderSpinningSymbol(PegasusStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		if(!stargate.isConnected() && stargate.symbolBuffer < stargate.addressBuffer.getLength())
	    {
			for(int i = 0; i < stargate.getAddress().getLength(); i++)
	    	{
	    		// This makes sure the Symbol doesn't render over other existing symbols
	    		if(stargate.getChevronPosition(i + 1) == this.currentSymbol)
	    			return;
	    	}

    		int renderedSymbol = stargate.addressBuffer.getSymbol(stargate.symbolBuffer);
			renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, this.currentSymbol, renderedSymbol, 0, R, ENGAGED_G, ENGAGED_B);
	    }
		
	}
	
	@Override
	protected void renderSymbols(PegasusStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		if((stargate.isDialingOut() && stargate.isConnected()) || (stargate.addressBuffer.getLength() > 0 && !stargate.isConnected()))
		{
			// Spinning Symbol
			renderSpinningSymbol(stargate, stack, consumer, source, combinedLight, rotation);
			
			// Point of Origin when Stargate is connected
			if(stargate.isConnected())
			{
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, 0, 0, 0, R, ENGAGED_G, ENGAGED_B);
			}
			
			// Locked Symbols
			for(int i = 0; i < stargate.getAddress().getLength(); i++)
			{
				int symbolNumber = stargate.getChevronPosition(i + 1);
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, symbolNumber, stargate.getAddress().toArray()[i], 0, R, ENGAGED_G, ENGAGED_B);
			}
		}
		else
		{
			float green = DEFAULT_G;
			float blue = DEFAULT_B;
			int symbolNumber = symbolSides;
			
			if(stargate.isConnected())
			{
				green = ENGAGED_G;
				blue = ENGAGED_B;
				symbolNumber = stargate.currentSymbol < symbolSides ? stargate.currentSymbol : symbolNumber;
				if(stargate.getKawooshTickCount() > 0)
					renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, 0, 0, 0, R, ENGAGED_G, ENGAGED_B);
			}
			
			// Idle Symbols
			for(int i = 0; i < symbolNumber; i++)
			{
				int renderedSymbol = (stargate.isConnected() ? i + 1 : i) % symbolSides;
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, renderedSymbol, renderedSymbol, 0, symbolR, green, blue);
			}
		}
	}
}
