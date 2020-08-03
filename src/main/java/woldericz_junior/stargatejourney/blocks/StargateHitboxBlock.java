package woldericz_junior.stargatejourney.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class StargateHitboxBlock extends Block
{
	public StargateHitboxBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	protected static final VoxelShape STARGATE_STRAIGHT = Block.makeCuboidShape(0.0D, 0.0D, 4.5D, 16.0D, 16.0D, 11.5D);
	protected static final VoxelShape STARGATE_TURNED = Block.makeCuboidShape(4.5D, 0.0D, 0.0D, 11.5D, 16.0D, 16.0D);
	
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) 
	{
		return true;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) 
	{
		builder.add(FACING);
	}
	
	   public BlockRenderType getRenderType(BlockState state) {
		      return BlockRenderType.INVISIBLE;
		   }
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
    {
   	switch((Direction)state.get(FACING)) 
        {
        case NORTH:
        	return STARGATE_STRAIGHT;
        case SOUTH:
        	return STARGATE_STRAIGHT;
        case WEST:
        	return STARGATE_TURNED;
        case EAST:
        default:
        	return STARGATE_TURNED;
        }
    }
}
