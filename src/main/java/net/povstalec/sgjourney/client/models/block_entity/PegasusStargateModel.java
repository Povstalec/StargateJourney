package net.povstalec.sgjourney.client.models.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.PegasusStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;

public class PegasusStargateModel extends GenericStargateModel<PegasusStargateEntity, PegasusStargateVariant>
{
	public PegasusStargateModel()
	{
		super((short) 36);
	}
	
	@Override
	public PegasusStargateVariant getClientVariant(PegasusStargateEntity stargate)
	{
		StargateVariant stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant != null)
		{
			if(stargateVariant.isFound())
				return ClientStargateVariants.getPegasusStargateVariant(stargateVariant.clientVariant());
			else if(!stargateVariant.isMissing())
				stargateVariant.handleLocation(ClientStargateVariants.hasPegasusStargateVariant(stargateVariant.clientVariant()));
		}
		
		return ClientStargateVariants.getPegasusStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void renderStargate(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(stargateVariant.texture()));
		this.renderOuterRing(stack, consumer, source, combinedLight);

		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, 0);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay, StargateJourney.isOculusLoaded());
	}
	
	@Override
	public StargateInfo.SymbolState symbolState(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, int symbol)
	{
		// This overload of the method assumes that we're only getting the state of symbols encoded in the Address
		// (except possibly for the case of Point of Origin)
		
		if(stargate.isConnected())
		{
			if(stargate.isDialingOut())
				return StargateInfo.SymbolState.ENGAGED;
			else
			{
				if(stargate.isWormholeEstablished())
					return StargateInfo.SymbolState.ENGAGED_INCOMING;
				else
					return StargateInfo.SymbolState.ENCODED_INCOMING;
			}
		}
		
		return StargateInfo.SymbolState.ENCODED;
	}
	
	protected void renderSpinningSymbol(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight,
										TextureAtlasSprite sprite, float rotation)
	{
		if(!stargate.isConnected() && stargate.getSymbolBuffer() < stargate.getAddressBuffer().getLength())
	    {
			for(int i = 0; i < stargate.getAddress().regularSymbolCount(); i++)
			{
				// This makes sure the Symbol doesn't render over other existing symbols
				if(stargate.getChevronPosition(i + 1) == stargate.getCurrentSymbol())
					return;
			}
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, StargateInfo.SymbolState.ENCODING) ? MAX_LIGHT : combinedLight,
				stargate.getCurrentSymbol(), sprite, rotation, getSymbolColor(stargate, stargateVariant, StargateInfo.SymbolState.ENCODING));
	    }
		
	}
	
	protected void renderIdleSymbols(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source,
									 ClientSymbols symbols, ColorUtil.RGBA symbolColor, float rotation, int numberOfSymbols)
	{
		// Idle Symbols
		for(int symbol = 1; symbol < numberOfSymbols && symbol < this.numberOfSymbols; symbol++)
		{
			renderSymbol(stargate, stargateVariant, stack, consumer, source, MAX_LIGHT, symbol,
					ClientSymbols.getSprite(symbols, symbol), rotation, symbolColor);
		}
	}
	
	@Override
	protected void renderSymbols(PegasusStargateEntity stargate, PegasusStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		int currentSymbol = stargate.getEncodedSymbols().symbolAt(stargate.getSymbolBuffer());
		
		ClientPointOfOrigin pointOfOrigin = getPointOfOrigin(stargate, stargateVariant);
		
		if(pointOfOrigin != null)
		{
			// Point of Origin
			if(stargate.getAddress().hasPointOfOrigin()) // Point of Origin is encoded
			{
				StargateInfo.SymbolState symbolState = symbolState(stargate, stargateVariant, 0);
				
				renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, symbolState) ? MAX_LIGHT : combinedLight,
					0, ClientPointOfOrigin.getSprite(pointOfOrigin), rotation, getSymbolColor(stargate, stargateVariant, symbolState));
			}
			else if(stargate.getAddressBuffer().getLength() > 0 && !stargate.isConnected() && currentSymbol == 0) // Point of Origin is spinning around the ring
				renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, combinedLight,
					ClientPointOfOrigin.getSprite(pointOfOrigin), rotation);
			else if(!stargate.isConnected() && stargate.getAddressBuffer().getLength() == 0) // Stargate is in its idle state
				renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, StargateInfo.SymbolState.IDLE) ? MAX_LIGHT : combinedLight,
					0, ClientPointOfOrigin.getSprite(pointOfOrigin), rotation, getSymbolColor(stargate, stargateVariant, StargateInfo.SymbolState.IDLE));
			else if(stargate.isConnected() && !stargate.isDialingOut() && stargate.isWormholeEstablished()) // Point of Origin for incoming connections that don't encode the Point of Origin
				renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, StargateInfo.SymbolState.ENGAGED_INCOMING) ? MAX_LIGHT : combinedLight,
					0, ClientPointOfOrigin.getSprite(pointOfOrigin), rotation, getSymbolColor(stargate, stargateVariant, StargateInfo.SymbolState.ENGAGED_INCOMING));
		}
		
		ClientSymbols symbols = getSymbols(stargate, stargateVariant);
		
		if(symbols == null)
			return;
		
		// When a Stargate is dialing out or connected after dialing out
		if((stargate.isDialingOut() && stargate.isConnected()) || (stargate.getAddressBuffer().getLength() > 0 && !stargate.isConnected()))
		{
			// Spinning Symbol
			if(currentSymbol > 0)
				renderSpinningSymbol(stargate, stargateVariant, stack, consumer, source, combinedLight, ClientSymbols.getSprite(symbols, currentSymbol), rotation);
			
			// Locked Symbols
			for(int i = 0; i < stargate.getAddress().regularSymbolCount(); i++)
			{
				int symbolPos = stargate.getChevronPosition(i + 1);
				int symbol = stargate.getEncodedSymbols().symbolAt(i);
				
				StargateInfo.SymbolState symbolState;
				
				// If the traveling symbol is over this symbol, render it with a different color
				if(stargate.isSymbolSpinning() && symbolPos == stargate.getCurrentSymbol())
					symbolState = StargateInfo.SymbolState.ENCODING;
				else
					symbolState = symbolState(stargate, stargateVariant, symbol);
				
				renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, symbolState) ? MAX_LIGHT : combinedLight,
					symbolPos, ClientSymbols.getSprite(symbols, symbol), rotation, getSymbolColor(stargate, stargateVariant, symbolState));
			}
		}
		else
		{
			// Render incoming idle symbols
			if(!stargate.isDialingOut() && stargate.isConnected())
			{
				StargateInfo.SymbolState symbolState = stargate.isWormholeEstablished() ?
					StargateInfo.SymbolState.ENGAGED_INCOMING : StargateInfo.SymbolState.ENCODED_INCOMING;
				
				renderIdleSymbols(stargate, stargateVariant, stack, consumer, source, symbols, getSymbolColor(stargate, stargateVariant, symbolState),
					rotation, stargate.isConnected() ? stargate.getCurrentSymbol() + 1 : numberOfSymbols);
			}
			else // Render idle symbols
				renderIdleSymbols(stargate, stargateVariant, stack, consumer, source, symbols, getSymbolColor(stargate, stargateVariant, StargateInfo.SymbolState.IDLE),
					rotation, stargate.isConnected() ? stargate.getCurrentSymbol() + 1 : numberOfSymbols);
		}
	}
}
