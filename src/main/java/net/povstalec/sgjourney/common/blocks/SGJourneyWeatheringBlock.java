package net.povstalec.sgjourney.common.blocks;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;
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
				
				.put(BlockInit.SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.EXPOSED_SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				.put(BlockInit.WEATHERED_SMOOTH_NAQUADAH_COPPER_BLOCK.get(), BlockInit.OXIDIZED_SMOOTH_NAQUADAH_COPPER_BLOCK.get())
				
				.put(BlockInit.NAQUADAH_COPPER_LAMP.get(), BlockInit.EXPOSED_NAQUADAH_COPPER_LAMP.get())
				.put(BlockInit.EXPOSED_NAQUADAH_COPPER_LAMP.get(), BlockInit.WEATHERED_NAQUADAH_COPPER_LAMP.get())
				.put(BlockInit.WEATHERED_NAQUADAH_COPPER_LAMP.get(), BlockInit.OXIDIZED_NAQUADAH_COPPER_LAMP.get())
				.build();
	});
	Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());
	
	static Optional<Block> getPrevious(Block block)
	{
		return Optional.ofNullable(PREVIOUS_BY_BLOCK.get().get(block));
	}
	
	static Block getFirst(Block startBlock)
	{
		Block block = startBlock;
		
		for(Block nextBlock = PREVIOUS_BY_BLOCK.get().get(startBlock); nextBlock != null; nextBlock = PREVIOUS_BY_BLOCK.get().get(nextBlock))
		{
			block = nextBlock;
		}
		
		return block;
	}
	
	static Optional<BlockState> getPrevious(BlockState state)
	{
		return getPrevious(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}
	
	static Optional<Block> getNext(Block block)
	{
		return Optional.ofNullable(NEXT_BY_BLOCK.get().get(block));
	}
	
	static BlockState getFirst(BlockState state)
	{
		return getFirst(state.getBlock()).withPropertiesOf(state);
	}
	
	@Override
	default Optional<BlockState> getNext(BlockState state)
	{
		return getNext(state.getBlock()).map(block -> block.withPropertiesOf(state));
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
		for(Direction direction : Direction.values())
		{
			BlockPos otherPos = pos.relative(direction);
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
		
		for(Direction direction : Direction.values())
		{
			BlockPos otherPos = pos.relative(direction);
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
