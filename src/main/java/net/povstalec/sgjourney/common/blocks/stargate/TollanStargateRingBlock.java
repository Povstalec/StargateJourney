package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.init.BlockInit;

public class TollanStargateRingBlock extends AbstractStargateRingBlock
{
	public TollanStargateRingBlock(Properties properties)
	{
		super(properties);
	}

	public Block getStargate()
	{
		return BlockInit.TOLLAN_STARGATE.get();
	}
}
