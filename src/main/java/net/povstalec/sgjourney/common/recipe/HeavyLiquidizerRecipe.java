package net.povstalec.sgjourney.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import org.jetbrains.annotations.Nullable;

public class HeavyLiquidizerRecipe implements Recipe<SimpleContainer>
{
	private final ResourceLocation recipeID;
	private final Ingredient ingredient;
	private final int inputAmount;
	private final int outputAmount;
	
	public HeavyLiquidizerRecipe(ResourceLocation recipeID, Ingredient ingredient, int inputAmount, int outputAmount)
	{
		this.recipeID = recipeID;
		this.ingredient = ingredient;
		this.inputAmount = inputAmount;
		this.outputAmount = outputAmount;
	}
	
	public int getInputAmount()
	{
		return inputAmount;
	}
	
	public int getOutputAmount()
	{
		return outputAmount;
	}

	@Override
	public boolean matches(SimpleContainer container, Level level)
	{
		if(level.isClientSide())
			return false;
		
		return ingredient.test(container.getItem(0));
	}

	@Override
	public ItemStack assemble(SimpleContainer container)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}

	@Override
	public ItemStack getResultItem()
	{
		return ItemStack.EMPTY;
	}

	@Override
	public ResourceLocation getId()
	{
		return recipeID;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return Serializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType()
	{
		return Type.INSTANCE;
	}

	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		NonNullList<Ingredient> ingredients = NonNullList.withSize(1, Ingredient.EMPTY);
		ingredients.set(0, ingredient);
		return ingredients;
	}
	
	public static class Type implements RecipeType<HeavyLiquidizerRecipe>
	{
		private Type(){}
		public static final Type INSTANCE = new Type();
	}
	
	public static class Serializer implements RecipeSerializer<HeavyLiquidizerRecipe>
	{
		public static final Serializer INSTANCE = new Serializer();
		
		@Override
		public HeavyLiquidizerRecipe fromJson(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			JsonElement ingredientElement = GsonHelper.isArrayNode(serializedRecipe, "ingredient") ?
					GsonHelper.getAsJsonArray(serializedRecipe, "ingredient") : GsonHelper.getAsJsonObject(serializedRecipe, "ingredient");
			Ingredient ingredient = Ingredient.fromJson(ingredientElement);
			int inputAmount = GsonHelper.getAsInt(serializedRecipe, "liquid_naquadah_input_amount");
			int outputAmount = GsonHelper.getAsInt(serializedRecipe, "heavy_liquid_naquadah_output_amount");
			
			return new HeavyLiquidizerRecipe(recipeID, ingredient, inputAmount, outputAmount);
		}

		@Override
		public @Nullable HeavyLiquidizerRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
			int inputAmount = friendlyByteBuf.readInt();
			int outputAmount = friendlyByteBuf.readInt();
			
			return new HeavyLiquidizerRecipe(recipeID, ingredient, inputAmount, outputAmount);
		}

		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, HeavyLiquidizerRecipe recipe)
		{
			recipe.ingredient.toNetwork(friendlyByteBuf);
			friendlyByteBuf.writeInt(recipe.inputAmount);
			friendlyByteBuf.writeInt(recipe.outputAmount);
		}
		
	}
}
