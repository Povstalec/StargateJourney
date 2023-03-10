package net.povstalec.sgjourney.blocks.stargate;

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
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.blocks.SGJourneyBaseEntityBlock;
import net.povstalec.sgjourney.stargate.StargatePart;

public abstract class AbstractStargateBlock extends SGJourneyBaseEntityBlock implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
	
	protected static final VoxelShape X = Block.box(0.0D, 0.0D, 4.5D, 16.0D, 16.0D, 11.5D);
	protected static final VoxelShape Z = Block.box(4.5D, 0.0D, 0.0D, 11.5D, 16.0D, 16.0D);
	protected static final VoxelShape UPWARD = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	
	public AbstractStargateBlock(Properties properties)
	{
		super(properties, "Stargates");
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(CONNECTED, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}
	 
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(CONNECTED).add(WATERLOGGED);
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
		
		if(blockpos.getY() > level.getMaxBuildHeight() - 6)
			return null;
		
		if (context.getHorizontalDirection().getOpposite().getAxis() == Direction.Axis.X)
		{
			if (blockpos.getY() < level.getMaxBuildHeight() - 6 && 
					level.getBlockState(blockpos.south()).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(2).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(3).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(3).above(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(3).above(3)).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(3).above(4)).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(3).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(2).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.south(2).above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.south().above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north().above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(2).above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(2).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(3).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(3).above(4)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(3).above(3)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(3).above(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(3).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(2).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.north(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.north()).canBeReplaced(context))
			{
				return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
			}
				
		}
		else
		{
			if (blockpos.getY() < level.getMaxBuildHeight() - 6 && 
					level.getBlockState(blockpos.east()).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(2).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(3).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(3).above(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(3).above(3)).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(3).above(4)).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(3).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(2).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.east(2).above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.east().above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west().above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(2).above(6)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(2).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(3).above(5)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(3).above(4)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(3).above(3)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(3).above(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(3).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(2).above()).canBeReplaced(context) &&
					level.getBlockState(blockpos.west(2)).canBeReplaced(context) &&
					level.getBlockState(blockpos.west()).canBeReplaced(context))
			{
				return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
			}
		}
			
		return null;
	 }
	 
	@Nullable
	@Override
	public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
	
	public abstract BlockState ringState();
	
	private void setBlock(Level level, BlockPos pos, BlockState state, StargatePart part)
	{
		level.setBlock(StargatePart.getRingPos(pos, state.getValue(FACING), part), ringState().setValue(AbstractStargateRingBlock.PART, part).setValue(AbstractStargateRingBlock.FACING, level.getBlockState(pos).getValue(FACING)).setValue(WATERLOGGED, Boolean.valueOf(level.getFluidState(StargatePart.getRingPos(pos, state.getValue(FACING), part)).getType() == Fluids.WATER)), 3);
	}
	
	@Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		
		setBlock(level, pos, state, StargatePart.LEFT);
		
		setBlock(level, pos, state, StargatePart.LEFT2);
		setBlock(level, pos, state, StargatePart.LEFT2_ABOVE);
		setBlock(level, pos, state, StargatePart.LEFT3_ABOVE);

		setBlock(level, pos, state, StargatePart.LEFT3_ABOVE2);
		setBlock(level, pos, state, StargatePart.LEFT3_ABOVE3);
		setBlock(level, pos, state, StargatePart.LEFT3_ABOVE4);

		setBlock(level, pos, state, StargatePart.LEFT3_ABOVE5);
		setBlock(level, pos, state, StargatePart.LEFT2_ABOVE5);
		setBlock(level, pos, state, StargatePart.LEFT2_ABOVE6);

		setBlock(level, pos, state, StargatePart.LEFT_ABOVE6);
		setBlock(level, pos, state, StargatePart.ABOVE6);
		setBlock(level, pos, state, StargatePart.RIGHT_ABOVE6);

		setBlock(level, pos, state, StargatePart.RIGHT2_ABOVE6);
		setBlock(level, pos, state, StargatePart.RIGHT2_ABOVE5);
		setBlock(level, pos, state, StargatePart.RIGHT3_ABOVE5);

		setBlock(level, pos, state, StargatePart.RIGHT3_ABOVE4);
		setBlock(level, pos, state, StargatePart.RIGHT3_ABOVE3);
		setBlock(level, pos, state, StargatePart.RIGHT3_ABOVE2);

		setBlock(level, pos, state, StargatePart.RIGHT3_ABOVE);
		setBlock(level, pos, state, StargatePart.RIGHT2_ABOVE);
		setBlock(level, pos, state, StargatePart.RIGHT2);
		
		setBlock(level, pos, state, StargatePart.RIGHT);
	}
	
	@Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
        if(oldState.getBlock() != newState.getBlock())
        {
    		BlockEntity blockentity = level.getBlockEntity(pos);
    		if(blockentity instanceof AbstractStargateEntity stargate)
    			stargate.disconnectStargate();
    		
            if(oldState.getValue(FACING).getAxis() == Direction.Axis.X)
      		{
            	level.setBlock(pos.south(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(2).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(3).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(3).above(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(3).above(3), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(3).above(4), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(3).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(2).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(2).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.south(1).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(1).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(2).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(2).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(3).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(3).above(4), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(3).above(3), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(3).above(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(3).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(2).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.north(), Blocks.AIR.defaultBlockState(), 35);
      		}
      		else
      		{
      			level.setBlock(pos.east(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(2).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(3).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(3).above(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(3).above(3), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(3).above(4), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(3).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(2).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(2).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.east(1).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(1).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(2).above(6), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(2).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(3).above(5), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(3).above(4), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(3).above(3), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(3).above(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(3).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(2).above(), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(2), Blocks.AIR.defaultBlockState(), 35);
            	level.setBlock(pos.west(), Blocks.AIR.defaultBlockState(), 35);
      		}
            super.onRemove(oldState, level, pos, newState, isMoving);
        }
    }
    
    public abstract Block getStargate();
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof AbstractStargateEntity stargate)
		{
			if (!level.isClientSide)
			{
				stargate.disconnectStargate();
				
				ItemStack itemstack = new ItemStack(getStargate());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
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
	
}
