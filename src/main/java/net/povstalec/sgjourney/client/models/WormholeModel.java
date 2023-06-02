package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.render.SGJourneyVertexRenderer;

public class WormholeModel implements SGJourneyVertexRenderer
{
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/event_horizon/event_horizon_idle.png");
	
	protected int r, g, b;
	
	protected float scale = 0.03125F;
	
	protected float[][] coordinates = coordinates(2.5F);
	
	public WormholeModel(int r, int g, int b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void renderEventHorizon(PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay, int tickCount)
	{
		this.renderPuddle(stack, source, (float)tickCount * this.scale);
	}
	
	protected void renderPuddle(PoseStack stack, MultiBufferSource source, float yOffset)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonFront(EVENT_HORIZON_TEXTURE, 0.0F, yOffset));
		
		for(int i = 0; i < 36; i++)
		{
			createTriangle(frontConsumer, matrix4, matrix3, i, 0.0F, true);
		}
		
		VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizonBack(EVENT_HORIZON_TEXTURE, 0.0F, yOffset));
		
		for(int i = 0; i < 36; i++)
		{
			createTriangle(backConsumer, matrix4, matrix3, i, 0.0F, false);
		}
	}
	
	protected float[][] coordinates(float distanceFromCenter)
	{
		float[][] coordinates = new float[36][4];
		float baseWidth = 9.8F / 16;
		float defaultDistance = 3.5F;
		
		float ratio = distanceFromCenter / defaultDistance;
		
		for(int j = 0; j < 36; j++)
		{
			float heightProjection = distanceFromCenter * (float) Math.cos(Math.toRadians(10 * j));
			float widthProjection = distanceFromCenter * (float) Math.sin(Math.toRadians(10 * j));
			
			float usedWidth = (baseWidth * ratio) / 2;

			float height = usedWidth * (float) Math.sin(Math.toRadians(10 * j));
			float width = usedWidth * (float) Math.cos(Math.toRadians(10 * j));
			
			coordinates[j][0] = widthProjection - width;
			coordinates[j][1] = heightProjection + height;
			coordinates[j][2] = widthProjection + width;
			coordinates[j][3] = heightProjection - height;
		}
		
		return coordinates;
	}
	
	protected void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3, int number, float zOffset, boolean facingFront)
	{
		float red = (float)r / 255;
		float green = (float)g / 255;
		float blue = (float)b / 255;
		int i = facingFront ? 1 : -1;
		
		consumer.vertex(matrix4, i * coordinates[number][0], coordinates[number][1], 0).color(red, green, blue, 1.0F).uv(coordinates[number][0] / 2.5F / 2 + 0.5F, coordinates[number][1] / 2.5F / 16 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(matrix4, 0.0F, 0.0F, zOffset).color(red, green, blue, 1.0F).uv(0.5F, 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
		consumer.vertex(matrix4, i * coordinates[number][2], coordinates[number][3], 0).color(red, green, blue, 1.0F).uv(coordinates[number][2] / 2.5F / 2 + 0.5F, coordinates[number][3] / 2.5F / 16 + 0.5F)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3, 0.0F, 1.0F, 0.0F).endVertex();
	}
}
