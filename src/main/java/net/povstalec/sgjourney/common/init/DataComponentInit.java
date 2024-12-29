package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.items.SyringeItem;
import net.povstalec.sgjourney.common.items.crystals.MaterializationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;

import java.util.function.UnaryOperator;

public class DataComponentInit
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, StargateJourney.MODID);

    //Blocks
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> STARGATE_VARIANT = register("stargate_variant", builder -> builder.persistent(ResourceLocation.CODEC));

    // Items
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ENERGY = register("energy", builder -> builder.persistent(Codec.LONG));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID = register("fluid", builder -> builder.persistent(SimpleFluidContent.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENTROPY = register("entropy", builder -> builder.persistent(Codec.INT));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TERROR_MODE = register("terror_mode", builder -> builder.persistent(Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_OPEN = register("is_open", builder -> builder.persistent(Codec.BOOL));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SHIELD_TEXTURE = register("shield_texture", builder -> builder.persistent(ResourceLocation.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> IRIS_TEXTURE = register("iris_texture", builder -> builder.persistent(ResourceLocation.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> IRIS_DURABILITY = register("iris_durability", builder -> builder.persistent(Codec.INT));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> STARGATE_UPGRADE = register("stargate_upgrade", builder -> builder.persistent(ResourceLocation.CODEC));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> IDC = register("idc", builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FREQUENCY = register("frequency", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MaterializationCrystalItem.CrystalMode>> MATERIALIZATION_CRYSTAL_MODE = register("materialization_crystal_mode", builder -> builder.persistent(MaterializationCrystalItem.CRYSTAL_MODE_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MemoryCrystalItem.MemoryType>> MEMORY_CRYSTAL_MODE = register("memory_crystal_mode", builder -> builder.persistent(MemoryCrystalItem.CRYSTAL_MODE_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NAQUADAH_FUEL = register("naquadah_fuel", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SyringeItem.Contents>> SYRINGE_CONTENTS = register("syringe_contents", builder -> builder.persistent(SyringeItem.CONTENTS_CODEC));



    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator)
    {
        return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus)
    {
        DATA_COMPONENTS.register(eventBus);
    }
}
