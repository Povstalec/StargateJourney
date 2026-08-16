package net.povstalec.sgjourney.common.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.transporter.*;

import java.util.function.Supplier;

public class TransporterInit
{
	public static final DeferredRegister<TransporterType<?>> TRANSPORTER_TYPES = DeferredRegister.create(TransporterType.TRANSPORTER_TYPE_LOCATION, StargateJourney.MODID);
	public static final Supplier<IForgeRegistry<TransporterType<?>>> TRANSPORTER_TYPE = TRANSPORTER_TYPES.makeRegistry(RegistryBuilder::new);
	
	
	
	// Block Entity Transporters
	public static final RegistryObject<TransporterType<AncientBlockEntityTransportRings>> ANCIENT_TRANSPORT_RINGS = TRANSPORTER_TYPES.register("ancient_transport_rings", () ->
			new TransporterType<>(AncientBlockEntityTransportRings::new));
	public static final RegistryObject<TransporterType<GoauldBlockEntityTransportRings>> GOAULD_TRANSPORT_RINGS = TRANSPORTER_TYPES.register("goauld_transport_rings", () ->
			new TransporterType<>(GoauldBlockEntityTransportRings::new));
	
	
	
	public static void register(IEventBus eventBus)
	{
		TRANSPORTER_TYPES.register(eventBus);
	}
}
