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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
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
	
	protected static final VoxelShape FULL_BLOCK = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	protected static final VoxelShape HORIZONTAL = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	protected static final VoxelShape HORIZONTAL_BOTTOM_LEFT = Block.box(0.0D, 1.0D, 0.0D, 8.0D, 8.0D, 8.0D);
	protected static final VoxelShape HORIZONTAL_BOTTOM_RIGHT = Block.box(8.0D, 1.0D, 0.0D, 16.0D, 8.0D, 8.0D);
	protected static final VoxelShape HORIZONTAL_TOP_LEFT = Block.box(0.0D, 1.0D, 8.0D, 8.0D, 8.0D, 16.0D);
	protected static final VoxelShape HORIZONTAL_TOP_RIGHT = Block.box(8.0D, 1.0D, 8.0D, 16.0D, 8.0D, 16.0D);
	
	protected static final VoxelShape HORIZONTAL_STAIR_BOTTOM_LEFT = Shapes.or(HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_LEFT, HORIZONTAL_TOP_RIGHT);
	protected static final VoxelShape HORIZONTAL_STAIR_BOTTOM_RIGHT = Shapes.or(HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_TOP_LEFT, HORIZONTAL_TOP_RIGHT);
	protected static final VoxelShape HORIZONTAL_STAIR_TOP_LEFT = Shapes.or(HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_RIGHT);
	protected static final VoxelShape HORIZONTAL_STAIR_TOP_RIGHT = Shapes.or(HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_LEFT);
	
	protected static final VoxelShape[] HORIZONTAL_SHAPES = new VoxelShape[] {HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_TOP_RIGHT, HORIZONTAL_TOP_LEFT};
	protected static final VoxelShape[] HORIZONTAL_STAIR_SHAPES = new VoxelShape[] {HORIZONTAL_STAIR_TOP_LEFT, HORIZONTAL_STAIR_TOP_RIGHT, HORIZONTAL_STAIR_BOTTOM_LEFT, HORIZONTAL_STAIR_BOTTOM_RIGHT};
	
	protected static final VoxelShape X = Block.box(0.0D, 0.0D, 4.5D, 16.0D, 16.0D, 11.5D);
	protected static final VoxelShape X_TOP_LEFT = Block.box(0.0D, 8.0D, 4.5D, 8.0D, 16.0D, 11.5D);
	protected static final VoxelShape X_TOP_RIGHT = Block.box(8.0D, 8.0D, 4.5D, 16.0D, 16.0D, 11.5D);
	protected static final VoxelShape X_BOTTOM_LEFT = Block.box(0.0D, 0.0D, 4.5D, 8.0D, 8.0D, 11.5D);
	protected static final VoxelShape X_BOTTOM_RIGHT = Block.box(8.0D, 0.0D, 4.5D, 16.0D, 8.0D, 11.5D);
	
	protected static final VoxelShape X_STAIR_TOP_LEFT =  Shapes.or(X_BOTTOM_LEFT, X_BOTTOM_RIGHT, X_TOP_RIGHT);
	protected static final VoxelShape X_STAIR_TOP_RIGHT =  Shapes.or(X_BOTTOM_LEFT, X_BOTTOM_RIGHT, X_TOP_LEFT);
	protected static final VoxelShape X_STAIR_BOTTOM_LEFT =  Shapes.or(X_TOP_LEFT, X_TOP_RIGHT, X_BOTTOM_RIGHT);
	protected static final VoxelShape X_STAIR_BOTTOM_RIGHT =  Shapes.or(X_TOP_LEFT, X_TOP_RIGHT, X_BOTTOM_LEFT);
	
	protected static final VoxelShape Z = Block.box(4.5D, 0.0D, 0.0D, 11.5D, 16.0D, 16.0D);
	protected static final VoxelShape Z_TOP_LEFT = Block.box(4.5D, 8.0D, 0.0D, 11.5D, 16.0D, 8.0D);
	protected static final VoxelShape Z_TOP_RIGHT = Block.box(4.5D, 8.0D, 8.0D, 11.5D, 16.0D, 16.0D);
	protected static final VoxelShape Z_BOTTOM_LEFT = Block.box(4.5D, 0.0D, 0.0D, 11.5D, 8.0D, 8.0D);
	protected static final VoxelShape Z_BOTTOM_RIGHT = Block.box(4.5D, 0.0D, 8.0D, 11.5D, 8.0D, 16.0D);
	
	protected static final VoxelShape Z_STAIR_TOP_LEFT =  Shapes.or(Z_BOTTOM_LEFT, Z_BOTTOM_RIGHT, Z_TOP_RIGHT);
	protected static final VoxelShape Z_STAIR_TOP_RIGHT =  Shapes.or(Z_BOTTOM_LEFT, Z_BOTTOM_RIGHT, Z_TOP_LEFT);
	protected static final VoxelShape Z_STAIR_BOTTOM_LEFT =  Shapes.or(Z_TOP_LEFT, Z_TOP_RIGHT, Z_BOTTOM_RIGHT);
	protected static final VoxelShape Z_STAIR_BOTTOM_RIGHT =  Shapes.or(Z_TOP_LEFT, Z_TOP_RIGHT, Z_BOTTOM_LEFT);

	protected static final VoxelShape[][] DEFAULT = new VoxelShape[][] {{HORIZONTAL}, {X, Z}, {HORIZONTAL}};
	
	protected static final VoxelShape[][] TOP_LEFT = new VoxelShape[][] {{HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_RIGHT, HORIZONTAL_TOP_LEFT}, {X_TOP_LEFT, Z_TOP_LEFT, X_TOP_RIGHT, Z_TOP_RIGHT}, {HORIZONTAL_TOP_LEFT, HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_RIGHT}};
	protected static final VoxelShape[][] BOTTOM_LEFT = new VoxelShape[][] {{HORIZONTAL_TOP_LEFT, HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_RIGHT}, {X_BOTTOM_LEFT, Z_BOTTOM_LEFT, X_BOTTOM_RIGHT, Z_BOTTOM_RIGHT}, {HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_RIGHT, HORIZONTAL_TOP_LEFT}};
	protected static final VoxelShape[][] BOTTOM_RIGHT = new VoxelShape[][] {{HORIZONTAL_TOP_RIGHT, HORIZONTAL_TOP_LEFT, HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT}, {X_BOTTOM_RIGHT, Z_BOTTOM_RIGHT, X_BOTTOM_LEFT, Z_BOTTOM_LEFT}, {HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_RIGHT, HORIZONTAL_TOP_LEFT, HORIZONTAL_BOTTOM_LEFT}};
	protected static final VoxelShape[][] TOP_RIGHT = new VoxelShape[][] {{HORIZONTAL_BOTTOM_RIGHT, HORIZONTAL_TOP_RIGHT, HORIZONTAL_TOP_LEFT, HORIZONTAL_BOTTOM_LEFT}, {X_TOP_RIGHT, Z_TOP_RIGHT, X_TOP_LEFT, Z_TOP_LEFT}, {HORIZONTAL_TOP_RIGHT, HORIZONTAL_TOP_LEFT, HORIZONTAL_BOTTOM_LEFT, HORIZONTAL_BOTTOM_RIGHT}};
	
	protected static final VoxelShape[][] STAIR_TOP_LEFT = new VoxelShape[][] {{HORIZONTAL_STAIR_BOTTOM_LEFT, HORIZONTAL_STAIR_BOTTOM_RIGHT, HORIZONTAL_STAIR_TOP_RIGHT, HORIZONTAL_STAIR_TOP_LEFT}, {X_STAIR_TOP_LEFT, Z_STAIR_TOP_LEFT, X_STAIR_TOP_RIGHT, Z_STAIR_TOP_RIGHT}, {HORIZONTAL_STAIR_TOP_LEFT, HORIZONTAL_STAIR_BOTTOM_LEFT, HORIZONTAL_STAIR_BOTTOM_RIGHT, HORIZONTAL_STAIR_TOP_RIGHT}};
	protected static final VoxelShape[][] STAIR_BOTTOM_LEFT = new VoxelShape[][] {{HORIZONTAL_STAIR_TOP_LEFT, HORIZONTAL_STAIR_BOTTOM_LEFT, HORIZONTAL_STAIR_BOTTOM_RIGHT, HORIZONTAL_STAIR_TOP_RIGHT}, {X_STAIR_BOTTOM_LEFT, Z_STAIR_BOTTOM_LEFT, X_STAIR_BOTTOM_RIGHT, Z_STAIR_BOTTOM_RIGHT}, {HORIZONTAL_STAIR_BOTTOM_LEFT, HORIZONTAL_STAIR_BOTTOM_RIGHT, HORIZONTAL_STAIR_TOP_RIGHT, HORIZONTAL_STAIR_TOP_LEFT}};
	protected static final VoxelShape[][] STAIR_BOTTOM_RIGHT = new VoxelShape[][] {{HORIZONTAL_STAIR_TOP_RIGHT, HORIZONTAL_STAIR_TOP_LEFT, HORIZONTAL_STAIR_BOTTOM_LEFT, HORIZONTAL_STAIR_BOTTOM_RIGHT}, {X_STAIR_BOTTOM_RIGHT, Z_STAIR_BOTTOM_RIGHT, X_STAIR_BOTTOM_LEFT, Z_STAIR_BOTTOM_LEFT}, {HORIZONTAL_STAIR_BOTTOM_RIGHT, HORIZONTAL_STAIR_TOP_RIGHT, HORIZONTAL_STAIR_TOP_LEFT, HORIZONTAL_STAIR_BOTTOM_LEFT}};
	protected static final VoxelShape[][] STAIR_TOP_RIGHT = new VoxelShape[][] {{HORIZONTAL_STAIR_BOTTOM_RIGHT, HORIZONTAL_STAIR_TOP_RIGHT, HORIZONTAL_STAIR_TOP_LEFT, HORIZONTAL_STAIR_BOTTOM_LEFT}, {X_STAIR_TOP_RIGHT, Z_STAIR_TOP_RIGHT, X_STAIR_TOP_LEFT, Z_STAIR_TOP_LEFT}, {HORIZONTAL_STAIR_TOP_RIGHT, HORIZONTAL_STAIR_TOP_LEFT, HORIZONTAL_STAIR_BOTTOM_LEFT, HORIZONTAL_STAIR_BOTTOM_RIGHT}};
	
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
		
		switch(state.getValue(PART))
		{
		case LEFT2:
			return getShapeFromArray(TOP_RIGHT, direction, orientation);
		case LEFT2_ABOVE:
			return getShapeFromArray(STAIR_TOP_RIGHT, direction, orientation);
		case LEFT3_ABOVE:
			return getShapeFromArray(TOP_RIGHT, direction, orientation);
			
		case LEFT3_ABOVE5:
			return getShapeFromArray(BOTTOM_RIGHT, direction, orientation);
		case LEFT2_ABOVE5:
			return getShapeFromArray(STAIR_BOTTOM_RIGHT, direction, orientation);
		case LEFT2_ABOVE6:
			return getShapeFromArray(BOTTOM_RIGHT, direction, orientation);
			
		case RIGHT2_ABOVE6:
			return getShapeFromArray(BOTTOM_LEFT, direction, orientation);
		case RIGHT2_ABOVE5:
			return getShapeFromArray(STAIR_BOTTOM_LEFT, direction, orientation);
		case RIGHT3_ABOVE5:
			return getShapeFromArray(BOTTOM_LEFT, direction, orientation);
			
		case RIGHT3_ABOVE:
			return getShapeFromArray(TOP_LEFT, direction, orientation);
		case RIGHT2_ABOVE:
			return getShapeFromArray(STAIR_TOP_LEFT, direction, orientation);
		case RIGHT2:
			return getShapeFromArray(TOP_LEFT, direction, orientation);
		default:
			return getShapeFromArray(DEFAULT, direction, orientation);
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
