package net.povstalec.sgjourney.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.entity.*;
import net.povstalec.sgjourney.client.models.block_entity.TransportRingModel;

public class Layers
{
	// Transport Rings
	public static final ModelLayerLocation TRANSPORT_RING_LAYER = new ModelLayerLocation(StargateJourney.sgjourneyLocation("transport_ring"), "main");
	
	// Armor
	public static final ModelLayerLocation FALCON_HEAD = new ModelLayerLocation(StargateJourney.sgjourneyLocation("falcon_head"), "main");
	public static final ModelLayerLocation JACKAL_HEAD = new ModelLayerLocation(StargateJourney.sgjourneyLocation("jackal_head"), "main");
	
	public static final ModelLayerLocation ABYDOS_LIZARD = new ModelLayerLocation(StargateJourney.sgjourneyLocation("abydos_lizard"), "main");
	public static final ModelLayerLocation MASTADGE = new ModelLayerLocation(StargateJourney.sgjourneyLocation("mastadge"), "main");
	public static final ModelLayerLocation GOAULD = new ModelLayerLocation(StargateJourney.sgjourneyLocation("goauld"), "main");
	
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		// Transport Rings
		event.registerLayerDefinition(TRANSPORT_RING_LAYER, TransportRingModel::createRingLayer);
		
		// Armor
		event.registerLayerDefinition(FALCON_HEAD, FalconArmorModel::createBodyLayer);
		event.registerLayerDefinition(JACKAL_HEAD, JackalArmorModel::createBodyLayer);
		
		// Entity
		event.registerLayerDefinition(ABYDOS_LIZARD, AbydosLizardModel::createBodyLayer);
		event.registerLayerDefinition(MASTADGE, MastadgeModel::createBodyLayer);
		event.registerLayerDefinition(GOAULD, GoauldModel::createBodyLayer);
	}

}
