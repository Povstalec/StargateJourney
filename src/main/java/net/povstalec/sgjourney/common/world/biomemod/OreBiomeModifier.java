package net.povstalec.sgjourney.common.world.biomemod;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public record OreBiomeModifier(HolderSet<Biome> biomes, Holder<PlacedFeature> feature) implements BiomeModifier
{
    public static final MapCodec<OreBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(OreBiomeModifier::biomes),
            PlacedFeature.CODEC.fieldOf("feature").forGetter(OreBiomeModifier::feature)
    ).apply(builder, OreBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder)
    {
        if(phase == Phase.ADD && biomes.contains(biome))
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, feature);
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec()
    {
        return BiomeModifiers.ORE_MODIFIER.get();
    }
}
