package net.povstalec.sgjourney.common.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.Goauld;
import net.povstalec.sgjourney.common.entities.Human;
import net.povstalec.sgjourney.common.entities.Jaffa;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.PowerCellItem;
import net.povstalec.sgjourney.common.items.VialItem;

public class ModEvents
{
	@EventBusSubscriber(modid = StargateJourney.MODID, bus = EventBusSubscriber.Bus.MOD)
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
	
	/*@EventBusSubscriber(value = Dist.DEDICATED_SERVER, modid = StargateJourney.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class Server
	{
	
	}*/
	
	@EventBusSubscriber(value = Dist.CLIENT, modid = StargateJourney.MODID, bus = EventBusSubscriber.Bus.MOD)
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
			
			event.register((stack, layer) ->
			{
				if(layer == 0)
					return -1;
				
				if(stack.getItem() instanceof PowerCellItem powerCell)
				{
					FluidStack fluidStack = powerCell.getFluidStack(stack);
					IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
					return renderProperties.getTintColor(fluidStack);
				}
				
				return -1;
			}, ItemInit.NAQUADAH_POWER_CELL.get());
		}
	}
}
