package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;

public class NaquadriaOreBlock extends ExplosiveBlock
{
	public static final float RADIATION_CHANCE = 1F / 16F;
	public static final float SPREAD_CHANCE = 1F / 128F;
	
	public static final int MIN_EXCITEMENT = 0;
	public static final int MAX_EXCITEMENT = 15;
	
	public static final IntegerProperty EXCITEMENT =  IntegerProperty.create("excitement", MIN_EXCITEMENT, MAX_EXCITEMENT);
	
	public NaquadriaOreBlock(Properties properties, float radius)
	{
		super(properties, radius);
		this.registerDefaultState(this.stateDefinition.any().setValue(EXCITEMENT, MIN_EXCITEMENT).setValue(UNSTABLE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(EXCITEMENT).add(UNSTABLE);
	}
	
	public void excite(BlockState state, ServerLevel level, BlockPos pos)
	{
		if(state.getValue(EXCITEMENT) < MAX_EXCITEMENT)
			level.setBlock(pos, state.setValue(EXCITEMENT, state.getValue(EXCITEMENT) + 1), 3);
		else
			setUnstable(state, level, pos);
	}
	
	public void exciteNearbyBlocks(ServerLevel level, BlockPos pos, RandomSource randomSource, int excitement)
	{
		AABB aabb = new AABB(pos).inflate(excitement);
		BlockPos.betweenClosedStream(aabb).forEach(otherPos ->
		{
			if(randomSource.nextFloat() < SPREAD_CHANCE && !otherPos.equals(pos))
			{
				BlockState otherState = level.getBlockState(otherPos);
				if(otherState.getBlock() instanceof NaquadriaOreBlock naquadriaOre)
					naquadriaOre.excite(otherState, level, pos);
				else if(otherState.getBlock() instanceof NaquadahOreBlock naquadahOre)
					naquadahOre.transform(level, otherPos, randomSource.nextInt(3 * excitement / 4, excitement));
			}
		});
	}
	
	public void releaseEnergy(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		//TODO Blue Ice -> Packed Ice -> Ice -> Water -> Nothing
		
		exciteNearbyBlocks(level, pos, randomSource, state.getValue(EXCITEMENT) + 1);
		
		level.setBlock(pos, state.setValue(EXCITEMENT, state.getValue(EXCITEMENT) - 1), 3);
	}
	
	@Override
	public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
	{
		return false;
	}
	
	@Override
	public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion)
	{
		if(level.isClientSide())
			return;
		
		exciteNearbyBlocks((ServerLevel) level, pos, level.getRandom(), 16);
		
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		if(randomSource.nextFloat() <= RADIATION_CHANCE * (state.getValue(EXCITEMENT) + 1))
			releaseEnergy(state, level, pos, randomSource);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state)
	{
		return state.getValue(EXCITEMENT) > 0;
	}
}
