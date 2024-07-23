package net.povstalec.sgjourney.common.misc;

import javax.annotation.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class CoverBlockPlaceContext extends BlockPlaceContext
{
	public CoverBlockPlaceContext(Level level, @Nullable Player player, InteractionHand hand, ItemStack stack, BlockHitResult result)
	{
		super(level, player, hand, stack, result);
		this.replaceClicked = true;
	}
}
