package net.povstalec.sgjourney.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LiquidizingRecipe extends ProgressRecipe
{
	private final Ingredient ingredient;
	private final int inputLiquidAmount;
	private final int outputLiquidAmount;
	
	protected LiquidizingRecipe(ResourceLocation recipeID, JsonObject serializedRecipe)
	{
		super(recipeID, serializedRecipe);
		
		JsonElement ingredientElement = GsonHelper.isArrayNode(serializedRecipe, "ingredient") ?
				GsonHelper.getAsJsonArray(serializedRecipe, "ingredient") : GsonHelper.getAsJsonObject(serializedRecipe, "ingredient");
		this.ingredient = Ingredient.fromJson(ingredientElement);
		this.inputLiquidAmount = GsonHelper.getAsInt(serializedRecipe, "input_liquid_amount");
		this.outputLiquidAmount = GsonHelper.getAsInt(serializedRecipe, "output_liquid_amount");
	}
	
	protected LiquidizingRecipe(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
	{
		super(recipeID, friendlyByteBuf);
		
		this.ingredient = Ingredient.fromNetwork(friendlyByteBuf);
		this.inputLiquidAmount = friendlyByteBuf.readInt();
		this.outputLiquidAmount = friendlyByteBuf.readInt();
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf friendlyByteBuf)
	{
		super.toNetwork(friendlyByteBuf);
		
		this.ingredient.toNetwork(friendlyByteBuf);
		friendlyByteBuf.writeInt(this.inputLiquidAmount);
		friendlyByteBuf.writeInt(this.outputLiquidAmount);
	}
	
	public int getInputLiquidAmount()
	{
		return inputLiquidAmount;
	}
	
	public int getOutputLiquidAmount()
	{
		return outputLiquidAmount;
	}
	
	@Override
	public boolean matches(SimpleContainer container, Level level)
	{
		if(level.isClientSide())
			return false;
		
		return ingredient.test(container.getItem(0));
	}
	
	@Override
	public @NotNull ItemStack assemble(SimpleContainer container)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}
	
	@Override
	public @NotNull ItemStack getResultItem()
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public @NotNull NonNullList<Ingredient> getIngredients()
	{
		NonNullList<Ingredient> ingredients = NonNullList.withSize(1, Ingredient.EMPTY);
		ingredients.set(0, ingredient);
		return ingredients;
	}
	
	//============================================================================================
	//*************************************Naquadah Liquidizer************************************
	//============================================================================================
	
	public static class NaquadahLiquidizer extends LiquidizingRecipe
	{
		public static final RecipeType<NaquadahLiquidizer> TYPE = new RecipeType<NaquadahLiquidizer>(){};
		
		protected NaquadahLiquidizer(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			super(recipeID, serializedRecipe);
		}
		
		protected NaquadahLiquidizer(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			super(recipeID, friendlyByteBuf);
		}
		
		@Override
		public @NotNull RecipeSerializer<?> getSerializer()
		{
			return NaquadahLiquidizerSerializer.INSTANCE;
		}
		
		@Override
		public @NotNull RecipeType<?> getType()
		{
			return TYPE;
		}
	}
	
	public static class NaquadahLiquidizerSerializer implements RecipeSerializer<NaquadahLiquidizer>
	{
		public static final NaquadahLiquidizerSerializer INSTANCE = new NaquadahLiquidizerSerializer();
		
		@Override
		public @NotNull LiquidizingRecipe.NaquadahLiquidizer fromJson(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			return new NaquadahLiquidizer(recipeID, serializedRecipe);
		}
		
		@Override
		public @Nullable LiquidizingRecipe.NaquadahLiquidizer fromNetwork(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			return new NaquadahLiquidizer(recipeID, friendlyByteBuf);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, NaquadahLiquidizer recipe)
		{
			recipe.toNetwork(friendlyByteBuf);
		}
	}
	
	//============================================================================================
	//**********************************Heavy Naquadah Liquidizer*********************************
	//============================================================================================
	
	public static class HeavyNaquadahLiquidizer extends LiquidizingRecipe
	{
		public static final RecipeType<HeavyNaquadahLiquidizer> TYPE = new RecipeType<HeavyNaquadahLiquidizer>(){};
		
		protected HeavyNaquadahLiquidizer(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			super(recipeID, serializedRecipe);
		}
		
		protected HeavyNaquadahLiquidizer(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			super(recipeID, friendlyByteBuf);
		}
		
		@Override
		public @NotNull RecipeSerializer<?> getSerializer()
		{
			return HeavyNaquadahLiquidizerSerializer.INSTANCE;
		}
		
		@Override
		public @NotNull RecipeType<?> getType()
		{
			return TYPE;
		}
	}
	
	public static class HeavyNaquadahLiquidizerSerializer implements RecipeSerializer<HeavyNaquadahLiquidizer>
	{
		public static final HeavyNaquadahLiquidizerSerializer INSTANCE = new HeavyNaquadahLiquidizerSerializer();
		
		@Override
		public @NotNull LiquidizingRecipe.HeavyNaquadahLiquidizer fromJson(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			return new HeavyNaquadahLiquidizer(recipeID, serializedRecipe);
		}
		
		@Override
		public @Nullable LiquidizingRecipe.HeavyNaquadahLiquidizer fromNetwork(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			return new HeavyNaquadahLiquidizer(recipeID, friendlyByteBuf);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, HeavyNaquadahLiquidizer recipe)
		{
			recipe.toNetwork(friendlyByteBuf);
		}
	}
}
