package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GoldenIdolBlock extends HorizontalDirectionalBlock
{
	private static final VoxelShape ARTIFACT_HEAD = Block.box(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D);
	private static final VoxelShape ARTIFACT_BODY_1 = Block.box(6.0D, 0.0D, 7.0D, 10.0D, 12.0D, 9.0D);
	private static final VoxelShape ARTIFACT_BODY_2 = Block.box(7.0D, 0.0D, 6.0D, 9.0D, 12.0D, 10.0D);
	private static final VoxelShape RIGHT_HAND_1 = Block.box(4.0D, 6.0D, 7.0D, 6.0D, 12.0D, 9.0D);
	private static final VoxelShape LEFT_HAND_1 = Block.box(10.0D, 6.0D, 7.0D, 12.0D, 12.0D, 9.0D);
	private static final VoxelShape RIGHT_HAND_2 = Block.box(7.0D, 6.0D, 4.0D, 9.0D, 12.0D, 6.0D);
	private static final VoxelShape LEFT_HAND_2 = Block.box(7.0D, 6.0D, 10.0D, 9.0D, 12.0D, 12.0D);
	
	private static final VoxelShape ARTIFACT_STRAIGHT = Shapes.or(ARTIFACT_HEAD, ARTIFACT_BODY_1, RIGHT_HAND_1, LEFT_HAND_1);
	private static final VoxelShape ARTIFACT_TURNED = Shapes.or(ARTIFACT_HEAD, ARTIFACT_BODY_2, RIGHT_HAND_2, LEFT_HAND_2);
	
	public GoldenIdolBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	public BlockState rotate(BlockState state, Rotation rotation)
	{
	      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) 
	{
		return true;
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
	      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
    public RenderShape getRenderType(BlockState state) 
    {
        return RenderShape.MODEL;
    }
    
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision) 
    {
        Direction direction = state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? ARTIFACT_TURNED : ARTIFACT_STRAIGHT;
     }
    
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
	      p_206840_1_.add(FACING);
	   }
}
