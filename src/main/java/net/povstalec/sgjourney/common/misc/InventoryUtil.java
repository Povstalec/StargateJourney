package net.povstalec.sgjourney.common.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;

import javax.annotation.Nullable;

public class InventoryUtil
{
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
	public static final String NAME = "Name";
	
	public static boolean hasPlayerStackInInventory(Player player, Item item)
	{
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.sameItem(new ItemStack(item)))
                return true;
        }

        return false;
    }

    public static int getFirstInventoryIndex(Player player, Item item)
    {
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack currentStack = player.getInventory().getItem(i);
            if (!currentStack.isEmpty() && currentStack.sameItem(new ItemStack(item)))
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
        if(stack.hasTag() && stack.getTag().contains(BLOCK_ENTITY_TAG))
            return stack.getTag().getCompound(BLOCK_ENTITY_TAG);
        
        return null;
    }
    
    public static ItemStack generationStep(ItemStack stack, StructureGenEntity.Step step)
    {
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putByte(StructureGenEntity.GENERATION_STEP, step.byteValue());
        stack.addTagElement(BLOCK_ENTITY_TAG, blockEntityTag);
        
        return stack;
    }
	
	public static boolean stackHasEnergy(ItemStack stack)
	{
		if(stack.getCapability(ForgeCapabilities.ENERGY).isPresent())
		{
			IEnergyStorage energyStorage = stack.getCapability(ForgeCapabilities.ENERGY).resolve().get();
			
			return energyStorage.canExtract() && energyStorage.getEnergyStored() > 0;
		}
		
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
		if(stack.is(Items.PLAYER_HEAD) && stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if(tag.contains(PlayerHeadItem.TAG_SKULL_OWNER, Tag.TAG_STRING))
				return tag.getString(PlayerHeadItem.TAG_SKULL_OWNER);
			else if(tag.contains(PlayerHeadItem.TAG_SKULL_OWNER, Tag.TAG_COMPOUND))
			{
				CompoundTag ownerTag = tag.getCompound(PlayerHeadItem.TAG_SKULL_OWNER);
				if(ownerTag.contains(NAME, Tag.TAG_STRING))
					return ownerTag.getString(NAME);
			}
		}
		
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
