package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public abstract class AbstractStargateBaseBlock extends AbstractStargateBlock implements EntityBlock
{
	public AbstractStargateBaseBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		if(state.getValue(ORIENTATION) != Orientation.REGULAR)
			return SHAPE_PROVIDER.HORIZONTAL;
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_PROVIDER.Z : SHAPE_PROVIDER.X;
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
			if(!part.equals(StargatePart.BASE) && !level.getBlockState(part.getRingPos(blockpos, context.getHorizontalDirection().getOpposite(), orientation)).canBeReplaced(context))
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
			if(!part.equals(StargatePart.BASE))
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
    		{
    			stargate.disconnectStargate(Stargate.Feedback.STARGATE_DESTROYED);
    			stargate.removeFromBlockEntityList();
    		}
    		
    		for(StargatePart part : StargatePart.values())
    		{
    			if(!part.equals(StargatePart.BASE))
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
	
	public void updateStargate(Level level, BlockPos pos, BlockState state, boolean isConnected, int chevronsActive)
	{
		level.setBlock(pos, state.setValue(AbstractStargateBaseBlock.CONNECTED, isConnected).setValue(AbstractStargateBaseBlock.CHEVRONS_ACTIVE, chevronsActive), 2);
		
		for(StargatePart part : StargatePart.values())
		{
			if(!part.equals(StargatePart.BASE))
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
        String id;
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Energy"))
			energy = stack.getTag().getCompound("BlockEntityTag").getInt("Energy");
		
        tooltipComponents.add(Component.literal("Energy: " + energy + " FE").withStyle(ChatFormatting.DARK_RED));
        
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("ID"))
			id = stack.getTag().getCompound("BlockEntityTag").getString("ID");
		else
			id = "";
		
        tooltipComponents.add(Component.literal("ID: " + id).withStyle(ChatFormatting.AQUA));

        if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("AddToNetwork") && !stack.getTag().getCompound("BlockEntityTag").getBoolean("AddToNetwork"))
            tooltipComponents.add(Component.literal("Won't be added to network").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
	
	public static ItemStack excludeFromNetwork(ItemStack stack)
	{
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putBoolean("AddToNetwork", false);
		stack.addTagElement("BlockEntityTag", compoundtag);
		
		return stack;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker)
	{
		return typeB == typeA ? (BlockEntityTicker<A>)ticker : null;
	}
}
