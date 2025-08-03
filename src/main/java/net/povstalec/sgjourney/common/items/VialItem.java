package net.povstalec.sgjourney.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;

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
	
	public static ItemStack liquidNaquadahSetup()
	{
		return FluidItem.fluidSetup(ItemInit.VIAL.get(), FluidInit.LIQUID_NAQUADAH_SOURCE.get(), CommonTechConfig.vial_capacity.get());
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		return FluidItem.fluidSetup(ItemInit.VIAL.get(), FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), CommonTechConfig.vial_capacity.get());
	}
}
