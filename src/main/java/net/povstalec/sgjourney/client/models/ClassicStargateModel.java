package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;

public class ClassicStargateModel extends AbstractStargateModel
{
	private static final ResourceLocation STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_stargate.png");
	private static final ResourceLocation ENGAGED_STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_stargate_lit.png");

	protected static final float DEFAULT_DISTANCE = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	protected static final int SPINNING_SIDES = 39;
	private static final float ANGLE = 360F / SPINNING_SIDES;
	
	protected static final float STARGATE_RING_THICKNESS = 8;
	protected static final float STARGATE_RING_OFFSET = STARGATE_RING_THICKNESS / 2 / 16;
	
	protected static final float SPINNY_RING_THICKNESS = 7;
	protected static final float SPINNY_RING_OFFSET = SPINNY_RING_THICKNESS / 2 / 16;
	protected static final float SYMBOL_OFFSET = SPINNY_RING_OFFSET + 0.001F;

	protected static final float LOCKED_CHEVRON_OFFSET = 4F / 16;
	
	protected static final float CHEVRON_LIGHT_THICKNESS = 2F / 16;
	protected static final float CHEVRON_LIGHT_UPPER_WIDTH = 7F / 16;
	protected static final float CHEVRON_LIGHT_LOWER_WIDTH = 2F / 16;
	protected static final float CHEVRON_LIGHT_HEIGHT = 6F / 16;
	
	protected static final float CHEVRON_LIGHT_Z_OFFSET = STARGATE_RING_OFFSET + CHEVRON_LIGHT_THICKNESS;
	protected static final float CHEVRON_LIGHT_UPPER_X_OFFSET = CHEVRON_LIGHT_UPPER_WIDTH / 2;
	protected static final float CHEVRON_LIGHT_LOWER_X_OFFSET = CHEVRON_LIGHT_LOWER_WIDTH / 2;
	protected static final float CHEVRON_LIGHT_HEIGHT_Y_OFFSET = CHEVRON_LIGHT_HEIGHT / 2;
	
	protected static final float OUTER_CHEVRON_THICKNESS = 3F / 16;
	protected static final float OUTER_CHEVRON_BOTTOM_WIDTH = 6F / 16;
	protected static final float OUTER_CHEVRON_BOTTOM_HEIGHT = 3F / 16;
	protected static final float OUTER_CHEVRON_SIDE_HEIGHT = 9F / 16;
	protected static final float SIN_25_5 = (float) Math.sin(Math.toRadians(22.5));
	protected static final float COS_25_5 = (float) Math.cos(Math.toRadians(22.5));
	protected static final float OUTER_CHEVRON_ANGLE = (float) Math.atan(3);
	
	protected static final float OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION = SIN_25_5 * OUTER_CHEVRON_SIDE_HEIGHT;
	protected static final float OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION = COS_25_5 * OUTER_CHEVRON_SIDE_HEIGHT;
	protected static final float OUTER_CHEVRON_SIDE_WIDTH = 3F / 16;
	protected static final float OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION = COS_25_5 * OUTER_CHEVRON_SIDE_WIDTH;
	protected static final float OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION = SIN_25_5 * OUTER_CHEVRON_SIDE_WIDTH;
	
	protected static final float OUTER_CHEVRON_Z_OFFSET = STARGATE_RING_OFFSET + CHEVRON_LIGHT_THICKNESS + 0.5F / 16;
	protected static final float OUTER_CHEVRON_BOTTOM_X_OFFSET = OUTER_CHEVRON_BOTTOM_WIDTH / 2;
	protected static final float OUTER_CHEVRON_BOTTOM_Y_OFFSET = OUTER_CHEVRON_BOTTOM_HEIGHT / 2;
	
	protected static final  float[] stargateRingOuter = SGJourneyModel.shrinkingCoordinates(DEFAULT_SIDES, 3.5F, DEFAULT_DISTANCE);
	protected static final  float[] stargateRingInner = SGJourneyModel.shrinkingCoordinates(DEFAULT_SIDES, 3F, DEFAULT_DISTANCE);

	protected static final  float[] spinnyRingOuter = SGJourneyModel.shrinkingCoordinates(SPINNING_SIDES, 3.05F, DEFAULT_DISTANCE);
	protected static final  float[] spinnyRingInner  = SGJourneyModel.shrinkingCoordinates(SPINNING_SIDES, 2.5F, DEFAULT_DISTANCE);
	
	protected static final float STARGATE_RING_OUTER_LENGTH = Math.abs(stargateRingOuter[0] * 16 - stargateRingOuter[2] * 16);
	protected static final float STARGATE_RING_INNER_LENGTH = Math.abs(stargateRingInner[0] * 16 - stargateRingInner[2] * 16);
	protected static final float STARGATE_RING_HEIGHT = Math.abs(stargateRingOuter[1] * 16 - stargateRingInner[1] * 16);
	
	protected static final float SPINNY_RING_OUTER_LENGTH = Math.abs(spinnyRingOuter[0] * 16 - spinnyRingOuter[2] * 16);
	protected static final float SPINNY_RING_INNER_LENGTH = Math.abs(spinnyRingInner[0] * 16 - spinnyRingInner[2] * 16);
	protected static final float SPINNY_RING_HEIGHT = Math.abs(spinnyRingOuter[1] * 16 - spinnyRingInner[1] * 16);
	
	
	private float rotation = 0F;
	
	public ClassicStargateModel()
	{
		super("classic");
	}
	
	public void renderStargate(ClassicStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(STARGATE_TEXTURE));
		renderOuterRing(stack, consumer, source, combinedLight);
		renderSpinnyRing(stargate, stack, consumer, source, combinedLight);
		
		renderChevrons(stargate, stack, source, combinedLight, stargate.chevronsRendered());
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	protected void renderOuterRing(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		for(int j = 0; j < DEFAULT_SIDES; j++)
		{
			stack.mulPose(Axis.ZP.rotationDegrees(10));
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					stargateRingOuter[0],
					stargateRingOuter[1],
					STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_OUTER_LENGTH/2) / 64, (12 - STARGATE_RING_HEIGHT/2) / 64,
					
					stargateRingInner[0], 
					stargateRingInner[1],
					STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_INNER_LENGTH/2) / 64, (12 + STARGATE_RING_HEIGHT/2) / 64,
					
					stargateRingInner[2],
					stargateRingInner[3],
					STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_INNER_LENGTH/2) / 64, (12 + STARGATE_RING_HEIGHT/2) / 64,
					
					stargateRingOuter[2],
					stargateRingOuter[3],
					STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_OUTER_LENGTH/2) / 64, (12 - STARGATE_RING_HEIGHT/2) / 64);
			
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					stargateRingOuter[2],
					stargateRingOuter[3],
					-STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_OUTER_LENGTH/2) / 64, (12 - STARGATE_RING_HEIGHT/2) / 64,
					
					stargateRingInner[2],
					stargateRingInner[3],
					-STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_LENGTH/2) / 64, (12 + STARGATE_RING_HEIGHT/2) / 64,
					
					stargateRingInner[0], 
					stargateRingInner[1],
					-STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_LENGTH/2) / 64, (12 + STARGATE_RING_HEIGHT/2) / 64,
					
					stargateRingOuter[0],
					stargateRingOuter[1],
					-STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_OUTER_LENGTH/2) / 64, (12 - STARGATE_RING_HEIGHT/2) / 64);
			
			//Outside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					stargateRingOuter[0], 
					stargateRingOuter[1],
					-STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_OUTER_LENGTH/2) / 64, 0,
					
					stargateRingOuter[0],
					stargateRingOuter[1],
					STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_OUTER_LENGTH/2) / 64, 8F / 64,
					
					stargateRingOuter[2],
					stargateRingOuter[3],
					STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_OUTER_LENGTH/2) / 64, 8F / 64,
					
					stargateRingOuter[2],
					stargateRingOuter[3],
					-STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_OUTER_LENGTH/2) / 64, 0);
			
			//Inside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					stargateRingInner[2],
					stargateRingInner[3],
					-STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_LENGTH/2) / 64, 0,
					
					stargateRingInner[2], 
					stargateRingInner[3],
					STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_LENGTH/2) / 64, 8F / 64,
					
					stargateRingInner[0],
					stargateRingInner[1],
					STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_LENGTH/2) / 64, 8F / 64,
					
					stargateRingInner[0],
					stargateRingInner[1],
					-STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_LENGTH/2) / 64, 0);
		}
	}
	
	protected void renderSpinnyRing(ClassicStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		for(int j = 0; j < SPINNING_SIDES; j++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(-ANGLE * j + rotation));
			matrix4 = stack.last().pose();
			matrix3 = stack.last().normal();
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					spinnyRingOuter[0],
					spinnyRingOuter[1],
					SPINNY_RING_OFFSET,
					(24.5F - SPINNY_RING_OUTER_LENGTH/2) / 64, (11.5F - SPINNY_RING_HEIGHT/2) / 64,
					
					spinnyRingInner[0],
					spinnyRingInner[1],
					SPINNY_RING_OFFSET,
					(24.5F - SPINNY_RING_INNER_LENGTH/2) / 64, (11.5F + SPINNY_RING_HEIGHT/2) / 64,
					
					spinnyRingInner[2], 
					spinnyRingInner[3],
					SPINNY_RING_OFFSET,
					(24.5F + SPINNY_RING_INNER_LENGTH/2) / 64, (11.5F + SPINNY_RING_HEIGHT/2) / 64,
					
					spinnyRingOuter[2],
					spinnyRingOuter[3],
					SPINNY_RING_OFFSET,
					(24.5F + SPINNY_RING_OUTER_LENGTH/2) / 64, (11.5F - SPINNY_RING_HEIGHT/2) / 64);
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					spinnyRingOuter[2], 
					spinnyRingOuter[3],
					-SPINNY_RING_OFFSET,
					(33.5F - SPINNY_RING_OUTER_LENGTH/2) / 64, (11.5F - SPINNY_RING_HEIGHT/2) / 64,
					
					spinnyRingInner[2],
					spinnyRingInner[3],
					-SPINNY_RING_OFFSET,
					(33.5F - SPINNY_RING_INNER_LENGTH/2) / 64, (11.5F + SPINNY_RING_HEIGHT/2) / 64,
					
					spinnyRingInner[0],
					spinnyRingInner[1],
					-SPINNY_RING_OFFSET,
					(33.5F + SPINNY_RING_INNER_LENGTH/2) / 64, (11.5F + SPINNY_RING_HEIGHT/2) / 64,
					
					spinnyRingOuter[0],
					spinnyRingOuter[1],
					-SPINNY_RING_OFFSET,
					(33.5F + SPINNY_RING_OUTER_LENGTH/2) / 64, (11.5F - SPINNY_RING_HEIGHT/2) / 64);
			//Bottom
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					spinnyRingInner[0],
					spinnyRingInner[1],
					SPINNY_RING_OFFSET,
					(23.5F - SPINNY_RING_INNER_LENGTH/2) / 64, 0,
					
					spinnyRingInner[0],
					spinnyRingInner[1],
					-SPINNY_RING_OFFSET,
					(23.5F - SPINNY_RING_INNER_LENGTH/2) / 64, 7F / 64,
					
					spinnyRingInner[2], 
					spinnyRingInner[3],
					-SPINNY_RING_OFFSET,
					(23.5F + SPINNY_RING_INNER_LENGTH/2) / 64, 7F / 64,
					
					spinnyRingInner[2],
					spinnyRingInner[3],
					SPINNY_RING_OFFSET,
					(23.5F + SPINNY_RING_INNER_LENGTH/2) / 64, 0);
			stack.popPose();
		}

		//Front Symbols
		for(int j = 0; j < SPINNING_SIDES; j++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(j * -ANGLE + rotation));
			matrix4 = stack.last().pose();
			matrix3 = stack.last().normal();
			VertexConsumer symbolConsumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbolTexture(stargate, j)));
			SGJourneyModel.createQuad(symbolConsumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					0.0F/255.0F, 109.0F/255.0F, 121.0F/255.0F, 1.0F, 
					-4F/16,
					spinnyRingInner[1] + 9F/16,
					SYMBOL_OFFSET,
					0, 0,
					
					-4F/16,
					spinnyRingInner[1],
					SYMBOL_OFFSET,
					0, 1,
					
					4F/16, 
					spinnyRingInner[3],
					SYMBOL_OFFSET,
					1, 1,
					
					4F/16,
					spinnyRingInner[3] + 9F/16,
					SYMBOL_OFFSET,
					1, 0);
			stack.popPose();
		}
	}
	
	protected void renderChevrons(ClassicStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int chevronsActive)
	{
		renderPrimaryChevron(stargate, stack, source, combinedLight);
		for(int i = 1; i < 9; i++)
		{
			renderChevron(stargate, stack, source, combinedLight, i, i <= chevronsActive);
		}
	}
	
	protected void renderPrimaryChevron(ClassicStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight)
	{
		boolean isLocked = stargate.isConnected() && (stargate.isDialingOut() || stargate.getKawooshTickCount() > 0);
		float subtracted = isLocked ? LOCKED_CHEVRON_OFFSET + 1F/16 :  1F/16;
		
		stack.pushPose();
		stack.translate(0, 3.5F - subtracted, 0);

		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(STARGATE_TEXTURE));
		renderChevronLight(stack, consumer, source, combinedLight, isLocked);
		renderOuterChevron(stack, consumer, source, combinedLight, isLocked);
		
		if(isLocked)
		{
			consumer = source.getBuffer(SGJourneyRenderTypes.stargateChevron(ENGAGED_STARGATE_TEXTURE));
			renderChevronLight(stack, consumer, source, 15728864, isLocked);
			renderOuterChevron(stack, consumer, source, 15728864, isLocked);
		}
		
		stack.popPose();
	}
	
	protected void renderChevron(ClassicStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int chevronNumber, boolean isEngaged)
	{
		int chevron = stargate.getEngagedChevrons()[chevronNumber];
		float subtracted = isEngaged ? LOCKED_CHEVRON_OFFSET + 1F/16 :  1F/16;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(-40 * chevron));
		stack.translate(0, 3.5F - subtracted, 0);

		
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(STARGATE_TEXTURE));
		renderChevronLight(stack, consumer, source, combinedLight, isEngaged);
		renderOuterChevron(stack, consumer, source, combinedLight, isEngaged);
		
		if(isEngaged)
		{
			consumer = source.getBuffer(SGJourneyRenderTypes.stargateChevron(ENGAGED_STARGATE_TEXTURE));
			renderChevronLight(stack, consumer, source, 15728864, isEngaged);
			renderOuterChevron(stack, consumer, source, 15728864, isEngaged);
		}
		
		stack.popPose();
	}
	
	protected void renderChevronLight(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, 
			int combinedLight, boolean isLocked)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		//Light Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-CHEVRON_LIGHT_UPPER_X_OFFSET,
				CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				32F/64, 21F/64,
				
				-CHEVRON_LIGHT_LOWER_X_OFFSET,
				-CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				34F/64, 26F/64,
				
				CHEVRON_LIGHT_LOWER_X_OFFSET,
				-CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				36F/64, 26F/64,
				
				CHEVRON_LIGHT_UPPER_X_OFFSET,
				CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				38F/64, 21F/64);
		
		//Light Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-CHEVRON_LIGHT_UPPER_X_OFFSET,
				CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET - CHEVRON_LIGHT_THICKNESS,
				32F/64, 19F/64,
				
				-CHEVRON_LIGHT_UPPER_X_OFFSET,
				CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				32F/64, 21F/64,
				
				CHEVRON_LIGHT_UPPER_X_OFFSET,
				CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				38F/64, 21F/64,
				
				CHEVRON_LIGHT_UPPER_X_OFFSET,
				CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET - CHEVRON_LIGHT_THICKNESS,
				38F/64, 19F/64);

		if(!isLocked)
		{
			//Light Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					CHEVRON_LIGHT_UPPER_X_OFFSET,
					CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
					CHEVRON_LIGHT_Z_OFFSET - CHEVRON_LIGHT_THICKNESS,
					38F/64, 19F/64,
					
					CHEVRON_LIGHT_UPPER_X_OFFSET,
					CHEVRON_LIGHT_HEIGHT_Y_OFFSET - 3F/16,
					CHEVRON_LIGHT_Z_OFFSET - CHEVRON_LIGHT_THICKNESS,
					38F/64, 16F/64,
					
					-CHEVRON_LIGHT_UPPER_X_OFFSET,
					CHEVRON_LIGHT_HEIGHT_Y_OFFSET - 3F/16,
					CHEVRON_LIGHT_Z_OFFSET - CHEVRON_LIGHT_THICKNESS,
					32F/64, 16F/64,
					
					-CHEVRON_LIGHT_UPPER_X_OFFSET,
					CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
					CHEVRON_LIGHT_Z_OFFSET - CHEVRON_LIGHT_THICKNESS,
					32F/64, 19F/64);
		}
	}
	
	protected void renderOuterChevron(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, 
			int combinedLight, boolean isLocked)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		//Bottom Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-CHEVRON_LIGHT_LOWER_X_OFFSET,
				-CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				13F/64, 16.5F/64,
				
				CHEVRON_LIGHT_LOWER_X_OFFSET,
				-CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				OUTER_CHEVRON_Z_OFFSET,
				13F/64, 17F/64,
				
				CHEVRON_LIGHT_LOWER_X_OFFSET,
				-CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				15F/64, 17F/64,
				
				-CHEVRON_LIGHT_LOWER_X_OFFSET,
				-CHEVRON_LIGHT_HEIGHT_Y_OFFSET,
				CHEVRON_LIGHT_Z_OFFSET,
				15F/64, 16.5F/64);
		//Bottom Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-CHEVRON_LIGHT_LOWER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				13F/64, 17F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				12F/64, 20F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				16F/64, 20F/64,
				
				CHEVRON_LIGHT_LOWER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				15F/64, 17F/64);
		
		//Bottom Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				12F/64, 20F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				12F/64, 23F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				16F/64, 23F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				16F/64, 20F/64);
		
		renderLeftChevronSide(stack, source, consumer, matrix3.rotate(Axis.ZP.rotationDegrees(22.5F)), matrix4, combinedLight, isLocked);
		renderRightChevronSide(stack, source, consumer, matrix3.rotate(Axis.ZP.rotationDegrees(-22.5F)), matrix4, combinedLight, isLocked);
	}
	
	protected void renderLeftChevronSide(PoseStack stack, MultiBufferSource source, VertexConsumer consumer, Matrix3f matrix3, Matrix4f matrix4, 
			int combinedLight, boolean isLocked)
	{
		//Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				3F/64, 19F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				3F/64, 28F/64,
				
				-CHEVRON_LIGHT_LOWER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				6F/64, 26F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				6F/64, 19F/64);
		//Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				0, 19F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				0, 28F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				3F/64, 28F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				3F/64, 19F/64);
		//Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				6F/64, 19F/64,
				
				-CHEVRON_LIGHT_LOWER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				6F/64, 26F/64,
				
				-CHEVRON_LIGHT_LOWER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				9F/64, 26F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				9F/64, 19F/64);
		//Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				3F/64, 16F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				3F/64, 19F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				6F/64, 19F/64,
				
				-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				6F/64, 16F/64);
		if(!isLocked)
		{
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
					-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					9F/64, 19F/64,
					
					-CHEVRON_LIGHT_LOWER_X_OFFSET,
					OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					9F/64, 26F/64,
					
					-OUTER_CHEVRON_BOTTOM_X_OFFSET,
					-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					12F/64, 28F/64,
					
					-OUTER_CHEVRON_BOTTOM_X_OFFSET - OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
					-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					12F/64, 19F/64);
		}
	}
	
	protected void renderRightChevronSide(PoseStack stack, MultiBufferSource source, VertexConsumer consumer, Matrix3f matrix3, Matrix4f matrix4, 
			int combinedLight, boolean isLocked)
	{
		//Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION - OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				19F/64, 19F/64,
				
				CHEVRON_LIGHT_LOWER_X_OFFSET,
				OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				19F/64, 26F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				22F/64, 28F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				22F/64, 19F/64);
		//Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 0, 0,
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				22F/64, 19F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				22F/64, 28F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				25F/64, 28F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				25F/64, 19F/64);
		//Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 0, 0,
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION - OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				16F/64, 19F/64,
				
				CHEVRON_LIGHT_LOWER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET - OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				16F/64, 26F/64,
				
				CHEVRON_LIGHT_LOWER_X_OFFSET,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET - OUTER_CHEVRON_BOTTOM_Y_OFFSET),
				OUTER_CHEVRON_Z_OFFSET,
				19F/64, 26F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION - OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				19F/64, 19F/64);
		//Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION - OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				19F/64, 16F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION - OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				19F/64, 19F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET,
				22F/64, 19F/64,
				
				OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
				-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
				OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
				22F/64, 16F/64);
		if(!isLocked)
		{
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION,
					-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION,
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					25F/64, 19F/64,
					
					OUTER_CHEVRON_BOTTOM_X_OFFSET,
					-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					25F/64, 28F/64,
					
					CHEVRON_LIGHT_LOWER_X_OFFSET,
					OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET),
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					28F/64, 26F/64,
					
					OUTER_CHEVRON_BOTTOM_X_OFFSET + OUTER_CHEVRON_SIDE_HEIGHT_X_PROJECTION - OUTER_CHEVRON_SIDE_WIDTH_X_PROJECTION,
					-OUTER_CHEVRON_BOTTOM_Y_OFFSET - (CHEVRON_LIGHT_HEIGHT_Y_OFFSET + OUTER_CHEVRON_BOTTOM_Y_OFFSET) + OUTER_CHEVRON_SIDE_HEIGHT_Y_PROJECTION + OUTER_CHEVRON_SIDE_WIDTH_Y_PROJECTION,
					OUTER_CHEVRON_Z_OFFSET - OUTER_CHEVRON_THICKNESS,
					28F/64, 19F/64);
		}
	}
}
