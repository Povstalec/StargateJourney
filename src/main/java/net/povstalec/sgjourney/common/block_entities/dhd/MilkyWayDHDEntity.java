package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.energy_cores.FusionCoreItem;

import org.jetbrains.annotations.NotNull;

public class MilkyWayDHDEntity extends CrystalDHDEntity
{
	public MilkyWayDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.MILKY_WAY_DHD.get(), pos, state);
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		symbolInfo().setPointOfOrigin(ResourceLocation.tryParse(tag.getString(POINT_OF_ORIGIN)));
		symbolInfo().setSymbols(ResourceLocation.tryParse(tag.getString(SYMBOLS)));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		tag.putString(POINT_OF_ORIGIN, symbolInfo().pointOfOrigin().toString());
		tag.putString(SYMBOLS, symbolInfo().symbols().toString());
	}
	
	protected long buttonPressEnergyCost()
	{
		return CommonDHDConfig.milky_way_dhd_button_press_energy_cost.get();
	}
	
	@Override
	protected long capacity()
	{
		return CommonDHDConfig.milky_way_dhd_energy_buffer_capacity.get();
	}
	
	@Override
	protected long maxReceive()
	{
		return CommonDHDConfig.milky_way_dhd_max_energy_receive.get();
	}
	
	@Override
	public long maxEnergyDeplete()
	{
		return this.maxEnergyTransfer < 0 ? CommonDHDConfig.milky_way_dhd_max_energy_extract.get() : this.maxEnergyTransfer;
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
	protected void generateEnergyCore()
	{
		super.generateEnergyCore();
		
		energyItemHandler.setStackInSlot(0, FusionCoreItem.randomFusionCore(CommonTechConfig.fusion_core_fuel_capacity.get() / 3, CommonTechConfig.fusion_core_fuel_capacity.get()));
	}
}
