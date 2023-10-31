package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.ConnectionState;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public abstract class AbstractStargateBaseBlock extends AbstractStargateBlock implements EntityBlock
{
	public AbstractStargateBaseBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties, width, horizontalOffset);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		Player player = context.getPlayer();
		Orientation orientation = Orientation.getOrientationFromXRot(player);
		
		if(orientation == Orientation.REGULAR && blockpos.getY() > level.getMaxBuildHeight() - 6)
			return null;
		
		for(StargatePart part : getStargateType().getParts())
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
		
		for(StargatePart part : getStargateType().getParts())
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
    			stargate.bypassDisconnectStargate(Stargate.Feedback.STARGATE_DESTROYED);
    			stargate.removeFromBlockEntityList();
    		}
    		
    		for(StargatePart part : getStargateType().getParts())
    		{
    			if(!part.equals(StargatePart.BASE))
    			{
    				BlockPos ringPos = part.getRingPos(pos, oldState.getValue(FACING), oldState.getValue(ORIENTATION));
        			BlockState state = level.getBlockState(ringPos);
        			
        			if(state.getBlock() instanceof AbstractStargateBlock)
        			{
        				boolean waterlogged = state.getBlock() instanceof AbstractStargateRingBlock ? state.getValue(AbstractStargateRingBlock.WATERLOGGED) : false;
        				
        				level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
        			}
    			}
    		}
            super.onRemove(oldState, level, pos, newState, isMoving);
        }
    }
	
	public void updateStargate(Level level, BlockPos pos, BlockState state, ConnectionState connectionState, int chevronsActive)
	{
		level.setBlock(pos, state.setValue(AbstractStargateBaseBlock.CONNECTION_STATE, connectionState).setValue(AbstractStargateBaseBlock.CHEVRONS_ACTIVE, chevronsActive), 2);
		
		for(StargatePart part : getStargateType().getParts())
		{
			if(!part.equals(StargatePart.BASE))
			{
				BlockPos ringPos = part.getRingPos(pos,  state.getValue(FACING), state.getValue(ORIENTATION));
				if(level.getBlockState(ringPos).getBlock() instanceof AbstractStargateBlock)
				{
					level.setBlock(part.getRingPos(pos,  state.getValue(FACING), state.getValue(ORIENTATION)), 
							ringState()
							.setValue(AbstractStargateRingBlock.PART, part)
							.setValue(AbstractStargateRingBlock.CONNECTION_STATE, level.getBlockState(pos).getValue(CONNECTION_STATE))
							.setValue(AbstractStargateRingBlock.CHEVRONS_ACTIVE, level.getBlockState(pos).getValue(CHEVRONS_ACTIVE))
							.setValue(AbstractStargateRingBlock.FACING, level.getBlockState(pos).getValue(FACING))
							.setValue(AbstractStargateRingBlock.ORIENTATION, level.getBlockState(pos).getValue(ORIENTATION))
							.setValue(AbstractStargateRingBlock.WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getRingPos(pos, state.getValue(FACING), state.getValue(ORIENTATION))).getType() == Fluids.WATER)), 3);
				}
			}
		}
	}
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	int energy = 0;
        String id = "";
    	
        if(stack.hasTag())
        {
            CompoundTag blockEntityTag = stack.getTag().getCompound("BlockEntityTag");
            
            if(blockEntityTag.contains("Energy"))
            	energy = blockEntityTag.getInt("Energy");
        }
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + energy + " FE")).withStyle(ChatFormatting.DARK_RED));
		
        
        if(stack.hasTag())
        {
        	CompoundTag blockEntityTag = stack.getTag().getCompound("BlockEntityTag");
        	
        	if((blockEntityTag.contains("DisplayID") && blockEntityTag.getBoolean("DisplayID")) || true)//TODO Add config value here
        	{
        		if(blockEntityTag.contains("ID"))
        			id = blockEntityTag.getString("ID");
            	
            	tooltipComponents.add(Component.translatable("tooltip.sgjourney.address").append(Component.literal(": " + id)).withStyle(ChatFormatting.AQUA));
        	}
        	
        	if((blockEntityTag.contains("Upgraded") && blockEntityTag.getBoolean("Upgraded")))
            	tooltipComponents.add(Component.translatable("tooltip.sgjourney.upgraded").withStyle(ChatFormatting.DARK_GREEN));
        }
        
        if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("AddToNetwork") && !stack.getTag().getCompound("BlockEntityTag").getBoolean("AddToNetwork"))
            tooltipComponents.add(Component.translatable("tooltip.sgjourney.not_added_to_network").withStyle(ChatFormatting.YELLOW));
        
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
