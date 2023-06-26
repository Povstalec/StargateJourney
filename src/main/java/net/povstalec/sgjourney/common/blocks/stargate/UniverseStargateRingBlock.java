package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class UniverseStargateRingBlock extends AbstractStargateRingBlock
{
	public UniverseStargateRingBlock(Properties properties)
	{
		super(properties, 7.0);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.UNIVERSE;
	}
	
	public Block getStargate()
	{
		return BlockInit.UNIVERSE_STARGATE.get();
	}
}
