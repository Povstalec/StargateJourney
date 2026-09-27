package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GoldenIdolBlock extends HorizontalDirectionalBlock
{
	private static final VoxelShape ARTIFACT_HEAD = Block.box(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D);
	private static final VoxelShape ARTIFACT_BODY_Z = Block.box(6.0D, 0.0D, 7.0D, 10.0D, 12.0D, 9.0D);
	private static final VoxelShape ARTIFACT_BODY_X = Block.box(7.0D, 0.0D, 6.0D, 9.0D, 12.0D, 10.0D);
	private static final VoxelShape RIGHT_HAND_Z = Block.box(4.0D, 6.0D, 7.0D, 6.0D, 12.0D, 9.0D);
	private static final VoxelShape LEFT_HAND_Z = Block.box(10.0D, 6.0D, 7.0D, 12.0D, 12.0D, 9.0D);
	private static final VoxelShape RIGHT_HAND_X = Block.box(7.0D, 6.0D, 4.0D, 9.0D, 12.0D, 6.0D);
	private static final VoxelShape LEFT_HAND_X = Block.box(7.0D, 6.0D, 10.0D, 9.0D, 12.0D, 12.0D);
	
	private static final VoxelShape ARTIFACT_Z = Shapes.or(ARTIFACT_HEAD, ARTIFACT_BODY_Z, RIGHT_HAND_Z, LEFT_HAND_Z);
	private static final VoxelShape ARTIFACT_X = Shapes.or(ARTIFACT_HEAD, ARTIFACT_BODY_X, RIGHT_HAND_X, LEFT_HAND_X);
	
	public GoldenIdolBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}
	
	public @NotNull BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	@Override
	public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos)
	{
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext collision)
	{
		Direction direction = state.getValue(FACING);
		return direction.getAxis() == Direction.Axis.X ? ARTIFACT_X : ARTIFACT_Z;
	}
}
