package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.povstalec.sgjourney.common.blocks.tech.AbstractCrystallizerBlock;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.SimpleFluidContainer;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

public abstract class AbstractCrystallizerEntity<R extends CrystallizingRecipe> extends ProgressRecipeEnergyBlockEntity<R, SimpleFluidContainer>
{
	private static final String INVENTORY = "Inventory"; //TODO For legacy reasons
	
	private static final String CRYSTAL_BASE_INVENTORY = "crystal_base_inventory";
	private static final String PRIMARY_INGREDIENT_INVENTORY = "primary_ingredient_inventory";
	private static final String SECONDARY_INGREDIENT_INVENTORY = "secondary_ingredient_inventory";
	private static final String OUTPUT_INVENTORY = "output_inventory";
	private static final String FLUID_INPUT_INVENTORY = "fluid_input_inventory";
    
	public final ItemStackHandler crystalBaseHandler = createCrystalBaseHandler();
	protected final LazyOptional<IItemHandler> lazyCrystalBaseHandler = LazyOptional.of(() -> crystalBaseHandler);
	public final ItemStackHandler primaryIngredientHandler = createIngredientHandler();
	protected final LazyOptional<IItemHandler> lazyPrimaryIngredientHandler = LazyOptional.of(() -> primaryIngredientHandler);
	public final ItemStackHandler secondaryIngredientHandler = createIngredientHandler();
	protected final LazyOptional<IItemHandler> lazySecondaryIngredientHandler = LazyOptional.of(() -> secondaryIngredientHandler);
	public final ItemStackHandler outputHandler = createOutputHandler();
	protected final LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
	public final ItemStackHandler fluidInputHandler = createFluidInputHandler();
	protected final LazyOptional<IItemHandler> lazyFluidInputHandler = LazyOptional.of(() -> fluidInputHandler);
	
	protected LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
	
	public AbstractCrystallizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, new SimpleFluidContainer(3, 1));
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyFluidHandler = LazyOptional.of(() -> inputFluidTank);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		
		lazyCrystalBaseHandler.invalidate();
		lazyPrimaryIngredientHandler.invalidate();
		lazySecondaryIngredientHandler.invalidate();
		lazyOutputHandler.invalidate();
		lazyFluidInputHandler.invalidate();
		
		lazyFluidHandler.invalidate();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		
		if(nbt.contains(INVENTORY)) // Old inventory getting updated
		{
			ItemStackHandler tempHandler = new ItemStackHandler(5);
			tempHandler.deserializeNBT(nbt.getCompound(INVENTORY));
			
			crystalBaseHandler.insertItem(0, tempHandler.getStackInSlot(0), false);
			primaryIngredientHandler.insertItem(0, tempHandler.getStackInSlot(1), false);
			secondaryIngredientHandler.insertItem(0, tempHandler.getStackInSlot(2), false);
			outputHandler.insertItem(0, tempHandler.getStackInSlot(3), false);
			fluidInputHandler.insertItem(0, tempHandler.getStackInSlot(4), false);
		}
		else
		{
			crystalBaseHandler.deserializeNBT(nbt.getCompound(CRYSTAL_BASE_INVENTORY));
			primaryIngredientHandler.deserializeNBT(nbt.getCompound(PRIMARY_INGREDIENT_INVENTORY));
			secondaryIngredientHandler.deserializeNBT(nbt.getCompound(SECONDARY_INGREDIENT_INVENTORY));
			outputHandler.deserializeNBT(nbt.getCompound(OUTPUT_INVENTORY));
			InventoryUtil.expandSlotsIfNeeded(outputHandler, 2);
			fluidInputHandler.deserializeNBT(nbt.getCompound(FLUID_INPUT_INVENTORY));
		}
		
		inputFluidTank.readFromNBT(nbt);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.put(CRYSTAL_BASE_INVENTORY, crystalBaseHandler.serializeNBT());
		nbt.put(PRIMARY_INGREDIENT_INVENTORY, primaryIngredientHandler.serializeNBT());
		nbt.put(SECONDARY_INGREDIENT_INVENTORY, secondaryIngredientHandler.serializeNBT());
		nbt.put(OUTPUT_INVENTORY, outputHandler.serializeNBT());
		nbt.put(FLUID_INPUT_INVENTORY, fluidInputHandler.serializeNBT());
		
		nbt = inputFluidTank.writeToNBT(nbt);
		
		super.saveAdditional(nbt);
	}
	
	public abstract boolean isDesiredInputFluid(FluidStack fluidStack);
	
	@Override
	protected void updateSimpleContainer()
	{
		this.simpleContainer.setItem(0, crystalBaseHandler.getStackInSlot(0));
		this.simpleContainer.setItem(1, primaryIngredientHandler.getStackInSlot(0));
		this.simpleContainer.setItem(2, secondaryIngredientHandler.getStackInSlot(0));
		this.simpleContainer.setFluid(0, inputFluidTank.getFluid());
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.FLUID_HANDLER)
			return lazyFluidHandler.cast();
		
		else if(capability == ForgeCapabilities.ITEM_HANDLER)
		{
			if(side == Direction.UP)
				return lazyCrystalBaseHandler.cast();
			else if(side == Direction.DOWN)
				return lazyOutputHandler.cast();
			else if(side == getDirection().getClockWise())
				return lazyPrimaryIngredientHandler.cast();
			else if(side == getDirection().getCounterClockWise())
				return lazySecondaryIngredientHandler.cast();
			else
				return lazyFluidInputHandler.cast();
		}
		
		return super.getCapability(capability, side);
	}
	
	public abstract int inputFluidTankCapacity();
	
	public abstract int maxFluidReceive();
	
	private final FluidTank inputFluidTank = new FluidTank(inputFluidTankCapacity())
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
	
	public void setFluid(FluidStack fluidStack)
	{
		this.inputFluidTank.setFluid(fluidStack);
	}
	
	public FluidStack getFluidStack()
	{
		return this.inputFluidTank.getFluid();
	}
	
	private ItemStackHandler createCrystalBaseHandler() // 0
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
	
	private ItemStackHandler createIngredientHandler() // 1 + 2
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
	
	private ItemStackHandler createOutputHandler() // 3
	{
		return new ItemStackHandler(2)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				setChanged();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
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
	
	private ItemStackHandler createFluidInputHandler() // 4
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
				return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
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
	
	@Nullable
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractCrystallizerBlock)
			return gateState.getValue(AbstractCrystallizerBlock.FACING);
		
		return Direction.NORTH;
	}

	public boolean hasInputFluidItem()
	{
    	return fluidInputHandler.getStackInSlot(0).getCount() > 0;
	}
	
	public void fillFluidTank(FluidStack stack, ItemStack container)
	{
		inputFluidTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);

		fluidInputHandler.extractItem(0, 1, false);
		fluidInputHandler.insertItem(0, container, false);
    }
	
	public void dumpEmptyFluidContainers()
	{
		ItemStack container = fluidInputHandler.getStackInSlot(0);
		Optional<IFluidHandlerItem> fluidHandler = container.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
		if(fluidHandler.isPresent())
		{
			if(fluidHandler.get().getFluidInTank(0).isEmpty()) // Try placing the container in the dump
				InventoryUtil.dumpIfPossible(fluidInputHandler, 0, outputHandler, 1);
		}
		else
			InventoryUtil.dumpIfPossible(fluidInputHandler, 0, outputHandler, 1);
	}
	
	public void drainFluidFromInputItem()
	{
		fluidInputHandler.getStackInSlot(0).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
		{
			int drainAmount = Math.min(inputFluidTank.getSpace(), maxFluidReceive());
			FluidStack fluidStack = handler.getFluidInTank(0);
			
			if(inputFluidTank.isFluidValid(fluidStack) && isSameFluidOrEmpty(inputFluidTank.getFluidInTank(0), fluidStack))
			{
				fluidStack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillFluidTank(fluidStack, handler.getContainer());
			}
		});
	}
	
	public void dumpInputFluidTank()
	{
		inputFluidTank.drain(inputFluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
	}
	
	public boolean canOutput(R recipe)
	{
		// Only allows creating Stargate Upgrade Crystals when it's enabled in the config
		if(!CommonStargateConfig.enable_classic_stargate_upgrades.get() && recipe.getResultItem().getItem() instanceof StargateUpgradeItem)
			return false;
		
		return InventoryUtil.canInsertStackInto(simpleContainer.getItem(3), recipe.getResultItem());
	}
	
	@Override
	public void depleteIngredients(R recipe)
	{
		crystalBaseHandler.extractItem(0, recipe.getAmountInSlot(0), false);
		if(recipe.depletePrimary())
			primaryIngredientHandler.extractItem(0, recipe.getAmountInSlot(1), false);
		if(recipe.depleteSecondary())
			secondaryIngredientHandler.extractItem(0, recipe.getAmountInSlot(2), false);
		inputFluidTank.drain(recipe.getInputFluid(), IFluidHandler.FluidAction.EXECUTE);
	}
	
	@Override
	public void createOutput(R recipe)
	{
		ItemStack outputStack = outputHandler.getStackInSlot(0);
		
		if(outputStack.isEmpty())
			outputHandler.setStackInSlot(0, recipe.getResultItem());
		else if(recipe.getResultItem().is(outputStack.getItem()))
			outputStack.grow(1);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractCrystallizerEntity<?> crystallizer)
	{
		if(!level.isClientSide())
		{
			if(crystallizer.hasInputFluidItem())
				crystallizer.drainFluidFromInputItem();
			crystallizer.dumpEmptyFluidContainers();
		}
		
		ProgressRecipeEnergyBlockEntity.tick(level, pos, state, crystallizer);
		
		if(!level.isClientSide())
	    	crystallizer.updateClient();
	}
}
