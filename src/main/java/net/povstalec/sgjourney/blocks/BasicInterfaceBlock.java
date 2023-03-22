package net.povstalec.sgjourney.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;

public class BasicInterfaceBlock extends BaseEntityBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BasicInterfaceBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	 
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}
	 
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new BasicInterfaceEntity(pos, state);
	}
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	public Block getDroppedBlock()
	{
		return BlockInit.BASIC_INTERFACE.get();
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof BasicInterfaceEntity)
		{
			if (!level.isClientSide && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(getDroppedBlock());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide)
			return;

		Direction direction = state.getValue(FACING);
		BlockPos targetPos = pos.relative(direction);
		if(targetPos.equals(pos2) && level.getBlockEntity(pos) instanceof BasicInterfaceEntity basicInterface && basicInterface.updateInterface())
		{
			level.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), state.getValue(FACING));
		}
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.BASIC_INTERFACE.get(), BasicInterfaceEntity::tick);
    }
	
	public long getCapacity()
	{
		return 5000000;
	}
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	int energy = 0;
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Energy"))
			energy = stack.getTag().getCompound("BlockEntityTag").getInt("Energy");
		
        tooltipComponents.add(Component.literal("Energy: " + energy + "/" + getCapacity() +" FE").withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
