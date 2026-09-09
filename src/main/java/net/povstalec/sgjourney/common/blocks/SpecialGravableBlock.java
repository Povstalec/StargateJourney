package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.GraverItem;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

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
	
	default void useGraver(ServerPlayer player, BlockPos pos)
	{
		if(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GraverItem)
		{
			player.swing(InteractionHand.MAIN_HAND, true);
			player.level.playSound(null, pos, SoundInit.GRAVER_ENGRAVE.get(), SoundSource.BLOCKS, 1.0F, player.level.getRandom().nextFloat() * 0.4F + 0.8F);
			ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		}
		else if(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GraverItem)
		{
			player.swing(InteractionHand.OFF_HAND, true);
			player.level.playSound(null, pos, SoundInit.GRAVER_ENGRAVE.get(), SoundSource.BLOCKS, 1.0F, player.level.getRandom().nextFloat() * 0.4F + 0.8F);
			ItemStack itemStack = player.getItemInHand(InteractionHand.OFF_HAND);
			itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
		}
	}
	
	default void setPointOfOrigin(Level level, BlockPos pos, BlockState state, ResourceKey<PointOfOrigin> pointOfOrigin) {}
	
	default void setSymbols(Level level, BlockPos pos, BlockState state, ResourceKey<Symbols> symbols) {}
	
	default void setAddress(Level level, BlockPos pos, BlockState state, Address address) {}
}
