package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SimpleTempContainer<M extends AbstractContainerMenu> implements Container
{
	private final NonNullList<ItemStack> items;
	private final int width;
	private final int height;
	private final M menu;
	
	public SimpleTempContainer(M menu, int width, int height)
	{
		this.items = NonNullList.withSize(width * height, ItemStack.EMPTY);
		this.menu = menu;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getContainerSize()
	{
		return this.items.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		for(ItemStack itemstack : this.items)
		{
			if(!itemstack.isEmpty())
				return false;
		}
		
		return true;
	}
	
	@Override
	public @NotNull ItemStack getItem(int slot)
	{
		return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(slot);
	}
	
	@Override
	public @NotNull ItemStack removeItemNoUpdate(int slot)
	{
		return ContainerHelper.takeItem(this.items, slot);
	}
	
	@Override
	public @NotNull ItemStack removeItem(int x, int y)
	{
		ItemStack itemstack = ContainerHelper.removeItem(this.items, x, y);
		if (!itemstack.isEmpty()) {
			this.menu.slotsChanged(this);
		}
		
		return itemstack;
	}
	
	@Override
	public void setItem(int slot, @NotNull ItemStack stack)
	{
		this.items.set(slot, stack);
		this.menu.slotsChanged(this);
	}
	
	@Override
	public void setChanged() {}
	
	@Override
	public boolean stillValid(@NotNull Player player)
	{
		return true;
	}
	
	@Override
	public void clearContent()
	{
		this.items.clear();
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	public int getWidth()
	{
		return this.width;
	}
}
