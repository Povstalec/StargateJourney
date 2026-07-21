package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.blocks.tech.AbstractCrystallizerBlock;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.items.StargateUpgradeItem;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipeInput;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractCrystallizerEntity<R extends CrystallizingRecipe> extends ProgressRecipeEnergyBlockEntity<R, CrystallizingRecipeInput>
{
	private static final String CRYSTAL_BASE_INVENTORY = "crystal_base_inventory";
	private static final String PRIMARY_INGREDIENT_INVENTORY = "primary_ingredient_inventory";
	private static final String SECONDARY_INGREDIENT_INVENTORY = "secondary_ingredient_inventory";
	private static final String OUTPUT_INVENTORY = "output_inventory";
	private static final String FLUID_INPUT_INVENTORY = "fluid_input_inventory";
	
	public final ItemStackHandler crystalBaseHandler = createCrystalBaseHandler();
	protected final Lazy<IItemHandler> lazyCrystalBaseHandler = Lazy.of(() -> crystalBaseHandler);
	public final ItemStackHandler primaryIngredientHandler = createIngredientHandler();
	protected final Lazy<IItemHandler> lazyPrimaryIngredientHandler = Lazy.of(() -> primaryIngredientHandler);
	public final ItemStackHandler secondaryIngredientHandler = createIngredientHandler();
	protected final Lazy<IItemHandler> lazySecondaryIngredientHandler = Lazy.of(() -> secondaryIngredientHandler);
	public final ItemStackHandler outputHandler = createOutputHandler();
	protected final Lazy<IItemHandler> lazyOutputHandler = Lazy.of(() -> outputHandler);
	public final ItemStackHandler fluidInputHandler = createFluidInputHandler();
	protected final Lazy<IItemHandler> lazyFluidInputHandler = Lazy.of(() -> fluidInputHandler);
	
	public final FluidTank inputFluidTank = createFluidTank();
	protected Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> inputFluidTank);
	
	public AbstractCrystallizerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, new CrystallizingRecipeInput());
	}
	
	@Override
	public void invalidateCapabilities()
	{
		super.invalidateCapabilities();
		
		lazyCrystalBaseHandler.invalidate();
		lazyPrimaryIngredientHandler.invalidate();
		lazySecondaryIngredientHandler.invalidate();
		lazyOutputHandler.invalidate();
		lazyFluidInputHandler.invalidate();
		
		lazyFluidHandler.invalidate();
	}
	
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.loadAdditional(nbt, registries);
		
		crystalBaseHandler.deserializeNBT(registries, nbt.getCompound(CRYSTAL_BASE_INVENTORY));
		primaryIngredientHandler.deserializeNBT(registries, nbt.getCompound(PRIMARY_INGREDIENT_INVENTORY));
		secondaryIngredientHandler.deserializeNBT(registries, nbt.getCompound(SECONDARY_INGREDIENT_INVENTORY));
		outputHandler.deserializeNBT(registries, nbt.getCompound(OUTPUT_INVENTORY));
		fluidInputHandler.deserializeNBT(registries, nbt.getCompound(FLUID_INPUT_INVENTORY));
		
		inputFluidTank.readFromNBT(registries, nbt);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries)
	{
		nbt.put(CRYSTAL_BASE_INVENTORY, crystalBaseHandler.serializeNBT(registries));
		nbt.put(PRIMARY_INGREDIENT_INVENTORY, primaryIngredientHandler.serializeNBT(registries));
		nbt.put(SECONDARY_INGREDIENT_INVENTORY, secondaryIngredientHandler.serializeNBT(registries));
		nbt.put(OUTPUT_INVENTORY, outputHandler.serializeNBT(registries));
		nbt.put(FLUID_INPUT_INVENTORY, fluidInputHandler.serializeNBT(registries));
		
		nbt = inputFluidTank.writeToNBT(registries, nbt);
		
		super.saveAdditional(nbt, registries);
	}
	
	public abstract boolean isDesiredInputFluid(FluidStack fluidStack);
	
	@Override
	protected void updateSimpleContainer()
	{
		this.recipeInput.setItem(0, crystalBaseHandler.getStackInSlot(0));
		this.recipeInput.setItem(1, primaryIngredientHandler.getStackInSlot(0));
		this.recipeInput.setItem(2, secondaryIngredientHandler.getStackInSlot(0));
		this.recipeInput.setFluid(0, inputFluidTank.getFluid());
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IFluidHandler getFluidHandler(Direction side)
	{
		return lazyFluidHandler.get();
	}
	
	public abstract int inputFluidTankCapacity();
	
	public abstract int maxFluidReceive();
	
	public IItemHandler getItemHandler(Direction side)
	{
		if(side == Direction.UP)
			return lazyCrystalBaseHandler.get();
		else if(side == Direction.DOWN)
			return lazyOutputHandler.get();
		else if(side == getDirection().getClockWise())
			return lazyPrimaryIngredientHandler.get();
		else if(side == getDirection().getCounterClockWise())
			return lazySecondaryIngredientHandler.get();
		else
			return lazyFluidInputHandler.get();
	}
	
	private FluidTank createFluidTank()
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
				return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
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
				return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
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
		IFluidHandlerItem fluidHandler = container.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
		{
			if(fluidHandler.getFluidInTank(0).isEmpty()) // Try placing the container in the dump
				InventoryUtil.dumpIfPossible(fluidInputHandler, 0, outputHandler, 1);
		}
		else
			InventoryUtil.dumpIfPossible(fluidInputHandler, 0, outputHandler, 1);
	}
	
	public void drainFluidFromInputItem()
	{
		IFluidHandlerItem cap = fluidInputHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
		{
			int drainAmount = Math.min(inputFluidTank.getSpace(), maxFluidReceive());
			FluidStack fluidStack = cap.getFluidInTank(0);
			
			if(inputFluidTank.isFluidValid(fluidStack) && isSameFluidOrEmpty(inputFluidTank.getFluidInTank(0), fluidStack))
			{
				fluidStack = cap.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillFluidTank(fluidStack, cap.getContainer());
			}
		}
	}
	
	public void dumpInputFluidTank()
	{
		inputFluidTank.drain(inputFluidTank.getCapacity(), IFluidHandler.FluidAction.EXECUTE);
	}
	
	public boolean canOutput(R recipe)
	{
		// Only allows creating Stargate Upgrade Crystals when it's enabled in the config
		if(!CommonStargateConfig.enable_classic_stargate_upgrades.get() && recipe.getResultItem(level.registryAccess()).getItem() instanceof StargateUpgradeItem)
			return false;
		
		return InventoryUtil.canInsertStackInto(outputHandler.getStackInSlot(0), recipe.getResultItem(level.registryAccess()));
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
			outputHandler.setStackInSlot(0, recipe.getResultItem(level.registryAccess()));
		else if(recipe.getResultItem(level.registryAccess()).is(outputStack.getItem()))
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
