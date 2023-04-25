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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.ATAGeneDetectorBlock;
import net.povstalec.sgjourney.common.blocks.ArcheologyTableBlock;
import net.povstalec.sgjourney.common.blocks.BasicInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.CrystalInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.CrystallizerBlock;
import net.povstalec.sgjourney.common.blocks.ExplosiveBlock;
import net.povstalec.sgjourney.common.blocks.FirePitBlock;
import net.povstalec.sgjourney.common.blocks.GoldenIdolBlock;
import net.povstalec.sgjourney.common.blocks.NaquadahGeneratorMarkIBlock;
import net.povstalec.sgjourney.common.blocks.NaquadahGeneratorMarkIIBlock;
import net.povstalec.sgjourney.common.blocks.RingPanelBlock;
import net.povstalec.sgjourney.common.blocks.SecretSwitchBlock;
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
import net.povstalec.sgjourney.common.blocks.stargate.UniverseStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.UniverseStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.symbols.SandstoneCartoucheBlock;
import net.povstalec.sgjourney.common.blocks.symbols.SandstoneSymbolBlock;
import net.povstalec.sgjourney.common.blocks.symbols.StoneCartoucheBlock;
import net.povstalec.sgjourney.common.blocks.symbols.StoneSymbolBlock;
import net.povstalec.sgjourney.common.items.DHDItem;
import net.povstalec.sgjourney.common.items.SGJourneyBlockItem;

//A class for initializing blocks
public class BlockInit
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StargateJourney.MODID);
	
	//Block Tab
	public static final RegistryObject<UniverseStargateBlock> UNIVERSE_STARGATE = registerEntityBlock("universe_stargate", 
			() -> new UniverseStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<UniverseStargateRingBlock> UNIVERSE_RING = BLOCKS.register("universe_ring", 
			() -> new UniverseStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<MilkyWayStargateBlock> MILKY_WAY_STARGATE = registerEntityBlock("milky_way_stargate", 
			() -> new MilkyWayStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<MilkyWayStargateRingBlock> MILKY_WAY_RING = BLOCKS.register("milky_way_ring", 
			() -> new MilkyWayStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<PegasusStargateBlock> PEGASUS_STARGATE = registerEntityBlock("pegasus_stargate", 
			() -> new PegasusStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<PegasusStargateRingBlock> PEGASUS_RING = BLOCKS.register("pegasus_ring", 
			() -> new PegasusStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final RegistryObject<ClassicStargateBlock> CLASSIC_STARGATE = registerEntityBlock("classic_stargate", 
			() -> new ClassicStargateBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	public static final RegistryObject<ClassicStargateRingBlock> CLASSIC_RING = BLOCKS.register("classic_ring", 
			() -> new ClassicStargateRingBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F, 1200.0F)
					.sound(SoundType.METAL).noOcclusion()));
	
	public static final RegistryObject<AbstractDHDBlock> MILKY_WAY_DHD = registerDHDBlock("milky_way_dhd", 
			() -> new MilkyWayDHDBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	public static final RegistryObject<AbstractDHDBlock> PEGASUS_DHD = registerDHDBlock("pegasus_dhd", 
			() -> new PegasusDHDBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.EPIC);
	
	public static final RegistryObject<AbstractDHDBlock> CLASSIC_DHD = registerDHDBlock("classic_dhd", 
			() -> new ClassicDHDBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.COMMON);
	
	public static final RegistryObject<TransportRingsBlock> TRANSPORT_RINGS = registerEntityBlock("transport_rings", 
			() -> new TransportRingsBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE);
	public static final RegistryObject<RingPanelBlock> RING_PANEL = registerBlock("ring_panel", 
			() -> new RingPanelBlock(BlockBehaviour.Properties.of(Material.METAL).strength(6.0F)
					.sound(SoundType.METAL).noOcclusion()), Rarity.RARE, 1);
	
	public static final RegistryObject<ExplosiveBlock> NAQUADAH_ORE = registerBlock("naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final RegistryObject<ExplosiveBlock> NETHER_NAQUADAH_ORE = registerBlock("nether_naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE).strength(3.0F).requiresCorrectToolForDrops(), 4.0F));
	public static final RegistryObject<ExplosiveBlock> DEEPSLATE_NAQUADAH_ORE = registerBlock("deepslate_naquadah_ore", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops(), 4.0F));
	
	public static final RegistryObject<ExplosiveBlock> RAW_NAQUADAH_BLOCK = registerBlock("raw_naquadah_block", 
			() -> new ExplosiveBlock(BlockBehaviour.Properties.of(Material.STONE).strength(5.0F, 6.0F).requiresCorrectToolForDrops(), 10.0F));
	
	public static final RegistryObject<LiquidBlock> LIQUID_NAQUADAH_BLOCK = registerBlock("liquid_naquadah", 
			() -> new LiquidBlock(FluidInit.LIQUID_NAQUADAH_SOURCE, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable()));
	
	public static final RegistryObject<Block> NAQUADAH_BLOCK = registerBlock("naquadah_block", 
			() -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> NAQUADAH_STAIRS = registerBlock("naquadah_stairs", 
			() -> new StairBlock(() -> NAQUADAH_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> NAQUADAH_SLAB = registerBlock("naquadah_slab", 
			() -> new SlabBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 600.0F).requiresCorrectToolForDrops()));
	
	public static final RegistryObject<GoldenIdolBlock> GOLDEN_IDOL = registerBlock("golden_idol", 
			() -> new GoldenIdolBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD).strength(3.0F, 6.0F)
					.sound(SoundType.METAL).requiresCorrectToolForDrops()), Rarity.UNCOMMON, 1);
	
	public static final RegistryObject<ArcheologyTableBlock> ARCHEOLOGY_TABLE = registerBlock("archeology_table", 
			() -> new ArcheologyTableBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F)
					.sound(SoundType.WOOD)
					.noOcclusion()));
	
	public static final RegistryObject<FirePitBlock> FIRE_PIT = registerBlock("fire_pit", 
			() -> new FirePitBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.TERRACOTTA_ORANGE).instabreak()
			.sound(SoundType.STONE).lightLevel((state) -> state.getValue(FirePitBlock.LIT) ? 15 : 0), ParticleTypes.FLAME));
	public static final RegistryObject<Block> SANDSTONE_WITH_LAPIS = registerBlock("sandstone_with_lapis", 
			() -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<Block> SANDSTONE_HIEROGLYPHS = registerBlock("sandstone_hieroglyphs", 
			() -> new Block(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));
	public static final RegistryObject<SecretSwitchBlock> SANDSTONE_SWITCH = registerBlock("sandstone_switch", 
			() -> new SecretSwitchBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()));

	public static final RegistryObject<SandstoneCartoucheBlock> SANDSTONE_CARTOUCHE = registerBlock("sandstone_cartouche", 
			() -> new SandstoneCartoucheBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops()), 1);
	public static final RegistryObject<StoneCartoucheBlock> STONE_CARTOUCHE = registerBlock("stone_cartouche", 
			() -> new StoneCartoucheBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()), 1);
	
	public static final RegistryObject<StoneSymbolBlock> STONE_SYMBOL = registerBlock("stone_symbol", 
			() -> new StoneSymbolBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.8F).requiresCorrectToolForDrops().noOcclusion()));
	public static final RegistryObject<SandstoneSymbolBlock> SANDSTONE_SYMBOL = registerBlock("sandstone_symbol", 
			() -> new SandstoneSymbolBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops().noOcclusion()));
	
	public static final RegistryObject<NaquadahGeneratorMarkIBlock> NAQUADAH_GENERATOR_MARK_I = registerBlock("naquadah_generator_mark_i", 
			() -> new NaquadahGeneratorMarkIBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), 1);
	public static final RegistryObject<NaquadahGeneratorMarkIIBlock> NAQUADAH_GENERATOR_MARK_II = registerBlock("naquadah_generator_mark_ii", 
			() -> new NaquadahGeneratorMarkIIBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), 1);
	
	public static final RegistryObject<BasicInterfaceBlock> BASIC_INTERFACE = registerBlock("basic_interface", 
			() -> new BasicInterfaceBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), 1);
	public static final RegistryObject<CrystalInterfaceBlock> CRYSTAL_INTERFACE = registerBlock("crystal_interface", 
			() -> new CrystalInterfaceBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), 1);
	
	public static final RegistryObject<ATAGeneDetectorBlock> ANCIENT_GENE_DETECTOR = registerBlock("ancient_gene_detector", 
			() -> new ATAGeneDetectorBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	
	public static final RegistryObject<ZPMHubBlock> ZPM_HUB = registerBlock("zpm_hub", 
			() -> new ZPMHubBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), Rarity.RARE, 1);
	// ZPM Plug
	
	public static final RegistryObject<CrystallizerBlock> CRYSTALLIZER = registerBlock("crystallizer", 
			() -> new CrystallizerBlock(BlockBehaviour.Properties.of(Material.METAL).strength(5.0F, 6.0F)), 1);
	
	
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
	
	private static <T extends Block>RegistryObject<Item> registerSGJourneyBlockItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new SGJourneyBlockItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerSGJourneyBlockItem(String name, RegistryObject<T> block, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new SGJourneyBlockItem(block.get(), new Item.Properties().stacksTo(stacksTo)));
	}
	
	private static <T extends Block>RegistryObject<Item> registerDHDItem(String name, RegistryObject<T> block, Rarity rarity, int stacksTo)
	{
		return ItemInit.ITEMS.register(name, () -> new DHDItem(block.get(), new Item.Properties().rarity(rarity).stacksTo(stacksTo)));
	}
	
	public static void register(IEventBus eventBus)
	{
		BLOCKS.register(eventBus);
	}
}
