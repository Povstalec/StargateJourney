package net.povstalec.sgjourney.common.blocks.dhd;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

import java.util.List;


public abstract class AbstractDHDBlock extends HorizontalDirectionalBlock implements EntityBlock
{
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
			
        	if(blockEntity instanceof AbstractDHDEntity dhd)
        		dhd.unsetStargate();
		}
		
		super.onRemove(oldState, level, pos, newState, isMoving);
	}

	protected abstract void use(Level level, BlockPos pos, Player player);

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
    
    public abstract Block getDHD();
	
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof AbstractDHDEntity)
		{
			if(!level.isClientSide() && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(getDHD());
				
				blockentity.saveToItem(itemstack, level.registryAccess());

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
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
		NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker)
	{
		return typeB == typeA ? (BlockEntityTicker<A>)ticker : null;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		if(stack.has(DataComponents.BLOCK_ENTITY_DATA))
		{
			CompoundTag blockEntityTag = stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
			if(blockEntityTag != null && blockEntityTag.contains(AbstractDHDEntity.GENERATE_ENERGY_CORE))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.dhd.generates_energy_core").withStyle(ChatFormatting.YELLOW));
		}
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
}
