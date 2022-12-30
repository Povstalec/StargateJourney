package net.povstalec.sgjourney.blocks.stargate;

import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.init.BlockInit;

public class MilkyWayStargateRingBlock extends AbstractStargateRingBlock
{
	public MilkyWayStargateRingBlock(Properties properties)
	{
		super(properties);
	}

	public Block getStargate()
	{
		return BlockInit.MILKY_WAY_STARGATE.get();
	}
}
