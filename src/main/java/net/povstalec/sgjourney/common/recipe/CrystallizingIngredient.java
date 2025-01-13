package net.povstalec.sgjourney.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public class CrystallizingIngredient
{
	private final Ingredient ingredient;
	private final int amount;
	private final boolean deplete;
	
	public static final MapCodec<CrystallizingIngredient> CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
	{
		return recipeBuilder.group(
				Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) ->
				{
					return recipe.ingredient;
				}), Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter((recipe) ->
				{
					return recipe.amount;
				})).apply(recipeBuilder, CrystallizingIngredient::new);
	});
	
	public static final MapCodec<CrystallizingIngredient> DEPLETABLE_CODEC = RecordCodecBuilder.mapCodec((recipeBuilder) ->
	{
		return recipeBuilder.group(
				Ingredient.CODEC.fieldOf("ingredient").forGetter((recipe) ->
				{
					return recipe.ingredient;
				}), Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter((recipe) ->
				{
					return recipe.amount;
				}), Codec.BOOL.optionalFieldOf("deplete", true).forGetter((recipe) ->
				{
					return recipe.deplete;
				})).apply(recipeBuilder, CrystallizingIngredient::new);
	});
	
	public CrystallizingIngredient(Ingredient ingredient, int amount, boolean deplete)
	{
		this.ingredient = ingredient;
		this.amount = amount;
		this.deplete = deplete;
	}
	
	public CrystallizingIngredient(Ingredient ingredient, int amount)
	{
		this(ingredient, amount, true);
	}
	
	public Ingredient ingredient()
	{
		return ingredient;
	}
	
	public int amount()
	{
		return amount;
	}
	
	public boolean deplete()
	{
		return deplete;
	}
}
