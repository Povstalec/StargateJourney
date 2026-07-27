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
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class ExplosiveBlock extends Block
{
	public static final BooleanProperty UNSTABLE = BooleanProperty.create("unstable");
	
	protected final float radius;
	
	public ExplosiveBlock(Properties properties, float radius)
	{
		super(properties);
		this.radius = radius;
	}
	
	@Override
	public boolean canDropFromExplosion(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return false;
    }
	
	/**
	 * Causes the block to blow up in a few ticks
	 * @param state BlockState the block is in
	 * @param level Level the block is in
	 * @param pos BlockPos of the block
	 */
	public void setUnstable(BlockState state, Level level, BlockPos pos)
	{
		level.setBlock(pos, state.setValue(UNSTABLE, true), Block.UPDATE_ALL);
		level.scheduleTick(pos, this, level.getRandom().nextInt(1, 4));
	}
	
	@Override
	public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion)
	{
		if(!level.isClientSide() && !state.getValue(UNSTABLE))
			setUnstable(state, level, pos);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource)
	{
		if(state.getValue(UNSTABLE))
		{
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
			level.explode(null, pos.getX(), pos.getY(), pos.getZ(), radius, Level.ExplosionInteraction.BLOCK);
		}
	}
}
