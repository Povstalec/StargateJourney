package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class PegasusStargateModel extends GenericStargateModel<PegasusStargateEntity>
{
	private static final int RED = 0;
	private static final int GREEN = 100;
	private static final int BLUE = 200;
	
	private static final int ENGAGED_GREEN = 200;
	private static final int ENGAGED_BLUE = 255;
	
	protected int currentSymbol = 0;
	
	protected final ResourceLocation alternateStargateTexture;
	protected final ResourceLocation alternateEngagedTexture;
	
	public PegasusStargateModel()
	{
		super(new ResourceLocation(StargateJourney.MODID, "pegasus"), 36, new Stargate.RGBA(RED, GREEN, BLUE, 255));
		
		this.alternateStargateTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate.png");
		this.alternateEngagedTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate_engaged.png");
		
		this.engagedSymbolColor = new Stargate.RGBA(RED, ENGAGED_GREEN, ENGAGED_BLUE, 255);
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
	protected Stargate.RGBA getSymbolColor(PegasusStargateEntity stargate, boolean isEngaged)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
		{
			if(!isEngaged && variant.get().getSymbolRGBA().isPresent())
				return variant.get().getSymbolRGBA().get();
			else if(isEngaged && variant.get().getEngagedSymbolRGBA().isPresent())
				return variant.get().getEngagedSymbolRGBA().get();
		}
		
		return isEngaged ? this.engagedSymbolColor : this.symbolColor;
	}
	
	@Override
	protected ResourceLocation getStargateTexture(PegasusStargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
			return variant.get().getTexture();
		
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateStargateTexture : this.stargateTexture;
	}

	@Override
	protected ResourceLocation getEngagedTexture(PegasusStargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
			return variant.get().getEngagedTexture();
		
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateEngagedTexture : this.engagedTexture;
	}
	
	protected void renderSpinningSymbol(PegasusStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		//System.out.println("Symbol " + stargate.symbolBuffer);
		System.out.println("Address Client " + stargate.addressBuffer.getLength());
		/*for(int i = 0; i < stargate.addressBuffer.getLength(); i++)
		{
			System.out.println(stargate.addressBuffer.toArray()[i]);
		}*/
		
		if(!stargate.isConnected() && stargate.symbolBuffer < stargate.addressBuffer.getLength())
	    {
			for(int i = 0; i < stargate.getAddress().getLength(); i++)
	    	{
	    		// This makes sure the Symbol doesn't render over other existing symbols
	    		if(stargate.getChevronPosition(i + 1) == this.currentSymbol)
	    			return;
	    	}

    		int renderedSymbol = stargate.addressBuffer.getSymbol(stargate.symbolBuffer);
			renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, this.currentSymbol, renderedSymbol, 0, getSymbolColor(stargate, true));
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
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, 0, 0, 0, getSymbolColor(stargate, true));
			
			// Locked Symbols
			for(int i = 0; i < stargate.getAddress().getLength(); i++)
			{
				int symbolNumber = stargate.getChevronPosition(i + 1);
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, symbolNumber, stargate.getAddress().toArray()[i], 0, getSymbolColor(stargate, true));
			}
		}
		else
		{
			Stargate.RGBA symbolColor = getSymbolColor(stargate, false);
			int symbolNumber = symbolSides;
			
			if(stargate.isConnected())
			{
				symbolColor = getSymbolColor(stargate, true);
				symbolNumber = stargate.currentSymbol < symbolSides ? stargate.currentSymbol : symbolNumber;
				if(stargate.getKawooshTickCount() > 0)
					renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, 0, 0, 0, symbolColor);
			}
			
			// Idle Symbols
			for(int i = 0; i < symbolNumber; i++)
			{
				int renderedSymbol = (stargate.isConnected() ? i + 1 : i) % symbolSides;
				renderSymbol(stargate, stack, consumer, source, MAX_LIGHT, renderedSymbol, renderedSymbol, 0, symbolColor);
			}
		}
	}
}
