package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;

import javax.annotation.Nullable;

public interface ProtectedBlock
{
	@Nullable
	ProtectedBlockEntity getProtectedBlockEntity(BlockGetter reader, BlockPos pos, BlockState state);
	
	boolean hasPermissions(BlockGetter reader, BlockPos pos, BlockState state, Player player, boolean sendMessage);
}
