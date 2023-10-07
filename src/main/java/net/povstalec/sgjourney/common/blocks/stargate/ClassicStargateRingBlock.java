package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.world.item.Item;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class ClassicStargateRingBlock extends AbstractStargateRingBlock
{
	public ClassicStargateRingBlock(Properties properties)
	{
		super(properties, 8.0D, 0.0D);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.CLASSIC;
	}

    @Override
	public Item asItem()
	{
		return BlockInit.CLASSIC_STARGATE.get().asItem();
	}
}
