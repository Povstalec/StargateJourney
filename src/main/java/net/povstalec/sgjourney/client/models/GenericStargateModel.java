package net.povstalec.sgjourney.client.models;

import java.util.Optional;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.GenericStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class GenericStargateModel<StargateEntity extends AbstractStargateEntity, Variant extends GenericStargateVariant> extends AbstractStargateModel<StargateEntity, Variant>
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
	
	public GenericStargateModel(short numberOfSymbols)
	{
		super(numberOfSymbols);
		this.symbolAngle = 360F / numberOfSymbols;
		
		this.stargateSymbolRingOuterLength = SGJourneyModel.getUsedWidth(numberOfSymbols, STARGATE_SYMBOL_RING_OUTER_HEIGHT, DEFAULT_RADIUS);
		this.stargateSymbolRingOuterCenter = stargateSymbolRingOuterLength / 2;

		this.stargateSymbolRingInnerLength = SGJourneyModel.getUsedWidth(numberOfSymbols, STARGATE_SYMBOL_RING_INNER_HEIGHT, DEFAULT_RADIUS);
		this.stargateSymbolRingInnerCenter = stargateSymbolRingInnerLength / 2;
	}
	
	@Override
	public void renderRing(StargateEntity stargate, Variant stargateVariant, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		this.renderOuterRing(stack, consumer, source, combinedLight);
		
		this.renderSymbolRing(stargate, stargateVariant, stack, consumer, source, combinedLight, this.rotation);
	}
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================
	
	protected boolean isPrimaryChevronBackRaised(StargateEntity stargate, Variant stargateVariant)
	{
		return false;
	}
	
	protected boolean isChevronBackRaised(StargateEntity stargate, Variant stargateVariant, int chevronNumber)
	{
		return false;
	}
	
	@Override
	protected void renderPrimaryChevron(StargateEntity stargate, Variant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, boolean chevronEngaged)
	{
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.translate(0, DEFAULT_RADIUS - 2.5F/16, 0);
		
		GenericChevronModel.renderChevronLight(stack, consumer, source, light, isPrimaryChevronRaised(stargate, stargateVariant), isPrimaryChevronBackRaised(stargate, stargateVariant));
		if(stargateVariant.stargateModel().useMovieStargatePrimaryChevron())
			MovieChevronModel.renderMovieChevronFront(stack, consumer, source, light);
		else
			GenericChevronModel.renderOuterChevronFront(stack, consumer, source, light, isPrimaryChevronLowered(stargate, stargateVariant));
		GenericChevronModel.renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();
	}

	@Override
	protected void renderChevron(StargateEntity stargate, Variant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		int chevron = AbstractStargateEntity.getChevron(stargate, chevronNumber);
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(-CHEVRON_ANGLE * chevron));
		stack.translate(0, DEFAULT_RADIUS - 2.5F/16, 0);
		
		GenericChevronModel.renderChevronLight(stack, consumer, source, light, isChevronRaised(stargate, stargateVariant, chevronNumber), isChevronBackRaised(stargate, stargateVariant, chevronNumber));
		GenericChevronModel.renderOuterChevronFront(stack, consumer, source, light, isChevronLowered(stargate, stargateVariant, chevronNumber));
		GenericChevronModel.renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();
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
					(10F * (j % 4) + 5 - STARGATE_RING_START_CENTER * 16) / 64, (33.5F - STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_INNER_CENTER * 16) / 64, (33.5F + STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_INNER_CENTER * 16) / 64, (33.5F + STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_START_CENTER * 16) / 64, (33.5F - STARGATE_CUTOUT_TO_INNER_HEIGHT/2 * 16) / 64);
			
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
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
					(10F * (j % 4) + 5 - STARGATE_RING_INNER_CENTER * 16) / 64, 14F / 64,
					
					STARGATE_RING_STOP_CENTER, 
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_INNER_CENTER * 16) / 64, 15F / 64,
					
					-STARGATE_RING_STOP_CENTER,
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_INNER_CENTER * 16) / 64, 15F / 64,
					
					-STARGATE_RING_STOP_CENTER,
					STARGATE_RING_STOP_RADIUS,
					STARGATE_RING_OFFSET - 1F / 16,
					(10F * (j % 4) + 5 + STARGATE_RING_INNER_CENTER * 16) / 64, 14F / 64);
			
			//Inside Start - This will essentially be just one pixel thick
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET - 1F / 16,
					(10F * (j % 4) + 5 - STARGATE_RING_INNER_CENTER * 16) / 64, 31F / 64,
					
					-STARGATE_RING_START_CENTER, 
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 - STARGATE_RING_INNER_CENTER * 16) / 64, 32F / 64,
					
					STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET,
					(10F * (j % 4) + 5 + STARGATE_RING_INNER_CENTER * 16) / 64, 32F / 64,
					
					STARGATE_RING_START_CENTER,
					STARGATE_RING_START_RADIUS,
					STARGATE_RING_OFFSET - 1F / 16,
					(10F * (j % 4) + 5 + STARGATE_RING_INNER_CENTER * 16) / 64, 31F / 64);
			
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
	
	protected void renderSymbolRing(StargateEntity stargate, Variant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		for(int j = 0; j < this.numberOfSymbols; j++)
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
		for(int j = 0; j < this.numberOfSymbols; j++)
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
		
		this.renderSymbols(stargate, stargateVariant, stack, consumer, source, combinedLight, rotation);
	}
	
	//============================================================================================
	//******************************************Symbols*******************************************
	//============================================================================================
	
	protected void renderSymbols(StargateEntity stargate, Variant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, float rotation)
	{
		Optional<PointOfOrigin> pointOfOrigin = getPointOfOrigin(stargate, stargateVariant);
		
		if(pointOfOrigin.isPresent())
		{
			boolean pointOfOriginEngaged = false;
			if(stargateVariant.symbols().engageEncodedSymbols() && (!stargate.isConnected() || stargate.isDialingOut()))
				pointOfOriginEngaged = stargate.isConnected();
			else if(stargate.isConnected())
				pointOfOriginEngaged = stargateVariant.symbols().engageSymbolsOnIncoming();
			
			consumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getPointOfOriginTexture(pointOfOrigin)));
			
			renderSymbol(stargate, stargateVariant, stack, consumer, source, symbolsGlow(stargate, stargateVariant, pointOfOriginEngaged) ? MAX_LIGHT : combinedLight, 0, 0.5F, 1, rotation, getSymbolColor(stargate, stargateVariant, pointOfOriginEngaged));
		}
		
		Optional<Symbols> symbols = getSymbols(stargate, stargateVariant);
		
		if(symbols.isEmpty())
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
					MAX_LIGHT : combinedLight, j, symbols.get().getTextureOffset(j), symbols.get().getSize(), rotation, getSymbolColor(stargate, stargateVariant, symbolEngaged));
		}
	}
	
	protected void renderSymbol(StargateEntity stargate, Variant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, 
		int symbolNumber, float symbolOffset, int textureXSize, float rotation, ColorUtil.RGBA symbolColor)
	{
		if(symbolNumber >= this.numberOfSymbols)
			return;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(symbolNumber * -this.symbolAngle + rotation));
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				symbolColor.red(), symbolColor.green(), symbolColor.blue(), symbolColor.alpha(), 
				-stargateSymbolRingOuterCenter,
				STARGATE_SYMBOL_RING_OUTER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				symbolOffset - (stargateSymbolRingOuterCenter * 32 / 16 / textureXSize), (8 - STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16,
				
				-stargateSymbolRingInnerCenter, 
				STARGATE_SYMBOL_RING_INNER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				symbolOffset - (stargateSymbolRingInnerCenter * 32 / 16 / textureXSize), (8 + STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16,
				
				stargateSymbolRingInnerCenter,
				STARGATE_SYMBOL_RING_INNER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				symbolOffset + (stargateSymbolRingInnerCenter * 32 / 16 / textureXSize), (8 + STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16,
				
				stargateSymbolRingOuterCenter,
				STARGATE_SYMBOL_RING_OUTER_HEIGHT,
				STARGATE_RING_OFFSET - 1F/16,
				symbolOffset + (stargateSymbolRingOuterCenter * 32 / 16 / textureXSize), (8 - STARGATE_SYMBOL_RING_HEIGHT/2 * 32) / 16);
		
		stack.popPose();
	}
}
