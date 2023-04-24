package net.povstalec.sgjourney.block_entities.dhd;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.ItemInit;
import net.povstalec.sgjourney.items.crystals.EnergyCrystalItem;
import net.povstalec.sgjourney.misc.ArrayHelper;

public class PegasusDHDEntity extends AbstractDHDEntity
{
	public PegasusDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.PEGASUS_DHD.get(), pos, state);
	}
	
	@Override
	protected boolean isSlotValid(int slot, @Nonnull ItemStack stack)
	{
		return isValidCrystal(stack);
	}
	
	protected boolean isValidCrystal(ItemStack stack)
	{
		if(stack.getItem() == ItemInit.ADVANCED_CONTROL_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.ADVANCED_MEMORY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.ADVANCED_ENERGY_CRYSTAL.get())
			return true;
		else if(stack.getItem() == ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get())
			return true;
		
		return false;
	}
	
	@Override
	public void recalculateCrystals()
	{
		this.enableAdvancedProtocols = false;
		this.memoryCrystals = new int[0];
		this.controlCrystals = new int[0];
		this.energyCrystals = new int[0];
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
			{
				EnergyCrystalItem.CrystalMode mode = EnergyCrystalItem.getCrystalMode(stack);
				
				switch(mode)
				{
				case ENERGY_STORAGE:
					this.desiredEnergyLevel += ItemInit.ADVANCED_ENERGY_CRYSTAL.get().getMaxStorage();
					break;
				case ENERGY_TRANSFER:
					this.maxEnergyTransfer += ItemInit.ADVANCED_ENERGY_CRYSTAL.get().getMaxTransfer();
					break;
				}
			}
		}
	}
	
	@Override
	public int getMaxDistance()
	{
		return this.communicationCrystals.length * ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get().getMaxDistance();
	}

}
