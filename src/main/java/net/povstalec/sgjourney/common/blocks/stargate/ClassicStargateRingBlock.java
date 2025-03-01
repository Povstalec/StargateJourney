package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.item.Item;
import net.povstalec.sgjourney.common.init.BlockInit;

public class ClassicStargateRingBlock extends RotatingStargateRingBlock
{
	public ClassicStargateRingBlock(Properties properties)
	{
		super(properties, 8.0D, 0.0D);
	}

    @Override
	public Item asItem()
	{
		return BlockInit.CLASSIC_STARGATE.get().asItem();
	}
}
