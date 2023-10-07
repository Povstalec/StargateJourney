package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.item.Item;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class UniverseStargateRingBlock extends AbstractStargateRingBlock
{
	public UniverseStargateRingBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.UNIVERSE;
	}
	
	@Override
	public Item asItem()
	{
		return BlockInit.UNIVERSE_STARGATE.get().asItem();
	}
}
