package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.item.Item;
import net.povstalec.sgjourney.common.init.BlockInit;

public class MilkyWayStargateRingBlock extends RotatingStargateRingBlock
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
}
