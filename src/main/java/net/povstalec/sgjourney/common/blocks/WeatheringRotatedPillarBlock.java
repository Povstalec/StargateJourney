package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WeatheringRotatedPillarBlock extends RotatedPillarBlock implements SGJourneyWeatheringBlock
{
	private final SGJourneyWeatheringBlock.WeatherState weatherState;
	
	public WeatheringRotatedPillarBlock(SGJourneyWeatheringBlock.WeatherState weatherState, Properties properties)
	{
		super(properties);
		this.weatherState = weatherState;
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		this.onRandomTick(state, level, pos, randomSource);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state)
	{
		return SGJourneyWeatheringBlock.getNext(state.getBlock()).isPresent();
	}
	
	@Override
	public @NotNull WeatherState getAge()
	{
		return this.weatherState;
	}
}
