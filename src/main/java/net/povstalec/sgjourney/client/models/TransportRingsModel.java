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
import net.povstalec.sgjourney.block_entities.TransportRingsEntity;

public class TransportRingsModel
{
	private static final ResourceLocation TRANSPORT_RINGS_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/block/transport_rings.png");
	
	private final ModelPart firstRing;
	private final ModelPart secondRing;
	private final ModelPart thirdRing;
	private final ModelPart fourthRing;
	private final ModelPart fifthRing;
	
	public TransportRingsModel(ModelPart ring)
	{
		this.firstRing = ring;
		this.secondRing = ring;
		this.thirdRing = ring;
		this.fourthRing = ring;
		this.fifthRing = ring;
	}
	
	public void setRingHeight(int ring, float height)
	{
		switch(ring)
		{
		case 1:
			firstRing.setPos(0.0F, height, 0.0F);
			break;
		case 2:
			secondRing.setPos(0.0F, height, 0.0F);
			break;
		case 3:
			thirdRing.setPos(0.0F, height, 0.0F);
			break;
		case 4:
			fourthRing.setPos(0.0F, height, 0.0F);
			break;
		case 5:
			fifthRing.setPos(0.0F, height, 0.0F);
			break;
		}
	}
	
	public void renderTransportRings(TransportRingsEntity transportRings, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(TRANSPORT_RINGS_TEXTURE));
		
		this.firstRing.render(stack, ringTexture, combinedLight, combinedOverlay);
		this.secondRing.render(stack, ringTexture, combinedLight, combinedOverlay);
		this.thirdRing.render(stack, ringTexture, combinedLight, combinedOverlay);
		this.fourthRing.render(stack, ringTexture, combinedLight, combinedOverlay);
		this.fifthRing.render(stack, ringTexture, combinedLight, combinedOverlay);
	}
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================
	
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition ring = partdefinition.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        /*PartDefinition second_ring = partdefinition.addOrReplaceChild("second_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition third_ring = partdefinition.addOrReplaceChild("third_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition fourth_ring = partdefinition.addOrReplaceChild("fourth_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition fifth_ring = partdefinition.addOrReplaceChild("fifth_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));*/
		
        createRing(ring);
        /*createRing(second_ring);
        createRing(third_ring);
        createRing(fourth_ring);
        createRing(fifth_ring);*/
		
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
