package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public enum ToolMaterialInit implements Tier
{
	NAQUADAH(4, 3200, 9.0F, 4.0F, 12, ItemInit.NAQUADAH.get()),
	TRINIUM(4, 512, 12.0F, 3.0F, 10, ItemInit.TRINIUM_INGOT.get());
	
	private final float attackDamage;
	private final float efficiency;
	private final int durability;
	private final int harvestLevel;
	private final int enchantability;
	private final Item repairMaterial;
	
	ToolMaterialInit(int harvestLevel, int durability, float efficiency, float attackDamage, int enchantability, Item repairMaterial)
	{
		this.harvestLevel = harvestLevel;
		this.durability = durability;
		this.efficiency = efficiency;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairMaterial = repairMaterial;
	}

	@Override
	public int getUses()
	{
		return this.durability;
	}

	@Override
	public float getSpeed()
	{
		return this.efficiency;
	}

	@Override
	public float getAttackDamageBonus()
	{
		return this.attackDamage;
	}

	@Override
	public int getLevel()
	{
		return this.harvestLevel;
	}

	@Override
	public int getEnchantmentValue()
	{
		return this.enchantability;
	}

	@Override
	public @NotNull Ingredient getRepairIngredient()
	{
		return Ingredient.of(repairMaterial);
	}
}
