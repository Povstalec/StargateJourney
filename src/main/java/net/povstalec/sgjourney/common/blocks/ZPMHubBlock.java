package net.povstalec.sgjourney.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.energy_gen.ZPMHubEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.menu.ZPMHubMenu;

public class ZPMHubBlock extends BaseEntityBlock
{
	public ZPMHubBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new ZPMHubEntity(pos, state);
	}
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if(!level.isClientSide())
        {
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof ZPMHubEntity hub) 
        	{
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.zpm_hub");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return new ZPMHubMenu(windowId, playerInventory, blockEntity);
        			}
        		};
        		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
        return InteractionResult.SUCCESS;
    }
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if (state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            
            if (blockEntity instanceof ZPMHubEntity hub)
            	hub.drops();
        }
        super.onRemove(state, level, pos, newState, isMoving);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ZPM_HUB.get(), ZPMHubEntity::tick);
    }
}
