package net.povstalec.sgjourney.blocks.dhd;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.block_entities.dhd.PegasusDHDEntity;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.menu.PegasusDHDMenu;

public class PegasusDHDBlock extends AbstractDHDBlock
{

	public PegasusDHDBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		PegasusDHDEntity dhd = new PegasusDHDEntity(pos, state);
		
		return dhd;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if (!level.isClientSide) 
        {
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof AbstractDHDEntity dhd) 
        	{
        		if(player.isShiftKeyDown())
        			this.openCrystalMenu(player, blockEntity);
        		else
        		{
        			MenuProvider containerProvider = new MenuProvider() 
            		{
            			@Override
            			public Component getDisplayName() 
            			{
            				return Component.translatable("screen.sgjourney.dhd");
            			}
            			
            			@Override
            			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
            			{
            				return new PegasusDHDMenu(windowId, playerInventory, blockEntity);
            			}
            		};
            		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        		}
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
        return InteractionResult.SUCCESS;
    }

	@Override
	public Block getDHD()
	{
		return BlockInit.PEGASUS_DHD.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.PEGASUS_DHD.get(), AbstractDHDEntity::tick);
    }
}
