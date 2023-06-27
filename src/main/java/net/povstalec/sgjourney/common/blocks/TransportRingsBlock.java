package net.povstalec.sgjourney.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.povstalec.sgjourney.common.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;


public class TransportRingsBlock extends SGJourneyBaseEntityBlock
{
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);
	public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

	public TransportRingsBlock(Properties properties) 
	{
		super(properties, "TransportRings");
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(ACTIVATED, false));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(ACTIVATED);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getNearestLookingVerticalDirection().getOpposite());
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TransportRingsEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.TRANSPORT_RINGS.get(), TransportRingsEntity::tick);
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof SGJourneyBlockEntity baseBlockEntity)
		{
			if (!level.isClientSide() && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(BlockInit.TRANSPORT_RINGS.get());
				
				blockentity.saveToItem(itemstack);
				/*if (baseBlockEntity.hasCustomName())
				{
					itemstack.setHoverName(baseBlockEntity.getCustomName());
				}*/

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
}
