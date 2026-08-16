package net.povstalec.sgjourney.common.items;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

/**
 * Class representing an item that holds another item inside it
 */
public class HolderItem extends Item implements IHolderItem
{
	public HolderItem(Properties properties)
	{
		super(properties);
	}
	
	public void onSwapped(ItemStack holderStack, ItemStack insertedStack, ItemStack removedStack) {}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack holderStack, Slot slot, ClickAction clickAction, Player player)
	{
		return stackedOnOther(holderStack, slot, clickAction, player);
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack holderStack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess)
	{
		return otherStackedOnMe(holderStack, otherStack, slot, clickAction, player, slotAccess);
	}
}
