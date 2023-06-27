package net.povstalec.sgjourney.common.blocks.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;


public abstract class AbstractDHDBlock extends BaseEntityBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public AbstractDHDBlock(Properties properties) 
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}

	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(level.isClientSide())
			return;
		
		if(oldState.getBlock() != newState.getBlock())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof AbstractDHDEntity dhd)
        		dhd.disconnectFromStargate();
		}
	}
    
    public abstract Block getDHD();
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof AbstractDHDEntity)
		{
			if(!level.isClientSide && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(getDHD());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	protected void openCrystalMenu(Player player, BlockEntity blockEntity)
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
				return new DHDCrystalMenu(windowId, playerInventory, blockEntity);
			}
		};
		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
	}
}
