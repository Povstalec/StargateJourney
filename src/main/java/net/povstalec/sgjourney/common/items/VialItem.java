package net.povstalec.sgjourney.common.items;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class VialItem extends FluidItem
{
	public static final int MAX_CAPACITY = 150;
	
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
		return MAX_CAPACITY; // TODO Make this configurable
	}
	
	public static ItemStack liquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.VIAL.get());
        
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler ->
        {
        	fluidHandler.fill(new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get().getSource(), MAX_CAPACITY), FluidAction.EXECUTE);
        });
		
		return stack;
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.VIAL.get());
        
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler ->
        {
        	fluidHandler.fill(new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get().getSource(), MAX_CAPACITY), FluidAction.EXECUTE);
        });
		
		return stack;
	}
}
