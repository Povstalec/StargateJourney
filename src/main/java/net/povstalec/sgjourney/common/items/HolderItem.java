package net.povstalec.sgjourney.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public class HolderItem extends Item
{
	public HolderItem(Properties properties)
	{
		super(properties);
	}
	
	public ItemStack getHeldItem(ItemStack holderStack)
	{
		IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
		if(itemHandler == null)
			return ItemStack.EMPTY;
		
		return itemHandler.getStackInSlot(0);
	}
	
	protected int getWeight(ItemStack stack)
	{
		if(stack.is(Items.BUNDLE))
			return 4 + getContentWeight(stack);
		else
		{
			if((stack.is(Items.BEEHIVE) || stack.is(Items.BEE_NEST)) && stack.hasTag())
			{
				CompoundTag compoundtag = BlockItem.getBlockEntityData(stack);
				if(compoundtag != null && !compoundtag.getList("Bees", 10).isEmpty())
					return 64;
			}
			
			return 64 / stack.getMaxStackSize();
		}
	}
	
	protected int getContentWeight(ItemStack holderStack)
	{
		ItemStack heldStack = getHeldItem(holderStack);
		
		return getWeight(heldStack) * heldStack.getCount();
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickAction, Player player)
	{
		if(clickAction != ClickAction.SECONDARY)
			return false;
		
		ItemStack itemstack = slot.getItem();
		if(itemstack.isEmpty())
		{
			//this.playRemoveOneSound(player);
			removeOne(stack).ifPresent((p_150740_) ->
			{
				add(stack, slot.safeInsert(p_150740_));
			});
		}
		else if (itemstack.getItem().canFitInsideContainerItems())
		{
			int i = (64 - getContentWeight(stack)) / getWeight(itemstack);
			int j = add(stack, slot.safeTake(itemstack.getCount(), i, player));
			/*if (j > 0)
			{
				this.playInsertSound(player);
			}*/
		}
		
		return true;
	}
	
	public boolean overrideOtherStackedOnMe(ItemStack holderStack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess)
	{
		if(clickAction == ClickAction.SECONDARY && slot.allowModification(player))
		{
			if(otherStack.isEmpty())
			{
				removeOne(holderStack).ifPresent((removedStack) ->
				{
					//this.playRemoveOneSound(player);
					slotAccess.set(removedStack);
				});
			}
			else
			{
				int i = add(holderStack, otherStack);
				if (i > 0)
				{
					//this.playInsertSound(player);
					otherStack.shrink(i);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	protected int add(ItemStack holderStack, ItemStack otherStack)
	{
		IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
		if(itemHandler == null)
			return 0;
		
		if(!itemHandler.getStackInSlot(0).isEmpty())
			return 0;
		
		itemHandler.insertItem(0, otherStack, false);
		return 1;
	}
	
	protected Optional<ItemStack> removeOne(ItemStack holderStack)
	{
		IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
		if(itemHandler == null)
			return Optional.empty();
		
		ItemStack removedStack = itemHandler.extractItem(0, 1, false);
		if(removedStack.isEmpty())
			return Optional.empty();
		
		return Optional.of(removedStack);
	}
}
