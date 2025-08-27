package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import net.povstalec.sgjourney.common.packets.ClientboundCrystallizerUpdatePacket;

public abstract class AbstractCrystallizerEntity extends EnergyBlockEntity
{
	private static final String PROGRESS = "progress";
	private static final String CRYSTAL_BASE_INVENTORY = "crystal_base_inventory";
	private static final String PRIMARY_INGREDIENT_INVENTORY = "primary_ingredient_inventory";
	private static final String SECONDARY_INGREDIENT_INVENTORY = "secondary_ingredient_inventory";
	private static final String OUTPUT_INVENTORY = "output_inventory";
	private static final String FLUID_INPUT_INVENTORY = "fluid_input_inventory";
	
	public static final int LIQUID_NAQUADAH_CAPACITY = 4000;
	public static final int MAX_PROGRESS = 200;
 
	protected final ItemStackHandler crystalBaseHandler = createCrystalBaseHandler();
	protected final Lazy<IItemHandler> lazyCrystalBaseHandler = Lazy.of(() -> crystalBaseHandler);
	protected final ItemStackHandler primaryIngredientHandler = createIngredientHandler();
	protected final Lazy<IItemHandler> lazyPrimaryIngredientHandler = Lazy.of(() -> primaryIngredientHandler);
	protected final ItemStackHandler secondaryIngredientHandler = createIngredientHandler();
	protected final Lazy<IItemHandler> lazySecondaryIngredientHandler = Lazy.of(() -> secondaryIngredientHandler);
	protected final ItemStackHandler outputHandler = createOutputHandler();
	protected final Lazy<IItemHandler> lazyOutputHandler = Lazy.of(() -> outputHandler);
	protected final ItemStackHandler fluidInputHandler = createFluidInputHandler();
	protected final Lazy<IItemHandler> lazyFluidInputHandler = Lazy.of(() -> fluidInputHandler);
	
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
		
		fluidTank.readFromNBT(registries, nbt);
		
		progress = nbt.getInt(PROGRESS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries)
	{
		nbt.put(CRYSTAL_BASE_INVENTORY, crystalBaseHandler.serializeNBT(registries));
		nbt.put(PRIMARY_INGREDIENT_INVENTORY, primaryIngredientHandler.serializeNBT(registries));
		nbt.put(SECONDARY_INGREDIENT_INVENTORY, secondaryIngredientHandler.serializeNBT(registries));
		nbt.put(OUTPUT_INVENTORY, outputHandler.serializeNBT(registries));
		nbt.put(FLUID_INPUT_INVENTORY, fluidInputHandler.serializeNBT(registries));
		
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
		IFluidHandlerItem cap = fluidInputHandler.getStackInSlot(0).getCapability(Capabilities.FluidHandler.ITEM);
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
	
	public Lazy<IItemHandler> getItemHandler(int slot)
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
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(crystallizer.worldPosition).getPos(), new ClientboundCrystallizerUpdatePacket(crystallizer.worldPosition, crystallizer.getFluid(), crystallizer.progress));
	}
	
}
