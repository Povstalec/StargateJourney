package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.CallForwardingDevice;
import net.povstalec.sgjourney.common.items.energy_cores.FusionCoreItem;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;

public class PegasusDHDEntity extends CrystalDHDEntity
{
	public PegasusDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.PEGASUS_DHD.get(), pos, state);
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		addTransferCrystals(itemHandler);
	}
	
	protected long buttonPressEnergyCost()
	{
		return CommonDHDConfig.pegasus_dhd_button_press_energy_cost.get();
	}
	
	@Override
	protected long capacity()
	{
		return CommonDHDConfig.pegasus_dhd_energy_buffer_capacity.get();
	}
	
	@Override
	protected long maxReceive()
	{
		return CommonDHDConfig.pegasus_dhd_max_energy_receive.get();
	}
	
	@Override
	public long maxEnergyDeplete()
	{
		return this.maxEnergyTransfer < 0 ? CommonDHDConfig.milky_way_dhd_max_energy_extract.get() : this.maxEnergyTransfer;
	}

	@Override
	protected SoundEvent getEnterSound()
	{
		return SoundInit.PEGASUS_DHD_ENTER.get();
	}

	@Override
	protected SoundEvent getPressSound()
	{
		return SoundInit.PEGASUS_DHD_PRESS.get();
	}
	
	@Override
	protected boolean isValidCrystal(int slot, ItemStack stack)
	{
		if(slot == 0)
			return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isLarge();
		
		return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isAdvanced() || stack.getItem() instanceof CallForwardingDevice;
	}
	

	// TODO Temporary function for replacing old Energy Crystals with new Transfer Crystals
	public static void addTransferCrystals(ItemStackHandler itemHandler)
	{
		int slots = itemHandler.getSlots();
		
		for(int i = 0; i < slots; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(i);
			
			if(stack.is(ItemInit.ADVANCED_ENERGY_CRYSTAL.get()) && stack.hasTag())
			{
				if(stack.getTag().getString(CRYSTAL_MODE).equals(ENERGY_TRANSFER))
				{
					itemHandler.setStackInSlot(i, new ItemStack(ItemInit.TRANSFER_CRYSTAL.get()));
					StargateJourney.LOGGER.info("Replaced Transfer Crystal");
				}
			}
		}
	}
	
	@Override
	protected void generateEnergyCore()
	{
		energyItemHandler.setStackInSlot(0, FusionCoreItem.randomFusionCore(CommonTechConfig.fusion_core_fuel_capacity.get() / 2, CommonTechConfig.fusion_core_fuel_capacity.get()));
	}
	
	@Override
	protected void generateCrystals()
	{
		itemHandler.setStackInSlot(0, new ItemStack(ItemInit.LARGE_CONTROL_CRYSTAL.get()));
		itemHandler.setStackInSlot(1, new ItemStack(ItemInit.ADVANCED_ENERGY_CRYSTAL.get()));
		itemHandler.setStackInSlot(2, new ItemStack(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get()));
		itemHandler.setStackInSlot(3, new ItemStack(ItemInit.ADVANCED_ENERGY_CRYSTAL.get()));
		itemHandler.setStackInSlot(6, new ItemStack(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get()));
		itemHandler.setStackInSlot(7, new ItemStack(ItemInit.ADVANCED_TRANSFER_CRYSTAL.get()));
	}

}
