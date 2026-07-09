package net.povstalec.sgjourney.client.models.block_entity;

import com.mojang.blaze3d.vertex.*;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;

public class TransportRingModel<TransportRingsEntity extends AbstractTransportRingsEntity<?>>
{
	public final ResourceLocation texture;
	
	public final int sides;
	public final float height;
	public final float outerRadius;
	public final float innerRadius;
	
	public final float angle;
	public final float outerSideLength;
	public final float innerSideLength;
	
	public final float ringHalfwayPoint;
	public final float dividerLength;
	public final float dividerWidth;
	public final float dividerHeight;
	
	public TransportRingModel(ResourceLocation texture, int sides, float height, float outerRadius, float innerRadius)
	{
		this.texture = texture;
		
		this.sides = sides;
		this.height = height;
		this.outerRadius = outerRadius;
		this.innerRadius = innerRadius;
		
		this.angle = 360F / sides;
		this.outerSideLength = sideLength(sides, outerRadius);
		this.innerSideLength = sideLength(sides, innerRadius);
		
		float ringThickness = outerRadius - innerRadius;
		this.ringHalfwayPoint = ringThickness / 2F + innerRadius;
		this.dividerLength = ringThickness + 1F / 16F;
		this.dividerWidth = 1F / 16F;
		this.dividerHeight = height + 1F / 16F;
	}
	
	public static float sideLength(int sides, float radius)
	{
		return (float) (2 * radius * Math.tan(Math.PI / sides));
	}
	
	private void renderRingSegment(VertexConsumer ringTexture, Matrix4f matrix4, Matrix3f matrix3, int combinedLight)
	{
		// Outer
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, 0, 1,
				-outerSideLength / 2,
				height / 2,
				outerRadius,
				0, 13 / 32F,
				
				-outerSideLength / 2,
				-height / 2,
				outerRadius,
				0 / 64F, 18 / 32F,
				
				outerSideLength / 2,
				-height / 2,
				outerRadius,
				8 / 64F, 18 / 32F,
				
				outerSideLength / 2,
				height / 2,
				outerRadius,
				8 / 64F, 13 / 32F);
		// Inner
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, 0, -1,
				innerSideLength / 2,
				height / 2,
				innerRadius,
				1 / 64F, 0,
				
				innerSideLength / 2,
				-height / 2,
				innerRadius,
				1 / 64F, 5 / 32F,
				
				-innerSideLength / 2,
				-height / 2,
				innerRadius,
				7 / 64F, 5 / 32F,
				
				-innerSideLength / 2,
				height / 2,
				innerRadius,
				7 / 64F, 0);
		// Top
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, 1, 0,
				-innerSideLength / 2,
				height / 2,
				innerRadius,
				1 / 64F, 5 / 32F,
				
				-outerSideLength / 2,
				height / 2,
				outerRadius,
				0, 13 / 32F,
				
				outerSideLength / 2,
				height / 2,
				outerRadius,
				8 / 64F, 13 / 32F,
				
				innerSideLength / 2,
				height / 2,
				innerRadius,
				7 / 64F, 5 / 32F);
		// Bottom
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, -1, 0,
				-outerSideLength / 2,
				-height / 2,
				outerRadius,
				0, 18 / 32F,
				
				-innerSideLength / 2,
				-height / 2,
				innerRadius,
				1 / 64F, 26 / 32F,
				
				innerSideLength / 2,
				-height / 2,
				innerRadius,
				7 / 64F, 26 / 32F,
				
				outerSideLength / 2,
				-height / 2,
				outerRadius,
				8 / 64F, 18 / 32F);
	}
	
	private void renderDivider(VertexConsumer ringTexture, Matrix4f matrix4, Matrix3f matrix3, int combinedLight)
	{
		// Outer
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, 0, 1,
				-dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				17 / 64F, 9 / 32F,
				
				-dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				17 / 64F, 15 / 32F,
				
				dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				18 / 64F, 15 / 32F,
				
				dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				18 / 64F, 9 / 32F);
		// Inner
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, 0, -1,
				dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				26 / 64F, 9 / 32F,
				
				dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				26 / 64F, 15 / 32F,
				
				-dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				27 / 64F, 15 / 32F,
				
				-dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				27 / 64F, 9 / 32F);
		// Top
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, 1, 0,
				-dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				17 / 64F, 0,
				
				-dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				17 / 64F, 9 / 32F,
				
				dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				18 / 64F, 9 / 32F,
				
				dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				18 / 64F, 0);
		// Bottom
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 0, -1, 0,
				-dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				18 / 64F, 0,
				
				-dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				18 / 64F, 9 / 32F,
				
				dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				19 / 64F, 9 / 32F,
				
				dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				19 / 64F, 0);
		// Left
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, -1, 0, 0,
				-dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				8 / 64F, 9 / 32F,
				
				-dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				8 / 64F, 15 / 32F,
				
				-dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				17 / 64F, 15 / 32F,
				
				-dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				17 / 64F, 9 / 32F);
		// Right
		SGJourneyModel.createQuad(ringTexture, matrix4, matrix3, combinedLight, 1, 0, 0,
				dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				18 / 64F, 9 / 32F,
				
				dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint + dividerLength / 2,
				18 / 64F, 15 / 32F,
				
				dividerWidth / 2,
				-dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				27 / 64F, 15 / 32F,
				
				dividerWidth / 2,
				dividerHeight / 2,
				ringHalfwayPoint - dividerLength / 2,
				27 / 64F, 9 / 32F);
	}
	
	/**
	 * Renders the Transport Rings
	 * @param transportRings Transport Rings Entity being rendered
	 * @param partialTick Partial Tick
	 * @param stack Pose Stack
	 * @param source Multi Buffer Source
	 * @param combinedLight Combined Light
	 * @param combinedOverlay Combined Overlay
	 */
	public void render(TransportRingsEntity transportRings, float partialTick, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(texture));
		
		Matrix4f matrix4;
		Matrix3f matrix3;
		
		for(int i = 0; i < sides; i++)
		{
			// Ring Segment
			stack.pushPose();
			stack.mulPose(Vector3f.YP.rotationDegrees(i * angle));
			matrix4 = stack.last().pose();
			matrix3 = stack.last().normal();
			renderRingSegment(ringTexture, matrix4, matrix3, combinedLight);
			
			stack.popPose();
			
			// Divider
			stack.pushPose();
			stack.mulPose(Vector3f.YP.rotationDegrees(i * angle - angle / 2F));
			matrix4 = stack.last().pose();
			matrix3 = stack.last().normal();
			renderDivider(ringTexture, matrix4, matrix3, combinedLight);
			
			stack.popPose();
		}
	}
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================
	
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		float radius = 2.45746F * 16F;
		float sideLength = sideLength(36, radius);
		float height = 5F;
		
		for(int i = 0; i < 36; i++)
		{
			partdefinition.addOrReplaceChild("ring_" + i, CubeListBuilder.create()
							.texOffs(0, 0)
							.addBox(-sideLength / 2F, -height / 2, radius - 8.0F, sideLength, height, 8.0F),
					PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10 * i), 0.0F));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 32);
	}
}
