package net.povstalec.sgjourney.common.world.features.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class SpireConfiguration implements FeatureConfiguration
{
	public static final Codec<SpireConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockStateProvider.CODEC.fieldOf("filling_provider").forGetter((config) ->
			{
				return config.fillingProvider;
			}),
			BlockStateProvider.CODEC.fieldOf("filling_placements").forGetter((config) ->
			{
				return config.fillingPlacements;
			}),
			TagKey.hashedCodec(Registries.BLOCK).fieldOf("can_protrude_through").forGetter((config) ->
			{
				return config.canProtrudeThrough;
			})
			).apply(instance, SpireConfiguration::new));
	
	public final BlockStateProvider fillingProvider; // What the spire is made out of
	public final BlockStateProvider fillingPlacements; // What generates in the spire
	public final TagKey<Block> canProtrudeThrough;
	
	public SpireConfiguration(BlockStateProvider fillingProvider, BlockStateProvider fillingPlacements, TagKey<Block> canProtrudeThrough)
	{
		this.fillingProvider = fillingProvider;
		this.fillingPlacements = fillingPlacements;
		this.canProtrudeThrough = canProtrudeThrough;
	}
	
	
	public static class SpireConfigurationBuilder
	{
		public final BlockStateProvider fillingProvider;
		public final BlockStateProvider fillingPlacements;
		public final TagKey<Block> canProtrudeThrough;
		
		public SpireConfigurationBuilder(BlockStateProvider fillingProvider, BlockStateProvider fillingPlacements, TagKey<Block> canProtrudeThrough)
		{
			this.fillingProvider = fillingProvider;
			this.fillingPlacements = fillingPlacements;
			this.canProtrudeThrough = canProtrudeThrough;
		}
		
		public SpireConfiguration build()
		{
			return new SpireConfiguration(this.fillingProvider, this.fillingPlacements, this.canProtrudeThrough);
		}
	}
}
