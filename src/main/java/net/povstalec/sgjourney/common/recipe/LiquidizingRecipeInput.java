package net.povstalec.sgjourney.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public record CrystalRecipeInput(ItemStack crystalBase, ItemStack primaryIngredient, ItemStack secondaryIngredient, FluidStack inputFluid) implements RecipeInput
{
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
	
	public int size()
	{
		return 3;
	}
}
