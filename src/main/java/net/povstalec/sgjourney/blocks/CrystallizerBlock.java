package net.povstalec.sgjourney.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.block_entities.CrystallizerEntity;

public class CrystallizerBlock extends HorizontalDirectionalBlock implements EntityBlock
{
	public CrystallizerBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new CrystallizerEntity(pos, state);
	}
	
}
