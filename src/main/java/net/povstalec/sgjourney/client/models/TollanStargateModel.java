package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.TollanStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;

public class TollanStargateModel extends AbstractStargateModel<TollanStargateEntity, TollanStargateVariant>
{
	protected static final float TOLLAN_RADIUS = 3F;
	protected static final float TOLLAN_RING_HEIGHT = 8F / 16;
	protected static final float STARGATE_RING_THICKNESS = 3F;
	protected static final float STARGATE_RING_OFFSET = STARGATE_RING_THICKNESS / 2 / 16;
	
	protected static final float STARGATE_RING_OUTER_RADIUS = TOLLAN_RADIUS - STARGATE_RING_SHRINK;
	protected static final float STARGATE_RING_OUTER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_OUTER_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_OUTER_CENTER = STARGATE_RING_OUTER_LENGTH / 2;

	protected static final float STARGATE_RING_INNER_RADIUS = TOLLAN_RADIUS - (TOLLAN_RING_HEIGHT - STARGATE_RING_SHRINK);
	protected static final float STARGATE_RING_INNER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_INNER_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_INNER_CENTER = STARGATE_RING_INNER_LENGTH / 2;
	
	protected static final float STARGATE_RING_HEIGHT = STARGATE_RING_OUTER_RADIUS - STARGATE_RING_INNER_RADIUS;

	protected static final float CHEVRON_OFFSET = 1F / 16;
	protected static final float CHEVRON_THICKNESS = STARGATE_RING_THICKNESS / 16 + 2 * CHEVRON_OFFSET;
	protected static final float CHEVRON_WIDTH = 5F / 16;
	protected static final float CHEVRON_HEIGHT = 9F / 16;

	public TollanStargateModel()
	{
		super((short) 0);
	}
	
	@Override
	public void renderRing(TollanStargateEntity stargate, TollanStargateVariant stargateVariant, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		renderOuterRing(stack, consumer, source, combinedLight);
	}
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================
	
	@Override
	protected void renderPrimaryChevron(TollanStargateEntity stargate, TollanStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean chevronEngaged)
	{
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.translate(0, TOLLAN_RADIUS - 0.5F + STARGATE_RING_SHRINK, 0);
		
		renderChevronLight(stack, consumer, source, light, chevronEngaged);
		
		stack.popPose();
	}
	
	@Override
	protected void renderChevron(TollanStargateEntity stargate, TollanStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		int chevron = AbstractStargateEntity.getChevron(stargate, chevronNumber);
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(-CHEVRON_ANGLE * chevron));
		stack.translate(0, TOLLAN_RADIUS - 0.5F + STARGATE_RING_SHRINK, 0);
		
		renderChevronLight(stack, consumer, source, light, chevronEngaged);
		
		stack.popPose();
	}
	
	protected void renderChevronLight(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, 
			int combinedLight, boolean isEngaged)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		// Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				-CHEVRON_THICKNESS / 2,
				49F / 64, 0F / 64,
				
				-CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				CHEVRON_THICKNESS / 2,
				49F / 64, 5F / 64,
				
				CHEVRON_WIDTH / 2, 
				CHEVRON_HEIGHT,
				CHEVRON_THICKNESS / 2,
				54F / 64, 5F / 64,
				
				CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				-CHEVRON_THICKNESS / 2,
				54F / 64, 0F / 64);
		
		// Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				CHEVRON_THICKNESS / 2,
				49F / 64, 5F / 64,
				
				-CHEVRON_WIDTH / 2,
				0,
				CHEVRON_THICKNESS / 2,
				49F / 64, 14F / 64,
				
				CHEVRON_WIDTH / 2, 
				0,
				CHEVRON_THICKNESS / 2,
				54F / 64, 14F / 64,
				
				CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				CHEVRON_THICKNESS / 2,
				54F / 64, 5F / 64);
		
		// Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
				-CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				-CHEVRON_THICKNESS / 2,
				44F / 64, 5F / 64,
				
				-CHEVRON_WIDTH / 2,
				0,
				-CHEVRON_THICKNESS / 2,
				44F / 64, 14F / 64,
				
				-CHEVRON_WIDTH / 2, 
				0,
				CHEVRON_THICKNESS / 2,
				49F / 64, 14F / 64,
				
				-CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				CHEVRON_THICKNESS / 2,
				49F / 64, 5F / 64);
		
		// Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
				CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				CHEVRON_THICKNESS / 2,
				44F / 64, 5F / 64,
				
				CHEVRON_WIDTH / 2,
				0,
				CHEVRON_THICKNESS / 2,
				44F / 64, 14F / 64,
				
				CHEVRON_WIDTH / 2, 
				0,
				-CHEVRON_THICKNESS / 2,
				49F / 64, 14F / 64,
				
				CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				-CHEVRON_THICKNESS / 2,
				49F / 64, 5F / 64);
		
		// Back
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
				CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				-CHEVRON_THICKNESS / 2,
				59F / 64, 5F / 64,
				
				CHEVRON_WIDTH / 2,
				0,
				-CHEVRON_THICKNESS / 2,
				59F / 64, 14F / 64,
				
				-CHEVRON_WIDTH / 2, 
				0,
				-CHEVRON_THICKNESS / 2,
				64F / 64, 14F / 64,
				
				-CHEVRON_WIDTH / 2,
				CHEVRON_HEIGHT,
				-CHEVRON_THICKNESS / 2,
				64F / 64, 5F / 64);
		
		// Bottom 1
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-CHEVRON_WIDTH / 2,
				0,
				CHEVRON_THICKNESS / 2,
				54F / 64, 0F / 64,
				
				-CHEVRON_WIDTH / 2,
				0,
				CHEVRON_THICKNESS / 2 - CHEVRON_OFFSET,
				54F / 64, 1F / 64,
				
				CHEVRON_WIDTH / 2, 
				0,
				CHEVRON_THICKNESS / 2 - CHEVRON_OFFSET,
				59F / 64, 1F / 64,
				
				CHEVRON_WIDTH / 2,
				0,
				CHEVRON_THICKNESS / 2,
				59F / 64, 0F / 64);
		
		// Bottom 2
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-CHEVRON_WIDTH / 2,
				0,
				-CHEVRON_THICKNESS / 2 + CHEVRON_OFFSET,
				54F / 64, 4F / 64,
				
				-CHEVRON_WIDTH / 2,
				0,
				-CHEVRON_THICKNESS / 2,
				54F / 64, 5F / 64,
				
				CHEVRON_WIDTH / 2, 
				0,
				-CHEVRON_THICKNESS / 2,
				59F / 64, 5F / 64,
				
				CHEVRON_WIDTH / 2,
				0,
				-CHEVRON_THICKNESS / 2 + CHEVRON_OFFSET,
				59F / 64, 4F / 64);
	}
	
	//============================================================================================
	//********************************************Ring********************************************
	//============================================================================================
	
	protected void renderOuterRing(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		for(int j = 0; j < DEFAULT_SIDES; j++)
		{
			stack.mulPose(Axis.ZP.rotationDegrees(10));
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Outside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_OUTER_CENTER, 
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, 0,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, 3F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, 3F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, 0);
			
			//Inside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_INNER_CENTER * 16) / 64, 0,
					
					STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_INNER_CENTER * 16) / 64, 3F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_INNER_CENTER * 16) / 64, 3F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_INNER_CENTER * 16) / 64, 0);
		}
	}

}
