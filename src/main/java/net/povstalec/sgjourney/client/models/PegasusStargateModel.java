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
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class PegasusStargateModel extends AbstractStargateModel
{
	private static final String CHEVRON = ClientStargateConfig.pegasus_stargate_back_lights_up.get() ? "pegasus_chevron" : "pegasus_chevron_front";
	private static final ResourceLocation RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_outer_ring.png");
	private static final ResourceLocation SYMBOL_RING_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_inner_ring.png");
	private static final ResourceLocation CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/" + CHEVRON + ".png");
	private static final ResourceLocation ENGAGED_CHEVRON_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/" + CHEVRON + "_lit.png");
	
	private final ModelPart ring;
	private final ModelPart symbolRing;
	private final ModelPart dividers;
	private final ModelPart chevrons;
	
	private static final int symbolCount = 36;
	private int currentSymbol = 0;
	
	public PegasusStargateModel(ModelPart ring, ModelPart symbolRing, ModelPart dividers, ModelPart chevrons)
	{
		this.ring = ring;
		this.symbolRing = symbolRing;
		this.dividers = dividers;
		this.chevrons = chevrons;
	}
	
	public void renderStargate(PegasusStargateEntity stargate, float partialTick, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		this.renderRing(stargate, stack, source, combinedLight, combinedOverlay, false);
		
		this.renderSymbolRing(stargate, stack, source, combinedLight, combinedOverlay);

		this.renderChevrons(stargate, stack, source, combinedLight, combinedOverlay);
	}
	
	protected void renderRing(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
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
	
	protected void renderSymbolRing(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		VertexConsumer ringTexture = source.getBuffer(RenderType.entitySolid(RING_TEXTURE));
		this.dividers.render(stack, ringTexture, combinedLight, combinedOverlay);
		
		VertexConsumer symbolRingTexture = source.getBuffer(RenderType.entitySolid(SYMBOL_RING_TEXTURE));

		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).render(stack, symbolRingTexture, combinedLight, combinedOverlay);
		}
		
		renderSpinningSymbol(stargate, stack, source, combinedLight, combinedOverlay);
		
		if(stargate.addressBuffer.length == 0)
			renderIdleSymbols(stargate, stack, source, combinedLight, combinedOverlay);
		else
			renderLockedSymbols(stargate, stack, source, combinedLight, combinedOverlay);
		
	}
	
	protected void renderSpinningSymbol(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		if(stargate.isConnected())
	    	this.getSymbol(0).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbolTexture(stargate, 0))), 255, combinedOverlay, 0.0F/255.0F, 200.0F/255.0F, 255.0F/255.0F, 1.0F);
		else if(stargate.symbolBuffer < stargate.addressBuffer.length)
	    {
	    	for(int i = 0; i < stargate.getAddress().length; i++)
	    	{
	    		if(stargate.getChevronPosition(i + 1) == this.currentSymbol)
	    			return;
	    	}
	    	this.getSymbol(this.currentSymbol).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbolTexture(stargate, stargate.addressBuffer[stargate.symbolBuffer]))), 255, combinedOverlay, 0.0F/255.0F, 200.0F/255.0F, 255.0F/255.0F, 1.0F);
	    }
	}
	
	protected void renderLockedSymbols(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		for(int i = 0; i < stargate.getAddress().length; i++)
		{
			this.getSymbol(stargate.getChevronPosition(i + 1)).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbolTexture(stargate, stargate.getAddress()[i]))), 255, combinedOverlay, 0.0F/255.0F, 200.0F/255.0F, 255.0F/255.0F, 1.0F);
		}
	}
	
	protected void renderIdleSymbols(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		float r = 0.0F;
		float g = 100.0F;
		float b = 200.0F;
		
		if(stargate.isConnected())
		{
			g = 200.0F;
			b = 255.0F;
		}
		
		for(int i = 0; i < symbolCount; i++)
		{
			this.getSymbol(i).render(stack, source.getBuffer(RenderType.entityNoOutline(getSymbolTexture(stargate, i))), 255, combinedOverlay, r/255.0F, g/255.0F, b/255.0F, 1.0F);
		}
	}
	
	protected ModelPart getSymbol(int symbol)
	{
		return this.symbolRing.getChild("symbol_" + symbol);
	}
	
	public void setCurrentSymbol(int currentSymbol)
	{
		this.currentSymbol = currentSymbol;
	}
	
	protected void renderChevrons(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
			int combinedLight, int combinedOverlay)
	{
		this.renderPegasusPrimaryChevron(stargate, stack, source, combinedLight, combinedOverlay);
		
		for(int i = 1; i <= 8; i++)
		{
			this.renderChevron(stargate, stack, source, combinedLight, combinedOverlay, i);
		}
	}
	
	protected ModelPart getChevron(int chevron)
	{
		return this.chevrons.getChild("chevron_" + chevron);
	}
	
	protected void renderChevron(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
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
	
	protected void renderPegasusPrimaryChevron(PegasusStargateEntity stargate, PoseStack stack, MultiBufferSource source, 
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
}
