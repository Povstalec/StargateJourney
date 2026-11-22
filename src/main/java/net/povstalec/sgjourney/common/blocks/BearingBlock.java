package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BearingBlock extends Block implements SimpleWaterloggedBlock
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private static final VoxelShape TOP = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
	
	private static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape EAST = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
	private static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
	private static final VoxelShape WEST = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public BearingBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.UP)
				.setValue(POWERED, Boolean.FALSE)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(POWERED).add(WATERLOGGED);
	}
	
	public @NotNull BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotation().rotate(state.getValue(FACING)));
	}
	
	public @NotNull BlockState mirror(BlockState rotation, Mirror mirror)
	{
		return rotation.setValue(FACING, mirror.rotation().rotate(rotation.getValue(FACING)));
	}
	
	@Override
	public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos)
	{
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				.setValue(FACING, context.getClickedFace())
				.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos1, Block block, BlockPos pos2, boolean p_55671_)
	{
		if(!level.isClientSide())
		{
			boolean isPowered = state.getValue(POWERED);
			
			if(isPowered != level.hasNeighborSignal(pos1))
			{
				if(isPowered)
	               level.scheduleTick(pos1, this, 4);
	            else
	               level.setBlock(pos1, state.cycle(POWERED), 2);
			}

		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, Random randomSource)
	{
		if(state.getValue(POWERED) && !level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(POWERED), 2);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision)
	{
		return switch(state.getValue(FACING))
		{
			case DOWN -> TOP;
			case NORTH -> NORTH;
			case EAST -> EAST;
			case SOUTH -> SOUTH;
			case WEST -> WEST;
			default -> BOTTOM;
		};
	}
	
	private boolean canSupport(BlockState state, LevelReader levelReader, BlockPos pos, Direction direction)
	{
		BlockPos blockPos = pos.relative(direction.getOpposite());
		BlockState blockstate = levelReader.getBlockState(blockPos);
		
		return blockstate.isFaceSturdy(levelReader, pos, direction);
	}
	
	@Override
	public BlockState updateShape(BlockState state1, Direction direction, BlockState state2, LevelAccessor levelAccessor, BlockPos pos1, BlockPos pos2)
	{
		if(state1.getValue(WATERLOGGED))
			levelAccessor.scheduleTick(pos1, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));

		return !this.canSurvive(state1, levelAccessor, pos1) ?
				Blocks.AIR.defaultBlockState() : super.updateShape(state1, direction, state2, levelAccessor, pos1, pos2);
	}
	
	@Override
	public void onPlace(BlockState state1, Level level, BlockPos pos, BlockState state2, boolean p_55728_)
	{
		for(Direction direction : Direction.values())
		{
			level.updateNeighborsAt(pos.relative(direction), this);
		}
	}
}
