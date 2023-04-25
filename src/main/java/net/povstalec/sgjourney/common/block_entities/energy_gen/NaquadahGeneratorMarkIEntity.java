package net.povstalec.sgjourney.common.block_entities.energy_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class NaquadahGeneratorMarkIEntity extends NaquadahGeneratorEntity
{
	private static final int reactionTime = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_reaction_time.get();
	private static final int energyPerTick = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get();
	private static final int capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
	private static final int maxTransfer = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_max_transfer.get();
	
	public NaquadahGeneratorMarkIEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.NAQUADAH_GENERATOR_MARK_I.get(), pos, state);
	}

	@Override
	public int getReactionTime()
	{
		return reactionTime;
	}

	@Override
	public int getEnergyPerTick()
	{
		return energyPerTick;
	}

	@Override
	public long capacity()
	{
		return capacity;
	}

	@Override
	public long maxReceive()
	{
		return maxTransfer;
	}

	@Override
	public long maxExtract()
	{
		return maxTransfer;
	}
	
}
