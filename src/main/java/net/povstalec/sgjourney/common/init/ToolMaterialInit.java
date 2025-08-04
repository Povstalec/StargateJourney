package net.povstalec.sgjourney.common.init;

import com.google.common.base.Suppliers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public enum ToolMaterialInit implements Tier
{
	NAQUADAH(TagInit.Blocks.INCORRECT_FOR_NAQUADAH_TOOL, 3200, 9.0F, 4.0F, 12, () -> {return Ingredient.of(new ItemLike[]{ItemInit.NAQUADAH.get()});});

	private final TagKey<Block> incorrectBlocksForDrops;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;
	
	private ToolMaterialInit(TagKey<Block> incorrectBlockForDrops, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient)
	{
		this.incorrectBlocksForDrops = incorrectBlockForDrops;
		this.uses = uses;
		this.speed = speed;
		this.damage = damage;
		this.enchantmentValue = enchantmentValue;
		Objects.requireNonNull(repairIngredient);
		this.repairIngredient = Suppliers.memoize(repairIngredient::get);
	}

	@Override
	public int getUses()
	{
		return this.uses;
	}

	@Override
	public float getSpeed()
	{
		return this.speed;
	}

	@Override
	public float getAttackDamageBonus()
	{
		return this.damage;
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops()
	{
		return this.incorrectBlocksForDrops;
	}

	@Override
	public int getEnchantmentValue()
	{
		return this.enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient()
	{
		return (Ingredient)this.repairIngredient.get();
	}
}
