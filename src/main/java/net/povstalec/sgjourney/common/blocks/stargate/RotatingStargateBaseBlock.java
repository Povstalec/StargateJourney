package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.RotatingStargateEntity;
import net.povstalec.sgjourney.common.blockstates.StargatePart;

public abstract class RotatingStargateBaseBlock extends AbstractStargateBaseBlock
{
	public RotatingStargateBaseBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties, width, horizontalOffset);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide())
			return;
		
		boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
		
		BlockEntity blockentity = level.getBlockEntity(pos);
		
		if(blockentity instanceof RotatingStargateEntity stargate)
		{
			if(hasSignal)
				stargate.updateSignal(StargatePart.BASE, level.getBestNeighborSignal(pos));
			else
				stargate.updateSignal(StargatePart.BASE, 0);
		}
	}
}
