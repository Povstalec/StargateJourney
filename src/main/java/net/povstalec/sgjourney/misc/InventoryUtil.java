package net.povstalec.sgjourney.misc;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class InventoryUtil
{
	public static boolean hasPlayerStackInInventory(Player player, Item item)
	{
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.sameItem(new ItemStack(item)))
            {
                return true;
            }
        }

        return false;
    }

    public static int getFirstInventoryIndex(Player player, Item item)
    {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.sameItem(new ItemStack(item)))
            {
                return i;
            }
        }

        return -1;
    }
    
    public static String itemName(Item item)
    {
    	return ForgeRegistries.ITEMS.getKey(item).toString();
    }
}
