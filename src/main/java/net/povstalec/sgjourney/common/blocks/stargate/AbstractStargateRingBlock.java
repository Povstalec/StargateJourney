package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public abstract class AbstractStargateRingBlock extends AbstractStargateBlock
{
	public AbstractStargateRingBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties, width, horizontalOffset);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		Direction direction = state.getValue(FACING);
		Orientation orientation = state.getValue(ORIENTATION);

		return switch (state.getValue(PART)) {
			case LEFT2, LEFT3_ABOVE -> getShapeFromArray(shapeProvider.CORNER_TOP_RIGHT, direction, orientation);
			case LEFT2_ABOVE -> getShapeFromArray(shapeProvider.STAIR_TOP_RIGHT, direction, orientation);
			case LEFT3_ABOVE5, LEFT2_ABOVE6 -> getShapeFromArray(shapeProvider.CORNER_BOTTOM_RIGHT, direction, orientation);
			case LEFT2_ABOVE5 -> getShapeFromArray(shapeProvider.STAIR_BOTTOM_RIGHT, direction, orientation);
			case RIGHT2_ABOVE6, RIGHT3_ABOVE5 -> getShapeFromArray(shapeProvider.CORNER_BOTTOM_LEFT, direction, orientation);
			case RIGHT2_ABOVE5 -> getShapeFromArray(shapeProvider.STAIR_BOTTOM_LEFT, direction, orientation);
			case RIGHT3_ABOVE, RIGHT2 -> getShapeFromArray(shapeProvider.CORNER_TOP_LEFT, direction, orientation);
			case RIGHT2_ABOVE -> getShapeFromArray(shapeProvider.STAIR_TOP_LEFT, direction, orientation);
			default -> getShapeFromArray(shapeProvider.FULL, direction, orientation);
		};
	}

	private boolean isWaterLogged(BlockState state, Level level, BlockPos pos)
	{
		FluidState fluidState = level.getFluidState(pos);
		
		if(fluidState.getType() == Fluids.WATER)
			return true;
		
		return state.getBlock() instanceof AbstractStargateBlock ? state.getValue(AbstractStargateBlock.WATERLOGGED) : false;
	}
	
	@Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (oldState.getBlock() != newState.getBlock())
		{
			BlockPos centerPos = oldState.getValue(PART).getBaseBlockPos(pos, oldState.getValue(FACING), oldState.getValue(ORIENTATION));
			BlockState centerState = level.getBlockState(centerPos);
			level.setBlock(centerPos, isWaterLogged(centerState, level, centerPos) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
			
	        super.onRemove(oldState, level, pos, newState, isMoving);
		}
    }
	
	@Override
	public AbstractStargateEntity getStargate(Level level, BlockPos pos, BlockState state)
	{
		BlockEntity blockentity = level.getBlockEntity(state.getValue(PART).getBaseBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION)));
		
		if(blockentity instanceof AbstractStargateEntity stargate)
			return stargate;
		
		return null;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		StargatePart part = state.getValue(PART);
		BlockPos baseBlockPos = part.getBaseBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION));
		
		if(level.getBlockState(baseBlockPos).getBlock() instanceof AbstractStargateBaseBlock baseBlock)
			return baseBlock.setVariant(level, baseBlockPos, player, hand) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		
		return InteractionResult.FAIL;
	}

	/*@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
		BlockPos mainBlockPos = state.getValue(PART).getMainBlockPos(pos, state.getValue(FACING));
    	
		BlockEntity blockEntity = level.getBlockEntity(mainBlockPos);
		
    	if(blockEntity instanceof AbstractStargateEntity stargate) 
    	{
    		
    	}
        
        if(!player.isShiftKeyDown() && !player.getItemInHand(hand).isEmpty() && !state.getValue(FULL))
		{
    		if(level.isClientSide)
    			return InteractionResult.SUCCESS;
    		
			//ItemStack stack = player.getItemInHand(hand);
			//player.sendSystemMessage(stack.getDisplayName());
        	level.setBlock(pos, state.setValue(FULL, true), 2);
			
			return InteractionResult.CONSUME;
		}
		else if(!player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty() && state.getValue(FULL))
		{
			if(level.isClientSide)
    			return InteractionResult.SUCCESS;
			
        	level.setBlock(pos, state.setValue(FULL, false), 2);
			return InteractionResult.CONSUME;
		}
    	
        return InteractionResult.FAIL;
    }*/
}
