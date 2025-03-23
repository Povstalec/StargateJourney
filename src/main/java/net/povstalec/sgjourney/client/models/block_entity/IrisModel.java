package net.povstalec.sgjourney.client.models.block_entity;

import java.util.Optional;

import net.povstalec.sgjourney.common.stargate.info.IrisInfo;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;

public class IrisModel
{
	private static final float OFFSET = 1F / 16 / 2;
	private static final int TOTAL_SIDES = 20;
	private static final float DEGREES = (float) 360 / TOTAL_SIDES;
	
	public static final float IRIS_TEXTURE_WIDTH = 256;
	public static final float IRIS_TEXTURE_HEIGHT = 256;
	
	public static final float IRIS_BLADE_WIDTH = 0.792F;
	public static final float IRIS_BLADE_WIDTH_HALF = IRIS_BLADE_WIDTH / 2;
	
	public static final float IRIS_BLADE_LENGTH = 2.5F;
	public static final float IRIS_BLADE_HEIGHT = IRIS_BLADE_LENGTH * 16;
	
	public static final float IRIS_OPEN_DEGREES = 84.0F;
	public static final float IRIS_ROTATE_DEGREES = 1.0F;
	
	public static final float TEXTURE_CENTER_U = 13F / 2 / IRIS_TEXTURE_WIDTH;
	public static final float TEXTURE_TOP_V = 0;
	
	public static final float TEXTURE_LEFT_U = TEXTURE_CENTER_U - IRIS_BLADE_WIDTH * 16 / 2 / IRIS_TEXTURE_WIDTH;
	public static final float TEXTURE_LEFT_V = IRIS_BLADE_HEIGHT / IRIS_TEXTURE_HEIGHT;
	
	public static final float TEXTURE_RIGHT_U = TEXTURE_CENTER_U + IRIS_BLADE_WIDTH * 16 / 2 / IRIS_TEXTURE_WIDTH;
	public static final float TEXTURE_RIGHT_V = IRIS_BLADE_HEIGHT / IRIS_TEXTURE_HEIGHT;
	
	private boolean renderWhenOpen;
	private float maxOpenDegrees;
	
	public IrisModel(boolean renderWhenOpen, float maxOpenDegrees)
	{
		this.renderWhenOpen = renderWhenOpen;
		this.maxOpenDegrees = maxOpenDegrees;
	}
	
	public void renderIris(IrisInfo.Interface irisStargate, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay, float progress)
	{
		float closingProgress = (float) (ShieldingState.MAX_PROGRESS - progress) / ShieldingState.MAX_PROGRESS;
		
		Optional<ResourceLocation> irisTexture = irisStargate.irisInfo().getIrisTexture();
		
		if(!this.renderWhenOpen && progress == 0 || irisTexture.isEmpty())
			return;
		
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.iris(irisTexture.get()));
		
		stack.pushPose();
		
		for(int j = 0; j < TOTAL_SIDES; j++)
		{
			stack.pushPose();
			
			stack.translate(IRIS_BLADE_WIDTH_HALF, IRIS_BLADE_LENGTH, 0);
			stack.mulPose(Axis.YP.rotationDegrees(-IRIS_ROTATE_DEGREES * closingProgress));
			stack.mulPose(Axis.ZP.rotationDegrees(-maxOpenDegrees * closingProgress));
			
			Matrix4f matrix4 = stack.last().pose();
			PoseStack.Pose pose = stack.last();
			
			float uAddition = 13 * (j % 10) / IRIS_TEXTURE_WIDTH;
			float vAddition = 40 * (j / 10) / IRIS_TEXTURE_WIDTH;
			
			// Front
			SGJourneyModel.createTriangle(consumer, matrix4, pose, combinedLight,
					0, 0, 1,
					
					-IRIS_BLADE_WIDTH,
					0,
					OFFSET,
					TEXTURE_RIGHT_U + uAddition, TEXTURE_RIGHT_V + vAddition,
					
					-IRIS_BLADE_WIDTH_HALF,
					-IRIS_BLADE_LENGTH,
					OFFSET,
					TEXTURE_CENTER_U + uAddition, TEXTURE_TOP_V + vAddition,
					
					0,
					0,
					OFFSET,
					TEXTURE_LEFT_U + uAddition, TEXTURE_LEFT_V + vAddition);
			
			// Back
			SGJourneyModel.createTriangle(consumer, matrix4, pose, combinedLight,
					0, 0, -1,
					-IRIS_BLADE_WIDTH_HALF,
					-IRIS_BLADE_LENGTH,
					OFFSET,
					TEXTURE_CENTER_U + uAddition, TEXTURE_TOP_V + vAddition,
					
					-IRIS_BLADE_WIDTH,
					0,
					OFFSET,
					TEXTURE_RIGHT_U + uAddition, TEXTURE_RIGHT_V + vAddition,
					
					0,
					0,
					OFFSET,
					TEXTURE_LEFT_U + uAddition, TEXTURE_LEFT_V + vAddition);
			
			stack.popPose();
			
			stack.mulPose(Axis.ZP.rotationDegrees(DEGREES));
		}
		
		stack.popPose();
	}
}
