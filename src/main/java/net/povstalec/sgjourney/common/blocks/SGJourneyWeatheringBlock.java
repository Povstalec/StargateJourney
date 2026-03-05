package net.povstalec.sgjourney.common.blocks;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.init.BlockInit;

import java.util.Optional;
import java.util.function.Supplier;

public interface SGJourneyWeatheringBlock extends ChangeOverTimeBlock<SGJourneyWeatheringBlock.WeatherState>
{
	float PROBABILITY = 0.05688889F;
	
	Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
		return ImmutableBiMap.<Block, Block>builder()
				.put(BlockInit.NAQUADAH_COPPER_BLOCK.get(), BlockInit.EXPOSED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WEATHERED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.OXIDIZED_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.NAQUADAH_COPPER_STAIRS.get(), BlockInit.EXPOSED_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WEATHERED_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_STAIRS.get(), BlockInit.OXIDIZED_NAQUADAH_COPPER_STAIRS.get())
				
				.put(BlockInit.NAQUADAH_COPPER_SLAB.get(), BlockInit.EXPOSED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WEATHERED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_SLAB.get(), BlockInit.OXIDIZED_NAQUADAH_COPPER_SLAB.get())
				
				.put(BlockInit.POLISHED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.POLISHED_NAQUADAH_COPPER_SLAB.get(), BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB.get(), BlockInit.OXIDIZED_POLISHED_NAQUADAH_COPPER_SLAB.get())
				
				.put(BlockInit.NAQUADAH_COPPER_LAMP.get(), BlockInit.EXPOSED_NAQUADAH_COPPER_LAMP.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_LAMP.get(), BlockInit.WEATHERED_NAQUADAH_COPPER_LAMP.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_LAMP.get(), BlockInit.OXIDIZED_NAQUADAH_COPPER_LAMP.get())
				
				.put(BlockInit.SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.SMOOTH_NAQUADAH_COPPER_STAIRS.get(), BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS.get(), BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_STAIRS.get())
				
				.put(BlockInit.SMOOTH_NAQUADAH_COPPER_SLAB.get(), BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB.get(), BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB.get(), BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_SLAB.get())
				
				.put(BlockInit.CUT_NAQUADAH_COPPER_BLOCK.get(), BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get(), BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.CUT_NAQUADAH_COPPER_STAIRS.get(), BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_STAIRS.get(), BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_STAIRS.get())
				
				.put(BlockInit.CUT_NAQUADAH_COPPER_SLAB.get(), BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_SLAB.get(), BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_SLAB.get(), BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_SLAB.get())
				.build();
	});
	Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());
	
	Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() ->
	{
		return ImmutableBiMap.<Block, Block>builder()
				.put(BlockInit.NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.OXIDIZED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.OXIDIZED_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_STAIRS.get())
				
				.put(BlockInit.NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.OXIDIZED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_SLAB.get())
				
				.put(BlockInit.POLISHED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_POLISHED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.POLISHED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_POLISHED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_EXPOSED_POLISHED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_WEATHERED_POLISHED_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.OXIDIZED_POLISHED_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_OXIDIZED_POLISHED_NAQUADAH_COPPER_SLAB.get())
				
				.put(BlockInit.NAQUADAH_COPPER_LAMP.get(), BlockInit.WAXED_NAQUADAH_COPPER_LAMP.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_LAMP.get(), BlockInit.WAXED_EXPOSED_NAQUADAH_COPPER_LAMP.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_LAMP.get(), BlockInit.WAXED_WEATHERED_NAQUADAH_COPPER_LAMP.get())
				.put(BlockInit.OXIDIZED_NAQUADAH_COPPER_LAMP.get(), BlockInit.WAXED_OXIDIZED_NAQUADAH_COPPER_LAMP.get())
				
				.put(BlockInit.SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.SMOOTH_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_SMOOTH_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_STAIRS.get())
				
				.put(BlockInit.SMOOTH_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_SMOOTH_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_EXPOSED_SMOOTH_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_WEATHERED_SMOOTH_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_OXIDIZED_SMOOTH_NAQUADAH_COPPER_SLAB.get())
				
				.put(BlockInit.CUT_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_CUT_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_EXPOSED_CUT_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_WEATHERED_CUT_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.CUT_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_CUT_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_EXPOSED_CUT_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_WEATHERED_CUT_NAQUADAH_COPPER_STAIRS.get())
				.put(BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_STAIRS.get(), BlockInit.WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_STAIRS.get())
				
				.put(BlockInit.CUT_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_CUT_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.EXPOSED_CUT_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_EXPOSED_CUT_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.WEATHERED_CUT_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_WEATHERED_CUT_NAQUADAH_COPPER_SLAB.get())
				.put(BlockInit.OXIDIZED_CUT_NAQUADAH_COPPER_SLAB.get(), BlockInit.WAXED_OXIDIZED_CUT_NAQUADAH_COPPER_SLAB.get())
				.build();
	});
	Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> WAXABLES.get().inverse());
	
	// Block
	
	static Block getFirst(Block startBlock)
	{
		Block block = startBlock;
		
		for(Block nextBlock = PREVIOUS_BY_BLOCK.get().get(startBlock); nextBlock != null; nextBlock = PREVIOUS_BY_BLOCK.get().get(nextBlock))
		{
			block = nextBlock;
		}
		
		return block;
	}
	
	static Optional<Block> getPrevious(Block block)
	{
		return Optional.ofNullable(PREVIOUS_BY_BLOCK.get().get(block));
	}
	
	static Optional<Block> getNext(Block block)
	{
		return Optional.ofNullable(NEXT_BY_BLOCK.get().get(block));
	}
	
	static Optional<Block> getWaxed(Block block)
	{
		return Optional.ofNullable(WAXABLES.get().get(block));
	}
	
	static Optional<Block> getUnwaxed(Block block)
	{
		return Optional.ofNullable(WAX_OFF_BY_BLOCK.get().get(block));
	}
	
	// BlockState
	
	static BlockState getFirst(BlockState state)
	{
		return getFirst(state.getBlock()).withPropertiesOf(state);
	}
	
	static Optional<BlockState> getPrevious(BlockState state)
	{
		return getPrevious(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}
	
	@Override
	default Optional<BlockState> getNext(BlockState state)
	{
		return getNext(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}
	
	static Optional<BlockState> getWaxed(BlockState state)
	{
		return getWaxed(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}
	
	static Optional<BlockState> getUnwaxed(BlockState state)
	{
		return getUnwaxed(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}
	
	default boolean tryApplyWax(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if(stack.getItem() instanceof HoneycombItem)
		{
			Optional<BlockState> waxedState = SGJourneyWeatheringBlock.getWaxed(state);
			if(waxedState.isPresent())
			{
				if(!player.isCreative())
					stack.shrink(1);
				level.setBlock(pos, waxedState.get(), 11);
				level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxedState.get()));
				level.levelEvent(player, 3003, pos, 0);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	default float getChanceModifier()
	{
		return this.getAge().chanceModifier;
	}
	
	static int getWeatherAge(BlockState state, ServerLevel level, BlockPos pos)
	{
		Block block = state.getBlock();
		if(block instanceof SGJourneyWeatheringBlock weatheringBlock)
			return weatheringBlock.getAge().ordinal();
		else if(block instanceof AbstractStargateBlock stargateBlock)
		{
			SGJourneyWeatheringBlock.WeatherState weatherState = stargateBlock.getWeatherState(level, pos, state);
			if(weatherState != null)
				return weatherState.ordinal();
		}
		
		return -1;
	}
	
	default Optional<BlockState> changeOverTime(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		int age = this.getAge().ordinal();
		int sameAge = 0;
		int older = 0;
		for(BlockPos otherPos : BlockPos.withinManhattan(pos, 1, 1, 1))
		{
			if(!otherPos.equals(pos))
			{
				BlockState otherState = level.getBlockState(otherPos);
				int otherAge = getWeatherAge(otherState, level, otherPos);
				if(otherAge >= 0)
				{
					if(otherAge < age) // If there is a younger block nearby, don't increase age further
						return Optional.empty();
					
					if(otherAge > age)
						older++;
					else
						sameAge++;
				}
			}
		}
		
		float modifier = (float)(older + 1) / (float)(older + sameAge + 1);
		float probability = modifier * modifier * this.getChanceModifier();
		if(randomSource.nextFloat() < probability)
			return this.getNext(state);
		
		return Optional.empty();
	}
	
	default boolean passesProbability(RandomSource randomSource)
	{
		return randomSource.nextFloat() < PROBABILITY;
	}
	
	@Override
	default void applyChangeOverTime(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		int age = this.getAge().ordinal();
		int sameAge = 0;
		int older = 0;
		
		for(BlockPos otherPos : BlockPos.withinManhattan(pos, 1, 1, 1))
		{
			if(!otherPos.equals(pos))
			{
				BlockState otherState = level.getBlockState(otherPos);
				int otherAge = getWeatherAge(otherState, level, otherPos);
				if(otherAge >= 0)
				{
					if(otherAge < age) // If there is a younger block nearby, don't increase age further
						return;
					
					if(otherAge > age)
						older++;
					else
						sameAge++;
				}
			}
		}
		
		float modifier = (float)(older + 1) / (float)(older + sameAge + 1);
		float probability = modifier * modifier * this.getChanceModifier();
		if(randomSource.nextFloat() < probability)
			this.getNext(state).ifPresent((nextState) -> level.setBlockAndUpdate(pos, nextState));
	}
	
	enum WeatherState
	{
		UNAFFECTED(0.5F),
		EXPOSED(0.5F),
		WEATHERED(0.125F),
		OXIDIZED(0.0F);
		
		public final float chanceModifier;
		
		WeatherState(float chanceModifier)
		{
			this.chanceModifier = chanceModifier;
		}
	}
}
