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

public class ShieldingBlock extends Block implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final EnumProperty<ShieldingState> SHIELDING_STATE = EnumProperty.create("shielding_state", ShieldingState.class);
	public static final EnumProperty<ShieldingPart> PART = EnumProperty.create("shielding_part", ShieldingPart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected VoxelShapeProvider shapeProvider;

	public ShieldingBlock(Properties properties, double width, double horizontalOffset)
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
		ShieldingState shieldingState = state.getValue(SHIELDING_STATE);
		
		return switch (state.getValue(PART))
		{
			// Outer parts
			case LEFT_ABOVE5, ABOVE5, RIGHT_ABOVE5 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_TOP, direction, orientation);
			case LEFT2_ABOVE4, LEFT2_ABOVE3, LEFT2_ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_LEFT, direction, orientation);
			case RIGHT2_ABOVE2, RIGHT2_ABOVE3, RIGHT2_ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_RIGHT, direction, orientation);
			case LEFT_ABOVE, ABOVE, RIGHT_ABOVE -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_BOTTOM, direction, orientation);
			
			// Inner corner parts
			case LEFT_ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_BOTTOM_RIGHT) : 
						shapeProvider.IRIS_CORNER_TOP_LEFT, direction, orientation);
			case RIGHT_ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_BOTTOM_LEFT) : 
						shapeProvider.IRIS_CORNER_TOP_RIGHT, direction, orientation);
			case LEFT_ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_TOP_RIGHT) : 
						shapeProvider.IRIS_CORNER_BOTTOM_LEFT, direction, orientation);
			case RIGHT_ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_TOP_LEFT) : 
						shapeProvider.IRIS_CORNER_BOTTOM_RIGHT, direction, orientation);

			// Inner parts
			case ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_TOP, direction, orientation);
			case LEFT_ABOVE3 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_LEFT, direction, orientation);
			case RIGHT_ABOVE3 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_RIGHT, direction, orientation);
			case ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_BOTTOM, direction, orientation);
			
			default -> VoxelShapeProvider.getShapeFromArray(shapeProvider.IRIS_FULL, direction, orientation);
		};
		
		//if(orientation == Orientation.REGULAR)
		//	return direction.getAxis() == Direction.Axis.X ? shapeProvider.Z_IRIS_FULL : shapeProvider.X_IRIS_FULL;
		
		//return shapeProvider.HORIZONTAL_IRIS_FULL;
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
			BlockPos ringPos = part.getShieldingPos(baseBlockPos, direction, orientation);
			BlockState state = level.getBlockState(ringPos);
			
			if(state.getBlock() instanceof ShieldingBlock)
			{
				boolean waterlogged = state.getValue(ShieldingBlock.WATERLOGGED);
				
				level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}
	
	public static void setIrisState(ShieldingBlock irisBlock, Level level, BlockPos baseBlockPos, ArrayList<ShieldingPart> parts, Direction direction, Orientation orientation, ShieldingState shieldingState)
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
			BlockPos ringPos = part.getShieldingPos(baseBlockPos, direction, orientation);
			BlockState state = level.getBlockState(ringPos);
			
			// Remove Shielding Block
			if(state.getBlock() instanceof ShieldingBlock && !part.canExist(shieldingState))
			{
				boolean waterlogged = state.getValue(ShieldingBlock.WATERLOGGED);
				
				level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
				
			}
			// Change or place new Shielding Block
			else if(part.canExist(shieldingState))
			{
				if(state.getBlock() instanceof ShieldingBlock || state.is(Blocks.AIR) || state.is(Blocks.WATER))
				{
					level.setBlock(part.getShieldingPos(baseBlockPos,  direction, orientation), 
							irisBlock.defaultBlockState()
							.setValue(ShieldingBlock.SHIELDING_STATE, shieldingState)
							.setValue(ShieldingBlock.PART, part)
							.setValue(ShieldingBlock.FACING, direction)
							.setValue(ShieldingBlock.ORIENTATION, orientation)
							.setValue(ShieldingBlock.WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getShieldingPos(baseBlockPos, direction, orientation)).getType() == Fluids.WATER)), 3);
				}
			}
			
		}
	}
}
