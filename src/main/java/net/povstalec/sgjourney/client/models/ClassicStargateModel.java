package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClassicStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class ClassicStargateModel extends AbstractStargateModel<ClassicStargateEntity, ClassicStargateVariant>
{
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

	protected static final float STARGATE_RING_OUTER_RADIUS = DEFAULT_RADIUS;
	protected static final float STARGATE_RING_OUTER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_OUTER_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_OUTER_CENTER = STARGATE_RING_OUTER_LENGTH / 2;

	protected static final float STARGATE_RING_INNER_RADIUS = DEFAULT_RADIUS - 0.5F;
	protected static final float STARGATE_RING_INNER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_INNER_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_INNER_CENTER = STARGATE_RING_INNER_LENGTH / 2;

	protected static final float STARGATE_RING_HEIGHT = STARGATE_RING_OUTER_RADIUS - STARGATE_RING_INNER_RADIUS;

	protected static final float SPINNY_RING_OUTER_RADIUS = 3.05F;
	protected static final float SPINNY_RING_OUTER_LENGTH = SGJourneyModel.getUsedWidth(SPINNING_SIDES, SPINNY_RING_OUTER_RADIUS, DEFAULT_RADIUS);
	protected static final float SPINNY_RING_OUTER_CENTER = SPINNY_RING_OUTER_LENGTH / 2;

	protected static final float SPINNY_RING_INNER_RADIUS = 2.5F;
	protected static final float SPINNY_RING_INNER_LENGTH = SGJourneyModel.getUsedWidth(SPINNING_SIDES, SPINNY_RING_INNER_RADIUS, DEFAULT_RADIUS);
	protected static final float SPINNY_RING_INNER_CENTER = SPINNY_RING_INNER_LENGTH / 2;
	
	protected static final float SPINNY_RING_HEIGHT = SPINNY_RING_OUTER_RADIUS - SPINNY_RING_INNER_RADIUS;
	
	private float rotation = 0F;
	
	public ClassicStargateModel()
	{
		super((short) 39);
	}
	
	@Override
	public void renderRing(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		renderOuterRing(stack, consumer, source, combinedLight);
		renderSpinnyRing(stargate, stargateVariant, stack, consumer, source, combinedLight);
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	//============================================================================================
	//******************************************Rendering*****************************************
	//============================================================================================
	
	protected void renderOuterRing(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		for(int j = 0; j < DEFAULT_SIDES; j++)
		{
			stack.mulPose(Axis.ZP.rotationDegrees(DEFAULT_ANGLE));
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_OUTER_CENTER * 16) / 64, (12 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_INNER_CENTER * 16) / 64, (12 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_INNER_CENTER * 16) / 64, (12 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_OUTER_CENTER * 16) / 64, (12 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_OUTER_CENTER * 16) / 64, (12 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_CENTER * 16) / 64, (12 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_CENTER * 16) / 64, (12 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_OUTER_CENTER * 16) / 64, (12 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Outside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_OUTER_CENTER, 
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_OUTER_CENTER * 16) / 64, 0,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(5 - STARGATE_RING_OUTER_CENTER * 16) / 64, 8F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_OUTER_CENTER * 16) / 64, 8F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(5 + STARGATE_RING_OUTER_CENTER * 16) / 64, 0);
			
			//Inside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_CENTER * 16) / 64, 0,
					
					STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(15 - STARGATE_RING_INNER_CENTER * 16) / 64, 8F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_CENTER * 16) / 64, 8F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_RADIUS,
					-STARGATE_RING_OFFSET,
					(15 + STARGATE_RING_INNER_CENTER * 16) / 64, 0);
		}
	}
	
	protected void renderSpinnyRing(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
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
					-SPINNY_RING_OUTER_CENTER,
					SPINNY_RING_OUTER_RADIUS,
					SPINNY_RING_OFFSET,
					(24.5F - SPINNY_RING_OUTER_CENTER * 16) / 64, (11.5F - SPINNY_RING_HEIGHT/2 * 16) / 64,
					
					-SPINNY_RING_INNER_CENTER,
					SPINNY_RING_INNER_RADIUS,
					SPINNY_RING_OFFSET,
					(24.5F - SPINNY_RING_INNER_CENTER * 16) / 64, (11.5F + SPINNY_RING_HEIGHT/2 * 16) / 64,
					
					SPINNY_RING_INNER_CENTER, 
					SPINNY_RING_INNER_RADIUS,
					SPINNY_RING_OFFSET,
					(24.5F + SPINNY_RING_INNER_CENTER * 16) / 64, (11.5F + SPINNY_RING_HEIGHT/2 * 16) / 64,
					
					SPINNY_RING_OUTER_CENTER,
					SPINNY_RING_OUTER_RADIUS,
					SPINNY_RING_OFFSET,
					(24.5F + SPINNY_RING_OUTER_CENTER * 16) / 64, (11.5F - SPINNY_RING_HEIGHT/2 * 16) / 64);
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
					SPINNY_RING_OUTER_CENTER, 
					SPINNY_RING_OUTER_RADIUS,
					-SPINNY_RING_OFFSET,
					(33.5F - SPINNY_RING_OUTER_CENTER * 16) / 64, (11.5F - SPINNY_RING_HEIGHT/2 * 16) / 64,
					
					SPINNY_RING_INNER_CENTER,
					SPINNY_RING_INNER_RADIUS,
					-SPINNY_RING_OFFSET,
					(33.5F - SPINNY_RING_INNER_CENTER * 16) / 64, (11.5F + SPINNY_RING_HEIGHT/2 * 16) / 64,
					
					-SPINNY_RING_INNER_CENTER,
					SPINNY_RING_INNER_RADIUS,
					-SPINNY_RING_OFFSET,
					(33.5F + SPINNY_RING_INNER_CENTER * 16) / 64, (11.5F + SPINNY_RING_HEIGHT/2 * 16) / 64,
					
					-SPINNY_RING_OUTER_CENTER,
					SPINNY_RING_OUTER_RADIUS,
					-SPINNY_RING_OFFSET,
					(33.5F + SPINNY_RING_OUTER_CENTER * 16) / 64, (11.5F - SPINNY_RING_HEIGHT/2 * 16) / 64);
			//Bottom
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					-SPINNY_RING_INNER_CENTER,
					SPINNY_RING_INNER_RADIUS,
					SPINNY_RING_OFFSET,
					(23.5F - SPINNY_RING_INNER_CENTER * 16) / 64, 0,
					
					-SPINNY_RING_INNER_CENTER,
					SPINNY_RING_INNER_RADIUS,
					-SPINNY_RING_OFFSET,
					(23.5F - SPINNY_RING_INNER_CENTER * 16) / 64, 7F / 64,
					
					SPINNY_RING_INNER_CENTER, 
					SPINNY_RING_INNER_RADIUS,
					-SPINNY_RING_OFFSET,
					(23.5F + SPINNY_RING_INNER_CENTER * 16) / 64, 7F / 64,
					
					SPINNY_RING_INNER_CENTER,
					SPINNY_RING_INNER_RADIUS,
					SPINNY_RING_OFFSET,
					(23.5F + SPINNY_RING_INNER_CENTER * 16) / 64, 0);
			stack.popPose();
		}
		
		this.renderSymbols(stargate, stargateVariant, stack, consumer, source, combinedLight, rotation);
	}
	
	protected void renderSymbols(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		PointOfOrigin pointOfOrigin = getPointOfOrigin(stargate, stargateVariant);
		
		if(pointOfOrigin != null)
		{
			boolean pointOfOriginEngaged = false;
			if(stargateVariant.symbols().engageEncodedSymbols() && (!stargate.isConnected() || stargate.isDialingOut()))
				pointOfOriginEngaged = stargate.isConnected();
			else if(stargate.isConnected())
				pointOfOriginEngaged = stargateVariant.symbols().engageSymbolsOnIncoming();
			
			consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(pointOfOrigin)));
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, pointOfOriginEngaged) ? MAX_LIGHT : combinedLight, 0, 0.5F, 1, rotation, getSymbolColor(stargate, stargateVariant, pointOfOriginEngaged));
		}
		
		Symbols symbols = getSymbols(stargate, stargateVariant);
		
		if(symbols == null)
			return;
		consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbolTexture(symbols)));
		
		for(int j = 1; j < this.numberOfSymbols; j++)
		{
			boolean symbolEngaged = false;
			if(stargateVariant.symbols().engageEncodedSymbols() && (!stargate.isConnected() || stargate.isDialingOut()))
			{
				for(int i = 0; i < stargate.getAddress().getLength(); i++)
				{
					int addressSymbol = stargate.getAddress().toArray()[i];
					if(addressSymbol == j)
						symbolEngaged = true;
				}
			}
			else if(stargate.isConnected())
				symbolEngaged = stargateVariant.symbols().engageSymbolsOnIncoming();
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, symbolEngaged) ? 
					MAX_LIGHT : combinedLight, j, symbols.getTextureOffset(j), symbols.getSize(), rotation, getSymbolColor(stargate, stargateVariant, symbolEngaged));
		}
	}
	
	protected void renderSymbol(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, 
			int symbolNumber, float symbolOffset, int textureXSize, float rotation, ColorUtil.RGBA symbolColor)
	{
		if(symbolNumber >= this.numberOfSymbols)
			return;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(symbolNumber * -ANGLE + rotation));
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				symbolColor.red(), symbolColor.green(), symbolColor.blue(), symbolColor.alpha(), 
				-SPINNY_RING_OUTER_CENTER,
				SPINNY_RING_INNER_RADIUS + 8.5F/16,
				SYMBOL_OFFSET,
				symbolOffset - (SPINNY_RING_OUTER_CENTER * 32 / 16 / textureXSize), 0,
				
				-SPINNY_RING_INNER_CENTER,
				SPINNY_RING_INNER_RADIUS + 0.5F/16,
				SYMBOL_OFFSET,
				symbolOffset - (SPINNY_RING_INNER_CENTER * 32 / 16 / textureXSize), 1,
				
				SPINNY_RING_INNER_CENTER, 
				SPINNY_RING_INNER_RADIUS + 0.5F/16,
				SYMBOL_OFFSET,
				symbolOffset + (SPINNY_RING_INNER_CENTER * 32 / 16 / textureXSize), 1,
				
				SPINNY_RING_OUTER_CENTER,
				SPINNY_RING_INNER_RADIUS + 8.5F/16,
				SYMBOL_OFFSET,
				symbolOffset + (SPINNY_RING_OUTER_CENTER * 32 / 16 / textureXSize), 0);
		
		stack.popPose();
	}
	
	@Override
	protected boolean isPrimaryChevronLowered(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant)
	{
		return isPrimaryChevronEngaged(stargate, stargateVariant);
	}
	
	@Override
	protected boolean isChevronLowered(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant, int chevronNumber)
	{
		return isChevronEngaged(stargate, stargateVariant, chevronNumber);
	}
	
	@Override
	protected void renderPrimaryChevron(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, boolean chevronEngaged)
	{
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		float subtracted = isPrimaryChevronLowered(stargate, stargateVariant) ? LOCKED_CHEVRON_OFFSET + 1F/16 :  1F/16;
		
		stack.pushPose();
		stack.translate(0, DEFAULT_RADIUS - subtracted, 0);
		
		renderChevronLight(stack, consumer, source, light, chevronEngaged);
		renderOuterChevron(stack, consumer, source, light, chevronEngaged);
		
		stack.popPose();
	}

	@Override
	protected void renderChevron(ClassicStargateEntity stargate, ClassicStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		int chevron = AbstractStargateEntity.getChevron(stargate, chevronNumber);
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		float subtracted = isChevronLowered(stargate, stargateVariant, chevronNumber) ? LOCKED_CHEVRON_OFFSET + 1F/16 :  1F/16;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(-CHEVRON_ANGLE * chevron));
		stack.translate(0, DEFAULT_RADIUS - subtracted, 0);
		
		renderChevronLight(stack, consumer, source, light, chevronEngaged);
		renderOuterChevron(stack, consumer, source, light, chevronEngaged);
		
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
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
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
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
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
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
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
