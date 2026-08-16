package net.povstalec.sgjourney.common.block_entities.tech_interface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class AdvancedCrystalInterfaceEntity extends AbstractInterfaceEntity
{
	public AdvancedCrystalInterfaceEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ADVANCED_CRYSTAL_INTERFACE.get(), pos, state, InterfaceType.ADVANCED_CRYSTAL);
	}
	
	protected AdvancedCrystalInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, InterfaceType.ADVANCED_CRYSTAL);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public long getEnergyCapacity()
	{
		return CommonInterfaceConfig.advanced_crystal_interface_capacity.get();
	}

	@Override
	public long getMaxEnergyReceive()
	{
		return CommonInterfaceConfig.advanced_crystal_interface_max_transfer.get();
	}
	
	@Override
	public long getMaxEnergyExtract()
	{
		return 0;
	}
	
	@Override
	public long getMaxEnergyDeplete()
	{
		return CommonInterfaceConfig.advanced_crystal_interface_max_transfer.get();
	}
}
