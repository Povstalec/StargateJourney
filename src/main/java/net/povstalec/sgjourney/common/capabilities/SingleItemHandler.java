package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.jetbrains.annotations.NotNull;

public abstract class SingleItemHandler extends ComponentItemHandler
{
	public SingleItemHandler(MutableDataComponentHolder parent, DataComponentType<ItemContainerContents> component)
	{
		super(parent, component, 1);
	}
	
	public @NotNull ItemStack getHeldItem()
	{
		return getStackInSlot(0);
	}
	
	public boolean hasItem()
	{
		return !getHeldItem().isEmpty();
	}
	
	@Override
	public int getSlots()
	{
		return 1;
	}
	
	@Override
	public @NotNull ItemStack getStackInSlot(int slot)
	{
		return super.getStackInSlot(slot);
	}
	
	@Override
	public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		return super.insertItem(slot, stack, simulate); //TODO Does this need changing?
	}
	
	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		return super.extractItem(slot, amount, simulate); //TODO Does this need changing?
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		return 1;
	}
	
	//TODO isItemValid()
	
	public void updateContents(ItemStack stack)
	{
		ItemContainerContents contents = this.getContents();
		this.updateContents(contents, stack, 0);
	}
}
