package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;

public class SGJourneyModel
{
	public static void createCircleTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light, 
			float red, float green, float blue, float alpha,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3)
	{
		consumer.vertex(matrix4, x1, y1, z1).color(red, green, blue, alpha).uv(x1 / 2.5F / 2 + 0.5F, y1 / 2.5F / 2 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		
		consumer.vertex(matrix4, x2, y2, z2).color(red, green, blue, alpha).uv(x2 / 2.5F / 2 + 0.5F, y2 / 2.5F / 2 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
		
		consumer.vertex(matrix4, x3, y3, z3).color(red, green, blue, alpha).uv(x3 / 2.5F / 2 + 0.5F, y3 / 2.5F / 2 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, 0.0F, 0.0F, 1.0F).endVertex();
	}
	
	public static void createCircleTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light, 
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3)
	{
		createCircleTriangle(consumer, matrix4, matrix3, light,
				1.0F, 1.0F, 1.0F, 1.0F,
				x1, y1, z1,
				x2, y2, z2,
				x3, y3, z3);
	}
	
	public static void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light, 
			float normal1, float normal2, float normal3,
			float red, float green, float blue, float alpha,
			float x1, float y1, float z1, float u1, float v1, 
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3)
	{
		//A
		consumer.vertex(matrix4, x1, y1, z1).color(red, green, blue, alpha).uv(u1, v1)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, normal1, normal2, normal3).endVertex();
		//B
		consumer.vertex(matrix4, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, normal1, normal2, normal3).endVertex();
		//C
		consumer.vertex(matrix4, x3, y3, z3).color(red, green, blue, alpha).uv(u3, v3)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, normal1, normal2, normal3).endVertex();
	}
	
	public static void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light, 
			float normal1, float normal2, float normal3,
			float x1, float y1, float z1, float u1, float v1, 
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3)
	{
		createTriangle(consumer, matrix4, matrix3, light,
				normal1, normal2, normal3,
				1.0F, 1.0F, 1.0F, 1.0F,
				x1, y1, z1, u1, v1,
				x2, y2, z2, u2, v2,
				x3, y3, z3, u3, v3);
	}
	
	public static void createQuad(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light, 
			float normal1, float normal2, float normal3,
			float red, float green, float blue, float alpha,
			float x1, float y1, float z1, float u1, float v1, 
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3,
			float x4, float y4, float z4, float u4, float v4)
	{
		//TOP LEFT
		consumer.vertex(matrix4, x1, y1, z1).color(red, green, blue, alpha).uv(u1, v1)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, normal1, normal2, normal3).endVertex();
		//BOTTOM LEFT
		consumer.vertex(matrix4, x2, y2, z2).color(red, green, blue, alpha).uv(u2, v2)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, normal1, normal2, normal3).endVertex();
		//BOTTOM RIGHT
		consumer.vertex(matrix4, x3, y3, z3).color(red, green, blue, alpha).uv(u3, v3)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, normal1, normal2, normal3).endVertex();
		//TOP RIGHT
		consumer.vertex(matrix4, x4, y4, z4).color(red, green, blue, alpha).uv(u4, v4)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3, normal1, normal2, normal3).endVertex();
	}
	
	public static void createQuad(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int light, 
			float normal1, float normal2, float normal3,
			float x1, float y1, float z1, float u1, float v1, 
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3,
			float x4, float y4, float z4, float u4, float v4)
	{
		createQuad(consumer, matrix4, matrix3, light,
				normal1, normal2, normal3,
				1.0F, 1.0F, 1.0F, 1.0F,
				x1, y1, z1, u1, v1,
				x2, y2, z2, u2, v2,
				x3, y3, z3, u3, v3,
				x4, y4, z4, u4, v4);
	}
	
	public static float circumcircleRadius(float phi, float baseWidth)
	{
		return baseWidth / (2 * (float) Math.sin(Math.toRadians(phi / 2)));
	}
	
	public static float[][] coordinates(int sides, float distanceFromCenter, float defaultDistance, float xyOffset)
	{
		float angle = (float) 360 / sides;
		float[][] coordinates = new float[sides][2];
		float baseWidth = 9.8F / 16;
		float ratio = distanceFromCenter / defaultDistance;
		float usedWidth = baseWidth * ratio;
		
		float circumcircleRadius = circumcircleRadius(angle, usedWidth);
		
		if(distanceFromCenter >= 0)
		{
			for(int i = 0; i < sides; i++)
			{
				coordinates[i][0] = CoordinateHelper.CoordinateSystems.polarToCartesianY(circumcircleRadius, angle * i - xyOffset);
				coordinates[i][1] = CoordinateHelper.CoordinateSystems.polarToCartesianX(circumcircleRadius, angle * i - xyOffset);
			}
		}
		else
		{
			coordinates[0][0] = 0;
			coordinates[0][1] = 0;
		}
		
		return coordinates;
	}
	
	public static float getSideWidth(int sides, float defaultDistance)
	{
		return 2 * defaultDistance * (float) Math.tan(Math.toRadians(180) / sides);
	}
	
	public static float getUsedWidth(int sides, float distanceFromCenter, float defaultDistance)
	{
		float sideWidth = getSideWidth(sides, defaultDistance);
		float ratio = distanceFromCenter / defaultDistance;
		
		return sideWidth * ratio;
	}
}
