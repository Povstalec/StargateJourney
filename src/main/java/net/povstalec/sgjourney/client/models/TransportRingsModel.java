package net.povstalec.sgjourney.client.models;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class TransportRingsModel
{
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition first_ring = partdefinition.addOrReplaceChild("first_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition second_ring = partdefinition.addOrReplaceChild("second_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition third_ring = partdefinition.addOrReplaceChild("third_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition fourth_ring = partdefinition.addOrReplaceChild("fourth_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
        PartDefinition fifth_ring = partdefinition.addOrReplaceChild("fifth_ring", CubeListBuilder.create(), PartPose.offset(8.0F, 0.0F, 8.0F));
		
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
			ring.addOrReplaceChild("ring" + i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-8.0F, 4.0F, 32.219F, 16.0F, 4.0F, 8.0F),
					PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(22.5 * i), 0.0F));
		}
	}
}
