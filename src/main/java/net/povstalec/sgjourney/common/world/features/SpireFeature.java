package net.povstalec.sgjourney.common.world.features;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.povstalec.sgjourney.common.world.features.configuration.SpireConfiguration;

public class SpireFeature extends Feature<SpireConfiguration>
{

	public SpireFeature(Codec<SpireConfiguration> codec)
	{
		super(codec);
	}
	
	public boolean place(FeaturePlaceContext<SpireConfiguration> context)
	{
		SpireConfiguration config = context.config();
		
		BlockPos blockpos = context.origin();
		RandomSource randomsource = context.random();
		
		WorldGenLevel worldgenlevel;
		for(worldgenlevel = context.level(); !worldgenlevel.getBlockState(blockpos).is(config.fillingProvider.getState(randomsource, blockpos).getBlock()) && blockpos.getY() > worldgenlevel.getMinBuildHeight() + 2; blockpos = blockpos.below())
		{
			
		}
		
		if(worldgenlevel.isEmptyBlock(blockpos) || !worldgenlevel.getBlockState(blockpos).is(config.canProtrudeThrough))
			return false;
		
		// Choose a random number of levels that make up the Spire
		int spireLevels = randomsource.nextIntBetweenInclusive(15, 25);
		int totalHeight = 0;
		
		// Each level is 5 blocks tall and had an ellipse like shape when viewed from above
		
		// The spire's center can shift, but it must not shift too much
		
		// This happens for each level
		for(int spireLevel = 0; spireLevel < spireLevels; spireLevel++)
		{
			int currentCenterX = randomsource.nextIntBetweenInclusive(-2, 2);
			int currentCenterZ = randomsource.nextIntBetweenInclusive(-2, 2);
			
			int xDeformation = randomsource.nextIntBetweenInclusive(-2, 2);
			int zDeformation = randomsource.nextIntBetweenInclusive(-2, 2);
			
			int ySize = randomsource.nextIntBetweenInclusive(4, 6);
			
			
			int levelRadius = 10 + spireLevels % 5 - (spireLevel / 3);
			
			int xRadius = levelRadius + xDeformation;
			int zRadius = levelRadius + zDeformation;
			
			for(int y = 0; y <= ySize; y++)
			{
				xRadius = adjustRadius(xRadius, ySize, y);
				zRadius = adjustRadius(zRadius, ySize, y);
				
				for(int x = -xRadius; x <= xRadius; x++)
				{
					for(int z = -zRadius; z <= zRadius; z++)
					{
						double xScale = Math.pow((double) (x - currentCenterX) / xRadius, 2);
						double zScale = Math.pow((double) (z - currentCenterZ) / zRadius, 2);
						if(xScale + zScale < 1)
						{
							int chance = randomsource.nextIntBetweenInclusive(1, 100);
							// Sometimes the block placed should be the ore
							if(chance > 5)
								setOre(worldgenlevel, blockpos, x, y, z, totalHeight, config, randomsource);
							else
								setFilling(worldgenlevel, blockpos, x, y, z, spireLevel, totalHeight, config, randomsource);
						}
					}
				}
			}
			totalHeight += ySize;
		}
		return true;
	}
	
	private void setOre(WorldGenLevel worldgenlevel, BlockPos blockpos, int x, int y, int z, int totalHeight, SpireConfiguration config, RandomSource randomsource)
	{
		this.setBlock(worldgenlevel, blockpos.offset(x, y + totalHeight, z), config.fillingProvider.getState(randomsource, blockpos));
	}
	
	private void setFilling(WorldGenLevel worldgenlevel, BlockPos blockpos, int x, int y, int z, int spireLevel, int totalHeight, SpireConfiguration config, RandomSource randomsource)
	{
		int chance = randomsource.nextIntBetweenInclusive(1, 100);
		if(chance - spireLevel < 7)
		{
			this.setBlock(worldgenlevel, blockpos.offset(x, y + totalHeight, z), Blocks.SANDSTONE.defaultBlockState());
		}
		else if(chance - spireLevel >= 7 && chance - spireLevel < 10)
		{
			this.setBlock(worldgenlevel, blockpos.offset(x, y + totalHeight, z), Blocks.GRANITE.defaultBlockState());
		}
		else
			this.setBlock(worldgenlevel, blockpos.offset(x, y + totalHeight, z), config.fillingPlacements.getState(randomsource, blockpos));
	}
	
	private int adjustRadius(int radius, int ySize, int y)
	{
		if(y == 0)
			return radius + 2;
		else if(y == ySize)
			return radius - 2;
		else if(y == 1)
			return radius + 1;
		else if(y == ySize - 1)
			return radius - 1;
		
		return radius;
	}
}
