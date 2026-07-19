package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.items.CallForwardingDevice;
import net.povstalec.sgjourney.common.items.crystals.*;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.StargateConnectionEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CrystalDHDEntity extends AbstractDHDEntity
{
	public static final String CRYSTAL_INVENTORY = "crystal_inventory";
	
	public final CrystalCache<CrystalDHDEntity> crystalCache = createCrystalCache();
	
	public final ItemStackHandler crystalHandler = createCrystalHandler();
	protected final Lazy<IItemHandler> lazyCrystalHandler = Lazy.of(() -> crystalHandler);
	
	public CrystalDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		crystalHandler.deserializeNBT(registries, tag.getCompound(CRYSTAL_INVENTORY));
		
		//TODO Remove this later
		if(!tag.contains(ENERGY_INVENTORY) && tag.contains(CRYSTAL_INVENTORY))
			generateEnergyCore();
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.put(CRYSTAL_INVENTORY, crystalHandler.serializeNBT(registries));
		super.saveAdditional(tag, registries);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		crystalCache.recalculateCrystals();
	}
	
	@Override
	public void invalidateCapabilities()
	{
		lazyCrystalHandler.invalidate();
		super.invalidateCapabilities();
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IItemHandler getItemHandler()
	{
		return lazyCrystalHandler.get();
	}
	
	@Nullable
	public IItemHandler getItemHandler(Direction side)
	{
		if(!isProtected() || CommonPermissionConfig.protected_inventory_access.get())
			return lazyCrystalHandler.get();
		return null;
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
			return stack.getItem() instanceof ControlCrystalItem crystal && crystal.isLarge();
		
		if(stack.getItem() instanceof AbstractCrystalItem crystal && !crystal.isLarge())
			return crystalCache.isSupported(crystal.getType());
		
		return false;
	}
	
	protected CrystalCache<CrystalDHDEntity> createCrystalCache()
	{
		return new CrystalCache.Generic9<>(this, CrystalCache.Type.CONTROL, CrystalCache.Type.MEMORY, CrystalCache.Type.ENERGY, CrystalCache.Type.TRANSFER, CrystalCache.Type.COMMUNICATION)
		{
			@Override
			protected void onReset()
			{
				enableAdvancedProtocols = false;
				
				hasNetworkRestrictions = false;
				networks.clear();
				
				energyTarget = 0;
				maxEnergyTransfer = 0;
				enableCallForwarding = false;
				maxConnectionDistance = DEFAULT_CONNECTION_DISTANCE;
			}
			
			@Override
			protected void fetchCrystals()
			{
				for(int i = 1; i < 9; i++)
				{
					ItemStack stack = crystalHandler.getStackInSlot(i);
					Item item = stack.getItem();
					
					if(item instanceof AbstractCrystalItem crystal)
						crystalCache.addCrystal(i, crystal);
					else if(item instanceof CallForwardingDevice)
						enableCallForwarding = true;
				}
			}
			
			@Override
			protected void updateFromCrystals()
			{
				// Check if the DHD has a Control Crystal
				enableAdvancedProtocols = !crystalHandler.getStackInSlot(0).isEmpty();
				
				// If there are 4 regular crystals or 3 advanced crystals
				if(crystalCache.energyCrystals().count(false) >= 4 || crystalCache.energyCrystals().count(true) >= 3)
					energyTarget = -1;
				else
					crystalCache.energyCrystals().forEach(slot -> energyTarget += slot.crystal.energyTargetIncrease());
				
				// If there are 4 regular crystals or 3 advanced crystals
				if(crystalCache.transferCrystals().count(false) >= 4 || crystalCache.energyCrystals().count(true) >= 3)
					energyTarget = -1;
				else
					crystalCache.transferCrystals().forEach(slot -> maxEnergyTransfer += slot.crystal.getMaxTransfer());
				
				crystalCache.controlCrystals().forEach(slot ->
				{
					//TODO Some special entry for Network Restriction
					if(!slot.crystal.isLarge())
						hasNetworkRestrictions = true;
				});
				
				crystalCache.communicationCrystals().forEach(slot ->
				{
					// Collect frequencies of different Communication Crystals and interpret them as networks the Stargate is in
					if(CommunicationCrystalItem.hasFrequency(crystalHandler.getStackInSlot(slot.index)))
						networks.add(CommunicationCrystalItem.getFrequency(crystalHandler.getStackInSlot(slot.index)));
					else
						maxConnectionDistance += slot.crystal.getRangeIncrease();
				});
				
				stargateCache.markDirtyTwoWays();
				stargateCache.ifPresent(AbstractStargateEntity::updateStargate);
			}
		};
	}
	
	@Override
	public void onDialAttempt(StargateInfo.FeedbackMessage feedback, Address address)
	{
		CompoundTag entry = new StargateConnectionEntry("", getLevel().getGameTime(), new StargateConnection.Result(address, feedback.feedback())).save();
		for(var slot : crystalCache.memoryCrystals().getSlots())
		{
			ItemStack stack = crystalHandler.getStackInSlot(slot.index);
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
