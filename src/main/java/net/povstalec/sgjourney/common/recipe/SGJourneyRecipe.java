package net.povstalec.sgjourney.common.recipe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

public abstract class SGJourneyRecipe implements Recipe<SimpleContainer>
{
	protected final ResourceLocation recipeID;
	
	public SGJourneyRecipe(ResourceLocation recipeID)
	{
		this.recipeID = recipeID;
	}
	
	public abstract void toNetwork(FriendlyByteBuf friendlyByteBuf);
	
	@Override
	public @NotNull ResourceLocation getId()
	{
		return recipeID;
	}
	
	@Override
	public boolean isSpecial()
	{
		return true; // Prevents Minecraft from screaming "Unknown recipe category" everywhere
	}
}
