package net.povstalec.sgjourney.block_entities.energy_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.config.ServerNaquadahGeneratorConfig;
import net.povstalec.sgjourney.init.BlockEntityInit;

public class NaquadahGeneratorMarkIIEntity extends NaquadahGeneratorEntity
{
	private static final int reactionTime = ServerNaquadahGeneratorConfig.naquadah_generator_mark_ii_reaction_time.get();
	private static final int energyPerTick = ServerNaquadahGeneratorConfig.naquadah_generator_mark_ii_energy_per_tick.get();
	private static final int capacity = ServerNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
	private static final int maxTransfer = ServerNaquadahGeneratorConfig.naquadah_generator_mark_ii_max_transfer.get();
	
	public NaquadahGeneratorMarkIIEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), pos, state);
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
