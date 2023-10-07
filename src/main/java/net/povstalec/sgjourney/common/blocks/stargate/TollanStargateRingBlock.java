package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class TollanStargateRingBlock extends AbstractStargateRingBlock
{
	public TollanStargateRingBlock(Properties properties)
	{
		super(properties, 3.0D, 1.0D);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.TOLLAN;
	}

	@Override
	public Item asItem()
	{
		return BlockInit.TOLLAN_STARGATE.get().asItem();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		Direction direction = state.getValue(FACING);
		Orientation orientation = state.getValue(ORIENTATION);

		return switch (state.getValue(PART)) {
			case LEFT, LEFT2_ABOVE -> getShapeFromArray(SHAPE_PROVIDER.STAIR_TOP_RIGHT, direction, orientation);
			case LEFT2, LEFT3_ABOVE -> getShapeFromArray(SHAPE_PROVIDER.CORNER_TOP_RIGHT, direction, orientation);
			case LEFT3_ABOVE2, LEFT3_ABOVE3 -> getShapeFromArray(SHAPE_PROVIDER.LEFT, direction, orientation);
			case LEFT2_ABOVE4, LEFT_ABOVE5 -> getShapeFromArray(SHAPE_PROVIDER.STAIR_BOTTOM_RIGHT, direction, orientation);

			case LEFT2_ABOVE5, LEFT3_ABOVE4 -> getShapeFromArray(SHAPE_PROVIDER.CORNER_BOTTOM_RIGHT, direction, orientation);
			case ABOVE5 -> getShapeFromArray(SHAPE_PROVIDER.TOP, direction, orientation);
			case RIGHT_ABOVE5, RIGHT2_ABOVE4 -> getShapeFromArray(SHAPE_PROVIDER.STAIR_BOTTOM_LEFT, direction, orientation);
			case RIGHT2_ABOVE5, RIGHT3_ABOVE4 -> getShapeFromArray(SHAPE_PROVIDER.CORNER_BOTTOM_LEFT, direction, orientation);

			case RIGHT3_ABOVE2, RIGHT3_ABOVE3 -> getShapeFromArray(SHAPE_PROVIDER.RIGHT, direction, orientation);
			case RIGHT2_ABOVE, RIGHT -> getShapeFromArray(SHAPE_PROVIDER.STAIR_TOP_LEFT, direction, orientation);
			case RIGHT2, RIGHT3_ABOVE -> getShapeFromArray(SHAPE_PROVIDER.CORNER_TOP_LEFT, direction, orientation);

			default -> getShapeFromArray(SHAPE_PROVIDER.BOTTOM, direction, orientation);
		};
	}
}
