package net.povstalec.sgjourney.client.models;

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
import net.povstalec.sgjourney.common.block_entities.TransportRingsEntity;

public class TransportRingsModel
{
	private static final ResourceLocation TRANSPORT_RINGS_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/block/transport_rings.png");
	
	private final ModelPart transportRings;
	
	public TransportRingsModel(ModelPart transportRings)
	{
		this.transportRings = transportRings;
	}
	
	public void setRingHeight(int ring, float height)
	{
		if(ring < 1 && ring > 5)
			return;
		
		getRing(ring).setPos(0.0F, height, 0.0F);
	}
	
	public ModelPart getRing(int ring)
	{
		return this.transportRings.getChild("transport_ring_" + ring);
	}
	
	public void renderTransportRings(TransportRingsEntity transportRings, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(TRANSPORT_RINGS_TEXTURE));
		
		this.transportRings.render(stack, ringTexture, combinedLight, combinedOverlay);
	}
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================
	
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition first_ring = partdefinition.addOrReplaceChild("transport_ring_1", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition second_ring = partdefinition.addOrReplaceChild("transport_ring_2", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition third_ring = partdefinition.addOrReplaceChild("transport_ring_3", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition fourth_ring = partdefinition.addOrReplaceChild("transport_ring_4", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition fifth_ring = partdefinition.addOrReplaceChild("transport_ring_5", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
		
        createRing(first_ring);
        createRing(second_ring);
        createRing(third_ring);
        createRing(fourth_ring);
        createRing(fifth_ring);
		
		return LayerDefinition.create(meshdefinition, 64, 32);
	}
	
	public static void createRing(PartDefinition ring)
	{
		for(int i = 0; i < 16; i++)
		{
			ring.addOrReplaceChild("ring_" + i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-8.0F, 4.0F, 32.219F, 16.0F, 4.0F, 8.0F),
					PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(22.5 * i), 0.0F));
		}
	}
	
	
}
