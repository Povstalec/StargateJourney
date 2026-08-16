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

import java.util.Random;

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
	
	// Liquid Naquadah Vial
	
	public static ItemStack liquidNaquadahSetup(int amount)
	{
		return FluidItem.fluidSetup(ItemInit.VIAL.get(), FluidInit.LIQUID_NAQUADAH_SOURCE.get(), amount);
	}
	
	public static ItemStack liquidNaquadahSetup()
	{
		return liquidNaquadahSetup(CommonTechConfig.vial_capacity.get());
	}
	
	public static ItemStack randomLiquidNaquadahSetup(int minCapacity, int maxCapacity)
	{
		Random random = new Random();
		return liquidNaquadahSetup(random.nextInt(minCapacity, maxCapacity + 1));
	}
	
	// Heavy Liquid Naquadah Vial
	
	public static ItemStack heavyLiquidNaquadahSetup(int amount)
	{
		return FluidItem.fluidSetup(ItemInit.VIAL.get(), FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), amount);
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		return heavyLiquidNaquadahSetup(CommonTechConfig.vial_capacity.get());
	}
	
	public static ItemStack randomHeavyLiquidNaquadahSetup(int minCapacity, int maxCapacity)
	{
		Random random = new Random();
		return heavyLiquidNaquadahSetup(random.nextInt(minCapacity, maxCapacity + 1));
	}
}
