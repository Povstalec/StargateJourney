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

public class CrystallizerRecipe implements Recipe<CrystalRecipeInput>
{
	protected final Pair<Ingredient, Integer> ingredient1;
	protected final Pair<Ingredient, Integer> ingredient2;
	protected final Pair<Ingredient, Integer> ingredient3;
	private final ItemStack output;
	private final boolean depletePrimary;
	private final boolean depleteSecondary;
	
	public CrystallizerRecipe(ItemStack output, Pair<Ingredient, Integer> ingredient1, Pair<Ingredient, Integer> ingredient2, Pair<Ingredient, Integer> ingredient3, boolean depletePrimary, boolean depleteSecondary)
	{
		this.ingredient1 = ingredient1;
		this.ingredient2 = ingredient2;
		this.ingredient3 = ingredient3;
		this.output = output;
		this.depletePrimary = depletePrimary;
		this.depleteSecondary = depleteSecondary;
	}
	
	public int getAmountInSlot(int slot)
	{
		return switch(slot)
		{
			case 0 -> this.ingredient1.getSecond();
			case 1 -> this.ingredient2.getSecond();
			case 2 -> this.ingredient3.getSecond();
			default -> 0;
		};
	}
	
	public boolean depletePrimary()
	{
		return this.depletePrimary;
	}
	
	public boolean depleteSecondary()
	{
		return this.depleteSecondary;
	}
	
	@Nullable
	private Ingredient getIngredient(int slot)
	{
		return switch(slot)
		{
			case 0 -> this.ingredient1.getFirst();
			case 1 -> this.ingredient2.getFirst();
			case 2 -> this.ingredient3.getFirst();
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
		return Serializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType()
	{
		return Type.CRYSTALLIZING;
	}

	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(this.ingredient1.getFirst());
		nonnulllist.add(this.ingredient2.getFirst());
		nonnulllist.add(this.ingredient3.getFirst());
		
		return nonnulllist;
	}
	
	public static class Type implements RecipeType<CrystallizerRecipe>
{
	public static final Type CRYSTALLIZING = new Type();
}
	
	public static class Serializer implements RecipeSerializer<CrystallizerRecipe>
	{
		public static final Serializer INSTANCE = new Serializer();
		public static final ResourceLocation ID = StargateJourney.sgjourneyLocation("crystallizing");
		
		public static final MapCodec<CrystallizerRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
		{
			return recipeBuilder.group(
				ItemStack.STRICT_CODEC.fieldOf("output").forGetter((recipe) ->
			{
						return recipe.output;
			}), Codec.pair(Ingredient.CODEC, Codec.INT).fieldOf("crystal_base").forGetter((recipe) ->
			{
				return recipe.ingredient1;
			}), Codec.pair(Ingredient.CODEC, Codec.INT).fieldOf("primary_ingredient").forGetter((recipe) ->
			{
				return recipe.ingredient2;
			}), Codec.pair(Ingredient.CODEC, Codec.INT).fieldOf("secondary_ingredient").forGetter((recipe) ->
			{
				return recipe.ingredient3;
			}), Codec.BOOL.optionalFieldOf("deplete_primary", true).forGetter((recipe) ->
			{
				return recipe.depletePrimary;
			}), Codec.BOOL.optionalFieldOf("deplete_secondary", true).forGetter((recipe) ->
			{
				return recipe.depleteSecondary;
			})).apply(recipeBuilder, CrystallizerRecipe::new);
		});
		
		public static final StreamCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
		
		public Serializer() {}
		
		private static CrystallizerRecipe fromNetwork(RegistryFriendlyByteBuf friendlyByteBuf)
		{
			Ingredient ingredient1 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount1 = friendlyByteBuf.readInt();
			Ingredient ingredient2 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount2 = friendlyByteBuf.readInt();
			Ingredient ingredient3 = Ingredient.CONTENTS_STREAM_CODEC.decode(friendlyByteBuf);
			int amount3 = friendlyByteBuf.readInt();
			
			ItemStack output = ItemStack.STREAM_CODEC.decode(friendlyByteBuf);
			
			boolean depletePrimary = friendlyByteBuf.readBoolean();
			boolean depleteSecondary = friendlyByteBuf.readBoolean();
			
			return new CrystallizerRecipe(output, Pair.of(ingredient1, amount1), Pair.of(ingredient2, amount2), Pair.of(ingredient3, amount3), depletePrimary, depleteSecondary);
		}
		
		private static void toNetwork(RegistryFriendlyByteBuf friendlyByteBuf, CrystallizerRecipe recipe)
		{
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.ingredient1.getFirst());
			friendlyByteBuf.writeInt(recipe.ingredient1.getSecond());
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.ingredient2.getFirst());
			friendlyByteBuf.writeInt(recipe.ingredient2.getSecond());
			Ingredient.CONTENTS_STREAM_CODEC.encode(friendlyByteBuf, recipe.ingredient3.getFirst());
			friendlyByteBuf.writeInt(recipe.ingredient3.getSecond());
			
			ItemStack.STREAM_CODEC.encode(friendlyByteBuf, recipe.getResultItem(null));
			
			friendlyByteBuf.writeBoolean(recipe.depletePrimary);
			friendlyByteBuf.writeBoolean(recipe.depleteSecondary);
		}
		
		/*public static Pair<Ingredient, Integer> getIngredient(Map<String, JsonElement> pair)
		{
			JsonElement item = pair.get("item");
			JsonObject json = new JsonObject();
			json.add("item", item);

			Ingredient ingredient = Ingredient.fromJson(json);
			int amount = pair.get("amount").getAsInt();
			
			return new Pair<Ingredient, Integer>(ingredient, amount);
		}*/
		
		@Override
		public MapCodec<CrystallizerRecipe> codec()
		{
			return CODEC;
		}
		
		public StreamCodec<RegistryFriendlyByteBuf, CrystallizerRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
