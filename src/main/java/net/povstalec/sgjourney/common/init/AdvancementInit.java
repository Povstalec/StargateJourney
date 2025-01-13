package net.povstalec.sgjourney.common.init;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.advancements.WormholeTravelCriterion;

import java.util.function.Supplier;

public class AdvancementInit
{
	public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES = DeferredRegister.create(Registries.TRIGGER_TYPE, StargateJourney.MODID);
	
	public static final Supplier<WormholeTravelCriterion> WORMHOLE_CRITERION_TRIGGER = TRIGGER_TYPES.register("stargate_wormhole_travel", WormholeTravelCriterion::new);
	
	
	
	public static void register(IEventBus eventBus)
	{
		TRIGGER_TYPES.register(eventBus);
	}
}
