package net.povstalec.sgjourney.common.init;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.items.KaraKeshItem;
import net.povstalec.sgjourney.common.items.NaquadahBottleItem;
import net.povstalec.sgjourney.common.items.PDAItem;
import net.povstalec.sgjourney.common.items.RingRemoteItem;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;
import net.povstalec.sgjourney.common.items.SyringeItem;
import net.povstalec.sgjourney.common.items.ZeroPointModule;
import net.povstalec.sgjourney.common.items.armor.FalconArmorItem;
import net.povstalec.sgjourney.common.items.armor.JackalArmorItem;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MaterializationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.items.tools.SGJourneyAxeItem;
import net.povstalec.sgjourney.common.items.tools.SGJourneyHoeItem;
import net.povstalec.sgjourney.common.items.tools.SGJourneyPickaxeItem;
import net.povstalec.sgjourney.common.items.tools.SGJourneyShovelItem;
import net.povstalec.sgjourney.common.items.tools.SGJourneySwordItem;

public class ItemInit
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StargateJourney.MODID);
	
	// Materials
	public static final RegistryObject<Item> RAW_NAQUADAH = ITEMS.register("raw_naquadah", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> NAQUADAH_ALLOY = ITEMS.register("naquadah_alloy", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> PURE_NAQUADAH = ITEMS.register("pure_naquadah", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16).fireResistant()));
	public static final RegistryObject<Item> NAQUADAH = ITEMS.register("naquadah", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16).fireResistant()));
	public static final RegistryObject<Item> LIQUID_NAQUADAH_BUCKET = ITEMS.register("liquid_naquadah_bucket", 
			() -> new BucketItem(FluidInit.LIQUID_NAQUADAH_SOURCE, new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1).craftRemainder(Items.BUCKET)));
	public static final RegistryObject<NaquadahBottleItem> LIQUID_NAQUADAH_BOTTLE = ITEMS.register("liquid_naquadah_bottle", 
			() -> new NaquadahBottleItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	
	// Crafting Items
	public static final RegistryObject<Item> NAQUADAH_ROD = ITEMS.register("naquadah_rod", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> REACTION_CHAMBER = ITEMS.register("reaction_chamber", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> PLASMA_CONVERTER = ITEMS.register("plasma_converter", 
			() -> new Item(new Item.Properties().fireResistant()));
	
	// Food
	
	// Useful Items
	public static final RegistryObject<Item> RING_REMOTE = ITEMS.register("ring_remote", 
			() -> new RingRemoteItem(new RingRemoteItem.Properties().stacksTo(1)));
	public static final RegistryObject<Item> KARA_KESH = ITEMS.register("kara_kesh", 
			() -> new KaraKeshItem(new KaraKeshItem.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
	
	public static final RegistryObject<Item> MATOK = ITEMS.register("matok", 
			() -> new StaffWeaponItem(new StaffWeaponItem.Properties().stacksTo(1)));
	
	public static final RegistryObject<Item> PDA = ITEMS.register("pda", 
			() -> new PDAItem(new PDAItem.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant()));
	
	public static final RegistryObject<Item> ZPM = ITEMS.register("zero_point_module", 
			() -> new ZeroPointModule(new ZeroPointModule.Properties().stacksTo(1).rarity(Rarity.EPIC)));
	
	public static final RegistryObject<Item> SYRINGE = ITEMS.register("syringe", 
			() -> new SyringeItem(new SyringeItem.Properties().stacksTo(1)));
	
	// Crystals
	public static final RegistryObject<ControlCrystalItem> CONTROL_CRYSTAL = ITEMS.register("control_crystal", 
			() -> new ControlCrystalItem(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<ControlCrystalItem> LARGE_CONTROL_CRYSTAL = ITEMS.register("large_control_crystal", 
			() -> new ControlCrystalItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<MemoryCrystalItem> MEMORY_CRYSTAL = ITEMS.register("memory_crystal", 
			() -> new MemoryCrystalItem(new Item.Properties().stacksTo(1), 4));
	public static final RegistryObject<MaterializationCrystalItem> MATERIALIZATION_CRYSTAL = ITEMS.register("materialization_crystal", 
			() -> new MaterializationCrystalItem(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<EnergyCrystalItem> ENERGY_CRYSTAL = ITEMS.register("energy_crystal", 
			() -> new EnergyCrystalItem(new Item.Properties().stacksTo(1), 50000, 2500, 1500));
	public static final RegistryObject<CommunicationCrystalItem> COMMUNICATION_CRYSTAL = ITEMS.register("communication_crystal", 
			() -> new CommunicationCrystalItem(new Item.Properties().stacksTo(1), 16));

	public static final RegistryObject<ControlCrystalItem> ADVANCED_CONTROL_CRYSTAL = ITEMS.register("advanced_control_crystal", 
			() -> new ControlCrystalItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<MemoryCrystalItem> ADVANCED_MEMORY_CRYSTAL = ITEMS.register("advanced_memory_crystal", 
			() -> new MemoryCrystalItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 6));
	public static final RegistryObject<MaterializationCrystalItem> ADVANCED_MATERIALIZATION_CRYSTAL = ITEMS.register("advanced_materialization_crystal", 
			() -> new MaterializationCrystalItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<EnergyCrystalItem> ADVANCED_ENERGY_CRYSTAL = ITEMS.register("advanced_energy_crystal", 
			() -> new EnergyCrystalItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 100000, 2500, 3000));
	public static final RegistryObject<CommunicationCrystalItem> ADVANCED_COMMUNICATION_CRYSTAL = ITEMS.register("advanced_communication_crystal", 
			() -> new CommunicationCrystalItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 32));
	
	// Tools
	public static final RegistryObject<SGJourneySwordItem> NAQUADAH_SWORD = ITEMS.register("naquadah_sword", 
			() -> new SGJourneySwordItem(ToolMaterialInit.naquadah, 4, -2.4f, new Item.Properties().fireResistant()));
	public static final RegistryObject<SGJourneyPickaxeItem> NAQUADAH_PICKAXE = ITEMS.register("naquadah_pickaxe", 
			() -> new SGJourneyPickaxeItem(ToolMaterialInit.naquadah, 2, -2.8f, new Item.Properties().fireResistant()));
	public static final RegistryObject<SGJourneyAxeItem> NAQUADAH_AXE = ITEMS.register("naquadah_axe", 
			() -> new SGJourneyAxeItem(ToolMaterialInit.naquadah, 6.0f, -3.0f, new Item.Properties().fireResistant()));
	public static final RegistryObject<SGJourneyShovelItem> NAQUADAH_SHOVEL = ITEMS.register("naquadah_shovel", 
			() -> new SGJourneyShovelItem(ToolMaterialInit.naquadah, 2.5f, -3.0f, new Item.Properties().fireResistant()));
	public static final RegistryObject<SGJourneyHoeItem> NAQUADAH_HOE = ITEMS.register("naquadah_hoe", 
			() -> new SGJourneyHoeItem(ToolMaterialInit.naquadah, -2, 0.0f, new Item.Properties().fireResistant()));
	
	// Armor
	public static final RegistryObject<ArmorItem> NAQUADAH_HELMET = ITEMS.register("naquadah_helmet", 
			() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> NAQUADAH_CHESTPLATE = ITEMS.register("naquadah_chestplate", 
			() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.CHEST, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> NAQUADAH_LEGGINGS = ITEMS.register("naquadah_leggings", 
			() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.LEGS, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> NAQUADAH_BOOTS = ITEMS.register("naquadah_boots", 
			() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.FEET, new Item.Properties().fireResistant()));
	
	public static final RegistryObject<ArmorItem> JAFFA_HELMET = ITEMS.register("jaffa_helmet", 
			() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> JAFFA_CHESTPLATE = ITEMS.register("jaffa_chestplate", 
			() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.CHEST, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> JAFFA_LEGGINGS = ITEMS.register("jaffa_leggings", 
			() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.LEGS, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> JAFFA_BOOTS = ITEMS.register("jaffa_boots", 
			() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.FEET, new Item.Properties().fireResistant()));
	
	public static final RegistryObject<JackalArmorItem> JACKAL_HELMET = ITEMS.register("jackal_helmet", 
			() -> new JackalArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	public static final RegistryObject<FalconArmorItem> FALCON_HELMET = ITEMS.register("falcon_helmet", 
			() -> new FalconArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	
	public static final RegistryObject<PersonalShieldItem> PERSONAL_SHIELD_EMITTER = ITEMS.register("personal_shield_emitter", 
			() -> new PersonalShieldItem(ArmorMaterialInit.personal_shield, EquipmentSlot.CHEST, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant()));
		
	
	public static void register(IEventBus eventBus)
	{
		ITEMS.register(eventBus);
	}
}
