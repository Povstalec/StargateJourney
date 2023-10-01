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
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;

public class ClassicStargateModel extends AbstractStargateModel
{
	private static final ResourceLocation SIDE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_stargate_side.png");
	private static final ResourceLocation TOP_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_stargate_top.png");
	private static final ResourceLocation STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_stargate.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/classic/classic_chevron.png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_chevron_lit.png");

	protected static final float DEFAULT_DISTANCE = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	protected static final int SPINNING_SIDES = 36;
	protected static final float STARGATE_RING_THICKNESS = 8;
	protected static final float SPINNY_RING_THICKNESS = 7;
	protected static final float STARGATE_RING_OFFSET = STARGATE_RING_THICKNESS / 2 / 16;
	protected static final float SPINNY_RING_OFFSET = SPINNY_RING_THICKNESS / 2 / 16;
	
	private final ModelPart chevrons;
	
	private float rotation;
	
	protected float[][] stargateRingOuter = SGJourneyModel.coordinates(DEFAULT_SIDES, 3.5F, DEFAULT_DISTANCE, 5, 0.0F);
	protected float[][] stargateRingInner = SGJourneyModel.coordinates(DEFAULT_SIDES, 3F, DEFAULT_DISTANCE, 5, 0.0F);

	protected float[][] spinnyRingOuter = SGJourneyModel.coordinates(SPINNING_SIDES, 3.05F, DEFAULT_DISTANCE, 5, 0.0F);
	protected float[][] spinnyRingInner  = SGJourneyModel.coordinates(SPINNING_SIDES, 2.5F, DEFAULT_DISTANCE, 5, 0.0F);
	
	public ClassicStargateModel(ModelPart chevrons)
	{
		super("classic");
		this.chevrons = chevrons;
	}
	
	public void renderStargate(ClassicStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		renderOuterRing(stack, source, combinedLight);
		renderSpinnyRing(stargate, stack, source, combinedLight);
		
		this.renderChevrons(stack, source, combinedLight, combinedOverlay, stargate.chevronsRendered());
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	protected void renderOuterRing(PoseStack stack, MultiBufferSource source, int combinedLight)
	{
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		VertexConsumer sideConsumer = source.getBuffer(SGJourneyRenderTypes.stargate(SIDE_TEXTURE));
		
		for(int j = 0; j < DEFAULT_SIDES; j++)
		{
			//Front
			SGJourneyModel.createQuad(sideConsumer, matrix4, matrix3, combinedLight, 
					stargateRingOuter[(j + 1) % stargateRingOuter.length][0],
					stargateRingOuter[(j + 1) % stargateRingOuter.length][1],
					STARGATE_RING_OFFSET,
					1, 0,
					
					stargateRingOuter[j % stargateRingOuter.length][0],
					stargateRingOuter[j % stargateRingOuter.length][1],
					STARGATE_RING_OFFSET,
					0, 0,
					
					stargateRingInner[j % stargateRingInner.length][0], 
					stargateRingInner[j % stargateRingInner.length][1],
					STARGATE_RING_OFFSET,
					0, 1,
					
					stargateRingInner[(j + 1) % stargateRingInner.length][0],
					stargateRingInner[(j + 1) % stargateRingInner.length][1],
					STARGATE_RING_OFFSET,
					1, 1);
			
			//Back
			SGJourneyModel.createQuad(sideConsumer, matrix4, matrix3, combinedLight, 
					stargateRingInner[j % stargateRingInner.length][0], 
					stargateRingInner[j % stargateRingInner.length][1],
					-STARGATE_RING_OFFSET,
					1, 1,
					
					stargateRingOuter[j % stargateRingOuter.length][0],
					stargateRingOuter[j % stargateRingOuter.length][1],
					-STARGATE_RING_OFFSET,
					1, 0,
					
					stargateRingOuter[(j + 1) % stargateRingOuter.length][0],
					stargateRingOuter[(j + 1) % stargateRingOuter.length][1],
					-STARGATE_RING_OFFSET,
					0, 0,
					
					stargateRingInner[(j + 1) % stargateRingInner.length][0],
					stargateRingInner[(j + 1) % stargateRingInner.length][1],
					-STARGATE_RING_OFFSET,
					0, 1);
		}
		
		VertexConsumer topConsumer = source.getBuffer(SGJourneyRenderTypes.stargate(TOP_TEXTURE));
		
		for(int j = 0; j < DEFAULT_SIDES; j++)
		{
			//Outside
			SGJourneyModel.createQuad(topConsumer, matrix4, matrix3, combinedLight, 
					stargateRingOuter[j % stargateRingOuter.length][0], 
					stargateRingOuter[j % stargateRingOuter.length][1],
					-STARGATE_RING_OFFSET,
					0, 0,
					
					stargateRingOuter[j % stargateRingOuter.length][0],
					stargateRingOuter[j % stargateRingOuter.length][1],
					STARGATE_RING_OFFSET,
					0, 1,
					
					stargateRingOuter[(j + 1) % stargateRingOuter.length][0],
					stargateRingOuter[(j + 1) % stargateRingOuter.length][1],
					STARGATE_RING_OFFSET,
					1, 1,
					
					stargateRingOuter[(j + 1) % stargateRingOuter.length][0],
					stargateRingOuter[(j + 1) % stargateRingOuter.length][1],
					-STARGATE_RING_OFFSET,
					1, 0);
			
			//Inside
			SGJourneyModel.createQuad(topConsumer, matrix4, matrix3, combinedLight, 
					stargateRingInner[(j + 1) % stargateRingInner.length][0],
					stargateRingInner[(j + 1) % stargateRingInner.length][1],
					STARGATE_RING_OFFSET,
					1, 1,
					
					stargateRingInner[j % stargateRingInner.length][0],
					stargateRingInner[j % stargateRingInner.length][1],
					STARGATE_RING_OFFSET,
					0, 1,
					
					stargateRingInner[j % stargateRingInner.length][0], 
					stargateRingInner[j % stargateRingInner.length][1],
					-STARGATE_RING_OFFSET,
					0, 0,
					
					stargateRingInner[(j + 1) % stargateRingInner.length][0],
					stargateRingInner[(j + 1) % stargateRingInner.length][1],
					-STARGATE_RING_OFFSET,
					1, 0);
		}
	}
	
	protected void renderSpinnyRing(ClassicStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight)
	{
		stack.pushPose();
        //stack.mulPose(Axis.ZP.rotationDegrees(180));
		
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		VertexConsumer sideConsumer = source.getBuffer(SGJourneyRenderTypes.stargate(TOP_TEXTURE));
		
		for(int j = 0; j < SPINNING_SIDES; j++)
		{
			//Front
			SGJourneyModel.createQuad(sideConsumer, matrix4, matrix3, combinedLight, 
					spinnyRingOuter[(j + 1) % spinnyRingOuter.length][0],
					spinnyRingOuter[(j + 1) % spinnyRingOuter.length][1],
					SPINNY_RING_OFFSET,
					1, 0,
					
					spinnyRingOuter[j % spinnyRingOuter.length][0],
					spinnyRingOuter[j % spinnyRingOuter.length][1],
					SPINNY_RING_OFFSET,
					0, 0,
					
					spinnyRingInner[j % spinnyRingInner.length][0], 
					spinnyRingInner[j % spinnyRingInner.length][1],
					SPINNY_RING_OFFSET,
					0, 1,
					
					spinnyRingInner[(j + 1) % spinnyRingInner.length][0],
					spinnyRingInner[(j + 1) % spinnyRingInner.length][1],
					SPINNY_RING_OFFSET,
					1, 1);
			
			//Back
			SGJourneyModel.createQuad(sideConsumer, matrix4, matrix3, combinedLight, 
					spinnyRingInner[j % spinnyRingInner.length][0], 
					spinnyRingInner[j % spinnyRingInner.length][1],
					-SPINNY_RING_OFFSET,
					1, 1,
					
					spinnyRingOuter[j % spinnyRingOuter.length][0],
					spinnyRingOuter[j % spinnyRingOuter.length][1],
					-SPINNY_RING_OFFSET,
					1, 0,
					
					spinnyRingOuter[(j + 1) % spinnyRingOuter.length][0],
					spinnyRingOuter[(j + 1) % spinnyRingOuter.length][1],
					-SPINNY_RING_OFFSET,
					0, 0,
					
					spinnyRingInner[(j + 1) % spinnyRingInner.length][0],
					spinnyRingInner[(j + 1) % spinnyRingInner.length][1],
					-SPINNY_RING_OFFSET,
					0, 1);
		}
		
		VertexConsumer topConsumer = source.getBuffer(SGJourneyRenderTypes.stargate(TOP_TEXTURE));
		
		for(int j = 0; j < SPINNING_SIDES; j++)
		{
			//Outside
			SGJourneyModel.createQuad(topConsumer, matrix4, matrix3, combinedLight, 
					spinnyRingInner[(j + 1) % spinnyRingInner.length][0],
					spinnyRingInner[(j + 1) % spinnyRingInner.length][1],
					SPINNY_RING_OFFSET,
					1, 1,
					
					spinnyRingInner[j % spinnyRingInner.length][0],
					spinnyRingInner[j % spinnyRingInner.length][1],
					SPINNY_RING_OFFSET,
					0, 1,
					
					spinnyRingInner[j % spinnyRingInner.length][0], 
					spinnyRingInner[j % spinnyRingInner.length][1],
					-SPINNY_RING_OFFSET,
					0, 0,
					
					spinnyRingInner[(j + 1) % spinnyRingInner.length][0],
					spinnyRingInner[(j + 1) % spinnyRingInner.length][1],
					-SPINNY_RING_OFFSET,
					1, 0);
		}
		
		for(int j = 0; j < SPINNING_SIDES; j++)
		{
			VertexConsumer symbolConsumer = source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbolTexture(stargate, j)));
			//Front Symbols
			SGJourneyModel.createQuad(symbolConsumer, matrix4, matrix3, combinedLight, 
					0.0F/255.0F, 109.0F/255.0F, 121.0F/255.0F, 1.0F, 
					
					spinnyRingOuter[(j + 1) % spinnyRingOuter.length][0],
					spinnyRingOuter[(j + 1) % spinnyRingOuter.length][1],
					SPINNY_RING_OFFSET,
					1, 0,
					
					spinnyRingOuter[j % spinnyRingOuter.length][0],
					spinnyRingOuter[j % spinnyRingOuter.length][1],
					SPINNY_RING_OFFSET,
					0, 0,
					
					spinnyRingInner[j % spinnyRingInner.length][0], 
					spinnyRingInner[j % spinnyRingInner.length][1],
					SPINNY_RING_OFFSET,
					0, 1,
					
					spinnyRingInner[(j + 1) % spinnyRingInner.length][0],
					spinnyRingInner[(j + 1) % spinnyRingInner.length][1],
					SPINNY_RING_OFFSET,
					1, 1);
		}

		stack.popPose();
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
	
	public static void createChevron(PartDefinition chevron)
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
