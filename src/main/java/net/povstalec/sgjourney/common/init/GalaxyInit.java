package net.povstalec.sgjourney.common.init;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.stargate.GalaxyType;

public class GalaxyInit
{
	public static final DeferredRegister<GalaxyType> GALAXY_TYPES = DeferredRegister.create(GalaxyType.GALAXY_TYPE_LOCATION, StargateJourney.MODID);
	public static final Supplier<IForgeRegistry<GalaxyType>> GALAXY_TYPE = GALAXY_TYPES.makeRegistry(RegistryBuilder::new);

	public static final RegistryObject<GalaxyType> DWARF_GALAXY = GALAXY_TYPES.register("dwarf_galaxy", () -> new GalaxyType(36)); // Like Pegasus
	public static final RegistryObject<GalaxyType> MEDIUM_GALAXY = GALAXY_TYPES.register("medium_galaxy", () -> new GalaxyType(39)); // Like Milky Way
	public static final RegistryObject<GalaxyType> LARGE_GALAXY = GALAXY_TYPES.register("large_galaxy", () -> new GalaxyType(42)); // Like Andromeda
	public static final RegistryObject<GalaxyType> GIANT_GALAXY = GALAXY_TYPES.register("giant_galaxy", () -> new GalaxyType(45)); // Like M87
	public static final RegistryObject<GalaxyType> SUPERGIANT_GALAXY = GALAXY_TYPES.register("supergiant_galaxy", () -> new GalaxyType(48)); // Like IC 1101
	
	public static void register(IEventBus eventBus)
	{
		GALAXY_TYPES.register(eventBus);
	}
	
	public static GalaxyType getGalaxyType(ResourceLocation galaxyType)
	{
		return RegistryObject.create(galaxyType, GALAXY_TYPE.get()).get();
	}
}
