package net.povstalec.sgjourney.client.models.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;

public class TransportRingModel
{
	private static final ResourceLocation TRANSPORT_RINGS_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/block/goauld_transport_rings.png");
	
	private final ModelPart ring;
	
	public TransportRingModel(ModelPart ring)
	{
		this.ring = ring;
	}
	
	public void render(AbstractTransportRingsEntity transportRings, float partialTick, PoseStack stack, MultiBufferSource source,
					   int combinedLight, int combinedOverlay, float height)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(TRANSPORT_RINGS_TEXTURE));
		
		this.ring.render(stack, ringTexture, combinedLight, combinedOverlay);
	}
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================
	
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		for(int i = 0; i < 16; i++)
		{
			partdefinition.addOrReplaceChild("ring_" + i, CubeListBuilder.create()
							.texOffs(0, 0)
							.addBox(-8.0F, -2.0F, 32.219F, 16.0F, 4.0F, 8.0F),
					PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(22.5 * i), 0.0F));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 32);
	}
}
