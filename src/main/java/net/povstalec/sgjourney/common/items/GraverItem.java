package net.povstalec.sgjourney.common.items;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Vanishable;
import net.minecraftforge.common.ToolAction;

public class GraverItem extends TieredItem implements Vanishable
{
	public static final ToolAction GRAVER_ENGRAVE = ToolAction.get("graver_engrave");
	
	public GraverItem(Tier tier, Properties properties)
	{
		super(tier, properties);
	}
}
