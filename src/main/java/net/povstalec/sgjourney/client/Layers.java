package net.povstalec.sgjourney.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.*;
import net.povstalec.sgjourney.client.render.block_entity.SymbolBlockRenderer;

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
	// Milky Way Stargate
	public static final ModelLayerLocation MILKY_WAY_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "milky_way_ring_layer"), "main");
	public static final ModelLayerLocation MILKY_WAY_SYMBOL_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "milky_way_symbol_ring_layer"), "main");
	public static final ModelLayerLocation MILKY_WAY_DIVIDER_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "milky_way_divider_layer"), "main");
	public static final ModelLayerLocation MILKY_WAY_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "milky_way_chevron_layer"), "main");
	// Pegasus Stargate
	public static final ModelLayerLocation PEGASUS_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "pegasus_ring_layer"), "main");
	public static final ModelLayerLocation PEGASUS_SYMBOL_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "pegasus_symbol_ring_layer"), "main");
	public static final ModelLayerLocation PEGASUS_DIVIDER_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "pegasus_divider_layer"), "main");
	public static final ModelLayerLocation PEGASUS_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "pegasus_chevron_layer"), "main");
	// Classic Stargate
	public static final ModelLayerLocation CLASSIC_OUTER_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "classic_outer_ring_layer"), "main");
	public static final ModelLayerLocation CLASSIC_INNER_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "classic_inner_ring_layer"), "main");
	public static final ModelLayerLocation CLASSIC_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "classic_chevron_layer"), "main");
	// Tollan Stargate
	public static final ModelLayerLocation TOLLAN_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "tollan_ring_layer"), "main");
	public static final ModelLayerLocation TOLLAN_SYMBOL_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "tollan_symbol_ring_layer"), "main");
	public static final ModelLayerLocation TOLLAN_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "tollan_chevron_layer"), "main");

	// Wormhole
	//public static final ModelLayerLocation EVENT_HORIZON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "event_horizon_layer"), "main");
	//public static final ModelLayerLocation KAWOOSH_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "kawoosh_layer"), "main");
	
	// Armor
	public static final ModelLayerLocation FALCON_HEAD = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "falcon_head"), "main");
	public static final ModelLayerLocation JACKAL_HEAD = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "jackal_head"), "main");
	
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		// Symbol Block
		event.registerLayerDefinition(SYMBOL_BLOCK_LAYER, () -> SymbolBlockRenderer.createBlockLayer());
		
		// Transport Rings
		event.registerLayerDefinition(TRANSPORT_RING_LAYER, () -> TransportRingsModel.createRingLayer());
		
		// Universe Stargate
		event.registerLayerDefinition(UNIVERSE_RING_LAYER, () -> UniverseStargateModel.createRingLayer());
		event.registerLayerDefinition(UNIVERSE_SYMBOL_RING_LAYER, () -> UniverseStargateModel.createSymbolRingLayer());
		event.registerLayerDefinition(UNIVERSE_DIVIDER_LAYER, () -> UniverseStargateModel.createDividerLayer());
		event.registerLayerDefinition(UNIVERSE_CHEVRON_LAYER, () -> UniverseStargateModel.createChevronLayer());
		// Milky Way Stargate
		event.registerLayerDefinition(MILKY_WAY_RING_LAYER, () -> MilkyWayStargateModel.createRingLayer());
		event.registerLayerDefinition(MILKY_WAY_SYMBOL_RING_LAYER, () -> MilkyWayStargateModel.createSymbolRingLayer());
		event.registerLayerDefinition(MILKY_WAY_DIVIDER_LAYER, () -> MilkyWayStargateModel.createDividerLayer());
		event.registerLayerDefinition(MILKY_WAY_CHEVRON_LAYER, () -> MilkyWayStargateModel.createChevronLayer());
		// Pegasus Stargate
		event.registerLayerDefinition(PEGASUS_RING_LAYER, () -> PegasusStargateModel.createRingLayer());
		event.registerLayerDefinition(PEGASUS_SYMBOL_RING_LAYER, () -> PegasusStargateModel.createSymbolRingLayer());
		event.registerLayerDefinition(PEGASUS_DIVIDER_LAYER, () -> PegasusStargateModel.createDividerLayer());
		event.registerLayerDefinition(PEGASUS_CHEVRON_LAYER, () -> PegasusStargateModel.createChevronLayer());
		// Classic Stargate
		event.registerLayerDefinition(CLASSIC_OUTER_RING_LAYER, () -> ClassicStargateModel.createOuterRingLayer());
		event.registerLayerDefinition(CLASSIC_INNER_RING_LAYER, () -> ClassicStargateModel.createInnerRingLayer());
		event.registerLayerDefinition(CLASSIC_CHEVRON_LAYER, () -> ClassicStargateModel.createChevronLayer());
		// Tollan Stargate
		event.registerLayerDefinition(TOLLAN_RING_LAYER, () -> TollanStargateModel.createRingLayer());
		event.registerLayerDefinition(TOLLAN_SYMBOL_RING_LAYER, () -> TollanStargateModel.createSymbolRingLayer());
		event.registerLayerDefinition(TOLLAN_CHEVRON_LAYER, () -> TollanStargateModel.createChevronLayer());
		// Wormhole
		//event.registerLayerDefinition(EVENT_HORIZON_LAYER, () -> WormholeModel.createEventHorizonLayer());
		//event.registerLayerDefinition(KAWOOSH_LAYER, () -> WormholeModel.createKawooshLayer());
		//event.registerLayerDefinition(VORTEX_LAYER, () -> WormholeModel.createVortexLayer());
		
		// Armor
		event.registerLayerDefinition(FALCON_HEAD, FalconArmorModel::createBodyLayer);
		event.registerLayerDefinition(JACKAL_HEAD, JackalArmorModel::createBodyLayer);
	}

}
