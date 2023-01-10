package net.povstalec.sgjourney.init;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.structures.StargateTemple;
import net.povstalec.sgjourney.structures.BuriedStargate;
import net.povstalec.sgjourney.structures.City;
import net.povstalec.sgjourney.structures.CommonStargate;
import net.povstalec.sgjourney.structures.GoauldTemple;
import net.povstalec.sgjourney.structures.JaffaHouse;
import net.povstalec.sgjourney.structures.StargateOutpost;
import net.povstalec.sgjourney.structures.StargatePedestal;

public class StructureInit
{
	public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, StargateJourney.MODID);

	public static final RegistryObject<StructureType<?>> COMMON_STARGATE =
            DEFERRED_REGISTRY_STRUCTURE.register("common_stargate", () -> typeConvert(CommonStargate.CODEC));
	
	public static final RegistryObject<StructureType<?>> STARGATE_OUTPOST =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_outpost", () -> typeConvert(StargateOutpost.CODEC));
	
	public static final RegistryObject<StructureType<?>> STARGATE_TEMPLE =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_temple", () -> typeConvert(StargateTemple.CODEC));
	
	public static final RegistryObject<StructureType<?>> STARGATE_PEDESTAL =
            DEFERRED_REGISTRY_STRUCTURE.register("stargate_pedestal", () -> typeConvert(StargatePedestal.CODEC));
	
	public static final RegistryObject<StructureType<?>> BURIED_STARGATE =
            DEFERRED_REGISTRY_STRUCTURE.register("buried_stargate", () -> typeConvert(BuriedStargate.CODEC));
	
	public static final RegistryObject<StructureType<?>> GOAULD_TEMPLE =
            DEFERRED_REGISTRY_STRUCTURE.register("goauld_temple", () -> typeConvert(GoauldTemple.CODEC));
	
	public static final RegistryObject<StructureType<?>> CITY =
            DEFERRED_REGISTRY_STRUCTURE.register("city", () -> typeConvert(City.CODEC));
	
	public static final RegistryObject<StructureType<?>> JAFFA_HOUSE =
            DEFERRED_REGISTRY_STRUCTURE.register("jaffa_house", () -> typeConvert(JaffaHouse.CODEC));
	
	private static <S extends Structure> StructureType<S> typeConvert(Codec<S> codec)
	{
        return () -> codec;
    }
	
	public static void register(IEventBus eventBus)
	{
		DEFERRED_REGISTRY_STRUCTURE.register(eventBus);
	}
}
