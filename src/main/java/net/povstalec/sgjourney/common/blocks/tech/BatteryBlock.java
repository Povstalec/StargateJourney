package net.povstalec.sgjourney.common.blocks.tech;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.tech.BatteryBlockEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.BatteryMenu;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

import javax.annotation.Nullable;

public abstract class BatteryBlock extends BaseEntityBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BatteryBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
	}
	
	public void use(Level level, BlockPos pos, Player player)
	{
		if(!level.isClientSide())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof BatteryBlockEntity)
			{
				MenuProvider containerProvider = new MenuProvider()
				{
					@Override
					public Component getDisplayName()
					{
						return Component.translatable("screen.sgjourney.naquadah_battery");
					}
					
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
					{
						return new BatteryMenu(windowId, playerInventory, blockEntity);
					}
				};
				NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
			}
			else
			{
				throw new IllegalStateException("Our named container provider is missing!");
			}
		}
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		use(level, pos, player);
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		use(level, pos, player);
		
		return ItemInteractionResult.SUCCESS;
	}
	
	public abstract Block getDroppedBlock();
	
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof BatteryBlockEntity)
		{
			if(!level.isClientSide() && !player.isCreative() && player.hasCorrectToolForDrops(state))
			{
				ItemStack itemstack = new ItemStack(getDroppedBlock());
				
				blockentity.saveToItem(itemstack, level.registryAccess());
				
				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}
		
		return super.playerWillDestroy(level, pos, state, player);
	}
	
	
	
	public static class Naquadah extends BatteryBlock
	{
		public static final MapCodec<BatteryBlock.Naquadah> CODEC = simpleCodec(BatteryBlock.Naquadah::new);
		
		public Naquadah(Properties properties)
		{
			super(properties);
		}
		
		@Override
		protected MapCodec<BatteryBlock.Naquadah> codec()
		{
			return CODEC;
		}
		
		@Override
		public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new BatteryBlockEntity.Naquadah(pos, state);
		}
		
		@Nullable
		@Override
		public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
		{
			return createTickerHelper(type, BlockEntityInit.LARGE_NAQUADAH_BATTERY.get(), BatteryBlockEntity::tick);
		}
		
		@Override
		public Block getDroppedBlock()
		{
			return BlockInit.LARGE_NAQUADAH_BATTERY.get();
		}
	}
}
