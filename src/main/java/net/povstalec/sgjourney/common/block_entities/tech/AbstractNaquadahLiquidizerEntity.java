package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
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

public abstract class AbstractNaquadahLiquidizerEntity<R extends LiquidizingRecipe> extends ProgressRecipeEnergyBlockEntity<R, SimpleFluidContainer>
{
	private static final String INPUT_INVENTORY = "input_inventory";
	private static final String FLUID_INPUT_INVENTORY = "fluid_input_inventory";
	private static final String FLUID_OUTPUT_INVENTORY = "fluid_output_inventory";
	public static final String FLUID_TANK_1 = "fluid_tank_1";
	public static final String FLUID_TANK_2 = "fluid_tank_2";
	
	public final ItemStackHandler itemInputHandler = createItemInputHandler();
	protected final Lazy<IItemHandler> lazyInputHandler = Lazy.of(() -> itemInputHandler);
	public final ItemStackHandler fluidItemInputHandler = createFluidItemHandler(true);
	protected final Lazy<IItemHandler> lazyFluidInputHandler = Lazy.of(() -> fluidItemInputHandler);
	public final ItemStackHandler fluidItemOutputHandler = createFluidItemHandler(false);
	protected final Lazy<IItemHandler> lazyFluidOutputHandler = Lazy.of(() -> fluidItemOutputHandler);
	
	protected final FluidTank inputFluidTank = createFluidTank1();
	protected Lazy<IFluidHandler> inputFluidHandler = Lazy.of(() -> inputFluidTank);
	protected final FluidTank outputFluidTank = createFluidTank2();
	protected Lazy<IFluidHandler> outputFluidHandler = Lazy.of(() -> outputFluidTank);
	
	public AbstractNaquadahLiquidizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, new SimpleFluidContainer(1, 2));
	}
	
	@Override
	public void invalidateCapabilities()
	{
		super.invalidateCapabilities();
		
		inputFluidHandler.invalidate();
		outputFluidHandler.invalidate();
		
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
		
		inputFluidTank.readFromNBT(registries, tag.getCompound(FLUID_TANK_1));
		outputFluidTank.readFromNBT(registries, tag.getCompound(FLUID_TANK_2));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		CompoundTag tag1 = new CompoundTag();
		CompoundTag tag2 = new CompoundTag();
		
		tag.put(INPUT_INVENTORY, itemInputHandler.serializeNBT(registries));
		tag.put(FLUID_INPUT_INVENTORY, fluidItemInputHandler.serializeNBT(registries));
		tag.put(FLUID_OUTPUT_INVENTORY, fluidItemOutputHandler.serializeNBT(registries));
		
		inputFluidTank.writeToNBT(registries, tag1);
		tag.put(FLUID_TANK_1, tag1);
		outputFluidTank.writeToNBT(registries, tag2);
		tag.put(FLUID_TANK_2, tag2);
		
		super.saveAdditional(tag, registries);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	private FluidTank createFluidTank1()
	{
		return new FluidTank(inputFluidTankCapacity())
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
	}
	
	private FluidTank createFluidTank2()
	{
		return new FluidTank(outputFluidTankCapacity())
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
	}
	
	public IFluidHandler getFluidHandler(Direction side)
	{
		if(side == Direction.DOWN)
			return outputFluidHandler.get();
		else
			return inputFluidHandler.get();
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
	
	public abstract boolean isDesiredInputFluid(FluidStack fluidStack);
	
	@Override
	protected void updateSimpleContainer()
	{
		this.recipeInput.setItem(0, itemInputHandler.getStackInSlot(0));
		this.recipeInput.setFluid(0, inputFluidTank.getFluid());
	}
	
	public abstract int inputFluidTankCapacity();
	
	public abstract int maxFluidReceive();
	
	public abstract int outputFluidTankCapacity();
	
	public abstract int maxFluidExtract();
	
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
		IFluidHandlerItem fluidHandler = container.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
		{
			if(fluidHandler.getFluidInTank(0).isEmpty()) // Try placing the container in the dump
				InventoryUtil.dumpIfPossible(fluidItemInputHandler, 0, fluidItemOutputHandler, 1);
		}
		else
			InventoryUtil.dumpIfPossible(fluidItemInputHandler, 0, fluidItemOutputHandler, 1);
	}
	
	public void dumpFullFluidContainers()
	{
		ItemStack container = fluidItemInputHandler.getStackInSlot(1);
		IFluidHandlerItem fluidHandler = container.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
		{
			if(fluidHandler.getFluidInTank(0).getAmount() >= fluidHandler.getTankCapacity(0)) // Try placing the container in the dump
				InventoryUtil.dumpIfPossible(fluidItemInputHandler, 1, fluidItemOutputHandler, 0);
		}
		else
			InventoryUtil.dumpIfPossible(fluidItemInputHandler, 1, fluidItemOutputHandler, 0);
	}
	
	public void drainInputFluidItem()
	{
		IFluidHandlerItem cap = fluidItemInputHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
		{
			int drainAmount = Math.min(inputFluidTank.getSpace(), maxFluidReceive());
			FluidStack fluidStack = cap.getFluidInTank(0);
			
			if(inputFluidTank.isFluidValid(fluidStack) && isSameFluidOrEmpty(inputFluidTank.getFluidInTank(0), fluidStack))
			{
				fluidStack = cap.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillInputTank(fluidStack, cap.getContainer());
			}
		}
	}
	
	public void fillOutputFluidItem()
	{
		ItemStack stack = fluidItemOutputHandler.getStackInSlot(0);
		
		IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
		{
			FluidStack simulatedDrained = outputFluidTank.drain(maxFluidExtract(), IFluidHandler.FluidAction.SIMULATE);
			int simulatedFilledAmount = cap.fill(simulatedDrained, IFluidHandler.FluidAction.SIMULATE);
			
			if(simulatedFilledAmount > 0)
			{
				FluidStack drained = outputFluidTank.drain(simulatedFilledAmount, IFluidHandler.FluidAction.EXECUTE);
				cap.fill(drained, IFluidHandler.FluidAction.EXECUTE);
				
				fluidItemInputHandler.setStackInSlot(1, cap.getContainer());
			}
		}
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
		
		IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, worldPosition.relative(Direction.DOWN), Direction.UP);
		if(fluidHandler != null)
		{
			FluidStack simulatedOutputAmount = this.outputFluidTank.drain(maxFluidExtract(), IFluidHandler.FluidAction.SIMULATE);
			int simulatedReceiveAmount = fluidHandler.fill(simulatedOutputAmount, IFluidHandler.FluidAction.SIMULATE);
			
			fluidHandler.fill(this.outputFluidTank.drain(simulatedReceiveAmount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
		}
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
