package net.povstalec.sgjourney.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.init.BlockEntityInit;

public class PegasusDHDEntity extends AbstractDHDEntity
{
	public PegasusDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.PEGASUS_DHD.get(), pos, state);
	}

}
