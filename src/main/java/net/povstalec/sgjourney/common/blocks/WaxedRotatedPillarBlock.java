package net.povstalec.sgjourney.common.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nullable;

public class WaxedRotatedPillarBlock extends RotatedPillarBlock
{
	public WaxedRotatedPillarBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
	{
		ItemStack itemStack = context.getItemInHand();
		if(ToolActions.AXE_WAX_OFF == toolAction && itemStack.canPerformAction(toolAction))
			return SGJourneyWeatheringBlock.getUnwaxed(state).orElse(null);
		
		return super.getToolModifiedState(state, context, toolAction, simulate);
	}
}
