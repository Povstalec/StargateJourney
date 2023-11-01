package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.init.BlockInit;

public class MilkyWayStargateRingBlock extends AbstractStargateRingBlock
{
	public MilkyWayStargateRingBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}

	@Override
	public Item asItem()
	{
		return BlockInit.MILKY_WAY_STARGATE.get().asItem();
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide)
			return;
		
		boolean hasSignal = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
		
		BlockEntity blockentity = level.getBlockEntity(state.getValue(PART).getBaseBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION)));
		
		if(blockentity instanceof MilkyWayStargateEntity stargate)
		{
			if(hasSignal)
				stargate.updateSignal(state.getValue(PART), level.getBestNeighborSignal(pos));
			else
				stargate.updateSignal(state.getValue(PART), 0);
		}
	}
}
