package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

public class ShieldModel
{
	private static final ResourceLocation SHIELD_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/shield/shield.png");
	
	private static final float OFFSET = 1F / 16 / 2;
	private static final int TOTAL_SIDES = 36;
	
	protected float[][] outerCircle = SGJourneyModel.coordinates(TOTAL_SIDES, 2.0F, 2.5F, 0, 0);
	
	public ShieldModel(){}
	
	public void renderShield(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();

		VertexConsumer consmer = source.getBuffer(SGJourneyRenderTypes.shield(SHIELD_TEXTURE));
		
		for(int j = 0; j < TOTAL_SIDES; j++)
		{
			SGJourneyModel.createTriangle(consmer, matrix4, matrix3, 
					outerCircle[j % outerCircle.length][0], 
					outerCircle[j % outerCircle.length][1],
					OFFSET,
					
					0,
					0,
					OFFSET,
					
					outerCircle[(j + 1) % outerCircle.length][0], 
					outerCircle[(j + 1) % outerCircle.length][1], 
					OFFSET);
			
			SGJourneyModel.createTriangle(consmer, matrix4, matrix3, 
					outerCircle[(j + 1) % outerCircle.length][0], 
					outerCircle[(j + 1) % outerCircle.length][1], 
					OFFSET,
					
					0,
					0,
					OFFSET,
					
					outerCircle[j % outerCircle.length][0],
					outerCircle[j % outerCircle.length][1],
					OFFSET);
		}
	}
}
