package net.povstalec.sgjourney.common.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.stargate.SolarSystem;

public class PointOfOriginInit
{
	public static final DeferredRegister<SolarSystem> SOLAR_SYSTEMS = DeferredRegister.create(SolarSystem.REGISTRY_KEY, StargateJourney.MODID);
	
	public static void register(IEventBus eventBus)
	{
		SOLAR_SYSTEMS.register(eventBus);
	}
}
