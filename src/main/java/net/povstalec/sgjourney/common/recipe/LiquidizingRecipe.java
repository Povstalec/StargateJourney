package net.povstalec.sgjourney.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.FluidInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LiquidizingRecipe extends ProgressRecipe<LiquidizingRecipeInput>
{
	protected final Ingredient ingredient;
	protected final FluidStack inputFluid;
	protected final FluidStack outputFluid;
	
	protected LiquidizingRecipe(int progress, Ingredient ingredient, FluidStack inputFluid, FluidStack outputFluid)
	{
		super(progress);
		
		this.ingredient = ingredient;
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
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
	public boolean matches(@NotNull LiquidizingRecipeInput recipeInput, Level level)
	{
		if(level.isClientSide())
			return false;
		
		return ingredient.test(recipeInput.getItem(0)) && recipeInput.testFluid(0, inputFluid);
	}
	
	@Override
	public @NotNull ItemStack assemble(@NotNull LiquidizingRecipeInput recipeInput, @NotNull HolderLookup.Provider provider)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}
	
	@Override
	public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider)
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
		public static final RecipeType<NaquadahLiquidizer> TYPE = new RecipeType<>(){};
		
		protected NaquadahLiquidizer(int progress, Ingredient ingredient, FluidStack inputFluid, FluidStack outputFluid)
		{
			super(progress, ingredient, inputFluid, outputFluid);
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
		public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("naquadah_liquidizing");
		
		public static final MapCodec<NaquadahLiquidizer> CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
				recipeBuilder.group(
								Codec.INT.optionalFieldOf("progress", 100).forGetter((recipe) -> recipe.progressTime),
								Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> recipe.ingredient),
								FluidStack.CODEC.optionalFieldOf("input_fluid", new FluidStack(Fluids.LAVA, 100)).forGetter((recipe) -> recipe.inputFluid),
								FluidStack.CODEC.optionalFieldOf("output_fluid", new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), 100)).forGetter((recipe) -> recipe.outputFluid))
						.apply(recipeBuilder, NaquadahLiquidizer::new));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, NaquadahLiquidizer> STREAM_CODEC = StreamCodec.of(NaquadahLiquidizerSerializer::toNetwork, NaquadahLiquidizerSerializer::fromNetwork);
		
		public NaquadahLiquidizerSerializer() {}
		
		private static NaquadahLiquidizer fromNetwork(RegistryFriendlyByteBuf friendlyByteBuf)
		{
			int progress = friendlyByteBuf.readInt();
			
			Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			
			FluidStack inputFluid = FluidStack.STREAM_CODEC.decode(friendlyByteBuf);
			FluidStack outputFluid = FluidStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			
			return new NaquadahLiquidizer(progress, ingredient, inputFluid, outputFluid);
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, NaquadahLiquidizer recipe)
		{
			friendlyByteBuf.writeInt(recipe.progressTime);
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.ingredient);
			
			FluidStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.inputFluid);
			FluidStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.outputFluid);
			
		}
		
		@Override
		public @NotNull MapCodec<NaquadahLiquidizer> codec()
		{
			return CODEC;
		}
		
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, NaquadahLiquidizer> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
	
	//============================================================================================
	//**********************************Heavy Naquadah Liquidizer*********************************
	//============================================================================================
	
	public static class HeavyNaquadahLiquidizer extends LiquidizingRecipe
	{
		public static final RecipeType<HeavyNaquadahLiquidizer> TYPE = new RecipeType<>(){};
		
		protected HeavyNaquadahLiquidizer(int progress, Ingredient ingredient, FluidStack inputFluid, FluidStack outputFluid)
		{
			super(progress, ingredient, inputFluid, outputFluid);
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
		public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("heavy_naquadah_liquidizing");
		
		public static final MapCodec<HeavyNaquadahLiquidizer> CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
				recipeBuilder.group(
								Codec.INT.optionalFieldOf("progress", 100).forGetter((recipe) -> recipe.progressTime),
								Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) -> recipe.ingredient),
								FluidStack.CODEC.optionalFieldOf("input_fluid", new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), 100)).forGetter((recipe) -> recipe.inputFluid),
								FluidStack.CODEC.optionalFieldOf("output_fluid", new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), 200)).forGetter((recipe) -> recipe.outputFluid))
						.apply(recipeBuilder, HeavyNaquadahLiquidizer::new));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, HeavyNaquadahLiquidizer> STREAM_CODEC = StreamCodec.of(HeavyNaquadahLiquidizerSerializer::toNetwork, HeavyNaquadahLiquidizerSerializer::fromNetwork);
		
		public HeavyNaquadahLiquidizerSerializer() {}
		
		private static HeavyNaquadahLiquidizer fromNetwork(RegistryFriendlyByteBuf friendlyByteBuf)
		{
			int progress = friendlyByteBuf.readInt();
			
			Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			
			FluidStack inputFluid = FluidStack.STREAM_CODEC.decode(friendlyByteBuf);
			FluidStack outputFluid = FluidStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			
			return new HeavyNaquadahLiquidizer(progress, ingredient, inputFluid, outputFluid);
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, HeavyNaquadahLiquidizer recipe)
		{
			friendlyByteBuf.writeInt(recipe.progressTime);
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.ingredient);
			
			FluidStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.inputFluid);
			FluidStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.outputFluid);
			
		}
		
		@Override
		public @NotNull MapCodec<HeavyNaquadahLiquidizer> codec()
		{
			return CODEC;
		}
		
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, HeavyNaquadahLiquidizer> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
