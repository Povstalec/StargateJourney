package net.povstalec.sgjourney.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class CrystallizingRecipeInput extends SGJourneyRecipeInput
{
	protected ItemStack crystalBase = ItemStack.EMPTY;
	protected ItemStack primaryIngredient = ItemStack.EMPTY;
	protected ItemStack secondaryIngredient = ItemStack.EMPTY;
	protected FluidStack inputFluid = FluidStack.EMPTY;
	
	@Override
	public @NotNull ItemStack getItem(int index)
	{
		return switch(index)
		{
			case 0 -> this.crystalBase;
			case 1 -> this.primaryIngredient;
			case 2 -> this.secondaryIngredient;
			default -> throw new IllegalArgumentException("No item for index " + index);
		};
	}
	
	@Override
	public int size()
	{
		return 3;
	}
	
	@Override
	public void setItem(int index, ItemStack itemStack)
	{
		switch(index)
		{
			case 0 -> this.crystalBase = itemStack;
			case 1 -> this.primaryIngredient = itemStack;
			case 2 -> this.secondaryIngredient = itemStack;
			default -> throw new IllegalArgumentException("No item for index " + index);
		};
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
