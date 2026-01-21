package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.blocks.tech.AbstractCrystallizerBlock;
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

public abstract class AbstractCrystallizerEntity extends EnergyBlockEntity
{
	private static final String INVENTORY = "Inventory"; //TODO For legacy reasons
	
	private static final String PROGRESS = "progress";
	private static final String CRYSTAL_BASE_INVENTORY = "crystal_base_inventory";
	private static final String PRIMARY_INGREDIENT_INVENTORY = "primary_ingredient_inventory";
	private static final String SECONDARY_INGREDIENT_INVENTORY = "secondary_ingredient_inventory";
	private static final String OUTPUT_INVENTORY = "output_inventory";
	private static final String FLUID_INPUT_INVENTORY = "fluid_input_inventory";
	
	public static final int LIQUID_NAQUADAH_CAPACITY = 4000;
	public static final int MAX_PROGRESS = 200;
    
	protected final ItemStackHandler crystalBaseHandler = createCrystalBaseHandler();
	protected final LazyOptional<IItemHandler> lazyCrystalBaseHandler = LazyOptional.of(() -> crystalBaseHandler);
	protected final ItemStackHandler primaryIngredientHandler = createIngredientHandler();
	protected final LazyOptional<IItemHandler> lazyPrimaryIngredientHandler = LazyOptional.of(() -> primaryIngredientHandler);
	protected final ItemStackHandler secondaryIngredientHandler = createIngredientHandler();
	protected final LazyOptional<IItemHandler> lazySecondaryIngredientHandler = LazyOptional.of(() -> secondaryIngredientHandler);
	protected final ItemStackHandler outputHandler = createOutputHandler();
	protected final LazyOptional<IItemHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
	protected final ItemStackHandler fluidInputHandler = createFluidInputHandler();
	protected final LazyOptional<IItemHandler> lazyFluidInputHandler = LazyOptional.of(() -> fluidInputHandler);
	
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
		
		if(nbt.contains(INVENTORY))
		{
			ItemStackHandler itemHandler = new ItemStackHandler(5);
			itemHandler.deserializeNBT(nbt.getCompound(INVENTORY));
			
			crystalBaseHandler.insertItem(0, itemHandler.getStackInSlot(0), false);
			primaryIngredientHandler.insertItem(0, itemHandler.getStackInSlot(1), false);
			secondaryIngredientHandler.insertItem(0, itemHandler.getStackInSlot(2), false);
			outputHandler.insertItem(0, itemHandler.getStackInSlot(3), false);
			fluidInputHandler.insertItem(0, itemHandler.getStackInSlot(4), false);
		}
		else
		{
			crystalBaseHandler.deserializeNBT(nbt.getCompound(CRYSTAL_BASE_INVENTORY));
			primaryIngredientHandler.deserializeNBT(nbt.getCompound(PRIMARY_INGREDIENT_INVENTORY));
			secondaryIngredientHandler.deserializeNBT(nbt.getCompound(SECONDARY_INGREDIENT_INVENTORY));
			outputHandler.deserializeNBT(nbt.getCompound(OUTPUT_INVENTORY));
			fluidInputHandler.deserializeNBT(nbt.getCompound(FLUID_INPUT_INVENTORY));
		}
		
		fluidTank.readFromNBT(nbt);
		
		progress = nbt.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.put(CRYSTAL_BASE_INVENTORY, crystalBaseHandler.serializeNBT());
		nbt.put(PRIMARY_INGREDIENT_INVENTORY, primaryIngredientHandler.serializeNBT());
		nbt.put(SECONDARY_INGREDIENT_INVENTORY, secondaryIngredientHandler.serializeNBT());
		nbt.put(OUTPUT_INVENTORY, outputHandler.serializeNBT());
		nbt.put(FLUID_INPUT_INVENTORY, fluidInputHandler.serializeNBT());
		
		nbt = fluidTank.writeToNBT(nbt);
		
		nbt.putInt(PROGRESS, progress);
		super.saveAdditional(nbt);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
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
	
	private ItemStackHandler createCrystalBaseHandler() // 0
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
	
	private ItemStackHandler createIngredientHandler() // 1 + 2
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
	
	private ItemStackHandler createOutputHandler() // 3
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
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return false;
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

	public boolean hasFluidItem()
	{
    	return fluidInputHandler.getStackInSlot(0).getCount() > 0;
	}
	
	public void fillFluidTank(FluidStack stack, ItemStack container)
	{
		fluidTank.fill(stack, IFluidHandler.FluidAction.EXECUTE);

        fluidInputHandler.extractItem(0, 1, false);
		fluidInputHandler.insertItem(0, container, false);
    }
	
	public void drainFluidFromItem()
	{
		fluidInputHandler.getStackInSlot(0).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler ->
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
	
	public LazyOptional<IItemHandler> getItemHandler(int slot)
	{
		return switch(slot)
		{
			case 1 -> lazyPrimaryIngredientHandler;
			case 2 -> lazySecondaryIngredientHandler;
			case 3 -> lazyOutputHandler;
			case 4 -> lazyFluidInputHandler;
			default -> lazyCrystalBaseHandler;
		};
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
	    
	    crystallizer.updateClient();
	}
	
}
