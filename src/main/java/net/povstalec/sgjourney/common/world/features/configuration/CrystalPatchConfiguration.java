package net.povstalec.sgjourney.common.world.features.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record CrystalPatchConfiguration(int tries, int xzSpread, int ySpread, Holder<PlacedFeature> clusterFeature, Holder<PlacedFeature> smallBudFeature, Holder<PlacedFeature> mediumBudFeature, Holder<PlacedFeature> largeBudFeature, int maxHeight, BlockStateProvider fillingProvider, BlockStateProvider buddingProvider) implements FeatureConfiguration
{
	public static final Codec<CrystalPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(configuration -> configuration.tries),
			ExtraCodecs.NON_NEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(configuration -> configuration.xzSpread),
			ExtraCodecs.NON_NEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(configuration -> configuration.ySpread),
			PlacedFeature.CODEC.fieldOf("cluster_feature").forGetter(configuration -> configuration.clusterFeature),
			PlacedFeature.CODEC.fieldOf("small_bud_feature").forGetter(configuration -> configuration.smallBudFeature),
			PlacedFeature.CODEC.fieldOf("medium_bud_feature").forGetter(configuration -> configuration.mediumBudFeature),
			PlacedFeature.CODEC.fieldOf("large_bud_feature").forGetter(configuration -> configuration.largeBudFeature),
			Codec.INT.fieldOf("max_height").forGetter(configuration -> configuration.maxHeight),
			BlockStateProvider.CODEC.fieldOf("support_provider").forGetter(configuration -> configuration.fillingProvider),
			BlockStateProvider.CODEC.fieldOf("budding_provider").forGetter(configuration -> configuration.buddingProvider)
		).apply(instance, CrystalPatchConfiguration::new));
}
