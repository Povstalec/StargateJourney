package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum ToolMaterialInit implements Tier
{
	naquadah(4, 3200, 9.0F, 4.0F, 12, ItemInit.NAQUADAH.get());
	
	private float attackDamage, efficiency;
	private int durability, harvestLevel, enchantability;
	private Item repairMaterial;
	
	private ToolMaterialInit(int harvestLevel, int durability, float efficiency, float attackDamage, int enchantability, Item repairMaterial) 
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
	public Ingredient getRepairIngredient()
	{
		return Ingredient.of(repairMaterial);
	}
}
