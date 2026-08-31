package net.povstalec.sgjourney.common.blocks.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMHubEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ZPMHubBlock extends AbstractZPMHolderBlock
{
	public ZPMHubBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new ZPMHubEntity(pos, state);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ZPM_HUB.get(), ZPMHubEntity::tick);
    }
}
