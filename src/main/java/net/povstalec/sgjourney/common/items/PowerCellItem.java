package net.povstalec.sgjourney.common.items;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PowerCellItem extends FluidItem.Holder
{
	public PowerCellItem(Properties properties)
	{
		super(properties);
	}
	
	//TODO Energy buffer
	
	@Override
	public boolean isCorrectFluid(FluidStack fluidStack)
	{
		return false;
	}
	
	@Override
	public ItemStack getHeldItem(ItemStack holderStack)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isValidItem(ItemStack heldStack)
	{
		return false;
	}
	
	
	public static class Heavy extends PowerCellItem
	{
		public Heavy(Properties properties)
		{
			super(properties);
		}
	}
}
