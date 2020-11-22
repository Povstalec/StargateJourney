package woldericz_junior.stargatejourney.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public class ArcheologyTableBlock extends Block
{
	public ArcheologyTableBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
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
}
