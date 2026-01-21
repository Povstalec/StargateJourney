package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;

public class WeatheringPillarLampBlock extends WeatheringRotatedPillarBlock
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	
	public WeatheringPillarLampBlock(WeatherState weatherState, Properties properties, int lightLevel)
	{
		super(weatherState, properties.lightLevel(state -> state.getValue(WeatheringPillarLampBlock.LIT) ? lightLevel : 0));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateDefinition)
	{
		super.createBlockStateDefinition(stateDefinition);
		stateDefinition.add(LIT);
	}
	
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis()).setValue(LIT, Boolean.valueOf(context.getLevel().hasNeighborSignal(context.getClickedPos())));
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean p_55671_)
	{
		if(!level.isClientSide())
		{
			boolean flag = state.getValue(LIT);
			if(flag != level.hasNeighborSignal(pos))
			{
				if(flag)
					level.scheduleTick(pos, this, 4);
				else
					level.setBlock(pos, state.cycle(LIT), 2);
			}
			
		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		if(state.getValue(LIT) && !level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(LIT), 2);
	}
	
	@org.jetbrains.annotations.Nullable
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
	{
		ItemStack itemStack = context.getItemInHand();
		if(ToolActions.AXE_SCRAPE == toolAction && itemStack.canPerformAction(toolAction))
			return SGJourneyWeatheringBlock.getPrevious(state).orElse(null);
		
		return super.getToolModifiedState(state, context, toolAction, simulate);
	}
}
