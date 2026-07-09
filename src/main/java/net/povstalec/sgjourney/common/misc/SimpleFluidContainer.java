package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class SimpleFluidContainer extends SimpleContainer
{
	private final NonNullList<FluidStack> fluids;
	
	public SimpleFluidContainer(int itemsSize, int fluidsSize)
	{
		super(itemsSize);
		
		fluids = NonNullList.withSize(fluidsSize, FluidStack.EMPTY);
	}
	
	public FluidStack getFluid(int index)
	{
		return index >= 0 && index < fluids.size() ? fluids.get(index) : FluidStack.EMPTY;
	}
	
	public void setFluid(int index, FluidStack fluidStack)
	{
		fluids.set(index, fluidStack);
		
		this.setChanged();
	}
	
	public int getFluidContainerSize()
	{
		return this.fluids.size();
	}
	
	public boolean testFluid(int index, FluidStack exampleFluid)
	{
		FluidStack containerFluid = getFluid(index);
		
		return exampleFluid.getFluid().isSame(containerFluid.getFluid()) && containerFluid.getAmount() >= exampleFluid.getAmount();
	}
}
