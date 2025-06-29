package net.povstalec.sgjourney.common.init;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.items.*;
import net.povstalec.sgjourney.common.items.armor.FalconArmorItem;
import net.povstalec.sgjourney.common.items.armor.JackalArmorItem;
import net.povstalec.sgjourney.common.items.armor.PersonalShieldItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MaterializationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.TransferCrystalItem;
import net.povstalec.sgjourney.common.items.energy_cores.FusionCoreItem;
import net.povstalec.sgjourney.common.items.energy_cores.NaquadahGeneratorCoreItem;

public class ItemInit
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StargateJourney.MODID);
	
	// Materials
	public static final RegistryObject<Item> RAW_NAQUADAH = ITEMS.register("raw_naquadah", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> NAQUADAH_ALLOY = ITEMS.register("naquadah_alloy", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> NAQUADAH_ALLOY_NUGGET = ITEMS.register("naquadah_alloy_nugget", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> REFINED_NAQUADAH = ITEMS.register("refined_naquadah", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> PURE_NAQUADAH = ITEMS.register("pure_naquadah", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16).fireResistant()));
	public static final RegistryObject<Item> NAQUADAH = ITEMS.register("naquadah", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16).fireResistant()));
	
	// Crafting Items
	public static final RegistryObject<Item> NAQUADAH_ROD = ITEMS.register("naquadah_rod", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> REACTION_CHAMBER = ITEMS.register("reaction_chamber", 
			() -> new Item(new Item.Properties().fireResistant()));
	public static final RegistryObject<Item> PLASMA_CONVERTER = ITEMS.register("plasma_converter", 
			() -> new Item(new Item.Properties().fireResistant()));
	
	public static final RegistryObject<Item> UNITY_SHARD = ITEMS.register("unity_shard",
			() -> new Item(new Item.Properties()));

	public static final RegistryObject<Item> CRYSTAL_BASE = ITEMS.register("crystal_base", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
	public static final RegistryObject<Item> ADVANCED_CRYSTAL_BASE = ITEMS.register("advanced_crystal_base", 
			() -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16)));
	
	// Food
	
	// Useful Items
	public static final RegistryObject<Item> LIQUID_NAQUADAH_BUCKET = ITEMS.register("liquid_naquadah_bucket", 
			() -> new BucketItem(FluidInit.LIQUID_NAQUADAH_SOURCE, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
	public static final RegistryObject<Item> HEAVY_LIQUID_NAQUADAH_BUCKET = ITEMS.register("heavy_liquid_naquadah_bucket", 
			() -> new BucketItem(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
	
	public static final RegistryObject<VialItem> VIAL = ITEMS.register("vial", 
			() -> new VialItem(new Item.Properties().stacksTo(1)));
	
	public static final RegistryObject<Item> RING_REMOTE = ITEMS.register("ring_remote", 
			() -> new RingRemoteItem(new RingRemoteItem.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
	public static final RegistryObject<Item> KARA_KESH = ITEMS.register("kara_kesh", 
			() -> new KaraKeshItem(new KaraKeshItem.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
	
	public static final RegistryObject<Item> MATOK = ITEMS.register("matok", 
			() -> new StaffWeaponItem(new StaffWeaponItem.Properties().stacksTo(1)));
	
	public static final RegistryObject<Item> PDA = ITEMS.register("pda", 
			() -> new PDAItem(new PDAItem.Properties().stacksTo(1).rarity(Rarity.RARE).fireResistant()));
	
	/*public static final RegistryObject<Item> UNIVERSE_DIALER = ITEMS.register("universe_dialer", 
			() -> new DialerItem(new PDAItem.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));*/
	
	public static final RegistryObject<Item> NAQUADAH_GENERATOR_CORE = ITEMS.register("naquadah_generator_core",
			() -> new NaquadahGeneratorCoreItem(new NaquadahGeneratorCoreItem.Properties().stacksTo(1)));
	public static final RegistryObject<Item> FUSION_CORE = ITEMS.register("fusion_core",
			() -> new FusionCoreItem(new FusionCoreItem.Properties().stacksTo(1).rarity(Rarity.EPIC)));
	
	public static final RegistryObject<Item> ZPM = ITEMS.register("zero_point_module", 
			() -> new ZeroPointModule(new ZeroPointModule.Properties().stacksTo(1).rarity(Rarity.EPIC)));
	
	public static final RegistryObject<Item> SYRINGE = ITEMS.register("syringe", 
			() -> new SyringeItem(new SyringeItem.Properties().stacksTo(1)));
	
	public static final RegistryObject<ArcheologistNotebook> ARCHEOLOGIST_NOTEBOOK = ITEMS.register("archeologist_notebook",
			() -> new ArcheologistNotebook(new Item.Properties().stacksTo(1)));
	
	// Crystals
	public static final RegistryObject<ControlCrystalItem> CONTROL_CRYSTAL = ITEMS.register("control_crystal", 
			() -> new ControlCrystalItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<ControlCrystalItem> LARGE_CONTROL_CRYSTAL = ITEMS.register("large_control_crystal", 
			() -> new ControlCrystalItem.Large(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<MemoryCrystalItem> MEMORY_CRYSTAL = ITEMS.register("memory_crystal", 
			() -> new MemoryCrystalItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<MaterializationCrystalItem> MATERIALIZATION_CRYSTAL = ITEMS.register("materialization_crystal", 
			() -> new MaterializationCrystalItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<EnergyCrystalItem> ENERGY_CRYSTAL = ITEMS.register("energy_crystal", 
			() -> new EnergyCrystalItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<TransferCrystalItem> TRANSFER_CRYSTAL = ITEMS.register("transfer_crystal", 
			() -> new TransferCrystalItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
	public static final RegistryObject<CommunicationCrystalItem> COMMUNICATION_CRYSTAL = ITEMS.register("communication_crystal", 
			() -> new CommunicationCrystalItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

	public static final RegistryObject<ControlCrystalItem> ADVANCED_CONTROL_CRYSTAL = ITEMS.register("advanced_control_crystal", 
			() -> new ControlCrystalItem.Advanced(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<MemoryCrystalItem> ADVANCED_MEMORY_CRYSTAL = ITEMS.register("advanced_memory_crystal", 
			() -> new MemoryCrystalItem.Advanced(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<MaterializationCrystalItem> ADVANCED_MATERIALIZATION_CRYSTAL = ITEMS.register("advanced_materialization_crystal", 
			() -> new MaterializationCrystalItem.Advanced(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<EnergyCrystalItem> ADVANCED_ENERGY_CRYSTAL = ITEMS.register("advanced_energy_crystal", 
			() -> new EnergyCrystalItem.Advanced(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<TransferCrystalItem> ADVANCED_TRANSFER_CRYSTAL = ITEMS.register("advanced_transfer_crystal", 
			() -> new TransferCrystalItem.Advanced(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<CommunicationCrystalItem> ADVANCED_COMMUNICATION_CRYSTAL = ITEMS.register("advanced_communication_crystal", 
			() -> new CommunicationCrystalItem.Advanced(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));

	public static final RegistryObject<StargateUpgradeItem> STARGATE_UPGRADE_CRYSTAL = ITEMS.register("stargate_upgrade_crystal", 
			() -> new StargateUpgradeItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	public static final RegistryObject<StargateVariantItem> STARGATE_VARIANT_CRYSTAL = ITEMS.register("stargate_variant_crystal", 
			() -> new StargateVariantItem(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
	
	// Tools
	public static final RegistryObject<SwordItem> NAQUADAH_SWORD = ITEMS.register("naquadah_sword", 
			() -> new SwordItem(ToolMaterialInit.naquadah, 4, -2.4f, new Item.Properties().fireResistant()));
	public static final RegistryObject<PickaxeItem> NAQUADAH_PICKAXE = ITEMS.register("naquadah_pickaxe", 
			() -> new PickaxeItem(ToolMaterialInit.naquadah, 2, -2.8f, new Item.Properties().fireResistant()));
	public static final RegistryObject<AxeItem> NAQUADAH_AXE = ITEMS.register("naquadah_axe", 
			() -> new AxeItem(ToolMaterialInit.naquadah, 6.0f, -3.0f, new Item.Properties().fireResistant()));
	public static final RegistryObject<ShovelItem> NAQUADAH_SHOVEL = ITEMS.register("naquadah_shovel", 
			() -> new ShovelItem(ToolMaterialInit.naquadah, 2.5f, -3.0f, new Item.Properties().fireResistant()));
	public static final RegistryObject<HoeItem> NAQUADAH_HOE = ITEMS.register("naquadah_hoe", 
			() -> new HoeItem(ToolMaterialInit.naquadah, -2, 0.0f, new Item.Properties().fireResistant()));
	
	// Armor
	public static final RegistryObject<ArmorItem> NAQUADAH_HELMET = ITEMS.register("naquadah_helmet", 
			() -> new ArmorItem(ArmorMaterialInit.NAQUADAH, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> NAQUADAH_CHESTPLATE = ITEMS.register("naquadah_chestplate", 
			() -> new ArmorItem(ArmorMaterialInit.NAQUADAH, EquipmentSlot.CHEST, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> NAQUADAH_LEGGINGS = ITEMS.register("naquadah_leggings", 
			() -> new ArmorItem(ArmorMaterialInit.NAQUADAH, EquipmentSlot.LEGS, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> NAQUADAH_BOOTS = ITEMS.register("naquadah_boots", 
			() -> new ArmorItem(ArmorMaterialInit.NAQUADAH, EquipmentSlot.FEET, new Item.Properties().fireResistant()));
	
	public static final RegistryObject<ArmorItem> SYSTEM_LORD_HELMET = ITEMS.register("system_lord_helmet",
			() -> new ArmorItem(ArmorMaterialInit.SYSTEM_LORD, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> SYSTEM_LORD_CHESTPLATE = ITEMS.register("system_lord_chestplate",
			() -> new ArmorItem(ArmorMaterialInit.SYSTEM_LORD, EquipmentSlot.CHEST, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> SYSTEM_LORD_LEGGINGS = ITEMS.register("system_lord_leggings",
			() -> new ArmorItem(ArmorMaterialInit.SYSTEM_LORD, EquipmentSlot.LEGS, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> SYSTEM_LORD_BOOTS = ITEMS.register("system_lord_boots",
			() -> new ArmorItem(ArmorMaterialInit.SYSTEM_LORD, EquipmentSlot.FEET, new Item.Properties().fireResistant()));
	
	public static final RegistryObject<ArmorItem> JAFFA_HELMET = ITEMS.register("jaffa_helmet", 
			() -> new ArmorItem(ArmorMaterialInit.JAFFA, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> JAFFA_CHESTPLATE = ITEMS.register("jaffa_chestplate", 
			() -> new ArmorItem(ArmorMaterialInit.JAFFA, EquipmentSlot.CHEST, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> JAFFA_LEGGINGS = ITEMS.register("jaffa_leggings", 
			() -> new ArmorItem(ArmorMaterialInit.JAFFA, EquipmentSlot.LEGS, new Item.Properties().fireResistant()));
	public static final RegistryObject<ArmorItem> JAFFA_BOOTS = ITEMS.register("jaffa_boots", 
			() -> new ArmorItem(ArmorMaterialInit.JAFFA, EquipmentSlot.FEET, new Item.Properties().fireResistant()));
	
	public static final RegistryObject<JackalArmorItem> JACKAL_HELMET = ITEMS.register("jackal_helmet", 
			() -> new JackalArmorItem(ArmorMaterialInit.JAFFA, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	public static final RegistryObject<FalconArmorItem> FALCON_HELMET = ITEMS.register("falcon_helmet", 
			() -> new FalconArmorItem(ArmorMaterialInit.JAFFA, EquipmentSlot.HEAD, new Item.Properties().fireResistant()));
	
	public static final RegistryObject<NaquadahFuelRodItem> NAQUADAH_FUEL_ROD = ITEMS.register("naquadah_fuel_rod", 
			() -> new NaquadahFuelRodItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1).fireResistant()));
	
	public static final RegistryObject<PersonalShieldItem> PERSONAL_SHIELD_EMITTER = ITEMS.register("personal_shield_emitter", 
			() -> new PersonalShieldItem(ArmorMaterialInit.PERSONAL_SHIELD, EquipmentSlot.CHEST, new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant()));
	
	public static final RegistryObject<CallForwardingDevice> CALL_FORWARDING_DEVICE = ITEMS.register("call_forwarding_device", 
			() -> new CallForwardingDevice(new Item.Properties().rarity(Rarity.RARE).stacksTo(1).fireResistant()));
	
	public static final RegistryObject<GDOItem> GDO = ITEMS.register("gdo", 
			() -> new GDOItem(new Item.Properties().stacksTo(1)));
	
	// Shielding
	public static final RegistryObject<Item> STARGATE_SHIELDING_RING = ITEMS.register("stargate_shielding_ring", 
			() -> new Item(new Item.Properties().stacksTo(1)));
	
	// Irises
	public static final RegistryObject<StargateIrisItem.Copper> COPPER_IRIS = ITEMS.register("copper_iris", 
			() -> new StargateIrisItem.Copper(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<StargateIrisItem.Iron> IRON_IRIS = ITEMS.register("iron_iris", 
			() -> new StargateIrisItem.Iron(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<StargateIrisItem.Gold> GOLDEN_IRIS = ITEMS.register("golden_iris", 
			() -> new StargateIrisItem.Gold(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<StargateIrisItem.Diamond> DIAMOND_IRIS = ITEMS.register("diamond_iris", 
			() -> new StargateIrisItem.Diamond(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<StargateIrisItem.Netherite> NETHERITE_IRIS = ITEMS.register("netherite_iris", 
			() -> new StargateIrisItem.Netherite(new Item.Properties().stacksTo(1).fireResistant()));

	public static final RegistryObject<StargateIrisItem.NaquadahAlloy> NAQUADAH_ALLOY_IRIS = ITEMS.register("naquadah_alloy_iris", 
			() -> new StargateIrisItem.NaquadahAlloy(new Item.Properties().stacksTo(1).fireResistant()));
	public static final RegistryObject<StargateIrisItem.Trinium> TRINIUM_IRIS = ITEMS.register("trinium_iris", 
			() -> new StargateIrisItem.Trinium(new Item.Properties().stacksTo(1)));

	public static final RegistryObject<StargateIrisItem.Bronze> BRONZE_IRIS = ITEMS.register("bronze_iris", 
			() -> new StargateIrisItem.Bronze(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<StargateIrisItem.Steel> STEEL_IRIS = ITEMS.register("steel_iris", 
			() -> new StargateIrisItem.Steel(new Item.Properties().stacksTo(1)));
		
	
	public static void register(IEventBus eventBus)
	{
		ITEMS.register(eventBus);
	}
}
