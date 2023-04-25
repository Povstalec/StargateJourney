package net.povstalec.sgjourney.common.block_entities.symbols;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class SandstoneCartoucheEntity extends CartoucheEntity
{

	public SandstoneCartoucheEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.SANDSTONE_CARTOUCHE.get(), pos, state);
	}
	
}
