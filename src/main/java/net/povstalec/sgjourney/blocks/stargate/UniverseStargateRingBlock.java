package net.povstalec.sgjourney.blocks.stargate;

import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.init.BlockInit;

public class UniverseStargateRingBlock extends AbstractStargateRingBlock
{
	public UniverseStargateRingBlock(Properties properties)
	{
		super(properties);
	}

	public Block getStargate()
	{
		return BlockInit.UNIVERSE_STARGATE.get();
	}
}
