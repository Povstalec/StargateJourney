package net.povstalec.sgjourney.common.init;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.*;
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
import net.povstalec.sgjourney.common.blocks.stargate.shielding.GenericShieldingBlock;
import net.povstalec.sgjourney.common.blocks.tech.AdvancedCrystalInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech.AdvancedCrystallizerBlock;
import net.povstalec.sgjourney.common.blocks.tech.BasicInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech.CrystalInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech.CrystallizerBlock;
import net.povstalec.sgjourney.common.blocks.tech.HeavyNaquadahLiquidizerBlock;
import net.povstalec.sgjourney.common.blocks.tech.NaquadahGeneratorMarkIBlock;
import net.povstalec.sgjourney.common.blocks.tech.NaquadahGeneratorMarkIIBlock;
import net.povstalec.sgjourney.common.blocks.tech.NaquadahLiquidizerBlock;
import net.povstalec.sgjourney.common.blocks.tech.TransportRingsBlock;
import net.povstalec.sgjourney.common.blocks.tech.ZPMHubBlock;
import net.povstalec.sgjourney.common.items.blocks.CartoucheBlockItem;
import net.povstalec.sgjourney.common.items.blocks.DHDItem;
import net.povstalec.sgjourney.common.items.blocks.StargateBlockItem;
import net.povstalec.sgjourney.common.items.blocks.TransporterBlockItem;

public class BlockInit
{
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(StargateJourney.MODID);
	
	//Block Tab
	public static final DeferredBlock<UniverseStargateBlock> UNIVERSE_STARGATE = registerStargateBlock("universe_stargate",
			() -> new UniverseStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final DeferredBlock<UniverseStargateRingBlock> UNIVERSE_RING = BLOCKS.register("universe_ring",
			() -> new UniverseStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final DeferredBlock<GenericShieldingBlock> UNIVERSE_SHIELDING =  BLOCKS.register("universe_shielding",
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	
	public static final DeferredBlock<MilkyWayStargateBlock> MILKY_WAY_STARGATE = registerStargateBlock("milky_way_stargate",
			() -> new MilkyWayStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final DeferredBlock<MilkyWayStargateRingBlock> MILKY_WAY_RING = BLOCKS.register("milky_way_ring",
			() -> new MilkyWayStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final DeferredBlock<GenericShieldingBlock> MILKY_WAY_SHIELDING =  BLOCKS.register("milky_way_shielding",
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	
	public static final DeferredBlock<PegasusStargateBlock> PEGASUS_STARGATE = registerStargateBlock("pegasus_stargate",
			() -> new PegasusStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final DeferredBlock<PegasusStargateRingBlock> PEGASUS_RING = BLOCKS.register("pegasus_ring",
			() -> new PegasusStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final DeferredBlock<GenericShieldingBlock> PEGASUS_SHIELDING =  BLOCKS.register("pegasus_shielding",
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	
	public static final DeferredBlock<ClassicStargateBlock> CLASSIC_STARGATE = registerStargateBlock("classic_stargate",
			() -> new ClassicStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.UNCOMMON);
	public static final DeferredBlock<ClassicStargateRingBlock> CLASSIC_RING = BLOCKS.register("classic_ring",
			() -> new ClassicStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final DeferredBlock<GenericShieldingBlock> CLASSIC_SHIELDING =  BLOCKS.register("classic_shielding",
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	public static final DeferredBlock<ClassicStargateBaseBlock> CLASSIC_STARGATE_BASE_BLOCK = registerBlock("classic_stargate_base_block",
			() -> new ClassicStargateBaseBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	public static final DeferredBlock<Block> CLASSIC_STARGATE_CHEVRON_BLOCK = registerBlock("classic_stargate_chevron_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	public static final DeferredBlock<Block> CLASSIC_STARGATE_RING_BLOCK = registerBlock("classic_stargate_ring_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	
	public static final DeferredBlock<TollanStargateBlock> TOLLAN_STARGATE = registerStargateBlock("tollan_stargate",
			() -> new TollanStargateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final DeferredBlock<TollanStargateRingBlock> TOLLAN_RING = BLOCKS.register("tollan_ring",
			() -> new TollanStargateRingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final DeferredBlock<AbstractDHDBlock> MILKY_WAY_DHD = registerDHDBlock("milky_way_dhd",
			() -> new MilkyWayDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	
	public static final DeferredBlock<AbstractDHDBlock> PEGASUS_DHD = registerDHDBlock("pegasus_dhd",
			() -> new PegasusDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	
	public static final DeferredBlock<AbstractDHDBlock> CLASSIC_DHD = registerDHDBlock("classic_dhd",
			() -> new ClassicDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.UNCOMMON);
	
	public static final DeferredBlock<ChevronBlock> UNIVERSE_STARGATE_CHEVRON = registerBlock("universe_stargate_chevron",
			() -> new ChevronBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F)
					.requiresCorrectToolForDrops().noOcclusion().noCollission()
					.lightLevel(litBlockEmission(7))), Rarity.UNCOMMON, 16);
	
	public static final DeferredBlock<TransportRingsBlock> TRANSPORT_RINGS = registerTransporterBlock("transport_rings",
			() -> new TransportRingsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	public static final DeferredBlock<RingPanelBlock> RING_PANEL = registerBlock("ring_panel",
			() -> new RingPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE, 1);
	
	public static final DeferredBlock<ColoredFallingBlock> SULFUR_SAND = registerBlock("sulfur_sand",
			() -> new ColoredFallingBlock(new ColorRGBA(16443180), BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.SAND)));
	
	public static final DeferredBlock<BuddingUnityBlock> BUDDING_UNITY = registerBlock("budding_unity",
			() -> new BuddingUnityBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.SAND).randomTicks()));
	
	public static final DeferredBlock<UnityClusterBlock> SMALL_UNITY_BUD = registerBlock("small_unity_bud",
			() -> new UnityClusterBlock(3, 4, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	public static final DeferredBlock<UnityClusterBlock> MEDIUM_UNITY_BUD = registerBlock("medium_unity_bud",
			() -> new UnityClusterBlock(4, 3, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	public static final DeferredBlock<UnityClusterBlock> LARGE_UNITY_BUD = registerBlock("large_unity_bud",
			() -> new UnityClusterBlock(5, 3, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	public static final DeferredBlock<UnityClusterBlock> UNITY_CLUSTER = registerBlock("unity_cluster",
			() -> new UnityClusterBlock(7, 3, BlockBehaviour.Properties.of().mapColor(MapColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<ExplosiveBlock> NAQUADAH_ORE = registerBlock("naquadah_ore",
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final DeferredBlock<ExplosiveBlock> NETHER_NAQUADAH_ORE = registerBlock("nether_naquadah_ore",
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final DeferredBlock<ExplosiveBlock> DEEPSLATE_NAQUADAH_ORE = registerBlock("deepslate_naquadah_ore",
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops(), 4.0F));
	
	public static final DeferredBlock<ExplosiveBlock> RAW_NAQUADAH_BLOCK = registerBlock("raw_naquadah_block",
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 10.0F));
	public static final DeferredBlock<ExplosiveBlock> PURE_NAQUADAH_BLOCK = registerBlock("pure_naquadah_block",
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 20.0F), Rarity.UNCOMMON, 64);
	
	public static final DeferredBlock<LiquidBlock> LIQUID_NAQUADAH_BLOCK = registerBlock("liquid_naquadah",
			() -> new LiquidBlock(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()));
	public static final DeferredBlock<LiquidBlock> HEAVY_LIQUID_NAQUADAH_BLOCK = registerBlock("heavy_liquid_naquadah",
			() -> new LiquidBlock(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()));
	
	public static final DeferredBlock<Block> NAQUADAH_BLOCK = registerBlock("naquadah_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> NAQUADAH_STAIRS = registerBlock("naquadah_stairs",
			() -> new StairBlock(NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> NAQUADAH_SLAB = registerBlock("naquadah_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CUT_NAQUADAH_BLOCK = registerBlock("cut_naquadah_block",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_NAQUADAH_STAIRS = registerBlock("cut_naquadah_stairs",
			() -> new StairBlock(NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_NAQUADAH_SLAB = registerBlock("cut_naquadah_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<GoldenIdolBlock> GOLDEN_IDOL = registerBlock("golden_idol",
			() -> new GoldenIdolBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(3.0F, 6.0F)
					.sound(SoundType.METAL).requiresCorrectToolForDrops()), Rarity.UNCOMMON, 16);
	
	public static final DeferredBlock<ArcheologyTableBlock> ARCHEOLOGY_TABLE = registerBlock("archeology_table",
			() -> new ArcheologyTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion()));
	
	public static final DeferredBlock<FirePitBlock> FIRE_PIT = registerBlock("fire_pit",
			() -> new FirePitBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instabreak()
			.sound(SoundType.STONE).lightLevel((state) -> state.getValue(FirePitBlock.LIT) ? 15 : 0), ParticleTypes.FLAME));
	
	public static final DeferredBlock<Block> SANDSTONE_WITH_LAPIS = registerBlock("sandstone_with_lapis",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<RotatedPillarBlock> SANDSTONE_WITH_GOLD = registerBlock("sandstone_with_gold",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<RotatedPillarBlock> SANDSTONE_HIEROGLYPHS = registerBlock("sandstone_hieroglyphs",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<SecretSwitchBlock> SANDSTONE_SWITCH = registerBlock("sandstone_switch",
			() -> new SecretSwitchBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> RED_SANDSTONE_WITH_LAPIS = registerBlock("red_sandstone_with_lapis",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> RED_SANDSTONE_WITH_GOLD = registerBlock("red_sandstone_with_gold",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> RED_SANDSTONE_GLYPHS = registerBlock("red_sandstone_glyphs",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));

	public static final DeferredBlock<CartoucheBlock> SANDSTONE_CARTOUCHE = registerCartoucheBlock("sandstone_cartouche",
			() -> new CartoucheBlock.Sandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<CartoucheBlock> RED_SANDSTONE_CARTOUCHE = registerCartoucheBlock("red_sandstone_cartouche",
			() -> new CartoucheBlock.RedSandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<CartoucheBlock> STONE_CARTOUCHE = registerCartoucheBlock("stone_cartouche",
			() -> new CartoucheBlock.Stone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<SymbolBlock> SANDSTONE_SYMBOL = registerBlock("sandstone_symbol",
			() -> new SymbolBlock.Sandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<SymbolBlock> RED_SANDSTONE_SYMBOL = registerBlock("red_sandstone_symbol",
			() -> new SymbolBlock.RedSandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<SymbolBlock> STONE_SYMBOL = registerBlock("stone_symbol",
			() -> new SymbolBlock.Stone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<NaquadahGeneratorMarkIBlock> NAQUADAH_GENERATOR_MARK_I = registerBlock("naquadah_generator_mark_i",
			() -> new NaquadahGeneratorMarkIBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), 1);
	public static final DeferredBlock<NaquadahGeneratorMarkIIBlock> NAQUADAH_GENERATOR_MARK_II = registerBlock("naquadah_generator_mark_ii",
			() -> new NaquadahGeneratorMarkIIBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), 1);
	
	public static final DeferredBlock<BasicInterfaceBlock> BASIC_INTERFACE = registerBlock("basic_interface",
			() -> new BasicInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)), 1);
	public static final DeferredBlock<CrystalInterfaceBlock> CRYSTAL_INTERFACE = registerBlock("crystal_interface",
			() -> new CrystalInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	public static final DeferredBlock<AdvancedCrystalInterfaceBlock> ADVANCED_CRYSTAL_INTERFACE = registerBlock("advanced_crystal_interface",
			() -> new AdvancedCrystalInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	
	public static final DeferredBlock<ATAGeneDetectorBlock> ANCIENT_GENE_DETECTOR = registerBlock("ancient_gene_detector",
			() -> new ATAGeneDetectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	
	public static final DeferredBlock<ZPMHubBlock> ZPM_HUB = registerBlock("zpm_hub",
			() -> new ZPMHubBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	//TODO ZPM Port
	//TODO ZPM Plug
	
	public static final DeferredBlock<NaquadahLiquidizerBlock> NAQUADAH_LIQUIDIZER = registerBlock("naquadah_liquidizer",
			() -> new NaquadahLiquidizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), 1);
	public static final DeferredBlock<HeavyNaquadahLiquidizerBlock> HEAVY_NAQUADAH_LIQUIDIZER = registerBlock("heavy_naquadah_liquidizer",
			() -> new HeavyNaquadahLiquidizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	
	public static final DeferredBlock<CrystallizerBlock> CRYSTALLIZER = registerBlock("crystallizer",
			() -> new CrystallizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	public static final DeferredBlock<AdvancedCrystallizerBlock> ADVANCED_CRYSTALLIZER = registerBlock("advanced_crystallizer",
			() -> new AdvancedCrystallizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);

	public static final DeferredBlock<TransceiverBlock> TRANSCEIVER = registerBlock("transceiver",
			() -> new TransceiverBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F)), 1);
	
	
	private static ToIntFunction<BlockState> litBlockEmission(int lightValue)
	{
		return (state) ->
		{
			return (Boolean) state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
		};
	}
	
	private static <T extends Block>DeferredBlock<T> registerBlock(String name, Supplier<T> block)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerBlockItem(name, toReturn);
		
		return toReturn;
	}
	private static <T extends Block>DeferredBlock<T> registerBlock(String name, Supplier<T> block, int stacksTo)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerBlockItem(name, toReturn, stacksTo);
		
		return toReturn;
	}
	private static <T extends Block>DeferredBlock<T> registerBlock(String name, Supplier<T> block, Rarity rarity, int stacksTo)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerBlockItem(name, toReturn, rarity, stacksTo);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredBlock<T> registerStargateBlock(String name, Supplier<T> block, Rarity rarity)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);

		registerStargateBlockItem(name, toReturn, rarity, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredBlock<T> registerTransporterBlock(String name, Supplier<T> block, Rarity rarity)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);

		registerTransporterBlockItem(name, toReturn, rarity, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredBlock<T> registerDHDBlock(String name, Supplier<T> block, Rarity rarity)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);

		registerDHDItem(name, toReturn, rarity, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredBlock<T> registerCartoucheBlock(String name, Supplier<T> block)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerCartoucheBlockItem(name, toReturn, 1);
		
		return toReturn;
	}
	
	private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block)
	{
		return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
	}
	
	private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	private static <T extends Block> DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	private static <T extends Block> DeferredItem<Item> registerStargateBlockItem(String name, DeferredBlock<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new StargateBlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo).fireResistant()));
	}
	
	private static <T extends Block> DeferredItem<Item> registerTransporterBlockItem(String name, DeferredBlock<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new TransporterBlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo).fireResistant()));
	}
	
	private static <T extends Block> DeferredItem<Item> registerDHDItem(String name, DeferredBlock<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new DHDItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	private static <T extends Block> DeferredItem<Item> registerCartoucheBlockItem(String name, DeferredBlock<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new CartoucheBlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	public static void register(IEventBus eventBus)
	{
		BLOCKS.register(eventBus);
	}
	
	
	
	private static boolean never(BlockState state, BlockGetter getter, BlockPos pos)
	{
		return false;
	}
}
