package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WeatheringSlabBlock extends SlabBlock implements SGJourneyWeatheringBlock
{
	private final WeatherState weatherState;
	
	public WeatheringSlabBlock(WeatherState weatherState, Properties properties)
	{
		super(properties);
		this.weatherState = weatherState;
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		this.changeOverTime(state, level, pos, randomSource);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state)
	{
		return SGJourneyWeatheringBlock.getNext(state.getBlock()).isPresent();
	}
	
	@Override
	public @NotNull WeatherState getAge()
	{
		return this.weatherState;
	}
	
	@Nullable
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate)
	{
		ItemStack itemStack = context.getItemInHand();
		if(ItemAbilities.AXE_SCRAPE == itemAbility && itemStack.canPerformAction(itemAbility))
			return SGJourneyWeatheringBlock.getPrevious(state).orElse(null);
		
		return super.getToolModifiedState(state, context, itemAbility, simulate);
	}
	
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(tryApplyWax(state, level, pos, player, hand))
			return ItemInteractionResult.SUCCESS;
		
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
}
