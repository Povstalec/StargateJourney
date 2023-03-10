package net.povstalec.sgjourney.block_entities.energy_gen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.config.ServerNaquadahGeneratorConfig;
import net.povstalec.sgjourney.init.BlockEntityInit;

public class NaquadahGeneratorMarkIEntity extends NaquadahGeneratorEntity
{
	private static final int reactionTime = ServerNaquadahGeneratorConfig.naquadah_generator_mark_i_reaction_time.get();
	private static final int energyPerTick = ServerNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get();
	private static final int capacity = ServerNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
	private static final int maxTransfer = ServerNaquadahGeneratorConfig.naquadah_generator_mark_i_max_transfer.get();
	
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
