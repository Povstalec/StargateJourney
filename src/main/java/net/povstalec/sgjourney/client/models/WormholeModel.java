package net.povstalec.sgjourney.client.models;

import java.util.Random;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;

public class WormholeModel
{
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/event_horizon/event_horizon_idle.png");

	protected static final float DEFAULT_DISTANCE = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	
	protected float red, green, blue;
	
	protected float scale = 0.03125F;
	
	protected float[][] outerCircle = coordinates(DEFAULT_SIDES, 2.5F, 5, 0);
	protected float[][] circle1 = coordinates(DEFAULT_SIDES, 2.0F, 0, 98);
	protected float[][] circle2 = coordinates(DEFAULT_SIDES, 1.5F, -5, 67);
	protected float[][] circle3 = coordinates(DEFAULT_SIDES, 1.0F, -10, 567);
	protected float[][] circle4 = coordinates(DEFAULT_SIDES, 0.5F, -15, 257);
	protected float[][] circle5 = coordinates(DEFAULT_SIDES, 0.0F, 0, 0);
	
	protected float[][][] coordinates = new float[][][] {outerCircle, circle1, circle2, circle3, circle4, circle5};
	
	public WormholeModel(int r, int g, int b)
	{
		this.red = (float) r / 255;
		this.green = (float) g / 255;
		this.blue = (float) b / 255;
	}
	
	public void renderEventHorizon(PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay, int tickCount)
	{
		this.renderPuddle(stack, source, (float)tickCount * this.scale);
		this.renderVortex(stack, source, (float)tickCount * this.scale);
	}
	
	protected void renderPuddle(PoseStack stack, MultiBufferSource source, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		for(int i = 0; i < 5; i++)
		{
			VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonFront(EVENT_HORIZON_TEXTURE, 0.0F, yOffset));
			
			int totalSides = coordinates[0].length;
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(frontConsumer, matrix4, matrix3, 
						coordinates[i][j % coordinates[i].length][0], 
						coordinates[i][j % coordinates[i].length][1],
						(float) Math.sin(coordinates[i][j % coordinates[i].length][2] * yOffset * 8) / 4,
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						(float) Math.sin(coordinates[i + 1][j % coordinates[i + 1].length][2] * yOffset * 8) / 4,
						
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						(float) Math.sin(coordinates[i][(j + 1) % coordinates[i].length][2] * yOffset * 8) / 4);
				
				createTriangle(frontConsumer, matrix4, matrix3, 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1],
						(float) Math.sin(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2] * yOffset * 8) / 4,
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						(float) Math.sin(coordinates[i][(j + 1) % coordinates[i].length][2] * yOffset * 8) / 4,
						
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1], 
						(float) Math.sin(coordinates[i + 1][j % coordinates[i + 1].length][2] * yOffset * 8) / 4);
			}
			
			VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonBack(EVENT_HORIZON_TEXTURE, 0.0F, yOffset));
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(backConsumer, matrix4, matrix3, 
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						(float) Math.sin(coordinates[i][(j + 1) % coordinates[i].length][2] * yOffset * 8) / 4,
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						(float) Math.sin(coordinates[i + 1][j % coordinates[i + 1].length][2] * yOffset * 8) / 4,
						
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						(float) Math.sin(coordinates[i][j % coordinates[i].length][2] * yOffset * 8) / 4);
				
				createTriangle(frontConsumer, matrix4, matrix3, 
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						(float) Math.sin(coordinates[i + 1][j % coordinates[i + 1].length][2] * yOffset * 8) / 4,
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						(float) Math.sin(coordinates[i][(j + 1) % coordinates[i].length][2] * yOffset * 8) / 4,
						
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], 
						(float) Math.sin(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2] * yOffset * 8) / 4);
			}
		}
	}
	
	protected void renderVortex(PoseStack stack, MultiBufferSource source, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		VertexConsumer vortexConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(EVENT_HORIZON_TEXTURE, yOffset, yOffset));
		
		int totalSides = coordinates[0].length;
		
		for(int i = 0; i < 5; i++)
		{
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(vortexConsumer, matrix4, matrix3, 
						coordinates[i][j % coordinates[i].length][0], 
						coordinates[i][j % coordinates[i].length][1],
						(float) - i * i / 4,
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						(float) - (i + 1) * (i + 1) / 4,
						
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						(float) - i * i / 4);
				
				createTriangle(vortexConsumer, matrix4, matrix3, 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1],
						(float) - (i + 1) * (i + 1) / 4,
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						(float) - i * i / 4,
						
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1], 
						(float) - (i + 1) * (i + 1) / 4);
			}
		}
	}
	
	//============================================================================================
	//**********************************Coordinates and Vertexes**********************************
	//============================================================================================
	
	protected float[][] coordinates(int sides, float distanceFromCenter, float offset, int seed)
	{
		Random random = new Random(seed);
		float angle = (float) 360 / sides;
		float[][] coordinates = new float[sides][3];
		float baseWidth = 9.8F / 16;
		float ratio = distanceFromCenter / DEFAULT_DISTANCE;
		float usedWidth = baseWidth * ratio;
		
		float circumcircleRadius = circumcircleRadius(angle, usedWidth);
		
		for(int i = 0; i < sides; i++)
		{
			coordinates[i][0] = CoordinateHelper.polarToCartesianY(circumcircleRadius, angle * i - offset);
			coordinates[i][1] = CoordinateHelper.polarToCartesianX(circumcircleRadius, angle * i - offset);
			coordinates[i][2] = seed <= 0 ? 0 : random.nextFloat() - 0.5F;
		}
		
		return coordinates;
	}
	
	protected void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, 
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3)
	{
		consumer.vertex(matrix4, x1, y1, z1).color(red, green, blue, 1.0F).uv(x1 / 2.5F / 2 + 0.5F, y1 / 2.5F / 16 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		
		consumer.vertex(matrix4, x2, y2, z2).color(red, green, blue, 1.0F).uv(x2 / 2.5F / 2 + 0.5F, y2 / 2.5F / 16 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		
		consumer.vertex(matrix4, x3, y3, z3).color(red, green, blue, 1.0F).uv(x3 / 2.5F / 2 + 0.5F, y3 / 2.5F / 16 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
	}
	
	protected float circumcircleRadius(float phi, float baseWidth)
	{
		return baseWidth / (2 * (float) Math.sin(Math.toRadians(phi / 2)));
	}
}
