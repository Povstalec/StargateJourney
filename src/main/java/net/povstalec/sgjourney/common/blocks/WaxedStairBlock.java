package net.povstalec.sgjourney.common.blocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WaxedStairBlock extends StairBlock
{
	public WaxedStairBlock(Supplier<BlockState> state, Properties properties)
	{
		super(state, properties);
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
