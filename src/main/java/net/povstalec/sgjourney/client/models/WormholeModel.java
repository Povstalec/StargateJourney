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
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;

public class WormholeModel
{
	private static final ResourceLocation EVENT_HORIZON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/event_horizon/event_horizon_idle.png");
	
	private ModelPart eventHorizon;
	private int r, g, b;
	
	private float scale = 0.03125F;
	
	public WormholeModel(ModelPart eventHorizon, int r, int g, int b)
	{
		this.eventHorizon = eventHorizon;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void renderEventHorizon(PoseStack stack,
			MultiBufferSource source, int combinedLight, int combinedOverlay, int tickCount)
	{
		VertexConsumer event_horizon_texture = source.getBuffer(SGJourneyRenderTypes.eventHorizon(EVENT_HORIZON_TEXTURE, 0.0F, (float)tickCount * this.scale));
		this.eventHorizon.render(stack, event_horizon_texture, 255, combinedOverlay, r/255.0F, g/255.0F, b/255.0F, 1.0F);
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
