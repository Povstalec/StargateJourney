package net.povstalec.sgjourney.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class LiquidizingRecipeInput extends SGJourneyRecipeInput
{
	protected ItemStack inputItem = ItemStack.EMPTY;
	protected FluidStack inputFluid = FluidStack.EMPTY;
	
	@Override
	public @NotNull ItemStack getItem(int index)
	{
		if(index == 0)
			return this.inputItem;
		else
			throw new IllegalArgumentException("No item for index " + index);
	}
	
	@Override
	public int size()
	{
		return 1;
	}
	
	@Override
	public void setItem(int index, ItemStack itemStack)
	{
		if(index == 0)
			this.inputItem = itemStack;
		else
			throw new IllegalArgumentException("No item for index " + index);
	}
	
	@Override
	public @NotNull FluidStack getFluid(int index)
	{
		if(index == 0)
			return this.inputFluid;
		else
			throw new IllegalArgumentException("No fluid for index " + index);
	}
	
	@Override
	public void setFluid(int index, FluidStack fluidStack)
	{
		if(index == 0)
			this.inputFluid = fluidStack;
		else
			throw new IllegalArgumentException("No fluid for index " + index);
	}
	
	@Override
	public int fluidContainersSize()
	{
		return 1;
	}
}
