package net.povstalec.sgjourney.common.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class TabInit
{
	public static final CreativeModeTab STARGATE_ITEMS = new CreativeModeTab("stargate_items")
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(ItemInit.NAQUADAH.get());
		}
	};
	
	public static final CreativeModeTab STARGATE_BLOCKS = new CreativeModeTab("stargate_blocks")
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(BlockInit.NAQUADAH_BLOCK.get());
		}
	};
}
