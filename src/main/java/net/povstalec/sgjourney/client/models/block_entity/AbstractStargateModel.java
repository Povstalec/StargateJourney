package net.povstalec.sgjourney.client.models.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;

public abstract class AbstractStargateModel<StargateEntity extends AbstractStargateEntity<?>, Variant extends ClientStargateVariant>
{
	protected static final float DEFAULT_RADIUS = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	protected static final float DEFAULT_RING_HEIGHT = 1F;
	protected static final float STARGATE_RING_SHRINK = 0.001F;
	
	protected static final float DEFAULT_ANGLE = 360F / DEFAULT_SIDES;
	protected static final float NUMBER_OF_CHEVRONS = 9;
	protected static final float CHEVRON_ANGLE = 360F / 9;
	
	protected static final int MAX_LIGHT = 15728880;
	
	protected static final int DEFAULT_TEXTURE_SIZE = 64;
	
	public static final String EMPTY = StargateJourney.EMPTY;
	
	protected final short numberOfSymbols;
	
	public AbstractStargateModel(short numberOfSymbols)
	{
		this.numberOfSymbols = numberOfSymbols;
	}
	
	@Nullable
	protected ClientPointOfOrigin getPointOfOrigin(StargateEntity stargate, Variant stargateVariant)
	{
		if(stargateVariant.symbols().permanentPointOfOrigin().isPresent())
			return ClientPointOfOrigin.getPointOfOrigin(stargateVariant.symbols().permanentPointOfOrigin().get());
		else
			return ClientPointOfOrigin.getPointOfOrigin(stargate.symbolInfo().pointOfOrigin());
	}
	
	@Nullable
	protected ClientSymbols getSymbols(StargateEntity stargate, Variant stargateVariant)
	{
		if(stargateVariant.symbols().permanentSymbols().isPresent())
			return ClientSymbols.getSymbols(stargateVariant.symbols().permanentSymbols().get());
		else
			return ClientSymbols.getSymbols(stargate.symbolInfo().symbols());
	}
	
	//============================================================================================
	//******************************************Rendering*****************************************
	//============================================================================================
	
	/**
	 * Renders the Stargate. By default (no methods are overridden), the resulting rendered Stargate will be a generic model (a mix between the Milky Way and Pegasus Stargate)
	 * @param stargate Stargate Entity being rendered
	 * @param partialTick Partial Tick
	 * @param stack Pose Stack
	 * @param source Multi Buffer Source
	 * @param combinedLight Combined Light
	 * @param combinedOverlay Combined Overlay
	 */
	public void renderStargate(StargateEntity stargate, Variant stargateVariant, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(stargateVariant.texture()));
		this.renderRing(stargate, stargateVariant, partialTick, stack, consumer, source, combinedLight, combinedOverlay);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	}

	public abstract void renderRing(StargateEntity stargate, Variant stargateVariant, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay);
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================
	
	protected boolean isPrimaryChevronRaised(StargateEntity stargate, Variant stargateVariant)
	{
		return false;
	}
	
	protected boolean isPrimaryChevronLowered(StargateEntity stargate, Variant stargateVariant)
	{
		return false;
	}
	
	protected boolean isPrimaryChevronEngaged(StargateEntity stargate, Variant stargateVariant)
	{
		return stargate.getAddress().hasPointOfOriginOrMaxLength();
	}
	
	protected boolean isChevronRaised(StargateEntity stargate, Variant stargateVariant, int chevronNumber)
	{
		return false;
	}
	
	protected boolean isChevronLowered(StargateEntity stargate, Variant stargateVariant, int chevronNumber)
	{
		return false;
	}
	
	protected boolean isChevronEngaged(StargateEntity stargate, Variant stargateVariant, int chevronNumber)
	{
		return stargate.chevronsRendered() >= chevronNumber;
	}

	protected abstract void renderPrimaryChevron(StargateEntity stargate, Variant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, boolean chevronEngaged);
	
	protected abstract void renderChevron(StargateEntity stargate, Variant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged);
	
	protected void renderChevrons(StargateEntity stargate, Variant stargateVariant, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer;
		
		if(StargateJourney.isOculusLoaded())
		{
			// Renders lit up parts of Chevrons
			consumer = source.getBuffer(SGJourneyRenderTypes.engagedChevron(stargateVariant.getOverlayTexture(stargate.isConnected())));
			
			if(isPrimaryChevronEngaged(stargate, stargateVariant))
				renderPrimaryChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, true);
			for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
			{
				if(isChevronEngaged(stargate, stargateVariant, chevronNumber))
					renderChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, chevronNumber, true);
			}
		}
		// Renders Chevrons
		consumer = source.getBuffer(SGJourneyRenderTypes.chevron(stargateVariant.texture()));
				
		renderPrimaryChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, false);
		for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
		{
			renderChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, chevronNumber, false);
		}
		
		if(!StargateJourney.isOculusLoaded())
		{
			// Renders lit up parts of Chevrons
			consumer = source.getBuffer(SGJourneyRenderTypes.engagedChevron(stargateVariant.getOverlayTexture(stargate.isConnected())));
			
			if(isPrimaryChevronEngaged(stargate, stargateVariant))
				renderPrimaryChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, true);
			for(int chevronNumber = 1; chevronNumber < NUMBER_OF_CHEVRONS; chevronNumber++)
			{
				if(isChevronEngaged(stargate, stargateVariant, chevronNumber))
					renderChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, chevronNumber, true);
			}
		}
	}

	//============================================================================================
	//******************************************Symbols*******************************************
	//============================================================================================
	
	protected boolean symbolsGlow(StargateEntity stargate, Variant stargateVariant, boolean isEngaged)
	{
		if(isEngaged)
			return stargate.isConnected() ? stargateVariant.symbols().engagedSymbolsGlow() : stargateVariant.symbols().encodedSymbolsGlow();
		else
			return stargateVariant.symbols().symbolsGlow();
	}
	
	protected ColorUtil.RGBA getSymbolColor(StargateEntity stargate, Variant stargateVariant, StargateInfo.ChevronSymbolState state, boolean incoming)
	{
		if(state == StargateInfo.ChevronSymbolState.OFF)
			return stargateVariant.symbols().symbolColor();
		else if(state == StargateInfo.ChevronSymbolState.ENGAGED)
			return stargateVariant.symbols().engagedSymbolColor(); // TODO Split for incoming
		else
			return stargateVariant.symbols().encodedSymbolColor(); // TODO Split for incoming
	}
	
	protected ColorUtil.RGBA getSymbolColor(StargateEntity stargate, Variant stargateVariant, boolean isEngaged)
	{
		if(isEngaged)
			return stargate.isConnected() ? stargateVariant.symbols().engagedSymbolColor() : stargateVariant.symbols().encodedSymbolColor();
		else
			return stargateVariant.symbols().symbolColor();
	}
}
