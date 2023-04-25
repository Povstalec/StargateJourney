package net.povstalec.sgjourney.common.block_entities.symbols;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class StoneSymbolBlockEntity extends SymbolBlockEntity
{

	public StoneSymbolBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.STONE_SYMBOL.get(), pos, state);
	}
	
}
