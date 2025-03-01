package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.povstalec.sgjourney.common.init.BlockInit;

public class BuddingUnityBlock extends Block
{
	public static final int GROWTH_CHANCE = 5;
	private static final Direction[] DIRECTIONS = Direction.values();
	
	public BuddingUnityBlock(BlockBehaviour.Properties properties)
	{
		super(properties);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		if(randomSource.nextInt(GROWTH_CHANCE) == 0)
		{
			Direction direction = DIRECTIONS[randomSource.nextInt(DIRECTIONS.length)];
			BlockPos crystalPos = pos.relative(direction);
			BlockState crystalState = level.getBlockState(crystalPos);
			Block crystalBlock = null;
			
			if(canClusterGrowAtState(crystalState))
				crystalBlock = BlockInit.SMALL_UNITY_BUD.get();
			
			else if(crystalState.is(BlockInit.SMALL_UNITY_BUD.get()) && crystalState.getValue(UnityClusterBlock.FACING) == direction)
				crystalBlock = BlockInit.MEDIUM_UNITY_BUD.get();
			
			else if(crystalState.is(BlockInit.MEDIUM_UNITY_BUD.get()) && crystalState.getValue(UnityClusterBlock.FACING) == direction)
				crystalBlock = BlockInit.LARGE_UNITY_BUD.get();
			
			else if(crystalState.is(BlockInit.LARGE_UNITY_BUD.get()) && crystalState.getValue(UnityClusterBlock.FACING) == direction)
				crystalBlock = BlockInit.UNITY_CLUSTER.get();
			
			if(crystalBlock != null)
			{
				BlockState newCrystalState = crystalBlock.defaultBlockState().setValue(UnityClusterBlock.FACING, direction).setValue(UnityClusterBlock.WATERLOGGED, crystalState.getFluidState().getType() == Fluids.WATER);
				level.setBlockAndUpdate(crystalPos, newCrystalState);
			}
			
		}
	}
	
	public static boolean canClusterGrowAtState(BlockState state)
	{
		return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8;
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.DESTROY;
	}
}
