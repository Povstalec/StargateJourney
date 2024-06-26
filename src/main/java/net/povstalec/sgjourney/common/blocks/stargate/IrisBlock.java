package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;
import net.povstalec.sgjourney.common.misc.VoxelShapeProvider;

public class IrisBlock extends Block implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final EnumProperty<ShieldingState> SHIELDING_STATE = EnumProperty.create("shielding_state", ShieldingState.class);
	public static final EnumProperty<ShieldingPart> PART = EnumProperty.create("stargate_part", ShieldingPart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected VoxelShapeProvider shapeProvider;

	public IrisBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ORIENTATION, Orientation.REGULAR)
				.setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(PART, ShieldingPart.ABOVE).setValue(SHIELDING_STATE, ShieldingState.OPEN));
		shapeProvider = new VoxelShapeProvider(width, horizontalOffset);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(WATERLOGGED).add(ORIENTATION).add(PART).add(SHIELDING_STATE);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
	{
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		Direction direction = state.getValue(FACING);
		Orientation orientation = state.getValue(ORIENTATION);
		
		/*boolean blocked = true;

		return switch (state.getValue(PART)) {
			case LEFT2, LEFT3_ABOVE -> getShapeFromArray(shapeProvider.CORNER_TOP_RIGHT, direction, orientation);
			case LEFT2_ABOVE -> getShapeFromArray(blocked ? shapeProvider.STAIR_TOP_RIGHT_BLOCKED : shapeProvider.STAIR_TOP_RIGHT, direction, orientation);
			case LEFT3_ABOVE5, LEFT2_ABOVE6 -> getShapeFromArray(shapeProvider.CORNER_BOTTOM_RIGHT, direction, orientation);
			case LEFT2_ABOVE5 -> getShapeFromArray(blocked ? shapeProvider.STAIR_BOTTOM_RIGHT_BLOCKED : shapeProvider.STAIR_BOTTOM_RIGHT, direction, orientation);
			case RIGHT2_ABOVE6, RIGHT3_ABOVE5 -> getShapeFromArray(shapeProvider.CORNER_BOTTOM_LEFT, direction, orientation);
			case RIGHT2_ABOVE5 -> getShapeFromArray(blocked ? shapeProvider.STAIR_BOTTOM_LEFT_BLOCKED : shapeProvider.STAIR_BOTTOM_LEFT, direction, orientation);
			case RIGHT3_ABOVE, RIGHT2 -> getShapeFromArray(shapeProvider.CORNER_TOP_LEFT, direction, orientation);
			case RIGHT2_ABOVE -> getShapeFromArray(blocked ? shapeProvider.STAIR_TOP_LEFT_BLOCKED : shapeProvider.STAIR_TOP_LEFT, direction, orientation);
			default -> getShapeFromArray(shapeProvider.FULL, direction, orientation);
		};*/
		
		if(orientation == Orientation.REGULAR)
			return direction.getAxis() == Direction.Axis.X ? shapeProvider.Z_IRIS_FULL : shapeProvider.X_IRIS_FULL;
		
		return shapeProvider.HORIZONTAL_IRIS_FULL;
	}
	
	public static void destroyShielding(Level level, BlockPos baseBlockPos, ArrayList<ShieldingPart> parts, Direction direction, Orientation orientation)
	{
		if(direction == null)
		{
			StargateJourney.LOGGER.error("Failed to destroy Shielding because direction is null");
			return;
		}
		
		if(orientation == null)
		{
			StargateJourney.LOGGER.error("Failed to destroy Shielding because orientation is null");
			return;
		}
		
		for(ShieldingPart part : parts)
		{
			BlockPos ringPos = part.getIrisPos(baseBlockPos, direction, orientation);
			BlockState state = level.getBlockState(ringPos);
			
			if(state.getBlock() instanceof IrisBlock)
			{
				boolean waterlogged = state.getValue(IrisBlock.WATERLOGGED);
				
				level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}
	
	public static void setIrisState(IrisBlock irisBlock, Level level, BlockPos baseBlockPos, ArrayList<ShieldingPart> parts, Direction direction, Orientation orientation, ShieldingState shieldingState)
	{
		if(direction == null)
		{
			StargateJourney.LOGGER.error("Failed to place Shielding because direction is null");
			return;
		}
		
		if(orientation == null)
		{
			StargateJourney.LOGGER.error("Failed to place Shielding because orientation is null");
			return;
		}
		
		for(ShieldingPart part : parts)
		{
			BlockPos ringPos = part.getIrisPos(baseBlockPos, direction, orientation);
			BlockState state = level.getBlockState(ringPos);
			
			// Remove Shielding Block
			if(state.getBlock() instanceof IrisBlock && !part.canExist(shieldingState))
			{
				boolean waterlogged = state.getValue(IrisBlock.WATERLOGGED);
				
				level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
				
			}
			// Change or place new Shielding Block
			else if(part.canExist(shieldingState))
			{
				if(state.getBlock() instanceof IrisBlock || state.is(Blocks.AIR) || state.is(Blocks.WATER))
				{
					level.setBlock(part.getIrisPos(baseBlockPos,  direction, orientation), 
							irisBlock.defaultBlockState()
							.setValue(IrisBlock.PART, part)
							.setValue(IrisBlock.FACING, direction)
							.setValue(IrisBlock.ORIENTATION, orientation)
							.setValue(IrisBlock.WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getIrisPos(baseBlockPos, direction, orientation)).getType() == Fluids.WATER)), 3);
				}
			}
			
		}
	}
}
