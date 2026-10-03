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
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;

public class AndromedaStargateModel extends GenericStargateModel<AndromedaStargateEntity, AndromedaStargateVariant>
{
	protected int currentSymbol = 0;
	
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

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay, StargateJourney.isIrisLoaded());
	}
	
	public void setCurrentSymbol(int currentSymbol)
	{
		this.currentSymbol = currentSymbol;
	}
	
	@Override
	protected void renderSymbols(AndromedaStargateEntity stargate, AndromedaStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		ClientPointOfOrigin pointOfOrigin = getPointOfOrigin(stargate, stargateVariant);
		
		if(pointOfOrigin != null)
		{
			boolean isEngaged = (!stargate.getAddressBuffer().isEmpty() && currentSymbol == 0) || stargate.getAddress().hasPointOfOrigin();
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, isEngaged) ? MAX_LIGHT : combinedLight, 0,
				ClientPointOfOrigin.getSprite(pointOfOrigin), rotation, getSymbolColor(stargate, stargateVariant, isEngaged));
		}
		
		ClientSymbols symbols = getSymbols(stargate, stargateVariant);
		
		if(symbols == null)
			return;
		
		for(int symbol = 1; symbol < numberOfSymbols; symbol++)
		{
			boolean isEngaged = (!stargate.isDialingOut() && stargate.isConnected()) || stargate.getAddress().containsSymbol(symbol) || currentSymbol == symbol;
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, isEngaged) ? MAX_LIGHT : combinedLight, symbol,
				ClientSymbols.getSprite(symbols, symbol), rotation, getSymbolColor(stargate, stargateVariant, isEngaged));
		}
	}
}
