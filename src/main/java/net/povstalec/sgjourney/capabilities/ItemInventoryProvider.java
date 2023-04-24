package net.povstalec.sgjourney.capabilities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class ItemInventoryProvider implements ICapabilityProvider
{
	private static final String INVENTORY = "Inventory";
	
	private ItemStack stack;
	
	public ItemInventoryProvider(ItemStack stack)
	{
		this.stack = stack;
	}
	
	public abstract int getNumberOfSlots();
	
	public abstract boolean isValid(int slot, @Nonnull ItemStack stack);
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(getNumberOfSlots())
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					saveInventory();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return isValid(slot, stack);
				}
				
				// Limits the number of items per slot
				public int getSlotLimit(int slot)
				{
					return 1;
				}
				
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					loadInventory();
					if(!isItemValid(slot, stack))
					{
						return stack;
					}
					
					return super.insertItem(slot, stack, simulate);
				}
				
				@Nonnull
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate)
				{
					loadInventory();
					return super.extractItem(slot, amount, simulate);
				}
				
				@Override
			    @NotNull
			    public ItemStack getStackInSlot(int slot)
			    {
					loadInventory();
			        validateSlotIndex(slot);
			        return this.stacks.get(slot);
			    }
			};
	}

	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ITEM_HANDLER)
			return handler.cast();
		return LazyOptional.empty();
	}

	// This needs to be used whenever the inventory is interacted with, otherwise a bunch of stuff doesn't work
	public void loadInventory()
	{
		CompoundTag tag = stack.getOrCreateTag();
		if(tag.contains(INVENTORY))
			itemHandler.deserializeNBT(tag.getCompound(INVENTORY));
	}
	
	public void saveInventory()
	{
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(INVENTORY, itemHandler.serializeNBT());
		stack.setTag(tag);
	}
}
