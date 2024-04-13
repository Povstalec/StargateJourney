package net.povstalec.sgjourney.common.init;

import java.util.function.Supplier;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.ATAGeneDetectorBlock;
import net.povstalec.sgjourney.common.blocks.ArcheologyTableBlock;
import net.povstalec.sgjourney.common.blocks.CartoucheBlock;
import net.povstalec.sgjourney.common.blocks.ChevronBlock;
import net.povstalec.sgjourney.common.blocks.ClassicStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.ExplosiveBlock;
import net.povstalec.sgjourney.common.blocks.FirePitBlock;
import net.povstalec.sgjourney.common.blocks.GoldenIdolBlock;
import net.povstalec.sgjourney.common.blocks.NaquadahGeneratorMarkIBlock;
import net.povstalec.sgjourney.common.blocks.NaquadahGeneratorMarkIIBlock;
import net.povstalec.sgjourney.common.blocks.RingPanelBlock;
import net.povstalec.sgjourney.common.blocks.SecretSwitchBlock;
import net.povstalec.sgjourney.common.blocks.SymbolBlock;
import net.povstalec.sgjourney.common.blocks.TransportRingsBlock;
import net.povstalec.sgjourney.common.blocks.ZPMHubBlock;
import net.povstalec.sgjourney.common.blocks.dhd.AbstractDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.ClassicDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.MilkyWayDHDBlock;
import net.povstalec.sgjourney.common.blocks.dhd.PegasusDHDBlock;
import net.povstalec.sgjourney.common.blocks.stargate.ClassicStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.ClassicStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.stargate.MilkyWayStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.MilkyWayStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.stargate.PegasusStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.PegasusStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.stargate.TollanStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.TollanStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.stargate.UniverseStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.UniverseStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.tech.AdvancedCrystalInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech.AdvancedCrystallizerBlock;
import net.povstalec.sgjourney.common.blocks.tech.BasicInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech.CrystalInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech.CrystallizerBlock;
import net.povstalec.sgjourney.common.blocks.tech.HeavyNaquadahLiquidizerBlock;
import net.povstalec.sgjourney.common.blocks.tech.NaquadahLiquidizerBlock;
import net.povstalec.sgjourney.common.items.blocks.CartoucheBlockItem;
import net.povstalec.sgjourney.common.items.blocks.DHDItem;
import net.povstalec.sgjourney.common.items.blocks.SGJourneyBlockItem;
import net.povstalec.sgjourney.common.items.blocks.StargateBlockItem;

public class BlockInit
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StargateJourney.MODID);
	
	//Block Tab
	public static final RegistryObject<UniverseStargateBlock> UNIVERSE_STARGATE = registerStargateBlock("universe_stargate", 
			() -> new UniverseStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<UniverseStargateRingBlock> UNIVERSE_RING = BLOCKS.register("universe_ring", 
			() -> new UniverseStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final RegistryObject<MilkyWayStargateBlock> MILKY_WAY_STARGATE = registerStargateBlock("milky_way_stargate", 
			() -> new MilkyWayStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<MilkyWayStargateRingBlock> MILKY_WAY_RING = BLOCKS.register("milky_way_ring", 
			() -> new MilkyWayStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final RegistryObject<PegasusStargateBlock> PEGASUS_STARGATE = registerStargateBlock("pegasus_stargate", 
			() -> new PegasusStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<PegasusStargateRingBlock> PEGASUS_RING = BLOCKS.register("pegasus_ring", 
			() -> new PegasusStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final RegistryObject<ClassicStargateBlock> CLASSIC_STARGATE = registerStargateBlock("classic_stargate", 
			() -> new ClassicStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.UNCOMMON);
	public static final RegistryObject<ClassicStargateRingBlock> CLASSIC_RING = BLOCKS.register("classic_ring", 
			() -> new ClassicStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<ClassicStargateBaseBlock> CLASSIC_STARGATE_BASE_BLOCK = registerBlock("classic_stargate_base_block", 
			() -> new ClassicStargateBaseBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	public static final RegistryObject<Block> CLASSIC_STARGATE_CHEVRON_BLOCK = registerBlock("classic_stargate_chevron_block", 
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	public static final RegistryObject<Block> CLASSIC_STARGATE_RING_BLOCK = registerBlock("classic_stargate_ring_block", 
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	
	public static final RegistryObject<TollanStargateBlock> TOLLAN_STARGATE = registerStargateBlock("tollan_stargate",
			() -> new TollanStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<TollanStargateRingBlock> TOLLAN_RING = BLOCKS.register("tollan_ring",
			() -> new TollanStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final RegistryObject<AbstractDHDBlock> MILKY_WAY_DHD = registerDHDBlock("milky_way_dhd", 
			() -> new MilkyWayDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	
	public static final RegistryObject<AbstractDHDBlock> PEGASUS_DHD = registerDHDBlock("pegasus_dhd", 
			() -> new PegasusDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	
	public static final RegistryObject<AbstractDHDBlock> CLASSIC_DHD = registerDHDBlock("classic_dhd", 
			() -> new ClassicDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.UNCOMMON);
	
	public static final RegistryObject<ChevronBlock> UNIVERSE_STARGATE_CHEVRON = registerBlock("universe_stargate_chevron", 
			() -> new ChevronBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F)
					.requiresCorrectToolForDrops().noOcclusion().noCollission()
					.lightLevel((state) -> state.getValue(FirePitBlock.LIT) ? 7 : 0)), Rarity.UNCOMMON, 16);
	
	public static final RegistryObject<TransportRingsBlock> TRANSPORT_RINGS = registerEntityBlock("transport_rings", 
			() -> new TransportRingsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	public static final RegistryObject<RingPanelBlock> RING_PANEL = registerBlock("ring_panel", 
			() -> new RingPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE, 1);
	
	public static final RegistryObject<ExplosiveBlock> NAQUADAH_ORE = registerBlock("naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final RegistryObject<ExplosiveBlock> NETHER_NAQUADAH_ORE = registerBlock("nether_naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final RegistryObject<ExplosiveBlock> DEEPSLATE_NAQUADAH_ORE = registerBlock("deepslate_naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops(), 4.0F));
	
	public static final RegistryObject<ExplosiveBlock> RAW_NAQUADAH_BLOCK = registerBlock("raw_naquadah_block", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 10.0F));
	
	public static final RegistryObject<LiquidBlock> LIQUID_NAQUADAH_BLOCK = registerBlock("liquid_naquadah", 
			() -> new LiquidBlock(FluidInit.LIQUID_NAQUADAH_SOURCE, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()));
	public static final RegistryObject<LiquidBlock> HEAVY_LIQUID_NAQUADAH_BLOCK = registerBlock("heavy_liquid_naquadah", 
			() -> new LiquidBlock(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE, BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()));
	
	public static final RegistryObject<Block> NAQUADAH_BLOCK = registerBlock("naquadah_block", 
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> NAQUADAH_STAIRS = registerBlock("naquadah_stairs", 
			() -> new StairBlock(() -> NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> NAQUADAH_SLAB = registerBlock("naquadah_slab", 
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<Block> CUT_NAQUADAH_BLOCK = registerBlock("cut_naquadah_block", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> CUT_NAQUADAH_STAIRS = registerBlock("cut_naquadah_stairs", 
			() -> new StairBlock(() -> NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> CUT_NAQUADAH_SLAB = registerBlock("cut_naquadah_slab", 
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<GoldenIdolBlock> GOLDEN_IDOL = registerBlock("golden_idol", 
			() -> new GoldenIdolBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(3.0F, 6.0F)
					.sound(SoundType.METAL).requiresCorrectToolForDrops()), Rarity.UNCOMMON, 16);
	
	public static final RegistryObject<ArcheologyTableBlock> ARCHEOLOGY_TABLE = registerBlock("archeology_table", 
			() -> new ArcheologyTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion()));
	
	public static final RegistryObject<FirePitBlock> FIRE_PIT = registerBlock("fire_pit", 
			() -> new FirePitBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instabreak()
			.sound(SoundType.STONE).lightLevel((state) -> state.getValue(FirePitBlock.LIT) ? 15 : 0), ParticleTypes.FLAME));
	
	public static final RegistryObject<Block> SANDSTONE_WITH_LAPIS = registerBlock("sandstone_with_lapis", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> SANDSTONE_WITH_GOLD = registerBlock("sandstone_with_gold",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> SANDSTONE_HIEROGLYPHS = registerBlock("sandstone_hieroglyphs", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<SecretSwitchBlock> SANDSTONE_SWITCH = registerBlock("sandstone_switch", 
			() -> new SecretSwitchBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));

	public static final RegistryObject<CartoucheBlock> SANDSTONE_CARTOUCHE = registerCartoucheBlock("sandstone_cartouche", 
			() -> new CartoucheBlock.Sandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<CartoucheBlock> STONE_CARTOUCHE = registerCartoucheBlock("stone_cartouche", 
			() -> new CartoucheBlock.Stone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<SymbolBlock> STONE_SYMBOL = registerBlock("stone_symbol", 
			() -> new SymbolBlock.Stone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<SymbolBlock> SANDSTONE_SYMBOL = registerBlock("sandstone_symbol", 
			() -> new SymbolBlock.Sandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<NaquadahGeneratorMarkIBlock> NAQUADAH_GENERATOR_MARK_I = registerBlock("naquadah_generator_mark_i", 
			() -> new NaquadahGeneratorMarkIBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), 1);
	public static final RegistryObject<NaquadahGeneratorMarkIIBlock> NAQUADAH_GENERATOR_MARK_II = registerBlock("naquadah_generator_mark_ii", 
			() -> new NaquadahGeneratorMarkIIBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), 1);
	
	public static final RegistryObject<BasicInterfaceBlock> BASIC_INTERFACE = registerBlock("basic_interface", 
			() -> new BasicInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), 1);
	public static final RegistryObject<CrystalInterfaceBlock> CRYSTAL_INTERFACE = registerBlock("crystal_interface", 
			() -> new CrystalInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	public static final RegistryObject<AdvancedCrystalInterfaceBlock> ADVANCED_CRYSTAL_INTERFACE = registerBlock("advanced_crystal_interface", 
			() -> new AdvancedCrystalInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	
	public static final RegistryObject<ATAGeneDetectorBlock> ANCIENT_GENE_DETECTOR = registerBlock("ancient_gene_detector", 
			() -> new ATAGeneDetectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	
	public static final RegistryObject<ZPMHubBlock> ZPM_HUB = registerBlock("zpm_hub", 
			() -> new ZPMHubBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	//TODO ZPM Port
	//TODO ZPM Plug
	
	public static final RegistryObject<NaquadahLiquidizerBlock> NAQUADAH_LIQUIDIZER = registerBlock("naquadah_liquidizer", 
			() -> new NaquadahLiquidizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), 1);
	public static final RegistryObject<HeavyNaquadahLiquidizerBlock> HEAVY_NAQUADAH_LIQUIDIZER = registerBlock("heavy_naquadah_liquidizer", 
			() -> new HeavyNaquadahLiquidizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	
	public static final RegistryObject<CrystallizerBlock> CRYSTALLIZER = registerBlock("crystallizer", 
			() -> new CrystallizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	public static final RegistryObject<AdvancedCrystallizerBlock> ADVANCED_CRYSTALLIZER = registerBlock("advanced_crystallizer", 
			() -> new AdvancedCrystallizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	
	
	private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		
		registerBlockItem(name, toReturn);
		
		return toReturn;
	}
	private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block, int stacksTo)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		
		registerBlockItem(name, toReturn, stacksTo);
		
		return toReturn;
	}
	private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block, Rarity rarity, int stacksTo)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		
		registerBlockItem(name, toReturn, rarity, stacksTo);
		
		return toReturn;
	}
	
	private static <T extends Block>RegistryObject<T> registerStargateBlock(String name, Supplier<T> block, Rarity rarity)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);

		registerStargateBlockItem(name, toReturn, rarity, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>RegistryObject<T> registerEntityBlock(String name, Supplier<T> block, Rarity rarity)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);

		registerSGJourneyBlockItem(name, toReturn, rarity, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>RegistryObject<T> registerDHDBlock(String name, Supplier<T> block, Rarity rarity)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);

		registerDHDItem(name, toReturn, rarity, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>RegistryObject<T> registerCartoucheBlock(String name, Supplier<T> block)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		
		registerCartoucheBlockItem(name, toReturn, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>RegistryObject<T> registerEntityBlock(String name, Supplier<T> block)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);

		registerSGJourneyBlockItem(name, toReturn, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
	{
		return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
	}
	
	private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerStargateBlockItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new StargateBlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo).fireResistant()));
	}
	
	private static <T extends Block>RegistryObject<Item> registerSGJourneyBlockItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new SGJourneyBlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo).fireResistant()));
	}
	
	private static <T extends Block>RegistryObject<Item> registerSGJourneyBlockItem(String name, RegistryObject<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new SGJourneyBlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerDHDItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new DHDItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerCartoucheBlockItem(String name, RegistryObject<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new CartoucheBlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	public static void register(IEventBus eventBus)
	{
		BLOCKS.register(eventBus);
	}
}