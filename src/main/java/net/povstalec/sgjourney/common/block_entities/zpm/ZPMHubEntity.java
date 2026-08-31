package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class ZPMHubEntity extends AbstractZPMHolderEntity
{
	public ZPMHubEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ZPM_HUB.get(), pos, state);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return side == Direction.DOWN;
	}
	
	@Override
	public long getEnergyCapacity()
	{
		return CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	}

	@Override
	public long getMaxEnergyReceive()
	{
		return 0;
	}

	@Override
	public long getMaxEnergyExtract()
	{
		return CommonZPMConfig.zpm_hub_max_transfer.get();
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, ZPMHubEntity hub)
	{
		if(level.isClientSide())
			return;
		
		hub.outputEnergy(Direction.DOWN);
	}
}
