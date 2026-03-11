package net.povstalec.sgjourney.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
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

import java.util.Map;

public abstract class CrystallizingRecipe extends ProgressRecipe
{
	private final NonNullList<Ingredient> ingredients;
	private final int[] amounts;
	private final ItemStack output;
	private final boolean depletePrimary;
	private final boolean depleteSecondary;
	private final int inputLiquidAmount;
	
	protected CrystallizingRecipe(ResourceLocation recipeID, JsonObject serializedRecipe)
	{
		super(recipeID, serializedRecipe);
		
		this.ingredients = NonNullList.withSize(3, Ingredient.EMPTY);
		this.amounts = new int[ingredients.size()];
		
		Pair<Ingredient, Integer> crystalBase = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "crystal_base").asMap());
		Pair<Ingredient, Integer> primaryIngredient = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "primary_ingredient").asMap());
		Pair<Ingredient, Integer> secondaryIngredient = getIngredient(GsonHelper.getAsJsonObject(serializedRecipe, "secondary_ingredient").asMap());
		
		this.ingredients.set(0, crystalBase.getFirst());
		this.amounts[0] = crystalBase.getSecond();
		
		this.ingredients.set(1, primaryIngredient.getFirst());
		this.amounts[1] = primaryIngredient.getSecond();
		
		this.ingredients.set(2, secondaryIngredient.getFirst());
		this.amounts[2] = secondaryIngredient.getSecond();
		
		this.output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(serializedRecipe, "output"));
		
		this.depletePrimary = GsonHelper.isBooleanValue(serializedRecipe, "deplete_primary") ?
				GsonHelper.getAsBoolean(serializedRecipe, "deplete_primary") : true;
		
		this.depleteSecondary = GsonHelper.isBooleanValue(serializedRecipe, "deplete_secondary") ?
				GsonHelper.getAsBoolean(serializedRecipe, "deplete_secondary") : true;
		
		this.inputLiquidAmount = GsonHelper.isNumberValue(serializedRecipe, "input_liquid_amount") ?
				GsonHelper.getAsInt(serializedRecipe, "input_liquid_amount") : 100;
	}
	
	protected CrystallizingRecipe(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
	{
		super(recipeID, friendlyByteBuf);
		
		this.amounts = friendlyByteBuf.readVarIntArray();
		this.ingredients = NonNullList.withSize(friendlyByteBuf.readInt(), Ingredient.EMPTY);
		
		for(int i = 0; i < ingredients.size(); i++)
		{
			ingredients.set(i, Ingredient.fromNetwork(friendlyByteBuf));
		}
		
		this.output = friendlyByteBuf.readItem();
		
		this.depletePrimary = friendlyByteBuf.readBoolean();
		this.depleteSecondary = friendlyByteBuf.readBoolean();
		
		this.inputLiquidAmount = friendlyByteBuf.readInt();
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf friendlyByteBuf)
	{
		super.toNetwork(friendlyByteBuf);
		
		friendlyByteBuf.writeVarIntArray(this.amounts);
		friendlyByteBuf.writeInt(this.getIngredients().size());
		
		for(Ingredient ingredient : this.getIngredients())
		{
			ingredient.toNetwork(friendlyByteBuf);
		}
		friendlyByteBuf.writeItemStack(this.getResultItem(), false);
		
		friendlyByteBuf.writeBoolean(this.depletePrimary);
		friendlyByteBuf.writeBoolean(this.depleteSecondary);
		
		friendlyByteBuf.writeInt(this.inputLiquidAmount);
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
	
	public int getInputLiquidAmount()
	{
		return this.inputLiquidAmount;
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
	public @NotNull ItemStack assemble(SimpleContainer container)
	{
		return output.copy();
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}
	
	@Override
	public @NotNull ItemStack getResultItem()
	{
		return output.copy();
	}
	
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
	public @NotNull NonNullList<Ingredient> getIngredients()
	{
		return this.ingredients;
	}
	
	//============================================================================================
	//****************************************Crystallizer****************************************
	//============================================================================================
	
	public static class Crystallizer extends CrystallizingRecipe
	{
		public static final RecipeType<Crystallizer> TYPE = new RecipeType<Crystallizer>(){};
		
		protected Crystallizer(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			super(recipeID, serializedRecipe);
		}
		
		protected Crystallizer(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			super(recipeID, friendlyByteBuf);
		}
		
		@Override
		public @NotNull RecipeSerializer<?> getSerializer()
		{
			return CrystallizerSerializer.INSTANCE;
		}
		
		@Override
		public @NotNull RecipeType<?> getType()
		{
			return TYPE;
		}
	}
	
	public static class CrystallizerSerializer implements RecipeSerializer<Crystallizer>
	{
		public static final CrystallizerSerializer INSTANCE = new CrystallizerSerializer();
		
		@Override
		public @NotNull Crystallizer fromJson(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			return new Crystallizer(recipeID, serializedRecipe);
		}
		
		@Override
		public @Nullable Crystallizer fromNetwork(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			return new Crystallizer(recipeID, friendlyByteBuf);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, Crystallizer recipe)
		{
			recipe.toNetwork(friendlyByteBuf);
		}
	}
	
	//============================================================================================
	//***********************************Advanced Crystallizer************************************
	//============================================================================================
	
	public static class AdvancedCrystallizer extends CrystallizingRecipe
	{
		public static final RecipeType<AdvancedCrystallizer> TYPE = new RecipeType<AdvancedCrystallizer>(){};
		
		protected AdvancedCrystallizer(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			super(recipeID, serializedRecipe);
		}
		
		protected AdvancedCrystallizer(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			super(recipeID, friendlyByteBuf);
		}
		
		@Override
		public @NotNull RecipeSerializer<?> getSerializer()
		{
			return AdvancedCrystallizerSerializer.INSTANCE;
		}
		
		@Override
		public @NotNull RecipeType<?> getType()
		{
			return TYPE;
		}
	}
	
	public static class AdvancedCrystallizerSerializer implements RecipeSerializer<AdvancedCrystallizer>
	{
		public static final AdvancedCrystallizerSerializer INSTANCE = new AdvancedCrystallizerSerializer();
		
		@Override
		public @NotNull AdvancedCrystallizer fromJson(ResourceLocation recipeID, JsonObject serializedRecipe)
		{
			return new AdvancedCrystallizer(recipeID, serializedRecipe);
		}
		
		@Override
		public @Nullable AdvancedCrystallizer fromNetwork(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
		{
			return new AdvancedCrystallizer(recipeID, friendlyByteBuf);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf friendlyByteBuf, AdvancedCrystallizer recipe)
		{
			recipe.toNetwork(friendlyByteBuf);
		}
	}
}
