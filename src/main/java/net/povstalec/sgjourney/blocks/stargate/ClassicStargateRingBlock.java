package net.povstalec.sgjourney.blocks.stargate;

import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.init.BlockInit;

public class ClassicStargateRingBlock extends AbstractStargateRingBlock
{
	public ClassicStargateRingBlock(Properties properties)
	{
		super(properties);
	}

	public Block getStargate()
	{
		return BlockInit.CLASSIC_STARGATE.get();
	}
}
