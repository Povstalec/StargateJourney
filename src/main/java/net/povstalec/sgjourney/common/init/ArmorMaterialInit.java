package net.povstalec.sgjourney.common.init;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.povstalec.sgjourney.StargateJourney;
import org.jetbrains.annotations.NotNull;

public enum ArmorMaterialInit implements ArmorMaterial
{
	NAQUADAH("naquadah", 42, new int[] {3, 6, 8, 3}, 5, ItemInit.NAQUADAH.get(), SoundInit.EQUIP_NAQUADAH_ARMOR.get(), 3.0F, 0.2F),
	TRINIUM("trinium", 25, new int[] {3, 6, 8, 3}, 5, ItemInit.TRINIUM_INGOT.get(), SoundInit.EQUIP_TRINIUM_ARMOR.get(), 0.0F, 0.0F),
	SYSTEM_LORD("system_lord", 42, new int[] {3, 6, 8, 3}, 5, ItemInit.NAQUADAH_IRON_ALLOY.get(), SoundInit.EQUIP_NAQUADAH_ARMOR.get(), 2.0F, 0.0F),
	JAFFA("jaffa", 42, new int[] {3, 6, 7, 3}, 5, ItemInit.NAQUADAH_IRON_ALLOY.get(), SoundInit.EQUIP_NAQUADAH_ARMOR.get(), 0.0F, 0.0F),
	PERSONAL_SHIELD("personal_shield", 0, new int[] {0, 0, 0, 0}, 0, ItemInit.NAQUADAH.get(), SoundInit.EQUIP_NAQUADAH_ARMOR.get(), 0.0F, 0.0F);
	
	private static final int[] MAX_DAMAGE_ARRAY = new int[] {13, 15, 16, 11};
	
	private final String name;
	private final SoundEvent equipSound;
	private final int durability;
	private final int enchantability;
	private final Item repairItem;
	private final int[] damageReductionAmounts;
	private final float toughness;
	private final float knockbackResistance;
	
	ArmorMaterialInit(String name, int durability, int[] damageReductionAmounts, int enchantability, Item repairItem, SoundEvent equipSound, float toughness, float knockbackResistance)
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
	public @NotNull String getName()
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
		return MAX_DAMAGE_ARRAY[slot.getIndex()] * this.durability;
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
	public @NotNull SoundEvent getEquipSound()
	{
		return equipSound;
	}

	@Override
	public @NotNull Ingredient getRepairIngredient()
	{
		return Ingredient.of(this.repairItem);
	}
}
