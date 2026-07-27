package net.povstalec.sgjourney.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public abstract class SGJourneyRecipeInput implements RecipeInput
{
	public abstract void setItem(int index, ItemStack itemStack);
	
	public abstract @NotNull FluidStack getFluid(int index);
	
	public abstract void setFluid(int index, FluidStack fluidStack);
	
	public abstract int fluidContainersSize();
	
	public boolean testFluid(int index, FluidStack exampleFluid)
	{
		FluidStack containerFluid = getFluid(index);
		
		return exampleFluid.getFluid().isSame(containerFluid.getFluid()) && containerFluid.getAmount() >= exampleFluid.getAmount();
	}
}
