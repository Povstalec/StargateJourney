package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.LiquidizerEntity;

public class LiquidizerBlock extends HorizontalDirectionalBlock implements EntityBlock
{
	public LiquidizerBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new LiquidizerEntity(pos, state);
	}
	
}
