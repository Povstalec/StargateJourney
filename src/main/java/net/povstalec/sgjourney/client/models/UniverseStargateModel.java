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
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;

public class UniverseStargateModel extends AbstractStargateModel
{
	private static final ResourceLocation RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_outer_ring.png");
	private static final ResourceLocation SYMBOL_RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_inner_ring.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_chevron.png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_chevron_lit.png");
	
	private final ModelPart ring;
	private final ModelPart symbolRing;
	private final ModelPart dividers;
	private final ModelPart chevrons;
	
	private static final int symbolCount = 36;
	private static final double angle = (float) 360 / 54;
	private float rotation = 0.0F;
	
	public UniverseStargateModel(ModelPart ring, ModelPart symbolRing, ModelPart dividers, ModelPart chevrons)
	{
		super("universe");
		this.ring = ring;
		this.symbolRing = symbolRing;
		this.dividers = dividers;
		this.chevrons = chevrons;
	}
	
	public void renderStargate(UniverseStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		this.renderRing(stargate, stack, source, combinedLight, combinedOverlay);
		
		this.renderSymbolRing(stargate, stack, source, combinedLight, combinedOverlay, combinedOverlay);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay, combinedOverlay);
	}
	
	protected void renderRing(UniverseStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
		
		this.ring.setRotation(0.0F, 0.0F, (float) Math.toRadians(this.rotation));
		this.ring.render(stack, ringTexture, combinedLight, combinedOverlay);
	}
	
	protected void renderSymbolRing(UniverseStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, int chevronsActive)
	{
		VertexConsumer symbolRingTexture = source.getBuffer(RenderType.entitySolid(SYMBOL_RING_TEXTURE));
		
		for(int i = 0; i < 9; i++)
		{
			this.getUnderChevronLeft(i).setRotation(0.0F, 0.0F, (float) Math.toRadians(180 - (angle / 2) - (40 * i) + this.rotation));
			this.getUnderChevronRight(i).setRotation(0.0F, 0.0F, (float) Math.toRadians(180 + (angle / 2) - (40 * i) + this.rotation));
		}
		for(int i = 0; i < 9; i++)
		{
			this.getUnderChevronLeft(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
			this.getUnderChevronRight(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
		}

		for(int i = 0; i < 9; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				this.getSymbol(i * 4 + j).setRotation(0.0F, 0.0F, (float) Math.toRadians(180 - 3 * (angle / 2) - i * 40 - (angle * j) + this.rotation));
			}
		}
		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
		}
		
		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbolTexture(stargate, i))), combinedLight, combinedOverlay, 21.0F/255.0F, 9.0F/255.0F, 0.0F/255.0F, 1.0F);
		}
		
		for(int i = 0; i < stargate.getAddress().length; i++)
	    {
			this.getSymbol(stargate.getAddress()[i]).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbolTexture(stargate, stargate.getAddress()[i]))), 255, combinedOverlay);
	    }
		
		if(stargate.isConnected())
	    	this.getSymbol(0).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbolTexture(stargate, 0))), 255, combinedOverlay);

		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
		this.dividers.setRotation(0.0F, 0.0F, (float) Math.toRadians(this.rotation));
		this.dividers.render(stack, ringTexture, combinedLight, combinedOverlay);
	}
	
	protected void renderChevrons(UniverseStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, int chevronsActive)
	{
		for(int i = 0; i <= 8; i++)
		{
			this.renderChevron(stargate, stack, source, combinedLight, combinedOverlay, i);
		}
	}
	
	protected ModelPart getChevron(int chevron)
	{
		return this.chevrons.getChild("chevron_" + chevron);
	}
	
	protected void renderChevron(UniverseStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, int chevronNumber)
	{
		VertexConsumer chevronTexture = source.getBuffer(RenderType.entitySolid(CHEVRON_TEXTURE));

		this.getChevron(chevronNumber).setRotation(0.0F, 0.0F, (float) Math.toRadians(-40 * chevronNumber + this.rotation));
		this.getChevron(chevronNumber).render(stack, chevronTexture, combinedLight, combinedOverlay);
		
		if(stargate.isConnected() || stargate.addressBuffer.length > 0)
		{
			VertexConsumer engagedChevronTexture = source.getBuffer(SGJourneyRenderTypes.stargateChevron(ENGAGED_CHEVRON_TEXTURE));
			this.getChevron(chevronNumber).render(stack, engagedChevronTexture, 255, combinedOverlay);
		}
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================
	
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition ring = meshdefinition.getRoot();
		
		PartDefinition outerRing = ring.addOrReplaceChild("outer_ring", CubeListBuilder.create(), PartPose.ZERO);
		createOuterRing(outerRing);
		
		PartDefinition backRing = ring.addOrReplaceChild("back_ring", CubeListBuilder.create(), PartPose.ZERO);
		createBackRing(backRing);
		
		PartDefinition innerRing = ring.addOrReplaceChild("inner_ring", CubeListBuilder.create(), PartPose.ZERO);
		createInnerRing(innerRing);
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static void createOuterRing(PartDefinition outerRing)
	{
		for(int i = 0; i < BOXES_PER_RING; i++)
		{
			outerRing.addOrReplaceChild("outer_ring_" + i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 4.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
		}
	}
	
	public static void createBackRing(PartDefinition backRing)
	{
		for(int i = 0; i < 54; i++)
		{
			backRing.addOrReplaceChild("back_ring_" + i, CubeListBuilder.create()
					.texOffs(34, 12)
					.addBox(-3.0F, -53.0F, -2.5F, 6.0F, 9.0F, 3.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-(angle / 2) - (angle * i))));
		}
	}
	
	public static void createInnerRing(PartDefinition innerRing)
	{
		for(int i = 0; i < BOXES_PER_RING; i++)
		{
			innerRing.addOrReplaceChild("inner_ring_" + i, CubeListBuilder.create()
					.texOffs(34, 24)
					.addBox(-4.0F, -44.0F, -3.5F, 8.0F, 4.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
		}
	}
	
	public static LayerDefinition createSymbolRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition symbolRing = meshdefinition.getRoot();
		
		createSymbolRing(symbolRing);
		
		return LayerDefinition.create(meshdefinition, 8, 8);
	}
	
	public static void createSymbolRing(PartDefinition symbolRing)
	{
		for(int i = 0; i < 9; i++)
		{
			symbolRing.addOrReplaceChild("under_chevron_left_" + i, CubeListBuilder.create()
					.texOffs(-1, -3)
					.addBox(-3.0F, -53.0F, 0.5F, 6.0F, 9.0F, 2.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - (angle / 2) - (40 * i))));
		}
		
		for(int i = 0; i < 9; i++)
		{
			symbolRing.addOrReplaceChild("under_chevron_right_" + i, CubeListBuilder.create()
					.texOffs(-1, -3)
					.addBox(-3.0F, -53.0F, 0.5F, 6.0F, 9.0F, 2.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 + (angle / 2) - (40 * i))));
		}
		
		for(int i = 0; i < 9; i++)
		{
			for(int j = 0; j < 4; j++)
			{
				symbolRing.addOrReplaceChild("symbol_" + (i * 4 + j), CubeListBuilder.create()
						.texOffs(-1, -3)
						.addBox(-3.0F, -53.0F, 0.5F, 6.0F, 9.0F, 2.0F), 
						PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - 3 * (angle / 2) - i * 40 - (angle * j))));
			}
		}
	}
	
	protected ModelPart getSymbol(int symbol)
	{
		return this.symbolRing.getChild("symbol_" + symbol);
	}
	
	protected ModelPart getUnderChevronLeft(int chevron)
	{
		return this.symbolRing.getChild("under_chevron_left_" + chevron);
	}
	
	protected ModelPart getUnderChevronRight(int chevron)
	{
		return this.symbolRing.getChild("under_chevron_right_" + chevron);
	}
	
	public static LayerDefinition createDividerLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition dividers = meshdefinition.getRoot();
		
		createDividers(dividers);
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static void createDividers(PartDefinition dividers)
	{
		for(int i = 0; i < 54; i++)
		{
			if(i % 6 == 0)
			{
				dividers.addOrReplaceChild("divider_" + i, CubeListBuilder.create()
						.texOffs(28, 35)
						.addBox(-2.0F, -52.0F, -3.5F, 4.0F, 8.0F, 7.0F), 
						PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - angle * i)));
			}
			else
			{
				dividers.addOrReplaceChild("divider_" + i, CubeListBuilder.create()
						.texOffs(50, 35)
						.addBox(-0.5F, -53.0F, -3.0F, 1.0F, 9.0F, 6.0F), 
						PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - angle * i)));
			}
		}
	}
	
	public static LayerDefinition createChevronLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition chevrons = meshdefinition.getRoot();
		
		for(int i = 0; i <= 8; i++)
		{
			createChevron(chevrons.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i))));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static void createChevron(PartDefinition chevron)
	{
		//	_
		//	V
		//	
		PartDefinition chevronLightFront = chevron.addOrReplaceChild("chevron_light_front", CubeListBuilder.create(), PartPose.offset(0.0F, 54.5F, 4.5F));
		createChevronLight(chevronLightFront);
		PartDefinition chevronLightBack = chevron.addOrReplaceChild("chevron_light_back", CubeListBuilder.create(), PartPose.offset(0.0F, 54.5F, -5.5F));
		createChevronLight(chevronLightBack);
		
		// -_   _-
		//	 - -
		//	
		PartDefinition outerChevronFront = chevron.addOrReplaceChild("outer_chevron_front", CubeListBuilder.create(), PartPose.offset(0.0F, 50.0F, 3.5F));
		createOuterChevron(outerChevronFront);
		PartDefinition outerChevronFrontLights = chevron.addOrReplaceChild("outer_chevron_front_lights", CubeListBuilder.create(), PartPose.offset(0.0F, 50.0F, 4.0F));
		createOuterChevronLights(outerChevronFrontLights);
		PartDefinition outerChevronBack = chevron.addOrReplaceChild("outer_chevron_back", CubeListBuilder.create(), PartPose.offset(0.0F, 50.0F, -4.5F));
		createOuterChevron(outerChevronBack);
		PartDefinition outerChevronBackLights = chevron.addOrReplaceChild("outer_chevron_back_lights", CubeListBuilder.create(), PartPose.offset(0.0F, 50.0F, -5.0F));
		createOuterChevronLights(outerChevronBackLights);
	}
	
	public static void createChevronLight(PartDefinition chevronLight)
	{
		chevronLight.addOrReplaceChild("chevron_top", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_upper", CubeListBuilder.create()
				.texOffs(0, 2)
				.addBox(-2.5F, -1.0F, 0.0F, 5.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_center", CubeListBuilder.create()
				.texOffs(0, 4)
				.addBox(-1.5F, -2.0F, 0.0F, 3.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_bottom", CubeListBuilder.create()
				.texOffs(0, 6)
				.addBox(-1.0F, -4.0F, 0.0F, 2.0F, 2.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_right", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(3.0F, 1.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-40)));
		chevronLight.addOrReplaceChild("chevron_left", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(-3.0F, 1.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(40)));
		chevronLight.addOrReplaceChild("chevron_lower_right", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(1.0F, -4.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-25)));
		chevronLight.addOrReplaceChild("chevron_lower_left", CubeListBuilder.create()
				.texOffs(0, 9)
				.addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(-1.0F, -4.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(25)));
	}
	
	public static void createOuterChevron(PartDefinition outerChevron)
	{
		outerChevron.addOrReplaceChild("outer_chevron_center", CubeListBuilder.create()
				.texOffs(14, 0)
				.addBox(-5.0F, 0.0F, 0.0F, 10.0F, 6.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		outerChevron.addOrReplaceChild("outer_chevron_bottom_A", CubeListBuilder.create()
				.texOffs(20, 7)
				.addBox(-2.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		outerChevron.addOrReplaceChild("outer_chevron_bottom_B", CubeListBuilder.create()
				.texOffs(14, 7)
				.addBox(-1.0F, -7.0F, 0.0F, 2.0F, 6.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		outerChevron.addOrReplaceChild("outer_chevron_bottom_C", CubeListBuilder.create()
				.texOffs(20, 7)
				.addBox(-2.0F, -7.0F, 0.0F, 4.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		
		outerChevron.addOrReplaceChild("outer_chevron_left_top_A", CubeListBuilder.create()
				.texOffs(14, 14)
				.addBox(-10.0F, -1.0F, 0.0F, 10.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		outerChevron.addOrReplaceChild("outer_chevron_left_top_B", CubeListBuilder.create()
				.texOffs(14, 16)
				.addBox(-8.0F, -2.0F, 0.0F, 8.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		outerChevron.addOrReplaceChild("outer_chevron_left_top_C", CubeListBuilder.create()
				.texOffs(14, 18)
				.addBox(-7.0F, -3.0F, 0.0F, 7.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		outerChevron.addOrReplaceChild("outer_chevron_left_A", CubeListBuilder.create()
				.texOffs(14, 20)
				.addBox(-4.0F, -4.0F, 0.0F, 4.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		outerChevron.addOrReplaceChild("outer_chevron_left_B", CubeListBuilder.create()
				.texOffs(14, 22)
				.addBox(-3.0F, -5.0F, 0.0F, 3.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		outerChevron.addOrReplaceChild("outer_chevron_left_C", CubeListBuilder.create()
				.texOffs(14, 24)
				.addBox(-2.0F, -6.0F, 0.0F, 2.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		
		outerChevron.addOrReplaceChild("outer_chevron_right_top_A", CubeListBuilder.create()
				.texOffs(14, 14)
				.addBox(0.0F, -1.0F, 0.0F, 10.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
		outerChevron.addOrReplaceChild("outer_chevron_right_top_B", CubeListBuilder.create()
				.texOffs(14, 16)
				.addBox(0.0F, -2.0F, 0.0F, 8.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
		outerChevron.addOrReplaceChild("outer_chevron_right_top_C", CubeListBuilder.create()
				.texOffs(14, 18)
				.addBox(0.0F, -3.0F, 0.0F, 7.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
		outerChevron.addOrReplaceChild("outer_chevron_right_A", CubeListBuilder.create()
				.texOffs(14, 20)
				.addBox(0.0F, -4.0F, 0.0F, 4.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
		outerChevron.addOrReplaceChild("outer_chevron_right_B", CubeListBuilder.create()
				.texOffs(14, 22)
				.addBox(0.0F, -5.0F, 0.0F, 3.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
		outerChevron.addOrReplaceChild("outer_chevron_right_C", CubeListBuilder.create()
				.texOffs(14, 24)
				.addBox(0.0F, -6.0F, 0.0F, 2.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
	}
	
	public static void createOuterChevronLights(PartDefinition outerChevronLights)
	{
		outerChevronLights.addOrReplaceChild("outer_chevron_left_lower_light", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(0.0F, -5.5F, 0.0F, 2.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		outerChevronLights.addOrReplaceChild("outer_chevron_left_center_light", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(-2.0F, -4.0F, 0.0F, 3.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		outerChevronLights.addOrReplaceChild("outer_chevron_left_upper_light", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(-4.0F, -2.5F, 0.0F, 4.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(10)));
		
		outerChevronLights.addOrReplaceChild("outer_chevron_right_lower_light", CubeListBuilder.create()
				.texOffs(0, 17)
				.addBox(-2.0F, -5.5F, 0.0F, 2.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
		outerChevronLights.addOrReplaceChild("outer_chevron_right_center_light", CubeListBuilder.create()
				.texOffs(0, 15)
				.addBox(-1.0F, -4.0F, 0.0F, 3.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
		outerChevronLights.addOrReplaceChild("outer_chevron_right_upper_light", CubeListBuilder.create()
				.texOffs(0, 13)
				.addBox(0.0F, -2.5F, 0.0F, 4.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(5.0F, 6.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-10)));
	}
}
