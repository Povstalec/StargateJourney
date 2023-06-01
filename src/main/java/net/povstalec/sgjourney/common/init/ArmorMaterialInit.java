package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.povstalec.sgjourney.StargateJourney;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;

public enum ArmorMaterialInit implements ArmorMaterial
{
	naquadah("naquadah", 42, new int[] {3, 6, 8, 3}, 5, ItemInit.NAQUADAH.get(), SoundInit.EQUIP_NAQUADAH_ARMOR.get(), 3.0F, 0.2F),
	jaffa("jaffa", 42, new int[] {3, 6, 8, 3}, 5, ItemInit.NAQUADAH_ALLOY.get(), SoundInit.EQUIP_NAQUADAH_ARMOR.get(), 2.0F, 0.0F),
	personal_shield("personal_shield", 0, new int[] {0, 0, 0, 0}, 0, ItemInit.NAQUADAH.get(), SoundInit.EQUIP_NAQUADAH_ARMOR.get(), 0.0F, 0.0F);
	
	private static final int[] max_damage_array = new int[]{13, 15, 16, 11};
	private String name;
	private SoundEvent equipSound;
	private int durability, enchantability;
	private Item repairItem;
	private int[] damageReductionAmounts;
	private float toughness;
	private float knockbackResistance;
	
	private ArmorMaterialInit(String name, int durability, int[] damageReductionAmounts, int enchantability, Item repairItem, SoundEvent equipSound, float toughness, float knockbackResistance)
	{
		this.name = name;
		this.equipSound = equipSound;
		this.durability = durability;
		this.enchantability = enchantability;
		this.repairItem = repairItem;
		this.damageReductionAmounts = damageReductionAmounts;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
	}

	@Override
	public String getName() 
	{
		return StargateJourney.MODID + ":" + this.name;
	}

	@Override
	public float getToughness() 
	{
		return this.toughness;
	}

	@Override
	public float getKnockbackResistance() 
	{
		return this.knockbackResistance;
	}

	@Override
	public int getDurabilityForSlot(EquipmentSlot slot) 
	{
		return max_damage_array[slot.getIndex()] * this.durability;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot slot) 
	{
		return this.damageReductionAmounts[slot.getIndex()];
	}

	@Override
	public int getEnchantmentValue() 
	{
		return this.enchantability;
	}

	@Override
	public SoundEvent getEquipSound() 
	{
		return equipSound;
	}

	@Override
	public Ingredient getRepairIngredient() 
	{
		return Ingredient.of(this.repairItem);
	}
}
