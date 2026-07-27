package net.povstalec.sgjourney.common.init;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.*;
import net.povstalec.sgjourney.common.sgjourney.stargate.classic.ClassicBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.classic.ClassicSpawnerStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.milky_way.MilkyWayBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.milky_way.MilkyWaySpawnerStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.pegasus.PegasusBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.pegasus.PegasusSpawnerStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.tollans.TollanBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.tollans.TollanSpawnerStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.universe.UniverseBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.universe.UniverseSpawnerStargate;

import java.util.function.Supplier;

public class StargateInit
{
	public static final DeferredRegister<StargateType<?>> STARGATE_TYPES = DeferredRegister.create(StargateType.STARGATE_TYPE_LOCATION, StargateJourney.MODID);
	public static final Supplier<IForgeRegistry<StargateType<?>>> STARGATE_TYPE = STARGATE_TYPES.makeRegistry(RegistryBuilder::new);
	
	
	
	// Block Entity Stargates
	public static final RegistryObject<StargateType<UniverseBlockEntityStargate>> UNIVERSE = STARGATE_TYPES.register("universe", () ->
			new StargateType<>(StargateInfo.Gen.GEN_1, UniverseBlockEntityStargate::new));
	public static final RegistryObject<StargateType<MilkyWayBlockEntityStargate>> MILKY_WAY = STARGATE_TYPES.register("milky_way", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, MilkyWayBlockEntityStargate::new));
	public static final RegistryObject<StargateType<PegasusBlockEntityStargate>> PEGASUS = STARGATE_TYPES.register("pegasus", () ->
			new StargateType<>(StargateInfo.Gen.GEN_3, PegasusBlockEntityStargate::new));
	public static final RegistryObject<StargateType<TollanBlockEntityStargate>> TOLLAN = STARGATE_TYPES.register("tollan", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, TollanBlockEntityStargate::new));
	public static final RegistryObject<StargateType<ClassicBlockEntityStargate>> CLASSIC = STARGATE_TYPES.register("classic", () ->
			new StargateType<>(StargateInfo.Gen.NONE, ClassicBlockEntityStargate::new));
	
	// Spawner Stargates
	public static final RegistryObject<StargateType<UniverseSpawnerStargate>> UNIVERSE_SPAWNER = STARGATE_TYPES.register("universe_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_1, UniverseSpawnerStargate::new));
	public static final RegistryObject<StargateType<MilkyWaySpawnerStargate>> MILKY_WAY_SPAWNER = STARGATE_TYPES.register("milky_way_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, MilkyWaySpawnerStargate::new));
	public static final RegistryObject<StargateType<PegasusSpawnerStargate>> PEGASUS_SPAWNER = STARGATE_TYPES.register("pegasus_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_3, PegasusSpawnerStargate::new));
	public static final RegistryObject<StargateType<TollanSpawnerStargate>> TOLLAN_SPAWNER = STARGATE_TYPES.register("tollan_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, TollanSpawnerStargate::new));
	public static final RegistryObject<StargateType<ClassicSpawnerStargate>> CLASSIC_SPAWNER = STARGATE_TYPES.register("classic_spawner", () ->
			new StargateType<>(StargateInfo.Gen.NONE, ClassicSpawnerStargate::new));
	
	
	
	public static void register(IEventBus eventBus)
	{
		STARGATE_TYPES.register(eventBus);
	}
}
