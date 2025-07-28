package net.povstalec.sgjourney.common.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.entities.Human;
import net.povstalec.sgjourney.common.entities.Jaffa;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.VialItem;

public class ModEvents
{
	@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Common
	{
		@SubscribeEvent
		public static void entityAttributeEvent(EntityAttributeCreationEvent event)
		{
			event.put(EntityInit.GOAULD.get(), Goauld.createAttributes().build());
			event.put(EntityInit.HUMAN.get(), Human.createAttributes().build());
			event.put(EntityInit.JAFFA.get(), Jaffa.createAttributes().build());
		}
	}
	
	/*@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Server
	{
	
	}*/
	
	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Client
	{
		@SubscribeEvent
		public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
		{
			event.register((stack, layer) -> 
			{
				if(layer == 0)
					return -1;
				
				if(stack.getItem() instanceof VialItem vial)
				{
					FluidStack fluidStack = vial.getFluidStack(stack);
					IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
					return renderProperties.getTintColor(fluidStack);
				}
				
				return -1;
			}, ItemInit.VIAL.get());
		}
	}
}
