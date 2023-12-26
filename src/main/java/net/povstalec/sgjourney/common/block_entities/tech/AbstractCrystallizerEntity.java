package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;

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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundCrystallizerUpdatePacket;

public abstract class AbstractCrystallizerEntity extends EnergyBlockEntity
{
	private static final String PROGRESS = "Progress";
	
	public static final int LIQUID_NAQUADAH_CAPACITY = 4000;
	public static final int MAX_PROGRESS = 200;
    
	protected final ItemStackHandler itemHandler = createHandler();
	protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	protected LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
	
	public int progress = 0;
	
	public AbstractCrystallizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyFluidHandler = LazyOptional.of(() -> fluidTank);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		lazyFluidHandler.invalidate();
		handler.invalidate();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
		fluidTank.readFromNBT(nbt);
		
		progress = nbt.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.put("Inventory", itemHandler.serializeNBT());
		nbt = fluidTank.writeToNBT(nbt);
		
		nbt.putInt(PROGRESS, progress);
		super.saveAdditional(nbt);
	}
	
	public abstract Fluid getDesiredFluid();
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.FLUID_HANDLER)
			return lazyFluidHandler.cast();
		
		else if(capability == ForgeCapabilities.ITEM_HANDLER)
			return handler.cast();
		
		return super.getCapability(capability, side);
	}
	
	private final FluidTank fluidTank = new FluidTank(LIQUID_NAQUADAH_CAPACITY)
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
	
	public void setFluid(FluidStack fluidStack)
	{
		this.fluidTank.setFluid(fluidStack);
	}
	
	public FluidStack getFluid()
	{
		return this.fluidTank.getFluid();
	}
	
	private ItemStackHandler createHandler()
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
						case 4 -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
						default -> true;
					};
				}
				
				@Nonnull
				@Override
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
    	return itemHandler.getStackInSlot(4).getCount() > 0;
	}
	
	public void fillFluidTank(FluidStack stack, ItemStack container)
	{
		fluidTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);

        itemHandler.extractItem(4, 1, false);
        itemHandler.insertItem(4, container, false);
    }
	
	public void drainFluidFromItem()
	{
		itemHandler.getStackInSlot(4).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
		{
			int drainAmount = Math.min(fluidTank.getSpace(), 1000);
			FluidStack stack = handler.getFluidInTank(0);
			
			if(fluidTank.isFluidValid(stack))
			{
				stack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillFluidTank(stack, handler.getContainer());
			}
		});
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
	    
	    PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(crystallizer.worldPosition)), new ClientboundCrystallizerUpdatePacket(crystallizer.worldPosition, crystallizer.getFluid(), crystallizer.progress));
	}
	
}
