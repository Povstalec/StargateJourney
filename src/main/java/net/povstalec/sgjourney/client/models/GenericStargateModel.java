package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.stargate.Stargate;

public abstract class GenericStargateModel<StargateEntity extends AbstractStargateEntity> extends AbstractStargateModel<StargateEntity>
{
	// Ring
	protected static final float STARGATE_RING_THICKNESS = 7F;
	protected static final float STARGATE_RING_OFFSET = STARGATE_RING_THICKNESS / 2 / 16;
	
	protected static final float STARGATE_RING_OUTER_RADIUS = DEFAULT_RADIUS - STARGATE_RING_SHRINK;
	protected static final float STARGATE_RING_OUTER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_OUTER_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_OUTER_CENTER = STARGATE_RING_OUTER_LENGTH / 2;
	
	protected static final float STARGATE_RING_STOP_RADIUS = DEFAULT_RADIUS - 7F / 16;
	protected static final float STARGATE_RING_STOP_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_STOP_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_STOP_CENTER = STARGATE_RING_STOP_LENGTH / 2;

	protected static final float STARGATE_RING_START_RADIUS = DEFAULT_RADIUS - 13F / 16;
	protected static final float STARGATE_RING_START_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_START_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_START_CENTER = STARGATE_RING_START_LENGTH / 2;

	protected static final float STARGATE_RING_INNER_HEIGHT = DEFAULT_RADIUS - (DEFAULT_RING_HEIGHT - STARGATE_RING_SHRINK);
	protected static final float STARGATE_RING_INNER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_INNER_HEIGHT, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_INNER_CENTER = STARGATE_RING_INNER_LENGTH / 2;

	protected static final float STARGATE_RING_HEIGHT = STARGATE_RING_OUTER_RADIUS - STARGATE_RING_INNER_HEIGHT;
	protected static final float STARGATE_EDGE_TO_CUTOUT_HEIGHT = STARGATE_RING_OUTER_RADIUS - STARGATE_RING_STOP_RADIUS;
	protected static final float STARGATE_RING_CUTOUT_HEIGHT = STARGATE_RING_STOP_RADIUS - STARGATE_RING_START_RADIUS;
	protected static final float STARGATE_CUTOUT_TO_INNER_HEIGHT = STARGATE_RING_START_RADIUS - STARGATE_RING_INNER_HEIGHT;

	// Chevrons
	protected static final float CHEVRON_LIGHT_FRONT_LENGTH = 4F / 16;
	protected static final float CHEVRON_LIGHT_DIVIDER_LENGTH = 1F / 16;
	protected static final float CHEVRON_LIGHT_BACK_LENGTH = 4F / 16;

	protected static final float CHEVRON_LIGHT_LENGTH = CHEVRON_LIGHT_FRONT_LENGTH + CHEVRON_LIGHT_DIVIDER_LENGTH + CHEVRON_LIGHT_BACK_LENGTH;

	protected static final float CHEVRON_DIVIDER_HEIGHT = 1F / 16;
	
	protected static final float CHEVRON_LIGHT_Z_OFFSET = CHEVRON_LIGHT_DIVIDER_LENGTH / 2;
	protected static final float CHEVRON_LIGHT_FRONT_Z_OFFSET = CHEVRON_LIGHT_Z_OFFSET + CHEVRON_LIGHT_FRONT_LENGTH;
	protected static final float CHEVRON_LIGHT_BACK_Z_OFFSET = -CHEVRON_LIGHT_Z_OFFSET - CHEVRON_LIGHT_BACK_LENGTH;
	
	protected static final float CHEVRON_LIGHT_TOP_WIDTH = 6F / 16;
	protected static final float CHEVRON_LIGHT_TOP_CENTER = CHEVRON_LIGHT_TOP_WIDTH / 2;
	protected static final float CHEVRON_LIGHT_BOTTOM_WIDTH = 3F / 16;
	protected static final float CHEVRON_LIGHT_BOTTOM_CENTER = CHEVRON_LIGHT_BOTTOM_WIDTH / 2;
	protected static final float CHEVRON_LIGHT_HEIGHT = 7F / 16;
	protected static final float CHEVRON_LIGHT_HEIGHT_CENTER = CHEVRON_LIGHT_HEIGHT / 2;
	
	protected static final float CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER = CHEVRON_LIGHT_BOTTOM_CENTER + (CHEVRON_LIGHT_TOP_CENTER - CHEVRON_LIGHT_BOTTOM_CENTER) * ((CHEVRON_LIGHT_HEIGHT - CHEVRON_DIVIDER_HEIGHT) / CHEVRON_LIGHT_HEIGHT);
	protected static final float CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT = CHEVRON_LIGHT_HEIGHT_CENTER - CHEVRON_DIVIDER_HEIGHT;
	
	protected static final float OUTER_CHEVRON_Z_THICKNESS = 1F / 16;
	protected static final float OUTER_CHEVRON_Z_OFFSET = OUTER_CHEVRON_Z_THICKNESS + STARGATE_RING_OFFSET;

	protected static final float OUTER_CHEVRON_SIDE_HEIGHT = 7F / 16;
	protected static final float OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS = 2F / 16;
	protected static final float OUTER_CHEVRON_SIDE_TOP_THICKNESS = OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS + 1F / 16;
	protected static final float OUTER_CHEVRON_TOP_OFFSET = 4F / 16;
	
	protected static final float OUTER_CHEVRON_LOWER_BOTTOM_WIDTH = 4F / 16;
	protected static final float OUTER_CHEVRON_LOWER_BOTTOM_CENTER = OUTER_CHEVRON_LOWER_BOTTOM_WIDTH / 2;
	
	protected static final float OUTER_CHEVRON_BOTTOM_HEIGHT = 2F / 16;
	protected static final float OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER = OUTER_CHEVRON_BOTTOM_HEIGHT / 2;
	protected static final float OUTER_CHEVRON_Y_OFFSET = -6.5F / 16;
	
	protected static final float OUTER_CHEVRON_UPPER_BOTTOM_CENTER = (OUTER_CHEVRON_TOP_OFFSET / (OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_BOTTOM_HEIGHT)) * OUTER_CHEVRON_BOTTOM_HEIGHT;
	
	protected int symbolSides;
	protected float symbolAngle;
	
	protected static final float STARGATE_SYMBOL_RING_OUTER_HEIGHT = DEFAULT_RADIUS - 6F / 16;
	protected float stargateSymbolRingOuterLength;
	protected float stargateSymbolRingOuterCenter;

	protected static final float STARGATE_SYMBOL_RING_INNER_HEIGHT = DEFAULT_RADIUS - 14F / 16;
	protected float stargateSymbolRingInnerLength;
	protected float stargateSymbolRingInnerCenter;
	
	protected static final float STARGATE_SYMBOL_RING_HEIGHT = STARGATE_SYMBOL_RING_OUTER_HEIGHT - STARGATE_SYMBOL_RING_INNER_HEIGHT;

	protected static final float DIVIDER_THICKNESS = 1F / 16;
	protected static final float DIVIDER_CENTER = DIVIDER_THICKNESS / 2;
	protected static final float DIVIDER_HEIGHT = 8F / 16;
	protected static final float DIVIDER_OFFSET = 0.5F / 16;
	protected static final float DIVIDER_Y_CENTER = STARGATE_SYMBOL_RING_HEIGHT / 2 + STARGATE_SYMBOL_RING_INNER_HEIGHT;
	
	protected float rotation = 0;
	
	public GenericStargateModel(ResourceLocation stargateName, int symbolSides, Stargate.RGBA symbolColor)
	{
		super(stargateName);
		this.symbolSides = symbolSides;
		this.symbolAngle = 360F / symbolSides;
		
		this.stargateSymbolRingOuterLength = SGJourneyModel.getUsedWidth(symbolSides, STARGATE_SYMBOL_RING_OUTER_HEIGHT, DEFAULT_RADIUS);
		this.stargateSymbolRingOuterCenter = stargateSymbolRingOuterLength / 2;

		this.stargateSymbolRingInnerLength = SGJourneyModel.getUsedWidth(symbolSides, STARGATE_SYMBOL_RING_INNER_HEIGHT, DEFAULT_RADIUS);
		this.stargateSymbolRingInnerCenter = stargateSymbolRingInnerLength / 2;
		
		this.symbolColor = symbolColor;
	}
	
	@Override
	public void renderRing(StargateEntity stargate, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		this.renderOuterRing(stack, consumer, source, combinedLight);
		
		this.renderSymbolRing(stargate, stack, consumer, source, combinedLight, this.rotation);
	}
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================

	protected boolean isPrimaryChevronBackRaised(StargateEntity stargate)
	{
		return false;
	}

	protected boolean isChevronBackRaised(StargateEntity stargate, int chevronNumber)
	{
		return false;
	}
	
	@Override
	protected void renderPrimaryChevron(StargateEntity stargate, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, boolean chevronEngaged)
	{
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.translate(0, DEFAULT_RADIUS - 2.5F/16, 0);
		
		renderChevronLight(stack, consumer, source, light, isPrimaryChevronRaised(stargate), isPrimaryChevronBackRaised(stargate));
		renderOuterChevronFront(stack, consumer, source, light, isPrimaryChevronLowered(stargate));
		renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();
	}

	@Override
	protected void renderChevron(StargateEntity stargate, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		int chevron = stargate.getEngagedChevrons()[chevronNumber];
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(-CHEVRON_ANGLE * chevron));
		stack.translate(0, DEFAULT_RADIUS - 2.5F/16, 0);
		
		renderChevronLight(stack, consumer, source, light, isChevronRaised(stargate, chevronNumber), isChevronBackRaised(stargate, chevronNumber));
		renderOuterChevronFront(stack, consumer, source, light, isChevronLowered(stargate, chevronNumber));
		renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();
	}
	
	protected void renderChevronLight(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isFrontRaised, boolean isBackRaised)
	{
		renderChevronLightFront(stack, consumer, source, combinedLight, isFrontRaised);
		renderChevronLightCenter(stack, consumer, source, combinedLight, isFrontRaised, isBackRaised);
		renderChevronLightBack(stack, consumer, source, combinedLight, isBackRaised);
	}
	
	protected void renderChevronLightFront(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isRaised)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		float yOffset = isRaised ? 2F / 16 : 0;
		//Light Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				48F/64, 17F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				48F/64, 13F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				54F/64, 13F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				54F/64, 17F/64);
		
		//Light Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				48F/64, 17F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				49.5F/64, 24F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				52.5F/64, 24F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				54F/64, 17F/64);

		//Light Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				48F/64, 24F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				48F/64, 17F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				44F/64, 17F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				44F/64, 24F/64);

		//Light Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				54F/64, 17F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				54F/64, 24F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				58F/64, 24F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				58F/64, 17F/64);

		//Light Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				54F/64, 13F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				54F/64, 17F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_Z_OFFSET,
				58F/64, 17F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_FRONT_Z_OFFSET,
				58F/64, 13F/64);
		
		if(isRaised)
		{
			//Light Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
					CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					CHEVRON_LIGHT_Z_OFFSET,
					58F/64, 17F/64,
					
					CHEVRON_LIGHT_BOTTOM_CENTER,
					-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					CHEVRON_LIGHT_Z_OFFSET,
					59.5F/64, 24F/64,
					
					-CHEVRON_LIGHT_BOTTOM_CENTER,
					-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					CHEVRON_LIGHT_Z_OFFSET,
					62.5F/64, 24F/64,
					
					-CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					CHEVRON_LIGHT_Z_OFFSET,
					64F/64, 17F/64);
		}
	}
	
	protected void renderChevronLightCenter(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isFrontRaised, boolean isBackRaised)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Light Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				CHEVRON_LIGHT_Z_OFFSET,
				51F/64, 11F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				-CHEVRON_LIGHT_Z_OFFSET,
				51F/64, 12F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				-CHEVRON_LIGHT_Z_OFFSET,
				57F/64, 12F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				CHEVRON_LIGHT_Z_OFFSET,
				57F/64, 11F/64);
		
		if(isFrontRaised)
		{
			//Light Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER,
					CHEVRON_LIGHT_Z_OFFSET,
					51F/64, 12F/64,
					
					-CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
					CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
					CHEVRON_LIGHT_Z_OFFSET,
					(51F + 1.5F * ((CHEVRON_LIGHT_HEIGHT - CHEVRON_DIVIDER_HEIGHT) / CHEVRON_LIGHT_HEIGHT))/64, 13F/64,
					
					CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
					CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
					CHEVRON_LIGHT_Z_OFFSET,
					(57F - 1.5F * ((CHEVRON_LIGHT_HEIGHT - CHEVRON_DIVIDER_HEIGHT) / CHEVRON_LIGHT_HEIGHT))/64, 13F/64,
					
					CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER,
					CHEVRON_LIGHT_Z_OFFSET,
					57F/64, 12F/64);
		}

		//Light Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
				CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				51F/64, 13F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				CHEVRON_LIGHT_Z_OFFSET,
				51F/64, 12F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				-CHEVRON_LIGHT_Z_OFFSET,
				50F/64, 12F/64,
				
				-CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
				CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
				-CHEVRON_LIGHT_Z_OFFSET,
				50F/64, 13F/64);

		//Light Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				CHEVRON_LIGHT_Z_OFFSET,
				57F/64, 12F/64,
				
				CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
				CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				57F/64, 13F/64,
				
				CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
				CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
				-CHEVRON_LIGHT_Z_OFFSET,
				58F/64, 13F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER,
				-CHEVRON_LIGHT_Z_OFFSET,
				58F/64, 12F/64);
		
		if(isBackRaised)
		{
			//Light Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
					CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER,
					-CHEVRON_LIGHT_Z_OFFSET,
					58F/64, 12F/64,
					
					CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
					CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
					-CHEVRON_LIGHT_Z_OFFSET,
					(58F + 1.5F * ((CHEVRON_LIGHT_HEIGHT - CHEVRON_DIVIDER_HEIGHT) / CHEVRON_LIGHT_HEIGHT))/64, 13F/64,
					
					-CHEVRON_LIGHT_DIVIDER_BOTTOM_CENTER,
					CHEVRON_LIGHT_DIVIDER_BOTTOM_HEIGHT,
					-CHEVRON_LIGHT_Z_OFFSET,
					(64F - 1.5F * ((CHEVRON_LIGHT_HEIGHT - CHEVRON_DIVIDER_HEIGHT) / CHEVRON_LIGHT_HEIGHT))/64, 13F/64,
					
					-CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER,
					-CHEVRON_LIGHT_Z_OFFSET,
					64F/64, 12F/64);
		}
	}
	
	protected void renderChevronLightBack(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isRaised)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		float yOffset = isRaised ? 2F / 16 : 0;
		//Light Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				48F/64, 5F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				48F/64, 0F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				54F/64, 0F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				54F/64, 5F/64);
		
		if(isRaised)
		{
			//Light Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					-CHEVRON_LIGHT_Z_OFFSET,
					47F/64, 4F/64,
					
					-CHEVRON_LIGHT_BOTTOM_CENTER,
					-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					-CHEVRON_LIGHT_Z_OFFSET,
					48.5F/64, 11F/64,
					
					CHEVRON_LIGHT_BOTTOM_CENTER,
					-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					-CHEVRON_LIGHT_Z_OFFSET,
					51.5F/64, 11F/64,
					
					CHEVRON_LIGHT_TOP_CENTER,
					CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
					-CHEVRON_LIGHT_Z_OFFSET,
					53F/64, 4F/64);
		}

		//Light Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				48F/64, 11F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				48F/64, 4F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				44F/64, 4F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				44F/64, 11F/64);

		//Light Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				53F/64, 4F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				53F/64, 11F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				58F/64, 11F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				58F/64, 4F/64);

		//Light Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				54F/64, 0F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				54F/64, 4F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				58F/64, 4F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				-CHEVRON_LIGHT_Z_OFFSET,
				58F/64, 0F/64);
		
		//Light Back
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				58F/64, 4F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				59.5F/64, 11F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				-CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				62.5F/64, 11F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_HEIGHT_CENTER + yOffset,
				CHEVRON_LIGHT_BACK_Z_OFFSET,
				64F/64, 4F/64);
	}
	
	protected void renderOuterChevronFront(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isOpen)
	{
		float yOffset = isOpen ? -2F / 16 : 0;
		renderCenterOuterChevron(stack, consumer, source, combinedLight, isOpen, yOffset);
		renderLeftOuterChevron(stack, consumer, source, combinedLight, isOpen, yOffset);
		renderRightOuterChevron(stack, consumer, source, combinedLight, isOpen, yOffset);
	}
	
	protected void renderCenterOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isOpen, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		//Center Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				54F/64, 43F/64,
				
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 44F/64,
				
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				56F/64, 44F/64,
				
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				56F/64, 43F/64);
		
		//Center Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				(55F - OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS * 16)/64, 43F/64,
				
				0F / 16,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				55F/64, 45F/64,
				
				0F / 16,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				55F/64, 45F/64,
				
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				(55F + OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS * 16)/64, 43F/64);
		
		//Center Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				53F/64, 45F/64,
				
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				53F/64, 46F/64,
				
				OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				57F/64, 46F/64,
				
				OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				57F/64, 45F/64);
	}
	
	protected void renderLeftOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isOpen, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Left Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				47F/64, 35F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 36F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 36F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				50F/64, 35F/64);
		
		//Left Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 36F/64,
				
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				52F/64, 45F/64,
				
				0F / 16,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 45F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				50F/64, 36F/64);
		
		//Left Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 1, 0,
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 36F/64,
				
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				54F/64, 43F/64,
				
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				55F/64, 43F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				55F/64, 36F/64);
		
		//Left Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				46F/64, 36F/64,
				
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				46F/64, 45F/64,
				
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 45F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				47F/64, 36F/64);
	}
	
	protected void renderRightOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean isOpen, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Right Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				60F/64, 35F/64,
				
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				60F/64, 36F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				OUTER_CHEVRON_Z_OFFSET,
				63F/64, 36F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
				STARGATE_RING_OFFSET,
				63F/64, 35F/64);
		
		//Right Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			60F/64, 36F/64,
			
			0F / 16,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			56F/64, 45F/64,
			
			OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			58F/64, 45F/64,
			
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 36F/64);
		
		//Right Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 36F/64,
			
			OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			63F/64, 45F/64,
			
			OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
			-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
			STARGATE_RING_OFFSET,
			64F/64, 45F/64,
			
			OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
			STARGATE_RING_OFFSET,
			64F/64, 36F/64);
		
		//Right Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 1, 0,
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
			STARGATE_RING_OFFSET,
			55F/64, 36F/64,
			
			OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
			STARGATE_RING_OFFSET,
			55F/64, 43F/64,
			
			OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			56F/64, 43F/64,
			
			OUTER_CHEVRON_TOP_OFFSET,
			OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET + yOffset,
			OUTER_CHEVRON_Z_OFFSET,
			56F/64, 36F/64);
	}
	
	protected void renderOuterChevronBack(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		renderBackCenterOuterChevron(stack, consumer, source, combinedLight);
		renderLeftOuterChevron(stack, consumer, source, combinedLight);
		renderRightOuterChevron(stack, consumer, source, combinedLight);
	}
	
	protected void renderBackCenterOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		//Center Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				54F/64, 32F/64,
				
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				54F/64, 33F/64,
				
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				56F/64, 33F/64,
				
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				56F/64, 32F/64);
		
		//Center Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				0F / 16,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				55F/64, 34F/64,
				
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				(55F - OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS * 16)/64, 32F/64,
				
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				(55F + OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS * 16)/64, 32F/64,
				
				0F / 16,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				55F/64, 34F/64);
		
		//Center Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				53F/64, 34F/64,
				
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				53F/64, 35F/64,
				
				OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				57F/64, 35F/64,
				
				OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				57F/64, 34F/64);
	}
	
	protected void renderLeftOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Left Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				47F/64, 24F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				47F/64, 25F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				50F/64, 25F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				50F/64, 24F/64);
		
		//Left Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				52F/64, 34F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				47F/64, 25F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				50F/64, 25F/64,
				
				0F / 16,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				54F/64, 34F/64);
		
		//Left Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				54F/64, 32F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				54F/64, 25F/64,
				
				-OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				55F/64, 25F/64,
				
				-OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				55F/64, 32F/64);
		
		//Left Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				46F/64, 34F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				46F/64, 25F/64,
				
				-(OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS),
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				47F/64, 25F/64,
				
				-OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				47F/64, 34F/64);
	}
	
	protected void renderRightOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Right Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				60F/64, 24F/64,
				
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				60F/64, 25F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				63F/64, 25F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				63F/64, 24F/64);
		
		//Right Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				0F / 16,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				56F/64, 34F/64,
				
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				60F/64, 25F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				63F/64, 25F/64,
				
				OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				58F/64, 34F/64);
		
		//Right Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
				OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				63F/64, 34F/64,
				
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				63F/64, 25F/64,
			
				OUTER_CHEVRON_TOP_OFFSET + OUTER_CHEVRON_SIDE_TOP_THICKNESS,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				64F/64, 25F/64,
			
				OUTER_CHEVRON_SIDE_BOTTOM_THICKNESS,
				-OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				64F/64, 34F/64);
		
		//Right Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				55F/64, 32F/64,
				
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-STARGATE_RING_OFFSET,
				55F/64, 25F/64,
			
				OUTER_CHEVRON_TOP_OFFSET,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_SIDE_HEIGHT + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				56F/64, 25F/64,
			
				OUTER_CHEVRON_UPPER_BOTTOM_CENTER,
				OUTER_CHEVRON_BOTTOM_HEIGHT_CENTER + OUTER_CHEVRON_Y_OFFSET,
				-OUTER_CHEVRON_Z_OFFSET,
				56F/64, 32F/64);
	}
	
	protected void renderOuterRing(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		for(int j = 0; j < DEFAULT_SIDES; j++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(j * -DEFAULT_ANGLE));
			Matrix4f matrix4 = stack.last().pose();
			Matrix3f matrix3 = stack.last().normal();
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_OUTER_CENTER * 16) / 64, (10.5F - STARGATE_EDGE_TO_CUTOUT_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_STOP_CENTER, 
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_STOP_CENTER * 16) / 64, (10.5F + STARGATE_EDGE_TO_CUTOUT_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_STOP_CENTER,
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_STOP_CENTER * 16) / 64, (10.5F + STARGATE_EDGE_TO_CUTOUT_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_OUTER_CENTER * 16) / 64, (10.5F - STARGATE_EDGE_TO_CUTOUT_HEIGHT/2 * 16) / 64);
			
			//Front 2
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_START_CENTER * 16) / 64, (29.5F - STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_INNER_CENTER * 16) / 64, (29.5F + STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_INNER_CENTER * 16) / 64, (29.5F + STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_START_CENTER * 16) / 64, (29.5F - STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64);
			
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_OUTER_CENTER * 16) / 64, (23 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_INNER_CENTER * 16) / 64, (23 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_INNER_CENTER * 16) / 64, (23 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_OUTER_CENTER * 16) / 64, (23 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Outside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_OUTER_CENTER, 
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_OUTER_CENTER * 16) / 64, 0,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_OUTER_CENTER * 16) / 64, 7F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_OUTER_CENTER * 16) / 64, 7F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_OUTER_CENTER * 16) / 64, 0);
			
			//Inside Stop - This will essentially be just one pixel thick
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					STARGATE_RING_STOP_CENTER,
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET - 1F / 16,
					(15 - STARGATE_RING_INNER_CENTER * 16) / 64, 14F / 64,
					
					STARGATE_RING_STOP_CENTER, 
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_CENTER * 16) / 64, 15F / 64,
					
					-STARGATE_RING_STOP_CENTER,
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_CENTER * 16) / 64, 15F / 64,
					
					-STARGATE_RING_STOP_CENTER,
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET - 1F / 16,
					(15 + STARGATE_RING_INNER_CENTER * 16) / 64, 14F / 64);
			
			//Inside Start - This will essentially be just one pixel thick
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET - 1F / 16,
					(15 - STARGATE_RING_INNER_CENTER * 16) / 64, 31F / 64,
					
					-STARGATE_RING_START_CENTER, 
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_CENTER * 16) / 64, 32F / 64,
					
					STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_CENTER * 16) / 64, 32F / 64,
					
					STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET - 1F / 16,
					(15 + STARGATE_RING_INNER_CENTER * 16) / 64, 31F / 64);
			
			//Inside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(8F * (j % 4) + 4 - STARGATE_RING_INNER_CENTER * 16) / 64, (38.5F - STARGATE_RING_THICKNESS/2) / 64,
					
					STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(8F * (j % 4) + 4 - STARGATE_RING_INNER_CENTER * 16) / 64, (38.5F + STARGATE_RING_THICKNESS/2) / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(8F * (j % 4) + 4 + STARGATE_RING_INNER_CENTER * 16) / 64, (38.5F + STARGATE_RING_THICKNESS/2) / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(8F * (j % 4) + 4 + STARGATE_RING_INNER_CENTER * 16) / 64, (38.5F - STARGATE_RING_THICKNESS/2) / 64);
			stack.popPose();
		}
	}
	
	//============================================================================================
	//********************************************Ring********************************************
	//============================================================================================
	
	protected void renderSymbolRing(StargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		for(int j = 0; j < this.symbolSides; j++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(j * -this.symbolAngle + rotation));
			Matrix4f matrix4 = stack.last().pose();
			Matrix3f matrix3 = stack.last().normal();
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-stargateSymbolRingOuterCenter,
					STARGATE_SYMBOL_RING_OUTER_HEIGHT,
					STARGATE_RING_OFFSET - 1F/16,
					(4 - stargateSymbolRingOuterCenter * 16) / 64, (46 - STARGATE_SYMBOL_RING_HEIGHT/2 * 16) / 64,
					
					-stargateSymbolRingInnerCenter, 
					STARGATE_SYMBOL_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET - 1F/16,
					(4 - stargateSymbolRingInnerCenter * 16) / 64, (46 + STARGATE_SYMBOL_RING_HEIGHT/2 * 16) / 64,
					
					stargateSymbolRingInnerCenter,
					STARGATE_SYMBOL_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET - 1F/16,
					(4 + stargateSymbolRingInnerCenter * 16) / 64, (46 + STARGATE_SYMBOL_RING_HEIGHT/2 * 16) / 64,
					
					stargateSymbolRingOuterCenter,
					STARGATE_SYMBOL_RING_OUTER_HEIGHT,
					STARGATE_RING_OFFSET - 1F/16,
					(4 + stargateSymbolRingOuterCenter * 16) / 64, (46 - STARGATE_SYMBOL_RING_HEIGHT/2 * 16) / 64);
			
			stack.popPose();
		}
		
		//Dividers
		for(int j = 0; j < this.symbolSides; j++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(j * -this.symbolAngle - this.symbolAngle/2 + rotation));
			Matrix4f matrix4 = stack.last().pose();
			Matrix3f matrix3 = stack.last().normal();
			
			//Divider Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-DIVIDER_CENTER,
					DIVIDER_Y_CENTER + DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					(9.5F - DIVIDER_CENTER * 16) / 64, (46 - DIVIDER_HEIGHT/2 * 16) / 64,
					
					-DIVIDER_CENTER, 
					DIVIDER_Y_CENTER - DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					(9.5F - DIVIDER_CENTER * 16) / 64, (46 + DIVIDER_HEIGHT/2 * 16) / 64,
					
					DIVIDER_CENTER,
					DIVIDER_Y_CENTER - DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					(9.5F + DIVIDER_CENTER * 16) / 64, (46 + DIVIDER_HEIGHT/2 * 16) / 64,
					
					DIVIDER_CENTER,
					DIVIDER_Y_CENTER + DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					(9.5F + DIVIDER_CENTER * 16) / 64, (46 - DIVIDER_HEIGHT/2 * 16) / 64);
			
			//Divider Left
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
					-DIVIDER_CENTER,
					DIVIDER_Y_CENTER + DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16,
					(9F - DIVIDER_OFFSET * 16) / 64, (46 - DIVIDER_HEIGHT/2 * 16) / 64,
					
					-DIVIDER_CENTER, 
					DIVIDER_Y_CENTER - DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16,
					(9F - DIVIDER_OFFSET * 16) / 64, (46 + DIVIDER_HEIGHT/2 * 16) / 64,
					
					-DIVIDER_CENTER,
					DIVIDER_Y_CENTER - DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					9F / 64, (46 + DIVIDER_HEIGHT/2 * 16) / 64,
					
					-DIVIDER_CENTER,
					DIVIDER_Y_CENTER + DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					9F / 64, (46 - DIVIDER_HEIGHT/2 * 16) / 64);
			
			//Divider Right
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
					DIVIDER_CENTER,
					DIVIDER_Y_CENTER + DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					10F / 64, (46 - DIVIDER_HEIGHT/2 * 16) / 64,
					
					DIVIDER_CENTER, 
					DIVIDER_Y_CENTER - DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16 + DIVIDER_OFFSET,
					10F / 64, (46 + DIVIDER_HEIGHT/2 * 16) / 64,
					
					DIVIDER_CENTER,
					DIVIDER_Y_CENTER - DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16,
					(10F + DIVIDER_OFFSET * 16) / 64, (46 + DIVIDER_HEIGHT/2 * 16) / 64,
					
					DIVIDER_CENTER,
					DIVIDER_Y_CENTER + DIVIDER_HEIGHT/2,
					STARGATE_RING_OFFSET - 1F/16,
					(10F + DIVIDER_OFFSET * 16) / 64, (46 - DIVIDER_HEIGHT/2 * 16) / 64);
			
			stack.popPose();
		}
		
		this.renderSymbols(stargate, stack, consumer, source, combinedLight, rotation);
	}
	
	protected void renderSymbols(StargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		//Front Symbols
		for(int j = 0; j < this.symbolSides; j++)
		{
			renderSymbol(stargate, stack, consumer, source, combinedLight, j, j, rotation, getSymbolColor(stargate, false));
		}
	}
	
	protected void renderSymbol(StargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, 
		int symbolNumber, int symbolRendered, float rotation,
		Stargate.RGBA symbolColor)
	{
		consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbolTexture(stargate, symbolRendered)));
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(symbolNumber * -this.symbolAngle + rotation));
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				symbolColor.getRed(), symbolColor.getGreen(), symbolColor.getBlue(), symbolColor.getAlpha(), 
				-stargateSymbolRingOuterCenter,
				STARGATE_SYMBOL_RING_OUTER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				(8 - stargateSymbolRingOuterCenter * 32) / 16, (8 - STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16,
				
				-stargateSymbolRingInnerCenter, 
				STARGATE_SYMBOL_RING_INNER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				(8 - stargateSymbolRingInnerCenter * 32) / 16, (8 + STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16,
				
				stargateSymbolRingInnerCenter,
				STARGATE_SYMBOL_RING_INNER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				(8 + stargateSymbolRingInnerCenter * 32) / 16, (8 + STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16,
				
				stargateSymbolRingOuterCenter,
				STARGATE_SYMBOL_RING_OUTER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				(8 + stargateSymbolRingOuterCenter * 32) / 16, (8 - STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16);
		
		stack.popPose();
	}
}
