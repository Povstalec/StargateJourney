package net.povstalec.sgjourney.common.items;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public interface IHolderItem
{
	default ItemStack getHeldItem(ItemStack holderStack)
	{
		IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
		if(itemHandler == null)
			return ItemStack.EMPTY;
		
		return itemHandler.getStackInSlot(0);
	}
	
	void onSwapped(ItemStack holderStack, ItemStack insertedStack, ItemStack removedStack);
	
	default ItemStack swapHeldItem(ItemStack holderStack, ItemStack otherStack)
	{
		IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
		if(itemHandler == null || !itemHandler.isItemValid(0, otherStack))
			return otherStack;
		
		if(otherStack.isEmpty())
		{
			ItemStack removedStack = itemHandler.extractItem(0, 1, false);
			onSwapped(holderStack, otherStack, removedStack);
			return removedStack;
		}
		
		ItemStack heldStack = itemHandler.getStackInSlot(0);
		
		if(heldStack.isEmpty())
		{
			ItemStack removedStack = itemHandler.insertItem(0, otherStack, false);
			onSwapped(holderStack, otherStack, removedStack);
			return removedStack;
		}
		
		heldStack = itemHandler.extractItem(0, 1, false);
		onSwapped(holderStack, otherStack, heldStack);
		itemHandler.insertItem(0, otherStack, false);
		
		return heldStack;
	}
	
	default boolean stackedOnOther(ItemStack holderStack, Slot slot, ClickAction clickAction, Player player)
	{
		if(clickAction != ClickAction.SECONDARY)
			return false;
		
		ItemStack slotStack = slot.getItem();
		
		if(slotStack.isEmpty())
		{
			ItemStack swappedStack = swapHeldItem(holderStack, slotStack);
			slot.safeInsert(swappedStack);
		}
		else
		{
			ItemStack swappedStack = swapHeldItem(holderStack, slotStack);
			if(swappedStack != slotStack)
			{
				slot.remove(1);
				slot.safeInsert(swappedStack);
			}
		}
		
		return true;
	}
	
	default boolean otherStackedOnMe(ItemStack holderStack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess)
	{
		if(clickAction == ClickAction.SECONDARY && slot.allowModification(player))
		{
			ItemStack swappedStack = swapHeldItem(holderStack, otherStack);
			
			if(!swappedStack.equals(otherStack))
				slotAccess.set(swappedStack);
			
			return true;
		}
		
		return false;
	}
}
