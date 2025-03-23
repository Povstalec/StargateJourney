package net.povstalec.sgjourney.common.init;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.povstalec.sgjourney.StargateJourney;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ArmorMaterialInit
{
	public static final Holder<ArmorMaterial> NAQUADAH = register("naquadah", Util.make(new EnumMap<>(ArmorItem.Type.class), map ->
	{
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	}), 10, SoundInit.EQUIP_NAQUADAH_ARMOR, 3.0F, 0.2F, () -> Ingredient.of(ItemInit.NAQUADAH.get()));

	public static final Holder<ArmorMaterial> JAFFA = register("jaffa", Util.make(new EnumMap<>(ArmorItem.Type.class), map ->
	{
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	}), 10, SoundInit.EQUIP_NAQUADAH_ARMOR, 2.0F, 0.0F, () -> Ingredient.of(ItemInit.NAQUADAH_ALLOY.get()));
	
	public static final Holder<ArmorMaterial> SYSTEM_LORD = register("system_lord", Util.make(new EnumMap<>(ArmorItem.Type.class), map ->
	{
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	}), 10, SoundInit.EQUIP_NAQUADAH_ARMOR, 2.0F, 0.0F, () -> Ingredient.of(ItemInit.NAQUADAH_ALLOY.get()));

	public static final Holder<ArmorMaterial> PERSONAL_SHIELD = register("personal_shield", Util.make(new EnumMap<>(ArmorItem.Type.class), map ->
	{
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 11);
	}), 10, SoundInit.EQUIP_NAQUADAH_ARMOR, 2.0F, 0.0F, () -> Ingredient.of());



	private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue,
			Holder<SoundEvent> equipSound, float toughness, float knockbackResistance,
			Supplier<Ingredient> repairIngredient)
	{
		List<ArmorMaterial.Layer> list = List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(StargateJourney.MODID, name)));
		return register(name, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairIngredient, list);
	}

	private static Holder<ArmorMaterial> register(String name, EnumMap<Type, Integer> defense, int enchantmentValue,
			Holder<SoundEvent> equipSound, float toughness, float knockbackResistance,
			Supplier<Ingredient> repairIngridient, List<ArmorMaterial.Layer> layers)
	{
		EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

		for (ArmorItem.Type armoritem$type : ArmorItem.Type.values())
		{
			enummap.put(armoritem$type, defense.get(armoritem$type));
		}

		return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.fromNamespaceAndPath(StargateJourney.MODID, name),
				new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngridient, layers, toughness, knockbackResistance));
	}
}
