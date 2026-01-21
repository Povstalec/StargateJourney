package net.povstalec.sgjourney.common.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.*;
import net.povstalec.sgjourney.common.block_entities.dhd.ClassicDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.MilkyWayDHDEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.PegasusDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.*;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.RingPanelEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.TransportRingsEntity;

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
	public static final RegistryObject<BlockEntityType<TollanStargateEntity>> TOLLAN_STARGATE = BLOCK_ENTITIES.register("tollan_stargate",
			() -> BlockEntityType.Builder.of(TollanStargateEntity::new, BlockInit.TOLLAN_STARGATE.get()).build(null));
	public static final RegistryObject<BlockEntityType<ClassicDHDEntity>> CLASSIC_DHD = BLOCK_ENTITIES.register("classic_dhd",
            () -> BlockEntityType.Builder.of(ClassicDHDEntity::new, BlockInit.CLASSIC_DHD.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<TransportRingsEntity>> GOAULD_TRANSPORT_RINGS = BLOCK_ENTITIES.register("goauld_transport_rings",
            () -> BlockEntityType.Builder.of(TransportRingsEntity::new, BlockInit.GOAULD_TRANSPORT_RINGS.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<RingPanelEntity>> GOAULD_RING_PANEL = BLOCK_ENTITIES.register("goauld_ring_panel",
            () -> BlockEntityType.Builder.of(RingPanelEntity::new, BlockInit.GOAULD_RING_PANEL.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<CartoucheEntity.Sandstone>> SANDSTONE_CARTOUCHE = BLOCK_ENTITIES.register("sandstone_cartouche",
            () -> BlockEntityType.Builder.of(CartoucheEntity.Sandstone::new, BlockInit.SANDSTONE_CARTOUCHE.get()).build(null));
	public static final RegistryObject<BlockEntityType<CartoucheEntity.RedSandstone>> RED_SANDSTONE_CARTOUCHE = BLOCK_ENTITIES.register("red_sandstone_cartouche",
			() -> BlockEntityType.Builder.of(CartoucheEntity.RedSandstone::new, BlockInit.RED_SANDSTONE_CARTOUCHE.get()).build(null));
	public static final RegistryObject<BlockEntityType<CartoucheEntity.Stone>> STONE_CARTOUCHE = BLOCK_ENTITIES.register("stone_cartouche",
            () -> BlockEntityType.Builder.of(CartoucheEntity.Stone::new, BlockInit.STONE_CARTOUCHE.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<SymbolBlockEntity.Stone>> STONE_SYMBOL = BLOCK_ENTITIES.register("stone_symbol",
            () -> BlockEntityType.Builder.of(SymbolBlockEntity.Stone::new, BlockInit.STONE_SYMBOL.get()).build(null));
	public static final RegistryObject<BlockEntityType<SymbolBlockEntity.Sandstone>> SANDSTONE_SYMBOL = BLOCK_ENTITIES.register("sandstone_symbol",
            () -> BlockEntityType.Builder.of(SymbolBlockEntity.Sandstone::new, BlockInit.SANDSTONE_SYMBOL.get()).build(null));
	public static final RegistryObject<BlockEntityType<SymbolBlockEntity.RedSandstone>> RED_SANDSTONE_SYMBOL = BLOCK_ENTITIES.register("red_sandstone_symbol",
			() -> BlockEntityType.Builder.of(SymbolBlockEntity.RedSandstone::new, BlockInit.RED_SANDSTONE_SYMBOL.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<NaquadahGeneratorEntity.Reactor>> NAQUADAH_REACTOR = BLOCK_ENTITIES.register("naquadah_reactor",
			() -> BlockEntityType.Builder.of(NaquadahGeneratorEntity.Reactor::new, BlockInit.NAQUADAH_REACTOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<NaquadahGeneratorEntity.MarkI>> NAQUADAH_GENERATOR_MARK_I = BLOCK_ENTITIES.register("naquadah_generator_mark_i",
            () -> BlockEntityType.Builder.of(NaquadahGeneratorEntity.MarkI::new, BlockInit.NAQUADAH_GENERATOR_MARK_I.get()).build(null));
	public static final RegistryObject<BlockEntityType<NaquadahGeneratorEntity.MarkII>> NAQUADAH_GENERATOR_MARK_II = BLOCK_ENTITIES.register("naquadah_generator_mark_ii",
            () -> BlockEntityType.Builder.of(NaquadahGeneratorEntity.MarkII::new, BlockInit.NAQUADAH_GENERATOR_MARK_II.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<BasicInterfaceEntity>> BASIC_INTERFACE = BLOCK_ENTITIES.register("basic_interface",
            () -> BlockEntityType.Builder.of(BasicInterfaceEntity::new, BlockInit.BASIC_INTERFACE.get()).build(null));
	public static final RegistryObject<BlockEntityType<CrystalInterfaceEntity>> CRYSTAL_INTERFACE = BLOCK_ENTITIES.register("crystal_interface",
            () -> BlockEntityType.Builder.of(CrystalInterfaceEntity::new, BlockInit.CRYSTAL_INTERFACE.get()).build(null));
	public static final RegistryObject<BlockEntityType<AdvancedCrystalInterfaceEntity>> ADVANCED_CRYSTAL_INTERFACE = BLOCK_ENTITIES.register("advanced_crystal_interface",
            () -> BlockEntityType.Builder.of(AdvancedCrystalInterfaceEntity::new, BlockInit.ADVANCED_CRYSTAL_INTERFACE.get()).build(null));

	public static final RegistryObject<BlockEntityType<ZPMHubEntity>> ZPM_HUB = BLOCK_ENTITIES.register("zpm_hub",
            () -> BlockEntityType.Builder.of(ZPMHubEntity::new, BlockInit.ZPM_HUB.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<NaquadahLiquidizerEntity>> NAQUADAH_LIQUIDIZER = BLOCK_ENTITIES.register("naquadah_liquidizer",
            () -> BlockEntityType.Builder.of(NaquadahLiquidizerEntity::new, BlockInit.NAQUADAH_LIQUIDIZER.get()).build(null));
	public static final RegistryObject<BlockEntityType<HeavyNaquadahLiquidizerEntity>> HEAVY_NAQUADAH_LIQUIDIZER = BLOCK_ENTITIES.register("heavy_naquadah_liquidizer",
            () -> BlockEntityType.Builder.of(HeavyNaquadahLiquidizerEntity::new, BlockInit.HEAVY_NAQUADAH_LIQUIDIZER.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<CrystallizerEntity>> CRYSTALLIZER = BLOCK_ENTITIES.register("crystallizer",
            () -> BlockEntityType.Builder.of(CrystallizerEntity::new, BlockInit.CRYSTALLIZER.get()).build(null));
	public static final RegistryObject<BlockEntityType<AdvancedCrystallizerEntity>> ADVANCED_CRYSTALLIZER = BLOCK_ENTITIES.register("advanced_crystallizer",
            () -> BlockEntityType.Builder.of(AdvancedCrystallizerEntity::new, BlockInit.ADVANCED_CRYSTALLIZER.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<TransceiverEntity>> TRANSCEIVER = BLOCK_ENTITIES.register("transciever",
            () -> BlockEntityType.Builder.of(TransceiverEntity::new, BlockInit.TRANSCEIVER.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<CableBlockEntity.NaquadahWire>> NAQUADAH_WIRE = BLOCK_ENTITIES.register("naquadah_wire",
			() -> BlockEntityType.Builder.of(CableBlockEntity.NaquadahWire::new, BlockInit.NAQUADAH_WIRE.get()).build(null));
	public static final RegistryObject<BlockEntityType<CableBlockEntity.SmallNaquadahCable>> SMALL_NAQUADAH_CABLE = BLOCK_ENTITIES.register("small_naquadah_cable",
			() -> BlockEntityType.Builder.of(CableBlockEntity.SmallNaquadahCable::new, BlockInit.SMALL_NAQUADAH_CABLE.get()).build(null));
	public static final RegistryObject<BlockEntityType<CableBlockEntity.MediumNaquadahCable>> MEDIUM_NAQUADAH_CABLE = BLOCK_ENTITIES.register("medium_naquadah_cable",
			() -> BlockEntityType.Builder.of(CableBlockEntity.MediumNaquadahCable::new, BlockInit.MEDIUM_NAQUADAH_CABLE.get()).build(null));
	public static final RegistryObject<BlockEntityType<CableBlockEntity.LargeNaquadahCable>> LARGE_NAQUADAH_CABLE = BLOCK_ENTITIES.register("large_naquadah_cable",
			() -> BlockEntityType.Builder.of(CableBlockEntity.LargeNaquadahCable::new, BlockInit.LARGE_NAQUADAH_CABLE.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<BatteryBlockEntity.Naquadah>> LARGE_NAQUADAH_BATTERY = BLOCK_ENTITIES.register("large_naquadah_battery",
			() -> BlockEntityType.Builder.of(BatteryBlockEntity.Naquadah::new, BlockInit.LARGE_NAQUADAH_BATTERY.get()).build(null));
	
	public static void register(IEventBus eventBus)
	{
		BLOCK_ENTITIES.register(eventBus);
	}
}
