package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.CallForwardingDevice;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;

public class ClassicDHDEntity extends CrystalDHDEntity
{
	public ClassicDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CLASSIC_DHD.get(), pos, state);
	}
	
	protected long buttonPressEnergyCost()
	{
		return CommonDHDConfig.classic_dhd_button_press_energy_cost.get();
	}
	
	@Override
	protected long capacity()
	{
		return CommonDHDConfig.classic_dhd_energy_buffer_capacity.get();
	}
	
	@Override
	protected long maxReceive()
	{
		return CommonDHDConfig.classic_dhd_max_energy_receive.get();
	}
	
	@Override
	public long maxEnergyDeplete()
	{
		return this.maxEnergyTransfer < 0 ? CommonDHDConfig.milky_way_dhd_max_energy_extract.get() : this.maxEnergyTransfer;
	}

	@Override
	protected SoundEvent getEnterSound()
	{
		return SoundInit.CLASSIC_DHD_ENTER.get();
	}

	@Override
	protected SoundEvent getPressSound()
	{
		return SoundInit.CLASSIC_DHD_PRESS.get();
	}
	
	@Override
	protected boolean isValidCrystal(int slot, ItemStack stack)
	{
		if(slot == 0)
			return stack.getItem() instanceof AbstractCrystalItem crystal && crystal.isLarge();
		
		return stack.getItem() instanceof AbstractCrystalItem crystal && !crystal.isAdvanced() || stack.getItem() instanceof CallForwardingDevice;
	}
}
