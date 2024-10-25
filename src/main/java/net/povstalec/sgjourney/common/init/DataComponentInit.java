package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;

import java.util.function.UnaryOperator;

public class DataComponentInit
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, StargateJourney.MODID);

    //Blocks
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> STARGATE_VARIANT = register("stargate_variant", builder -> builder.persistent(ResourceLocation.CODEC));

    // Items
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TERROR_MODE = register("terror_mode", builder -> builder.persistent(Codec.BOOL));



    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator)
    {
        return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus)
    {
        DATA_COMPONENTS.register(eventBus);
    }
}
