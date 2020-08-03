package init;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import woldericz_junior.stargatejourney.StargateJourney;

public enum CustomArmorMaterials implements IArmorMaterial
{
	naquadah("naquadah", 25, new int[] {2, 5, 7, 2}, 12, StargateItems.naquadah_ingot, "item.armor.equip_iron", 1.0f),
	jaffa("jaffa", 25, new int[] {2, 5, 7, 2}, 12, StargateItems.naquadah_ingot, "item.armor.equip_iron", 1.0f);
	
	private static final int[] max_damage_array = new int[]{13, 15, 16, 11};
	private String name, equipSound;
	private int durability, enchantability;
	private Item repairItem;
	private int[] damageReductionAmounts;
	private float toughness;
	
	private CustomArmorMaterials(String name, int durability, int[] damageReductionAmounts, int enchantability, Item repairItem, String equipSound, float toughness)
	{
		this.name = name;
		this.equipSound = equipSound;
		this.durability = durability;
		this.enchantability = enchantability;
		this.repairItem = repairItem;
		this.damageReductionAmounts = damageReductionAmounts;
		this.toughness = toughness;
	}

	@Override
	public int getDurability(EquipmentSlotType slot) {
		// TODO Auto-generated method stub
		return max_damage_array[slot.getIndex()] * this.durability;
	}

	@Override
	public int getDamageReductionAmount(EquipmentSlotType slot) {
		// TODO Auto-generated method stub
		return this.damageReductionAmounts[slot.getIndex()];
	}

	@Override
	public int getEnchantability() {
		// TODO Auto-generated method stub
		return this.enchantability;
	}

	@Override
	public SoundEvent getSoundEvent() {
		// TODO Auto-generated method stub
		return new SoundEvent(new ResourceLocation(equipSound));
	}

	@Override
	public Ingredient getRepairMaterial() {
		// TODO Auto-generated method stub
		return Ingredient.fromItems(this.repairItem);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return StargateJourney.MODID + ":" + this.name;
	}

	@Override
	public float getToughness() {
		// TODO Auto-generated method stub
		return this.toughness;
	}
}
