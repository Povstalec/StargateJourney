package net.povstalec.sgjourney.common.world.biomemod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.povstalec.sgjourney.StargateJourney;

public class BiomeModifiers
{
	public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, StargateJourney.MODID);
	
	public static DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<OreBiomeModifier>> ORE_MODIFIER =
			BIOME_MODIFIERS.register("ores", () -> OreBiomeModifier.CODEC);
	
	public static void register(IEventBus eventBus)
	{
        BIOME_MODIFIERS.register(eventBus);
    }
}
