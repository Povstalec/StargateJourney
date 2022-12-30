package net.povstalec.sgjourney.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ExplosiveBlock extends Block
{
	private final float radius;
	
	public ExplosiveBlock(Properties properties, float radius)
	{
		super(properties);
		this.radius = radius;
	}
	
	public void wasExploded(Level level, BlockPos pos, Explosion explosion)
	{
		if(!level.isClientSide)
		{
			level.explode(null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), radius, Level.ExplosionInteraction.BLOCK);
		}
	}
	
}
