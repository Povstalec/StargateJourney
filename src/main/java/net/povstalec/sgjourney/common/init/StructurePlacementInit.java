package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.world.UniqueStructurePlacement;

public class StructurePlacementInit
{
	
	public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, StargateJourney.MODID);
	
	public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<UniqueStructurePlacement>> UNIQUE_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("unique_placement", () -> typeConvert(UniqueStructurePlacement.CODEC));
	public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<UniqueStructurePlacement.Stargate>> STARGATE_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("stargate_placement", () -> typeConvert(UniqueStructurePlacement.Stargate.CODEC));
	public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<UniqueStructurePlacement.BuriedStargate>> BURIED_STARGATE_PLACEMENT = STRUCTURE_PLACEMENT_TYPES.register("buried_stargate_placement", () -> typeConvert(UniqueStructurePlacement.BuriedStargate.CODEC));
	
	private static <T extends StructurePlacement> StructurePlacementType<T> typeConvert(MapCodec<T> structurePlacementTypeCodec)
	{
		return () -> structurePlacementTypeCodec;
	}
	
	public static void register(IEventBus eventBus)
	{
		STRUCTURE_PLACEMENT_TYPES.register(eventBus);
	}
}
