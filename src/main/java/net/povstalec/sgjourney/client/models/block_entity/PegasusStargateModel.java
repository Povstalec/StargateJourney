package net.povstalec.sgjourney.client.models.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.PegasusStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

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
										TextureAtlasSprite sprite, float rotation)
	{
		if(!stargate.isConnected() && stargate.symbolBuffer < stargate.addressBuffer.getLength())
	    {
			for(int i = 0; i < stargate.getAddress().regularSymbolCount(); i++)
	    	{
	    		// This makes sure the Symbol doesn't render over other existing symbols
	    		if(stargate.getChevronPosition(i + 1) == this.currentSymbol)
	    			return;
	    	}

			renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, this.currentSymbol, sprite, rotation, getSymbolColor(stargate, stargateVariant, true));
	    }
		
	}
	
	protected void renderIdleSymbols(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source,
									 Symbols symbols, ColorUtil.RGBA symbolColor, int numberOfSymbols)
	{
		// Idle Symbols
		for(int symbol = 1; symbol < numberOfSymbols && symbol < this.numberOfSymbols; symbol++)
		{
			renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, symbol,
					ClientUtil.getSymbolSprite(symbols, symbol), 0, symbolColor);
		}
	}
	
	@Override
	protected void renderSymbols(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		int currentSymbol = stargate.addressBuffer.symbolAt(stargate.symbolBuffer);
		
		PointOfOrigin pointOfOrigin = getPointOfOrigin(stargate, stargateVariant);
		
		if(pointOfOrigin != null)
		{
			// Point of Origin
			if(stargate.getAddress().hasPointOfOrigin()) // Point of Origin is encoded
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, ClientUtil.getPointOfOriginSprite(pointOfOrigin), rotation, getSymbolColor(stargate, stargateVariant, true));
			else if(stargate.addressBuffer.getLength() > 0 && !stargate.isConnected() && currentSymbol == 0) // Point of Origin is spinning around the ring
				renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, ClientUtil.getPointOfOriginSprite(pointOfOrigin), rotation);
			else if(!stargate.isConnected() && stargate.addressBuffer.getLength() == 0) // Stargate is in its idle state
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, 0, ClientUtil.getPointOfOriginSprite(pointOfOrigin), rotation, getSymbolColor(stargate, stargateVariant, false));
		}
		
		Symbols symbols = getSymbols(stargate, stargateVariant);
		
		if(symbols == null)
			return;
		
		// When a Stargate is dialing out or connected after dialing out
		if((stargate.isDialingOut() && stargate.isConnected()) || (stargate.addressBuffer.getLength() > 0 && !stargate.isConnected()))
		{
			// Spinning Symbol
			if(currentSymbol > 0)
				renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, combinedLight, ClientUtil.getSymbolSprite(symbols, currentSymbol), rotation);
			
			// Locked Symbols
			for(int i = 0; i < stargate.getAddress().regularSymbolCount(); i++)
			{
				int symbolNumber = stargate.getChevronPosition(i + 1);
				renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, symbolNumber,
						ClientUtil.getSymbolSprite(symbols, stargate.getAddress().getArray()[i]), rotation, getSymbolColor(stargate, stargateVariant, true));
			}
		}
		else
			renderIdleSymbols(stargate, stargateVariant, stack, consumer, source, symbols, getSymbolColor(stargate, stargateVariant, stargate.isConnected()), stargate.isConnected() ? stargate.getCurrentSymbol() : 36);
	}
}
