package net.povstalec.sgjourney.common.blocks.transporter;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class AbstractTransportRingsBlock extends AbstractTransporterBlock
{
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);
	public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

	public AbstractTransportRingsBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(ACTIVATED, false));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(ACTIVATED);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getNearestLookingVerticalDirection().getOpposite());
	}
}
