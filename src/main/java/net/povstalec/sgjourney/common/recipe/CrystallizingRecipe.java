package net.povstalec.sgjourney.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.FluidInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CrystallizingRecipe extends ProgressRecipe<CrystallizingRecipeInput>
{
	protected final CrystallizingIngredient crystalBase;
	protected final CrystallizingIngredient primaryIngredient;
	protected final CrystallizingIngredient secondaryIngredient;
	protected final ItemStack output;
	protected final FluidStack inputFluid;
	
	public CrystallizingRecipe(int progress, ItemStack output, CrystallizingIngredient crystalBase, CrystallizingIngredient primaryIngredient, CrystallizingIngredient secondaryIngredient, FluidStack inputFluid)
	{
		super(progress);
		
		this.crystalBase = crystalBase;
		this.primaryIngredient = primaryIngredient;
		this.secondaryIngredient = secondaryIngredient;
		this.output = output;
		this.inputFluid = inputFluid;
	}
	
	public int getAmountInSlot(int slot)
	{
		return switch(slot)
		{
			case 0 -> this.crystalBase.amount();
			case 1 -> this.primaryIngredient.amount();
			case 2 -> this.secondaryIngredient.amount();
			default -> 0;
		};
	}
	
	public boolean depletePrimary()
	{
		return primaryIngredient.deplete();
	}
	
	public boolean depleteSecondary()
	{
		return secondaryIngredient.deplete();
	}
	
	public FluidStack getInputFluid()
	{
		return this.inputFluid;
	}
	
	@Nullable
	private Ingredient getIngredient(int slot)
	{
		return switch(slot)
		{
			case 0 -> this.crystalBase.ingredient();
			case 1 -> this.primaryIngredient.ingredient();
			case 2 -> this.secondaryIngredient.ingredient();
			default -> null;
		};
	}
	
	public boolean itemStackMatches(RecipeInput craftingInput, int slot)
	{
		ItemStack stack = craftingInput.getItem(slot);
		
		Ingredient ingredient = getIngredient(slot);
		if(ingredient != null)
			return ingredient.test(stack) && getAmountInSlot(slot) <= stack.getCount();
		
		return false;
	}
	
	@Override
	public boolean matches(@NotNull CrystallizingRecipeInput craftingInput, Level level)
	{
		if(level.isClientSide())
			return false;
		
		return itemStackMatches(craftingInput, 0) && itemStackMatches(craftingInput, 1) && itemStackMatches(craftingInput, 2);
	}
	
	@Override
	public @NotNull ItemStack assemble(@NotNull CrystallizingRecipeInput recipeInput, @NotNull HolderLookup.Provider provider)
	{
		return output.copy();
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}
	
	@Override
	public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider)
	{
		return output.copy();
	}
	
	@Override
	public @NotNull NonNullList<Ingredient> getIngredients()
	{
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(this.crystalBase.ingredient());
		nonnulllist.add(this.primaryIngredient.ingredient());
		nonnulllist.add(this.secondaryIngredient.ingredient());
		
		return nonnulllist;
	}
	
	//============================================================================================
	//****************************************Crystallizer****************************************
	//============================================================================================
	
	public static class Crystallizer extends CrystallizingRecipe
	{
		public static final RecipeType<Crystallizer> TYPE = new RecipeType<>(){};
		
		protected Crystallizer(int progress, ItemStack output, CrystallizingIngredient crystalBase, CrystallizingIngredient primaryIngredient, CrystallizingIngredient secondaryIngredient, FluidStack inputFluid)
		{
			super(progress, output, crystalBase, primaryIngredient, secondaryIngredient, inputFluid);
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
		public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("crystallizing");
		
		public static final MapCodec<Crystallizer> CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
				recipeBuilder.group(
								Codec.INT.optionalFieldOf("progress", 100).forGetter((recipe) -> recipe.progressTime),
								ItemStack.STRICT_CODEC.fieldOf("output").forGetter((recipe) -> recipe.output),
								CrystallizingIngredient.CODEC.fieldOf("crystal_base").forGetter((recipe) -> recipe.crystalBase),
								CrystallizingIngredient.DEPLETABLE_CODEC.fieldOf("primary_ingredient").forGetter((recipe) -> recipe.primaryIngredient),
								CrystallizingIngredient.DEPLETABLE_CODEC.fieldOf("secondary_ingredient").forGetter((recipe) -> recipe.secondaryIngredient),
								FluidStack.CODEC.optionalFieldOf("input_fluid", new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), 100)).forGetter((recipe) -> recipe.inputFluid))
						.apply(recipeBuilder, Crystallizer::new));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, Crystallizer> STREAM_CODEC = StreamCodec.of(CrystallizerSerializer::toNetwork, CrystallizerSerializer::fromNetwork);
		
		public CrystallizerSerializer() {}
		
		private static Crystallizer fromNetwork(RegistryFriendlyByteBuf friendlyByteBuf)
		{
			int progress = friendlyByteBuf.readInt();
			
			Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount1 = friendlyByteBuf.readInt();
			Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount2 = friendlyByteBuf.readInt();
			boolean depletePrimary = friendlyByteBuf.readBoolean();
			Ingredient ingredient3 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount3 = friendlyByteBuf.readInt();
			boolean depleteSecondary = friendlyByteBuf.readBoolean();
			
			FluidStack inputFluid = FluidStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			ItemStack output = ItemStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			
			return new Crystallizer(progress, output, new CrystallizingIngredient(ingredient1, amount1), new CrystallizingIngredient(ingredient2, amount2, depletePrimary), new CrystallizingIngredient(ingredient3, amount3, depleteSecondary), inputFluid);
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, Crystallizer recipe)
		{
			friendlyByteBuf.writeInt(recipe.progressTime);
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.crystalBase.ingredient());
			friendlyByteBuf.writeInt(recipe.crystalBase.amount());
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.primaryIngredient.ingredient());
			friendlyByteBuf.writeInt(recipe.primaryIngredient.amount());
			friendlyByteBuf.writeBoolean(recipe.primaryIngredient.deplete());
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.secondaryIngredient.ingredient());
			friendlyByteBuf.writeInt(recipe.secondaryIngredient.amount());
			friendlyByteBuf.writeBoolean(recipe.secondaryIngredient.deplete());
			
			FluidStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.inputFluid);
			
			ItemStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.output);
			
		}
		
		@Override
		public @NotNull MapCodec<Crystallizer> codec()
		{
			return CODEC;
		}
		
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, Crystallizer> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
	
	//============================================================================================
	//***********************************Advanced Crystallizer************************************
	//============================================================================================
	
	public static class AdvancedCrystallizer extends CrystallizingRecipe
	{
		public static final RecipeType<AdvancedCrystallizer> TYPE = new RecipeType<>(){};
		
		protected AdvancedCrystallizer(int progress, ItemStack output, CrystallizingIngredient crystalBase, CrystallizingIngredient primaryIngredient, CrystallizingIngredient secondaryIngredient, FluidStack inputFluid)
		{
			super(progress, output, crystalBase, primaryIngredient, secondaryIngredient, inputFluid);
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
		public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("advanced_crystallizing");
		
		public static final MapCodec<AdvancedCrystallizer> CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
				recipeBuilder.group(
								Codec.INT.optionalFieldOf("progress", 100).forGetter((recipe) -> recipe.progressTime),
								ItemStack.STRICT_CODEC.fieldOf("output").forGetter((recipe) -> recipe.output),
								CrystallizingIngredient.CODEC.fieldOf("crystal_base").forGetter((recipe) -> recipe.crystalBase),
								CrystallizingIngredient.DEPLETABLE_CODEC.fieldOf("primary_ingredient").forGetter((recipe) -> recipe.primaryIngredient),
								CrystallizingIngredient.DEPLETABLE_CODEC.fieldOf("secondary_ingredient").forGetter((recipe) -> recipe.secondaryIngredient),
								FluidStack.CODEC.optionalFieldOf("input_fluid", new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), 100)).forGetter((recipe) -> recipe.inputFluid))
						.apply(recipeBuilder, AdvancedCrystallizer::new));
		
		public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedCrystallizer> STREAM_CODEC = StreamCodec.of(AdvancedCrystallizerSerializer::toNetwork, AdvancedCrystallizerSerializer::fromNetwork);
		
		public AdvancedCrystallizerSerializer() {}
		
		private static AdvancedCrystallizer fromNetwork(RegistryFriendlyByteBuf friendlyByteBuf)
		{
			int progress = friendlyByteBuf.readInt();
			
			Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount1 = friendlyByteBuf.readInt();
			Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount2 = friendlyByteBuf.readInt();
			boolean depletePrimary = friendlyByteBuf.readBoolean();
			Ingredient ingredient3 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount3 = friendlyByteBuf.readInt();
			boolean depleteSecondary = friendlyByteBuf.readBoolean();
			
			FluidStack inputFluid = FluidStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			ItemStack output = ItemStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			
			return new AdvancedCrystallizer(progress, output, new CrystallizingIngredient(ingredient1, amount1), new CrystallizingIngredient(ingredient2, amount2, depletePrimary), new CrystallizingIngredient(ingredient3, amount3, depleteSecondary), inputFluid);
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, AdvancedCrystallizer recipe)
		{
			friendlyByteBuf.writeInt(recipe.progressTime);
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.crystalBase.ingredient());
			friendlyByteBuf.writeInt(recipe.crystalBase.amount());
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.primaryIngredient.ingredient());
			friendlyByteBuf.writeInt(recipe.primaryIngredient.amount());
			friendlyByteBuf.writeBoolean(recipe.primaryIngredient.deplete());
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.secondaryIngredient.ingredient());
			friendlyByteBuf.writeInt(recipe.secondaryIngredient.amount());
			friendlyByteBuf.writeBoolean(recipe.secondaryIngredient.deplete());
			
			FluidStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.inputFluid);
			
			ItemStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.output);
			
		}
		
		@Override
		public @NotNull MapCodec<AdvancedCrystallizer> codec()
		{
			return CODEC;
		}
		
		public @NotNull StreamCodec<RegistryFriendlyByteBuf, AdvancedCrystallizer> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
