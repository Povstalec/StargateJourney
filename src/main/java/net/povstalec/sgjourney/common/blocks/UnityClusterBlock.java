package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class UnityClusterBlock extends Block implements SimpleWaterloggedBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	protected final VoxelShape northAabb;
	protected final VoxelShape southAabb;
	protected final VoxelShape eastAabb;
	protected final VoxelShape westAabb;
	protected final VoxelShape upAabb;
	protected final VoxelShape downAabb;
	
	public UnityClusterBlock(int height, int widthInverse, BlockBehaviour.Properties properties)
	{
		super(properties);
		
		this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false).setValue(FACING, Direction.UP));
		
		this.upAabb = Block.box(widthInverse, 0.0, widthInverse, 16 - widthInverse, height, 16 - widthInverse);
		this.downAabb = Block.box(widthInverse, 16 - height, widthInverse, 16 - widthInverse, 16.0, 16 - widthInverse);
		this.northAabb = Block.box(widthInverse, widthInverse, 16 - height, 16 - widthInverse, 16 - widthInverse, 16.0);
		this.southAabb = Block.box(widthInverse, widthInverse, 0.0, 16 - widthInverse, 16 - widthInverse, height);
		this.eastAabb = Block.box(0.0, widthInverse, widthInverse, height, 16 - widthInverse, 16 - widthInverse);
		this.westAabb = Block.box(16 - height, widthInverse, widthInverse, 16.0, 16 - widthInverse, 16 - widthInverse);
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(WATERLOGGED).add(FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
	{
		Direction direction = state.getValue(FACING);
		switch (direction)
		{
			case NORTH:
				return this.northAabb;
			case SOUTH:
				return this.southAabb;
			case EAST:
				return this.eastAabb;
			case WEST:
				return this.westAabb;
			case DOWN:
				return this.downAabb;
			default:
				return this.upAabb;
		}
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
	{
		Direction direction = state.getValue(FACING);
		BlockPos oppositePos = pos.relative(direction.getOpposite());
		
		return reader.getBlockState(oppositePos).isFaceSturdy(reader, oppositePos, direction);
	}
	
	@Override
	public BlockState updateShape(BlockState oldState, Direction direction, BlockState newState, LevelAccessor level, BlockPos oldPos, BlockPos newPos)
	{
		if(oldState.getValue(WATERLOGGED))
			level.scheduleTick(oldPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		
		return direction == oldState.getValue(FACING).getOpposite() && !oldState.canSurvive(level, oldPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(oldState, direction, newState, level, oldPos, newPos);
	}
	
	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		LevelAccessor level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		
		return this.defaultBlockState().setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER).setValue(FACING, context.getClickedFace());
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	public PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.DESTROY;
	}
}
