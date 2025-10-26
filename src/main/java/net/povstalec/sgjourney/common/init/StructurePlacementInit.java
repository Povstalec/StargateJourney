package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.world.UniqueStructurePlacement;

public class StructurePlacementInit
{
	public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES = DeferredRegister.create(Registry.STRUCTURE_PLACEMENT_TYPE_REGISTRY, StargateJourney.MODID);
	
	public static final RegistryObject<StructurePlacementType<UniqueStructurePlacement>> UNIQUE_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("unique_placement", () -> typeConvert(UniqueStructurePlacement.CODEC));
	public static final RegistryObject<StructurePlacementType<UniqueStructurePlacement.Stargate>> STARGATE_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("stargate_placement", () -> typeConvert(UniqueStructurePlacement.Stargate.CODEC));
	public static final RegistryObject<StructurePlacementType<UniqueStructurePlacement.BuriedStargate>> BURIED_STARGATE_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("buried_stargate_placement", () -> typeConvert(UniqueStructurePlacement.BuriedStargate.CODEC));
	
	private static <T extends StructurePlacement> StructurePlacementType<T> typeConvert(Codec<T> codec)
	{
		return () -> codec;
	}
	
	public static void register(IEventBus eventBus)
	{
		STRUCTURE_PLACEMENT_TYPES.register(eventBus);
	}
}
