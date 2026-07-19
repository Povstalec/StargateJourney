package net.povstalec.sgjourney.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.transporter.AncientBlockEntityTransportRings;
import net.povstalec.sgjourney.common.sgjourney.transporter.GoauldBlockEntityTransportRings;
import net.povstalec.sgjourney.common.sgjourney.transporter.TransporterType;

public class TransporterInit
{
	public static final ResourceKey<Registry<TransporterType<?>>> TRANSPORTER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(TransporterType.TRANSPORTER_TYPE_LOCATION);
	public static final Registry<TransporterType<?>> TRANSPORTER_TYPE_REGISTRY = new RegistryBuilder<>(TRANSPORTER_TYPE_REGISTRY_KEY).sync(true).create();
	public static final DeferredRegister<TransporterType<?>> TRANSPORTER_TYPES = DeferredRegister.create(TransporterType.TRANSPORTER_TYPE_LOCATION, StargateJourney.MODID);
	
	
	
	// Block Entity Transporters
	public static final DeferredHolder<TransporterType<?>, TransporterType<AncientBlockEntityTransportRings>> ANCIENT_TRANSPORT_RINGS = TRANSPORTER_TYPES.register("ancient_transport_rings", () ->
			new TransporterType<>(AncientBlockEntityTransportRings::new));
	public static final DeferredHolder<TransporterType<?>, TransporterType<GoauldBlockEntityTransportRings>> GOAULD_TRANSPORT_RINGS = TRANSPORTER_TYPES.register("goauld_transport_rings", () ->
			new TransporterType<>(GoauldBlockEntityTransportRings::new));
	
	
	
	public static void register(IEventBus eventBus)
	{
		TRANSPORTER_TYPES.register(eventBus);
	}
}
