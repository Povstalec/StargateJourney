package net.povstalec.sgjourney.common.scheduler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.povstalec.sgjourney.StargateJourney;

public class SchedulerInit {

	public static void register()
	{
		TimerCallbacks.SERVER_CALLBACKS.register(
				new CodecSerializer<>(
						new ResourceLocation(StargateJourney.MODID, "stargate_destruction"),
						StargateDestruction.class, StargateDestruction.CODEC));
		TimerCallbacks.SERVER_CALLBACKS.register(
				new CodecSerializer<>(
						new ResourceLocation(StargateJourney.MODID, "shielding_destruction"),
						ShieldingDestruction.class, ShieldingDestruction.CODEC));

	}

}
