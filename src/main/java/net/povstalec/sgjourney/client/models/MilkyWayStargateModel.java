package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.MilkyWayStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class MilkyWayStargateModel extends GenericStargateModel<MilkyWayStargateEntity, MilkyWayStargateVariant>
{
	public MilkyWayStargateModel()
	{
		super(new ResourceLocation(StargateJourney.MODID, "milky_way/milky_way"), (short) 39);
	}
	
	@Override
	protected MilkyWayStargateVariant getClientVariant(MilkyWayStargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.get().clientVariant());
		
		return ClientStargateVariants.getMilkyWayStargateVariant(getResourceLocation());
	}
	
	@Override
	public void renderStargate(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		MilkyWayStargateVariant stargateVariant = getClientVariant(stargate);
		
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(stargateVariant.texture()));
		this.renderOuterRing(stack, consumer, source, combinedLight);
		
		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, this.rotation);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}

	@Override
	protected boolean isPrimaryChevronRaised(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		if(stargateVariant.useMovieStargateModel())
		{
			if(ClientStargateConfig.movie_primary_chevron_opens.get())
				return stargate.isConnected();
			else 
				return false;
		}
		
		if(stargate.isChevronOpen())
			return true;
		
		return false;
	}

	@Override
	protected boolean isPrimaryChevronBackRaised(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		if(!stargateVariant.raiseBackChevrons())
			return false;
		
		return isPrimaryChevronRaised(stargate, stargateVariant);
	}

	@Override
	protected boolean isPrimaryChevronLowered(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		return isPrimaryChevronRaised(stargate, stargateVariant);
	}

	@Override
	protected boolean isPrimaryChevronEngaged(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant)
	{
		if(!stargateVariant.useMovieStargateModel() && stargate.isChevronOpen())
			return true;
			
		if(stargate.isConnected())
			return stargate.isDialingOut() || stargate.getKawooshTickCount() > 0;
		
		return false;
	}
	
	@Override
	protected boolean isChevronRaised(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant, int chevronNumber)
	{
		if(!stargateVariant.useMovieStargateModel())
			return false;
		
		int chevronsRendered = stargate.chevronsRendered();
		
		if(stargate.isConnected() && chevronNumber < chevronsRendered + 1)
			return true;
		
		if(stargate.isChevronOpen())
		{
			if(stargate.getCurrentSymbol() == 0 || chevronsRendered >= 8)
			{
				if(chevronNumber < chevronsRendered + 1)
					return true;
			}
			else
			{
				Address address = stargate.getAddress();
				if(stargate.isCurrentSymbol(address.getSymbol(address.getLength() - 1)))
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
		if(!stargateVariant.raiseBackChevrons())
			return false;
		
		return isChevronRaised(stargate, stargateVariant, chevronNumber);
	}

	@Override
	protected boolean isChevronLowered(MilkyWayStargateEntity stargate, MilkyWayStargateVariant stargateVariant, int chevronNumber)
	{
		if(!stargateVariant.useMovieStargateModel())
			return false;

		int chevronsRendered = stargate.chevronsRendered();
		
		if(stargate.isConnected() && chevronNumber < chevronsRendered + 1)
			return true;
		
		if(stargate.isChevronOpen())
		{
			if(stargate.getCurrentSymbol() == 0 || chevronsRendered > 8)
			{
				if(chevronNumber < chevronsRendered + 1)
					return true;
			}
			else
			{
				Address address = stargate.getAddress();
				if(stargate.isCurrentSymbol(address.getSymbol(address.getLength() - 1)))
				{
					if(AbstractStargateEntity.getChevron(stargate, chevronNumber) == AbstractStargateEntity.getChevron(stargate, chevronsRendered))
						return true;
				}
				else if(AbstractStargateEntity.getChevron(stargate, chevronNumber) == AbstractStargateEntity.getChevron(stargate, chevronsRendered + 1))
					return true;
			}
		}
		
		if(ClientStargateConfig.alternate_movie_chevron_locking.get() && chevronNumber < chevronsRendered + 1)
			return true;
		
		return false;
	}
}
