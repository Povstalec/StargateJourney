package net.povstalec.sgjourney.common.recipe;

import net.minecraft.world.item.crafting.RecipeInput;

public abstract class ProgressRecipe<I extends RecipeInput> extends SGJourneyRecipe<I>
{
	protected final int progressTime;
	
	public ProgressRecipe(int progressTime)
	{
		this.progressTime = progressTime;
	}
	
	public int getProgressTime()
	{
		return progressTime;
	}
}
