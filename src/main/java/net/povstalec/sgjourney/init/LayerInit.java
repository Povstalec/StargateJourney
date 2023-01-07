package net.povstalec.sgjourney.init;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.HorusArmorModel;
import net.povstalec.sgjourney.client.models.JackalArmorModel;
import net.povstalec.sgjourney.client.models.StargateModel;
import net.povstalec.sgjourney.client.models.TransportRingsModel;
import net.povstalec.sgjourney.client.render.SymbolBlockRenderer;

public class LayerInit
{

	// Transport Rings
	public static final ModelLayerLocation SYMBOL_BLOCK_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "symbol_block"), "main");
	
	// Transport Rings
	public static final ModelLayerLocation TRANSPORT_RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "transport_ring"), "main");
	
	// Stargate
	public static final ModelLayerLocation MILKY_WAY_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "milky_way_chevron_layer"), "main");
	public static final ModelLayerLocation PEGASUS_CHEVRON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "pegasus_chevron_layer"), "main");
	public static final ModelLayerLocation RING_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "ring_layer"), "main");
	public static final ModelLayerLocation DIVIDER_LAYER_39 = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "divider_layer_39"), "main");
	public static final ModelLayerLocation DIVIDER_LAYER_36 = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "divider_layer_36"), "main");
	public static final ModelLayerLocation SYMBOL_LAYER_39 = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "symbol_layer_39"), "main");
	public static final ModelLayerLocation SYMBOL_LAYER_36 = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "symbol_layer_36"), "main");
	
	public static final ModelLayerLocation EVENT_HORIZON_LAYER = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "event_horizon_layer"), "main");
	
	// Armor
	public static final ModelLayerLocation HORUS_HEAD = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "horus_head"), "main");
	public static final ModelLayerLocation JACKAL_HEAD = new ModelLayerLocation(new ResourceLocation(StargateJourney.MODID, "jackal_head"), "main");
	
	public static void initLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(SYMBOL_BLOCK_LAYER, () -> SymbolBlockRenderer.createBlockLayer());
		
		event.registerLayerDefinition(TRANSPORT_RING_LAYER, () -> TransportRingsModel.createRingLayer());

		event.registerLayerDefinition(MILKY_WAY_CHEVRON_LAYER, () -> StargateModel.createMilkyWayChevronLayer());
		event.registerLayerDefinition(PEGASUS_CHEVRON_LAYER, () -> StargateModel.createPegasusChevronLayer());
		event.registerLayerDefinition(RING_LAYER, () -> StargateModel.createRingLayer());
		event.registerLayerDefinition(DIVIDER_LAYER_39, () -> StargateModel.createDividerLayer(39));
		event.registerLayerDefinition(DIVIDER_LAYER_36, () -> StargateModel.createDividerLayer(36));
		event.registerLayerDefinition(SYMBOL_LAYER_39, () -> StargateModel.createSymbolLayer(39));
		event.registerLayerDefinition(SYMBOL_LAYER_36, () -> StargateModel.createSymbolLayer(36));
		
		event.registerLayerDefinition(EVENT_HORIZON_LAYER, () -> StargateModel.createEventHorizonLayer());
		
		event.registerLayerDefinition(HORUS_HEAD, HorusArmorModel::createBodyLayer);
		event.registerLayerDefinition(JACKAL_HEAD, JackalArmorModel::createBodyLayer);
	}

}
