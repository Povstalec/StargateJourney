package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.item.Item;
import net.povstalec.sgjourney.common.init.BlockInit;

public class UniverseStargateRingBlock extends RotatingStargateRingBlock
{
	public UniverseStargateRingBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}
	
	@Override
	public Item asItem()
	{
		return BlockInit.UNIVERSE_STARGATE.get().asItem();
	}
}
