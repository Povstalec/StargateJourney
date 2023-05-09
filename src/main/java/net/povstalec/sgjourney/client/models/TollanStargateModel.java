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
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class TollanStargateModel extends AbstractStargateModel
{
	private static final String CHEVRON = ClientStargateConfig.tollan_stargate_back_lights_up.get() ? "tollan_chevron" : "tollan_chevron_front";
	private static final ResourceLocation RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_outer_ring.png");
	private static final ResourceLocation SYMBOL_RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_inner_ring.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/" + CHEVRON + ".png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/" + CHEVRON + "_lit.png");

	private static final float OUTER_RADIUS = 48.0F;
	private static final float SYMBOL_RING_RADIUS = 46.0F;
	private static final float CHEVRON_FRAME_RADIUS = 39.0F;
	private static final float CHEVRON_LIGHT_RADIUS = 46.0F;
	private static final float BACK_RING_RADIUS = 45.0F;
	private static final float INNER_RING_RADIUS = 43.0F;
	public static final float OUTER_RING_SEGMENT_WIDTH = 8.5F; // 10.0F
	public static final float CHEVRON_FRAME_LENGTH = 10.0F;
	public static final float CHEVRON_LIGHT_LENGTH = 3.0F;
	public static final float CHEVRON_LIGHT_WIDTH = 4.0F;
	public static final float OUTER_RING_LENGTH = 3.0F;
	public static final float RING_DEPTH = 5.0F;

	private final ModelPart ring;
	private final ModelPart symbolRing;
	private final ModelPart chevrons;

	private static final int symbolCount = 39;

	public TollanStargateModel(ModelPart ring, ModelPart symbolRing, ModelPart chevrons)
	{
		this.ring = ring;
		this.symbolRing = symbolRing;
		this.chevrons = chevrons;
	}
	
	public void renderStargate(TollanStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source,
							   int combinedLight, int combinedOverlay)
	{
		this.renderRing(stargate, stack, source, combinedLight, combinedOverlay, false);
		
		this.renderSymbolRing(stargate, stack, source, combinedLight, combinedOverlay);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	protected void renderRing(TollanStargateEntity stargate, PoseStack stack, MultiBufferSource source,
			int combinedLight, int combinedOverlay, boolean isBottomCovered)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
		ModelPart outerRing = this.ring.getChild("outer_ring");
		ModelPart backRing = this.ring.getChild("back_ring");
		ModelPart innerRing = this.ring.getChild("inner_ring");
		
		int start = 0;
		
		if(isBottomCovered)
			start = 1;
		
		for(int i = start; i < 36; i++)
		{
			outerRing.getChild("outer_ring_" + i).render(stack, ringTexture, combinedLight, combinedOverlay);
		}
		for(int i = 0; i < 36; i++)
		{
			backRing.getChild("back_ring_" + i).render(stack, ringTexture, combinedLight, combinedOverlay);
		}
		for(int i = start; i < 36; i++)
		{
			innerRing.getChild("inner_ring_" + i).render(stack, ringTexture, combinedLight, combinedOverlay);
		}
	}
	
	protected void renderSymbolRing(TollanStargateEntity stargate, PoseStack stack, MultiBufferSource source,
			int combinedLight, int combinedOverlay) {
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));

		VertexConsumer symbolRingTexture = source.getBuffer(RenderType.entitySolid(SYMBOL_RING_TEXTURE));

		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
		}
	}

	protected ModelPart getSymbol(int symbol)
	{
		return this.symbolRing.getChild("symbol_" + symbol);
	}

	protected void renderChevrons(TollanStargateEntity stargate, PoseStack stack, MultiBufferSource source,
			int combinedLight, int combinedOverlay)
	{
		this.renderTollanPrimaryChevron(stargate, stack, source, combinedLight, combinedOverlay);
		
		for(int i = 1; i <= 8; i++)
		{
			this.renderChevron(stargate, stack, source, combinedLight, combinedOverlay, i);
		}
	}
	
	protected ModelPart getChevron(int chevron)
	{
		return this.chevrons.getChild("chevron_" + chevron);
	}
	
	protected void renderChevron(TollanStargateEntity stargate, PoseStack stack, MultiBufferSource source,
			int combinedLight, int combinedOverlay, int chevronNumber)
	{
		VertexConsumer chevronTexture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
		this.getChevron(chevronNumber).render(stack, chevronTexture, combinedLight, combinedOverlay);
		
		if(stargate.chevronsRendered() >= chevronNumber)
		{
			VertexConsumer engagedChevronTexture = source.getBuffer(SGJourneyRenderTypes.stargateChevron(ENGAGED_CHEVRON_TEXTURE));
			this.getChevron(chevronNumber).render(stack, engagedChevronTexture, 255, combinedOverlay);
		}
	}
	
	protected void renderTollanPrimaryChevron(TollanStargateEntity stargate, PoseStack stack, MultiBufferSource source,
											  int combinedLight, int combinedOverlay)
	{
		VertexConsumer chevron_texture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));
	    this.getChevron(0).render(stack, chevron_texture, combinedLight, combinedOverlay);
		
		if(stargate.isConnected())
		{
			VertexConsumer engaged_chevron_texture = source.getBuffer(SGJourneyRenderTypes.stargateChevron(ENGAGED_CHEVRON_TEXTURE));
		    this.getChevron(0).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
	}
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================


	public static void createOuterRing(PartDefinition outerRing)
	{
		for(int i = 0; i < 9; i++)
		{
			outerRing.addOrReplaceChild("outer_ring_" + 4 * i, CubeListBuilder.create()
							.texOffs(0, 0)
							.addBox(-OUTER_RING_SEGMENT_WIDTH/2, -OUTER_RADIUS, -3.5F, OUTER_RING_SEGMENT_WIDTH, OUTER_RING_LENGTH, RING_DEPTH),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * 4 * i)));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 1), CubeListBuilder.create()
							.texOffs(0, 42)
							.addBox(-OUTER_RING_SEGMENT_WIDTH/2, -OUTER_RADIUS, -3.5F, OUTER_RING_SEGMENT_WIDTH, OUTER_RING_LENGTH, RING_DEPTH),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 1))));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 2), CubeListBuilder.create()
							.texOffs(0, 28)
							.addBox(-OUTER_RING_SEGMENT_WIDTH/2, -OUTER_RADIUS, -3.5F, OUTER_RING_SEGMENT_WIDTH, OUTER_RING_LENGTH, RING_DEPTH),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 2))));
			outerRing.addOrReplaceChild("outer_ring_" + (4 * i + 3), CubeListBuilder.create()
							.texOffs(0, 14)
							.addBox(-OUTER_RING_SEGMENT_WIDTH/2, -OUTER_RADIUS, -3.5F, OUTER_RING_SEGMENT_WIDTH, OUTER_RING_LENGTH, RING_DEPTH),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 3))));
		}
	}

	public static void createBackRing(PartDefinition backRing)
	{
		for(int i = 0; i < 9; i++)
		{
			backRing.addOrReplaceChild("back_ring_" + 4 * i, CubeListBuilder.create()
							.texOffs(34, -2)
							.addBox(-4.5F, -BACK_RING_RADIUS, -3.5F, 9.0F, 6.0F, 4.0F),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * 4 * i)));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 1), CubeListBuilder.create()
							.texOffs(34, 6)
							.addBox(-4.5F, -BACK_RING_RADIUS, -3.5F, 9.0F, 6.0F, 4.0F),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 1))));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 2), CubeListBuilder.create()
							.texOffs(34, 14)
							.addBox(-4.5F, -BACK_RING_RADIUS, -3.5F, 9.0F, 6.0F, 4.0F),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4 * i + 2))));
			backRing.addOrReplaceChild("back_ring_" + (4 * i + 3), CubeListBuilder.create()
							.texOffs(34, 6)
							.addBox(-4.5F, -BACK_RING_RADIUS, -3.5F, 9.0F, 6.0F, 4.0F),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * (4*i + 3))));
		}
	}

	public static void createInnerRing(PartDefinition innerRing)
	{
		for(int i = 0; i < 36; i++)
		{
			innerRing.addOrReplaceChild("inner_ring_" + i, CubeListBuilder.create()
							.texOffs(34, 25)
							.addBox(-4.0F, -INNER_RING_RADIUS, -3.5F, 8.0F, 3.0F, RING_DEPTH),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
		}
	}

	public static void createRing(PartDefinition ring)
	{
		PartDefinition outerRing = ring.addOrReplaceChild("outer_ring", CubeListBuilder.create(), PartPose.ZERO);
		createOuterRing(outerRing);

		PartDefinition backRing = ring.addOrReplaceChild("back_ring", CubeListBuilder.create(), PartPose.ZERO);
		createBackRing(backRing);

		PartDefinition innerRing = ring.addOrReplaceChild("inner_ring", CubeListBuilder.create(), PartPose.ZERO);
		createInnerRing(innerRing);
	}

	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition ring = meshdefinition.getRoot();
        
		createRing(ring);
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public static void createSymbolRing(PartDefinition symbolRing, int symbolCount)
	{
		double angle = (double) 360 / symbolCount;
		for(int i = 0; i < symbolCount; i++)
		{
			symbolRing.addOrReplaceChild("symbol_" + i, CubeListBuilder.create()
							.texOffs(-4, 6)
							.addBox(-3.0F, -SYMBOL_RING_RADIUS, 0.5F, 8.0F, 4.0F, 0.5F),
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - angle * i)));
		}
	}

	public static LayerDefinition createSymbolRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition symbolRing = meshdefinition.getRoot();
        
		createSymbolRing(symbolRing, symbolCount);
		
		return LayerDefinition.create(meshdefinition, 8, 8);
	}
	
	public static LayerDefinition createChevronLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition chevrons = meshdefinition.getRoot();
		
		for(int i = 0; i <= 3; i++)
		{
			createChevron(chevrons.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i))));
		}
		for(int i = 4; i <= 6; i++)
		{
			createChevron(chevrons.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i - 80 ))));
		}
		for(int i = 7; i <= 8; i++)
		{
			createChevron(chevrons.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i + 120))));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}


	public static void createChevron(PartDefinition chevron)
	{
		//	 _
		//	|_|
		//
		PartDefinition chevronLight = chevron.addOrReplaceChild("chevron_light", CubeListBuilder.create(), PartPose.ZERO);
		createChevronLight(chevronLight);

		//
		//	|__|
		//
		PartDefinition outerChevron = chevron.addOrReplaceChild("outer_chevron", CubeListBuilder.create(), PartPose.ZERO);
		createOuterChevron(outerChevron);

		PartDefinition backChevron = chevron.addOrReplaceChild("back_chevron", CubeListBuilder.create(), PartPose.ZERO);
		createBackChevron(backChevron);
	}

	public static void createChevronLight(PartDefinition chevronLight)
	{
		chevronLight.addOrReplaceChild("chevron_center", CubeListBuilder.create()
						.texOffs(22, 5)
						.addBox(-CHEVRON_LIGHT_WIDTH/2, CHEVRON_LIGHT_RADIUS, 0.5F, CHEVRON_LIGHT_WIDTH, CHEVRON_LIGHT_LENGTH, 2.0F),
				PartPose.offset(0.0F, 0.0F, 0.0F));
	}

	public static void createOuterChevron(PartDefinition outerChevron)
	{
		outerChevron.addOrReplaceChild("chevron_f", CubeListBuilder.create()
						.texOffs(0, 29)
						.addBox(-2.0F, CHEVRON_FRAME_RADIUS, 3.5F, 4.0F, 2.0F, 1.0F),
				PartPose.offset(0.0F, 0.0F, -2.0F));
		outerChevron.addOrReplaceChild("chevron_right_f", CubeListBuilder.create()
						.texOffs(10, 29)
						.addBox(-2.0F, 0.0F, 0.0F, 2.0F, CHEVRON_FRAME_LENGTH, 4.0F),
				PartPose.offset(4.0F, CHEVRON_FRAME_RADIUS, -1.5F));
		outerChevron.addOrReplaceChild("chevron_left_f", CubeListBuilder.create()
						.texOffs(10, 29)
						.addBox(0.0F, 0.0F, 0.0F, 2.0F, CHEVRON_FRAME_LENGTH, 4.0F),
				PartPose.offset(-4.0F, CHEVRON_FRAME_RADIUS, -1.5F));
	}

	public static void createBackChevron(PartDefinition backChevron)
	{
		backChevron.addOrReplaceChild("chevron_b", CubeListBuilder.create()
						.texOffs(0, 40)
						.addBox(-2.0F, CHEVRON_FRAME_RADIUS, 0.0F, 4.0F, 2.0F, 1.0F),
				PartPose.offset(0.0F, 0.0F, -4.5F));
		backChevron.addOrReplaceChild("chevron_right_b", CubeListBuilder.create()
						.texOffs(10, 40)
						.addBox(-2.0F, 0.0F, 0.0F, 2.0F, CHEVRON_FRAME_LENGTH, 3.0F),
				PartPose.offset(4.0F, CHEVRON_FRAME_RADIUS, -4.5F));
		backChevron.addOrReplaceChild("chevron_left_b", CubeListBuilder.create()
						.texOffs(10, 40)
						.addBox(0.0F, 0.0F, 0.0F, 2.0F, CHEVRON_FRAME_LENGTH, 3.0F),
				PartPose.offset(-4.0F, CHEVRON_FRAME_RADIUS, -4.5F));

//		backChevron.addOrReplaceChild("chevron_b_top", CubeListBuilder.create()
//						.texOffs(0, 0)
//						.addBox(-1.5F, OUTER_RADIUS, 0.5F, 3.0F, 1.0F, 5.0F),
//				PartPose.offset(0.0F, 0.0F, -5.0F));
		backChevron.addOrReplaceChild("chevron_b_center", CubeListBuilder.create()
						.texOffs(0, 6)
						.addBox(-CHEVRON_LIGHT_WIDTH/2, CHEVRON_LIGHT_RADIUS, 0.5F, CHEVRON_LIGHT_WIDTH, CHEVRON_LIGHT_LENGTH, 5.0F),
				PartPose.offset(0.0F, 0.0F, -5.0F));
	}

}
