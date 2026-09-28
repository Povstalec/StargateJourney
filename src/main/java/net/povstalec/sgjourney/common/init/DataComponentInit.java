package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.items.KaraKeshItem;
import net.povstalec.sgjourney.common.items.SyringeItem;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import java.util.function.UnaryOperator;

public class DataComponentInit
{
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, StargateJourney.MODID);

    //Blocks
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> STARGATE_VARIANT = register("stargate_variant", builder -> builder.persistent(ResourceLocation.CODEC));

    // Items
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ENERGY = register("energy", builder -> builder.persistent(Codec.LONG));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> ENERGY_TARGET = register("energy_target", builder -> builder.persistent(Codec.LONG));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> FLUID = register("fluid", builder -> builder.persistent(SimpleFluidContent.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENTROPY = register("entropy", builder -> builder.persistent(Codec.INT));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KaraKeshItem.Mode>> KARA_KESH_MODE = register("kara_kesh_mode", builder -> builder.persistent(KaraKeshItem.Mode.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_OPEN = register("is_open", builder -> builder.persistent(Codec.BOOL));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SHIELD_TEXTURE = register("shield_texture", builder -> builder.persistent(ResourceLocation.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> IRIS_TEXTURE = register("iris_texture", builder -> builder.persistent(ResourceLocation.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> IRIS_DURABILITY = register("iris_durability", builder -> builder.persistent(Codec.INT));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> STARGATE_UPGRADE = register("stargate_upgrade", builder -> builder.persistent(ResourceLocation.CODEC));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> IDC = register("idc", builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FREQUENCY = register("frequency", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> CRYSTAL_MEMORY = register("crystal_memory", builder -> builder.persistent(CompoundTag.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> NAQUADAH_FUEL = register("naquadah_fuel", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FUSION_FUEL = register("fusion_fuel", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> REACTION_PROGRESS = register("reaction_progress", builder -> builder.persistent(Codec.LONG));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SyringeItem.Contents>> SYRINGE_CONTENTS = register("syringe_contents", builder -> builder.persistent(SyringeItem.CONTENTS_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> INDEX = register("index", builder -> builder.persistent(Codec.INT));
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> GOAULD_INFO = register("goauld_info", builder -> builder.persistent(CompoundTag.CODEC));
	
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<Level>>> DIMENSION = register("dimension", builder -> builder.persistent(Level.RESOURCE_KEY_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> SKIP_LOADED_CHUNKS = register("skip_loaded_chunks", builder -> builder.persistent(Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<Structure>>> TARGET_STRUCTURE = register("target_structure", builder -> builder.persistent(TagKey.codec(Registries.STRUCTURE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<MapDecorationType>>> DECORATION_TYPE = register("decoration_type", builder -> builder.persistent(MapDecorationType.CODEC));
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<PointOfOrigin>>> POINT_OF_ORIGIN = register("point_of_origin", builder -> builder.persistent(PointOfOrigin.RESOURCE_KEY_CODEC));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<Symbols>>> SYMBOLS = register("symbols", builder -> builder.persistent(Symbols.RESOURCE_KEY_CODEC));
	
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Address.Immutable>> ADDRESS_IMMUTABLE = register("address_immutable", builder -> builder.persistent(Address.Immutable.CODEC));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Address.Mutable>> ADDRESS_MUTABLE = register("address_mutable", builder -> builder.persistent(Address.Mutable.CODEC));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Address.Dimension>> ADDRESS_DIMENSION = register("address_dimension", builder -> builder.persistent(Address.Dimension.CODEC));
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DO_KAWOOSH = register("address", builder -> builder.persistent(Codec.BOOL));



    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator)
    {
        return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus)
    {
        DATA_COMPONENTS.register(eventBus);
    }
}
