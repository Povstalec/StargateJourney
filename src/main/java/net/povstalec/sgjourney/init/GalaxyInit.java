package net.povstalec.sgjourney.init;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.stargate.GalaxyType;

public class GalaxyInit
{
	public static final DeferredRegister<GalaxyType> GALAXY_TYPES = DeferredRegister.create(new ResourceLocation(StargateJourney.MODID, "galaxy_type"), StargateJourney.MODID);
	public static final Supplier<IForgeRegistry<GalaxyType>> GALAXY_TYPE = GALAXY_TYPES.makeRegistry(RegistryBuilder::new);

	public static final RegistryObject<GalaxyType> DWARF_GALAXY = GALAXY_TYPES.register("dwarf_galaxy", () -> new GalaxyType(35)); // Like Pegasus
	public static final RegistryObject<GalaxyType> MEDIUM_GALAXY = GALAXY_TYPES.register("medium_galaxy", () -> new GalaxyType(38)); // Like Milky Way
	public static final RegistryObject<GalaxyType> LARGE_GALAXY = GALAXY_TYPES.register("large_galaxy", () -> new GalaxyType(41)); // Like Andromeda
	public static final RegistryObject<GalaxyType> GIANT_GALAXY = GALAXY_TYPES.register("giant_galaxy", () -> new GalaxyType(44)); // Like M87
	public static final RegistryObject<GalaxyType> SUPERGIANT_GALAXY = GALAXY_TYPES.register("supergiant_galaxy", () -> new GalaxyType(47)); // Like IC 1101
	
	public static void register(IEventBus eventBus)
	{
		GALAXY_TYPES.register(eventBus);
	}
}
