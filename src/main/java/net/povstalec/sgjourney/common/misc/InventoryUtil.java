package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InventoryUtil
{
	public static boolean hasPlayerStackInInventory(Player player, Item item)
	{
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.is(item))
                return true;
        }

        return false;
    }

    public static int getFirstInventoryIndex(Player player, Item item)
    {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.is(item))
                return i;
        }

        return -1;
    }
    
    public static String itemName(Item item)
    {
    	return BuiltInRegistries.ITEM.getKey(item).toString();
    }
}
