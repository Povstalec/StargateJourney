package net.povstalec.sgjourney.common.events;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.entities.*;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.PowerCellItem;
import net.povstalec.sgjourney.common.items.VialItem;
import net.povstalec.sgjourney.common.misc.ColorUtil;

import javax.annotation.Nullable;

public class ModEvents
{
	@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Common
	{
		@SubscribeEvent
		public static void entityAttributeEvent(EntityAttributeCreationEvent event)
		{
			// Animals
			event.put(EntityInit.MASTADGE.get(), Mastadge.createAttributes().build());
			event.put(EntityInit.ABYDOS_LIZARD.get(), AbydosLizard.createAttributes().build());
			// Humanoids
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
		public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event)
		{
			event.register((state, blockAndTintGetter, pos, layer) -> ColorUtil.getTint(198, 174, 113, 255), BlockInit.SANDSTONE_SYMBOL.get());
		}
		
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
			
			event.register((stack, layer) -> ColorUtil.getTint(198, 174, 113, 255), BlockInit.SANDSTONE_SYMBOL.get());
		}
	}
}
