package net.povstalec.sgjourney.common.block_entities.dhd;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.CallForwardingDevice;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;

public class MilkyWayDHDEntity extends CrystalDHDEntity
{
	public MilkyWayDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.MILKY_WAY_DHD.get(), pos, state);
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		addTransferCrystals(itemHandler);
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
	
	@Override
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
					if(slot == 0)
						return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isLarge();
					else
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
						return stack;
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
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
