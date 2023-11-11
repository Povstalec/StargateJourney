package net.povstalec.sgjourney.client.models;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

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

public class TollanStargateModel extends AbstractStargateModel<TollanStargateEntity>
{
	private static final String CHEVRON = ClientStargateConfig.tollan_stargate_back_lights_up.get() ? "tollan_chevron" : "tollan_chevron_front";
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/" + CHEVRON + ".png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/" + CHEVRON + "_lit.png");

	protected static final float TOLLAN_DISTANCE = 3F;//TODO Added this
	protected static final float TOLLAN_RING_HEIGHT = 8F / 16;//TODO Added this
	protected static final float STARGATE_RING_THICKNESS = 3F;//TODO Added this
	protected static final float STARGATE_RING_OFFSET = STARGATE_RING_THICKNESS / 2 / 16;
	
	protected static final float STARGATE_RING_OUTER_HEIGHT = TOLLAN_DISTANCE - STARGATE_RING_SHRINK;
	protected static final float STARGATE_RING_OUTER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_OUTER_HEIGHT, DEFAULT_DISTANCE);
	protected static final float STARGATE_RING_OUTER_CENTER = STARGATE_RING_OUTER_LENGTH / 2;

	protected static final float STARGATE_RING_INNER_HEIGHT = TOLLAN_DISTANCE - (TOLLAN_RING_HEIGHT - STARGATE_RING_SHRINK);
	protected static final float STARGATE_RING_INNER_LENGTH = SGJourneyModel.getUsedWidth(DEFAULT_SIDES, STARGATE_RING_INNER_HEIGHT, DEFAULT_DISTANCE);
	protected static final float STARGATE_RING_INNER_CENTER = STARGATE_RING_INNER_LENGTH / 2;
	
	protected static final float STARGATE_RING_HEIGHT = STARGATE_RING_OUTER_HEIGHT - STARGATE_RING_INNER_HEIGHT;
	
	protected static final float CHEVRON_THICKNESS = STARGATE_RING_THICKNESS + 2F / 16;
	protected static final float CHEVRON_WIDTH = 5F / 16;
	protected static final float CHEVRON_HEIGHT = 9F / 16;
	
	
	
	protected static final float DEFAULT_DISTANCE_FROM_CENTER = 3.0F * 16.0F; // 3 blocks away from the center of the Stargate
	public static final float DEFAULT_Z = 3.0F;
	protected static final int BOXES_PER_RING = 36;

	protected static final float OUTER_CHEVRON_ANGLE = -2.5F;
	protected static final float OUTER_CHEVRON_BOTTOM_X = 4.5F;
	protected static final float OUTER_CHEVRON_CENTER_X = 3.0F;
	protected static final float OUTER_CHEVRON_CENTER_Y = 1.0F;
	protected static final float OUTER_CHEVRON_CENTER_Z = 4.75F;

	protected static final float OUTER_CHEVRON_SIDE_X = 1.0F;
	protected static final float OUTER_CHEVRON_SIDE_Y = 9.0F;
	protected static final float OUTER_CHEVRON_SIDE_Z = OUTER_CHEVRON_CENTER_Z; //

	public static final float OUTER_CHEVRON_X_OFFSET = 1.25F;
	protected static final float OUTER_CHEVRON_Y_OFFSET = 0.375F;
	protected static final float OUTER_CHEVRON_Y_OFFSET_ANGLED = 8.0F + OUTER_CHEVRON_CENTER_Y; // The + 2.0F is there because there's a 2 pixel gap between the Chevron Light and Outer Chevron
	protected static final float OUTER_CHEVRON_Z_OFFSET = -OUTER_CHEVRON_SIDE_Z / 2;

	protected static final float CHEVRON_LIGHT_Z = (OUTER_CHEVRON_SIDE_Z / 2) - (1.0F / 16);
	protected static final float CHEVRON_LIGHT_Y_OFFSET = 3.125F;
	protected static final float CHEVRON_LIGHT_Z_OFFSET = 0.0F;

	protected static final float CHEVRON_LIGHT_CENTER_X = 3.375F;
	protected static final float CHEVRON_LIGHT_CENTER_Y = 3.375F;
	
	private final ModelPart chevrons;

	public TollanStargateModel(ModelPart chevrons)
	{
		super("tollan");
		this.chevrons = chevrons;
	}
	
	@Override
	public void renderStargate(TollanStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source,
							   int combinedLight, int combinedOverlay)
	{
		VertexConsumer consumer = source.getBuffer(SGJourneyRenderTypes.stargate(getStargateTexture()));
		renderRing(stack, consumer, source, combinedLight);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}

	/*protected void renderChevrons(TollanStargateEntity stargate, PoseStack stack, MultiBufferSource source,
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
			VertexConsumer engagedChevronTexture = source.getBuffer(SGJourneyRenderTypes.engagedChevron(ENGAGED_CHEVRON_TEXTURE));
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
			VertexConsumer engaged_chevron_texture = source.getBuffer(SGJourneyRenderTypes.engagedChevron(ENGAGED_CHEVRON_TEXTURE));
		    this.getChevron(0).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
	}*/
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================


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
						.addBox(
								-CHEVRON_LIGHT_CENTER_X / 2,
								-CHEVRON_LIGHT_Y_OFFSET,
								CHEVRON_LIGHT_Z_OFFSET,
								CHEVRON_LIGHT_CENTER_X,
								CHEVRON_LIGHT_CENTER_Y,
								CHEVRON_LIGHT_Z
						),
				PartPose.offset(
						0.0F,
						DEFAULT_DISTANCE_FROM_CENTER,
						0.0F
				)
		);
	}

	public static void createOuterChevron(PartDefinition outerChevron)
	{
		outerChevron.addOrReplaceChild("chevron_f", CubeListBuilder.create()
						.texOffs(0, 29)
						.addBox(
								-OUTER_CHEVRON_BOTTOM_X / 2,
								OUTER_CHEVRON_Y_OFFSET - 1.0F,
								OUTER_CHEVRON_Z_OFFSET,
								OUTER_CHEVRON_BOTTOM_X,
								OUTER_CHEVRON_CENTER_Y,
								OUTER_CHEVRON_SIDE_Z
						),
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET_ANGLED, 0.0F));
		outerChevron.addOrReplaceChild("chevron_b", CubeListBuilder.create()
						.texOffs(0, 29)
						.addBox(
								-OUTER_CHEVRON_CENTER_X / 2,
								OUTER_CHEVRON_Y_OFFSET + 4.5F,
								OUTER_CHEVRON_Z_OFFSET,
								OUTER_CHEVRON_CENTER_X,
								OUTER_CHEVRON_CENTER_Y,
								OUTER_CHEVRON_SIDE_Z
						),
				PartPose.offset(0.0F, DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET_ANGLED, 0.0F));
		outerChevron.addOrReplaceChild("chevron_right_f", CubeListBuilder.create()
						.texOffs(10, 29)
						.addBox(
								-(OUTER_CHEVRON_X_OFFSET + OUTER_CHEVRON_SIDE_X),
								OUTER_CHEVRON_Y_OFFSET,
								OUTER_CHEVRON_Z_OFFSET,
								OUTER_CHEVRON_SIDE_X,
								OUTER_CHEVRON_SIDE_Y,
								OUTER_CHEVRON_SIDE_Z
						),
				PartPose.offsetAndRotation(
						0.0F,
						DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET_ANGLED,
						0.0F,
						0.0F,
						0.0F,
						(float) Math.toRadians(-OUTER_CHEVRON_ANGLE)
				)
		);
		outerChevron.addOrReplaceChild("chevron_left_f", CubeListBuilder.create()
						.texOffs(10, 29)
						.addBox(
								OUTER_CHEVRON_X_OFFSET,
								OUTER_CHEVRON_Y_OFFSET,
								OUTER_CHEVRON_Z_OFFSET,
								OUTER_CHEVRON_SIDE_X,
								OUTER_CHEVRON_SIDE_Y,
								OUTER_CHEVRON_SIDE_Z
						),
				PartPose.offsetAndRotation(
						0.0F,
						DEFAULT_DISTANCE_FROM_CENTER - OUTER_CHEVRON_Y_OFFSET_ANGLED,
						0.0F,
						0.0F,
						0.0F,
						(float) Math.toRadians(OUTER_CHEVRON_ANGLE)
				)
		);
	}

	public static void createBackChevron(PartDefinition backChevron)
	{
		backChevron.addOrReplaceChild("chevron_b_center", CubeListBuilder.create()
						.texOffs(0, 6)
						.addBox(
								-CHEVRON_LIGHT_CENTER_X / 2,
								-CHEVRON_LIGHT_Y_OFFSET,
								CHEVRON_LIGHT_Z_OFFSET,
								CHEVRON_LIGHT_CENTER_X,
								CHEVRON_LIGHT_CENTER_Y,
								CHEVRON_LIGHT_Z
						),
				PartPose.offset(
						0.0F,
						DEFAULT_DISTANCE_FROM_CENTER,
						-CHEVRON_LIGHT_Z
				)
		);
	}
	
	//============================================================================================
	//******************************************Chevrons******************************************
	//============================================================================================
	
	@Override
	protected void renderPrimaryChevron(TollanStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, boolean chevronEngaged)
	{
		/*int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.translate(0, 3.5F - 2.5F/16, 0);
		
		renderChevronLight(stack, consumer, source, light, isPrimaryChevronRaised(stargate));
		renderOuterChevronFront(stack, consumer, source, light, isOuterPrimaryChevronLowered(stargate));
		renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();*/
	}

	protected void renderChevron(TollanStargateEntity stargate, PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight, int chevronNumber, boolean chevronEngaged)
	{
		/*int chevron = stargate.getEngagedChevrons()[chevronNumber];
		int light = chevronEngaged ? MAX_LIGHT : combinedLight;
		
		stack.pushPose();
		stack.mulPose(Axis.ZP.rotationDegrees(-CHEVRON_ANGLE * chevron));
		stack.translate(0, 3.5F - 2.5F/16, 0);
		
		renderChevronLight(stack, consumer, source, light, isChevronLightRaised(stargate, chevronNumber));
		renderOuterChevronFront(stack, consumer, source, light, isOuterChevronLowered(stargate, chevronNumber));
		renderOuterChevronBack(stack, consumer, source, light);
		
		stack.popPose();*/
	}
	
	//============================================================================================
	//********************************************Ring********************************************
	//============================================================================================
	
	protected void renderRing(PoseStack stack, VertexConsumer consumer, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		for(int j = 0; j < DEFAULT_SIDES; j++)
		{
			stack.mulPose(Axis.ZP.rotationDegrees(10));
			//Front
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_HEIGHT,
					STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_HEIGHT,
					STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Back
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 0, 1,
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_INNER_CENTER * 16) / 64, (7 + STARGATE_RING_HEIGHT/2 * 16) / 64,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, (7 - STARGATE_RING_HEIGHT/2 * 16) / 64);
			
			//Outside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, 1, 0,
					-STARGATE_RING_OUTER_CENTER, 
					STARGATE_RING_OUTER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, 0,
					
					-STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_HEIGHT,
					STARGATE_RING_OFFSET,
					(4.5F - STARGATE_RING_OUTER_CENTER * 16) / 64, 3F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_HEIGHT,
					STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, 3F / 64,
					
					STARGATE_RING_OUTER_CENTER,
					STARGATE_RING_OUTER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(4.5F + STARGATE_RING_OUTER_CENTER * 16) / 64, 0);
			
			//Inside
			SGJourneyModel.createQuad(consumer, matrix4, matrix3, combinedLight, 0, -1, 0,
					STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_INNER_CENTER * 16) / 64, 0,
					
					STARGATE_RING_INNER_CENTER, 
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(12.5F - STARGATE_RING_INNER_CENTER * 16) / 64, 3F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_INNER_CENTER * 16) / 64, 3F / 64,
					
					-STARGATE_RING_INNER_CENTER,
					STARGATE_RING_INNER_HEIGHT,
					-STARGATE_RING_OFFSET,
					(12.5F + STARGATE_RING_INNER_CENTER * 16) / 64, 0);
		}
	}

}
