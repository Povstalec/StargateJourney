package net.povstalec.sgjourney.common.block_entities;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundCrystallizerUpdatePacket;
import net.povstalec.sgjourney.common.recipe.CrystallizerRecipe;

public class CrystallizerEntity extends EnergyBlockEntity
{
	private static final ResourceLocation UNIVERSE_UPGRADE_CRYSTAL = new ResourceLocation(StargateJourney.MODID, "universe_upgrade_crystal");
	private static final ResourceLocation MILKY_WAY_UPGRADE_CRYSTAL = new ResourceLocation(StargateJourney.MODID, "milky_way_upgrade_crystal");
	private static final ResourceLocation PEGASUS_UPGRADE_CRYSTAL = new ResourceLocation(StargateJourney.MODID, "pegasus_upgrade_crystal");
	private static final ResourceLocation TOLLAN_UPGRADE_CRYSTAL = new ResourceLocation(StargateJourney.MODID, "tollan_upgrade_crystal");
	
    protected static final int MAX_PROGRESS = 200;
    
	protected final ItemStackHandler itemHandler = createHandler();
	protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	protected LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
	
	protected int progress = 0;
	
	public CrystallizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CRYSTALLIZER.get(), pos, state);
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
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.put("Inventory", itemHandler.serializeNBT());
		nbt = fluidTank.writeToNBT(nbt);
		super.saveAdditional(nbt);
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
			return handler.cast();
		
		return super.getCapability(capability, side);
	}
	
	private final FluidTank fluidTank = new FluidTank(64000)
	{
		@Override
		protected void onContentsChanged()
		{
			setChanged();
	    }
		
		@Override
	    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
	    {
			return stack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get();
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
		return new ItemStackHandler(6)
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
						case 5 -> 1;
						default -> 64;
					};
			    }
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return switch(slot)
					{
						//case 0 -> stack.getItem() == Items.DIAMOND;
						case 3 -> false;
						case 4 -> stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
						case 5 -> false;
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
		itemHandler.getStackInSlot(4).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> {
			int drainAmount = Math.min(fluidTank.getSpace(), 1000);
			
			FluidStack stack = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
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
	
	protected boolean hasIngredients()
	{
		Level level = this.getLevel();
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		
		for(int i = 0; i < itemHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		
		Optional<CrystallizerRecipe> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.INSTANCE, inventory, level);
		
		if(!recipe.isPresent())
			return false;
		
		// Only allows creating Stargate Upgrade Crystals when it's enabled in the config
		if(!CommonStargateConfig.enable_classic_stargate_upgrades.get())
		{
			ResourceLocation recipeID = recipe.get().getId();
			
			if(		recipeID.equals(UNIVERSE_UPGRADE_CRYSTAL) ||
					recipeID.equals(MILKY_WAY_UPGRADE_CRYSTAL) ||
					recipeID.equals(PEGASUS_UPGRADE_CRYSTAL) ||
					recipeID.equals(TOLLAN_UPGRADE_CRYSTAL))
				return false;
		}
		
		return hasSpaceInOutputSlot(inventory, recipe.get().getResultItem());
	}
	
	protected void crystallize()
	{
		Level level = this.getLevel();
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		
		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		
		Optional<CrystallizerRecipe> recipe = level.getRecipeManager()
				.getRecipeFor(CrystallizerRecipe.Type.INSTANCE, inventory, level);
		
		if(hasIngredients())
		{
			useUpItems(recipe.get(), 0);
			useUpItems(recipe.get(), 1);
			useUpItems(recipe.get(), 2);
			itemHandler.setStackInSlot(3, new ItemStack(recipe.get().getResultItem().getItem(), 1));
			
			this.progress = 0;
		}
	}
	
	protected void useUpItems(CrystallizerRecipe recipe, int slot)
	{
		itemHandler.extractItem(slot, recipe.getAmountInSlot(slot), false);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, CrystallizerEntity crystallizer)
	{
		if(level.isClientSide())
			return;

	    if(crystallizer.hasFluidItem())
	    	crystallizer.drainFluidFromItem();
	    
	    if(crystallizer.hasIngredients())
	    {
	    	crystallizer.progress++;
	    	setChanged(level, pos, state);
	    	
	    	if(crystallizer.progress >= MAX_PROGRESS)
	    		crystallizer.crystallize();
	    }
	    else
	    {
	    	crystallizer.progress = 0;
	    	setChanged(level, pos, state);
	    }
	    
	    PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(crystallizer.worldPosition)), new ClientboundCrystallizerUpdatePacket(crystallizer.worldPosition, crystallizer.getFluid().getAmount()));
	}
	
}
