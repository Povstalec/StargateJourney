package woldericz_junior.stargatejourney.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class BatteryBlock extends Block
{
	public BatteryBlock(Properties properties) 
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	@Nullable
	@Override
	   public BlockState getStateForPlacement(BlockItemUseContext context) 
	{
	      return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}
	    		  
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) 
	{
		builder.add(FACING);
	}
}
