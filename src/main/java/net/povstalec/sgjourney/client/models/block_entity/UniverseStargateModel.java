package net.povstalec.sgjourney.client.models.block_entity;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.UniverseStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class UniverseStargateModel extends AbstractStargateModel<UniverseStargateEntity, UniverseStargateVariant>
{
	protected static final int UNIVERSE_SIDES = 54;
	protected static final float UNIVERSE_ANGLE = 360F / UNIVERSE_SIDES;
	
	protected static final float STARGATE_SYMBOL_RING_OUTER_HEIGHT = DEFAULT_RADIUS - 5F / 16;
	protected static final float STARGATE_SYMBOL_RING_INNER_HEIGHT = DEFAULT_RADIUS - 13F / 16;

	protected static final float STARGATE_SYMBOL_RING_OUTER_LENGTH = SGJourneyModel.getUsedWidth(UNIVERSE_SIDES, STARGATE_SYMBOL_RING_OUTER_HEIGHT, DEFAULT_RADIUS);
	protected static final float STARGATE_SYMBOL_RING_OUTER_CENTER = STARGATE_SYMBOL_RING_OUTER_LENGTH / 2;
	protected static final float STARGATE_SYMBOL_RING_INNER_LENGTH = SGJourneyModel.getUsedWidth(UNIVERSE_SIDES, STARGATE_SYMBOL_RING_INNER_HEIGHT, DEFAULT_RADIUS);
	protected static final float STARGATE_SYMBOL_RING_INNER_CENTER = STARGATE_SYMBOL_RING_INNER_LENGTH / 2;
	
	protected static final float STARGATE_RING_FRONT_THICKNESS = 3F;
	protected static final float STARGATE_RING_BACK_THICKNESS = 4F;
	protected static final float STARGATE_RING_OFFSET = (STARGATE_RING_FRONT_THICKNESS + STARGATE_RING_BACK_THICKNESS) / 2 / 16;
	protected static final float STARGATE_RING_DIVIDE_OFFSET = (STARGATE_RING_BACK_THICKNESS - STARGATE_RING_FRONT_THICKNESS) / 2 / 16;
	protected static final float SYMBOL_OFFSET = STARGATE_RING_OFFSET + 0.001F;
	
	protected static final float STARGATE_RING_OUTER_RADIUS = DEFAULT_RADIUS - STARGATE_RING_SHRINK;
	protected static final float STARGATE_RING_OUTER_LENGTH = SGJourneyModel.getUsedWidth(UNIVERSE_SIDES, STARGATE_RING_OUTER_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_OUTER_CENTER = STARGATE_RING_OUTER_LENGTH / 2;
	
	protected static final float STARGATE_RING_STOP_RADIUS = DEFAULT_RADIUS - 7F / 16;
	protected static final float STARGATE_RING_STOP_LENGTH = SGJourneyModel.getUsedWidth(UNIVERSE_SIDES, STARGATE_RING_STOP_RADIUS, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_STOP_CENTER = STARGATE_RING_STOP_LENGTH / 2;

	protected static final float STARGATE_RING_INNER_HEIGHT = DEFAULT_RADIUS - (DEFAULT_RING_HEIGHT - STARGATE_RING_SHRINK);
	protected static final float STARGATE_RING_INNER_LENGTH = SGJourneyModel.getUsedWidth(UNIVERSE_SIDES, STARGATE_RING_INNER_HEIGHT, DEFAULT_RADIUS);
	protected static final float STARGATE_RING_INNER_CENTER = STARGATE_RING_INNER_LENGTH / 2;
	
	protected static final float STARGATE_RING_HEIGHT = STARGATE_RING_OUTER_RADIUS - STARGATE_RING_INNER_HEIGHT;
	protected static final float STARGATE_SYMBOL_RING_HEIGHT = STARGATE_SYMBOL_RING_OUTER_HEIGHT - STARGATE_SYMBOL_RING_INNER_HEIGHT;

	protected static final float CHEVRON_LIGHT_THICKNESS = 1F / 16;
	protected static final float CHEVRON_LIGHT_Z_OFFSET = STARGATE_RING_OFFSET + CHEVRON_LIGHT_THICKNESS;
	
	protected static final float CHEVRON_LIGHT_TOP_LENGTH = 4F / 16;
	protected static final float CHEVRON_LIGHT_TOP_CENTER = CHEVRON_LIGHT_TOP_LENGTH / 2;
	protected static final float CHEVRON_LIGHT_TOP_HEIGHT = 5F / 16;
	
	protected static final float CHEVRON_LIGHT_MID1_LENGTH = 6F / 16;
	protected static final float CHEVRON_LIGHT_MID1_CENTER = CHEVRON_LIGHT_MID1_LENGTH / 2;
	protected static final float CHEVRON_LIGHT_MID1_HEIGHT = 4F / 16;
	
	protected static final float CHEVRON_LIGHT_MID2_LENGTH = 3F / 16;
	protected static final float CHEVRON_LIGHT_MID2_CENTER = CHEVRON_LIGHT_MID2_LENGTH / 2;
	protected static final float CHEVRON_LIGHT_MID2_HEIGHT = 2F / 16;
	
	protected static final float CHEVRON_LIGHT_BOTTOM_LENGTH = 1F / 16;
	protected static final float CHEVRON_LIGHT_BOTTOM_CENTER = CHEVRON_LIGHT_BOTTOM_LENGTH / 2;
	protected static final float CHEVRON_LIGHT_BOTTOM_HEIGHT = 0;

	protected static final float OUTER_CHEVRON_THICKNESS = 0.5F / 16;
	protected static final float OUTER_CHEVRON_Z_OFFSET = STARGATE_RING_OFFSET + OUTER_CHEVRON_THICKNESS;
	
	private float rotation = 0.0F;

	protected static final float DEFAULT_DISTANCE_FROM_CENTER = 56.0F;
	protected static final int BOXES_PER_RING = 36;
	
	public UniverseStargateModel()
	{
		super((short) 36);
	}
	
	public float getRotation(boolean shouldRotate)
	{
		return shouldRotate ? this.rotation : 0;
	}
	
	@Override
	public void renderRing(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, float partialTick, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		this.renderOuterRingFront(stargate, stargateVariant, stack, consumer, source, combinedLight, combinedOverlay);
		this.renderOuterRingBack(stargate, stargateVariant, stack, consumer, source, combinedLight, combinedOverlay);
		
		this.renderSymbols(stargate, stargateVariant, stack, consumer, source, combinedLight, getRotation(true));
	}
	
	protected void renderOuterRingFront(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		for(int j = 0; j < UNIVERSE_SIDES; j++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(j * UNIVERSE_ANGLE - UNIVERSE_ANGLE/2 + getRotation(true)));
			Matrix4f matrix4 = stack.last().pose();
			Matrix3f matrix3 = stack.last().normal();
			
			//Outside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_OUTER_CENTER, 
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 4F / 64,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 7F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 7F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 4F / 64);
			
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, (15 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_INNER_CENTER * 16) / 64, (15 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_INNER_CENTER * 16) / 64, (15 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, (15 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Inside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 47F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 44F / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 44F / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 47F / 64);
			
			if(stargateVariant.onlyFrontRotates())
			{
				//Back
				SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
						STARGATE_RING_OUTER_CENTER,
						STARGATE_RING_OUTER_RADIUS,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F + STARGATE_RING_OUTER_CENTER * 16) / 64, (15 - STARGATE_RING_HEIGHT/2 * 16) / 64,
						
						STARGATE_RING_INNER_CENTER,
						STARGATE_RING_INNER_HEIGHT,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F + STARGATE_RING_INNER_CENTER * 16) / 64, (15 + STARGATE_RING_HEIGHT/2 * 16) / 64,
						
						-STARGATE_RING_INNER_CENTER, 
						STARGATE_RING_INNER_HEIGHT,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F - STARGATE_RING_INNER_CENTER * 16) / 64, (15 + STARGATE_RING_HEIGHT/2 * 16) / 64,
						
						-STARGATE_RING_OUTER_CENTER,
						STARGATE_RING_OUTER_RADIUS,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F - STARGATE_RING_OUTER_CENTER * 16) / 64, (15 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			}
			
			stack.popPose();
		}
	}
	
	protected void renderOuterRingBack(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		for(int j = 0; j < UNIVERSE_SIDES; j++)
		{
			stack.pushPose();
			stack.mulPose(Axis.ZP.rotationDegrees(j * -UNIVERSE_ANGLE + UNIVERSE_ANGLE/2 + getRotation(!stargateVariant.onlyFrontRotates())));
			Matrix4f matrix4 = stack.last().pose();
			Matrix3f matrix3 = stack.last().normal();
			
			//Outside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_OUTER_CENTER, 
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 0,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 4F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 4F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 0);
			
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, -1,
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, (32 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_INNER_CENTER * 16) / 64, (32 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_INNER_CENTER * 16) / 64, (32 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_RADIUS,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, (32 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Inside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 40F / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 - STARGATE_RING_OUTER_CENTER * 16) / 64, 44F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_DIVIDE_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 44F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(8F * (j % 6) + 4 + STARGATE_RING_OUTER_CENTER * 16) / 64, 40F / 64);
			
			if(stargateVariant.onlyFrontRotates())
			{
				//Front
				SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
						-STARGATE_RING_OUTER_CENTER,
						STARGATE_RING_OUTER_RADIUS,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F + STARGATE_RING_OUTER_CENTER * 16) / 64, (15 - STARGATE_RING_HEIGHT/2 * 16) / 64,
						
						-STARGATE_RING_INNER_CENTER,
						STARGATE_RING_INNER_HEIGHT,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F + STARGATE_RING_INNER_CENTER * 16) / 64, (15 + STARGATE_RING_HEIGHT/2 * 16) / 64,
						
						STARGATE_RING_INNER_CENTER, 
						STARGATE_RING_INNER_HEIGHT,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F - STARGATE_RING_INNER_CENTER * 16) / 64, (15 + STARGATE_RING_HEIGHT/2 * 16) / 64,
						
						STARGATE_RING_OUTER_CENTER,
						STARGATE_RING_OUTER_RADIUS,
						STARGATE_RING_DIVIDE_OFFSET,
						(52F - STARGATE_RING_OUTER_CENTER * 16) / 64, (15 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			}
			
			stack.popPose();
		}
	}
	
	protected void renderSymbols(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, float rotation)
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
				for(int i = 0; i < stargate.getAddress().regularSymbolCount(); i++)
				{
					int addressSymbol = stargate.getAddress().getArray()[i];
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
	
	protected void renderSymbol(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, 
			int symbolNumber, float symbolOffset, int textureXSize, float rotation, ColorUtil.RGBA symbolColor)
	{
		stack.pushPose();
		int symbolRow = symbolNumber / 4;
		int symbolInRow = symbolNumber % 4;
		stack.mulPose(Axis.ZP.rotationDegrees(-UNIVERSE_ANGLE * 3 / 2 + symbolRow * -CHEVRON_ANGLE + symbolInRow * -UNIVERSE_ANGLE + rotation));
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				symbolColor.red(), symbolColor.green(), symbolColor.blue(), symbolColor.alpha(), 
				-STARGATE_SYMBOL_RING_OUTER_CENTER,
				STARGATE_SYMBOL_RING_OUTER_HEIGHT,
				SYMBOL_OFFSET,
				symbolOffset - (STARGATE_SYMBOL_RING_OUTER_CENTER * 32 / 16 / textureXSize), 0,
				
				-STARGATE_SYMBOL_RING_INNER_CENTER, 
				STARGATE_SYMBOL_RING_INNER_HEIGHT,
				SYMBOL_OFFSET,
				symbolOffset - (STARGATE_SYMBOL_RING_INNER_CENTER * 32 / 16 / textureXSize), 1,
				
				STARGATE_SYMBOL_RING_INNER_CENTER,
				STARGATE_SYMBOL_RING_INNER_HEIGHT,
				SYMBOL_OFFSET,
				symbolOffset + (STARGATE_SYMBOL_RING_INNER_CENTER * 32 / 16 / textureXSize), 1,
				
				STARGATE_SYMBOL_RING_OUTER_CENTER,
				STARGATE_SYMBOL_RING_OUTER_HEIGHT,
				SYMBOL_OFFSET,
				symbolOffset + (STARGATE_SYMBOL_RING_OUTER_CENTER * 32 / 16 / textureXSize), 0);
		
		stack.popPose();
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================
	
	@Override
	protected boolean isPrimaryChevronEngaged(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant)
	{
		return stargate.isConnected() || stargate.addressBuffer.getLength() > 0;
	}
	
	@Override
	protected boolean isChevronEngaged(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, int chevronNumber)
	{
		return stargate.isConnected() || stargate.addressBuffer.getLength() > 0;
	}

	@Override
	protected void renderPrimaryChevron(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, boolean chevronEngaged)
	{
		this.renderChevron(stargate, stargateVariant, stack, consumer, source, combinedLight, 0, chevronEngaged);
	}

	@Override
	protected void renderChevron(UniverseStargateEntity stargate, UniverseStargateVariant stargateVariant, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		// Front
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(-CHEVRON_ANGLE * chevronNumber + getRotation(true)));
		stack.translate(0, DEFAULT_RADIUS - 5.5F/16, 0);
		renderChevronLight(stargate, stack, consumer, source, light, chevronNumber, chevronEngaged);
		renderOuterChevron(stargate, stack, consumer, source, light, chevronNumber, chevronEngaged);
		stack.popPose();
		
		// Back
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(180));
		stack.mulPose(Axis.ZP.rotationDegrees(CHEVRON_ANGLE * chevronNumber - getRotation(!stargateVariant.onlyFrontRotates())));
		stack.translate(0, DEFAULT_RADIUS - 5.5F/16, 0);
		renderChevronLight(stargate, stack, consumer, source, light, chevronNumber, chevronEngaged);
		renderOuterChevron(stargate, stack, consumer, source, light, chevronNumber, chevronEngaged);
		stack.popPose();
	}
	
	protected void renderChevronLight(UniverseStargateEntity stargate, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		//Light Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				STARGATE_RING_OFFSET,
				13F/64, 47F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				13F/64, 48F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				17F/64, 48F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				STARGATE_RING_OFFSET,
				17F/64, 47F/64);
		
		//Light Front 1
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				13F/64, 48F/64,
				
				-CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 49F/64,
				
				CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 49F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				17F/64, 48F/64);
		
		//Light Left 1
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, 1, 0,
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				STARGATE_RING_OFFSET,
				11F/64, 48F/64,
				
				-CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				STARGATE_RING_OFFSET,
				11F/64, 49F/64,
				
				-CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 49F/64,
				
				-CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 48F/64);
		
		//Light Right 1
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, 1, 0,
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 48F/64,
				
				CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 49F/64,
				
				CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				STARGATE_RING_OFFSET,
				19F/64, 49F/64,
				
				CHEVRON_LIGHT_TOP_CENTER,
				CHEVRON_LIGHT_TOP_HEIGHT,
				STARGATE_RING_OFFSET,
				19F/64, 48F/64);
		
		//Light Front 2
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 49F/64,
				
				-CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				13.5F/64, 51F/64,
				
				CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				16.5F/64, 51F/64,
				
				CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 49F/64);
		
		//Light Left 2
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				STARGATE_RING_OFFSET,
				11F/64, 49F/64,
				
				-CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				STARGATE_RING_OFFSET,
				11F/64, 51F/64,
				
				-CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 51F/64,
				
				-CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 49F/64);
		
		//Light Right 2
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 49F/64,
				
				CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 51F/64,
				
				CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				STARGATE_RING_OFFSET,
				19F/64, 51F/64,
				
				CHEVRON_LIGHT_MID1_CENTER,
				CHEVRON_LIGHT_MID1_HEIGHT,
				STARGATE_RING_OFFSET,
				19F/64, 49F/64);
		
		//Light Front 3
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				-CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				13.5F/64, 51F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				14.5F/64, 53F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				15.5F/64, 53F/64,
				
				CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				16.5F/64, 51F/64);
		
		//Light Left 3
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				-CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				STARGATE_RING_OFFSET,
				11F/64, 51F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				STARGATE_RING_OFFSET,
				11F/64, 53F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 53F/64,
				
				-CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				12F/64, 51F/64);
		
		//Light Right 3
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 51F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				18F/64, 53F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				STARGATE_RING_OFFSET,
				19F/64, 53F/64,
				
				CHEVRON_LIGHT_MID2_CENTER,
				CHEVRON_LIGHT_MID2_HEIGHT,
				STARGATE_RING_OFFSET,
				19F/64, 51F/64);
		
		//Light Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				14.5F/64, 53F/64,
				
				-CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				STARGATE_RING_OFFSET,
				14.5F/64, 54F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				STARGATE_RING_OFFSET,
				15.5F/64, 54F/64,
				
				CHEVRON_LIGHT_BOTTOM_CENTER,
				CHEVRON_LIGHT_BOTTOM_HEIGHT,
				CHEVRON_LIGHT_Z_OFFSET,
				15.5F/64, 53F/64);
	}
	
	protected void renderOuterChevron(UniverseStargateEntity stargate, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		// Left 1
		renderLightStrip(stargate, stack, consumer, source, combinedLight,
				4F / 16, 1F / 16, 0.5F / 16,
				-6F / 16, 3.5F / 16, OUTER_CHEVRON_Z_OFFSET,
				10, 5F, 48F);
		
		// Left 2
		renderLightStrip(stargate, stack, consumer, source, combinedLight,
				3F / 16, 1F / 16, 0.5F / 16,
				-4F / 16, 2F / 16, OUTER_CHEVRON_Z_OFFSET,
				10, 6F, 52F);
		
		// Left 3
		renderLightStrip(stargate, stack, consumer, source, combinedLight,
				2F / 16, 1F / 16, 0.5F / 16,
				-2.5F / 16, 0.5F / 16, OUTER_CHEVRON_Z_OFFSET,
				10, 7F, 56F);
		
		// Right 1
		renderLightStrip(stargate, stack, consumer, source, combinedLight,
				4F / 16, 1F / 16, 0.5F / 16,
				6F / 16, 3.5F / 16, OUTER_CHEVRON_Z_OFFSET,
				-10, 21F, 48F);
		
		// Left 2
		renderLightStrip(stargate, stack, consumer, source, combinedLight,
				3F / 16, 1F / 16, 0.5F / 16,
				4F / 16, 2F / 16, OUTER_CHEVRON_Z_OFFSET,
				-10, 21F, 52F);
		
		// Left 3
		renderLightStrip(stargate, stack, consumer, source, combinedLight,
				2F / 16, 1F / 16, 0.5F / 16,
				2.5F / 16, 0.5F / 16, OUTER_CHEVRON_Z_OFFSET,
				-10, 21F, 56F);
	}
	
	protected void renderLightStrip(UniverseStargateEntity stargate, PoseStack stack, VertexConsumer consumer,
			MultiBufferSource source, int combinedLight,
			float xSize, float ySize, float zSize,
			float xPos, float yPos, float zPos,
			float rotation, float textureX, float textureY)
	{
		stack.pushPose();
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		float halfX = xSize / 2;
		float halfY = ySize / 2;
		
		float r1 = CoordinateHelper.CoordinateSystems.cartesianToPolarR(-halfX, halfY);
		float phi1 = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(-halfX, halfY);
		float x1 = xPos + CoordinateHelper.CoordinateSystems.polarToCartesianX(r1, phi1 + rotation);
		float y1 = yPos + CoordinateHelper.CoordinateSystems.polarToCartesianY(r1, phi1 + rotation);
		
		float r2 = CoordinateHelper.CoordinateSystems.cartesianToPolarR(-halfX, -halfY);
		float phi2 = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(-halfX, -halfY);
		float x2 = xPos + CoordinateHelper.CoordinateSystems.polarToCartesianX(r2, phi2 + rotation);
		float y2 = yPos + CoordinateHelper.CoordinateSystems.polarToCartesianY(r2, phi2 + rotation);
		
		float r3 = CoordinateHelper.CoordinateSystems.cartesianToPolarR(halfX, -halfY);
		float phi3 = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(halfX, -halfY);
		float x3 = xPos + CoordinateHelper.CoordinateSystems.polarToCartesianX(r3, phi3 + rotation);
		float y3 = yPos + CoordinateHelper.CoordinateSystems.polarToCartesianY(r3, phi3 + rotation);
		
		float r4 = CoordinateHelper.CoordinateSystems.cartesianToPolarR(halfX, halfY);
		float phi4 = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(halfX, halfY);
		float x4 = xPos + CoordinateHelper.CoordinateSystems.polarToCartesianX(r4, phi4 + rotation);
		float y4 = yPos + CoordinateHelper.CoordinateSystems.polarToCartesianY(r4, phi4 + rotation);
		
		//Top
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
				x1,
				y1,
				STARGATE_RING_OFFSET,
				textureX / 64, (textureY - zSize * 16) / 64,
				
				x1,
				y1,
				zPos,
				textureX / 64, textureY / 64,
				
				x4,
				y4,
				zPos,
				(textureX + xSize * 16) / 64, textureY / 64,
				
				x4,
				y4,
				STARGATE_RING_OFFSET,
				(textureX + xSize * 16) / 64, (textureY - zSize * 16) / 64);
		
		//Front
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
				x1,
				y1,
				zPos,
				textureX / 64, textureY / 64,
				
				x2,
				y2,
				zPos,
				textureX / 64, (textureY - ySize * 16) / 64,
				
				x3,
				y3,
				zPos,
				(textureX + xSize * 16) / 64, (textureY - ySize * 16) / 64,
				
				x4,
				y4,
				zPos,
				(textureX + xSize * 16) / 64, textureY / 64);
		
		//Left
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, -1, -1, 0,
				x1,
				y1,
				STARGATE_RING_OFFSET,
				(textureX - zSize * 16) / 64, textureY / 64,
				
				x2,
				y2,
				STARGATE_RING_OFFSET,
				(textureX - zSize * 16) / 64, (textureY + ySize * 16) / 64,
				
				x2,
				y2,
				zPos,
				textureX / 64, (textureY + ySize * 16) / 64,
				
				x1,
				y1,
				zPos,
				textureX / 64, textureY / 64);
		
		//Right
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 1, -1, 0,
				x4,
				y4,
				zPos,
				(textureX + xSize * 16) / 64, textureY / 64,
				
				x3,
				y3,
				zPos,
				(textureX + xSize * 16) / 64, (textureY + ySize * 16) / 64,
				
				x3,
				y3,
				STARGATE_RING_OFFSET,
				(textureX + xSize * 16 + zSize * 16) / 64, (textureY + ySize * 16) / 64,
				
				x4,
				y4,
				STARGATE_RING_OFFSET,
				(textureX + xSize * 16 + zSize * 16) / 64, textureY / 64);
		
		//Bottom
		SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
				x2,
				y2,
				zPos,
				textureX / 64, textureY / 64,
				
				x2,
				y2,
				STARGATE_RING_OFFSET,
				textureX / 64, (textureY + zSize * 16) / 64,
				
				x3,
				y3,
				STARGATE_RING_OFFSET,
				(textureX + xSize * 16) / 64, (textureY + zSize * 16) / 64,
				
				x3,
				y3,
				zPos,
				(textureX + xSize * 16) / 64, textureY / 64);
		
		stack.popPose();
	}
}
