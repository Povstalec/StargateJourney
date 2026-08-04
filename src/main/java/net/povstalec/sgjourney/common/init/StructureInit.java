package net.povstalec.sgjourney.common.init;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.structures.*;

public class StructureInit
{
	//public static final DeferredRegister<ConfiguredStructureFeature<?, ?>> DEFERRED_REGISTRY_CONFIGURED_STRUCTURE = DeferredRegister.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, StargateJourney.MODID);
	
	public static final DeferredRegister<StructureFeature<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registry.STRUCTURE_FEATURE_REGISTRY, StargateJourney.MODID);
	
	public static final RegistryObject<StructureFeature<?>> COMMON_STARGATE =
            DEFERRED_REGISTRY_STRUCTURE.register("common_stargate", CommonStargate::new);
	
	public static final RegistryObject<StructureFeature<?>> STARGATE_OUTPOST =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_outpost", StargateOutpost::new);
	
	public static final RegistryObject<StructureFeature<?>> STARGATE_TEMPLE =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_temple", StargateTemple::new);
	
	public static final RegistryObject<StructureFeature<?>> STARGATE_PEDESTAL =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_pedestal", StargatePedestal::new);
	
	public static final RegistryObject<StructureFeature<?>> BURIED_STARGATE =
            DEFERRED_REGISTRY_STRUCTURE.register("buried_stargate", BuriedStargate::new);
	
	public static final RegistryObject<StructureFeature<?>> STARGATE_VOID_STRUCTURE =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_void_structure", StargateVoidStructure::new);
	
	public static final RegistryObject<StructureFeature<?>> GOAULD_TEMPLE =
            DEFERRED_REGISTRY_STRUCTURE.register("goauld_temple", GoauldTemple::new);
	
	public static final RegistryObject<StructureFeature<?>> CITY =
            DEFERRED_REGISTRY_STRUCTURE.register("city", City::new);
	
	public static final RegistryObject<StructureFeature<?>> CARTOUCHE =
            DEFERRED_REGISTRY_STRUCTURE.register("cartouche", Cartouche::new);
	
	public static final RegistryObject<StructureFeature<?>> JAFFA_HOUSE =
            DEFERRED_REGISTRY_STRUCTURE.register("jaffa_house", JaffaHouse::new);
	
	public static void register(IEventBus eventBus)
	{
		//DEFERRED_REGISTRY_CONFIGURED_STRUCTURE.register(eventBus);
		
		DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
	}
}
