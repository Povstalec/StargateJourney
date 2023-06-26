package net.povstalec.sgjourney.common.events;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.init.EntityInit;

@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents
{
	@SubscribeEvent
	public static void entityAttributeEvent(EntityAttributeCreationEvent event)
	{
		event.put(EntityInit.GOAULD.get(), Goauld.createAttributes().build());
	}
}
