package net.povstalec.sgjourney.client.models.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.MilkyWayStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;

public class MilkyWayStargateModel extends GenericStargateModel<MilkyWayStargateEntity, MilkyWayStargateVariant>
{
	public MilkyWayStargateModel()
	{
		super((short) 39);
	}
	
	@Override
	public MilkyWayStargateVariant getClientVariant(MilkyWayStargateEntity stargate)
	{
		StargateVariant stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant != null)
		{
			if(stargateVariant.isFound())
				return ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.clientVariant());
			else if(!stargateVariant.isMissing())
				stargateVariant.handleLocation(ClientStargateVariants.hasMilkyWayStargateVariant(stargateVariant.clientVariant()));
		}
		
		return ClientStargateVariants.getMilkyWayStargateVariant(stargate.defaultVariant());
	}
	
	@Override
	public void renderStargate(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(stargateVariant.texture()));
		this.renderOuterRing(stack, consumer, source, combinedLight);
		
		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, this.rotation);
		
		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay, StargateJourney.isOculusLoaded());
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}

	@Override
	protected boolean isPrimaryChevronOpen(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		if(stargateVariant.stargateModel().movieChevronLocking())
		{
			if(ClientStargateConfig.movie_primary_chevron_opens.get())
				return stargate.isConnected();
			else 
				return false;
		}
		
		return stargate.isChevronOpen();
	}

	@Override
	protected boolean isPrimaryChevronBackRaised(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		if(!stargateVariant.stargateModel().raiseBackChevrons())
			return false;
		
		return isPrimaryChevronOpen(stargate, stargateVariant);
	}

	@Override
	protected boolean isPrimaryChevronLowered(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		return isPrimaryChevronOpen(stargate, stargateVariant);
	}

	@Override
	protected boolean isPrimaryChevronEngaged(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		if(!stargateVariant.stargateModel().movieChevronLocking() && stargate.isChevronOpen())
			return true;
		
		return super.isPrimaryChevronEngaged(stargate, stargateVariant);
	}
	
	@Override
	protected boolean isChevronOpen(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant, int chevronNumber)
	{
		if(!stargateVariant.stargateModel().movieChevronLocking())
			return false;
		
		int chevronsRendered = stargate.chevronsRendered();
		
		if(stargate.isConnected() && chevronNumber <= chevronsRendered)
			return true;
		
		if(stargate.isChevronOpen())
		{
			Address address = stargate.getAddress();
			if(stargate.getCurrentSymbol() == 0 || address.hasPointOfOriginOrMaxLength())
			{
				if(chevronNumber <= chevronsRendered)
					return true;
			}
			else
			{
				if(stargate.isCurrentSymbol(address.symbolAt(address.regularSymbolCount() - 1)))
				{
					if(AbstractStargateEntity.getChevron(stargate, chevronNumber) == AbstractStargateEntity.getChevron(stargate, chevronsRendered))
						return true;
				}
				else if(AbstractStargateEntity.getChevron(stargate, chevronNumber) == AbstractStargateEntity.getChevron(stargate, chevronsRendered + 1))
					return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean isChevronBackRaised(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant, int chevronNumber)
	{
		if(!stargateVariant.stargateModel().raiseBackChevrons())
			return false;
		
		return isChevronOpen(stargate, stargateVariant, chevronNumber);
	}

	@Override
	protected boolean isChevronClosed(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant, int chevronNumber)
	{
		if(!stargateVariant.stargateModel().movieChevronLocking())
			return false;
		
		int chevronsRendered = stargate.chevronsRendered();
		
		if(stargate.isConnected() && chevronNumber <= chevronsRendered)
			return true;
		
		if(stargate.isChevronOpen())
		{
			Address address = stargate.getAddress();
			if(stargate.getCurrentSymbol() == 0 || address.hasPointOfOriginOrMaxLength())
			{
				if(chevronNumber <= chevronsRendered)
					return true;
			} else
			{
				if(stargate.isCurrentSymbol(address.symbolAt(address.regularSymbolCount() - 1)))
				{
					if(AbstractStargateEntity.getChevron(stargate, chevronNumber) == AbstractStargateEntity.getChevron(stargate, chevronsRendered))
						return true;
				} else if(AbstractStargateEntity.getChevron(stargate, chevronNumber) == AbstractStargateEntity.getChevron(stargate, chevronsRendered + 1))
					return true;
			}
		}
		
		if(ClientStargateConfig.alternate_movie_chevron_locking.get() && chevronNumber <= chevronsRendered)
			return true;
		
		return false;
	}
}
