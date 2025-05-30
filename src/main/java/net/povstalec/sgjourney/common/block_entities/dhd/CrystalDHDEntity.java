package net.povstalec.sgjourney.common.block_entities.dhd;

import javax.annotation.Nonnull;

import net.povstalec.sgjourney.common.handlers.ProtectedItemStackHandler;
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
	public static final String CRYSTAL_INVENTORY = "Inventory"; // TODO Rename this to "crystal_inventory" in the future
	
	protected AbstractCrystalItem.Storage memoryCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage controlCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage energyCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage transferCrystals = new AbstractCrystalItem.Storage();
	protected AbstractCrystalItem.Storage communicationCrystals = new AbstractCrystalItem.Storage();
	
	protected final ItemStackHandler itemHandler;
	protected final ProtectedItemStackHandler protectedItemHandler;
	private final LazyOptional<IItemHandler> lazyItemHandler;
	private final LazyOptional<IItemHandler> lazyProtectedItemHandler;

	public CrystalDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);

		protectedItemHandler = new ProtectedItemStackHandler(9, this::isProtected);
		itemHandler = protectedItemHandler.unprotect()
						.set_onContentsChanged((slots)->{
					this.setChanged();
					this.recalculateCrystals();
				})
					.set_isItemValid((slot, stack)->
							isValidCrystal(slot, stack) || stack.getItem() instanceof CallForwardingDevice)
					.set_getSlotLimit((slot)->1);
		lazyProtectedItemHandler = LazyOptional.of(()->protectedItemHandler);
		lazyItemHandler = LazyOptional.of(() -> itemHandler);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		protectedItemHandler.deserializeNBT(tag.getCompound(CRYSTAL_INVENTORY));
		
		//TODO Remove this later
		if(!tag.contains(ENERGY_INVENTORY) && tag.contains(CRYSTAL_INVENTORY))
			generateEnergyCore();
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.put(CRYSTAL_INVENTORY, protectedItemHandler.serializeNBT());
		super.saveAdditional(tag);
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
		lazyItemHandler.invalidate();
		lazyProtectedItemHandler.invalidate();
		super.invalidateCaps();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER) {
			if (side != null) // automation passes a side 99.9% of times
				return lazyProtectedItemHandler.cast();
			else // User trying to access by hand
				return lazyItemHandler.cast();
		}
		
		return super.getCapability(capability, side);
	}
	
	protected boolean isValidCrystal(int slot, ItemStack stack)
	{
		if(slot == 0)
			return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isLarge();
		
		return stack.getItem() instanceof AbstractCrystalItem || stack.getItem() instanceof CallForwardingDevice;
	}
	
	public void recalculateCrystals()
	{
		// Check if the DHD has a Control Crystal
		this.enableCallForwarding = false;
		this.enableAdvancedProtocols = !protectedItemHandler.unprotect().getStackInSlot(0).isEmpty();
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
				controlCrystals.addCrystal(controlCrystal.isAdvanced(), i);
			
			else if(item instanceof MemoryCrystalItem memoryCrystal)
				memoryCrystals.addCrystal(memoryCrystal.isAdvanced(), i);
			
			else if(item instanceof EnergyCrystalItem energyCrystal)
			{
				energyCrystals.addCrystal(energyCrystal.isAdvanced(), i);
				if(energyCrystals.getCrystals().length >= 4 || energyCrystals.getAdvancedCrystals().length >= 3)
					energyTarget = -1;
				else if(energyTarget >= 0)
					energyTarget += energyCrystal.getCapacity();
			}
			
			else if(item instanceof TransferCrystalItem transferCrystal)
			{
				transferCrystals.addCrystal(transferCrystal.isAdvanced(), i);
				if(transferCrystals.getCrystals().length >= 4 || transferCrystals.getAdvancedCrystals().length >= 3)
					maxEnergyTransfer = -1;
				else if(maxEnergyTransfer >= 0)
					maxEnergyTransfer += transferCrystal.getMaxTransfer();
			}
			
			else if(item instanceof CommunicationCrystalItem communicationCrystal)
				communicationCrystals.addCrystal(communicationCrystal.isAdvanced(), i);
			
			else if(item instanceof CallForwardingDevice)
				enableCallForwarding = true;
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
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	@Override
	public void generate()
	{
		generateCrystals();
		
		super.generate();
	}
	
	protected abstract void generateCrystals();
}
