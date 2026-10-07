package net.povstalec.sgjourney.client.models.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.AndromedaStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.stargate.AndromedaStargateEntity;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;

public class AndromedaStargateModel extends GenericStargateModel<AndromedaStargateEntity, AndromedaStargateVariant>
{
	public AndromedaStargateModel()
	{
		super((short) 39);
	}
	
	@Override
	public AndromedaStargateVariant getClientVariant(AndromedaStargateEntity stargate)
	{
		StargateVariant stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant != null)
		{
			if(stargateVariant.isFound())
				return ClientStargateVariants.getAndromedaStargateVariant(stargateVariant.clientVariant());
			else if(!stargateVariant.isMissing())
				stargateVariant.handleLocation(ClientStargateVariants.hasAndromedaStargateVariant(stargateVariant.clientVariant()));
		}
		
		return ClientStargateVariants.getAndromedaStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void renderStargate(AndromedaStargateEntity stargate, AndromedaStargateVariant stargateVariant, float partialTick, PoseStack stack, MultiBufferSource source,
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(stargateVariant.texture()));
		this.renderOuterRing(stack, consumer, source, combinedLight);

		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, 0);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay, StargateJourney.isOculusLoaded());
	}
	
	@Override
	public StargateInfo.SymbolState symbolState(AndromedaStargateEntity stargate, AndromedaStargateVariant stargateVariant, int symbol)
	{
		if(stargate.isConnected())
		{
			if(stargate.getAddress().containsSymbol(symbol))
			{
				if(stargate.isDialingOut())
					return StargateInfo.SymbolState.ENGAGED;
			}
			
			if(!stargate.isDialingOut())
				return stargate.isWormholeEstablished() ? StargateInfo.SymbolState.ENGAGED_INCOMING : StargateInfo.SymbolState.ENCODED_INCOMING;
		}
		else
		{
			if(stargate.isSymbolSpinning() && stargate.getCurrentSymbol() == symbol)
				return StargateInfo.SymbolState.ENCODING;
			
			if(stargate.getAddress().containsSymbol(symbol))
				return StargateInfo.SymbolState.ENCODED;
		}
		
		return StargateInfo.SymbolState.IDLE;
	}
	
	@Override
	protected void renderSymbols(AndromedaStargateEntity stargate, AndromedaStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		ClientPointOfOrigin pointOfOrigin = getPointOfOrigin(stargate, stargateVariant);
		
		if(pointOfOrigin != null)
		{
			StargateInfo.SymbolState symbolState = symbolState(stargate, stargateVariant, 0);
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, symbolState) ? MAX_LIGHT : combinedLight, 0,
				ClientPointOfOrigin.getSprite(pointOfOrigin), rotation, getSymbolColor(stargate, stargateVariant, symbolState));
		}
		
		ClientSymbols symbols = getSymbols(stargate, stargateVariant);
		
		if(symbols == null)
			return;
		
		for(int symbol = 1; symbol < numberOfSymbols; symbol++)
		{
			StargateInfo.SymbolState symbolState = symbolState(stargate, stargateVariant, symbol);
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, symbolState) ? MAX_LIGHT : combinedLight, symbol,
				ClientSymbols.getSprite(symbols, symbol), rotation, getSymbolColor(stargate, stargateVariant, symbolState));
		}
	}
}
