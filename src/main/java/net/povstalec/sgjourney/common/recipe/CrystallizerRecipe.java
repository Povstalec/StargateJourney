package net.povstalec.sgjourney.common.recipe;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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

public class CrystallizerRecipe implements Recipe<SimpleContainer>
{
	private final ResourceLocation recipeID;
	private final NonNullList<Ingredient> ingredients;
	private final int[] amounts;
	private final ItemStack output;
	private final boolean depletePrimary;
	private final boolean depleteSecondary;
	
	public CrystallizerRecipe(ResourceLocation recipeID, ItemStack output, NonNullList<Ingredient> ingredients, int[] amounts, boolean depletePrimary, boolean depleteSecondary)
	{
		this.recipeID = recipeID;
		this.ingredients = ingredients;
		this.amounts = amounts;
		this.output = output;
		this.depletePrimary = depletePrimary;
		this.depleteSecondary = depleteSecondary;
	}
	
	public int getAmountInSlot(int slot)
	{
		if(slot < 0 || slot >= this.amounts.length)
			return 0;
		
		return this.amounts[slot];
	}
	
	public boolean depletePrimary()
	{
		return this.depletePrimary;
	}
	
	public boolean depleteSecondary()
	{
		return this.depleteSecondary;
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
	public ItemStack assemble(SimpleContainer container, RegistryAccess access)
	{
		return output.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess access)
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
	
	public static class Type implements RecipeType<CrystallizerRecipe>
	{
		private Type(){}
		public static final Type INSTANCE = new Type();
		public static final String ID = "crystallizing";
	}
	
	public static class Serializer implements RecipeSerializer<CrystallizerRecipe>
	{
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = new ResourceLocation(StargateJourney.MODID, "crystallizing");
		
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
		public CrystallizerRecipe fromJson(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "output"));
			
			NonNullList<Ingredient> ingredients = NonNullList.withSize(3, Ingredient.EMPTY);
			int[] amounts = new int[ingredients.size()];
			
			Pair<Ingredient, Integer> crystalBase = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "crystal_base").asMap());
			Pair<Ingredient, Integer> primaryIngredient = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "primary_ingredient").asMap());
			Pair<Ingredient, Integer> secondaryIngredient = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "secondary_ingredient").asMap());
			
			boolean depletePrimary = GsonHelper.isBooleanValue(serializedRecipe, "deplete_primary") ?
					GsonHelper.getAsBoolean(serializedRecipe, "deplete_primary") : true;
			
			boolean depleteSecondary = GsonHelper.isBooleanValue(serializedRecipe, "deplete_secondary") ?
					GsonHelper.getAsBoolean(serializedRecipe, "deplete_secondary") : true;

			ingredients.set(0, crystalBase.getFirst());
			amounts[0] = crystalBase.getSecond();
			
			ingredients.set(1, primaryIngredient.getFirst());
			amounts[1] = primaryIngredient.getSecond();
			
			ingredients.set(2, secondaryIngredient.getFirst());
			amounts[2] = secondaryIngredient.getSecond();
			
			return new CrystallizerRecipe(recipeID, output, ingredients, amounts, depletePrimary, depleteSecondary);
		}

		@Override
		public @Nullable CrystallizerRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			int[] amounts = friendlyByteBuf.readVarIntArray();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(friendlyByteBuf.readInt(), Ingredient.EMPTY);
			
			for(int i = 0; i < ingredients.size(); i++)
			{
				ingredients.set(i, Ingredient.fromNetwork(friendlyByteBuf));
			}
			
			ItemStack output = friendlyByteBuf.readItem();
			
			boolean depletePrimary = friendlyByteBuf.readBoolean();
			boolean depleteSecondary = friendlyByteBuf.readBoolean();
			
			return new CrystallizerRecipe(recipeID, output, ingredients, amounts, depletePrimary, depleteSecondary);
		}

		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, CrystallizerRecipe recipe)
		{
			friendlyByteBuf.writeVarIntArray(recipe.amounts);
			friendlyByteBuf.writeInt(recipe.getIngredients().size());
			
			for(Ingredient ingredient : recipe.getIngredients())
			{
				ingredient.toNetwork(friendlyByteBuf);
			}
			friendlyByteBuf.writeItemStack(recipe.getResultItem(null), false);
			
			friendlyByteBuf.writeBoolean(recipe.depletePrimary);
			friendlyByteBuf.writeBoolean(recipe.depleteSecondary);
		}
		
	}
}
