package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Interface for Blocks with special Graver interactions (like opening a screen to select specific symbols)
 */
public interface SpecialGravableBlock
{
	InteractionResult onGraverUsed(Level level, BlockPos pos, @Nullable Player player, InteractionHand hand, ItemStack graverStack);
	
	default InteractionResult onGraverUsed(UseOnContext context)
	{
		return onGraverUsed(context.getLevel(), context.getClickedPos(), context.getPlayer(), context.getHand(), context.getItemInHand());
	}
}
