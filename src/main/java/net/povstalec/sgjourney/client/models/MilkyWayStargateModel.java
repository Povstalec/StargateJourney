package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class MilkyWayStargateModel extends GenericStargateModel<MilkyWayStargateEntity>
{
	protected static final float MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT = 4F / 16;
	protected static final float MOVIE_OUTER_CHEVRON_OUTER_CUTOFF_HEIGHT = 4.4F / 16; // Why this? Don't ask me, it just works for some reason
	protected static final float MOVIE_OUTER_CHEVRON_X_OFFSET = (OUTER_CHEVRON_TOP_OFFSET / (OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_BOTTOM_HEIGHT)) * MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT;
	protected static final float MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS = ((OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS - OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS) / (OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_BOTTOM_HEIGHT)) * MOVIE_OUTER_CHEVRON_OUTER_CUTOFF_HEIGHT;
	protected static final float MOVIE_OUTER_OUTER_X_OFFSET = -0.5F / 16;
	protected static final float MOVIE_OUTER_OUTER_Y_OFFSET = 2F / 16;
	protected static final float MOVIE_OUTER_OUTER_Y_LENGTH = 4F / 16;
	
	protected final ResourceLocation alternateStargateTexture;
	protected final ResourceLocation alternateEngagedTexture;
	
	public MilkyWayStargateModel()
	{
		super(new ResourceLocation(StargateJourney.MODID, "milky_way"), 39, new Stargate.RGBA(48, 49, 63, 255));
		
		this.alternateStargateTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate.png");
		this.alternateEngagedTexture = new ResourceLocation(namespace, "textures/entity/stargate/" + name + "/" + name +"_stargate_alternate_engaged.png");
	}
	
	public void renderStargate(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture(stargate)));
		this.renderOuterRing(stack, consumer, source, combinedLight);
		
		this.renderSymbolRing(stargate, stack, consumer, source, combinedLight, this.rotation);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	@Override
	protected ResourceLocation getStargateTexture(MilkyWayStargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
			return variant.get().getTexture();
		
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ?
				this.alternateStargateTexture : this.stargateTexture;
	}

	@Override
	protected ResourceLocation getEngagedTexture(MilkyWayStargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && canUseVariant(variant.get()))
			return variant.get().getEngagedTexture();
		
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ?
				this.alternateEngagedTexture : this.engagedTexture;
	}
	
	protected boolean useMovieStargateModel(MilkyWayStargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && variant.get().useAlternateModel().isPresent())
			return variant.get().useAlternateModel().get();
		
		return ClientStargateConfig.use_movie_stargate_model.get();
	}
	
	protected boolean raiseBackChevrons(MilkyWayStargateEntity stargate)
	{
		Optional<StargateVariant> variant = getVariant(stargate);
		if(variant.isPresent() && variant.get().backChevrons().isPresent())
			return variant.get().backChevrons().get();
		
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get();
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	protected void renderMovieChevronFront(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		renderLeftMovieChevron(stack, consumer, source, combinedLight);
		renderLeftMovieOuterChevron(stack, consumer, source, combinedLight);
		renderRightMovieChevron(stack, consumer, source, combinedLight);
		renderRightMovieOuterChevron(stack, consumer, source, combinedLight);
	}
	
	@Override
	protected void renderPrimaryChevron(MilkyWayStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean chevronEngaged)
	{
		if(!useMovieStargateModel(stargate))
		{
			super.renderPrimaryChevron(stargate, stack, consumer, source, combinedLight, chevronEngaged);
			return;
		}
		
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.translate(0, 3.5F - 2.5F/16, 0);
		
		renderChevronLight(stack, consumer, source, light, isPrimaryChevronRaised(stargate), isPrimaryChevronBackRaised(stargate));
		renderMovieChevronFront(stack, consumer, source, light);
		renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();
	}

	@Override
	protected boolean isPrimaryChevronRaised(MilkyWayStargateEntity stargate)
	{
		if(useMovieStargateModel(stargate))
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

	protected boolean isPrimaryChevronBackRaised(MilkyWayStargateEntity stargate)
	{
		if(!raiseBackChevrons(stargate))
			return false;
		
		return isPrimaryChevronRaised(stargate);
	}

	@Override
	protected boolean isPrimaryChevronLowered(MilkyWayStargateEntity stargate)
	{
		return isPrimaryChevronRaised(stargate);
	}

	@Override
	protected boolean isPrimaryChevronEngaged(MilkyWayStargateEntity stargate)
	{
		if(!useMovieStargateModel(stargate) && stargate.isChevronOpen())
			return true;
			
		if(stargate.isConnected())
			return stargate.isDialingOut() || stargate.getKawooshTickCount() > 0;
		
		return false;
	}
	
	@Override
	protected boolean isChevronRaised(MilkyWayStargateEntity stargate, int chevronNumber)
	{
		if(!useMovieStargateModel(stargate))
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
					if(chevronNumber == chevronsRendered)
						return true;
				}
				else if(chevronNumber == chevronsRendered + 1)
					return true;
			}
		}
		
		return false;
	}

	protected boolean isChevronBackRaised(MilkyWayStargateEntity stargate, int chevronNumber)
	{
		if(!raiseBackChevrons(stargate))
			return false;
		
		return isChevronRaised(stargate, chevronNumber);
	}

	@Override
	protected boolean isChevronLowered(MilkyWayStargateEntity stargate, int chevronNumber)
	{
		if(!useMovieStargateModel(stargate))
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
					if(chevronNumber == chevronsRendered)
						return true;
				}
				else if(chevronNumber == chevronsRendered + 1)
					return true;
			}
		}
		
		if(alternateChevronLocking && chevronNumber < chevronsRendered + 1)
			return true;
		
		return false;
	}
	
	protected void renderLeftMovieChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Left Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				47F/64, 35F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 36F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 36F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				50F/64, 35F/64);
		
		//Left Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 36F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(52F - MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, (45F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(54F - MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, (45F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 36F/64);
		
		//Left Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 1, 0,
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 36F/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 41F/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				55F/64, 41F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				55F/64, 36F/64);
		
		//Left Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				46F/64, 36F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				46F/64, 41F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 41F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 36F/64);
		
		//Left Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(52F - MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 41F/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(52F - MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 40F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(54F - MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 40F/64,
			
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(54F - MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 41F/64);
	}
	
	protected void renderLeftMovieOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		//Left Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				47F/64, 46F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 47F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 47F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				50F/64, 46F/64);
		
		//Left Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-(OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 47F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				OUTER_CHEVRON_Z_OFFSET,
				53F/64, 56F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				53F/64, 52F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 47F/64);
		
		//Left Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				OUTER_CHEVRON_Z_OFFSET,
				46F/64, 56F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 47F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				47F/64, 47F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				STARGATE_RING_OFFSET,
				46F/64, 56F/64);

		//Left Right 1
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				53F/64, 52F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				OUTER_CHEVRON_Z_OFFSET,
				53F/64, 56F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				STARGATE_RING_OFFSET,
				54F/64, 56F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				54F/64, 52F/64);

		//Left Right 2
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 1, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				53F/64, 47F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				53F/64, 52F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				54F/64, 52F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				54F/64, 47F/64);
	}
	
	protected void renderRightMovieChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Right Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				60F/64, 35F/64,
				
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				60F/64, 36F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				63F/64, 36F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				63F/64, 35F/64);
		
		//Right Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			60F/64, 36F/64,
			
			MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			(56F + MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, (45F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
			
			MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			(58F + MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, (45F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
			
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 36F/64);
		
		//Right Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 36F/64,
			
			MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 41F/64,
			
			MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			STARGATE_RING_OFFSET,
			64F/64, 41F/64,
			
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			STARGATE_RING_OFFSET,
			64F/64, 36F/64);
		
		//Right Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 1, 0,
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			STARGATE_RING_OFFSET,
			55F/64, 36F/64,
			
			MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			STARGATE_RING_OFFSET,
			55F/64, 41F/64,
			
			MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			56F/64, 41F/64,
			
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			56F/64, 36F/64);
		
		//Right Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(56F + MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 40F/64,
				
				MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(56F + MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 41F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(58F + MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 41F/64,
			
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(58F + MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 40F/64);
	}
	
	protected void renderRightMovieOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Right Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				60F/64, 47F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				60F/64, 46F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				63F/64, 46F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				63F/64, 47F/64);
		
		//Right Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				OUTER_CHEVRON_Z_OFFSET,
				57F/64, 56F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				63F/64, 47F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				60F/64, 47F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				57F/64, 52F/64);
		
		//Right Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				63F/64, 47F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				OUTER_CHEVRON_Z_OFFSET,
				64F/64, 56F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				STARGATE_RING_OFFSET,
				64F/64, 56F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + 2 * OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				63F/64, 47F/64);

		//Right Left 1
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				OUTER_CHEVRON_Z_OFFSET,
				57F/64, 56F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				57F/64, 52F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				56F/64, 52F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET - MOVIE_OUTER_OUTER_Y_LENGTH,
				STARGATE_RING_OFFSET,
				56F/64, 56F/64);

		//Right Left 2
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 1, 0,
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				57F/64, 52F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				57F/64, 47F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS + MOVIE_OUTER_OUTER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				56F/64, 47F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET + MOVIE_OUTER_OUTER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT - MOVIE_OUTER_OUTER_Y_OFFSET,
				STARGATE_RING_OFFSET,
				56F/64, 52F/64);
	}
}
