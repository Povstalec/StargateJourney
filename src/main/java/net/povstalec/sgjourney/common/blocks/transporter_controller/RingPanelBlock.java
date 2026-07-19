package net.povstalec.sgjourney.common.blocks.transporter_controller;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.transporter_controller.RingPanelEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.RingPanelMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

import javax.annotation.Nullable;
import java.util.List;

public class RingPanelBlock extends TransporterControllerBlock
{
	protected static final VoxelShape NORTH = Block.box(2.0D, 0.0D, 13.0D, 14.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 3.0D);
	protected static final VoxelShape EAST = Block.box(0.0D, 0.0D, 2.0D, 3.0D, 16.0D, 14.0D);
	protected static final VoxelShape WEST = Block.box(13.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D);

	public static final MapCodec<RingPanelBlock> CODEC = simpleCodec(RingPanelBlock::new);
	
	public RingPanelBlock(Properties properties) 
	{
		super(properties);
	}

	protected MapCodec<RingPanelBlock> codec() {
		return CODEC;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new RingPanelEntity(pos, state);
	}

	public void use(Level level, BlockPos pos, Player player)
	{
        if(!level.isClientSide())
        {
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if(blockEntity instanceof RingPanelEntity ringPanel)
        	{
				ringPanel.tryUpdateButtons();
				
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.ring_panel");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return ringPanel.hasPermissions(player, false) ? new RingPanelMenu.Unprotected(windowId, playerInventory, ringPanel) : new RingPanelMenu.Protected(windowId, playerInventory, ringPanel);
        			}
        		};
				NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        	}
        	else
        		throw new IllegalStateException("Our named container provider is missing!");
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
	
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof RingPanelEntity)
		{
			if(!level.isClientSide() && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(BlockInit.GOAULD_RING_PANEL.get());
				
				blockentity.saveToItem(itemstack, level.registryAccess());
				
				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}
		
		return super.playerWillDestroy(level, pos, state, player);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		return switch (state.getValue(FACING))
		{
			case EAST -> EAST;
			case SOUTH -> SOUTH;
			case WEST -> WEST;
			default -> NORTH;
		};
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker)
	{
		return typeB == typeA ? (BlockEntityTicker<A>) ticker : null;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.GOAULD_RING_PANEL.get(), RingPanelEntity::tick);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.ring_panel.description"));
	}
}
