package net.povstalec.sgjourney.common.blocks.transporter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter_controller.TransporterControllerEntity;
import net.povstalec.sgjourney.common.blocks.ProtectedBlock;

import javax.annotation.Nullable;

public abstract class TransporterControllerBlock extends HorizontalDirectionalBlock implements EntityBlock, ProtectedBlock
{
	public TransporterControllerBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
	{
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = reader.getBlockState(blockpos);
		
		return blockstate.isFaceSturdy(reader, blockpos, direction);
	}
	
	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(level.isClientSide())
			return;
		
		if(oldState.getBlock() != newState.getBlock())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof TransporterControllerEntity transporterController)
				transporterController.transporterCache.clearTwoWays();
		}
		
		super.onRemove(oldState, level, pos, newState, isMoving);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState blockstate = this.defaultBlockState();
		LevelReader levelreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction[] adirection = context.getNearestLookingDirections();
		
		for(Direction direction : adirection)
		{
			if(direction.getAxis().isHorizontal())
			{
				blockstate = blockstate.setValue(FACING, direction.getOpposite());
				if(blockstate.canSurvive(levelreader, blockpos))
					return blockstate;
			}
		}
		
		return null;
	}
	
	@Nullable
	public ProtectedBlockEntity getProtectedBlockEntity(BlockGetter reader, BlockPos pos, BlockState state)
	{
		BlockEntity blockEntity = reader.getBlockEntity(pos);
		
		if(blockEntity instanceof TransporterControllerEntity transporterController)
			return transporterController;
		
		return null;
	}
	
	@Override
	public boolean hasPermissions(BlockGetter reader, BlockPos pos, BlockState state, Player player, boolean sendMessage)
	{
		BlockEntity blockEntity = reader.getBlockEntity(pos);
		
		if(blockEntity instanceof TransporterControllerEntity transporterController)
			return transporterController.hasPermissions(player, sendMessage);
		
		return true;
	}
}
