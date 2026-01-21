package net.povstalec.sgjourney.common.block_entities.dhd;

import javax.annotation.Nonnull;

import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
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
	
	protected final ItemStackHandler itemHandler = createHandler();
	protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
	
	public CrystalDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		itemHandler.deserializeNBT(tag.getCompound(CRYSTAL_INVENTORY));
		
		//TODO Remove this later
		if(!tag.contains(ENERGY_INVENTORY) && tag.contains(CRYSTAL_INVENTORY))
			generateEnergyCore();
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.put(CRYSTAL_INVENTORY, itemHandler.serializeNBT());
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
		handler.invalidate();
		super.invalidateCaps();
	}
	
	public LazyOptional<IItemHandler> getItemHandler()
	{
		return handler.cast();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER && (!isProtected() || CommonPermissionConfig.protected_inventory_access.get()))
			return lazyEnergyItemHandler.cast();
		
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
					return isValidCrystal(slot, stack) || stack.getItem() instanceof CallForwardingDevice;
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
				controlCrystals.addCrystal(controlCrystal.isAdvanced(), i);
			
			else if(item instanceof MemoryCrystalItem memoryCrystal)
				memoryCrystals.addCrystal(memoryCrystal.isAdvanced(), i);
			
			else if(item instanceof EnergyCrystalItem energyCrystal)
			{
				energyCrystals.addCrystal(energyCrystal.isAdvanced(), i);
				if(energyCrystals.getCrystals().length >= 4 || energyCrystals.getAdvancedCrystals().length >= 3)
					energyTarget = -1;
				else if(energyTarget >= 0)
					energyTarget += energyCrystal.energyTargetIncrease();
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
	
	@Override
	public void onDialAttempt(StargateInfo.Feedback feedback, Address address)
	{
		//TODO Save the address to more than one crystal
		if(memoryCrystals.getCrystals().length > 0)
		{
			ItemStack stack = itemHandler.getStackInSlot(memoryCrystals.getCrystals()[0]);
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
				memoryCrystal.saveMemoryEntry(stack, new MemoryEntry.StargateConnectionResult("", getLevel().getGameTime(), MemoryEntry.Type.ADDRESS, new StargateConnection.Result(address, feedback)), true);
		}
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
