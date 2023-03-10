package net.povstalec.sgjourney.init;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.items.CommunicationCrystalItem;
import net.povstalec.sgjourney.items.HorusArmorItem;
import net.povstalec.sgjourney.items.JackalArmorItem;
import net.povstalec.sgjourney.items.KaraKeshItem;
import net.povstalec.sgjourney.items.MaTok;
import net.povstalec.sgjourney.items.MemoryCrystalItem;
import net.povstalec.sgjourney.items.PDAItem;
import net.povstalec.sgjourney.items.RingRemoteItem;
import net.povstalec.sgjourney.items.SyringeItem;
import net.povstalec.sgjourney.items.ZeroPointModule;
import net.povstalec.sgjourney.items.tools.SGJourneyAxeItem;
import net.povstalec.sgjourney.items.tools.SGJourneyHoeItem;
import net.povstalec.sgjourney.items.tools.SGJourneyPickaxeItem;
import net.povstalec.sgjourney.items.tools.SGJourneyShovelItem;
import net.povstalec.sgjourney.items.tools.SGJourneySwordItem;

//A class for initializing items
public class ItemInit
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StargateJourney.MODID);
	
	//Items
	public static final RegistryObject<Item> RAW_NAQUADAH = ITEMS.register("raw_naquadah", 
			() -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> NAQUADAH_ALLOY = ITEMS.register("naquadah_alloy", 
			() -> new Item(new Item.Properties()));
	public static final RegistryObject<Item> PURE_NAQUADAH = ITEMS.register("pure_naquadah", 
			() -> new Item(new Item.Properties().stacksTo(16)));
	public static final RegistryObject<Item> NAQUADAH = ITEMS.register("naquadah", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
	public static final RegistryObject<Item> NAQUADAH_BUCKET = ITEMS.register("liquid_naquadah_bucket", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1).craftRemainder(Items.BUCKET)));
	public static final RegistryObject<Item> NAQUADAH_BOTTLE = ITEMS.register("liquid_naquadah_bottle", 
			() -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));
	
	public static final RegistryObject<MemoryCrystalItem> MEMORY_CRYSTAL = ITEMS.register("memory_crystal", 
			() -> new MemoryCrystalItem(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> TRANSPORTATION_CRYSTAL = ITEMS.register("transportation_crystal", 
			() -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> ENERGY_CRYSTAL = ITEMS.register("energy_crystal", 
			() -> new Item(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<CommunicationCrystalItem> COMMUNICATION_CRYSTAL = ITEMS.register("communication_crystal", 
			() -> new CommunicationCrystalItem(new Item.Properties().stacksTo(1)));
	
	//Armor
		public static final RegistryObject<ArmorItem> NAQUADAH_HELMET = ITEMS.register("naquadah_helmet", 
				() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.HEAD, new Item.Properties()));
		public static final RegistryObject<ArmorItem> NAQUADAH_CHESTPLATE = ITEMS.register("naquadah_chestplate", 
				() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.CHEST, new Item.Properties()));
		public static final RegistryObject<ArmorItem> NAQUADAH_LEGGINGS = ITEMS.register("naquadah_leggings", 
				() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.LEGS, new Item.Properties()));
		public static final RegistryObject<ArmorItem> NAQUADAH_BOOTS = ITEMS.register("naquadah_boots", 
				() -> new ArmorItem(ArmorMaterialInit.naquadah, EquipmentSlot.FEET, new Item.Properties()));
		
		public static final RegistryObject<ArmorItem> JAFFA_HELMET = ITEMS.register("jaffa_helmet", 
				() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.HEAD, new Item.Properties()));
		public static final RegistryObject<ArmorItem> JAFFA_CHESTPLATE = ITEMS.register("jaffa_chestplate", 
				() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.CHEST, new Item.Properties()));
		public static final RegistryObject<ArmorItem> JAFFA_LEGGINGS = ITEMS.register("jaffa_leggings", 
				() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.LEGS, new Item.Properties()));
		public static final RegistryObject<ArmorItem> JAFFA_BOOTS = ITEMS.register("jaffa_boots", 
				() -> new ArmorItem(ArmorMaterialInit.jaffa, EquipmentSlot.FEET, new Item.Properties()));
		
		public static final RegistryObject<JackalArmorItem> JACKAL_HELMET = ITEMS.register("jackal_helmet", 
				() -> new JackalArmorItem(ArmorMaterialInit.jackal, EquipmentSlot.HEAD, new Item.Properties()));
		public static final RegistryObject<HorusArmorItem> HORUS_HELMET = ITEMS.register("horus_helmet", 
				() -> new HorusArmorItem(ArmorMaterialInit.horus, EquipmentSlot.HEAD, new Item.Properties()));
	
	//Tools
		public static final RegistryObject<SGJourneySwordItem> NAQUADAH_SWORD = ITEMS.register("naquadah_sword", 
				() -> new SGJourneySwordItem(ToolMaterialInit.naquadah, 4, -2.4f, new Item.Properties()));
		public static final RegistryObject<SGJourneyPickaxeItem> NAQUADAH_PICKAXE = ITEMS.register("naquadah_pickaxe", 
				() -> new SGJourneyPickaxeItem(ToolMaterialInit.naquadah, 2, -2.8f, new Item.Properties()));
		public static final RegistryObject<SGJourneyAxeItem> NAQUADAH_AXE = ITEMS.register("naquadah_axe", 
				() -> new SGJourneyAxeItem(ToolMaterialInit.naquadah, 6.0f, -3.0f, new Item.Properties()));
		public static final RegistryObject<SGJourneyShovelItem> NAQUADAH_SHOVEL = ITEMS.register("naquadah_shovel", 
				() -> new SGJourneyShovelItem(ToolMaterialInit.naquadah, 2.5f, -3.0f, new Item.Properties()));
		public static final RegistryObject<SGJourneyHoeItem> NAQUADAH_HOE = ITEMS.register("naquadah_hoe", 
				() -> new SGJourneyHoeItem(ToolMaterialInit.naquadah, -2, 0.0f, new Item.Properties()));

		public static final RegistryObject<Item> RING_REMOTE = ITEMS.register("ring_remote", 
				() -> new RingRemoteItem(new RingRemoteItem.Properties().stacksTo(1)));
		
		public static final RegistryObject<Item> KARA_KESH = ITEMS.register("kara_kesh", 
				() -> new KaraKeshItem(new KaraKeshItem.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
		
		public static final RegistryObject<Item> MATOK = ITEMS.register("matok", 
				() -> new MaTok(new MaTok.Properties().stacksTo(1)));
		
		public static final RegistryObject<Item> PDA = ITEMS.register("pda", 
				() -> new PDAItem(new PDAItem.Properties().stacksTo(1).rarity(Rarity.RARE)));
		
		public static final RegistryObject<Item> ZPM = ITEMS.register("zero_point_module", 
				() -> new ZeroPointModule(new ZeroPointModule.Properties().stacksTo(1).rarity(Rarity.EPIC)));
		
		public static final RegistryObject<Item> SYRINGE = ITEMS.register("syringe", 
				() -> new SyringeItem(new SyringeItem.Properties().stacksTo(1)));
		
	
	public static void register(IEventBus eventBus)
	{
		ITEMS.register(eventBus);
	}
}
