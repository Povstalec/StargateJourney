package net.povstalec.sgjourney.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;
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

public class StargateInit
{
	public static final ResourceKey<Registry<StargateType<?>>> STARGATE_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(StargateType.STARGATE_TYPE_LOCATION);
	public static final Registry<StargateType<?>> STARGATE_TYPE_REGISTRY = new net.neoforged.neoforge.registries.RegistryBuilder<>(STARGATE_TYPE_REGISTRY_KEY).sync(true).create();
	public static final net.neoforged.neoforge.registries.DeferredRegister<StargateType<?>> STARGATE_TYPES = net.neoforged.neoforge.registries.DeferredRegister.create(StargateType.STARGATE_TYPE_LOCATION, StargateJourney.MODID);
	
	
	
	// Block Entity Stargates
	public static final DeferredHolder<StargateType<?>, StargateType<UniverseBlockEntityStargate>> UNIVERSE = STARGATE_TYPES.register("universe", () ->
			new StargateType<>(StargateInfo.Gen.GEN_1, UniverseBlockEntityStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<MilkyWayBlockEntityStargate>> MILKY_WAY = STARGATE_TYPES.register("milky_way", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, MilkyWayBlockEntityStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<PegasusBlockEntityStargate>> PEGASUS = STARGATE_TYPES.register("pegasus", () ->
			new StargateType<>(StargateInfo.Gen.GEN_3, PegasusBlockEntityStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<TollanBlockEntityStargate>> TOLLAN = STARGATE_TYPES.register("tollan", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, TollanBlockEntityStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<ClassicBlockEntityStargate>> CLASSIC = STARGATE_TYPES.register("classic", () ->
			new StargateType<>(StargateInfo.Gen.NONE, ClassicBlockEntityStargate::new));
	
	// Spawner Stargates
	public static final DeferredHolder<StargateType<?>, StargateType<UniverseSpawnerStargate>> UNIVERSE_SPAWNER = STARGATE_TYPES.register("universe_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_1, UniverseSpawnerStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<MilkyWaySpawnerStargate>> MILKY_WAY_SPAWNER = STARGATE_TYPES.register("milky_way_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, MilkyWaySpawnerStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<PegasusSpawnerStargate>> PEGASUS_SPAWNER = STARGATE_TYPES.register("pegasus_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_3, PegasusSpawnerStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<TollanSpawnerStargate>> TOLLAN_SPAWNER = STARGATE_TYPES.register("tollan_spawner", () ->
			new StargateType<>(StargateInfo.Gen.GEN_2, TollanSpawnerStargate::new));
	public static final DeferredHolder<StargateType<?>, StargateType<ClassicSpawnerStargate>> CLASSIC_SPAWNER = STARGATE_TYPES.register("classic_spawner", () ->
			new StargateType<>(StargateInfo.Gen.NONE, ClassicSpawnerStargate::new));
	
	
	
	public static void register(IEventBus eventBus)
	{
		STARGATE_TYPES.register(eventBus);
	}
	
	@SubscribeEvent
	public static void registerRegistries(NewRegistryEvent event)
	{
		event.register(STARGATE_TYPE_REGISTRY);
	}
}
