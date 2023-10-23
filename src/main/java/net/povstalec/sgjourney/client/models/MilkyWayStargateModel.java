package net.povstalec.sgjourney.client.models;

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

public class MilkyWayStargateModel extends GenericStargateModel
{
	protected static final float MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT = 4F / 16;
	protected static final float MOVIE_OUTER_CHEVRON_OUTER_CUTOFF_HEIGHT = 4.4F / 16; // Why this? Don't ask me, it just works for some reason
	protected static final float MOVIE_OUTER_CHEVRON_X_OFFSET = (OUTER_CHEVRON_TOP_OFFSET / (OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_BOTTOM_HEIGHT)) * MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT;
	protected static final float MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS = ((OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS - OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS) / (OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_BOTTOM_HEIGHT)) * MOVIE_OUTER_CHEVRON_OUTER_CUTOFF_HEIGHT;
	
	private float rotation = 0.0F;
	
	protected ResourceLocation alternateStargateTexture;
	protected ResourceLocation alternateEngagedTexture;
	
	public MilkyWayStargateModel()
	{
		super("milky_way", 39, 48F/255.0F, 49F/255.0F, 63F/255.0F);
		
		this.alternateStargateTexture = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/" + stargateName + "/" + stargateName +"_stargate_alternate.png");
		this.alternateEngagedTexture = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/" + stargateName + "/" + stargateName +"_stargate_alternate_engaged.png");
	}
	
	public void renderStargate(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture()));
		this.renderOuterRing(stack, consumer, source, combinedLight);
		
		this.renderSymbolRing(stargate, stack, consumer, source, combinedLight, this.rotation);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	@Override
	protected ResourceLocation getStargateTexture()
	{
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ?
				this.alternateStargateTexture : this.stargateTexture;
	}

	@Override
	protected ResourceLocation getEngagedTexture()
	{
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ?
				this.alternateEngagedTexture : this.engagedTexture;
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	protected void renderMovieChevronFront(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		renderLeftMovieChevron(stack, consumer, source, combinedLight);
		renderRightMovieChevron(stack, consumer, source, combinedLight);
	}

	protected void renderPrimaryChevron(MilkyWayStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean chevronEngaged, boolean isRaised)
	{
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.translate(0, 3.5F - 2.5F/16, 0);
		
		renderChevronLight(stack, consumer, source, light, isRaised);
		
		if(!ClientStargateConfig.use_movie_stargate_model.get())
			renderOuterChevronFront(stack, consumer, source, light, isRaised);
		else
			renderMovieChevronFront(stack, consumer, source, light);
		
		renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();
	}
	
	protected boolean isPrimaryChevronRaised(MilkyWayStargateEntity stargate)
	{
		boolean useMovieStargateModel = ClientStargateConfig.use_movie_stargate_model.get();
		
		if(useMovieStargateModel)
			return false;
		
		if(stargate.isChevronRaised())
			return true;
		
		return false;
	}
	
	protected boolean isPrimaryChevronEngaged(MilkyWayStargateEntity stargate)
	{
		boolean useMovieStargateModel = ClientStargateConfig.use_movie_stargate_model.get();
		
		if(!useMovieStargateModel && stargate.isChevronRaised())
			return true;
			
		if(stargate.isConnected())
			return stargate.isDialingOut() || stargate.getKawooshTickCount() > 0;
		
		return false;
	}
	
	protected boolean isMovieChevronLightRaised(MilkyWayStargateEntity stargate, int chevronNumber)
	{
		boolean alternateChevronLocking = ClientStargateConfig.alternate_movie_chevron_locking.get();
		int chevronsRendered = stargate.chevronsRendered();
		
		if(stargate.isConnected() && chevronNumber < chevronsRendered + 1)
			return true;
		
		if(stargate.isChevronRaised())
		{
			if(stargate.getCurrentSymbol() == 0 || chevronsRendered >= 8)
			{
				if(chevronNumber < chevronsRendered + 1)
					return !alternateChevronLocking;
			}
			else
			{
				if(chevronNumber == chevronsRendered + 1)
					return true;
			}
		}
		
		return false;
	}
	
	protected boolean isOuterMovieChevronLowered(MilkyWayStargateEntity stargate, int chevronNumber)
	{
		boolean alternateChevronLocking = ClientStargateConfig.alternate_movie_chevron_locking.get();
		int chevronsRendered = stargate.chevronsRendered();
		
		if(stargate.isConnected() && chevronNumber < chevronsRendered + 1)
			return true;
		
		if(stargate.isChevronRaised())
		{
			if(stargate.getCurrentSymbol() == 0)
			{
				if(chevronNumber < chevronsRendered + 1)
					return false;
			}
			else
			{
				if(chevronNumber == chevronsRendered + 1)
					return true;
			}
		}
		
		if(alternateChevronLocking && chevronNumber < chevronsRendered + 1)
			return true;
		
		return false;
	}
	
	protected void renderChevrons(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		//Renders Chevrons
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.chevron(getStargateTexture()));
		
		renderPrimaryChevron(stargate, stack, consumer, source, combinedLight, false, isPrimaryChevronRaised(stargate));
		for(int chevronNumber = 1; chevronNumber < 9; chevronNumber++)
		{
			boolean isChevronLightRaised = ClientStargateConfig.use_movie_stargate_model.get() ? isMovieChevronLightRaised(stargate, chevronNumber) : false;
			boolean isOuterChevronOpen = ClientStargateConfig.use_movie_stargate_model.get() ? isOuterMovieChevronLowered(stargate, chevronNumber) : false;
			
			renderChevron(stargate, stack, consumer, source, combinedLight, chevronNumber, false, isChevronLightRaised, isOuterChevronOpen);
		}
		
		//Renders lit up parts of Chevrons
		consumer = source.getBuffer(SGJourneyRenderTypes.engagedChevron(getEngagedTexture()));
		if(isPrimaryChevronEngaged(stargate))
			renderPrimaryChevron(stargate, stack, consumer, source, combinedLight, true, isPrimaryChevronRaised(stargate));
		for(int chevronNumber = 1; chevronNumber < 9; chevronNumber++)
		{
			boolean isChevronEngaged = stargate.chevronsRendered() >= chevronNumber;
			boolean isChevronLightRaised = ClientStargateConfig.use_movie_stargate_model.get() ? isMovieChevronLightRaised(stargate, chevronNumber) : false;
			boolean isOuterChevronOpen = ClientStargateConfig.use_movie_stargate_model.get() ? isOuterMovieChevronLowered(stargate, chevronNumber) : false;
			
			if(isChevronEngaged)
				renderChevron(stargate, stack, consumer, source, combinedLight, chevronNumber, isChevronEngaged, isChevronLightRaised, isOuterChevronOpen);
		}
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
				47F/64, 34F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 35F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 35F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				50F/64, 34F/64);
		
		//TODO
		//Left Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 35F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(52F - MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, (44F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(54F - MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, (44F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 35F/64);
		
		//Left Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 35F/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 40F/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				55F/64, 40F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				55F/64, 35F/64);
		
		//Left Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				46F/64, 35F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				46F/64, 40F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 40F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 35F/64);
		
		//Left Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(52F - MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 40F/64,
				
				-MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(52F - MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 39F/64,
				
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(54F - MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 39F/64,
			
				-(MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET),
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(54F - MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 40F/64);
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
				60F/64, 34F/64,
				
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				60F/64, 35F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				63F/64, 35F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				STARGATE_RING_OFFSET,
				63F/64, 34F/64);
		
		//TODO
		//Right Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			60F/64, 35F/64,
			
			MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			(56F + MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, (44F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
			
			MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			(58F + MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, (44F - MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT * 16)/64,
			
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 35F/64);
		
		//Right Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 35F/64,
			
			MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 40F/64,
			
			MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			STARGATE_RING_OFFSET,
			64F/64, 40F/64,
			
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			STARGATE_RING_OFFSET,
			64F/64, 35F/64);
		
		//Right Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			STARGATE_RING_OFFSET,
			55F/64, 35F/64,
			
			MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			STARGATE_RING_OFFSET,
			55F/64, 40F/64,
			
			MOVIE_OUTER_CHEVRON_X_OFFSET,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
			OUTER_CHEVRON_Z_OFFSET,
			56F/64, 40F/64,
			
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
			OUTER_CHEVRON_Z_OFFSET,
			56F/64, 35F/64);
		
		//Right Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(56F + MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 39F/64,
				
				MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(56F + MOVIE_OUTER_CHEVRON_X_OFFSET * 16)/64, 40F/64,
				
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				STARGATE_RING_OFFSET,
				(58F + MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 40F/64,
			
				MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS + MOVIE_OUTER_CHEVRON_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + MOVIE_OUTER_CHEVRON_CUTOFF_HEIGHT,
				OUTER_CHEVRON_Z_OFFSET,
				(58F + MOVIE_OUTER_CHEVRON_BOTTOM_THICKNESS * 16)/64, 39F/64);
	}
}
