package net.povstalec.sgjourney.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.block_entities.AbstractStargateEntity;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.stargate.StargatePart;

public abstract class AbstractStargateRingBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<StargatePart> PART = EnumProperty.create("stargate_part", StargatePart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected static final VoxelShape FULL = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	protected static final VoxelShape UPWARD = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape UPWARD_BOTTOM_LEFT = Block.box(0.0D, 1.0D, 0.0D, 8.0D, 8.0D, 8.0D);
	protected static final VoxelShape UPWARD_BOTTOM_RIGHT = Block.box(8.0D, 1.0D, 0.0D, 16.0D, 8.0D, 8.0D);
	protected static final VoxelShape UPWARD_TOP_LEFT = Block.box(0.0D, 1.0D, 8.0D, 8.0D, 8.0D, 16.0D);
	protected static final VoxelShape UPWARD_TOP_RIGHT = Block.box(8.0D, 1.0D, 8.0D, 18.0D, 8.0D, 18.0D);
	
	protected static final VoxelShape X = Block.box(0.0D, 0.0D, 4.5D, 16.0D, 16.0D, 11.5D);
	protected static final VoxelShape X_TOP_LEFT = Block.box(0.0D, 8.0D, 4.5D, 8.0D, 16.0D, 11.5D);
	protected static final VoxelShape X_TOP_RIGHT = Block.box(8.0D, 8.0D, 4.5D, 16.0D, 16.0D, 11.5D);
	protected static final VoxelShape X_BOTTOM_LEFT = Block.box(0.0D, 0.0D, 4.5D, 8.0D, 8.0D, 11.5D);
	protected static final VoxelShape X_BOTTOM_RIGHT = Block.box(8.0D, 0.0D, 4.5D, 16.0D, 8.0D, 11.5D);
	
	protected static final VoxelShape X_STAIRS_TOP_RIGHT =  Shapes.or(X_BOTTOM_LEFT, X_BOTTOM_RIGHT, X_TOP_RIGHT);
	protected static final VoxelShape X_STAIRS_TOP_LEFT =  Shapes.or(X_BOTTOM_LEFT, X_BOTTOM_RIGHT, X_TOP_LEFT);
	protected static final VoxelShape X_STAIRS_BOTTOM_RIGHT =  Shapes.or(X_TOP_LEFT, X_TOP_RIGHT, X_BOTTOM_RIGHT);
	protected static final VoxelShape X_STAIRS_BOTTOM_LEFT =  Shapes.or(X_TOP_LEFT, X_TOP_RIGHT, X_BOTTOM_LEFT);
	
	protected static final VoxelShape Z = Block.box(4.5D, 0.0D, 0.0D, 11.5D, 16.0D, 16.0D);
	protected static final VoxelShape Z_TOP_LEFT = Block.box(4.5D, 8.0D, 0.0D, 11.5D, 16.0D, 8.0D);
	protected static final VoxelShape Z_TOP_RIGHT = Block.box(4.5D, 8.0D, 8.0D, 11.5D, 16.0D, 16.0D);
	protected static final VoxelShape Z_BOTTOM_LEFT = Block.box(4.5D, 0.0D, 0.0D, 11.5D, 8.0D, 8.0D);
	protected static final VoxelShape Z_BOTTOM_RIGHT = Block.box(4.5D, 0.0D, 8.0D, 11.5D, 8.0D, 16.0D);
	
	protected static final VoxelShape Z_STAIRS_TOP_RIGHT =  Shapes.or(Z_BOTTOM_LEFT, Z_BOTTOM_RIGHT, Z_TOP_RIGHT);
	protected static final VoxelShape Z_STAIRS_TOP_LEFT =  Shapes.or(Z_BOTTOM_LEFT, Z_BOTTOM_RIGHT, Z_TOP_LEFT);
	protected static final VoxelShape Z_STAIRS_BOTTOM_RIGHT =  Shapes.or(Z_TOP_LEFT, Z_TOP_RIGHT, Z_BOTTOM_RIGHT);
	protected static final VoxelShape Z_STAIRS_BOTTOM_LEFT =  Shapes.or(Z_TOP_LEFT, Z_TOP_RIGHT, Z_BOTTOM_LEFT);

	public AbstractStargateRingBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(PART, StargatePart.ABOVE6));
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(PART).add(WATERLOGGED);
	}
	
	public BlockState rotate(BlockState state, Rotation rotation)
	{
	      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		if(state.getValue(FACING) == Direction.NORTH)
		{
			switch(state.getValue(PART))
			{
			case LEFT2:
				return X_TOP_LEFT;
			case LEFT2_ABOVE:
				return X_STAIRS_TOP_RIGHT;
			case LEFT3_ABOVE:
				return X_TOP_LEFT;
				
			case LEFT3_ABOVE5:
				return X_BOTTOM_LEFT;
			case LEFT2_ABOVE5:
				return X_STAIRS_BOTTOM_RIGHT;
			case LEFT2_ABOVE6:
				return X_BOTTOM_LEFT;
				
			case RIGHT2_ABOVE6:
				return X_BOTTOM_RIGHT;
			case RIGHT2_ABOVE5:
				return X_STAIRS_BOTTOM_LEFT;
			case RIGHT3_ABOVE5:
				return X_BOTTOM_RIGHT;
				
			case RIGHT3_ABOVE:
				return X_TOP_RIGHT;
			case RIGHT2_ABOVE:
				return X_STAIRS_TOP_LEFT;
			case RIGHT2:
				return X_TOP_RIGHT;
			default:
				return X;
			}
		}
		else if(state.getValue(FACING) == Direction.EAST)
		{
			switch(state.getValue(PART))
			{
			case LEFT2:
				return Z_TOP_LEFT;
			case LEFT2_ABOVE:
				return Z_STAIRS_TOP_RIGHT;
			case LEFT3_ABOVE:
				return Z_TOP_LEFT;
				
			case LEFT3_ABOVE5:
				return Z_BOTTOM_LEFT;
			case LEFT2_ABOVE5:
				return Z_STAIRS_BOTTOM_RIGHT;
			case LEFT2_ABOVE6:
				return Z_BOTTOM_LEFT;
				
			case RIGHT2_ABOVE6:
				return Z_BOTTOM_RIGHT;
			case RIGHT2_ABOVE5:
				return Z_STAIRS_BOTTOM_LEFT;
			case RIGHT3_ABOVE5:
				return Z_BOTTOM_RIGHT;
				
			case RIGHT3_ABOVE:
				return Z_TOP_RIGHT;
			case RIGHT2_ABOVE:
				return Z_STAIRS_TOP_LEFT;
			case RIGHT2:
				return Z_TOP_RIGHT;
			default:
				return Z;
			}
		}
		else if(state.getValue(FACING) == Direction.WEST)
		{
			switch(state.getValue(PART))
			{
			case RIGHT2:
				return Z_TOP_LEFT;
			case RIGHT2_ABOVE:
				return Z_STAIRS_TOP_RIGHT;
			case RIGHT3_ABOVE:
				return Z_TOP_LEFT;
				
			case RIGHT3_ABOVE5:
				return Z_BOTTOM_LEFT;
			case RIGHT2_ABOVE5:
				return Z_STAIRS_BOTTOM_RIGHT;
			case RIGHT2_ABOVE6:
				return Z_BOTTOM_LEFT;
				
			case LEFT2_ABOVE6:
				return Z_BOTTOM_RIGHT;
			case LEFT2_ABOVE5:
				return Z_STAIRS_BOTTOM_LEFT;
			case LEFT3_ABOVE5:
				return Z_BOTTOM_RIGHT;
				
			case LEFT3_ABOVE:
				return Z_TOP_RIGHT;
			case LEFT2_ABOVE:
				return Z_STAIRS_TOP_LEFT;
			case LEFT2:
				return Z_TOP_RIGHT;
			default:
				return Z;
			}
		}
		else
		{
			switch(state.getValue(PART))
			{
			case RIGHT2:
				return X_TOP_LEFT;
			case RIGHT2_ABOVE:
				return X_STAIRS_TOP_RIGHT;
			case RIGHT3_ABOVE:
				return X_TOP_LEFT;
				
			case RIGHT3_ABOVE5:
				return X_BOTTOM_LEFT;
			case RIGHT2_ABOVE5:
				return X_STAIRS_BOTTOM_RIGHT;
			case RIGHT2_ABOVE6:
				return X_BOTTOM_LEFT;
				
			case LEFT2_ABOVE6:
				return X_BOTTOM_RIGHT;
			case LEFT2_ABOVE5:
				return X_STAIRS_BOTTOM_LEFT;
			case LEFT3_ABOVE5:
				return X_BOTTOM_RIGHT;
				
			case LEFT3_ABOVE:
				return X_TOP_RIGHT;
			case LEFT2_ABOVE:
				return X_STAIRS_TOP_LEFT;
			case LEFT2:
				return X_TOP_RIGHT;
			default:
				return X;
			}
		}
	}
	
	public BlockState updateShape(BlockState oldState, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos oldPos, BlockPos newPos)
	{
		if (oldState.getValue(WATERLOGGED))
		{
			levelAccessor.scheduleTick(oldPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
		}

		return super.updateShape(oldState, direction, newState, levelAccessor, oldPos, newPos);
	}
	
	@Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (oldState.getBlock() != newState.getBlock())
		{
			level.setBlock(StargatePart.getMainBlockPos(pos, oldState.getValue(FACING), oldState.getValue(PART)), Blocks.AIR.defaultBlockState(), 35);
			
	        super.onRemove(oldState, level, pos, newState, isMoving);
		}
    }
	
	public PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.BLOCK;
	}
    
    public abstract Block getStargate();
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(StargatePart.getMainBlockPos(pos, state.getValue(FACING), state.getValue(PART)));
		if (blockentity instanceof AbstractStargateEntity stargate)
		{
			if (!level.isClientSide)
			{
				ItemStack itemstack = new ItemStack(getStargate());
				
				blockentity.saveToItem(itemstack);
				/*if (stargate.hasCustomName())
				{
					itemstack.setHoverName(stargate.getCustomName());
				}*/

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
		}
}
