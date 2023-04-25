package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class CrystallizerEntity extends EnergyBlockEntity
{
	public CrystallizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CRYSTALLIZER.get(), pos, state);
	}

	@Override
	protected long capacity()
	{
		return 0;
	}

	@Override
	protected long maxReceive()
	{
		return 0;
	}

	@Override
	protected long maxExtract()
	{
		return 0;
	}
	
}
