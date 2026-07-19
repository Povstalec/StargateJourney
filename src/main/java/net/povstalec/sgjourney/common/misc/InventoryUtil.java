package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;

import javax.annotation.Nullable;

public class InventoryUtil
{
	public static final String NAME = "Name";
	
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
	
	public static InteractionHand otherHand(InteractionHand hand)
	{
		return hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}
	
	public static void dumpIfPossible(IItemHandler from, int fromSlot, IItemHandler to, int toSlot)
	{
		ItemStack stack = from.extractItem(fromSlot, 1, true);
		if(to.insertItem(toSlot, stack, true).isEmpty())
		{
			from.extractItem(fromSlot, 1, false);
			to.insertItem(toSlot, stack, false);
		}
	}
	
	public static boolean canInsertStackInto(ItemStack slotStack, ItemStack toInsert)
	{
		if(slotStack.isEmpty())
			return true;
		
		if(slotStack.getMaxStackSize() <= slotStack.getCount())
			return false;
		
		return slotStack.getItem() == toInsert.getItem();
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
	
	public static boolean stackHasEnergy(ItemStack stack)
	{
		IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(energyStorage != null)
			return energyStorage.canExtract() && energyStorage.getEnergyStored() > 0;
		
		return false;
	}
	
	public static boolean isWeapon(Item item)
	{
		if(item instanceof SwordItem)
			return true;
		
		if(item instanceof StaffWeaponItem)
			return true;
		
		if(item instanceof BowItem)
			return true;
		
		if(item instanceof CrossbowItem)
			return true;
		
		//TODO Add a tag for more items
		
		return false;
	}
	
	@Nullable
	public static String getPlayerNameFromHead(ItemStack stack)
	{
		if(stack.getItem() instanceof PlayerHeadItem playerHead)
			return playerHead.getName(stack).getString();
		
		return null;
	}
	
	public static void expandSlotsIfNeeded(ItemStackHandler handler, int minSlots)
	{
		if(handler.getSlots() >= minSlots)
			return;
		
		ItemStack[] stacks = new ItemStack[handler.getSlots()];
		for(int i = 0; i < stacks.length; i++)
		{
			stacks[i] = handler.getStackInSlot(i);
		}
		handler.setSize(minSlots);
		for(ItemStack stack : stacks)
		{
			handler.setStackInSlot(0, stack);
		}
	}
}
