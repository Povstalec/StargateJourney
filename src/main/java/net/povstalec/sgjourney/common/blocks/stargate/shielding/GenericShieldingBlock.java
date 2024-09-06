package net.povstalec.sgjourney.common.blocks.stargate.shielding;

import java.util.ArrayList;

import net.povstalec.sgjourney.common.blockstates.ShieldingPart;

public class GenericShieldingBlock extends AbstractShieldingBlock
{
	public GenericShieldingBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties, width, horizontalOffset);
	}
	public GenericShieldingBlock(Properties properties)
	{
		this(properties, 7.0D, 1.0D);
	}

	@Override
	public ArrayList<ShieldingPart> getShieldingParts()
	{
		return ShieldingPart.DEFAULT_PARTS;
	}
}
