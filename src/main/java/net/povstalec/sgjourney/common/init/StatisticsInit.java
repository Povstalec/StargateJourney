package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;

import java.util.ArrayList;
import java.util.List;

public class StatisticsInit
{
	public static final DeferredRegister<ResourceLocation> STATISTICS = DeferredRegister.create(Registries.CUSTOM_STAT, StargateJourney.MODID);
	private static final List<Runnable> STATISTIC_SETUP = new ArrayList<>();
	
	public static final RegistryObject<ResourceLocation> TIMES_USED_WORMHOLE = STATISTICS.register("times_used_wormhole", () -> registerDefaultStatistic("times_used_wormhole"));
	public static final RegistryObject<ResourceLocation> TIMES_KILLED_BY_KAWOOSH = STATISTICS.register("times_killed_by_kawoosh", () -> registerDefaultStatistic("times_killed_by_kawoosh"));
	public static final RegistryObject<ResourceLocation> TIMES_KILLED_BY_WORMHOLE = STATISTICS.register("times_killed_by_wormhole", () -> registerDefaultStatistic("times_killed_by_wormhole"));
	public static final RegistryObject<ResourceLocation> DISTANCE_TRAVELED_BY_STARGATE = STATISTICS.register("distance_traveled_by_stargate", () -> registerDistanceStatistic("distance_traveled_by_stargate"));
	
	
	private static ResourceLocation registerDefaultStatistic(String key)
	{
		ResourceLocation resourceLocation = new ResourceLocation(StargateJourney.MODID, key);
		STATISTIC_SETUP.add(() -> Stats.CUSTOM.get(resourceLocation, StatFormatter.DEFAULT));
		return resourceLocation;
	}
	
	private static ResourceLocation registerDistanceStatistic(String key)
	{
		ResourceLocation resourceLocation = new ResourceLocation(StargateJourney.MODID, key);
		STATISTIC_SETUP.add(() -> Stats.CUSTOM.get(resourceLocation, StatFormatter.DISTANCE));
		return resourceLocation;
	}

	public static void register()
	{
		STATISTIC_SETUP.forEach(Runnable::run);
	}

	public static void register(IEventBus bus)
	{
		STATISTICS.register(bus);
	}
}

