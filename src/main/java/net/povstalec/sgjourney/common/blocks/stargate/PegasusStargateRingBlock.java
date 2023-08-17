package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class PegasusStargateRingBlock extends AbstractStargateRingBlock
{
	public PegasusStargateRingBlock(Properties properties)
	{
		super(properties, 7.0);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.PEGASUS;
	}

	public Block getItem()
	{
		return BlockInit.PEGASUS_STARGATE.get();
	}
}
