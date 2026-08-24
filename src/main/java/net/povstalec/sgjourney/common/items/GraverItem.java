package net.povstalec.sgjourney.common.items;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.neoforged.neoforge.common.ItemAbility;

public class GraverItem extends TieredItem
{
	public static final ItemAbility GRAVER_ENGRAVE = ItemAbility.get("graver_engrave");
	
	public GraverItem(Tier tier, Properties properties)
	{
		super(tier, properties);
	}
}
