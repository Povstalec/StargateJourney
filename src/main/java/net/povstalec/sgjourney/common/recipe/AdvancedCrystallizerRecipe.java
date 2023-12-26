package net.povstalec.sgjourney.common.recipe;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class AdvancedCrystallizerRecipe implements Recipe<SimpleContainer>
{
	private final ResourceLocation recipeID;
	private final NonNullList<Ingredient> ingredients;
	private final int[] amounts;
	private final ItemStack output;
	
	public AdvancedCrystallizerRecipe(ResourceLocation recipeID, ItemStack output, NonNullList<Ingredient> ingredients, int[] amounts)
	{
		this.recipeID = recipeID;
		this.ingredients = ingredients;
		this.amounts = amounts;
		this.output = output;
	}
	
	public int getAmountInSlot(int slot)
	{
		if(slot < 0 || slot >= this.amounts.length)
			return 0;
		
		return this.amounts[slot];
	}
	
	public boolean itemStackMatches(SimpleContainer container, int slot)
	{
		ItemStack stack = container.getItem(slot);
		
		return ingredients.get(slot).test(stack) && amounts[slot] <= stack.getCount();
	}

	@Override
	public boolean matches(SimpleContainer container, Level level)
	{
		if(level.isClientSide())
			return false;
		
		return itemStackMatches(container, 0) && itemStackMatches(container, 1) && itemStackMatches(container, 2);
	}

	@Override
	public ItemStack assemble(SimpleContainer container)
	{
		return output.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}

	@Override
	public ItemStack getResultItem()
	{
		return output.copy();
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
		return this.ingredients;
	}
	
	public static class Type implements RecipeType<AdvancedCrystallizerRecipe>
	{
		private Type(){}
		public static final Type INSTANCE = new Type();
		public static final String ID = "advanced_crystallizing";
	}
	
	public static class Serializer implements RecipeSerializer<AdvancedCrystallizerRecipe>
	{
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = new ResourceLocation(StargateJourney.MODID, "advanced_crystallizing");
		
		public static Pair<Ingredient, Integer> getIngredient(Map<String, JsonElement> pair)
		{
			JsonElement item = pair.get("item");
			JsonObject json = new JsonObject();
			json.add("item", item);

			Ingredient ingredient = Ingredient.fromJson(json);
			int amount = pair.get("amount").getAsInt();
			
			return new Pair<Ingredient, Integer>(ingredient, amount);
		}
		
		@Override
		public AdvancedCrystallizerRecipe fromJson(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "output"));
			
			NonNullList<Ingredient> ingredients = NonNullList.withSize(3, Ingredient.EMPTY);
			int[] amounts = new int[ingredients.size()];
			
			Pair<Ingredient, Integer> crystalBase = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "crystal_base").asMap());
			Pair<Ingredient, Integer> primaryIngredient = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "primary_ingredient").asMap());
			Pair<Ingredient, Integer> secondaryIngredient = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "secondary_ingredient").asMap());

			ingredients.set(0, crystalBase.getFirst());
			amounts[0] = crystalBase.getSecond();
			
			ingredients.set(1, primaryIngredient.getFirst());
			amounts[1] = primaryIngredient.getSecond();
			
			ingredients.set(2, secondaryIngredient.getFirst());
			amounts[2] = secondaryIngredient.getSecond();
			
			return new AdvancedCrystallizerRecipe(recipeID, output, ingredients, amounts);
		}

		@Override
		public @Nullable AdvancedCrystallizerRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			int[] amounts = friendlyByteBuf.readVarIntArray();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(friendlyByteBuf.readInt(), Ingredient.EMPTY);
			
			for(int i = 0; i < ingredients.size(); i++)
			{
				ingredients.set(i, Ingredient.fromNetwork(friendlyByteBuf));
			}
			
			ItemStack output = friendlyByteBuf.readItem();
			return new AdvancedCrystallizerRecipe(recipeID, output, ingredients, amounts);
		}

		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, AdvancedCrystallizerRecipe recipe)
		{
			friendlyByteBuf.writeVarIntArray(recipe.amounts);
			friendlyByteBuf.writeInt(recipe.getIngredients().size());
			
			for(Ingredient ingredient : recipe.getIngredients())
			{
				ingredient.toNetwork(friendlyByteBuf);
			}
			friendlyByteBuf.writeItemStack(recipe.getResultItem(), false);
		}
		
	}
}
