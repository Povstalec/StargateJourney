package net.povstalec.sgjourney.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;

public abstract class ProgressRecipe<C extends Container> extends SGJourneyRecipe<C>
{
	protected final int progressTime;
	
	public ProgressRecipe(ResourceLocation recipeID, int progressTime)
	{
		super(recipeID);
		
		this.progressTime = progressTime;
	}
	
	protected ProgressRecipe(ResourceLocation recipeID, JsonObject serializedRecipe)
	{
		this(recipeID, GsonHelper.isNumberValue(serializedRecipe, "progress") ? GsonHelper.getAsInt(serializedRecipe, "progress") : 100);
	}
	
	protected ProgressRecipe(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
	{
		this(recipeID, friendlyByteBuf.readInt());
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf friendlyByteBuf)
	{
		friendlyByteBuf.writeInt(this.progressTime);
	}
	
	public int getProgressTime()
	{
		return progressTime;
	}
}
