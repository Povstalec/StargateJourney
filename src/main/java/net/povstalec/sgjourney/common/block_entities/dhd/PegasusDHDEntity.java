package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.SoundInit;

public class PegasusDHDEntity extends CrystalDHDEntity
{
	public PegasusDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.PEGASUS_DHD.get(), pos, state);
	}

	@Override
	protected SoundEvent getEnterSound()
	{
		return SoundInit.PEGASUS_DHD_ENTER.get();
	}

	@Override
	protected SoundEvent getPressSound()
	{
		return SoundInit.PEGASUS_DHD_PRESS.get();
	}
}
