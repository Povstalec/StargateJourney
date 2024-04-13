package net.povstalec.sgjourney.client.models;

import java.util.Optional;
import java.util.Random;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.stargate.Connection;

public class WormholeModel
{
	protected static final float DEFAULT_RADIUS = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	
	protected static final int MAX_LIGHT = 15728864;
	
	protected static final float SHIELDING_OFFSET = 1F / 16 / 2;
	
	//protected Stargate.RGBA rgba;
	protected float maxDefaultDistortion;
	
	//new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/event_horizon/event_horizon_idle.png")
	private final ResourceLocation eventHorizonTexture;
	private final Optional<ResourceLocation> shinyEventHorizonTexture;
	
	protected float[][] outerCircle = coordinates(DEFAULT_SIDES, 2.5F, 5, 0);
	protected float[][] circle1 = coordinates(DEFAULT_SIDES, 2.0F, 0, 98);
	protected float[][] circle2 = coordinates(DEFAULT_SIDES, 1.5F, -5, 67);
	protected float[][] circle3 = coordinates(DEFAULT_SIDES, 1.0F, -10, 567);
	protected float[][] circle4 = coordinates(DEFAULT_SIDES, 0.5F, -15, 257);
	protected float[][] circle5 = coordinates(DEFAULT_SIDES, 0.0F, 0, -2);
	
	protected float[][][] coordinates = new float[][][] {outerCircle, circle1, circle2, circle3, circle4, circle5};
	
	public WormholeModel(ResourceLocation eventHorizonTexture, Optional<ResourceLocation> shinyEventHorizonTexture, float maxDefaultDistortion)
	{
		this.eventHorizonTexture = eventHorizonTexture;
		this.shinyEventHorizonTexture = shinyEventHorizonTexture;
		this.maxDefaultDistortion = maxDefaultDistortion;
	}
	
	protected float getMaxDistortion()
	{
		float configDistortion = (float) ClientStargateConfig.event_horizon_distortion.get() / 100;
		return configDistortion > maxDefaultDistortion ? maxDefaultDistortion : configDistortion;
	}
	
	protected ResourceLocation getEventHorizonTexture()
	{
		if(ClientStargateConfig.shiny_event_horizons.get())
		{
			if(shinyEventHorizonTexture.isPresent())
				return shinyEventHorizonTexture.get();
		}
		return eventHorizonTexture;
	}
	
	public void renderEventHorizon(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, Optional<ResourceLocation> texture, int frames, int combinedLight, int combinedOverlay, boolean hasVortex)
	{
		float scale = 1F / frames;
		
		this.renderKawoosh(stack, source, texture, frames, scale, stargate.getTickCount(), stargate.getKawooshTickCount(), false);
		this.renderPuddle(stack, source, texture, frames, scale, stargate.getTickCount(), stargate.getKawooshTickCount(), false);
		if(hasVortex)
			this.renderVortex(stack, source, texture, frames, scale, stargate.getTickCount(), stargate.getKawooshTickCount());
	}
	
	protected void renderPuddle(PoseStack stack, MultiBufferSource source, Optional<ResourceLocation> texture, int frames, float scale, int ticks, int kawooshProgress, boolean isShieldOn)
	{
		float yOffset = ticks * scale;
		float textureTickOffset = (ticks % frames) * scale;
		
		if(kawooshProgress <= 0)
			return;
		
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		for(int i = 0; i < 5; i++)
		{
			VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonFront(texture.isPresent() ? texture.get() : getEventHorizonTexture(), 0.0F, textureTickOffset));
			
			int totalSides = coordinates[0].length;
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(frontConsumer, matrix4, matrix3,
						coordinates[i][j % coordinates[i].length][0], 
						coordinates[i][j % coordinates[i].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][j % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0));
				
				createTriangle(frontConsumer, matrix4, matrix3,
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1], 
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0));
			}
			
			VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonBack(texture.isPresent() ? texture.get() : getEventHorizonTexture(), 0.0F, yOffset));
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(backConsumer, matrix4, matrix3,
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][j % coordinates[i].length][2], yOffset, i, 0));
				
				createTriangle(frontConsumer, matrix4, matrix3,
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], 
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, 0));
			}
		}
	}
	
	protected void renderKawoosh(PoseStack stack, MultiBufferSource source, Optional<ResourceLocation> texture, int frames, float scale, int ticks, int kawooshProgress, boolean isShieldOn)
	{
		float yOffset = ticks * scale;
		float textureTickOffset = (ticks % frames) * scale;
		
		if(kawooshProgress <= 0 || kawooshProgress >= Connection.KAWOOSH_TICKS)
			return;
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		VertexConsumer kawooshConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(texture.isPresent() ? texture.get() : getEventHorizonTexture(), 0, textureTickOffset));
		
		int totalSides = coordinates[0].length;
		
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(kawooshConsumer, matrix4, matrix3,
						bubbleX(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress));
				
				createTriangle(kawooshConsumer, matrix4, matrix3,
						bubbleX(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),

						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),

						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isShieldOn, getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress));
			}
		}
	}
	
	protected void renderVortex(PoseStack stack, MultiBufferSource source, Optional<ResourceLocation> texture, int frames, float scale, int ticks, int kawooshProgress)
	{
		float yOffset = ticks * scale;
		float textureTickOffset = (ticks % frames) * scale;
		
		if(kawooshProgress <= Connection.KAWOOSH_TICKS)
			return;
		
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		VertexConsumer vortexConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(texture.isPresent() ? texture.get() : getEventHorizonTexture(), 0, textureTickOffset));
		
		int totalSides = coordinates[0].length;
		
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(vortexConsumer, matrix4, matrix3,
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						vortexMaker(getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						vortexMaker(getMaxDistortion(), coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress));
				
				createTriangle(vortexConsumer, matrix4, matrix3,
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(getMaxDistortion(), coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						vortexMaker(getMaxDistortion(), coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], 
						vortexMaker(getMaxDistortion(), coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress));
			}
		}
	}
	
	//============================================================================================
	//**********************************Coordinates and Vertexes**********************************
	//============================================================================================
	
	/**
	 * Ïf the seed is above 0, a random offset will be chosen.
	 * If the seed is 0, the offset will be 0.
	 * If the seed is below 0, a random offset will be chosen that is same for all the coordinates.
	 * @param sides
	 * @param distanceFromCenter
	 * @param offset
	 * @param seed
	 * @return
	 */
	protected static float[][] coordinates(int sides, float distanceFromCenter, float offset, int seed)
	{
		Random random = new Random(seed);
		float angle = (float) 360 / sides;
		float[][] coordinates = new float[sides][3];
		float baseWidth = 9.8F / 16;
		float ratio = distanceFromCenter / DEFAULT_RADIUS;
		float usedWidth = baseWidth * ratio;
		
		float circumcircleRadius = SGJourneyModel.circumcircleRadius(angle, usedWidth);
		float defaultOffset = seed == 0 ? 0 : random.nextFloat() - 0.5F;
		
		if(distanceFromCenter >= 0)
		{
			for(int i = 0; i < sides; i++)
			{
				coordinates[i][0] = CoordinateHelper.CoordinateSystems.polarToCartesianY(circumcircleRadius, angle * i - offset);
				coordinates[i][1] = CoordinateHelper.CoordinateSystems.polarToCartesianX(circumcircleRadius, angle * i - offset);
				coordinates[i][2] = seed <= 0 ? defaultOffset : 2 * random.nextFloat() - 1;
			}
		}
		else
		{
			coordinates[0][0] = 0;
			coordinates[0][1] = 0;
			coordinates[0][2] = defaultOffset;
		}
		
		return coordinates;
	}
	
	protected static float bubbleX(float x, float y, int multiplier, int progress)
	{
		float r = CoordinateHelper.CoordinateSystems.cartesianToPolarR(x, y);
		float phi = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(x, y);
		
		r *= (1 + 0.4 * (float) (Math.pow(Math.sin((double) multiplier / 3), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		return CoordinateHelper.CoordinateSystems.polarToCartesianX(r, phi);
	}
	
	protected static float bubbleY(float x, float y, int multiplier, int progress)
	{
		float r = CoordinateHelper.CoordinateSystems.cartesianToPolarR(x, y);
		float phi = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(x, y);
		
		r *= (1 + 0.4 * (float) (Math.pow(Math.sin((double) multiplier / 3), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		return CoordinateHelper.CoordinateSystems.polarToCartesianY(r, phi);
	}
	
	protected static float distortionMaker(boolean shielded, float maxDefaultDistortion, float defaultOffset, float distortionOffset, int multiplier, int progress)
	{
		defaultOffset *= maxDefaultDistortion;
		float defaultDistortion = (float) Math.sin(defaultOffset * distortionOffset * 8) * maxDefaultDistortion;
		
		float gradualKawoosh = (float) Math.pow(Math.sin((double) multiplier / 4), 2);
		float kawooshDistortion = (float) (1.2 * AbstractStargateEntity.kawooshFunction(progress)) * gradualKawoosh;
		
		float totalDistortion = defaultDistortion + kawooshDistortion;
		
		if(shielded && totalDistortion > SHIELDING_OFFSET) //TODO
			totalDistortion = SHIELDING_OFFSET - 0.001F;
		
		return totalDistortion < -maxDefaultDistortion ? -maxDefaultDistortion : totalDistortion;
	}
	
	protected static float vortexMaker(float maxDefaultDistortion, float defaultOffset, float distortionOffset, int multiplier, int progress)
	{
		defaultOffset *= maxDefaultDistortion;
		float defaultDistortion = (float) Math.sin(defaultOffset * distortionOffset * 8) * maxDefaultDistortion;
		float actualDistortion = defaultDistortion * (1 + (float)( Math.pow(Math.sin((double) multiplier / 5), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		float gradualKawoosh = (float) Math.pow(Math.sin((double) multiplier / 5), 2);
		float kawooshDistortion = (float) (1.2 * AbstractStargateEntity.kawooshFunction(progress)) * gradualKawoosh;
		
		float totalDistortion = actualDistortion + kawooshDistortion;
		
		return totalDistortion;
	}
	
	protected void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3)
	{
		
		consumer.vertex(matrix4, x1, y1, z1).color(1F, 1F, 1F, 0.75F).uv(x1 / 2.5F / 2 + 0.5F, y1 / 2.5F / 80 + 2.5F / 2 / 80)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(MAX_LIGHT).normal(matrix3, 1.0F, 1.0F, 1.0F).endVertex();
		
		consumer.vertex(matrix4, x2, y2, z2).color(1F, 1F, 1F, 0.75F).uv(x2 / 2.5F / 2 + 0.5F, y2 / 2.5F / 80 + 2.5F / 2 / 80)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(MAX_LIGHT).normal(matrix3, 1.0F, 1.0F, 1.0F).endVertex();
		
		consumer.vertex(matrix4, x3, y3, z3).color(1F, 1F, 1F, 0.75F).uv(x3 / 2.5F / 2 + 0.5F, y3 / 2.5F / 80 + 2.5F / 2 / 80)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(MAX_LIGHT).normal(matrix3, 1.0F, 1.0F, 1.0F).endVertex();
	}
}
