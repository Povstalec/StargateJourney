package net.povstalec.sgjourney.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CrystalRecipeInput(ItemStack crystalBase, ItemStack primaryIngredient, ItemStack secondaryIngredient) implements RecipeInput
{
	public ItemStack getItem(int index)
	{
		switch(index)
		{
			case 0:
				return this.crystalBase;
			case 1:
				return this.primaryIngredient;
			case 2:
				return this.secondaryIngredient;
		};
		
		throw new IllegalArgumentException("No item for index " + index);
	}
	
	public int size()
	{
		return 3;
	}
}
