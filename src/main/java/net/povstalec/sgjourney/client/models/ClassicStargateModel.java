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
import net.povstalec.sgjourney.block_entities.stargate.ClassicStargateEntity;

public class ClassicStargateModel
{
	private static final ResourceLocation STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_stargate.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_chevron.png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_chevron_lit.png");
	
	private final ModelPart outerRing;
	private final ModelPart innerRing;
	private final ModelPart chevrons;
	
	private float rotation;
	
	public ClassicStargateModel(ModelPart outerRing, ModelPart innerRing, ModelPart chevrons)
	{
		this.outerRing = outerRing;
		this.innerRing = innerRing;
		this.chevrons = chevrons;
	}
	
	public void renderStargate(ClassicStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer stargateTexture = source.getBuffer(RenderType.entitySolid(STARGATE_TEXTURE));
		this.outerRing.render(stack, stargateTexture, combinedLight, combinedOverlay);
		
		this.innerRing.setRotation(0.0F, 0.0F, this.rotation);
		this.innerRing.render(stack, stargateTexture, combinedLight, combinedOverlay);
		
		this.renderChevrons(stack, source, combinedLight, combinedOverlay, stargate.chevronsRendered());
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	protected void renderChevrons(PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, int chevronsActive)
	{
		VertexConsumer chevronTexture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
		
		for(int i = 0; i < 9; i++)
		{
			this.setActiveChevron(chevronsActive, i);
			
			this.getChevron(i).render(stack, chevronTexture, combinedLight, combinedOverlay);
		}
	}
	
	protected void setActiveChevron(int chevronsActive, int chevron)
	{
		if(chevronsActive > 0 && chevron <= chevronsActive)
		{
			if(chevron > 0 && chevron <= 3)
			{
				this.getChevron(chevron).x = (float) (-4 * Math.cos(Math.toRadians(90 - 40 * chevron)));
				this.getChevron(chevron).y = (float) (-4 * Math.sin(Math.toRadians(90 - 40 * chevron)));
			}
			else if(chevron > 3 && chevron <= 6)
			{
				this.getChevron(chevron).x = (float) (-4 * Math.cos(Math.toRadians(10 - 40 * chevron)));
				this.getChevron(chevron).y = (float) (-4 * Math.sin(Math.toRadians(10 - 40 * chevron)));
			}
			else if(chevron > 6 && chevron <= 8)
			{
				this.getChevron(chevron).x = (float) (-4 * Math.cos(Math.toRadians(90 - 40 * (chevron - 3))));
				this.getChevron(chevron).y = (float) (-4 * Math.sin(Math.toRadians(90 - 40 * (chevron - 3))));
			}
		}
		else
		{
			this.getChevron(chevron).x = 0;
			this.getChevron(chevron).y = 0;
		}
	}
	
	protected ModelPart getChevron(int chevron)
	{
		return this.chevrons.getChild("chevron_" + chevron);
	}
	
	public static LayerDefinition createInnerRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition ring = partdefinition.addOrReplaceChild("inner_ring", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		for(int i = 0; i < 36; i++)
		{
			ring.addOrReplaceChild("inner_ring" + i, CubeListBuilder.create()
					.texOffs(0, 16)
					.addBox(-5.0F, -48.0F, -3.5F, 10.0F, 8.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static LayerDefinition createOuterRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		PartDefinition ring = partdefinition.addOrReplaceChild("outer_ring", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		for(int i = 0; i < 36; i++)
		{
			ring.addOrReplaceChild("outer_ring" + i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-5.0F, -56.0F, -4.0F, 10.0F, 8.0F, 8.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static LayerDefinition createChevronLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		for(int i = 0; i <= 3; i++)
		{
			createChevron(partdefinition.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i))));
		}
		for(int i = 4; i <= 6; i++)
		{
			createChevron(partdefinition.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i-80 ))));
		}
		for(int i = 7; i <= 8; i++)
		{
			createChevron(partdefinition.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i + 120))));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	protected static void createChevron(PartDefinition chevron)
	{
		//	_
		//	V
		//	
		chevron.addOrReplaceChild("chevron_light_a", CubeListBuilder.create()
				.texOffs(18, 0)
				.addBox(-4.5F, 0.0F, 0.0F, 9.0F, 2.0F, 2.0F), 
				PartPose.offset(0.0F, 56.0F, 3.5F));
		chevron.addOrReplaceChild("chevron_light_b", CubeListBuilder.create()
				.texOffs(18, 4)
				.addBox(-3.5F, 0.0F, 0.0F, 7.0F, 5.0F, 2.0F), 
				PartPose.offset(0.0F, 51.0F, 3.5F));
		
		//	
		//	\_/
		//	
		PartDefinition chevronBottom = chevron.addOrReplaceChild("chevron_bottom", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 3.0F), 
				PartPose.offset(0.0F, 49.0F, 3.0F));
		chevronBottom.addOrReplaceChild("chevron_right", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(-3.0F, 0.0F, 0.0F, 3.0F, 9.0F, 3.0F), 
				PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		chevronBottom.addOrReplaceChild("chevron_left", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(0.0F, 0.0F, 0.0F, 3.0F, 9.0F, 3.0F), 
				PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
	}
}
