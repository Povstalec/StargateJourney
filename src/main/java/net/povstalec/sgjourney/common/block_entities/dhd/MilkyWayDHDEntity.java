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
import net.povstalec.sgjourney.common.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.common.misc.ArrayHelper;

public class MilkyWayDHDEntity extends AbstractDHDEntity
{
	protected int[] memoryCrystals = new int[0];
	protected int[] controlCrystals = new int[0];
	protected int[] energyCrystals = new int[0];
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
						return stack.getItem() == ItemInit.LARGE_CONTROL_CRYSTAL.get();
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
		if(stack.getItem() == ItemInit.MEMORY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.ENERGY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.COMMUNICATION_CRYSTAL.get())
			return true;
		
		return false;
	}
	
	public void recalculateCrystals()
	{
		// Check if the DHD has a Control Crystal
		this.enableAdvancedProtocols = !itemHandler.getStackInSlot(0).isEmpty();
		this.memoryCrystals = new int[0];
		this.controlCrystals = new int[0];
		this.energyCrystals = new int[0];
		this.desiredEnergyLevel = 0;
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
			else if(item == ItemInit.COMMUNICATION_CRYSTAL.get())
				this.communicationCrystals = ArrayHelper.growIntArray(this.communicationCrystals, i);
		}
		
		// Set up Energy Crystals
		for(int i = 0; i < this.energyCrystals.length; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(energyCrystals[i]);
			
			if(!stack.isEmpty())
			{
				EnergyCrystalItem.CrystalMode mode = EnergyCrystalItem.getCrystalMode(stack);
				
				switch(mode)
				{
				case ENERGY_STORAGE:
					this.desiredEnergyLevel += ItemInit.ENERGY_CRYSTAL.get().getMaxStorage();
					break;
				case ENERGY_TRANSFER:
					this.maxEnergyTransfer += ItemInit.ENERGY_CRYSTAL.get().getMaxTransfer();
					break;
				}
			}
		}
	}
	
	@Override
	public int getMaxDistance()
	{
		return this.communicationCrystals.length * ItemInit.COMMUNICATION_CRYSTAL.get().getMaxDistance() + 16;
	}
}
