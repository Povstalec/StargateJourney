package net.povstalec.sgjourney.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum ToolMaterialInit implements Tier
{
	naquadah(2.0f, 7.0f, 2031, 3, 12, ItemInit.NAQUADAH_ALLOY.get());
	
	private float attackDamage, efficency;
	private int durability, harvestLevel, enchantability;
	private Item repairMaterial;
	
	private ToolMaterialInit(float attackDamage, float efficency, int durability, int harvestLevel, int enchantability, Item repairMaterial) 
	{
		this.attackDamage = attackDamage;
		this.efficency = efficency;
		this.durability = durability;
		this.harvestLevel = harvestLevel;
		this.enchantability = enchantability;
		this.repairMaterial = repairMaterial;
	}

	@Override
	public int getUses() {
		return this.durability;
	}

	@Override
	public float getSpeed() {
		return this.efficency;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.attackDamage;
	}

	@Override
	public int getLevel() {
		return this.harvestLevel;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.of(repairMaterial);
	}
}
