package net.povstalec.sgjourney.common.blocks.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;
import net.povstalec.sgjourney.common.block_entities.zpm.AbstractZPMHolderEntity;
import net.povstalec.sgjourney.common.blocks.ProtectedBlock;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractZPMHolderBlock extends BaseEntityBlock implements ProtectedBlock
{
	public AbstractZPMHolderBlock(Properties properties)
	{
		super(properties);
	}
	
	public @NotNull RenderShape getRenderShape(@NotNull BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		return use(state, level, pos, player, InteractionHand.MAIN_HAND, hitResult) ? InteractionResult.sidedSuccess(level.isClientSide()) : InteractionResult.PASS;
	}
	
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		return use(state, level, pos, player, hand, hitResult) ? ItemInteractionResult.sidedSuccess(level.isClientSide()) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}
	
	public boolean use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult trace)
	{
		if(!hasPermissions(level, pos, state, player, true))
			return false;
		
		ItemStack itemInHand = player.getItemInHand(hand);
		if(itemInHand.isEmpty() || itemInHand.is(ItemInit.ZPM.get()))
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof AbstractZPMHolderEntity zpmHolder)
			{
				IItemHandler itemHandler = zpmHolder.getItemHandler();
				if(itemHandler != null)
				{
					if(itemInHand.isEmpty())
					{
						if(itemHandler.getStackInSlot(0).isEmpty())
							return false;
						else
						{
							ItemStack extractedStack = itemHandler.extractItem(0, 1, false);
							if(!extractedStack.isEmpty() && !level.isClientSide())
							{
								zpmHolder.setChanged();
								zpmHolder.updateClient();
								player.setItemInHand(hand, extractedStack);
							}
							return true;
						}
					}
					else
					{
						ItemStack returnedStack = itemHandler.insertItem(0, itemInHand, false);
						if(returnedStack != itemInHand && !level.isClientSide())
						{
							zpmHolder.setChanged();
							zpmHolder.updateClient();
							player.setItemInHand(hand, returnedStack);
						}
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof AbstractZPMHolderEntity zpmHolder)
				zpmHolder.drops();
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}
}
