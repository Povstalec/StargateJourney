package net.povstalec.sgjourney.common.block_entities.dhd;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.TransferCrystalItem;
import net.povstalec.sgjourney.common.misc.ArrayHelper;

public class MilkyWayDHDEntity extends AbstractDHDEntity
{
	protected int[] memoryCrystals = new int[0];
	protected int[] controlCrystals = new int[0];
	protected int[] energyCrystals = new int[0];
	protected int[] transferCrystals = new int[0];
	protected int[] communicationCrystals = new int[0];
	
	protected final ItemStackHandler itemHandler = createHandler();
	protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	public MilkyWayDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.MILKY_WAY_DHD.get(), pos, state);
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
		addTransferCrystals(itemHandler);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		nbt.put("Inventory", itemHandler.serializeNBT());
		super.saveAdditional(nbt);
	}
	
	@Override
	public void onLoad()
	{
		if(!this.getLevel().isClientSide())
			this.recalculateCrystals();
		
		super.onLoad();
	}
	
	@Override
	public void invalidateCaps()
	{
		handler.invalidate();
		super.invalidateCaps();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER)
		{
			return handler.cast();
		}
		
		return super.getCapability(capability, side);
	}
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(9)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
					recalculateCrystals();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					if(slot == 0)
						return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isLarge();
					else
						return isValidCrystal(stack);
				}
				
				// Limits the number of items per slot
				public int getSlotLimit(int slot)
				{
					return 1;
				}
				
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					if(!isItemValid(slot, stack))
					{
						return stack;
					}
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
	}
	
	protected boolean isValidCrystal(ItemStack stack)
	{
		return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isRegular();
	}
	
	public void recalculateCrystals()
	{
		// Check if the DHD has a Control Crystal
		this.enableAdvancedProtocols = !itemHandler.getStackInSlot(0).isEmpty();
		this.memoryCrystals = new int[0];
		this.controlCrystals = new int[0];
		this.energyCrystals = new int[0];
		this.transferCrystals = new int[0];
		this.energyTarget = 0;
		this.maxEnergyTransfer = 0;
		this.communicationCrystals = new int[0];
		
		// Check where the Crystals are and save their positions
		for(int i = 1; i < 9; i++)
		{
			Item item = itemHandler.getStackInSlot(i).getItem();
			
			if(item == ItemInit.CONTROL_CRYSTAL.get())
				this.controlCrystals = ArrayHelper.growIntArray(this.controlCrystals, i);
			else if(item == ItemInit.MEMORY_CRYSTAL.get())
				this.memoryCrystals = ArrayHelper.growIntArray(this.memoryCrystals, i);
			else if(item == ItemInit.ENERGY_CRYSTAL.get())
				this.energyCrystals = ArrayHelper.growIntArray(this.energyCrystals, i);
			else if(item == ItemInit.TRANSFER_CRYSTAL.get())
				this.transferCrystals = ArrayHelper.growIntArray(this.transferCrystals, i);
			else if(item == ItemInit.COMMUNICATION_CRYSTAL.get())
				this.communicationCrystals = ArrayHelper.growIntArray(this.communicationCrystals, i);
		}
		
		// Set up Energy Crystals
		for(int i = 0; i < this.energyCrystals.length; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(energyCrystals[i]);
			
			if(!stack.isEmpty())
				this.energyTarget += ItemInit.ENERGY_CRYSTAL.get().getCapacity();
		}
		
		// Set up Transfer Crystals
		for(int i = 0; i < this.transferCrystals.length; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(transferCrystals[i]);
			
			if(!stack.isEmpty())
			{
				this.maxEnergyTransfer += TransferCrystalItem.getMaxTransfer(stack);
			}
		}
		
		setStargate();
	}
	
	@Override
	public int getMaxDistance()
	{
		return this.communicationCrystals.length * ItemInit.COMMUNICATION_CRYSTAL.get().getMaxDistance() + DEFAULT_CONNECTION_DISTANCE;
	}

	@Override
	protected SoundEvent getEnterSound()
	{
		return SoundInit.MILKY_WAY_DHD_ENTER.get();
	}

	@Override
	protected SoundEvent getPressSound()
	{
		return SoundInit.MILKY_WAY_DHD_PRESS.get();
	}
	
	
	

	// TODO Temporary function for replacing old Energy Crystals with new Transfer Crystals
	public static void addTransferCrystals(ItemStackHandler itemHandler)
	{
		int slots = itemHandler.getSlots();
		
		for(int i = 0; i < slots; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(i);
			
			if(stack.is(ItemInit.ENERGY_CRYSTAL.get()) && stack.hasTag())
			{
				if(stack.getTag().getString(CRYSTAL_MODE).equals(ENERGY_TRANSFER))
				{
					itemHandler.setStackInSlot(i, new ItemStack(ItemInit.TRANSFER_CRYSTAL.get()));
					StargateJourney.LOGGER.info("Replaced Transfer Crystal");
				}
			}
		}
	}
}
