package net.povstalec.sgjourney.client.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.texture.OverlayTexture;

public interface SGJourneyVertexRenderer
{
	//uv2 - Lightmap
	
	default void renderTriangle(VertexConsumer vertexConsumer, Matrix4f matrix4, Matrix3f matrix3)
	{
		float x = 0;
		float y = 0;
		float z = 0;
		
		vertexConsumer.vertex(matrix4, x - 2.5F, y - 2.5F, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 1.0F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4, x - 2.5F, y + 2.5F, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(1.0F, 0.0F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		vertexConsumer.vertex(matrix4, x + 2.5F, y + 2.5F, z).color(1.0F, 1.0F, 1.0F, 1.0F).uv(0.0F, 0.0F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
	}
}
