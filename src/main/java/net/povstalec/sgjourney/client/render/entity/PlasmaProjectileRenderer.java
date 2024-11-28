package net.povstalec.sgjourney.client.render.entity;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.PlasmaProjectile;

public class PlasmaProjectileRenderer extends EntityRenderer<PlasmaProjectile>
{
	protected static final int MAX_LIGHT = 15728864;
	
	private static final ResourceLocation TEXTURE_LOCATION = StargateJourney.sgjourneyLocation("textures/entity/jaffa_staff_weapon_plasma.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);
	
	public PlasmaProjectileRenderer(EntityRendererProvider.Context context)
	{
		super(context);
	}

	public void render(PlasmaProjectile projectile, float p_114081_, float partialTick, PoseStack stack, MultiBufferSource source, int light)
	{
		stack.pushPose();
		stack.scale(2.0F, 2.0F, 2.0F);
		stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		stack.mulPose(Axis.YP.rotationDegrees(180.0F));
		
		PoseStack.Pose posestack$pose = stack.last();
		Matrix4f matrix4f = posestack$pose.pose();
		Matrix3f matrix3f = posestack$pose.normal();
		VertexConsumer vertexconsumer = source.getBuffer(RENDER_TYPE);
		
		vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 0, 0, 1);
		vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 0, 1, 1);
		vertex(vertexconsumer, matrix4f, matrix3f, 1.0F, 1, 1, 0);
		vertex(vertexconsumer, matrix4f, matrix3f, 0.0F, 1, 0, 0);
		
		stack.popPose();
		
		super.render(projectile, p_114081_, partialTick, stack, source, light);
	}

	private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float p_114094_, int p_114095_, int p_114096_, int p_114097_)
	{
		consumer.vertex(matrix4f, p_114094_ - 0.5F, (float)p_114095_ - 0.25F, 0.0F)
		.color(255, 255, 255, 255)
		.uv((float)p_114096_, (float)p_114097_)
		.overlayCoords(OverlayTexture.NO_OVERLAY)
		.uv2(MAX_LIGHT).normal(matrix3f, 0.0F, 1.0F, 0.0F)
		.endVertex();
	}
	   
	@Override
	public ResourceLocation getTextureLocation(PlasmaProjectile p_114482_) 
	{
		return TEXTURE_LOCATION;
	}
}
