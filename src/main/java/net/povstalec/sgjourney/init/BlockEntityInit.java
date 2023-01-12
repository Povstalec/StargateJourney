package net.povstalec.sgjourney.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.ClassicDHDEntity;
import net.povstalec.sgjourney.block_entities.MilkyWayDHDEntity;
import net.povstalec.sgjourney.block_entities.MilkyWayStargateEntity;
import net.povstalec.sgjourney.block_entities.PegasusDHDEntity;
import net.povstalec.sgjourney.block_entities.PegasusStargateEntity;
import net.povstalec.sgjourney.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.block_entities.address.SandstoneCartoucheEntity;
import net.povstalec.sgjourney.block_entities.address.SandstoneSymbolBlockEntity;
import net.povstalec.sgjourney.block_entities.address.StoneCartoucheEntity;
import net.povstalec.sgjourney.block_entities.address.StoneSymbolBlockEntity;
public class BlockEntityInit 
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, StargateJourney.MODID);
	
	public static final RegistryObject<BlockEntityType<MilkyWayStargateEntity>> MILKY_WAY_STARGATE = BLOCK_ENTITIES.register("milky_way_stargate",
            () -> BlockEntityType.Builder.of(MilkyWayStargateEntity::new, BlockInit.MILKY_WAY_STARGATE.get()).build(null));
	public static final RegistryObject<BlockEntityType<MilkyWayDHDEntity>> MILKY_WAY_DHD = BLOCK_ENTITIES.register("milky_way_dhd",
            () -> BlockEntityType.Builder.of(MilkyWayDHDEntity::new, BlockInit.MILKY_WAY_DHD.get()).build(null));
	
	public static final RegistryObject<BlockEntityType<PegasusStargateEntity>> PEGASUS_STARGATE = BLOCK_ENTITIES.register("pegasus_stargate",
            () -> BlockEntityType.Builder.of(PegasusStargateEntity::new, BlockInit.PEGASUS_STARGATE.get()).build(null));
	public static final RegistryObject<BlockEntityType<PegasusDHDEntity>> PEGASUS_DHD = BLOCK_ENTITIES.register("pegasus_dhd",
            () -> BlockEntityType.Builder.of(PegasusDHDEntity::new, BlockInit.PEGASUS_DHD.get()).build(null));

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
	
	public static void register(IEventBus eventBus)
	{
		BLOCK_ENTITIES.register(eventBus);
	}
}
