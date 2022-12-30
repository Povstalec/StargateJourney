package net.povstalec.sgjourney.client.models;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.povstalec.sgjourney.config.ClientStargateConfig;

public class StargateModel
{
	public static LayerDefinition createChevronLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		//Top Chevron
		if(ClientStargateConfig.use_movie_stargate_model.get())
			createPrimaryOuterChevron(partdefinition.addOrReplaceChild("chevron9_front", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F)));
		else
			createOuterChevron(partdefinition.addOrReplaceChild("chevron9_front", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F)));
		createChevronLight(partdefinition.addOrReplaceChild("chevron9_light", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F)));
		
		for(int i = 1; i <= 3; i++)
		{
			createOuterChevron(partdefinition.addOrReplaceChild("chevron" + i + "_front", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i))));
			createChevronLight(partdefinition.addOrReplaceChild("chevron" + i + "_light", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i))));
		}
		for(int i = 4; i <= 6; i++)
		{
			createOuterChevron(partdefinition.addOrReplaceChild("chevron" + i + "_front", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i-80 ))));
			createChevronLight(partdefinition.addOrReplaceChild("chevron" + i + "_light", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i-80 ))));
		}
		for(int i = 7; i <= 8; i++)
		{
			createOuterChevron(partdefinition.addOrReplaceChild("chevron" + i + "_front", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i + 120))));
			createChevronLight(partdefinition.addOrReplaceChild("chevron" + i + "_light", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i + 120))));
		}
		
		for(int i = 1; i <= 9; i++)
		{
			createBackChevron(partdefinition.addOrReplaceChild("chevron" + i + "_back", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i))));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	private static void createChevronLight(PartDefinition chevron)
	{
		chevron.addOrReplaceChild("chevron_top", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(-3.0F, 56.0F, 0.5F, 6.0F, 1.0F, 4.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevron.addOrReplaceChild("chevron_center", CubeListBuilder.create()
				.texOffs(22, 5)
				.addBox(-1.5F, 50.0F, 0.5F, 3.0F, 6.0F, 4.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevron.addOrReplaceChild("chevron_right", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(-2F, -6.1847F, 0.0F, 2.0F, 6.1847F, 4.0F),
				PartPose.offsetAndRotation(3.0F, 56.0F, 0.5F, 0.0F, 0.0F, (float) Math.toRadians(-15)));
		chevron.addOrReplaceChild("chevron_left", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(0.0F, -6.1847F, 0.0F, 2.0F, 6.1847F, 4.0F),
				PartPose.offsetAndRotation(-3.0F, 56.0F, 0.5F, 0.0F, 0.0F, (float) Math.toRadians(15)));
	}
	
	private static void createOuterChevron(PartDefinition chevron)
	{
		chevron.addOrReplaceChild("chevron_f", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(-2.0F, 46.0F, 3.5F, 4.0F, 2.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevron.addOrReplaceChild("chevron_right_f", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		chevron.addOrReplaceChild("chevron_left_f", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(0.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
	}
	
	private static void createBackChevron(PartDefinition chevron)
	{
		chevron.addOrReplaceChild("chevron_b", CubeListBuilder.create()
				.texOffs(0, 29)
				.addBox(-2.0F, 46.0F, 0.0F, 4.0F, 2.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, -4.5F));
		chevron.addOrReplaceChild("chevron_right_b", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		chevron.addOrReplaceChild("chevron_left_b", CubeListBuilder.create()
				.texOffs(10, 29)
				.addBox(0.0F, 0.0F, 0.0F, 2.0F, 10.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
		
		chevron.addOrReplaceChild("chevron_b_top", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, 56.0F, 0.5F, 6.0F, 1.0F, 5.0F), 
				PartPose.offset(0.0F, 0.0F, -5.0F));
		chevron.addOrReplaceChild("chevron_b_center", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(-1.5F, 50.0F, 0.5F, 3.0F, 6.0F, 5.0F), 
				PartPose.offset(0.0F, 0.0F, -5.0F));
		chevron.addOrReplaceChild("chevron_b_right", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-2F, -6.1847F, 0.0F, 2.0F, 6.1847F, 5.0F),
				PartPose.offsetAndRotation(3.0F, 56.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(-15)));
		chevron.addOrReplaceChild("chevron_b_left", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(0.0F, -6.1847F, 0.0F, 2.0F, 6.1847F, 5.0F),
				PartPose.offsetAndRotation(-3.0F, 56.0F, -4.5F, 0.0F, 0.0F, (float) Math.toRadians(15)));
	}
	
	private static void createPrimaryOuterChevron(PartDefinition chevron)
	{
		chevron.addOrReplaceChild("chevron_right_f", CubeListBuilder.create()
				.texOffs(16, 29)
				.addBox(-1.0F, 2.0F, 0.0F, 3.0F, 8.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		chevron.addOrReplaceChild("chevron_right_f2", CubeListBuilder.create()
				.texOffs(25, 29)
				.addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		chevron.addOrReplaceChild("chevron_right_f3", CubeListBuilder.create()
				.texOffs(31, 29)
				.addBox(1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		
		chevron.addOrReplaceChild("chevron_left_f", CubeListBuilder.create()
				.texOffs(16, 29)
				.addBox(-2.0F, 2.0F, 0.0F, 3.0F, 8.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
		chevron.addOrReplaceChild("chevron_left_f2", CubeListBuilder.create()
				.texOffs(25, 29)
				.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
		chevron.addOrReplaceChild("chevron_left_f3", CubeListBuilder.create()
				.texOffs(31, 29)
				.addBox(-2.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
	}
	
	/**
	 * 
	 * @param symbolCount Number of symbols(inluding the Point of Origin)
	 * @return
	 */
	public static LayerDefinition createSymbolLayer(int symbolCount)
	{
		double angle = (double)360 / symbolCount;
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition ring = partdefinition.addOrReplaceChild("symbols", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        
		for(int i = 0; i < symbolCount; i++)
		{
			ring.addOrReplaceChild("symbol" + i, CubeListBuilder.create()
					.texOffs(-4, 6)
					.addBox(-4.0F, -50.0F, 0.5F, 8.0F, 8.0F, 2.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - angle * i)));
		}
		
		return LayerDefinition.create(meshdefinition, 8, 8);
	}
	
	public static LayerDefinition createDividerLayer(int symbolCount)
	{
		double angle = (double)360 / symbolCount;
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition ring = partdefinition.addOrReplaceChild("dividers", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		for(int i = 0; i < symbolCount; i++)
		{
			ring.addOrReplaceChild("divider" + i, CubeListBuilder.create()
					.texOffs(34, 34)
					.addBox(-0.5F, -50.0F, 0.5F, 1.0F, 8.0F, 2.5F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - (angle/2 + angle * i))));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition ring = partdefinition.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        
		for(int i = 0; i < 9; i++)
		{
			ring.addOrReplaceChild("outer_ring" + 4*i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * 4*i)));
			ring.addOrReplaceChild("outer_ring" + (4*i + 1), CubeListBuilder.create()
					.texOffs(0, 42)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 1))));
			ring.addOrReplaceChild("outer_ring" + (4*i + 2), CubeListBuilder.create()
					.texOffs(0, 28)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 2))));
			ring.addOrReplaceChild("outer_ring" + (4*i + 3), CubeListBuilder.create()
					.texOffs(0, 14)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 7.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 3))));
			
			ring.addOrReplaceChild("back_ring" + 4*i, CubeListBuilder.create()
					.texOffs(34, -2)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * 4*i)));
			ring.addOrReplaceChild("back_ring" + (4*i + 1), CubeListBuilder.create()
					.texOffs(34, 6)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 1))));
			ring.addOrReplaceChild("back_ring" + (4*i + 2), CubeListBuilder.create()
					.texOffs(34, 14)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 2))));
			ring.addOrReplaceChild("back_ring" + (4*i + 3), CubeListBuilder.create()
					.texOffs(34, 6)
					.addBox(-4.5F, -49.0F, -3.5F, 9.0F, 6.0F, 4.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 3))));
		}
		
		for(int i = 0; i < 36; i++)
		{
			ring.addOrReplaceChild("inner_ring" + i, CubeListBuilder.create()
					.texOffs(34, 24)
					.addBox(-4.0F, -43.0F, -3.5F, 8.0F, 3.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static LayerDefinition createEventHorizonLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		partdefinition.addOrReplaceChild("event_horizon", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-40.0F, -40.0F, 0.0F, 80.0F, 80.0F, 0.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		
		return LayerDefinition.create(meshdefinition, 160, 512);
	}
}
