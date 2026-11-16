package net.povstalec.sgjourney.common.blocks.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMHolderEntity;
import org.jetbrains.annotations.NotNull;

public class ZPMHolderBlock extends AbstractZPMHolderBlock
{
	private static final VoxelShape ZPM_HOLDER = Block.box(5.5D, 0.0D, 5.5D, 10.5D, 9.0D, 10.5D);
	
	public ZPMHolderBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new ZPMHolderEntity(pos, state);
	}
	
	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext collision)
	{
		return ZPM_HOLDER;
	}
}
