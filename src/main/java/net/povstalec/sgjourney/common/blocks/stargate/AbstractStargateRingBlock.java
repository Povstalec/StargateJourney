package net.povstalec.sgjourney.common.blocks.stargate;

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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.VoxelDimensionProvider;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public abstract class AbstractStargateRingBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final EnumProperty<StargatePart> PART = EnumProperty.create("stargate_part", StargatePart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
	public static final IntegerProperty CHEVRONS_ACTIVE = IntegerProperty.create("chevrons_active", 0, 9);
	
	//TODO
	//public static final BooleanProperty FULL = BooleanProperty.create("full");
	private static final VoxelDimensionProvider VOXEL_COORDS = new VoxelDimensionProvider();

	public AbstractStargateRingBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ORIENTATION, Orientation.REGULAR).setValue(CONNECTED, Boolean.valueOf(false)).setValue(CHEVRONS_ACTIVE, 0).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(PART, StargatePart.ABOVE6)/*.setValue(FULL, Boolean.valueOf(false))*/);
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(ORIENTATION).add(PART).add(CONNECTED).add(CHEVRONS_ACTIVE).add(WATERLOGGED)/*.add(FULL)*/;
	}
	
	public BlockState rotate(BlockState state, Rotation rotation)
	{
	      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	public VoxelShape getShapeFromArray(VoxelShape[][] shapes, Direction direction, Orientation orientation)
	{
		int horizontal = direction.get2DDataValue();
		int vertical = orientation.get2DDataValue();
		
		return shapes[vertical][horizontal % shapes[vertical].length];
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		/*if(state.getValue(FULL))
			return FULL_BLOCK;*/
		
		Direction direction = state.getValue(FACING);
		Orientation orientation = state.getValue(ORIENTATION);

		return switch (state.getValue(PART)) {
			case LEFT2, LEFT3_ABOVE -> getShapeFromArray(VOXEL_COORDS.TOP_RIGHT, direction, orientation);
			case LEFT2_ABOVE -> getShapeFromArray(VOXEL_COORDS.STAIR_TOP_RIGHT, direction, orientation);
			case LEFT3_ABOVE5, LEFT2_ABOVE6 -> getShapeFromArray(VOXEL_COORDS.BOTTOM_RIGHT, direction, orientation);
			case LEFT2_ABOVE5 -> getShapeFromArray(VOXEL_COORDS.STAIR_BOTTOM_RIGHT, direction, orientation);
			case RIGHT2_ABOVE6, RIGHT3_ABOVE5 -> getShapeFromArray(VOXEL_COORDS.BOTTOM_LEFT, direction, orientation);
			case RIGHT2_ABOVE5 -> getShapeFromArray(VOXEL_COORDS.STAIR_BOTTOM_LEFT, direction, orientation);
			case RIGHT3_ABOVE, RIGHT2 -> getShapeFromArray(VOXEL_COORDS.TOP_LEFT, direction, orientation);
			case RIGHT2_ABOVE -> getShapeFromArray(VOXEL_COORDS.STAIR_TOP_LEFT, direction, orientation);
			default -> getShapeFromArray(VOXEL_COORDS.DEFAULT, direction, orientation);
		};
	}
	
	public BlockState updateShape(BlockState oldState, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos oldPos, BlockPos newPos)
	{
		if (oldState.getValue(WATERLOGGED))
		{
			levelAccessor.scheduleTick(oldPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
		}

		return super.updateShape(oldState, direction, newState, levelAccessor, oldPos, newPos);
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
			BlockPos centerPos = oldState.getValue(PART).getMainBlockPos(pos, oldState.getValue(FACING), oldState.getValue(ORIENTATION));
			BlockState centerState = level.getBlockState(centerPos);
			level.setBlock(centerPos, isWaterLogged(centerState, level, centerPos) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 35);
			
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
		BlockEntity blockentity = level.getBlockEntity(state.getValue(PART).getMainBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION)));
		if (blockentity instanceof AbstractStargateEntity stargate)
		{
			if (!level.isClientSide)
			{
				stargate.disconnectStargate(Stargate.Feedback.STARGATE_DESTROYED);
				
				if(!player.isCreative())
				{
					ItemStack itemstack = new ItemStack(getStargate());
					
					blockentity.saveToItem(itemstack);

					ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
					itementity.setDefaultPickUpDelay();
					level.addFreshEntity(itementity);
				}
			}
		}
		super.playerWillDestroy(level, pos, state, player);
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

	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return state.getValue(CONNECTED) ? 15 : state.getValue(CHEVRONS_ACTIVE);
	}

}
