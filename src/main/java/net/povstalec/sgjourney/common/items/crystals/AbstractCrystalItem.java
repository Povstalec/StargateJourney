package net.povstalec.sgjourney.common.items.crystals;

import net.minecraft.world.item.Item;

public abstract class AbstractCrystalItem extends Item
{
	public AbstractCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public boolean isLarge()
	{
		return false;
	}
	
	public boolean isAdvanced()
	{
		return false;
	}
	
	public boolean isRegular()
	{
		return !isAdvanced() && !isLarge();
	}
}
