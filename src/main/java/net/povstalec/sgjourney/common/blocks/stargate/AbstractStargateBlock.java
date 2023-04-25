package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.SGJourneyBaseEntityBlock;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public abstract class AbstractStargateBlock extends SGJourneyBaseEntityBlock implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
	public static final IntegerProperty CHEVRONS_ACTIVE = IntegerProperty.create("chevrons_active", 0, 9);
	
	protected static final VoxelShape FULL_BLOCK = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	
	protected static final VoxelShape X = Block.box(0.0D, 0.0D, 4.5D, 16.0D, 16.0D, 11.5D);
	protected static final VoxelShape Z = Block.box(4.5D, 0.0D, 0.0D, 11.5D, 16.0D, 16.0D);
	protected static final VoxelShape HORIZONTAL = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	
	public AbstractStargateBlock(Properties properties)
	{
		super(properties, "Stargates");
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ORIENTATION, Orientation.REGULAR).setValue(CONNECTED, Boolean.valueOf(false)).setValue(CHEVRONS_ACTIVE, 0).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}
	 
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(ORIENTATION).add(CONNECTED).add(CHEVRONS_ACTIVE).add(WATERLOGGED);
	}
	 
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) 
	{
		return true;
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		if(state.getValue(ORIENTATION) != Orientation.REGULAR)
			return HORIZONTAL;
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? Z : X;
	}
	
	public BlockState updateShape(BlockState oldState, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos oldPos, BlockPos newPos)
	{
		if (oldState.getValue(WATERLOGGED))
		{
			levelAccessor.scheduleTick(oldPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
		}

		return super.updateShape(oldState, direction, newState, levelAccessor, oldPos, newPos);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		Player player = context.getPlayer();
		Orientation orientation = Orientation.getOrientationFromXRot(player);
		
		if(orientation == Orientation.REGULAR && blockpos.getY() > level.getMaxBuildHeight() - 6)
			return null;
		
		for(StargatePart part : StargatePart.values())
		{
			if(!part.equals(StargatePart.CENTER) && !level.getBlockState(part.getRingPos(blockpos, context.getHorizontalDirection().getOpposite(), orientation)).canBeReplaced(context))
			{
				player.displayClientMessage(Component.translatable("block.sgjourney.stargate.not_enough_space"), true);
				return null;
			}
		}
		
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER))
				.setValue(ORIENTATION, orientation);
	}
	 
	@Nullable
	@Override
	public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
	
	public abstract BlockState ringState();
	
	@Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		
		for(StargatePart part : StargatePart.values())
		{
			if(!part.equals(StargatePart.CENTER))
			{
				level.setBlock(part.getRingPos(pos,  state.getValue(FACING), state.getValue(ORIENTATION)), 
						ringState()
						.setValue(AbstractStargateRingBlock.PART, part)
						.setValue(AbstractStargateRingBlock.FACING, level.getBlockState(pos).getValue(FACING))
						.setValue(AbstractStargateRingBlock.ORIENTATION, level.getBlockState(pos).getValue(ORIENTATION))
						.setValue(WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getRingPos(pos, state.getValue(FACING), state.getValue(ORIENTATION))).getType() == Fluids.WATER)), 3);
			}
		}
	}
	
	@Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
        if(oldState.getBlock() != newState.getBlock())
        {
    		BlockEntity blockentity = level.getBlockEntity(pos);
    		if(blockentity instanceof AbstractStargateEntity stargate)
    			stargate.disconnectStargate(Stargate.Feedback.STARGATE_DESTROYED);
    		
    		for(StargatePart part : StargatePart.values())
    		{
    			if(!part.equals(StargatePart.CENTER))
    			{
    				BlockPos ringPos = part.getRingPos(pos, oldState.getValue(FACING), oldState.getValue(ORIENTATION));
        			BlockState state = level.getBlockState(ringPos);
        			boolean waterlogged = state.getBlock() instanceof AbstractStargateRingBlock ? state.getValue(AbstractStargateRingBlock.WATERLOGGED) : false;
    				
    				level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
    			}
    		}
            super.onRemove(oldState, level, pos, newState, isMoving);
        }
    }
    
    public abstract Block getStargate();
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof AbstractStargateEntity stargate)
		{
			if(!level.isClientSide)
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
	
	public void updateStargate(Level level, BlockPos pos, BlockState state, boolean isConnected, int chevronsActive)
	{
		level.setBlock(pos, state.setValue(AbstractStargateBlock.CONNECTED, isConnected).setValue(AbstractStargateBlock.CHEVRONS_ACTIVE, chevronsActive), 2);
		
		for(StargatePart part : StargatePart.values())
		{
			if(!part.equals(StargatePart.CENTER))
			{
				level.setBlock(part.getRingPos(pos,  state.getValue(FACING), state.getValue(ORIENTATION)), 
						ringState()
						.setValue(AbstractStargateRingBlock.PART, part)
						.setValue(AbstractStargateRingBlock.CONNECTED, level.getBlockState(pos).getValue(CONNECTED))
						.setValue(AbstractStargateRingBlock.CHEVRONS_ACTIVE, level.getBlockState(pos).getValue(CHEVRONS_ACTIVE))
						.setValue(AbstractStargateRingBlock.FACING, level.getBlockState(pos).getValue(FACING))
						.setValue(AbstractStargateRingBlock.ORIENTATION, level.getBlockState(pos).getValue(ORIENTATION))
						.setValue(AbstractStargateRingBlock.WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getRingPos(pos, state.getValue(FACING), state.getValue(ORIENTATION))).getType() == Fluids.WATER)), 3);
			}
		}
	}
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	int energy = 0;
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Energy"))
			energy = stack.getTag().getCompound("BlockEntityTag").getInt("Energy");
		
        tooltipComponents.add(Component.literal("Energy: " + energy + " FE").withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }

	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return state.getValue(CONNECTED) ? 15 : state.getValue(CHEVRONS_ACTIVE);
	}

}
