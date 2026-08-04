package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import java.util.Random;

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
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random randomSource)
	{
		if(state.getValue(LIT) && !level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(LIT), 2);
	}
}
