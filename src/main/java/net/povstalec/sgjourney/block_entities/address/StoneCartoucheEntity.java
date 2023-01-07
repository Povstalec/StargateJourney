package net.povstalec.sgjourney.block_entities.address;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.init.BlockEntityInit;

public class StoneCartoucheEntity extends CartoucheEntity
{

	public StoneCartoucheEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.STONE_CARTOUCHE.get(), pos, state);
	}
	
}
