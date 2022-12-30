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

	public static final RegistryObject<GalaxyType> DWARF_GALAXY = GALAXY_TYPES.register("dwarf_galaxy", () -> new GalaxyType(35));
	public static final RegistryObject<GalaxyType> MEDIUM_GALAXY = GALAXY_TYPES.register("medium_galaxy", () -> new GalaxyType(38));
	
	public static void register(IEventBus eventBus)
	{
		GALAXY_TYPES.register(eventBus);
	}
}
