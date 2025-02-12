package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.PegasusStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class PegasusStargateModel extends GenericStargateModel<PegasusStargateEntity, PegasusStargateVariant>
{
	private static final int RED = 0;
	private static final int GREEN = 100;
	private static final int BLUE = 200;
	
	private static final int ENGAGED_GREEN = 200;
	private static final int ENGAGED_BLUE = 255;
	
	protected int currentSymbol = 0;
	
	public PegasusStargateModel()
	{
		super((short) 36);
	}

	@Override
	public void renderStargate(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(stargateVariant.texture()));
		this.renderOuterRing(stack, consumer, source, combinedLight);

		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, 0);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	}
	
	public void setCurrentSymbol(int currentSymbol)
	{
		this.currentSymbol = currentSymbol;
	}
	
	protected void renderSpinningSymbol(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, 
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
	protected void renderSymbols(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		int currentSymbol = stargate.addressBuffer.getSymbol(stargate.symbolBuffer);
		
		PointOfOrigin pointOfOrigin = getPointOfOrigin(stargate, stargateVariant);
		
		if(pointOfOrigin != null)
		{
			// Point of Origin
			if(stargate.isDialingOut() && stargate.isConnected() || stargate.isConnected() && stargate.getKawooshTickCount() > 0)
			{
				consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(pointOfOrigin)));
				
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, 0.5F, 1, rotation, getSymbolColor(stargate, stargateVariant, true));
			}
			else if(stargate.addressBuffer.getLength() > 0 && !stargate.isConnected() && currentSymbol == 0)
			{
				consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(pointOfOrigin)));

				renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0.5F, 1, rotation);
			}
			else if(!stargate.isConnected() && stargate.addressBuffer.getLength() == 0)
			{
				consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(pointOfOrigin)));
				
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, 0.5F, 1, rotation, getSymbolColor(stargate, stargateVariant, false));
			}
		}
		
		Symbols symbols = getSymbols(stargate, stargateVariant);
		
		if(symbols == null)
			return;
		consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbolTexture(symbols)));
		
		// When a Stargate is dialing out or connected after dialing out
		if((stargate.isDialingOut() && stargate.isConnected()) || (stargate.addressBuffer.getLength() > 0 && !stargate.isConnected()))
		{
			// Spinning Symbol
			if(currentSymbol > 0)
				renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, combinedLight, symbols.getTextureOffset(currentSymbol), symbols.getSize(), rotation);
			
			// Point of Origin when Stargate is connected
			//if(stargate.isConnected())
			//	renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, 0, 0, getSymbolColor(stargate, stargateVariant, true));
			
			// Locked Symbols
			for(int i = 0; i < stargate.getAddress().getLength(); i++)
			{
				int symbolNumber = stargate.getChevronPosition(i + 1);
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, symbolNumber, 
						symbols.getTextureOffset(stargate.getAddress().toArray()[i]), symbols.getSize(), 0, getSymbolColor(stargate, stargateVariant, true));
			}
		}
		else
		{
			ColorUtil.RGBA symbolColor = getSymbolColor(stargate, stargateVariant, false);
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
						symbols.getTextureOffset(renderedSymbol), symbols.getSize(), 0, symbolColor);
			}
		}
	}
}
