package net.povstalec.sgjourney.blocks.stargate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.block_entities.PegasusStargateEntity;
import net.povstalec.sgjourney.init.BlockInit;

public class PegasusStargateBlock extends AbstractStargateBlock
{
	public PegasusStargateBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		PegasusStargateEntity stargate = new PegasusStargateEntity(pos, state);
		
		 return stargate;
	}
	
	public BlockState ringState()
	{
		return BlockInit.PEGASUS_RING.get().defaultBlockState();
	}

	public Block getStargate()
	{
		return BlockInit.PEGASUS_STARGATE.get();
	}
}
