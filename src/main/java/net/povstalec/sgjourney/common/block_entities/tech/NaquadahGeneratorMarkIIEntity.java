package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class NaquadahGeneratorMarkIIEntity extends NaquadahGeneratorEntity
{
	public NaquadahGeneratorMarkIIEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), pos, state);
	}

	@Override
	public long getReactionTime()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_reaction_time.get();
	}

	@Override
	public long getEnergyPerTick()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_energy_per_tick.get();
	}

	@Override
	public long capacity()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
	}

	@Override
	public long maxReceive()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_max_transfer.get();
	}

	@Override
	public long maxExtract()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_max_transfer.get();
	}
	
}
