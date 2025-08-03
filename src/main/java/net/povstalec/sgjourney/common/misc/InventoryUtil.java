package net.povstalec.sgjourney.common.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;

import javax.annotation.Nullable;

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
    	return ForgeRegistries.ITEMS.getKey(item).toString();
    }
    
    public static CompoundTag addItem(int slot, String id, int count, @Nullable CompoundTag tag)
    {
        CompoundTag itemTag = new CompoundTag();
        
        itemTag.putInt("Slot", slot);
        itemTag.putString("id", id);
        itemTag.putByte("Count", (byte) count);
        
        if(tag != null)
            itemTag.put("tag", tag);
        
        return itemTag;
    }
    
    @Nullable
    public static CompoundTag getBlockEntityTag(ItemStack stack)
    {
        if(stack.hasTag() && stack.getTag().contains(BlockItem.BLOCK_ENTITY_TAG))
            return stack.getTag().getCompound(BlockItem.BLOCK_ENTITY_TAG);
        
        return null;
    }
    
    public static ItemStack generationStep(ItemStack stack, StructureGenEntity.Step step)
    {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putByte(StructureGenEntity.GENERATION_STEP, step.byteValue());
        stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag);
        
        return stack;
    }
}
