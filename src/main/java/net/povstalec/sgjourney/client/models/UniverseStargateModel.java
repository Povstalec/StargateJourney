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
import net.povstalec.sgjourney.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;

public class UniverseStargateModel extends AbstractStargateModel
{
	private static final ResourceLocation RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_outer_ring.png");
	private static final ResourceLocation SYMBOL_RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_inner_ring.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_chevron.png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_chevron_lit.png");
	
	private final ModelPart ring;
	private final ModelPart symbolRing;
	private final ModelPart dividers;
	private final ModelPart chevrons;
	
	private static final int symbolCount = 36;
	
	public UniverseStargateModel(ModelPart ring, ModelPart symbolRing, ModelPart dividers, ModelPart chevrons)
	{
		this.ring = ring;
		this.symbolRing = symbolRing;
		this.dividers = dividers;
		this.chevrons = chevrons;
	}
	
	public void renderStargate(UniverseStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
		
		this.ring.render(stack, ringTexture, combinedLight, combinedOverlay);
		
		this.chevrons.render(stack, ringTexture, combinedLight, combinedOverlay);

		this.renderSymbolRing(stargate, stack, source, combinedLight, combinedOverlay, combinedOverlay);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay, combinedOverlay);
	}
	
	protected void renderSymbolRing(UniverseStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, int chevronsActive)
	{
		VertexConsumer symbolRingTexture = source.getBuffer(RenderType.entitySolid(SYMBOL_RING_TEXTURE));
		
		for(int i = 0; i < 9; i++)
		{
			this.getUnderChevronLeft(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
			this.getUnderChevronRight(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
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
		this.getChevron(chevronNumber).render(stack, chevronTexture, combinedLight, combinedOverlay);
		
		VertexConsumer engagedChevronTexture = source.getBuffer(SGJourneyRenderTypes.stargateChevron(ENGAGED_CHEVRON_TEXTURE));
		this.getChevron(chevronNumber).render(stack, engagedChevronTexture, 255, combinedOverlay);
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
		for(int i = 0; i < 36; i++)
		{
			outerRing.addOrReplaceChild("outer_ring_" + i, CubeListBuilder.create()
					.texOffs(0, 0)
					.addBox(-5.0F, -56.0F, -3.5F, 10.0F, 4.0F, 7.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-10 * i)));
		}
	}
	
	public static void createBackRing(PartDefinition backRing)
	{
		double angle = (float) 360 / 54;
		
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
		for(int i = 0; i < 36; i++)
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
		double angle = (float) 360 / 54;
		
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
		double angle = (double) 360 / 54;
		for(int i = 0; i < 54; i++)
		{
			dividers.addOrReplaceChild("divider_" + i, CubeListBuilder.create()
					.texOffs(34, 34)
					.addBox(-0.5F, -53.0F, -3.0F, 1.0F, 9.0F, 6.0F), 
					PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(180 - angle * i)));
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
		PartDefinition chevronLight = chevron.addOrReplaceChild("chevron_light", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.5F));
		createChevronLight(chevronLight);
		PartDefinition chevronBackLight = chevron.addOrReplaceChild("chevron_light_back", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -5.5F));
		createChevronLight(chevronBackLight);
		
		// -_   _-
		//	 - -
		//	
		/*PartDefinition outerChevron = chevron.addOrReplaceChild("outer_chevron", CubeListBuilder.create(), PartPose.ZERO);
		createOuterChevron(outerChevron);
		
		PartDefinition backChevron = chevron.addOrReplaceChild("back_chevron", CubeListBuilder.create(), PartPose.ZERO);
		createBackChevron(backChevron);*/
	}
	
	public static void createChevronLight(PartDefinition chevronLight)
	{
		chevronLight.addOrReplaceChild("chevron_top", CubeListBuilder.create()
				.texOffs(22, 0)
				.addBox(-3.0F, 54.0F, 0.0F, 6.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_upper", CubeListBuilder.create()
				.texOffs(22, 5)
				.addBox(-2.5F, 53.0F, 0.0F, 5.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_center", CubeListBuilder.create()
				.texOffs(22, 5)
				.addBox(-1.5F, 52.0F, 0.0F, 3.0F, 1.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_bottom", CubeListBuilder.create()
				.texOffs(22, 5)
				.addBox(-1.0F, 50.0F, 0.0F, 2.0F, 2.0F, 1.0F), 
				PartPose.offset(0.0F, 0.0F, 0.0F));
		chevronLight.addOrReplaceChild("chevron_right", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(3.0F, 55.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-40)));
		chevronLight.addOrReplaceChild("chevron_left", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(-3.0F, 55.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(40)));
		chevronLight.addOrReplaceChild("chevron_lower_right", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(1.0F, 50.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(-25)));
		chevronLight.addOrReplaceChild("chevron_lower_left", CubeListBuilder.create()
				.texOffs(22, 15)
				.addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F),
				PartPose.offsetAndRotation(-1.0F, 50.0F, 0.0F, 0.0F, 0.0F, (float) Math.toRadians(25)));
	}
}
