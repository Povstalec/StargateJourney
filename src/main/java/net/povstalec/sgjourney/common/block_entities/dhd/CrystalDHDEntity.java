package net.povstalec.sgjourney.common.block_entities.dhd;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.CallForwardingDevice;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.TransferCrystalItem;

public abstract class CrystalDHDEntity extends AbstractDHDEntity
{
	protected AbstractCrystalItem.Storage memoryCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage controlCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage energyCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage transferCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage communicationCrystals = new AbstractCrystalItem.Storage();
	
	protected final ItemStackHandler itemHandler = createHandler();
	protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	public CrystalDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
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
		if(!this.getLevel().isClientSide())
			this.recalculateCrystals();
		
		super.onLoad();
	}
	
	@Override
	public void invalidateCapabilities()
	{
		handler.invalidate();
		super.invalidateCapabilities();
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
	
	protected ItemStackHandler createHandler()
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
					return isValidCrystal(stack) || stack.getItem() instanceof CallForwardingDevice;
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
		return stack.getItem() instanceof AbstractCrystalItem;
	}
	
	public void recalculateCrystals()
	{
		// Check if the DHD has a Control Crystal
		this.enableCallForwarding = false;
		this.enableAdvancedProtocols = !itemHandler.getStackInSlot(0).isEmpty();
		this.memoryCrystals.reset();
		this.controlCrystals.reset();
		this.energyCrystals.reset();
		this.transferCrystals.reset();
		this.energyTarget = 0;
		this.maxEnergyTransfer = 0;
		this.communicationCrystals.reset();
		
		// Check where the Crystals are and save their positions
		for(int i = 1; i < 9; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(i);
			Item item = stack.getItem();
			
			if(item instanceof ControlCrystalItem controlCrystal)
				this.controlCrystals.addCrystal(controlCrystal.isAdvanced(), i);
			
			else if(item instanceof MemoryCrystalItem memoryCrystal)
				this.memoryCrystals.addCrystal(memoryCrystal.isAdvanced(), i);
			
			else if(item instanceof EnergyCrystalItem energyCrystal)
			{
				this.energyCrystals.addCrystal(energyCrystal.isAdvanced(), i);
				this.energyTarget += energyCrystal.getCapacity();
			}
			
			else if(item instanceof TransferCrystalItem transferCrystal)
			{
				this.transferCrystals.addCrystal(transferCrystal.isAdvanced(), i);
				this.maxEnergyTransfer += transferCrystal.getMaxTransfer();
			}
			
			else if(item instanceof CommunicationCrystalItem communicationCrystal)
				this.communicationCrystals.addCrystal(communicationCrystal.isAdvanced(), i);
			
			else if(item instanceof CallForwardingDevice)
				this.enableCallForwarding = true;
		}
		
		setStargate();
	}
	
	@Override
	public int getMaxDistance()
	{
		int regularDistance = this.communicationCrystals.getCrystals().length * ItemInit.COMMUNICATION_CRYSTAL.get().getMaxDistance();
		int advancedDistance = this.communicationCrystals.getAdvancedCrystals().length * ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get().getMaxDistance();
		
		return DEFAULT_CONNECTION_DISTANCE + regularDistance + advancedDistance;
	}
}
