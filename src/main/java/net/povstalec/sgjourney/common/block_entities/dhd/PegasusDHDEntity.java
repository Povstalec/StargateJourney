package net.povstalec.sgjourney.common.block_entities.dhd;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.TransferCrystalItem;
import net.povstalec.sgjourney.common.misc.ArrayHelper;

public class PegasusDHDEntity extends AbstractDHDEntity
{
	protected int[] memoryCrystals = new int[0];
	protected int[] controlCrystals = new int[0];
	protected int[] energyCrystals = new int[0];
	protected int[] transferCrystals = new int[0];
	protected int[] communicationCrystals = new int[0];
	
	protected final ItemStackHandler itemHandler = createHandler();
	protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	public PegasusDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.PEGASUS_DHD.get(), pos, state);
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
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
		this.recalculateCrystals();
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		handler.invalidate();
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
		return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isAdvanced();
	}
	
	public void recalculateCrystals()
	{
		this.enableAdvancedProtocols = false;
		this.memoryCrystals = new int[0];
		this.controlCrystals = new int[0];
		this.energyCrystals = new int[0];
		this.transferCrystals = new int[0];
		this.desiredEnergyLevel = 0;
		this.maxEnergyTransfer = 0;
		this.communicationCrystals = new int[0];
		
		// Check where the Crystals are and save their positions
		for(int i = 0; i < 9; i++)
		{
			Item item = itemHandler.getStackInSlot(i).getItem();

			
			if(item == ItemInit.ADVANCED_CONTROL_CRYSTAL.get())
				this.controlCrystals = ArrayHelper.growIntArray(this.controlCrystals, i);
			else if(item == ItemInit.ADVANCED_MEMORY_CRYSTAL.get())
				this.memoryCrystals = ArrayHelper.growIntArray(this.memoryCrystals, i);
			else if(item == ItemInit.ADVANCED_ENERGY_CRYSTAL.get())
				this.energyCrystals = ArrayHelper.growIntArray(this.energyCrystals, i);
			else if(item == ItemInit.ADVANCED_TRANSFER_CRYSTAL.get())
				this.transferCrystals = ArrayHelper.growIntArray(this.transferCrystals, i);
			else if(item == ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get())
				this.communicationCrystals = ArrayHelper.growIntArray(this.communicationCrystals, i);
		}
		
		if(this.controlCrystals.length > 0)
			this.enableAdvancedProtocols = true;
		
		// Set up Energy Crystals
		for(int i = 0; i < this.energyCrystals.length; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(energyCrystals[i]);
			
			if(!stack.isEmpty())
				this.desiredEnergyLevel += ItemInit.ENERGY_CRYSTAL.get().getCapacity();
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
	}
	
	@Override
	public int getMaxDistance()
	{
		return this.communicationCrystals.length * ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get().getMaxDistance() + 16;
	}

}
