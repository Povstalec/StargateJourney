package net.povstalec.sgjourney.blocks.entities.address;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.init.BlockEntityInit;

public class SandstoneCartoucheEntity extends CartoucheEntity
{

	public SandstoneCartoucheEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.SANDSTONE_CARTOUCHE.get(), pos, state);
	}
	
}
