package net.povstalec.sgjourney.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.block_entities.SGJourneyBlockEntity;

public interface SGJourneyEntityBlock
{
	public default void removeFromBlockEntityList(Level level, BlockPos pos)
	{
		BlockEntity entity = level.getBlockEntity(pos);
        
        if (entity instanceof SGJourneyBlockEntity blockEntity)
            blockEntity.removeFromBlockEntityList();
	}
}
