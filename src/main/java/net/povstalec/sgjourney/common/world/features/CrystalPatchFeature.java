package net.povstalec.sgjourney.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.povstalec.sgjourney.common.world.features.configuration.CrystalPatchConfiguration;

public class CrystalPatchFeature extends Feature<CrystalPatchConfiguration>
{
	public static int BUDDING_CHANCE = 10;
	
	public CrystalPatchFeature(Codec<CrystalPatchConfiguration> codec)
	{
		super(codec);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<CrystalPatchConfiguration> context)
	{
		CrystalPatchConfiguration configuration = context.config();
		RandomSource randomSource = context.random();
		BlockPos pos = context.origin();
		WorldGenLevel level = context.level();
		
		int placed = 0;
		BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
		int xzSpread = configuration.xzSpread() + 1;
		int ySpread = configuration.ySpread() + 1;
		
		for(int i = 0; i < configuration.tries(); ++i)
		{
			mutablePos.setWithOffset(pos, randomSource.nextInt(xzSpread) - randomSource.nextInt(xzSpread), randomSource.nextInt(ySpread) - randomSource.nextInt(ySpread), randomSource.nextInt(xzSpread) - randomSource.nextInt(xzSpread));
			
			if(mutablePos.getY() <= configuration.maxHeight() && level.getBlockState(mutablePos.below()).is(configuration.fillingProvider().getState(randomSource, mutablePos).getBlock()))
			{
				if(randomFeature(configuration, randomSource).value().place(level, context.chunkGenerator(), randomSource, mutablePos))
					++placed;
				
				if(randomSource.nextInt(BUDDING_CHANCE) == 0)
					setBlock(level, mutablePos.below(), configuration.buddingProvider().getState(randomSource, mutablePos));
			}
		}
		
		return placed > 0;
	}
	
	private Holder<PlacedFeature> randomFeature(CrystalPatchConfiguration configuration, RandomSource randomSource)
	{
		return switch(Math.abs(randomSource.nextInt(4)))
		{
			case 1 -> configuration.smallBudFeature();
			case 2 -> configuration.mediumBudFeature();
			case 3 -> configuration.largeBudFeature();
			default -> configuration.clusterFeature();
		};
	}
}
