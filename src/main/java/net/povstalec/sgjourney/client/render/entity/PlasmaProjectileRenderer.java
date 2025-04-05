package net.povstalec.sgjourney.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

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
	public static final int MAX_LIGHT = 15728864;
	public static final float SIZE = 0.5F;
	
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/jaffa_staff_weapon_plasma.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE_LOCATION);
	
	public PlasmaProjectileRenderer(EntityRendererProvider.Context context)
	{
		super(context);
	}

	public void render(PlasmaProjectile projectile, float p_114081_, float partialTick, PoseStack stack, MultiBufferSource source, int light)
	{
		stack.pushPose();
		
		//stack.scale(2.0F, 2.0F, 2.0F);
		stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		
		PoseStack.Pose posestack$pose = stack.last();
		Matrix4f matrix4f = posestack$pose.pose();
		Matrix3f matrix3f = posestack$pose.normal();
		VertexConsumer vertexconsumer = source.getBuffer(RENDER_TYPE);
		
		vertex(vertexconsumer, matrix4f, matrix3f, -SIZE, -SIZE, 0F, 1F);
		vertex(vertexconsumer, matrix4f, matrix3f, SIZE, -SIZE, 1F, 1F);
		vertex(vertexconsumer, matrix4f, matrix3f, SIZE, SIZE, 1F, 0F);
		vertex(vertexconsumer, matrix4f, matrix3f, -SIZE, SIZE, 0F, 0F);
		
		stack.popPose();
		
		super.render(projectile, p_114081_, partialTick, stack, source, light);
	}
	
	public static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, float u, float v)
	{
		consumer.vertex(matrix4f, x, y + 0.125F, 0F)
		.color(255, 255, 255, 255)
		.uv(u, v)
		.overlayCoords(OverlayTexture.NO_OVERLAY)
		.uv2(MAX_LIGHT).normal(matrix3f, 1F, 1F, 1F)
		.endVertex();
	}
	   
	@Override
	public ResourceLocation getTextureLocation(PlasmaProjectile projectile)
	{
		return TEXTURE_LOCATION;
	}
}
