package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundNaquadahLiquidizerUpdatePacket;

public abstract class AbstractNaquadahLiquidizerEntity extends BlockEntity
{
	private static final String PROGRESS = "Progress";

	public static final int TANK_CAPACITY = 4000;
	public static final int MAX_PROGRESS = 100;
	
	protected final ItemStackHandler itemHandler = createHandler();
	protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	protected LazyOptional<IFluidHandler> lazyFluidHandler1 = LazyOptional.empty();
	protected LazyOptional<IFluidHandler> lazyFluidHandler2 = LazyOptional.empty();
	
	public int progress = 0;
	
	public AbstractNaquadahLiquidizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyFluidHandler1 = LazyOptional.of(() -> fluidTank1);
		lazyFluidHandler2 = LazyOptional.of(() -> fluidTank2);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		lazyFluidHandler1.invalidate();
		lazyFluidHandler2.invalidate();
		handler.invalidate();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
		fluidTank1.readFromNBT(nbt.getCompound("FluidTank1"));
		fluidTank2.readFromNBT(nbt.getCompound("FluidTank2"));
		
		progress = nbt.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		CompoundTag tag1 = new CompoundTag();
		CompoundTag tag2 = new CompoundTag();
		nbt.put("Inventory", itemHandler.serializeNBT());
		
		fluidTank1.writeToNBT(tag1);
		nbt.put("FluidTank1", tag1);
		fluidTank2.writeToNBT(tag2);
		nbt.put("FluidTank2", tag2);
		
		nbt.putInt(PROGRESS, progress);
		super.saveAdditional(nbt);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.FLUID_HANDLER)
		{
			if(side == Direction.UP)
				return lazyFluidHandler1.cast();
			else if(side == Direction.DOWN)
				return lazyFluidHandler2.cast();
		}
		
		else if(capability == ForgeCapabilities.ITEM_HANDLER)
			return handler.cast();
		
		return super.getCapability(capability, side);
	}
	
	public abstract Fluid getDesiredFluid1();
	
	public abstract Fluid getDesiredFluid2();
	
	protected final FluidTank fluidTank1 = new FluidTank(TANK_CAPACITY)
	{
		@Override
		protected void onContentsChanged()
		{
			setChanged();
	    }
		
		@Override
	    public boolean isFluidValid(FluidStack stack)
	    {
			return stack.getFluid() == getDesiredFluid1();
	    }
	};
	
	protected final FluidTank fluidTank2 = new FluidTank(TANK_CAPACITY)
	{
		@Override
		protected void onContentsChanged()
		{
			setChanged();
	    }
		
		@Override
	    public boolean isFluidValid(FluidStack stack)
	    {
			return stack.getFluid() == getDesiredFluid2();
	    }
	};
	
	public void setFluid1(FluidStack fluidStack)
	{
		this.fluidTank1.setFluid(fluidStack);
	}
	
	public FluidStack getFluid1()
	{
		return this.fluidTank1.getFluid();
	}
	
	public void setFluid2(FluidStack fluidStack)
	{
		this.fluidTank2.setFluid(fluidStack);
	}
	
	public FluidStack getFluid2()
	{
		return this.fluidTank2.getFluid();
	}
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(3)
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
						case 0 -> 64;
						default -> 1;
					};
			    }
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return switch(slot)
					{
						case 0 -> true;
						default -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
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

	public boolean hasFluidItem1()
	{
    	return itemHandler.getStackInSlot(1).getCount() > 0;
	}

	public boolean hasFluidItem2()
	{
    	return itemHandler.getStackInSlot(2).getCount() > 0;
	}
	
	public void fillTank1(FluidStack stack, ItemStack container)
	{
		fluidTank1.fill(stack, IFluidHandler.FluidAction.EXECUTE);

        itemHandler.extractItem(1, 1, false);
        itemHandler.insertItem(1, container, false);
    }
	
	public void fillTank2(FluidStack stack, ItemStack container)
	{
		fluidTank1.fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }
	
	public void drainFluidFromItem()
	{
		itemHandler.getStackInSlot(1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
		{
			int drainAmount = Math.min(fluidTank1.getSpace(), 1000);
			FluidStack fluidStack = handler.getFluidInTank(0);
			
			if(fluidTank1.isFluidValid(fluidStack))
			{
				fluidStack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillTank1(fluidStack, handler.getContainer());
			}
		});
	}
	
	public void putFluidInsideItem()
	{
		ItemStack stack = itemHandler.getStackInSlot(2);
		
		stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
		{
			if(!handler.isFluidValid(0, getFluid2()))
				return;
			
			int fillAmount = handler.fill(getFluid2(), IFluidHandler.FluidAction.EXECUTE);
			
			itemHandler.setStackInSlot(2, handler.getContainer());
			
			fluidTank2.drain(fillAmount, IFluidHandler.FluidAction.EXECUTE);
		});
	}
	
	protected abstract boolean hasMaterial();
	
	protected abstract void makeLiquidNaquadah();
	
	protected void useUpItems(int amount)
	{
		itemHandler.extractItem(0, amount, false);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractNaquadahLiquidizerEntity naquadahLiquidizer)
	{
		if(level.isClientSide())
			return;
		
	    if(naquadahLiquidizer.hasFluidItem1())
	    	naquadahLiquidizer.drainFluidFromItem();
	    
	    if(naquadahLiquidizer.hasMaterial() && naquadahLiquidizer.fluidTank1.getFluidAmount() > 0 && naquadahLiquidizer.fluidTank2.getFluidAmount() < naquadahLiquidizer.fluidTank2.getCapacity())
	    {
	    	naquadahLiquidizer.progress++;
	    	naquadahLiquidizer.fluidTank1.drain(1, IFluidHandler.FluidAction.EXECUTE);
	    	setChanged(level, pos, state);
	    	
	    	if(naquadahLiquidizer.progress >= MAX_PROGRESS)
	    		naquadahLiquidizer.makeLiquidNaquadah();
	    }
	    else
	    {
	    	naquadahLiquidizer.progress = 0;
	    	setChanged(level, pos, state);
	    }
		
	    if(naquadahLiquidizer.hasFluidItem2())
	    	naquadahLiquidizer.putFluidInsideItem();
	    
	    PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(naquadahLiquidizer.worldPosition)), new ClientboundNaquadahLiquidizerUpdatePacket(naquadahLiquidizer.worldPosition, naquadahLiquidizer.getFluid1(), naquadahLiquidizer.getFluid2(), naquadahLiquidizer.progress));
	}
	
}
