package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class ItemFluidHolderProvider implements IFluidHandlerItem, ICapabilityProvider
{
	public static final String INVENTORY_LEGACY = "Inventory"; // Legacy version
	public static final String INVENTORY = "inventory";
	
	private final LazyOptional<IFluidHandlerItem> lazyFluidHandler = LazyOptional.of(() -> this);
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
	
	@NotNull
	protected ItemStack stack;
	
	public ItemFluidHolderProvider(ItemStack stack)
	{
		this.stack = stack;
	}
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ITEM_HANDLER)
			return lazyItemHandler.cast();
		else if(cap == ForgeCapabilities.FLUID_HANDLER_ITEM)
			return lazyFluidHandler.cast();
		
		return LazyOptional.empty();
	}
	
	//============================================================================================
	//********************************************Item********************************************
	//============================================================================================
	
	public abstract boolean isValid(@Nonnull ItemStack stack);
	
	public ItemStack getHeldItemStack()
	{
		return itemHandler.getStackInSlot(0);
	}
	
	public boolean hasItem()
	{
		return !getHeldItemStack().isEmpty();
	}
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(1)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				saveInventory();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return isValid(stack);
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
					return stack;
				
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
	
	public void loadInventory()
	{
		if(!stack.hasTag())
			return;
		
		CompoundTag tag = stack.getTag();
		if(tag.contains(INVENTORY))
			itemHandler.deserializeNBT(tag.getCompound(INVENTORY));
		else if(tag.contains(INVENTORY_LEGACY))
			itemHandler.deserializeNBT(tag.getCompound(INVENTORY_LEGACY));
	}
	
	public void saveInventory()
	{
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(INVENTORY, itemHandler.serializeNBT());
		stack.setTag(tag);
	}
	
	//============================================================================================
	//*******************************************Fluids*******************************************
	//============================================================================================
	
	@Override
	public @NotNull ItemStack getContainer()
	{
		return stack;
	}
	
	@Override
	public int getTanks()
	{
		return 1;
	}
	
	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		if(!hasItem())
			return 0;
		
		ItemStack heldStack = getHeldItemStack();
		IFluidHandlerItem fluidHandler = heldStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
		if(fluidHandler != null)
		{
			int amount = fluidHandler.fill(resource, action);
			
			if(amount != 0)
				saveInventory();
			
			return amount;
		}
		
		return 0;
	}
	
	public @NotNull FluidStack deplete(int maxDrain, FluidAction action)
	{
		if(!hasItem())
			return FluidStack.EMPTY;
		
		ItemStack heldStack = getHeldItemStack();
		IFluidHandlerItem fluidHandler = heldStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
		if(fluidHandler != null)
		{
			FluidStack fluidStack = fluidHandler.drain(maxDrain, action);
			
			if(!fluidStack.isEmpty())
				saveInventory();
			
			return fluidStack;
		}
		
		return FluidStack.EMPTY;
	}
	
	@Override
	public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
	{
		if(stack.getCount() != 1 || resource.isEmpty() || !resource.isFluidEqual(getFluidInTank(0)))
			return FluidStack.EMPTY;
		
		return drain(resource.getAmount(), action);
	}
	
	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action)
	{
		return FluidStack.EMPTY;
	}
}
