package net.povstalec.sgjourney.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.CrystalInterfaceEntity;
import net.povstalec.sgjourney.block_entities.CrystallizerEntity;
import net.povstalec.sgjourney.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.block_entities.dhd.PegasusDHDEntity;
import net.povstalec.sgjourney.block_entities.energy_gen.NaquadahGeneratorMarkIEntity;
import net.povstalec.sgjourney.block_entities.energy_gen.NaquadahGeneratorMarkIIEntity;
import net.povstalec.sgjourney.block_entities.energy_gen.ZPMHubEntity;
import net.povstalec.sgjourney.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.block_entities.symbols.SandstoneCartoucheEntity;
import net.povstalec.sgjourney.block_entities.symbols.SandstoneSymbolBlockEntity;
import net.povstalec.sgjourney.block_entities.symbols.StoneCartoucheEntity;
import net.povstalec.sgjourney.block_entities.symbols.StoneSymbolBlockEntity;
public class BlockEntityInit 
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StargateJourney.MODID);
	
	public static final RegistryObject<BlockEntityType<UniverseStargateEntity>> UNIVERSE_STARGATE = BLOCK_ENTITIES.register("universe_stargate",
            () -> BlockEntityType.Builder.of(UniverseStargateEntity::new, BlockInit.UNIVERSE_STARGATE.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<MilkyWayStargateEntity>> MILKY_WAY_STARGATE = BLOCK_ENTITIES.register("milky_way_stargate",
            () -> BlockEntityType.Builder.of(MilkyWayStargateEntity::new, BlockInit.MILKY_WAY_STARGATE.get()).build(null));
	public static final RegistryObject<BlockEntityType<MilkyWayDHDEntity>> MILKY_WAY_DHD = BLOCK_ENTITIES.register("milky_way_dhd",
            () -> BlockEntityType.Builder.of(MilkyWayDHDEntity::new, BlockInit.MILKY_WAY_DHD.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<PegasusStargateEntity>> PEGASUS_STARGATE = BLOCK_ENTITIES.register("pegasus_stargate",
            () -> BlockEntityType.Builder.of(PegasusStargateEntity::new, BlockInit.PEGASUS_STARGATE.get()).build(null));
	public static final RegistryObject<BlockEntityType<PegasusDHDEntity>> PEGASUS_DHD = BLOCK_ENTITIES.register("pegasus_dhd",
            () -> BlockEntityType.Builder.of(PegasusDHDEntity::new, BlockInit.PEGASUS_DHD.get()).build(null));

	public static final RegistryObject<BlockEntityType<ClassicStargateEntity>> CLASSIC_STARGATE = BLOCK_ENTITIES.register("classic_stargate",
            () -> BlockEntityType.Builder.of(ClassicStargateEntity::new, BlockInit.CLASSIC_STARGATE.get()).build(null));
	public static final RegistryObject<BlockEntityType<ClassicDHDEntity>> CLASSIC_DHD = BLOCK_ENTITIES.register("classic_dhd",
            () -> BlockEntityType.Builder.of(ClassicDHDEntity::new, BlockInit.CLASSIC_DHD.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<TransportRingsEntity>> TRANSPORT_RINGS = BLOCK_ENTITIES.register("transport_rings",
            () -> BlockEntityType.Builder.of(TransportRingsEntity::new, BlockInit.TRANSPORT_RINGS.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<RingPanelEntity>> RING_PANEL = BLOCK_ENTITIES.register("ring_panel",
            () -> BlockEntityType.Builder.of(RingPanelEntity::new, BlockInit.RING_PANEL.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<SandstoneCartoucheEntity>> SANDSTONE_CARTOUCHE = BLOCK_ENTITIES.register("sandstone_cartouche",
            () -> BlockEntityType.Builder.of(SandstoneCartoucheEntity::new, BlockInit.SANDSTONE_CARTOUCHE.get()).build(null));
	public static final RegistryObject<BlockEntityType<StoneCartoucheEntity>> STONE_CARTOUCHE = BLOCK_ENTITIES.register("stone_cartouche",
            () -> BlockEntityType.Builder.of(StoneCartoucheEntity::new, BlockInit.STONE_CARTOUCHE.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<StoneSymbolBlockEntity>> STONE_SYMBOL = BLOCK_ENTITIES.register("stone_symbol",
            () -> BlockEntityType.Builder.of(StoneSymbolBlockEntity::new, BlockInit.STONE_SYMBOL.get()).build(null));
	public static final RegistryObject<BlockEntityType<SandstoneSymbolBlockEntity>> SANDSTONE_SYMBOL = BLOCK_ENTITIES.register("sandstone_symbol",
            () -> BlockEntityType.Builder.of(SandstoneSymbolBlockEntity::new, BlockInit.SANDSTONE_SYMBOL.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<NaquadahGeneratorMarkIEntity>> NAQUADAH_GENERATOR_MARK_I = BLOCK_ENTITIES.register("naquadah_generator_mark_i",
            () -> BlockEntityType.Builder.of(NaquadahGeneratorMarkIEntity::new, BlockInit.NAQUADAH_GENERATOR_MARK_I.get()).build(null));
	public static final RegistryObject<BlockEntityType<NaquadahGeneratorMarkIIEntity>> NAQUADAH_GENERATOR_MARK_II = BLOCK_ENTITIES.register("naquadah_generator_mark_ii",
            () -> BlockEntityType.Builder.of(NaquadahGeneratorMarkIIEntity::new, BlockInit.NAQUADAH_GENERATOR_MARK_II.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<BasicInterfaceEntity>> BASIC_INTERFACE = BLOCK_ENTITIES.register("basic_interface",
            () -> BlockEntityType.Builder.of(BasicInterfaceEntity::new, BlockInit.BASIC_INTERFACE.get()).build(null));
	public static final RegistryObject<BlockEntityType<CrystalInterfaceEntity>> CRYSTAL_INTERFACE = BLOCK_ENTITIES.register("crystal_interface",
            () -> BlockEntityType.Builder.of(CrystalInterfaceEntity::new, BlockInit.CRYSTAL_INTERFACE.get()).build(null));

	public static final RegistryObject<BlockEntityType<ZPMHubEntity>> ZPM_HUB = BLOCK_ENTITIES.register("zpm_hub",
            () -> BlockEntityType.Builder.of(ZPMHubEntity::new, BlockInit.ZPM_HUB.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<CrystallizerEntity>> CRYSTALLIZER = BLOCK_ENTITIES.register("crystallizer",
            () -> BlockEntityType.Builder.of(CrystallizerEntity::new, BlockInit.CRYSTALLIZER.get()).build(null));
	
	public static void register(IEventBus eventBus)
	{
		BLOCK_ENTITIES.register(eventBus);
	}
}
