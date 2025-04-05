package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.GalaxyType;

public class GalaxyInit
{
	public static final ResourceKey<Registry<GalaxyType>> GALAXY_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(GalaxyType.GALAXY_TYPE_LOCATION);
	public static final Registry<GalaxyType> GALAXY_TYPE_REGISTRY = new RegistryBuilder<>(GALAXY_TYPE_REGISTRY_KEY).sync(true).create();
	public static final DeferredRegister<GalaxyType> GALAXY_TYPES = DeferredRegister.create(GalaxyType.GALAXY_TYPE_LOCATION, StargateJourney.MODID);

	public static final DeferredHolder<GalaxyType, GalaxyType> DWARF_GALAXY = GALAXY_TYPES.register("dwarf_galaxy", () -> new GalaxyType(36)); // Like Pegasus
	public static final DeferredHolder<GalaxyType, GalaxyType> MEDIUM_GALAXY = GALAXY_TYPES.register("medium_galaxy", () -> new GalaxyType(39)); // Like Milky Way
	public static final DeferredHolder<GalaxyType, GalaxyType> LARGE_GALAXY = GALAXY_TYPES.register("large_galaxy", () -> new GalaxyType(42)); // Like Andromeda
	public static final DeferredHolder<GalaxyType, GalaxyType> GIANT_GALAXY = GALAXY_TYPES.register("giant_galaxy", () -> new GalaxyType(45)); // Like M87
	public static final DeferredHolder<GalaxyType, GalaxyType> SUPERGIANT_GALAXY = GALAXY_TYPES.register("supergiant_galaxy", () -> new GalaxyType(48)); // Like IC 1101
	
	public static final Codec<GalaxyType> CODEC = GALAXY_TYPE_REGISTRY.byNameCodec();

	public static void register(IEventBus eventBus)
	{
		GALAXY_TYPES.register(eventBus);
	}
	
	public static GalaxyType getGalaxyType(ResourceLocation galaxyType)
	{
		return GALAXY_TYPE_REGISTRY.get(galaxyType);
	}

	@SubscribeEvent
	public static void registerRegistries(NewRegistryEvent event)
	{
		event.register(GALAXY_TYPE_REGISTRY);
	}
}
