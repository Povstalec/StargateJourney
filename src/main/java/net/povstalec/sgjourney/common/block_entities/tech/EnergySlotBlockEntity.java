package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Block entity with an energy slot for charging
 */
public abstract class EnergySlotBlockEntity extends EnergyBlockEntity
{
	public static final String ENERGY_INVENTORY = "energy_inventory";
	
	public final ItemStackHandler energyItemHandler;
	protected final Lazy<IItemHandler> lazyEnergyItemHandler;
	
	public EnergySlotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		
		this.energyItemHandler = createEnergyItemHandler();
		this.lazyEnergyItemHandler = Lazy.of(() -> energyItemHandler);
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		energyItemHandler.deserializeNBT(registries, tag.getCompound(ENERGY_INVENTORY));
		
		super.loadAdditional(tag, registries);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		tag.put(ENERGY_INVENTORY, energyItemHandler.serializeNBT(registries));
	}
	
	
	
	@Override
	public void invalidateCapabilities()
	{
		lazyEnergyItemHandler.invalidate();
		
		super.invalidateCapabilities();
	}
	
	protected ItemStackHandler createEnergyItemHandler()
	{
		return new ItemStackHandler(1)
		{
			@Override
			protected void onContentsChanged(int slot)
			{
				setChanged();
			}
			
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack)
			{
				return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
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
	
	public static void tick(Level level, BlockPos pos, BlockState state, EnergySlotBlockEntity energyBlockEntity)
	{
		if(level.isClientSide())
			return;
		
		energyBlockEntity.extractItemEnergy(energyBlockEntity.energyItemHandler.getStackInSlot(0));
	}
}