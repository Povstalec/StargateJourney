package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class NaquadahOreBlock extends ExplosiveBlock
{
	protected Supplier<Block> naquadriaBlock;
	
	public NaquadahOreBlock(Properties properties, Supplier<Block> naquadriaBlock, float radius)
	{
		super(properties, radius);
		
		this.naquadriaBlock = naquadriaBlock;
	}
	
	public void transform(ServerLevel level, BlockPos pos, int excitement)
	{
		level.setBlock(pos, naquadriaBlock.get().defaultBlockState().setValue(NaquadriaOreBlock.EXCITEMENT, excitement), 3);
	}
}
