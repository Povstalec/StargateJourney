package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.init.BlockInit;

public class TollanStargateRingBlock extends AbstractStargateRingBlock
{
	public TollanStargateRingBlock(Properties properties)
	{
		super(properties, 3.0D, 1.0D);
	}

	@Override
	public ArrayList<StargatePart> getParts()
	{
		return StargatePart.TOLLAN_PARTS;
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
			case LEFT, LEFT2_ABOVE -> getShapeFromArray(shapeProvider.STAIR_TOP_RIGHT, direction, orientation);
			case LEFT2, LEFT3_ABOVE -> getShapeFromArray(shapeProvider.CORNER_TOP_RIGHT, direction, orientation);
			case LEFT3_ABOVE2, LEFT3_ABOVE3 -> getShapeFromArray(shapeProvider.LEFT, direction, orientation);
			case LEFT2_ABOVE4, LEFT_ABOVE5 -> getShapeFromArray(shapeProvider.STAIR_BOTTOM_RIGHT, direction, orientation);

			case LEFT2_ABOVE5, LEFT3_ABOVE4 -> getShapeFromArray(shapeProvider.CORNER_BOTTOM_RIGHT, direction, orientation);
			case ABOVE5 -> getShapeFromArray(shapeProvider.TOP, direction, orientation);
			case RIGHT_ABOVE5, RIGHT2_ABOVE4 -> getShapeFromArray(shapeProvider.STAIR_BOTTOM_LEFT, direction, orientation);
			case RIGHT2_ABOVE5, RIGHT3_ABOVE4 -> getShapeFromArray(shapeProvider.CORNER_BOTTOM_LEFT, direction, orientation);

			case RIGHT3_ABOVE2, RIGHT3_ABOVE3 -> getShapeFromArray(shapeProvider.RIGHT, direction, orientation);
			case RIGHT2_ABOVE, RIGHT -> getShapeFromArray(shapeProvider.STAIR_TOP_LEFT, direction, orientation);
			case RIGHT2, RIGHT3_ABOVE -> getShapeFromArray(shapeProvider.CORNER_TOP_LEFT, direction, orientation);

			default -> getShapeFromArray(shapeProvider.BOTTOM, direction, orientation);
		};
	}
}
