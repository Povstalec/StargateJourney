package net.povstalec.sgjourney.block_entities.address;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.init.BlockEntityInit;

public class SandstoneSymbolBlockEntity extends SymbolBlockEntity
{

	public SandstoneSymbolBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.SANDSTONE_SYMBOL.get(), pos, state);
	}
	
}
