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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.povstalec.sgjourney.common.packets.ClientboundNaquadahLiquidizerUpdatePacket;

public abstract class AbstractNaquadahLiquidizerEntity extends BlockEntity
{
	private static final String PROGRESS = "progress";
	private static final String INPUT_INVENTORY = "input_inventory";
	private static final String FLUID_INPUT_INVENTORY = "fluid_input_inventory";
	private static final String FLUID_OUTPUT_INVENTORY = "fluid_output_inventory";
	public static final String FLUID_TANK_1 = "fluid_tank_1";
	public static final String FLUID_TANK_2 = "fluid_tank_2";

	public static final int TANK_CAPACITY = 4000;
	public static final int MAX_PROGRESS = 100;
	
	protected final ItemStackHandler itemInputHandler = createItemInputHandler();
	protected final Lazy<IItemHandler> lazyInputHandler = Lazy.of(() -> itemInputHandler);
	protected final ItemStackHandler fluidItemInputHandler = createFluidItemHandler();
	protected final Lazy<IItemHandler> lazyFluidInputHandler = Lazy.of(() -> fluidItemInputHandler);
	protected final ItemStackHandler fluidItemOutputHandler = createFluidItemHandler();
	protected final Lazy<IItemHandler> lazyFluidOutputHandler = Lazy.of(() -> fluidItemOutputHandler);
	
	protected final FluidTank fluidTank1 = createFluidTank1();
	protected Lazy<IFluidHandler> lazyFluidHandler1 = Lazy.of(() -> fluidTank1);
	protected final FluidTank fluidTank2 = createFluidTank2();
	protected Lazy<IFluidHandler> lazyFluidHandler2 = Lazy.of(() -> fluidTank2);
	
	public int progress = 0;
	
	public AbstractNaquadahLiquidizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void invalidateCapabilities()
	{
		super.invalidateCapabilities();

		lazyFluidHandler1.invalidate();
		lazyFluidHandler2.invalidate();
		
		lazyInputHandler.invalidate();
		lazyFluidInputHandler.invalidate();
		lazyFluidOutputHandler.invalidate();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		itemInputHandler.deserializeNBT(registries, tag.getCompound(INPUT_INVENTORY));
		fluidItemInputHandler.deserializeNBT(registries, tag.getCompound(FLUID_INPUT_INVENTORY));
		fluidItemOutputHandler.deserializeNBT(registries, tag.getCompound(FLUID_OUTPUT_INVENTORY));
		
		fluidTank1.readFromNBT(registries, tag.getCompound(FLUID_TANK_1));
		fluidTank2.readFromNBT(registries, tag.getCompound(FLUID_TANK_2));
		
		progress = tag.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		CompoundTag tag1 = new CompoundTag();
		CompoundTag tag2 = new CompoundTag();
		
		tag.put(INPUT_INVENTORY, itemInputHandler.serializeNBT(registries));
		tag.put(FLUID_INPUT_INVENTORY, fluidItemInputHandler.serializeNBT(registries));
		tag.put(FLUID_OUTPUT_INVENTORY, fluidItemOutputHandler.serializeNBT(registries));
		
		fluidTank1.writeToNBT(registries, tag1);
		tag.put(FLUID_TANK_1, tag1);
		fluidTank2.writeToNBT(registries, tag2);
		tag.put(FLUID_TANK_2, tag2);
		
		tag.putInt(PROGRESS, progress);
		super.saveAdditional(tag, registries);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	private FluidTank createFluidTank1()
	{
		return new FluidTank(TANK_CAPACITY)
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
	}
	
	private FluidTank createFluidTank2()
	{
		return new FluidTank(TANK_CAPACITY)
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
	}
	
	public IFluidHandler getFluidHandler(Direction side)
	{
		if(side == Direction.DOWN)
			return lazyFluidHandler2.get();
		else
			return lazyFluidHandler1.get();
	}
	
	public IItemHandler getItemHandler(Direction side)
	{
		if(side == Direction.UP)
			return lazyInputHandler.get();
		else if(side == Direction.DOWN)
			return lazyFluidOutputHandler.get();
		else
			return lazyFluidInputHandler.get();
	}
	
	public abstract Fluid getDesiredFluid1();
	
	public abstract Fluid getDesiredFluid2();
	
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
	
	private ItemStackHandler createItemInputHandler()
	{
		return new ItemStackHandler(1)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				setChanged();
			}
			
			@Override
			public int getSlotLimit(int slot)
			{
				return 64;
			}
		};
	}
	
	private ItemStackHandler createFluidItemHandler()
	{
		return new ItemStackHandler(1)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}

			    @Override
			    public int getSlotLimit(int slot)
			    {
			    	return 1;
			    }
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
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
    	return fluidItemInputHandler.getStackInSlot(0).getCount() > 0;
	}

	public boolean hasFluidItem2()
	{
    	return fluidItemOutputHandler.getStackInSlot(0).getCount() > 0;
	}
	
	public void fillTank1(FluidStack stack, ItemStack container)
	{
		fluidTank1.fill(stack, IFluidHandler.FluidAction.EXECUTE);
		
		fluidItemInputHandler.extractItem(0, 1, false);
		fluidItemInputHandler.insertItem(0, container, false);
    }
	
	public void fillTank2(FluidStack stack, ItemStack container)
	{
		fluidTank1.fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }
	
	public void drainFluidFromItem()
	{
		IFluidHandlerItem cap = fluidItemInputHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
		{
			int drainAmount = Math.min(fluidTank1.getSpace(), 1000);
			FluidStack fluidStack = cap.getFluidInTank(0);
			
			if(fluidTank1.isFluidValid(fluidStack))
			{
				fluidStack = cap.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillTank1(fluidStack, cap.getContainer());
			}
		}
	}
	
	public void putFluidInsideItem()
	{
		ItemStack stack = fluidItemOutputHandler.getStackInSlot(0);
		
		IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
		{
			if(!cap.isFluidValid(0, getFluid2()))
				return;
			
			int fillAmount = cap.fill(getFluid2(), IFluidHandler.FluidAction.EXECUTE);
			
			fluidItemOutputHandler.setStackInSlot(0, cap.getContainer());
			
			fluidTank2.drain(fillAmount, IFluidHandler.FluidAction.EXECUTE);
		}
	}
	
	protected abstract boolean hasMaterial();
	
	protected abstract void makeLiquidNaquadah();
	
	protected void useUpItems(int amount)
	{
		itemInputHandler.extractItem(0, amount, false);
	}
	
	public void outputLiquid()
	{
		BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(Direction.DOWN));
		
		if(blockEntity == null)
			return;
		
		IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, worldPosition.relative(Direction.DOWN), Direction.UP);
		if(fluidHandler != null)
		{
			fluidHandler.fill(this.fluidTank2.drain(100, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
		}
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
		
		naquadahLiquidizer.outputLiquid();
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(naquadahLiquidizer.worldPosition).getPos(), new ClientboundNaquadahLiquidizerUpdatePacket(naquadahLiquidizer.worldPosition, naquadahLiquidizer.getFluid1(), naquadahLiquidizer.getFluid2(), naquadahLiquidizer.progress));
	}
	
}
