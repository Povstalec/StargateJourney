package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

public class IrisModel
{
private static final ResourceLocation IRIS_TEXTURE = new ResourceLocation("textures/block/iron_block.png");//new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/iris/iron_iris.png");
	
	private static final float OFFSET = 1F / 16 / 2;
	private static final int TOTAL_SIDES = 20;
	
	public static final float IRIS_BLADE_WIDTH = 0.792F;
	public static final float IRIS_BLADE_WIDTH_HALF = IRIS_BLADE_WIDTH / 2;
	
	public static final float IRIS_BLADE_LENGTH = 2.5F;
	
	public IrisModel(){}
	
	public void renderIris(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.iris(IRIS_TEXTURE));
		
		stack.pushPose();
		
		for(int j = 0; j < TOTAL_SIDES; j++)
		{
			stack.pushPose();
			
			stack.translate(IRIS_BLADE_WIDTH_HALF, IRIS_BLADE_LENGTH, 0);
			stack.mulPose(Axis.YP.rotationDegrees(-0.2F)); // -1.0F
			stack.mulPose(Axis.ZP.rotationDegrees(-20.0F)); // -82.0F
			
			Matrix4f matrix4 = stack.last().pose();
			Matrix3f matrix3 = stack.last().normal();
			
			// Front
			SGJourneyModel.createTriangle(consumer, matrix4, matrix3, combinedLight,
					0, 0, 1,
					
					-IRIS_BLADE_WIDTH,
					0,
					OFFSET,
					1, 0,
					
					-IRIS_BLADE_WIDTH_HALF,
					-IRIS_BLADE_LENGTH,
					OFFSET,
					1, 1,
					
					0,
					0,
					OFFSET,
					0, 0);
			
			// Back
			SGJourneyModel.createTriangle(consumer, matrix4, matrix3, combinedLight, 
					0, 0, -1,
					-IRIS_BLADE_WIDTH_HALF,
					-IRIS_BLADE_LENGTH,
					OFFSET,
					1, 1,
					
					-IRIS_BLADE_WIDTH,
					0,
					OFFSET,
					1, 0,
					
					0,
					0,
					OFFSET,
					0, 0);
			
			stack.popPose();
			
			stack.mulPose(Axis.ZP.rotationDegrees(18));
		}
		
		stack.popPose();
	}
}
