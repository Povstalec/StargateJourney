package net.povstalec.sgjourney.common.items;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.fluids.FluidStack;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class VialItem extends FluidItem
{
	public VialItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return false;
	}
	
	public boolean isCorrectFluid(FluidStack fluidStack)
	{
		return fluidStack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get() ||
				fluidStack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
	}
	
	public int getFluidCapacity(ItemStack stack)
	{
		return CommonTechConfig.vial_capacity.get();
	}
	
	public static ItemStack liquidNaquadahSetup(int amount)
	{
		return FluidItem.fluidSetup(ItemInit.VIAL.get(), FluidInit.LIQUID_NAQUADAH_SOURCE.get(), amount);
	}
	
	public static ItemStack liquidNaquadahSetup()
	{
		return liquidNaquadahSetup(CommonTechConfig.vial_capacity.get());
	}
	
	public static ItemStack heavyLiquidNaquadahSetup(int amount)
	{
		return FluidItem.fluidSetup(ItemInit.VIAL.get(), FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), amount);
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		return heavyLiquidNaquadahSetup(CommonTechConfig.vial_capacity.get());
	}
	
	
	
	public static class FluidHandler extends FluidHandlerItemStack
	{
		public FluidHandler(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container)
		{
			super(componentType, container, CommonTechConfig.vial_capacity.get());
		}
		
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		{
			return stack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get() ||
					stack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
		}
	}
}
