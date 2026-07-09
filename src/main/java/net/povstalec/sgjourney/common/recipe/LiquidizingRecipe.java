package net.povstalec.sgjourney.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.misc.SimpleFluidContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LiquidizingRecipe extends ProgressRecipe<SimpleFluidContainer>
{
	private final Ingredient ingredient;
	private final FluidStack inputFluid;
	private final FluidStack outputFluid;
	
	protected LiquidizingRecipe(ResourceLocation recipeID, JsonObject serializedRecipe)
	{
		super(recipeID, serializedRecipe);
		
		JsonElement ingredientElement = GsonHelper.isArrayNode(serializedRecipe, "ingredient") ?
				GsonHelper.getAsJsonArray(serializedRecipe, "ingredient") : GsonHelper.getAsJsonObject(serializedRecipe, "ingredient");
		this.ingredient = Ingredient.fromJson(ingredientElement);
		
		if(serializedRecipe.has("input_fluid"))
			this.inputFluid = deserializeFluidStack(GsonHelper.getAsJsonObject(serializedRecipe, "input_fluid"), defaultInputFluid());
		else
			this.inputFluid = defaultInputFluid();
		
		if(serializedRecipe.has("output_fluid"))
			this.outputFluid = deserializeFluidStack(GsonHelper.getAsJsonObject(serializedRecipe, "output_fluid"), defaultOutputFluid());
		else
			this.outputFluid = defaultOutputFluid();
	}
	
	protected LiquidizingRecipe(ResourceLocation recipeID, FriendlyByteBuf friendlyByteBuf)
	{
		super(recipeID, friendlyByteBuf);
		
		this.ingredient = Ingredient.fromNetwork(friendlyByteBuf);
		this.inputFluid = FluidStack.readFromPacket(friendlyByteBuf);
		this.outputFluid = FluidStack.readFromPacket(friendlyByteBuf);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf friendlyByteBuf)
	{
		super.toNetwork(friendlyByteBuf);
		
		this.ingredient.toNetwork(friendlyByteBuf);
		this.inputFluid.writeToPacket(friendlyByteBuf);
		this.outputFluid.writeToPacket(friendlyByteBuf);
	}
	
	public FluidStack getInputFluid()
	{
		return inputFluid;
	}
	
	public FluidStack getOutputFluid()
	{
		return outputFluid;
	}
	
	@Override
	public boolean matches(@NotNull SimpleFluidContainer container, Level level)
	{
		if(level.isClientSide())
			return false;
		
		return ingredient.test(container.getItem(0)) && container.testFluid(0, inputFluid);
	}
	
	@Override
	public @NotNull ItemStack assemble(@NotNull SimpleFluidContainer container, @NotNull RegistryAccess registryAccess)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}
	
	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess)
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
	
	protected abstract FluidStack defaultInputFluid();
	
	protected abstract FluidStack defaultOutputFluid();
	
	//============================================================================================
	//*************************************Naquadah Liquidizer************************************
	//============================================================================================
	
	public static class NaquadahLiquidizer extends LiquidizingRecipe
	{
		public static final RecipeType<NaquadahLiquidizer> TYPE = new RecipeType<>(){};
		
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
		
		protected FluidStack defaultInputFluid()
		{
			return new FluidStack(Fluids.LAVA, 100);
		}
		
		protected FluidStack defaultOutputFluid()
		{
			return new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), 100);
		}
	}
	
	public static class NaquadahLiquidizerSerializer implements RecipeSerializer<NaquadahLiquidizer>
	{
		public static final NaquadahLiquidizerSerializer INSTANCE = new NaquadahLiquidizerSerializer();
		
		@Override
		public @NotNull LiquidizingRecipe.NaquadahLiquidizer fromJson(@NotNull ResourceLocation recipeID, @NotNull JsonObject serializedRecipe)
		{
			return new NaquadahLiquidizer(recipeID, serializedRecipe);
		}
		
		@Override
		public @Nullable LiquidizingRecipe.NaquadahLiquidizer fromNetwork(@NotNull ResourceLocation recipeID, @NotNull FriendlyByteBuf friendlyByteBuf)
		{
			return new NaquadahLiquidizer(recipeID, friendlyByteBuf);
		}
		
		@Override
		public void toNetwork(@NotNull FriendlyByteBuf friendlyByteBuf, NaquadahLiquidizer recipe)
		{
			recipe.toNetwork(friendlyByteBuf);
		}
	}
	
	//============================================================================================
	//**********************************Heavy Naquadah Liquidizer*********************************
	//============================================================================================
	
	public static class HeavyNaquadahLiquidizer extends LiquidizingRecipe
	{
		public static final RecipeType<HeavyNaquadahLiquidizer> TYPE = new RecipeType<>(){};
		
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
		
		protected FluidStack defaultInputFluid()
		{
			return new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), 100);
		}
		
		protected FluidStack defaultOutputFluid()
		{
			return new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), 200);
		}
	}
	
	public static class HeavyNaquadahLiquidizerSerializer implements RecipeSerializer<HeavyNaquadahLiquidizer>
	{
		public static final HeavyNaquadahLiquidizerSerializer INSTANCE = new HeavyNaquadahLiquidizerSerializer();
		
		@Override
		public @NotNull LiquidizingRecipe.HeavyNaquadahLiquidizer fromJson(@NotNull ResourceLocation recipeID, @NotNull JsonObject serializedRecipe)
		{
			return new HeavyNaquadahLiquidizer(recipeID, serializedRecipe);
		}
		
		@Override
		public @Nullable LiquidizingRecipe.HeavyNaquadahLiquidizer fromNetwork(@NotNull ResourceLocation recipeID, @NotNull FriendlyByteBuf friendlyByteBuf)
		{
			return new HeavyNaquadahLiquidizer(recipeID, friendlyByteBuf);
		}
		
		@Override
		public void toNetwork(@NotNull FriendlyByteBuf friendlyByteBuf, HeavyNaquadahLiquidizer recipe)
		{
			recipe.toNetwork(friendlyByteBuf);
		}
	}
}
