package net.povstalec.sgjourney.common.block_entities.dhd;

import javax.annotation.Nonnull;

import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.items.crystals.*;
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
import net.povstalec.sgjourney.common.items.CallForwardingDevice;

public abstract class CrystalDHDEntity extends AbstractDHDEntity
{
	public static final String CRYSTAL_INVENTORY = "Inventory"; // TODO Rename this to "crystal_inventory" in the future
	
	public final CrystalCache<CrystalDHDEntity> crystalCache = new CrystalCache<>(this, CrystalCache.Type.CONTROL, CrystalCache.Type.MEMORY, CrystalCache.Type.ENERGY, CrystalCache.Type.TRANSFER, CrystalCache.Type.COMMUNICATION);
	
	public final ItemStackHandler crystalHandler = createCrystalHandler();
	protected final LazyOptional<IItemHandler> lazyCrystalHandler = LazyOptional.of(() -> crystalHandler);
	
	public CrystalDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
		crystalCache.setOnRecalculateCrystals(CrystalDHDEntity::recalculateCrystals);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		crystalHandler.deserializeNBT(tag.getCompound(CRYSTAL_INVENTORY));
		
		//TODO Remove this later
		if(!tag.contains(ENERGY_INVENTORY) && tag.contains(CRYSTAL_INVENTORY))
			generateEnergyCore();
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		tag.put(CRYSTAL_INVENTORY, crystalHandler.serializeNBT());
		super.saveAdditional(tag);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		crystalCache.recalculateCrystals();
	}
	
	@Override
	public void invalidateCaps()
	{
		lazyCrystalHandler.invalidate();
		super.invalidateCaps();
	}
	
	public LazyOptional<IItemHandler> getCrystalHandler()
	{
		return lazyCrystalHandler.cast();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER && (!isProtected() || CommonPermissionConfig.protected_inventory_access.get()))
			return lazyEnergyItemHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	protected ItemStackHandler createCrystalHandler()
	{
		return new ItemStackHandler(9)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
					crystalCache.recalculateCrystals();
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
						return stack;
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
	}
	
	protected boolean isValidCrystal(int slot, ItemStack stack)
	{
		if(slot == 0)
			return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isLarge();
		
		return (stack.getItem() instanceof AbstractCrystalItem crystal && !crystal.isLarge()) || stack.getItem() instanceof CallForwardingDevice;
	}
	
	protected void recalculateCrystals()
	{
		// Check if the DHD has a Control Crystal
		enableAdvancedProtocols = !crystalHandler.getStackInSlot(0).isEmpty();
		
		hasNetworkRestrictions = false;
		networks.clear();
		
		energyTarget = 0;
		maxEnergyTransfer = 0;
		enableCallForwarding = false;
		maxConnectionDistance = DEFAULT_CONNECTION_DISTANCE;
		
		// Check where the Crystals are and save their positions
		for(int i = 1; i < 9; i++)
		{
			ItemStack stack = crystalHandler.getStackInSlot(i);
			Item item = stack.getItem();
			
			if(item instanceof AbstractCrystalItem crystal)
				crystalCache.addCrystal(i, crystal);
			else if(item instanceof CallForwardingDevice)
				enableCallForwarding = true;
		}
		
		// If there are 4 regular crystals or 3 advanced crystals
		if(crystalCache.energyCrystals().count(false) >= 4 || crystalCache.energyCrystals().count(true) >= 3)
			energyTarget = -1;
		else
			crystalCache.energyCrystals().forEach((slot, energyCrystal) -> energyTarget += energyCrystal.energyTargetIncrease());
		
		// If there are 4 regular crystals or 3 advanced crystals
		if(crystalCache.transferCrystals().count(false) >= 4 || crystalCache.energyCrystals().count(true) >= 3)
			energyTarget = -1;
		else
			crystalCache.transferCrystals().forEach((slot, transferCrystal) -> maxEnergyTransfer += transferCrystal.getMaxTransfer());
		
		crystalCache.controlCrystals().forEach((slot, controlCrystal) ->
		{
			//TODO Some special entry for Network Restriction
			if(!controlCrystal.isLarge())
				hasNetworkRestrictions = true;
		});
		
		crystalCache.communicationCrystals().forEach((slot, communicationCrystal) ->
		{
			// Collect frequencies of different Communication Crystals and interpret them as networks the Stargate is in
			if(CommunicationCrystalItem.hasFrequency(crystalHandler.getStackInSlot(slot)))
				networks.add(CommunicationCrystalItem.getFrequency(crystalHandler.getStackInSlot(slot)));
			else
				maxConnectionDistance += communicationCrystal.getRangeIncrease();
		});
		
		stargateCache.markDirtyTwoWays();
		stargateCache.ifPresent(AbstractStargateEntity::updateStargate);
	}
	
	@Override
	public void onDialAttempt(StargateInfo.FeedbackMessage feedback, Address address)
	{
		CompoundTag entry = new MemoryEntry.StargateConnectionResult("", getLevel().getGameTime(), MemoryEntry.Type.STARGATE_CONNECTION_RESULT, new StargateConnection.Result(address, feedback.feedback())).save();
		for(int slot : crystalCache.memoryCrystals().getSlots())
		{
			ItemStack stack = crystalHandler.getStackInSlot(slot);
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
				entry = memoryCrystal.saveCompound(stack, entry, true); // Save memory and move the oldest one to another crystal
			
			if(entry == null)
				break; // End early if there are no more memories to move back
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
