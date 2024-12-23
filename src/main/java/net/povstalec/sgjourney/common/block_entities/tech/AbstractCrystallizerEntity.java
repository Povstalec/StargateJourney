package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.packets.ClientboundCrystallizerUpdatePacket;

public abstract class AbstractCrystallizerEntity extends EnergyBlockEntity
{
	private static final String PROGRESS = "Progress";
	
	public static final int LIQUID_NAQUADAH_CAPACITY = 4000;
	public static final int MAX_PROGRESS = 200;
    
	protected final ItemStackHandler itemStackHandler = createItemStackHandler();
	protected final Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemStackHandler);
	
	protected final FluidTank fluidTank = createFluidTank();
	protected Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidTank);
	
	public int progress = 0;
	
	public AbstractCrystallizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void invalidateCapabilities()
	{
		super.invalidateCapabilities();
		lazyFluidHandler.invalidate();
		lazyItemHandler.invalidate();
	}
	
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.loadAdditional(nbt, registries);
		itemStackHandler.deserializeNBT(registries, nbt.getCompound("Inventory"));
		fluidTank.readFromNBT(registries, nbt);
		
		progress = nbt.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries)
	{
		nbt.put("Inventory", itemStackHandler.serializeNBT(registries));
		nbt = fluidTank.writeToNBT(registries, nbt);
		
		nbt.putInt(PROGRESS, progress);
		super.saveAdditional(nbt, registries);
	}
	
	public abstract Fluid getDesiredFluid();
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IFluidHandler getFluidHandler(Direction side)
	{
		return lazyFluidHandler.get();
	}
	
	public IItemHandler getItemHandler(Direction side)
	{
		return lazyItemHandler.get();
	}
	
	private FluidTank createFluidTank()
	{
		return new FluidTank(LIQUID_NAQUADAH_CAPACITY)
		{
			@Override
			protected void onContentsChanged()
			{
				setChanged();
			}
			
			@Override
			public boolean isFluidValid(FluidStack stack)
			{
				return stack.getFluid() == getDesiredFluid();
			}
		};
	}
	
	public void setFluid(FluidStack fluidStack)
	{
		this.fluidTank.setFluid(fluidStack);
	}
	
	public FluidStack getFluid()
	{
		return this.fluidTank.getFluid();
	}
	
	private ItemStackHandler createItemStackHandler()
	{
		return new ItemStackHandler(5)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}

			    @Override
			    public int getSlotLimit(int slot)
			    {
			    	return switch(slot)
					{
						case 4 -> 1;
						default -> 64;
					};
			    }
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return switch(slot)
					{
						case 3 -> false;
						case 4 -> stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
						default -> true;
					};
				}
				
				@Override
				@NotNull
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					if(!isItemValid(slot, stack))
						return stack;
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
	}

	public boolean hasFluidItem()
	{
    	return itemStackHandler.getStackInSlot(4).getCount() > 0;
	}
	
	public void fillFluidTank(FluidStack stack, ItemStack container)
	{
		fluidTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);

        itemStackHandler.extractItem(4, 1, false);
		itemStackHandler.insertItem(4, container, false);
    }
	
	public void drainFluidFromItem()
	{
		IFluidHandlerItem cap = itemStackHandler.getStackInSlot(4).getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
		{
			int drainAmount = Math.min(fluidTank.getSpace(), 1000);
			FluidStack stack = cap.getFluidInTank(0);
			
			if(fluidTank.isFluidValid(stack))
			{
				stack = cap.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillFluidTank(stack, cap.getContainer());
			}
		}
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================

	@Override
	protected long capacity()
	{
		return 0;
	}

	@Override
	protected long maxReceive()
	{
		return 0;
	}

	@Override
	protected long maxExtract()
	{
		return 0;
	}
	
	protected static boolean hasSpaceInOutputSlot(SimpleContainer inventory, ItemStack stack)
	{
		if(inventory.getItem(3).getMaxStackSize() <= inventory.getItem(3).getCount())
			return false;
		
        return inventory.getItem(3).getItem() == stack.getItem() || inventory.getItem(3).isEmpty();
    }

	protected abstract boolean hasIngredients();

	protected abstract void crystallize();
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractCrystallizerEntity crystallizer)
	{
		if(level.isClientSide())
			return;
		
	    if(crystallizer.hasFluidItem())
	    	crystallizer.drainFluidFromItem();
	    
	    if(crystallizer.hasIngredients() && crystallizer.fluidTank.getFluidAmount() > 0)
	    {
	    	crystallizer.progress++;
	    	crystallizer.fluidTank.drain(1, IFluidHandler.FluidAction.EXECUTE);
	    	setChanged(level, pos, state);
	    	
	    	if(crystallizer.progress >= MAX_PROGRESS)
	    		crystallizer.crystallize();
	    }
	    else
	    {
	    	crystallizer.progress = 0;
	    	setChanged(level, pos, state);
	    }
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(crystallizer.worldPosition).getPos(), new ClientboundCrystallizerUpdatePacket(crystallizer.worldPosition, crystallizer.getFluid(), crystallizer.progress));
	}
	
}
