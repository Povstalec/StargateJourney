package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class NaquadahGeneratorMarkIEntity extends NaquadahGeneratorEntity
{
	public NaquadahGeneratorMarkIEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.NAQUADAH_GENERATOR_MARK_I.get(), pos, state);
	}

	@Override
	public long getReactionTime()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_reaction_time.get();
	}

	@Override
	public long getEnergyPerTick()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get();
	}

	@Override
	public long capacity()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
	}

	@Override
	public long maxReceive()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_max_transfer.get();
	}

	@Override
	public long maxExtract()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_max_transfer.get();
	}
	
}
