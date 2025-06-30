package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;

import javax.annotation.Nullable;

public interface ProtectedBlock
{
	@Nullable
	ProtectedBlockEntity getProtectedBlockEntity(BlockGetter reader, BlockPos pos, BlockState state);
	
	boolean hasPermissions(BlockGetter reader, BlockPos pos, BlockState state, Player player, boolean sendMessage);
	
	default boolean canExplode(BlockGetter reader, BlockPos pos, BlockState state, Explosion explosion)
	{
		if(explosion.getIndirectSourceEntity() instanceof Player player)
			return hasPermissions(reader, pos, state, player, false);
		
		return !CommonPermissionConfig.protected_blocks_ignore_explosions.get();
	}
}
