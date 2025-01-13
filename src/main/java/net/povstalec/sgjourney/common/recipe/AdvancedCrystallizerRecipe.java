package net.povstalec.sgjourney.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

import javax.annotation.Nullable;

public class AdvancedCrystallizerRecipe implements Recipe<CrystalRecipeInput>
{
	protected final CrystallizingIngredient crystalBase;
	protected final CrystallizingIngredient primaryIngredient;
	protected final CrystallizingIngredient secondaryIngredient;
	private final ItemStack output;
	
	public AdvancedCrystallizerRecipe(ItemStack output, CrystallizingIngredient crystalBase, CrystallizingIngredient primaryIngredient, CrystallizingIngredient secondaryIngredient)
	{
		this.crystalBase = crystalBase;
		this.primaryIngredient = primaryIngredient;
		this.secondaryIngredient = secondaryIngredient;
		this.output = output;
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
	public boolean matches(CrystalRecipeInput craftingInput, Level level)
	{
		if(level.isClientSide())
			return false;
		
		return itemStackMatches(craftingInput, 0) && itemStackMatches(craftingInput, 1) && itemStackMatches(craftingInput, 2);
	}
	
	@Override
	public ItemStack assemble(CrystalRecipeInput craftingInput, HolderLookup.Provider provider)
	{
		return output.copy();
	}
	
	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return false;
	}
	
	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider)
	{
		return output.copy();
	}
	
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return AdvancedCrystallizerRecipe.Serializer.INSTANCE;
	}
	
	@Override
	public RecipeType<?> getType()
	{
		return AdvancedCrystallizerRecipe.Type.ADVANCED_CRYSTALLIZING;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(this.crystalBase.ingredient());
		nonnulllist.add(this.primaryIngredient.ingredient());
		nonnulllist.add(this.secondaryIngredient.ingredient());
		
		return nonnulllist;
	}
	
	public static class Type implements RecipeType<AdvancedCrystallizerRecipe>
	{
		public static final Type ADVANCED_CRYSTALLIZING = new Type();
	}
	
	public static class Serializer implements RecipeSerializer<AdvancedCrystallizerRecipe>
	{
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("advanced_crystallizing");
		
		public static final MapCodec<AdvancedCrystallizerRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
		{
			return recipeBuilder.group(
					ItemStack.STRICT_CODEC.fieldOf("output").forGetter((recipe) ->
					{
						return recipe.output;
					}), CrystallizingIngredient.CODEC.fieldOf("crystal_base").forGetter((recipe) ->
					{
						return recipe.crystalBase;
					}), CrystallizingIngredient.DEPLETABLE_CODEC.fieldOf("primary_ingredient").forGetter((recipe) ->
					{
						return recipe.primaryIngredient;
					}), CrystallizingIngredient.DEPLETABLE_CODEC.fieldOf("secondary_ingredient").forGetter((recipe) ->
					{
						return recipe.secondaryIngredient;
					})).apply(recipeBuilder, AdvancedCrystallizerRecipe::new);
		});
		
		public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedCrystallizerRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
		
		public Serializer() {}
		
		private static AdvancedCrystallizerRecipe fromNetwork(RegistryFriendlyByteBuf friendlyByteBuf)
		{
			Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount1 = friendlyByteBuf.readInt();
			Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount2 = friendlyByteBuf.readInt();
			boolean depletePrimary = friendlyByteBuf.readBoolean();
			Ingredient ingredient3 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount3 = friendlyByteBuf.readInt();
			boolean depleteSecondary = friendlyByteBuf.readBoolean();
			
			ItemStack output = ItemStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			
			return new AdvancedCrystallizerRecipe(output, new CrystallizingIngredient(ingredient1, amount1), new CrystallizingIngredient(ingredient2, amount2, depletePrimary), new CrystallizingIngredient(ingredient3, amount3, depleteSecondary));
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, AdvancedCrystallizerRecipe recipe)
		{
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.crystalBase.ingredient());
			friendlyByteBuf.writeInt(recipe.crystalBase.amount());
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.primaryIngredient.ingredient());
			friendlyByteBuf.writeInt(recipe.primaryIngredient.amount());
			friendlyByteBuf.writeBoolean(recipe.primaryIngredient.deplete());
			
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.secondaryIngredient.ingredient());
			friendlyByteBuf.writeInt(recipe.secondaryIngredient.amount());
			friendlyByteBuf.writeBoolean(recipe.secondaryIngredient.deplete());
			
			ItemStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.getResultItem(null));
		}
		
		@Override
		public MapCodec<AdvancedCrystallizerRecipe> codec()
		{
			return CODEC;
		}
		
		public StreamCodec<RegistryFriendlyByteBuf, AdvancedCrystallizerRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
