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
import net.povstalec.sgjourney.common.blocks.stargate.*;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.GenericShieldingBlock;
import net.povstalec.sgjourney.common.blocks.tech.*;
import net.povstalec.sgjourney.common.blocks.tech_interface.AdvancedCrystalInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech_interface.BasicInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.tech_interface.CrystalInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.transporter.AncientTransportRingsBlock;
import net.povstalec.sgjourney.common.blocks.transporter.GoauldTransportRingsBlock;
import net.povstalec.sgjourney.common.blocks.transporter_controller.GoauldRingPanelBlock;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.items.blocks.*;

public class BlockInit
{
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(StargateJourney.MODID);
	
	// Stargates
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
	
	// DHDs
	public static final DeferredBlock<AbstractDHDBlock> MILKY_WAY_DHD = registerDHDBlock("milky_way_dhd",
			() -> new MilkyWayDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	
	public static final DeferredBlock<AbstractDHDBlock> PEGASUS_DHD = registerDHDBlock("pegasus_dhd",
			() -> new PegasusDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	
	public static final DeferredBlock<AbstractDHDBlock> CLASSIC_DHD = registerDHDBlock("classic_dhd",
			() -> new ClassicDHDBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.UNCOMMON);
	
	public static final DeferredBlock<ChevronBlock> UNIVERSE_STARGATE_CHEVRON = registerBlock("universe_stargate_chevron",
			() -> new ChevronBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F)
					.requiresCorrectToolForDrops().noOcclusion().noCollission()
					.lightLevel(litBlockEmission(7))), Rarity.UNCOMMON, 16);
	// Transporters
	public static final DeferredBlock<AncientTransportRingsBlock> ANCIENT_TRANSPORT_RINGS = registerTransporterBlock("ancient_transport_rings",
			() -> new AncientTransportRingsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	public static final DeferredBlock<GoauldTransportRingsBlock> GOAULD_TRANSPORT_RINGS = registerTransporterBlock("goauld_transport_rings",
			() -> new GoauldTransportRingsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	// Transporter Controllers
	public static final DeferredBlock<GoauldRingPanelBlock> GOAULD_RING_PANEL = registerTransporterControllerBlock("goauld_ring_panel",
			() -> new GoauldRingPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	// Natural Blocks
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
	
	// Ores
	public static final DeferredBlock<NaquadriaOreBlock> NAQUADRIA_ORE = registerBlock("naquadria_ore",
			() -> new NaquadriaOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), 16.0F));
	public static final DeferredBlock<NaquadriaOreBlock> NETHER_NAQUADRIA_ORE = registerBlock("nether_naquadria_ore",
			() -> new NaquadriaOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), 16.0F));
	public static final DeferredBlock<NaquadriaOreBlock> DEEPSLATE_NAQUADRIA_ORE = registerBlock("deepslate_naquadria_ore",
			() -> new NaquadriaOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops(), 16.0F));
	public static final DeferredBlock<NaquadahOreBlock> NAQUADAH_ORE = registerBlock("naquadah_ore",
			() -> new NaquadahOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), NAQUADRIA_ORE::get, 4.0F));
	public static final DeferredBlock<NaquadahOreBlock> NETHER_NAQUADAH_ORE = registerBlock("nether_naquadah_ore",
			() -> new NaquadahOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops(), NETHER_NAQUADRIA_ORE::get, 4.0F));
	public static final DeferredBlock<NaquadahOreBlock> DEEPSLATE_NAQUADAH_ORE = registerBlock("deepslate_naquadah_ore",
			() -> new NaquadahOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops(), DEEPSLATE_NAQUADRIA_ORE::get, 4.0F));
	public static final DeferredBlock<Block> TRINIUM_ORE = registerBlock("trinium_ore",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> NETHER_TRINIUM_ORE = registerBlock("nether_trinium_ore",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(3.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> DEEPSLATE_TRINIUM_ORE = registerBlock("deepslate_trinium_ore",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
	// Raw Blocks
	public static final DeferredBlock<NaquadriaOreBlock> RAW_NAQUADRIA_BLOCK = registerBlock("raw_naquadria_block",
			() -> new NaquadriaOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 40.0F));
	public static final DeferredBlock<NaquadahOreBlock> RAW_NAQUADAH_BLOCK = registerBlock("raw_naquadah_block",
			() -> new NaquadahOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), RAW_NAQUADRIA_BLOCK::get, 10.0F));
	public static final DeferredBlock<ExplosiveBlock> PURE_NAQUADAH_BLOCK = registerBlock("pure_naquadah_block",
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 20.0F), Rarity.UNCOMMON, 64);
	public static final DeferredBlock<Block> RAW_TRINIUM_BLOCK = registerBlock("raw_trinium_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	// Liquids
	public static final DeferredBlock<LiquidBlock> LIQUID_NAQUADAH_BLOCK = registerBlock("liquid_naquadah",
			() -> new LiquidBlock(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()));
	public static final DeferredBlock<LiquidBlock> HEAVY_LIQUID_NAQUADAH_BLOCK = registerBlock("heavy_liquid_naquadah",
			() -> new LiquidBlock(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().strength(100.0F).noLootTable()));
	// Naquadah Blocks
	public static final DeferredBlock<Block> NAQUADAH_BLOCK = registerBlock("naquadah_block",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> NAQUADAH_STAIRS = registerBlock("naquadah_stairs",
			() -> new StairBlock(NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> NAQUADAH_SLAB = registerBlock("naquadah_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CUT_NAQUADAH_BLOCK = registerBlock("cut_naquadah_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_NAQUADAH_STAIRS = registerBlock("cut_naquadah_stairs",
			() -> new StairBlock(CUT_NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_NAQUADAH_SLAB = registerBlock("cut_naquadah_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> NAQUADAH_PILLAR = registerBlock("naquadah_pillar",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> POLISHED_NAQUADAH_BLOCK = registerBlock("polished_naquadah_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> POLISHED_NAQUADAH_STAIRS = registerBlock("polished_naquadah_stairs",
			() -> new StairBlock(POLISHED_NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> POLISHED_NAQUADAH_SLAB = registerBlock("polished_naquadah_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CHISELED_NAQUADAH_BLOCK = registerBlock("chiseled_naquadah_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> SMOOTH_NAQUADAH_BLOCK = registerBlock("smooth_naquadah_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> SMOOTH_NAQUADAH_STAIRS = registerBlock("smooth_naquadah_stairs",
			() -> new StairBlock(SMOOTH_NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> SMOOTH_NAQUADAH_SLAB = registerBlock("smooth_naquadah_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.0F, 6.0F).requiresCorrectToolForDrops()));
	// Naquadah-Copper Blocks
	public static final DeferredBlock<WeatheringRotatedPillarBlock> NAQUADAH_COPPER_BLOCK = registerBlock("naquadah_copper_block",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringRotatedPillarBlock> EXPOSED_NAQUADAH_COPPER_BLOCK = registerBlock("exposed_naquadah_copper_block",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringRotatedPillarBlock> WEATHERED_NAQUADAH_COPPER_BLOCK = registerBlock("weathered_naquadah_copper_block",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringRotatedPillarBlock> OXIDIZED_NAQUADAH_COPPER_BLOCK = registerBlock("oxidized_naquadah_copper_block",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedRotatedPillarBlock> WAXED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_naquadah_copper_block",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedRotatedPillarBlock> WAXED_EXPOSED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_exposed_naquadah_copper_block",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedRotatedPillarBlock> WAXED_WEATHERED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_weathered_naquadah_copper_block",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedRotatedPillarBlock> WAXED_OXIDIZED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_oxidized_naquadah_copper_block",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringStairBlock> NAQUADAH_COPPER_STAIRS = registerBlock("naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> EXPOSED_NAQUADAH_COPPER_STAIRS = registerBlock("exposed_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, EXPOSED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> WEATHERED_NAQUADAH_COPPER_STAIRS = registerBlock("weathered_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, WEATHERED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> OXIDIZED_NAQUADAH_COPPER_STAIRS = registerBlock("oxidized_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, OXIDIZED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_EXPOSED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_exposed_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_EXPOSED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_WEATHERED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_weathered_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_WEATHERED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_OXIDIZED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_oxidized_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_OXIDIZED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringSlabBlock> NAQUADAH_COPPER_SLAB = registerBlock("naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> EXPOSED_NAQUADAH_COPPER_SLAB = registerBlock("exposed_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> WEATHERED_NAQUADAH_COPPER_SLAB = registerBlock("weathered_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> OXIDIZED_NAQUADAH_COPPER_SLAB = registerBlock("oxidized_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_EXPOSED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_exposed_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_WEATHERED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_weathered_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_OXIDIZED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_oxidized_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringFullBlock> CUT_NAQUADAH_COPPER_BLOCK = registerBlock("cut_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> EXPOSED_CUT_NAQUADAH_COPPER_BLOCK = registerBlock("exposed_cut_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> WEATHERED_CUT_NAQUADAH_COPPER_BLOCK = registerBlock("weathered_cut_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK = registerBlock("oxidized_cut_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_CUT_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_cut_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_EXPOSED_CUT_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_exposed_cut_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_WEATHERED_CUT_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_weathered_cut_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_oxidized_cut_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringStairBlock> CUT_NAQUADAH_COPPER_STAIRS = registerBlock("cut_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> EXPOSED_CUT_NAQUADAH_COPPER_STAIRS = registerBlock("exposed_cut_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> WEATHERED_CUT_NAQUADAH_COPPER_STAIRS = registerBlock("weathered_cut_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> OXIDIZED_CUT_NAQUADAH_COPPER_STAIRS = registerBlock("oxidized_cut_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_CUT_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_cut_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_EXPOSED_CUT_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_exposed_cut_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_WEATHERED_CUT_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_weathered_cut_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_oxidized_cut_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringSlabBlock> CUT_NAQUADAH_COPPER_SLAB = registerBlock("cut_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> EXPOSED_CUT_NAQUADAH_COPPER_SLAB = registerBlock("exposed_cut_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> WEATHERED_CUT_NAQUADAH_COPPER_SLAB = registerBlock("weathered_cut_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> OXIDIZED_CUT_NAQUADAH_COPPER_SLAB = registerBlock("oxidized_cut_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_CUT_NAQUADAH_COPPER_SLAB = registerBlock("waxed_cut_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_EXPOSED_CUT_NAQUADAH_COPPER_SLAB = registerBlock("waxed_exposed_cut_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_WEATHERED_CUT_NAQUADAH_COPPER_SLAB = registerBlock("waxed_weathered_cut_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_SLAB = registerBlock("waxed_oxidized_cut_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> NAQUADAH_COPPER_PILLAR = registerBlock("naquadah_copper_pillar",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> EXPOSED_NAQUADAH_COPPER_PILLAR = registerBlock("exposed_naquadah_copper_pillar",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WEATHERED_NAQUADAH_COPPER_PILLAR = registerBlock("weathered_naquadah_copper_pillar",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> OXIDIZED_NAQUADAH_COPPER_PILLAR = registerBlock("oxidized_naquadah_copper_pillar",
			() -> new WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_NAQUADAH_COPPER_PILLAR = registerBlock("waxed_naquadah_copper_pillar",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_EXPOSED_NAQUADAH_COPPER_PILLAR = registerBlock("waxed_exposed_naquadah_copper_pillar",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_WEATHERED_NAQUADAH_COPPER_PILLAR = registerBlock("waxed_weathered_naquadah_copper_pillar",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_OXIDIZED_NAQUADAH_COPPER_PILLAR = registerBlock("waxed_oxidized_naquadah_copper_pillar",
			() -> new WaxedRotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringFullBlock> POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("polished_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("exposed_polished_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("weathered_polished_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("oxidized_polished_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_polished_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_exposed_polished_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_weathered_polished_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_oxidized_polished_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringStairBlock> POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("polished_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> EXPOSED_POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("exposed_polished_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> WEATHERED_POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("weathered_polished_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> OXIDIZED_POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("oxidized_polished_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_polished_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_exposed_polished_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_weathered_polished_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_oxidized_polished_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringSlabBlock> POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("polished_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("exposed_polished_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("weathered_polished_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> OXIDIZED_POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("oxidized_polished_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_polished_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_exposed_polished_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_weathered_polished_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_SLAB = registerBlock("waxed_oxidized_polished_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("chiseled_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> EXPOSED_CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("exposed_chiseled_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WEATHERED_CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("weathered_chiseled_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> OXIDIZED_CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("oxidized_chiseled_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_chiseled_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_EXPOSED_CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_exposed_chiseled_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_WEATHERED_CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_weathered_chiseled_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> WAXED_OXIDIZED_CHISELED_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_oxidized_chiseled_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringPillarLampBlock> NAQUADAH_COPPER_LAMP = registerBlock("naquadah_copper_lamp",
			() -> new WeatheringPillarLampBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 15));
	public static final DeferredBlock<WeatheringPillarLampBlock> EXPOSED_NAQUADAH_COPPER_LAMP = registerBlock("exposed_naquadah_copper_lamp",
			() -> new WeatheringPillarLampBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 12));
	public static final DeferredBlock<WeatheringPillarLampBlock> WEATHERED_NAQUADAH_COPPER_LAMP = registerBlock("weathered_naquadah_copper_lamp",
			() -> new WeatheringPillarLampBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 9));
	public static final DeferredBlock<WeatheringPillarLampBlock> OXIDIZED_NAQUADAH_COPPER_LAMP = registerBlock("oxidized_naquadah_copper_lamp",
			() -> new WeatheringPillarLampBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 6));
	public static final DeferredBlock<WaxedPillarLampBlock> WAXED_NAQUADAH_COPPER_LAMP = registerBlock("waxed_naquadah_copper_lamp",
			() -> new WaxedPillarLampBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 15));
	public static final DeferredBlock<WaxedPillarLampBlock> WAXED_EXPOSED_NAQUADAH_COPPER_LAMP = registerBlock("waxed_exposed_naquadah_copper_lamp",
			() -> new WaxedPillarLampBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 12));
	public static final DeferredBlock<WaxedPillarLampBlock> WAXED_WEATHERED_NAQUADAH_COPPER_LAMP = registerBlock("waxed_weathered_naquadah_copper_lamp",
			() -> new WaxedPillarLampBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 9));
	public static final DeferredBlock<WaxedPillarLampBlock> WAXED_OXIDIZED_NAQUADAH_COPPER_LAMP = registerBlock("waxed_oxidized_naquadah_copper_lamp",
			() -> new WaxedPillarLampBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 6));
	
	public static final DeferredBlock<WeatheringFullBlock> SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("smooth_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("exposed_smooth_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("weathered_smooth_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringFullBlock> OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("oxidized_smooth_naquadah_copper_block",
			() -> new WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_smooth_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_exposed_smooth_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_weathered_smooth_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedFullBlock> WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK = registerBlock("waxed_oxidized_smooth_naquadah_copper_block",
			() -> new WaxedFullBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringStairBlock> SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("smooth_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("exposed_smooth_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("weathered_smooth_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringStairBlock> OXIDIZED_SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("oxidized_smooth_naquadah_copper_stairs",
			() -> new WeatheringStairBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_smooth_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_exposed_smooth_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_weathered_smooth_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedStairBlock> WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_STAIRS = registerBlock("waxed_oxidized_smooth_naquadah_copper_stairs",
			() -> new WaxedStairBlock(WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<WeatheringSlabBlock> SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("smooth_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.UNAFFECTED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("exposed_smooth_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.EXPOSED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("weathered_smooth_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.WEATHERED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WeatheringSlabBlock> OXIDIZED_SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("oxidized_smooth_naquadah_copper_slab",
			() -> new WeatheringSlabBlock(SGJourneyWeatheringBlock.WeatherState.OXIDIZED, BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("waxed_smooth_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("waxed_exposed_smooth_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("waxed_weathered_smooth_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<WaxedSlabBlock> WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_SLAB = registerBlock("waxed_oxidized_smooth_naquadah_copper_slab",
			() -> new WaxedSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(5.0F, 6.0F).requiresCorrectToolForDrops()));
	// Naquadah-Iron Blocks
	public static final DeferredBlock<Block> NAQUADAH_IRON_BLOCK = registerBlock("naquadah_iron_block",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> NAQUADAH_IRON_STAIRS = registerBlock("naquadah_iron_stairs",
			() -> new StairBlock(NAQUADAH_IRON_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> NAQUADAH_IRON_SLAB = registerBlock("naquadah_iron_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CUT_NAQUADAH_IRON_BLOCK = registerBlock("cut_naquadah_iron_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_NAQUADAH_IRON_STAIRS = registerBlock("cut_naquadah_iron_stairs",
			() -> new StairBlock(CUT_NAQUADAH_IRON_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_NAQUADAH_IRON_SLAB = registerBlock("cut_naquadah_iron_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> NAQUADAH_IRON_PILLAR = registerBlock("naquadah_iron_pillar",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> POLISHED_NAQUADAH_IRON_BLOCK = registerBlock("polished_naquadah_iron_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> POLISHED_NAQUADAH_IRON_STAIRS = registerBlock("polished_naquadah_iron_stairs",
			() -> new StairBlock(POLISHED_NAQUADAH_IRON_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> POLISHED_NAQUADAH_IRON_SLAB = registerBlock("polished_naquadah_iron_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CHISELED_NAQUADAH_IRON_BLOCK = registerBlock("chiseled_naquadah_iron_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> SMOOTH_NAQUADAH_IRON_BLOCK = registerBlock("smooth_naquadah_iron_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> SMOOTH_NAQUADAH_IRON_STAIRS = registerBlock("smooth_naquadah_iron_stairs",
			() -> new StairBlock(SMOOTH_NAQUADAH_IRON_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> SMOOTH_NAQUADAH_IRON_SLAB = registerBlock("smooth_naquadah_iron_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	// Trinium Blocks
	public static final DeferredBlock<Block> TRINIUM_BLOCK = registerBlock("trinium_block",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> TRINIUM_STAIRS = registerBlock("trinium_stairs",
			() -> new StairBlock(TRINIUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> TRINIUM_SLAB = registerBlock("trinium_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CUT_TRINIUM_BLOCK = registerBlock("cut_trinium_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_TRINIUM_STAIRS = registerBlock("cut_trinium_stairs",
			() -> new StairBlock(CUT_TRINIUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> CUT_TRINIUM_SLAB = registerBlock("cut_trinium_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> TRINIUM_PILLAR = registerBlock("trinium_pillar",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> POLISHED_TRINIUM_BLOCK = registerBlock("polished_trinium_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> POLISHED_TRINIUM_STAIRS = registerBlock("polished_trinium_stairs",
			() -> new StairBlock(POLISHED_TRINIUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> POLISHED_TRINIUM_SLAB = registerBlock("polished_trinium_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> CHISELED_TRINIUM_BLOCK = registerBlock("chiseled_trinium_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final DeferredBlock<Block> SMOOTH_TRINIUM_BLOCK = registerBlock("smooth_trinium_block",
			() -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> SMOOTH_TRINIUM_STAIRS = registerBlock("smooth_trinium_stairs",
			() -> new StairBlock(SMOOTH_TRINIUM_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<Block> SMOOTH_TRINIUM_SLAB = registerBlock("smooth_trinium_slab",
			() -> new SlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(7.0F, 6.0F).requiresCorrectToolForDrops()));
	// Archeology Blocks
	public static final DeferredBlock<GoldenIdolBlock> GOLDEN_IDOL = registerBlock("golden_idol",
			() -> new GoldenIdolBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(3.0F, 6.0F)
					.sound(SoundType.METAL).requiresCorrectToolForDrops()), Rarity.UNCOMMON, 16);
	
	public static final DeferredBlock<ArcheologyTableBlock> ARCHEOLOGY_TABLE = registerBlock("archeology_table",
			() -> new ArcheologyTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion()));
	// Decoration Blocks
	public static final DeferredBlock<FirePitBlock> FIRE_PIT = registerBlock("fire_pit",
			() -> new FirePitBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).instabreak()
			.sound(SoundType.STONE), 15, ParticleTypes.FLAME));
	
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
	// Cartouches
	public static final DeferredBlock<CartoucheBlock> SANDSTONE_CARTOUCHE = registerCartoucheBlock("sandstone_cartouche",
			() -> new CartoucheBlock.Sandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<CartoucheBlock> RED_SANDSTONE_CARTOUCHE = registerCartoucheBlock("red_sandstone_cartouche",
			() -> new CartoucheBlock.RedSandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<CartoucheBlock> STONE_CARTOUCHE = registerCartoucheBlock("stone_cartouche",
			() -> new CartoucheBlock.Stone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	// Symbols
	public static final DeferredBlock<SymbolBlock> SANDSTONE_SYMBOL = registerBlock("sandstone_symbol",
			() -> new SymbolBlock.Sandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<SymbolBlock> RED_SANDSTONE_SYMBOL = registerBlock("red_sandstone_symbol",
			() -> new SymbolBlock.RedSandstone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final DeferredBlock<SymbolBlock> STONE_SYMBOL = registerBlock("stone_symbol",
			() -> new SymbolBlock.Stone(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	// Tech
	public static final DeferredBlock<NaquadahReactorBlock> NAQUADAH_REACTOR = registerEnergyBlock("naquadah_reactor",
			() -> new NaquadahReactorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)),
			() -> CommonNaquadahGeneratorConfig.naquadah_reactor_capacity.get(), Rarity.COMMON);
	public static final DeferredBlock<NaquadahGeneratorMarkIBlock> NAQUADAH_GENERATOR_MARK_I = registerEnergyBlock("naquadah_generator_mark_i",
			() -> new NaquadahGeneratorMarkIBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F)),
			() -> CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get(), Rarity.COMMON);
	public static final DeferredBlock<NaquadahGeneratorMarkIIBlock> NAQUADAH_GENERATOR_MARK_II = registerEnergyBlock("naquadah_generator_mark_ii",
			() -> new NaquadahGeneratorMarkIIBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F).forceSolidOn()),
			() -> CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get(), Rarity.COMMON);
	
	public static final DeferredBlock<BasicInterfaceBlock> BASIC_INTERFACE = registerInterfaceBlock("basic_interface",
			() -> new BasicInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)),
			() -> CommonInterfaceConfig.basic_interface_capacity.get(), Rarity.COMMON);
	public static final DeferredBlock<CrystalInterfaceBlock> CRYSTAL_INTERFACE = registerInterfaceBlock("crystal_interface",
			() -> new CrystalInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)),
			() -> CommonInterfaceConfig.crystal_interface_capacity.get(), Rarity.UNCOMMON);
	public static final DeferredBlock<AdvancedCrystalInterfaceBlock> ADVANCED_CRYSTAL_INTERFACE = registerInterfaceBlock("advanced_crystal_interface",
			() -> new AdvancedCrystalInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)),
			() -> CommonInterfaceConfig.advanced_crystal_interface_capacity.get(), Rarity.RARE);
	
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
	
	public static final DeferredBlock<BatteryBlock> LARGE_NAQUADAH_BATTERY = registerEnergyBlock("large_naquadah_battery",
			() -> new BatteryBlock.Naquadah(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F)),
			() -> CommonTechConfig.large_naquadah_battery_capacity.get(), Rarity.RARE);
	// Cables
	public static final DeferredBlock<CableBlock> NAQUADAH_WIRE = registerBlock("naquadah_wire",
			() -> new CableBlock.NaquadahWire(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0F, 6.0F).noOcclusion()), 64);
	public static final DeferredBlock<CableBlock> SMALL_NAQUADAH_CABLE = registerBlock("small_naquadah_cable",
			() -> new CableBlock.SmallNaquadahCable(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F, 6.0F).noOcclusion().requiresCorrectToolForDrops()), 64);
	public static final DeferredBlock<CableBlock> MEDIUM_NAQUADAH_CABLE = registerBlock("medium_naquadah_cable",
			() -> new CableBlock.MediumNaquadahCable(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).noOcclusion().requiresCorrectToolForDrops()), Rarity.UNCOMMON, 64);
	public static final DeferredBlock<CableBlock> LARGE_NAQUADAH_CABLE = registerBlock("large_naquadah_cable",
			() -> new CableBlock.LargeNaquadahCable(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F, 6.0F).noOcclusion().requiresCorrectToolForDrops()), Rarity.RARE, 64);
	
	
	private static ToIntFunction<BlockState> litBlockEmission(int lightValue)
	{
		return state -> state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
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
	
	private static <T extends Block>DeferredBlock<T> registerTransporterControllerBlock(String name, Supplier<T> block, Rarity rarity)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerTransporterControllerItem(name, toReturn, rarity, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredBlock<T> registerCartoucheBlock(String name, Supplier<T> block)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerCartoucheBlockItem(name, toReturn, 1);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredBlock<T> registerEnergyBlock(String name, Supplier<T> block, EnergyBlockItem.CapacityGetter getter, Rarity rarity)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerEnergyBlockItem(name, toReturn, getter, rarity);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredBlock<T> registerInterfaceBlock(String name, Supplier<T> block, EnergyBlockItem.CapacityGetter getter, Rarity rarity)
	{
		DeferredBlock<T> toReturn = BLOCKS.register(name, block);
		
		registerInterfaceBlockItem(name, toReturn, getter, rarity);
		
		return toReturn;
	}
	
	private static <T extends Block>DeferredItem<Item> registerBlockItem(String name, DeferredBlock<T> block)
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
	
	private static <T extends Block>DeferredItem<Item> registerTransporterControllerItem(String name, DeferredBlock<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new TransporterControllerItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	private static <T extends Block>DeferredItem<Item> registerCartoucheBlockItem(String name, DeferredBlock<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new CartoucheBlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	private static <T extends Block>DeferredItem<Item> registerEnergyBlockItem(String name, DeferredBlock<T> block, EnergyBlockItem.CapacityGetter getter, Rarity rarity)
	{
		return ItemInit.ITEMS.register(name, () -> new EnergyBlockItem.Getter(block.get(), new Item.Properties().rarity(rarity).stacksTo(1), getter));
	}
	
	private static <T extends Block>DeferredItem<Item> registerInterfaceBlockItem(String name, DeferredBlock<T> block, EnergyBlockItem.CapacityGetter getter, Rarity rarity)
	{
		return ItemInit.ITEMS.register(name, () -> new EnergyBlockItem.Getter(block.get(), new Item.Properties().rarity(rarity).stacksTo(1), getter, "tooltip.sgjourney.energy_buffer"));
	}
	
	public static void register(IEventBus eventBus)
	{
		BLOCKS.register(eventBus);
		
		// Aliases
		BLOCKS.addAlias(StargateJourney.sgjourneyLocation("transport_rings"), StargateJourney.sgjourneyLocation("goauld_transport_rings"));
		BLOCKS.addAlias(StargateJourney.sgjourneyLocation("ring_panel"), StargateJourney.sgjourneyLocation("goauld_ring_panel"));
	}
	
	
	
	private static boolean never(BlockState state, BlockGetter getter, BlockPos pos)
	{
		return false;
	}
}
