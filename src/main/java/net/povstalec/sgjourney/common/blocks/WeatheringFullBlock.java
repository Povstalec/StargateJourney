package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeatheringFullBlock extends Block implements SGJourneyWeatheringBlock
{
	private final SGJourneyWeatheringBlock.WeatherState weatherState;
	
	public WeatheringFullBlock(SGJourneyWeatheringBlock.WeatherState weatherState, BlockBehaviour.Properties properties)
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
	
	@Nullable
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
	{
		ItemStack itemStack = context.getItemInHand();
		if(ToolActions.AXE_SCRAPE == toolAction && itemStack.canPerformAction(toolAction))
			return SGJourneyWeatheringBlock.getPrevious(state).orElse(null);
		
		return super.getToolModifiedState(state, context, toolAction, simulate);
	}
}
