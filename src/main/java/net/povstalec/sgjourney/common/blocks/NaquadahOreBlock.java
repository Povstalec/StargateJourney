package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.function.Supplier;

public class NaquadahOreBlock extends ExplosiveBlock
{
	protected Supplier<Block> naquadriaBlock;
	
	public NaquadahOreBlock(Properties properties, Supplier<Block> naquadriaBlock, float radius)
	{
		super(properties, radius);
		
		this.naquadriaBlock = naquadriaBlock;
		this.registerDefaultState(this.stateDefinition.any().setValue(UNSTABLE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(UNSTABLE);
	}
	
	public void transform(ServerLevel level, BlockPos pos, int excitement)
	{
		level.setBlock(pos, naquadriaBlock.get().defaultBlockState().setValue(NaquadriaOreBlock.EXCITEMENT, excitement), Block.UPDATE_ALL);
	}
}
