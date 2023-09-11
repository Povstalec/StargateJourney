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
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.Connection;

public class MilkyWayStargateModel extends AbstractStargateModel
{
	//private static final String CHEVRON = ClientStargateConfig.milky_way_stargate_back_lights_up.get() ? "milky_way_chevron" : "milky_way_chevron_front";
	private static final ResourceLocation RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_outer_ring.png");
	private static final ResourceLocation SYMBOL_RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_inner_ring.png");
	
	private final ModelPart ring;
	private final ModelPart symbolRing;
	private final ModelPart dividers;
	private final ModelPart chevrons;
	
	private static final int symbolCount = 39;
	private static final double angle = (double) 360 / symbolCount;
	private float rotation = 0.0F;
	
	public MilkyWayStargateModel(ModelPart ring, ModelPart symbolRing, ModelPart dividers, ModelPart chevrons)
	{
		super("milky_way");
		this.ring = ring;
		this.symbolRing = symbolRing;
		this.dividers = dividers;
		this.chevrons = chevrons;
	}
	
	public void renderStargate(MilkyWayStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		this.renderRing(stargate, stack, source, combinedLight, combinedOverlay, false);
		
		this.renderSymbolRing(stargate, stack, source, combinedLight, combinedOverlay);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	protected void renderRing(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, boolean isBottomCovered)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
		ModelPart outerRing = this.ring.getChild("outer_ring");
		ModelPart backRing = this.ring.getChild("back_ring");
		ModelPart innerRing = this.ring.getChild("inner_ring");
		
		int start = 0;
		
		if(isBottomCovered)
			start = 1;
		
		for(int i = start; i < BOXES_PER_RING; i++)
		{
			outerRing.getChild("outer_ring_" + i).render(stack, ringTexture, combinedLight, combinedOverlay);
		}
		for(int i = 0; i < BOXES_PER_RING; i++)
		{
			backRing.getChild("back_ring_" + i).render(stack, ringTexture, combinedLight, combinedOverlay);
		}
		for(int i = start; i < BOXES_PER_RING; i++)
		{
			innerRing.getChild("inner_ring_" + i).render(stack, ringTexture, combinedLight, combinedOverlay);
		}
	}
	
	protected void renderSymbolRing(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer symbolRingTexture = source.getBuffer(RenderType.entitySolid(SYMBOL_RING_TEXTURE));
		
		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).setRotation(0.0F, 0.0F, (float) Math.toRadians(180 - angle * i + this.rotation));
		}
		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
		}
		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).render(stack, source.getBuffer(SGJourneyRenderTypes.stargateRing(getSymbolTexture(stargate, i))), combinedLight, combinedOverlay, 48.0F/255.0F, 49.0F/255.0F, 63.0F/255.0F, 1.0F);
		}

		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
		this.dividers.setRotation(0.0F, 0.0F, (float) Math.toRadians(this.rotation));
		this.dividers.render(stack, ringTexture, combinedLight, combinedOverlay);
	}
	
	protected ModelPart getSymbol(int symbol)
	{
		return this.symbolRing.getChild("symbol_" + symbol);
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	protected void renderChevrons(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		if(ClientStargateConfig.use_movie_stargate_model.get())
		{
			this.doMovieChevron(stargate);
			this.renderMoviePrimaryChevron(stargate, stack, source, combinedLight, combinedOverlay);
		}
		else
			renderMilkyWayPrimaryChevron(stargate, stack, source, combinedLight, combinedOverlay);
		
		for(int i = 1; i <= 8; i++)
		{
			this.renderChevron(stargate, stack, source, combinedLight, combinedOverlay, i);
		}
	}
	
	protected ModelPart getChevron(int chevron)
	{
		return this.chevrons.getChild("chevron_" + chevron);
	}
	
	protected ModelPart getMovieChevron()
	{
		return this.chevrons.getChild("movie_chevron");
	}
	
	protected ModelPart getChevronLight(int chevron)
	{
		return this.getChevron(chevron).getChild("chevron_light");
	}
	
	protected ModelPart getOuterChevron(int chevron)
	{
		return this.getChevron(chevron).getChild("outer_chevron");
	}
	
	protected void renderChevron(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay, int chevronNumber)
	{
		// Make sure this is at the top, otherwise the lit up chevrons are rendered under the not lit chevrons
		int chevron = stargate.getEngagedChevrons()[chevronNumber];
		
		VertexConsumer chevronTexture = source.getBuffer(RenderType.entitySolid(ClientStargateConfig.milky_way_stargate_back_lights_up.get() ? CHEVRON_TEXTURE : CHEVRON_FRONT_TEXTURE));
		this.getChevron(chevron).render(stack, chevronTexture, combinedLight, combinedOverlay);
		
		if(stargate.chevronsRendered() >= chevronNumber)
		{
			VertexConsumer engagedChevronTexture = source.getBuffer(SGJourneyRenderTypes.stargateChevron(ClientStargateConfig.milky_way_stargate_back_lights_up.get() ? ENGAGED_CHEVRON_TEXTURE : ENGAGED_CHEVRON_FRONT_TEXTURE));
			this.getChevron(chevron).render(stack, engagedChevronTexture, 255, combinedOverlay);
		}
	}
	
	protected void controlChevrons(MilkyWayStargateEntity stargate, int chevronsRaised, boolean onlyOuter)
	{
		for(int i = 1; i <= chevronsRaised; i++)
		{
			int chevron = stargate.getEngagedChevrons()[i];
			
			if(!onlyOuter)
				this.getChevronLight(chevron).y = 2;
			else
				this.getChevronLight(chevron).y = 0;
			this.getOuterChevron(chevron).y = -2;
		}
		
		for(int i = chevronsRaised + 1; i < 9; i++)
		{
			int chevron = stargate.getEngagedChevrons()[i];
			
			this.getChevronLight(chevron).y = 0;
			this.getOuterChevron(chevron).y = 0;
		}
	}
	
	protected void controlChevron(MilkyWayStargateEntity stargate, int chevronRaised)
	{
		for(int i = 0; i < 9; i++)
		{
			int chevron = stargate.getEngagedChevrons()[i];
			
			this.getChevronLight(chevron).y = 0;
			this.getOuterChevron(chevron).y = 0;
		}
		
		int chevron = stargate.getEngagedChevrons()[chevronRaised];
		
		this.getChevronLight(chevron).y = 2;
		this.getOuterChevron(chevron).y = -2;
	}
	
	protected void doMovieChevron(MilkyWayStargateEntity stargate)
	{
		//boolean raiseChevron = stargate.isChevronRaised && stargate.getCurrentSymbol() != 0;
		int chevronsRendered = stargate.chevronsRendered();
		
		if(stargate.isConnected())
		{
			System.out.println(chevronsRendered);
			controlChevrons(stargate, chevronsRendered, true);
		}
		else
		{
			if(stargate.isChevronRaised)
			{
				if(stargate.getCurrentSymbol() == 0)
					controlChevrons(stargate, chevronsRendered, false);
				else
				{
					int chevronNumber = chevronsRendered + 1;
					
					if(chevronNumber < 9)
						controlChevron(stargate, chevronNumber);
					else
						controlChevrons(stargate, chevronsRendered, false);
				}
			}
			else
				controlChevrons(stargate, 0, false);
		}
	}
	
	protected void renderMoviePrimaryChevron(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer chevronTexture = source.getBuffer(RenderType.entitySolid(getChevronTexture(ClientStargateConfig.milky_way_stargate_back_lights_up.get(), false)));
		this.getMovieChevron().render(stack, chevronTexture, combinedLight, combinedOverlay);
		
		if(!stargate.isChevronRaised && stargate.isConnected())
		{
			VertexConsumer engagedChevronTexture = source.getBuffer(SGJourneyRenderTypes.stargateChevron(getChevronTexture(ClientStargateConfig.milky_way_stargate_back_lights_up.get(), true)));
			
			if(stargate.isDialingOut() || stargate.getKawooshTickCount() > 0)
				this.getMovieChevron().render(stack, engagedChevronTexture, 255, combinedOverlay);
		}
	}
	
	protected void renderMilkyWayPrimaryChevron(MilkyWayStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		
		if(stargate.isChevronRaised)
		{
			this.getChevronLight(0).y = 2;
			this.getOuterChevron(0).y = -2;
		}
		else
		{
			this.getChevronLight(0).y = 0;
			this.getOuterChevron(0).y = 0;
		}
		
		VertexConsumer chevron_texture = source.getBuffer(RenderType.entitySolid(getChevronTexture(ClientStargateConfig.milky_way_stargate_back_lights_up.get(), false)));
		this.getChevron(0).render(stack, chevron_texture, combinedLight, combinedOverlay);
		
		if(stargate.isConnected() || stargate.isChevronRaised)
		{
			VertexConsumer engaged_chevron_texture = source.getBuffer(SGJourneyRenderTypes.stargateChevron(getChevronTexture(ClientStargateConfig.milky_way_stargate_back_lights_up.get(), true)));

			if(stargate.isDialingOut() || stargate.getKawooshTickCount() > 0)
				this.getChevron(0).render(stack, engaged_chevron_texture, 255, combinedOverlay);
		}
	}
	
	//============================================================================================
	//*******************************************Layers*******************************************
	//============================================================================================
	
	public static LayerDefinition createRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition ring = meshdefinition.getRoot();
        
		createRing(ring);
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static LayerDefinition createSymbolRingLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition symbolRing = meshdefinition.getRoot();
        
		createSymbolRing(symbolRing, symbolCount);
		
		return LayerDefinition.create(meshdefinition, 8, 8);
	}
	
	public static LayerDefinition createDividerLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition dividers = meshdefinition.getRoot();
		
		createDividers(dividers, symbolCount);
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	public static LayerDefinition createChevronLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition chevrons = meshdefinition.getRoot();
		
		createMovieChevron(chevrons.addOrReplaceChild("movie_chevron", CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, 0.0F)));
		
		for(int i = 0; i < 9; i++)
		{
			createChevron(chevrons.addOrReplaceChild("chevron_" + i, CubeListBuilder.create(), PartPose.rotation(0.0F, 0.0F, (float) Math.toRadians(-40 * i))));
		}
		
		return LayerDefinition.create(meshdefinition, 64, 64);
	}
	
	protected static void createMovieChevron(PartDefinition chevron)
	{
		//	_
		//	V
		//	
		PartDefinition chevronLight = chevron.addOrReplaceChild("chevron_light", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		createChevronLight(chevronLight);
		
		//	
		//	\ /
		//	
		PartDefinition outerChevron = chevron.addOrReplaceChild("outer_chevron", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		createOuterMovieChevron(outerChevron);
		
		PartDefinition backChevron = chevron.addOrReplaceChild("back_chevron", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
		createBackChevron(backChevron);
	}
	
	protected static void createOuterMovieChevron(PartDefinition outerChevron)
	{
		outerChevron.addOrReplaceChild("chevron_right_f", CubeListBuilder.create()
				.texOffs(16, 29)
				.addBox(-1.0F, 2.0F, 0.0F, 3.0F, 8.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		outerChevron.addOrReplaceChild("chevron_right_f2", CubeListBuilder.create()
				.texOffs(25, 29)
				.addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		outerChevron.addOrReplaceChild("chevron_right_f3", CubeListBuilder.create()
				.texOffs(31, 29)
				.addBox(1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(-22.5)));
		
		outerChevron.addOrReplaceChild("chevron_left_f", CubeListBuilder.create()
				.texOffs(16, 29)
				.addBox(-2.0F, 2.0F, 0.0F, 3.0F, 8.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
		outerChevron.addOrReplaceChild("chevron_left_f2", CubeListBuilder.create()
				.texOffs(25, 29)
				.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
		outerChevron.addOrReplaceChild("chevron_left_f3", CubeListBuilder.create()
				.texOffs(31, 29)
				.addBox(-2.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F), 
				PartPose.offsetAndRotation(-2.0F, 46.0F, 3.5F, 0.0F, 0.0F, (float) Math.toRadians(22.5)));
	}
}
