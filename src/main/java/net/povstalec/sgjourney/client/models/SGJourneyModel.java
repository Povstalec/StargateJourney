package net.povstalec.sgjourney.client.models;

import java.util.Random;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;

public class SGJourneyModel
{
	public static void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, 
			float red, float green, float blue, float alpha,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3)
	{
		consumer.vertex(matrix4, x1, y1, z1).color(red, green, blue, alpha).uv(x1 / 2.5F / 2 + 0.5F, y1 / 2.5F / 2 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		
		consumer.vertex(matrix4, x2, y2, z2).color(red, green, blue, alpha).uv(x2 / 2.5F / 2 + 0.5F, y2 / 2.5F / 2 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		
		consumer.vertex(matrix4, x3, y3, z3).color(red, green, blue, alpha).uv(x3 / 2.5F / 2 + 0.5F, y3 / 2.5F / 2 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
	}
	
	public static void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, 
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3)
	{
		createTriangle(consumer, matrix4, matrix3,
				1.0F, 1.0F, 1.0F, 1.0F,
				x1, y1, z1,
				x2, y2, z2,
				x3, y3, z3);
	}
	
	public static float circumcircleRadius(float phi, float baseWidth)
	{
		return baseWidth / (2 * (float) Math.sin(Math.toRadians(phi / 2)));
	}
	
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
	public static float[][] coordinates(int sides, float distanceFromCenter, float defaultDistance, float offset, int seed)
	{
		Random random = new Random(seed);
		float angle = (float) 360 / sides;
		float[][] coordinates = new float[sides][3];
		float baseWidth = 9.8F / 16;
		float ratio = distanceFromCenter / defaultDistance;
		float usedWidth = baseWidth * ratio;
		
		float circumcircleRadius = circumcircleRadius(angle, usedWidth);
		float defaultOffset = seed == 0 ? 0 : random.nextFloat() - 0.5F;
		
		if(distanceFromCenter >= 0)
		{
			for(int i = 0; i < sides; i++)
			{
				coordinates[i][0] = CoordinateHelper.polarToCartesianY(circumcircleRadius, angle * i - offset);
				coordinates[i][1] = CoordinateHelper.polarToCartesianX(circumcircleRadius, angle * i - offset);
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
}
