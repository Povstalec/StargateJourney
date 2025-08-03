package net.povstalec.sgjourney.common.init;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
import net.povstalec.sgjourney.common.blocks.tech.*;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.items.blocks.*;

//A class for initializing blocks
public class BlockInit
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StargateJourney.MODID);
	
	//Block Tab
	public static final RegistryObject<UniverseStargateBlock> UNIVERSE_STARGATE = registerStargateBlock("universe_stargate", 
			() -> new UniverseStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<UniverseStargateRingBlock> UNIVERSE_RING = BLOCKS.register("universe_ring", 
			() -> new UniverseStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<GenericShieldingBlock> UNIVERSE_SHIELDING =  BLOCKS.register("universe_shielding", 
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	
	public static final RegistryObject<MilkyWayStargateBlock> MILKY_WAY_STARGATE = registerStargateBlock("milky_way_stargate", 
			() -> new MilkyWayStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<MilkyWayStargateRingBlock> MILKY_WAY_RING = BLOCKS.register("milky_way_ring", 
			() -> new MilkyWayStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<GenericShieldingBlock> MILKY_WAY_SHIELDING =  BLOCKS.register("milky_way_shielding", 
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	
	public static final RegistryObject<PegasusStargateBlock> PEGASUS_STARGATE = registerStargateBlock("pegasus_stargate", 
			() -> new PegasusStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<PegasusStargateRingBlock> PEGASUS_RING = BLOCKS.register("pegasus_ring", 
			() -> new PegasusStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<GenericShieldingBlock> PEGASUS_SHIELDING =  BLOCKS.register("pegasus_shielding", 
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	
	public static final RegistryObject<ClassicStargateBlock> CLASSIC_STARGATE = registerStargateBlock("classic_stargate", 
			() -> new ClassicStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.UNCOMMON);
	public static final RegistryObject<ClassicStargateRingBlock> CLASSIC_RING = BLOCKS.register("classic_ring", 
			() -> new ClassicStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<GenericShieldingBlock> CLASSIC_SHIELDING =  BLOCKS.register("classic_shielding", 
			() -> new GenericShieldingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion(), 7.0D, 1.0D));
	public static final RegistryObject<ClassicStargateBaseBlock> CLASSIC_STARGATE_BASE_BLOCK = registerBlock("classic_stargate_base_block", 
			() -> new ClassicStargateBaseBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	public static final RegistryObject<Block> CLASSIC_STARGATE_CHEVRON_BLOCK = registerBlock("classic_stargate_chevron_block", 
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	public static final RegistryObject<Block> CLASSIC_STARGATE_RING_BLOCK = registerBlock("classic_stargate_ring_block", 
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)), Rarity.UNCOMMON, 64);
	
	public static final RegistryObject<TollanStargateBlock> TOLLAN_STARGATE = registerStargateBlock("tollan_stargate",
			() -> new TollanStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<TollanStargateRingBlock> TOLLAN_RING = BLOCKS.register("tollan_ring",
			() -> new TollanStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final RegistryObject<AbstractDHDBlock> MILKY_WAY_DHD = registerDHDBlock("milky_way_dhd", 
			() -> new MilkyWayDHDBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	
	public static final RegistryObject<AbstractDHDBlock> PEGASUS_DHD = registerDHDBlock("pegasus_dhd", 
			() -> new PegasusDHDBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	
	public static final RegistryObject<AbstractDHDBlock> CLASSIC_DHD = registerDHDBlock("classic_dhd", 
			() -> new ClassicDHDBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.UNCOMMON);
	
	public static final RegistryObject<ChevronBlock> UNIVERSE_STARGATE_CHEVRON = registerBlock("universe_stargate_chevron", 
			() -> new ChevronBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F)
					.requiresCorrectToolForDrops().noOcclusion().noCollission()
					.lightLevel((state) -> state.getValue(FirePitBlock.LIT) ? 7 : 0)), Rarity.UNCOMMON, 16);
	
	public static final RegistryObject<TransportRingsBlock> TRANSPORT_RINGS = registerTransporterBlock("transport_rings", 
			() -> new TransportRingsBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	public static final RegistryObject<RingPanelBlock> RING_PANEL = registerBlock("ring_panel", 
			() -> new RingPanelBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE, 1);
	
	public static final RegistryObject<FallingBlock> SULFUR_SAND = registerBlock("sulfur_sand",
			() -> new FallingBlock(BlockBehaviour.Properties.of(Material.SAND, MaterialColor.SAND).strength(0.5F).sound(SoundType.SAND)));
	
	public static final RegistryObject<BuddingUnityBlock> BUDDING_UNITY = registerBlock("budding_unity",
			() -> new BuddingUnityBlock(BlockBehaviour.Properties.of(Material.SAND, MaterialColor.SAND).strength(0.5F).sound(SoundType.SAND).randomTicks()));
	
	public static final RegistryObject<UnityClusterBlock> SMALL_UNITY_BUD = registerBlock("small_unity_bud",
			() -> new UnityClusterBlock(3, 4, BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	public static final RegistryObject<UnityClusterBlock> MEDIUM_UNITY_BUD = registerBlock("medium_unity_bud",
			() -> new UnityClusterBlock(4, 3, BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	public static final RegistryObject<UnityClusterBlock> LARGE_UNITY_BUD = registerBlock("large_unity_bud",
			() -> new UnityClusterBlock(5, 3, BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	public static final RegistryObject<UnityClusterBlock> UNITY_CLUSTER = registerBlock("unity_cluster",
			() -> new UnityClusterBlock(7, 3, BlockBehaviour.Properties.of(Material.AMETHYST, MaterialColor.DIAMOND).strength(1.5F).sound(SoundType.GLASS).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<ExplosiveBlock> NAQUADAH_ORE = registerBlock("naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final RegistryObject<ExplosiveBlock> NETHER_NAQUADAH_ORE = registerBlock("nether_naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final RegistryObject<ExplosiveBlock> DEEPSLATE_NAQUADAH_ORE = registerBlock("deepslate_naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops(), 4.0F));
	
	public static final RegistryObject<ExplosiveBlock> RAW_NAQUADAH_BLOCK = registerBlock("raw_naquadah_block", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 10.0F));
	public static final RegistryObject<ExplosiveBlock> PURE_NAQUADAH_BLOCK = registerBlock("pure_naquadah_block",
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 20.0F), Rarity.UNCOMMON, 64);
	
	public static final RegistryObject<LiquidBlock> LIQUID_NAQUADAH_BLOCK = registerBlock("liquid_naquadah", 
			() -> new LiquidBlock(FluidInit.LIQUID_NAQUADAH_SOURCE, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable()));
	public static final RegistryObject<LiquidBlock> HEAVY_LIQUID_NAQUADAH_BLOCK = registerBlock("heavy_liquid_naquadah", 
			() -> new LiquidBlock(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable()));
	
	public static final RegistryObject<Block> NAQUADAH_BLOCK = registerBlock("naquadah_block", 
			() -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> NAQUADAH_STAIRS = registerBlock("naquadah_stairs", 
			() -> new StairBlock(() -> NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> NAQUADAH_SLAB = registerBlock("naquadah_slab", 
			() -> new SlabBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<Block> CUT_NAQUADAH_BLOCK = registerBlock("cut_naquadah_block", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> CUT_NAQUADAH_STAIRS = registerBlock("cut_naquadah_stairs", 
			() -> new StairBlock(() -> NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> CUT_NAQUADAH_SLAB = registerBlock("cut_naquadah_slab", 
			() -> new SlabBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 9.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<GoldenIdolBlock> GOLDEN_IDOL = registerBlock("golden_idol", 
			() -> new GoldenIdolBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD).strength(3.0F, 6.0F)
					.sound(SoundType.METAL).requiresCorrectToolForDrops()), Rarity.UNCOMMON, 16);
	
	public static final RegistryObject<ArcheologyTableBlock> ARCHEOLOGY_TABLE = registerBlock("archeology_table", 
			() -> new ArcheologyTableBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion()));
	
	public static final RegistryObject<FirePitBlock> FIRE_PIT = registerBlock("fire_pit", 
			() -> new FirePitBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_ORANGE).instabreak()
			.sound(SoundType.STONE).lightLevel((state) -> state.getValue(FirePitBlock.LIT) ? 15 : 0), ParticleTypes.FLAME));
	
	public static final RegistryObject<Block> SANDSTONE_WITH_LAPIS = registerBlock("sandstone_with_lapis",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<RotatedPillarBlock> SANDSTONE_WITH_GOLD = registerBlock("sandstone_with_gold", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<RotatedPillarBlock> SANDSTONE_HIEROGLYPHS = registerBlock("sandstone_hieroglyphs", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<SecretSwitchBlock> SANDSTONE_SWITCH = registerBlock("sandstone_switch", 
			() -> new SecretSwitchBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<Block> RED_SANDSTONE_WITH_LAPIS = registerBlock("red_sandstone_with_lapis",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> RED_SANDSTONE_WITH_GOLD = registerBlock("red_sandstone_with_gold",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> RED_SANDSTONE_GLYPHS = registerBlock("red_sandstone_glyphs",
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));

	public static final RegistryObject<CartoucheBlock> SANDSTONE_CARTOUCHE = registerCartoucheBlock("sandstone_cartouche", 
			() -> new CartoucheBlock.Sandstone(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<CartoucheBlock> RED_SANDSTONE_CARTOUCHE = registerCartoucheBlock("red_sandstone_cartouche",
			() -> new CartoucheBlock.RedSandstone(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<CartoucheBlock> STONE_CARTOUCHE = registerCartoucheBlock("stone_cartouche", 
			() -> new CartoucheBlock.Stone(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<SymbolBlock> SANDSTONE_SYMBOL = registerBlock("sandstone_symbol",
			() -> new SymbolBlock.Sandstone(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<SymbolBlock> RED_SANDSTONE_SYMBOL = registerBlock("red_sandstone_symbol",
			() -> new SymbolBlock.RedSandstone(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<SymbolBlock> STONE_SYMBOL = registerBlock("stone_symbol", 
			() -> new SymbolBlock.Stone(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<NaquadahGeneratorMarkIBlock> NAQUADAH_GENERATOR_MARK_I = registerEnergyBlock("naquadah_generator_mark_i",
			() -> new NaquadahGeneratorMarkIBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)),
			() -> CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get(), Rarity.COMMON);
	public static final RegistryObject<NaquadahGeneratorMarkIIBlock> NAQUADAH_GENERATOR_MARK_II = registerEnergyBlock("naquadah_generator_mark_ii",
			() -> new NaquadahGeneratorMarkIIBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)),
			() -> CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get(), Rarity.COMMON);
	
	public static final RegistryObject<BasicInterfaceBlock> BASIC_INTERFACE = registerEnergyBlock("basic_interface",
			() -> new BasicInterfaceBlock(BlockBehaviour.Properties.of(Material.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)),
			() -> CommonInterfaceConfig.basic_interface_capacity.get(), Rarity.COMMON);
	public static final RegistryObject<CrystalInterfaceBlock> CRYSTAL_INTERFACE = registerEnergyBlock("crystal_interface",
			() -> new CrystalInterfaceBlock(BlockBehaviour.Properties.of(Material.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)),
			() -> CommonInterfaceConfig.crystal_interface_capacity.get(), Rarity.UNCOMMON);
	public static final RegistryObject<AdvancedCrystalInterfaceBlock> ADVANCED_CRYSTAL_INTERFACE = registerEnergyBlock("advanced_crystal_interface",
			() -> new AdvancedCrystalInterfaceBlock(BlockBehaviour.Properties.of(Material.METAL).isRedstoneConductor(BlockInit::never).strength(5.0F, 6.0F)),
			() -> CommonInterfaceConfig.advanced_crystal_interface_capacity.get(), Rarity.RARE);
	
	public static final RegistryObject<ATAGeneDetectorBlock> ANCIENT_GENE_DETECTOR = registerBlock("ancient_gene_detector", 
			() -> new ATAGeneDetectorBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	
	public static final RegistryObject<ZPMHubBlock> ZPM_HUB = registerBlock("zpm_hub", 
			() -> new ZPMHubBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), Rarity.EPIC, 1);
	//TODO ZPM Port
	//TODO ZPM Plug
	
	public static final RegistryObject<NaquadahLiquidizerBlock> NAQUADAH_LIQUIDIZER = registerBlock("naquadah_liquidizer", 
			() -> new NaquadahLiquidizerBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), 1);
	public static final RegistryObject<HeavyNaquadahLiquidizerBlock> HEAVY_NAQUADAH_LIQUIDIZER = registerBlock("heavy_naquadah_liquidizer", 
			() -> new HeavyNaquadahLiquidizerBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	
	public static final RegistryObject<CrystallizerBlock> CRYSTALLIZER = registerBlock("crystallizer", 
			() -> new CrystallizerBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), Rarity.UNCOMMON, 1);
	public static final RegistryObject<AdvancedCrystallizerBlock> ADVANCED_CRYSTALLIZER = registerBlock("advanced_crystallizer", 
			() -> new AdvancedCrystallizerBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);

	public static final RegistryObject<TransceiverBlock> TRANSCEIVER = registerBlock("transceiver", 
			() -> new TransceiverBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.0F, 6.0F)), 1);
	
	public static final RegistryObject<CableBlock> NAQUADAH_WIRE = registerBlock("naquadah_wire",
			() -> new CableBlock.NaquadahWire(BlockBehaviour.Properties.of(Material.METAL).strength(1.0F, 6.0F).noOcclusion()), 64);
	public static final RegistryObject<CableBlock> SMALL_NAQUADAH_CABLE = registerBlock("small_naquadah_cable",
			() -> new CableBlock.SmallNaquadahCable(BlockBehaviour.Properties.of(Material.METAL).strength(1.5F, 6.0F).noOcclusion().requiresCorrectToolForDrops()), 64);
	public static final RegistryObject<CableBlock> MEDIUM_NAQUADAH_CABLE = registerBlock("medium_naquadah_cable",
			() -> new CableBlock.MediumNaquadahCable(BlockBehaviour.Properties.of(Material.METAL).strength(2.0F, 6.0F).noOcclusion().requiresCorrectToolForDrops()), Rarity.UNCOMMON, 64);
	public static final RegistryObject<CableBlock> LARGE_NAQUADAH_CABLE = registerBlock("large_naquadah_cable",
			() -> new CableBlock.LargeNaquadahCable(BlockBehaviour.Properties.of(Material.METAL).strength(3.0F, 6.0F).noOcclusion().requiresCorrectToolForDrops()), Rarity.RARE, 64);
	
	public static final RegistryObject<BatteryBlock> LARGE_NAQUADAH_BATTERY = registerEnergyBlock("large_naquadah_battery",
			() -> new BatteryBlock.Naquadah(BlockBehaviour.Properties.of(Material.METAL).strength(3.0F, 6.0F)),
			() -> CommonTechConfig.large_naquadah_battery_capacity.get(), Rarity.RARE);
	
	
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
	
	private static <T extends Block>RegistryObject<T> registerTransporterBlock(String name, Supplier<T> block, Rarity rarity)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);

		registerTransporterBlockItem(name, toReturn, rarity, 1);
		
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
	
	private static <T extends Block>RegistryObject<T> registerEnergyBlock(String name, Supplier<T> block, EnergyBlockItem.CapacityGetter getter, Rarity rarity)
	{
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		
		registerEnergyBlockItem(name, toReturn, getter, rarity);
		
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
	
	private static <T extends Block>RegistryObject<Item> registerTransporterBlockItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new TransporterBlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo).fireResistant()));
	}
	
	private static <T extends Block>RegistryObject<Item> registerDHDItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new DHDItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerCartoucheBlockItem(String name, RegistryObject<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new CartoucheBlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerEnergyBlockItem(String name, RegistryObject<T> block, EnergyBlockItem.CapacityGetter getter, Rarity rarity)
	{
		return ItemInit.ITEMS.register(name, () -> new EnergyBlockItem.Getter(block.get(), new Item.Properties().rarity(rarity).stacksTo(1), getter));
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
