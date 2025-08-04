package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

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
    	return BuiltInRegistries.ITEM.getKey(item).toString();
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
        if(stack.has(DataComponents.BLOCK_ENTITY_DATA))
            return stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
        
        return null;
    }
    
    public static ItemStack generationStep(BlockEntityType<?> blockEntityType, ItemStack stack, StructureGenEntity.Step step)
    {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putByte(AbstractStargateEntity.GENERATION_STEP, step.byteValue());
        BlockEntity.addEntityType(blockEntityTag, blockEntityType);
        
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
        
        return stack;
    }
}
