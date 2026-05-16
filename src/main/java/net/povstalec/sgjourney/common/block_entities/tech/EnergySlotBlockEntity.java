package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Block entity with an energy slot for charging
 */
public abstract class EnergySlotBlockEntity extends EnergyBlockEntity
{
	public static final String ENERGY_INVENTORY = "energy_inventory";
	
	public final ItemStackHandler energyItemHandler;
	protected final LazyOptional<IItemHandler> lazyEnergyItemHandler;
	
	public EnergySlotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		
		this.energyItemHandler = createEnergyItemHandler();
		this.lazyEnergyItemHandler = LazyOptional.of(() -> energyItemHandler);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		energyItemHandler.deserializeNBT(tag.getCompound(ENERGY_INVENTORY));
		
		super.load(tag);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.put(ENERGY_INVENTORY, energyItemHandler.serializeNBT());
	}
	
	
	
	@Override
	public void invalidateCaps()
	{
		lazyEnergyItemHandler.invalidate();
		
		super.invalidateCaps();
	}
	
	public LazyOptional<IItemHandler> getEnergyItemHandler()
	{
		return lazyEnergyItemHandler.cast();
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
				return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
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
		
		energyBlockEntity.updateClient();
	}
}