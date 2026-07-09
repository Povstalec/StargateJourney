package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.SimpleFluidContainer;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

public abstract class AbstractNaquadahLiquidizerEntity<R extends LiquidizingRecipe> extends ProgressRecipeEnergyBlockEntity<R, SimpleFluidContainer>
{
	private static final String INVENTORY = "Inventory"; //TODO For legacy reasons
	
	private static final String INPUT_INVENTORY = "input_inventory";
	private static final String FLUID_INPUT_INVENTORY = "fluid_input_inventory";
	private static final String FLUID_OUTPUT_INVENTORY = "fluid_output_inventory";
	
	public final ItemStackHandler itemInputHandler = createItemInputHandler();
	protected final LazyOptional<IItemHandler> lazyInputHandler = LazyOptional.of(() -> itemInputHandler);
	public final ItemStackHandler fluidItemInputHandler = createFluidItemHandler(true);
	protected final LazyOptional<IItemHandler> lazyFluidInputHandler = LazyOptional.of(() -> fluidItemInputHandler);
	public final ItemStackHandler fluidItemOutputHandler = createFluidItemHandler(false);
	protected final LazyOptional<IItemHandler> lazyFluidOutputHandler = LazyOptional.of(() -> fluidItemOutputHandler);
	
	protected LazyOptional<IFluidHandler> lazyFluidHandler1 = LazyOptional.empty();
	protected LazyOptional<IFluidHandler> lazyFluidHandler2 = LazyOptional.empty();
	
	public AbstractNaquadahLiquidizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, new SimpleFluidContainer(1, 2));
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyFluidHandler1 = LazyOptional.of(() -> inputFluidTank);
		lazyFluidHandler2 = LazyOptional.of(() -> outputFluidTank);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		
		lazyFluidHandler1.invalidate();
		lazyFluidHandler2.invalidate();
		
		lazyInputHandler.invalidate();
		lazyFluidInputHandler.invalidate();
		lazyFluidOutputHandler.invalidate();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		
		if(nbt.contains(INVENTORY))
		{
			ItemStackHandler itemHandler = new ItemStackHandler(3);
			itemHandler.deserializeNBT(nbt.getCompound(INVENTORY));
			
			itemInputHandler.insertItem(0, itemHandler.getStackInSlot(0), false);
			fluidItemInputHandler.insertItem(0, itemHandler.getStackInSlot(1), false);
			fluidItemOutputHandler.insertItem(0, itemHandler.getStackInSlot(2), false);
		}
		else
		{
			itemInputHandler.deserializeNBT(nbt.getCompound(INPUT_INVENTORY));
			fluidItemInputHandler.deserializeNBT(nbt.getCompound(FLUID_INPUT_INVENTORY));
			InventoryUtil.expandSlotsIfNeeded(fluidItemInputHandler, 2);
			fluidItemOutputHandler.deserializeNBT(nbt.getCompound(FLUID_OUTPUT_INVENTORY));
			InventoryUtil.expandSlotsIfNeeded(fluidItemOutputHandler, 2);
		}
		
		inputFluidTank.readFromNBT(nbt.getCompound("FluidTank1"));
		outputFluidTank.readFromNBT(nbt.getCompound("FluidTank2"));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		CompoundTag tag1 = new CompoundTag();
		CompoundTag tag2 = new CompoundTag();
		
		nbt.put(INPUT_INVENTORY, itemInputHandler.serializeNBT());
		nbt.put(FLUID_INPUT_INVENTORY, fluidItemInputHandler.serializeNBT());
		nbt.put(FLUID_OUTPUT_INVENTORY, fluidItemOutputHandler.serializeNBT());
		
		inputFluidTank.writeToNBT(tag1);
		nbt.put("FluidTank1", tag1);
		outputFluidTank.writeToNBT(tag2);
		nbt.put("FluidTank2", tag2);
		
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
			if(side == Direction.DOWN)
				return lazyFluidHandler2.cast();
			else
				return lazyFluidHandler1.cast();
		}
		
		else if(capability == ForgeCapabilities.ITEM_HANDLER)
		{
			if(side == Direction.UP)
				return lazyInputHandler.cast();
			else if(side == Direction.DOWN)
				return lazyFluidOutputHandler.cast();
			else
				return lazyFluidInputHandler.cast();
		}
		
		return super.getCapability(capability, side);
	}
	
	public abstract boolean isDesiredInputFluid(FluidStack fluidStack);
	
	@Override
	protected void updateSimpleContainer()
	{
		this.simpleContainer.setItem(0, itemInputHandler.getStackInSlot(0));
		this.simpleContainer.setFluid(0, inputFluidTank.getFluid());
	}
	
	public abstract int inputFluidTankCapacity();
	
	public abstract int maxFluidReceive();
	
	protected final FluidTank inputFluidTank = new FluidTank(inputFluidTankCapacity())
	{
		@Override
		protected void onContentsChanged()
		{
			updateSimpleContainer();
			setChanged();
	    }
		
		@Override
		public boolean isFluidValid(FluidStack stack)
		{
			return isDesiredInputFluid(stack);
		}
	};
	
	public abstract int outputFluidTankCapacity();
	
	public abstract int maxFluidExtract();
	
	protected final FluidTank outputFluidTank = new FluidTank(outputFluidTankCapacity())
	{
		@Override
		protected void onContentsChanged()
		{
			setChanged();
	    }
		
		@Override
	    public boolean isFluidValid(FluidStack stack)
	    {
			return true;
	    }
	};
	
	public FluidStack getInputFluidStack()
	{
		return this.inputFluidTank.getFluid();
	}
	
	public FluidStack getOutputFluidStack()
	{
		return this.outputFluidTank.getFluid();
	}
	
	private ItemStackHandler createItemInputHandler()
	{
		return new ItemStackHandler(1)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				updateSimpleContainer();
				setChanged();
			}
		};
	}
	
	private ItemStackHandler createFluidItemHandler(boolean input)
	{
		return new ItemStackHandler(2)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}

			    @Override
			    public int getSlotLimit(int slot)
			    {
			    	return input ? 1 : 64;
			    }
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
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

	public boolean hasInputFluidItem()
	{
    	return fluidItemInputHandler.getStackInSlot(0).getCount() > 0;
	}

	public boolean hasOutputFluidItem()
	{
    	return fluidItemInputHandler.getStackInSlot(1).getCount() > 0;
	}
	
	public void fillInputTank(FluidStack stack, ItemStack container)
	{
		inputFluidTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
		
		fluidItemInputHandler.extractItem(0, 1, false);
		fluidItemInputHandler.insertItem(0, container, false);
    }
	
	public void dumpEmptyFluidContainers()
	{
		ItemStack container = fluidItemInputHandler.getStackInSlot(0);
		Optional<IFluidHandlerItem> fluidHandler = container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
		if(fluidHandler.isPresent())
		{
			if(fluidHandler.get().getFluidInTank(0).isEmpty()) // Try placing the container in the dump
				InventoryUtil.dumpIfPossible(fluidItemInputHandler, 0, fluidItemOutputHandler, 1);
		}
		else
			InventoryUtil.dumpIfPossible(fluidItemInputHandler, 0, fluidItemOutputHandler, 1);
	}
	
	public void dumpFullFluidContainers()
	{
		ItemStack container = fluidItemInputHandler.getStackInSlot(1);
		Optional<IFluidHandlerItem> fluidHandler = container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
		if(fluidHandler.isPresent())
		{
			if(fluidHandler.get().getFluidInTank(0).getAmount() >= fluidHandler.get().getTankCapacity(0)) // Try placing the container in the dump
				InventoryUtil.dumpIfPossible(fluidItemInputHandler, 1, fluidItemOutputHandler, 0);
		}
		else
			InventoryUtil.dumpIfPossible(fluidItemInputHandler, 1, fluidItemOutputHandler, 0);
	}
	
	public void drainInputFluidItem()
	{
		fluidItemInputHandler.getStackInSlot(0).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
		{
			int drainAmount = Math.min(inputFluidTank.getSpace(), maxFluidReceive());
			FluidStack fluidStack = handler.getFluidInTank(0);
			
			if(inputFluidTank.isFluidValid(fluidStack) && isSameFluidOrEmpty(inputFluidTank.getFluidInTank(0), fluidStack))
			{
				fluidStack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillInputTank(fluidStack, handler.getContainer());
			}
		});
	}
	
	public void fillOutputFluidItem()
	{
		fluidItemInputHandler.getStackInSlot(1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
		{
			FluidStack simulatedDrained = outputFluidTank.drain(maxFluidExtract(), IFluidHandler.FluidAction.SIMULATE);
			int simulatedFilledAmount = handler.fill(simulatedDrained, IFluidHandler.FluidAction.SIMULATE);
			
			if(simulatedFilledAmount > 0)
			{
				FluidStack drained = outputFluidTank.drain(simulatedFilledAmount, IFluidHandler.FluidAction.EXECUTE);
				handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
				
				fluidItemInputHandler.setStackInSlot(1, handler.getContainer());
			}
		});
	}
	
	public void dumpInputFluidTank()
	{
		inputFluidTank.drain(inputFluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
	}
	
	public void dumpOutputFluidTank()
	{
		outputFluidTank.drain(outputFluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
	}
	
	// Recipe
	
	public boolean canOutput(R recipe)
	{
		// Check if it's the same fluid as in the output tank
		if(isSameFluidOrEmpty(outputFluidTank.getFluidInTank(0), recipe.getOutputFluid()))
			return outputFluidTank.getFluidAmount() + recipe.getOutputFluid().getAmount() <= outputFluidTank.getCapacity(); // Check if the fluid fits in the output tank
		
		return false;
	}
	
	@Override
	public void depleteIngredients(R recipe)
	{
		itemInputHandler.extractItem(0, 1, false);
		inputFluidTank.drain(recipe.getInputFluid(), IFluidHandler.FluidAction.EXECUTE);
	}
	
	@Override
	public void createOutput(R recipe)
	{
		outputFluidTank.fill(recipe.getOutputFluid().copy(), IFluidHandler.FluidAction.EXECUTE);
	}
	
	public void outputLiquid()
	{
		BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(Direction.DOWN));
		
		if(blockEntity == null)
			return;
		
		blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).ifPresent(fluidHandler ->
		{
			FluidStack simulatedOutputAmount = this.outputFluidTank.drain(maxFluidExtract(), IFluidHandler.FluidAction.SIMULATE);
			int simulatedReceiveAmount = fluidHandler.fill(simulatedOutputAmount, IFluidHandler.FluidAction.SIMULATE);
			
			fluidHandler.fill(this.outputFluidTank.drain(simulatedReceiveAmount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
		});
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractNaquadahLiquidizerEntity<?> liquidizer)
	{
		if(!level.isClientSide())
		{
			if(liquidizer.hasInputFluidItem())
				liquidizer.drainInputFluidItem();
			liquidizer.dumpEmptyFluidContainers();
		}
		
		ProgressRecipeEnergyBlockEntity.tick(level, pos, state, liquidizer);
		
		if(level.isClientSide())
			return;
		
		if(liquidizer.hasOutputFluidItem())
			liquidizer.fillOutputFluidItem();
		liquidizer.dumpFullFluidContainers();
		
		liquidizer.outputLiquid();
		
		liquidizer.updateClient();
	}
}
