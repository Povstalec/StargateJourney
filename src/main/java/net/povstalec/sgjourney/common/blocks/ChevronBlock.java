package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChevronBlock extends Block
{
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;
	//TODO Add waterlogging

	private static final VoxelShape TOP = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
	
	private static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape EAST = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
	private static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
	private static final VoxelShape WEST = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	public ChevronBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(ORIENTATION, FrontAndTop.UP_NORTH)
				.setValue(LIT, Boolean.valueOf(false)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(ORIENTATION).add(LIT);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) 
	{
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		Direction direction = context.getClickedFace();
		Direction direction1;
		
		if(direction.getAxis() == Direction.Axis.Y)
			direction1 = context.getHorizontalDirection().getOpposite();
		else
			direction1 = Direction.UP;
		
		return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(direction, direction1));
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos1, Block block, BlockPos pos2, boolean p_55671_)
	{
		if(!level.isClientSide())
		{
			boolean isLit = state.getValue(LIT);
			
			if(isLit != level.hasNeighborSignal(pos1))
			{
				if(isLit)
	               level.scheduleTick(pos1, this, 4);
	            else
	               level.setBlock(pos1, state.cycle(LIT), 2);
			}

		}
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		if(state.getValue(LIT) && !level.hasNeighborSignal(pos))
			level.setBlock(pos, state.cycle(LIT), 2);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision)
	{
		FrontAndTop orientation = state.getValue(ORIENTATION);
		
		if(orientation == FrontAndTop.NORTH_UP)
			return NORTH;
		else if(orientation == FrontAndTop.EAST_UP)
			return EAST;
		else if(orientation == FrontAndTop.SOUTH_UP)
			return SOUTH;
		else if(orientation == FrontAndTop.WEST_UP)
			return WEST;
		
		else if(orientation.front() == Direction.DOWN)
			return TOP;
			
		return BOTTOM;
	}
	
	private boolean canSupport(BlockState state, LevelReader levelReader, BlockPos pos, Direction direction)
	{
		BlockPos blockPos = pos.relative(direction.getOpposite());
		BlockState blockstate = levelReader.getBlockState(blockPos);
		
		return blockstate.isFaceSturdy(levelReader, pos, direction);
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos)
	{
		FrontAndTop orientation = state.getValue(ORIENTATION);
		
		if(orientation == FrontAndTop.NORTH_UP)
			return canSupport(state, levelReader, pos, Direction.NORTH);
		else if(orientation == FrontAndTop.EAST_UP)
			return canSupport(state, levelReader, pos, Direction.EAST);
		else if(orientation == FrontAndTop.SOUTH_UP)
			return canSupport(state, levelReader, pos, Direction.SOUTH);
		else if(orientation == FrontAndTop.WEST_UP)
			return canSupport(state, levelReader, pos, Direction.WEST);
		
		else if(orientation.front() == Direction.DOWN)
			return canSupport(state, levelReader, pos, Direction.DOWN);
			
		return canSupport(state, levelReader, pos, Direction.UP);
	}
	
	@Override
	public BlockState updateShape(BlockState state1, Direction direction, BlockState state2, LevelAccessor levelAccessor, BlockPos pos1, BlockPos pos2)
	{
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
