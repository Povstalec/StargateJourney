package net.povstalec.sgjourney.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.FalconArmorModel;
import net.povstalec.sgjourney.client.models.JackalArmorModel;
import net.povstalec.sgjourney.client.models.TollanStargateModel;
import net.povstalec.sgjourney.client.models.TransportRingsModel;
import net.povstalec.sgjourney.client.models.UniverseStargateModel;

public class Layers
{
	
	// Symbol Block
	public static final ModelLayerLocation SYMBOL_BLOCK_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "symbol_block"), "main");
	
	// Transport Rings
	public static final ModelLayerLocation TRANSPORT_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "transport_ring"), "main");
	
	// Universe Stargate
	public static final ModelLayerLocation UNIVERSE_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "universe_ring_layer"), "main");
	public static final ModelLayerLocation UNIVERSE_SYMBOL_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "universe_symbol_ring_layer"), "main");
	public static final ModelLayerLocation UNIVERSE_DIVIDER_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "universe_divider_layer"), "main");
	public static final ModelLayerLocation UNIVERSE_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "universe_chevron_layer"), "main");
	// Tollan Stargate
	public static final ModelLayerLocation TOLLAN_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "tollan_ring_layer"), "main");
	public static final ModelLayerLocation TOLLAN_SYMBOL_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "tollan_symbol_ring_layer"), "main");
	public static final ModelLayerLocation TOLLAN_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "tollan_chevron_layer"), "main");
	
	// Armor
	public static final ModelLayerLocation FALCON_HEAD = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "falcon_head"), "main");
	public static final ModelLayerLocation JACKAL_HEAD = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "jackal_head"), "main");
	
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		// Transport Rings
		event.registerLayerDefinition(TRANSPORT_RING_LAYER, () -> TransportRingsModel.createRingLayer());
		
		// Universe Stargate
		event.registerLayerDefinition(UNIVERSE_RING_LAYER, () -> UniverseStargateModel.createRingLayer());
		event.registerLayerDefinition(UNIVERSE_SYMBOL_RING_LAYER, () -> UniverseStargateModel.createSymbolRingLayer());
		event.registerLayerDefinition(UNIVERSE_DIVIDER_LAYER, () -> UniverseStargateModel.createDividerLayer());
		event.registerLayerDefinition(UNIVERSE_CHEVRON_LAYER, () -> UniverseStargateModel.createChevronLayer());
		// Tollan Stargate
		event.registerLayerDefinition(TOLLAN_RING_LAYER, () -> TollanStargateModel.createRingLayer());
		event.registerLayerDefinition(TOLLAN_SYMBOL_RING_LAYER, () -> TollanStargateModel.createSymbolRingLayer());
		event.registerLayerDefinition(TOLLAN_CHEVRON_LAYER, () -> TollanStargateModel.createChevronLayer());
		
		// Armor
		event.registerLayerDefinition(FALCON_HEAD, FalconArmorModel::createBodyLayer);
		event.registerLayerDefinition(JACKAL_HEAD, JackalArmorModel::createBodyLayer);
	}

}
