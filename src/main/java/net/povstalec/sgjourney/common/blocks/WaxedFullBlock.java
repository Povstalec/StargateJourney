package net.povstalec.sgjourney.common.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class WaxedFullBlock extends Block
{
	public WaxedFullBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate)
	{
		ItemStack itemStack = context.getItemInHand();
		if(ItemAbilities.AXE_WAX_OFF == itemAbility && itemStack.canPerformAction(itemAbility))
			return SGJourneyWeatheringBlock.getUnwaxed(state).orElse(null);
		
		return super.getToolModifiedState(state, context, itemAbility, simulate);
	}
}
