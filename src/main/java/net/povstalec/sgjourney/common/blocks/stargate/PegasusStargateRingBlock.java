package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.init.BlockInit;

public class PegasusStargateRingBlock extends AbstractStargateRingBlock
{
	public PegasusStargateRingBlock(Properties properties)
	{
		super(properties);
	}

	public Block getStargate()
	{
		return BlockInit.PEGASUS_STARGATE.get();
	}
}
