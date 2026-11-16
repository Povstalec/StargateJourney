package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.zpm.AbstractZPMHolderEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;

import javax.annotation.Nullable;

public interface ProtectedBlock
{
	@Nullable
	default ProtectedBlockEntity getProtectedBlockEntity(BlockGetter reader, BlockPos pos, BlockState state)
	{
		BlockEntity blockEntity = reader.getBlockEntity(pos);
		
		if(blockEntity instanceof ProtectedBlockEntity protectedBlockEntity)
			return protectedBlockEntity;
		
		return null;
	}
	
	default boolean hasPermissions(BlockGetter reader, BlockPos pos, BlockState state, Player player, boolean sendMessage)
	{
		BlockEntity blockEntity = reader.getBlockEntity(pos);
		
		if(blockEntity instanceof ProtectedBlockEntity protectedBlockEntity)
			return protectedBlockEntity.hasPermissions(player, sendMessage);
		
		return true;
	}
	
	default boolean canExplode(BlockGetter reader, BlockPos pos, BlockState state, Explosion explosion)
	{
		if(explosion.getIndirectSourceEntity() instanceof Player player)
			return hasPermissions(reader, pos, state, player, false);
		
		return !CommonPermissionConfig.protected_blocks_ignore_explosions.get();
	}
}
