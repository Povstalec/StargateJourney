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
import net.povstalec.sgjourney.common.stargate.Symbols;

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
		super(new ResourceLocation(StargateJourney.MODID, "pegasus"), (short) 36, new Stargate.RGBA(RED, GREEN, BLUE, 255));
		
		this.alternateStargateTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate.png");
		this.alternateEngagedTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate_engaged.png");
		
		this.engagedSymbolColor = new Stargate.RGBA(RED, ENGAGED_GREEN, ENGAGED_BLUE, 255);
	}

	@Override
	public void renderStargate(PegasusStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		Optional<StargateVariant> stargateVariant = getStargateVariant(stargate);
		
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture(stargate, stargateVariant)));
		this.renderOuterRing(stack, consumer, source, combinedLight);

		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, 0);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	}
	
	@Override
	protected boolean useMovieStargateModel(PegasusStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent() && stargateVariant.get().useAlternateModel().isPresent())
			return stargateVariant.get().useAlternateModel().get();
		
		return false;
	}
	
	public void setCurrentSymbol(int currentSymbol)
	{
		this.currentSymbol = currentSymbol;
	}

	@Override
	protected Stargate.RGBA getSymbolColor(PegasusStargateEntity stargate, Optional<StargateVariant> stargateVariant, boolean isEngaged)
	{
		if(stargateVariant.isPresent())
		{
			if(isEngaged)
			{
				if(!stargate.isConnected() && stargateVariant.get().getEncodedSymbolRGBA().isPresent())
					return stargateVariant.get().getEncodedSymbolRGBA().get();
				
				else if(stargateVariant.get().getEngagedSymbolRGBA().isPresent())
					return stargateVariant.get().getEngagedSymbolRGBA().get();
			}
			
			else if(!isEngaged && stargateVariant.get().getSymbolRGBA().isPresent())
				return stargateVariant.get().getSymbolRGBA().get();
		}
		
		return isEngaged ? this.engagedSymbolColor : this.symbolColor;
	}
	
	@Override
	protected ResourceLocation getStargateTexture(PegasusStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
			return stargateVariant.get().getTexture();
		
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateStargateTexture : this.stargateTexture;
	}

	@Override
	protected ResourceLocation getEngagedTexture(PegasusStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
			return stargateVariant.get().getEngagedTexture();
		
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ?
				this.alternateEngagedTexture : this.engagedTexture;
	}
	
	protected void renderSpinningSymbol(PegasusStargateEntity stargate, Optional<StargateVariant> stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, 
			float symbolOffset, int textureXSize, float rotation)
	{
		if(!stargate.isConnected() && stargate.symbolBuffer < stargate.addressBuffer.getLength())
	    {
			for(int i = 0; i < stargate.getAddress().getLength(); i++)
	    	{
	    		// This makes sure the Symbol doesn't render over other existing symbols
	    		if(stargate.getChevronPosition(i + 1) == this.currentSymbol)
	    			return;
	    	}

			renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, this.currentSymbol, symbolOffset, textureXSize, 0, getSymbolColor(stargate, stargateVariant, true));
	    }
		
	}
	
	@Override
	protected void renderSymbols(PegasusStargateEntity stargate, Optional<StargateVariant> stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		int currentSymbol = stargate.addressBuffer.getSymbol(stargate.symbolBuffer);
		
		// Point of Origin
		if(stargate.isDialingOut() && stargate.isConnected() || stargate.isConnected() && stargate.getKawooshTickCount() > 0)
		{
			consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(stargate, stargateVariant)));
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, 0.5F, 1, rotation, getSymbolColor(stargate, stargateVariant, true));
		}
		else if(stargate.addressBuffer.getLength() > 0 && !stargate.isConnected() && currentSymbol == 0)
		{
			consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(stargate, stargateVariant)));

			renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0.5F, 1, rotation);
		}
		else if(!stargate.isConnected() && stargate.addressBuffer.getLength() == 0)
		{
			consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(stargate, stargateVariant)));
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, 0.5F, 1, rotation, getSymbolColor(stargate, stargateVariant, false));
		}
		
		Optional<Symbols> symbols = getSymbols(stargate, stargateVariant);
		consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbolTexture(symbols)));
		
		if(symbols.isEmpty())
			return;
		
		// When a Stargate is dialing out or connected after dialing out
		if((stargate.isDialingOut() && stargate.isConnected()) || (stargate.addressBuffer.getLength() > 0 && !stargate.isConnected()))
		{
			// Spinning Symbol
			if(currentSymbol > 0)
				renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, combinedLight, symbols.get().getTextureOffset(currentSymbol), symbols.get().getSize(), rotation);
			
			// Point of Origin when Stargate is connected
			//if(stargate.isConnected())
			//	renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, 0, 0, getSymbolColor(stargate, stargateVariant, true));
			
			// Locked Symbols
			for(int i = 0; i < stargate.getAddress().getLength(); i++)
			{
				int symbolNumber = stargate.getChevronPosition(i + 1);
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, symbolNumber, 
						symbols.get().getTextureOffset(stargate.getAddress().toArray()[i]), symbols.get().getSize(), 0, getSymbolColor(stargate, stargateVariant, true));
			}
		}
		else
		{
			Stargate.RGBA symbolColor = getSymbolColor(stargate, stargateVariant, false);
			int symbolNumber = this.numberOfSymbols;
			
			if(stargate.isConnected())
			{
				symbolColor = getSymbolColor(stargate, stargateVariant, true);
				symbolNumber = stargate.currentSymbol < this.numberOfSymbols ? stargate.currentSymbol : symbolNumber;
				//if(stargate.getKawooshTickCount() > 0)
				//	renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, 0, 0, symbolColor);
			}
			
			// Idle Symbols
			int startFrom = stargate.isConnected() ? 0 : 1;
			for(int i = startFrom; i < symbolNumber; i++)
			{
				int renderedSymbol = (stargate.isConnected() ? i + 1 : i) % this.numberOfSymbols;
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, renderedSymbol, 
						symbols.get().getTextureOffset(renderedSymbol), symbols.get().getSize(), 0, symbolColor);
			}
		}
	}
}
