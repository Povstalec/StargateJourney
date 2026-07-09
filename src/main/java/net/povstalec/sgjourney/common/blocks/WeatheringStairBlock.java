package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WeatheringStairBlock extends StairBlock implements SGJourneyWeatheringBlock
{
	private final WeatherState weatherState;
	
	public WeatheringStairBlock(WeatherState weatherState, Supplier<BlockState> state, Properties properties)
	{
		super(state, properties);
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
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		if(tryApplyWax(state, level, pos, player, hand))
			return InteractionResult.SUCCESS;
		
		return super.use(state, level, pos, player, hand, result);
	}
}
