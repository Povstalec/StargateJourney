package net.povstalec.sgjourney.common.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public abstract class SGJourneyRecipe<I extends RecipeInput> implements Recipe<I>
{
	@Override
	public boolean isSpecial()
	{
		return true; // Prevents Minecraft from screaming "Unknown recipe category" everywhere
	}
}
