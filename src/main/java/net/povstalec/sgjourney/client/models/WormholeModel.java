package net.povstalec.sgjourney.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;

public class WormholeModel
{
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/event_horizon/event_horizon_idle.png");
	
	protected float red, green, blue;
	
	protected float scale = 0.03125F;
	
	protected float[] outerCircle = coordinates(36, 2.5F);
	protected float[] circle1 = coordinates(18, 2.0F);
	protected float[] circle2 = coordinates(9, 1.5F);
	
	protected float[][]coordinates = new float[][] {outerCircle, circle1, circle2};
	
	public WormholeModel(int r, int g, int b)
	{
		this.red = (float) r / 255;
		this.green = (float) g / 255;
		this.blue = (float) b / 255;
	}
	
	public void renderEventHorizon(PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay, int tickCount)
	{
		this.renderPuddle(stack, source, (float)tickCount * this.scale);
		//this.renderVortex(stack, source, (float)tickCount * this.scale);
	}
	
	protected void renderPuddle(PoseStack stack, MultiBufferSource source, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		for(int i = 0; i < 1; i++)
		{
			VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonFront(EVENT_HORIZON_TEXTURE, 0.0F, yOffset));
			
			int number = 72 / (i + 1);
			int nextNumber = number / (2 * (i + 1));
			
			for(int j = 0; j < nextNumber; j++)
			{
				createTriangle(frontConsumer, matrix4, matrix3, 
						coordinates[i][j * 2 % number], coordinates[i][(j * 2 + 1) % number],
						0.0F, 0.0F, // coordinates[i + 1][j * 2 % nextNumber], coordinates[i + 1][(j * 2 + 1) % nextNumber], 
						coordinates[i][(j * 2 + 2) % number], coordinates[i][(j * 2 + 3) % number],
						(float) 0);
			}
			
			VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonBack(EVENT_HORIZON_TEXTURE, 0.0F, yOffset));
			
			for(int j = 0; j < nextNumber; j++)
			{
				createTriangle(backConsumer, matrix4, matrix3, 
						coordinates[i][(j * 2 + 2) % number], coordinates[i][(j * 2 + 3) % number],
						0.0F, 0.0F, // coordinates[i + 1][j * 2 % nextNumber], coordinates[i + 1][(j * 2 + 1) % nextNumber], 
						coordinates[i][j * 2 % number], coordinates[i][(j * 2 + 1) % number],
						(float) 0);
			}
		}
	}
	
	protected void renderVortex(PoseStack stack, MultiBufferSource source, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		VertexConsumer vortexConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(EVENT_HORIZON_TEXTURE, yOffset, yOffset));
		
		for(int i = 0; i < 72; i++)
		{
			createTriangle(vortexConsumer, matrix4, matrix3, 
					outerCircle[i * 2 % 72], outerCircle[(i * 2 + 1) % 72],
					0.0F, 0.0F, 
					outerCircle[(i * 2 + 2) % 72], outerCircle[(i * 2 + 3) % 72],
					-6.0F);
		}
	}
	
	//============================================================================================
	//**********************************Coordinates and Vertexes**********************************
	//============================================================================================
	
	protected float[] coordinates(int sides, float distanceFromCenter)
	{
		float angle = (float) 360 / sides;
		float addition = sides % 2 == 0 ? 0 : angle;
		float[] coordinates = new float[sides * 2];
		float baseWidth = 9.8F / 16;
		float defaultDistance = 3.5F;
		
		float ratio = distanceFromCenter / defaultDistance;
		
		for(int i = 0; i < sides; i++)
		{
			float heightProjection = distanceFromCenter * (float) Math.cos(Math.toRadians(angle * i + addition));
			float widthProjection = distanceFromCenter * (float) Math.sin(Math.toRadians(angle * i + addition));
			
			float usedWidth = (baseWidth * ratio) / 2;

			float height = usedWidth * (float) Math.sin(Math.toRadians(angle * i + addition));
			float width = usedWidth * (float) Math.cos(Math.toRadians(angle * i + addition));
			
			coordinates[i * 2] = widthProjection - width;
			coordinates[i * 2 + 1] = heightProjection + height;
		}
		
		return coordinates;
	}
	
	protected void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, 
			float x1, float y1,
			float x2, float y2,
			float x3, float y3,
			float zOffset)
	{
		consumer.vertex(matrix4, x1, y1, 0).color(red, green, blue, 1.0F).uv(x1 / 2.5F / 2 + 0.5F, y1 / 2.5F / 16 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(matrix4, x2, y2, zOffset).color(red, green, blue, 1.0F).uv(x2 / 2.5F + 0.5F, y2 / 2.5F + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(matrix4, x3, y3, 0).color(red, green, blue, 1.0F).uv(x3 / 2.5F / 2 + 0.5F, y3 / 2.5F / 16 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
	}
}
