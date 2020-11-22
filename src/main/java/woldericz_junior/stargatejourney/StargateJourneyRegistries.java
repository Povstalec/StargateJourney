package woldericz_junior.stargatejourney;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import init.CustomArmorMaterials;
import init.CustomToolMaterials;
import init.StargateBlocks;
import init.StargateItems;
import init.StargateJourneyBiomes;
import net.minecraft.block.Block;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import woldericz_junior.stargatejourney.blocks.ArcheologyTableBlock;
import woldericz_junior.stargatejourney.blocks.ArtifactBlock;
import woldericz_junior.stargatejourney.blocks.DHDBlock;
import woldericz_junior.stargatejourney.blocks.FirePitBlock;
import woldericz_junior.stargatejourney.blocks.StargateBlock;
import woldericz_junior.stargatejourney.blocks.StargateHitboxBlock;
import woldericz_junior.stargatejourney.items.CustomAxeItem;
import woldericz_junior.stargatejourney.items.CustomHoeItem;
import woldericz_junior.stargatejourney.items.CustomPickaxeItem;
import woldericz_junior.stargatejourney.items.CustomShovelItem;
import woldericz_junior.stargatejourney.items.CustomSwordItem;
import woldericz_junior.stargatejourney.items.HorusArmor;
import woldericz_junior.stargatejourney.items.JackalArmor;
import woldericz_junior.stargatejourney.items.JaffaStaffWeapon;
import woldericz_junior.stargatejourney.tileentities.MovieStargateTile;
import woldericz_junior.stargatejourney.world.biomes.AbydosDesert;
import woldericz_junior.stargatejourney.world.biomes.AbydosDesertHills;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class StargateJourneyRegistries 
{
	public static final String MODID = "sgjourney";
	private static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final ItemGroup STARGATE = new StargateItemGroup();
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll
		(
				StargateItems.movie_stargate = new BlockItem(StargateBlocks.movie_stargate, new Item.Properties().maxStackSize(1).rarity(Rarity.EPIC).group(STARGATE)).setRegistryName(StargateBlocks.movie_stargate.getRegistryName()),
				StargateItems.movie_stargate_hitbox = new BlockItem(StargateBlocks.movie_stargate_hitbox, new Item.Properties().maxStackSize(1).rarity(Rarity.EPIC).group(STARGATE)).setRegistryName(StargateBlocks.movie_stargate_hitbox.getRegistryName()),
				StargateItems.milky_way_stargate = new BlockItem(StargateBlocks.milky_way_stargate, new Item.Properties().maxStackSize(1).rarity(Rarity.EPIC).group(STARGATE)).setRegistryName(StargateBlocks.milky_way_stargate.getRegistryName()),
				StargateItems.milky_way_dhd = new BlockItem(StargateBlocks.milky_way_dhd, new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(STARGATE)).setRegistryName(location("milky_way_dhd")),
				
				StargateItems.naquadah_ore = new BlockItem(StargateBlocks.naquadah_ore, new Item.Properties().group(STARGATE)).setRegistryName(StargateBlocks.naquadah_ore.getRegistryName()),
				StargateItems.naquadah_block = new BlockItem(StargateBlocks.naquadah_block, new Item.Properties().group(STARGATE)).setRegistryName(StargateBlocks.naquadah_block.getRegistryName()),
				StargateItems.sandstone_hieroglyphs = new BlockItem(StargateBlocks.sandstone_hieroglyphs, new Item.Properties().group(STARGATE)).setRegistryName(StargateBlocks.sandstone_hieroglyphs.getRegistryName()),
				StargateItems.fire_pit = new BlockItem(StargateBlocks.fire_pit, new Item.Properties().group(STARGATE)).setRegistryName(StargateBlocks.fire_pit.getRegistryName()),
				StargateItems.archeology_table = new BlockItem(StargateBlocks.archeology_table, new Item.Properties().group(STARGATE)).setRegistryName(StargateBlocks.archeology_table.getRegistryName()),
				StargateItems.golden_idol = new BlockItem(StargateBlocks.golden_idol, new Item.Properties().group(STARGATE)).setRegistryName(StargateBlocks.golden_idol.getRegistryName()),
					
				StargateItems.naquadah = new Item(new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah")),
				StargateItems.weapons_grade_naquadah = new Item(new Item.Properties().maxStackSize(16).rarity(Rarity.UNCOMMON).group(STARGATE)).setRegistryName(location("weapons_grade_naquadah")),
				StargateItems.naquadah_ingot = new Item(new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_ingot")),
				
				StargateItems.naquadah_sword = new CustomSwordItem(CustomToolMaterials.naquadah, 3, -2.4f, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_sword")),
				StargateItems.naquadah_pickaxe = new CustomPickaxeItem(CustomToolMaterials.naquadah, 1, -2.8f, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_pickaxe")),
				StargateItems.naquadah_axe = new CustomAxeItem(CustomToolMaterials.naquadah, 5.0f, -3.1f, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_axe")),
				StargateItems.naquadah_shovel = new CustomShovelItem(CustomToolMaterials.naquadah, 1.5f, -3.0f, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_shovel")),
				StargateItems.naquadah_hoe = new CustomHoeItem(CustomToolMaterials.naquadah, -1.0f, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_hoe")),
				
				StargateItems.jaffa_staff_weapon = new JaffaStaffWeapon(new Item.Properties().maxStackSize(1).group(STARGATE)).setRegistryName(location("jaffa_staff_weapon")),
				
				StargateItems.naquadah_helmet = new ArmorItem(CustomArmorMaterials.naquadah, EquipmentSlotType.HEAD, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_helmet")),
				StargateItems.naquadah_chestplate = new ArmorItem(CustomArmorMaterials.naquadah, EquipmentSlotType.CHEST, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_chestplate")),
				StargateItems.naquadah_leggings = new ArmorItem(CustomArmorMaterials.naquadah, EquipmentSlotType.LEGS, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_leggings")),
				StargateItems.naquadah_boots = new ArmorItem(CustomArmorMaterials.naquadah, EquipmentSlotType.FEET, new Item.Properties().group(STARGATE)).setRegistryName(location("naquadah_boots")),
				StargateItems.jaffa_helmet = new ArmorItem(CustomArmorMaterials.jaffa, EquipmentSlotType.HEAD, new Item.Properties().group(STARGATE)).setRegistryName(location("jaffa_helmet")),
				StargateItems.jaffa_chestplate = new ArmorItem(CustomArmorMaterials.jaffa, EquipmentSlotType.CHEST, new Item.Properties().group(STARGATE)).setRegistryName(location("jaffa_chestplate")),
				StargateItems.jaffa_leggings = new ArmorItem(CustomArmorMaterials.jaffa, EquipmentSlotType.LEGS, new Item.Properties().group(STARGATE)).setRegistryName(location("jaffa_leggings")),
				StargateItems.jaffa_boots = new ArmorItem(CustomArmorMaterials.jaffa, EquipmentSlotType.FEET, new Item.Properties().group(STARGATE)).setRegistryName(location("jaffa_boots")),
				StargateItems.jackal_helmet = new JackalArmor(CustomArmorMaterials.jackal, EquipmentSlotType.HEAD, new Item.Properties().group(STARGATE)).setRegistryName(location("jackal_helmet")),
				StargateItems.horus_helmet = new HorusArmor(CustomArmorMaterials.horus, EquipmentSlotType.HEAD, new Item.Properties().group(STARGATE)).setRegistryName(location("horus_helmet"))
		);
		
		LOGGER.info("Items registered.");
	}
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll
		(
				StargateBlocks.movie_stargate = new StargateBlock(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(6.0f).harvestLevel(2).harvestTool(ToolType.PICKAXE).notSolid()).setRegistryName(location("movie_stargate")),
				StargateBlocks.milky_way_stargate = new StargateBlock(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(6.0f).harvestLevel(2).harvestTool(ToolType.PICKAXE).notSolid()).setRegistryName(location("milky_way_stargate")),
				StargateBlocks.movie_stargate_hitbox = new StargateHitboxBlock(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(6.0f).harvestLevel(2).harvestTool(ToolType.PICKAXE).notSolid()).setRegistryName(location("movie_stargate_hitbox")),
				StargateBlocks.milky_way_dhd = new DHDBlock(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(6.0f).harvestLevel(2).harvestTool(ToolType.PICKAXE).notSolid()).setRegistryName(location("milky_way_dhd")),
				
				StargateBlocks.naquadah_ore = new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(3.0f, 3.0f).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE).harvestLevel(2).harvestTool(ToolType.PICKAXE)).setRegistryName(location("naquadah_ore")),
				StargateBlocks.naquadah_block = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0f, 6.0f).harvestLevel(2).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)).setRegistryName(location("naquadah_block")),
				StargateBlocks.sandstone_hieroglyphs = new Block(Block.Properties.create(Material.ROCK, MaterialColor.SAND).hardnessAndResistance(0.8F).harvestTool(ToolType.PICKAXE).sound(SoundType.STONE)).setRegistryName(location("sandstone_hieroglyphs")),
				StargateBlocks.fire_pit = new FirePitBlock(Block.Properties.create(Material.ROCK, MaterialColor.ORANGE_TERRACOTTA).hardnessAndResistance(2.0F).harvestTool(ToolType.PICKAXE).lightValue(14).sound(SoundType.STONE)).setRegistryName(location("fire_pit")),
				StargateBlocks.archeology_table = new ArcheologyTableBlock(Block.Properties.create(Material.WOOD, MaterialColor.WOOD).hardnessAndResistance(2.0F, 6.0F).harvestTool(ToolType.AXE).sound(SoundType.WOOD)).setRegistryName(location("archeology_table")),
				StargateBlocks.golden_idol = new ArtifactBlock(Block.Properties.create(Material.IRON, MaterialColor.GOLD).hardnessAndResistance(3.0F, 6.0F).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)).setRegistryName(location("golden_idol"))
		);
		
		LOGGER.info("Blocks registered.");
	}
	
	@SubscribeEvent
	public static void registerBiomes(final RegistryEvent.Register<Biome> event)
	{
		event.getRegistry().registerAll
		(
			StargateJourneyBiomes.abydos_desert = new AbydosDesert(),
			StargateJourneyBiomes.abydos_desert_hills = new AbydosDesertHills()
		);
		
		StargateJourneyBiomes.registerBiomes();
	}
	
	@SubscribeEvent
	public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event)
	{
		event.getRegistry().registerAll
		(
			TileEntityType.Builder.create(MovieStargateTile::new, StargateBlocks.movie_stargate).build(null).setRegistryName("movie_stargate")
		);
	}
	
	public static final ResourceLocation ABYDOS_DIM_TYPE = new ResourceLocation(MODID, "abydos");
	
	public static ResourceLocation location(String name)
	{
		return new ResourceLocation(MODID, name);
	}
}
