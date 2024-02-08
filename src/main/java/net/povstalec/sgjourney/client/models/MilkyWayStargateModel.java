package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class MilkyWayStargateModel extends GenericStargateModel<MilkyWayStargateEntity>
{
	protected final ResourceLocation alternateStargateTexture;
	protected final ResourceLocation alternateEngagedTexture;
	
	public MilkyWayStargateModel()
	{
		super(new ResourceLocation(StargateJourney.MODID, "milky_way"), (short) 39, new Stargate.RGBA(48, 49, 63, 255));
		
		this.alternateStargateTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate.png");
		this.alternateEngagedTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate_engaged.png");
	}
	
	@Override
	public void renderStargate(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		Optional<StargateVariant> stargateVariant = getStargateVariant(stargate);
		
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture(stargate, stargateVariant)));
		this.renderOuterRing(stack, consumer, source, combinedLight);
		
		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, this.rotation);

		this.renderChevrons(stargate, stargateVariant, stack, source, combinedLight, combinedOverlay);
	}
	
	@Override
	protected ResourceLocation getStargateTexture(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
			return stargateVariant.get().getTexture();
		
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ?
				this.alternateStargateTexture : this.stargateTexture;
	}

	@Override
	protected ResourceLocation getEngagedTexture(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent())
			return stargateVariant.get().getEngagedTexture();
		
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ?
				this.alternateEngagedTexture : this.engagedTexture;
	}
	
	@Override
	protected boolean useMovieStargateModel(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent() && stargateVariant.get().useAlternateModel().isPresent())
			return stargateVariant.get().useAlternateModel().get();
		
		return ClientStargateConfig.use_movie_stargate_model.get();
	}
	
	protected boolean raiseBackChevrons(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(stargateVariant.isPresent() && stargateVariant.get().backChevrons().isPresent())
			return stargateVariant.get().backChevrons().get();
		
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get();
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}

	@Override
	protected boolean isPrimaryChevronRaised(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(useMovieStargateModel(stargate, stargateVariant))
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
	protected boolean isPrimaryChevronBackRaised(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(!raiseBackChevrons(stargate, stargateVariant))
			return false;
		
		return isPrimaryChevronRaised(stargate, stargateVariant);
	}

	@Override
	protected boolean isPrimaryChevronLowered(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		return isPrimaryChevronRaised(stargate, stargateVariant);
	}

	@Override
	protected boolean isPrimaryChevronEngaged(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant)
	{
		if(!useMovieStargateModel(stargate, stargateVariant) && stargate.isChevronOpen())
			return true;
			
		if(stargate.isConnected())
			return stargate.isDialingOut() || stargate.getKawooshTickCount() > 0;
		
		return false;
	}
	
	@Override
	protected boolean isChevronRaised(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant, int chevronNumber)
	{
		if(!useMovieStargateModel(stargate, stargateVariant))
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
	protected boolean isChevronBackRaised(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant, int chevronNumber)
	{
		if(!raiseBackChevrons(stargate, stargateVariant))
			return false;
		
		return isChevronRaised(stargate, stargateVariant, chevronNumber);
	}

	@Override
	protected boolean isChevronLowered(MilkyWayStargateEntity stargate, Optional<StargateVariant> stargateVariant, int chevronNumber)
	{
		if(!useMovieStargateModel(stargate, stargateVariant))
			return false;

		boolean alternateChevronLocking = ClientStargateConfig.alternate_movie_chevron_locking.get();
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
		
		if(alternateChevronLocking && AbstractStargateEntity.getChevron(stargate, chevronNumber) < AbstractStargateEntity.getChevron(stargate, chevronsRendered + 1))
			return true;
		
		return false;
	}
}
