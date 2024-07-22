package net.povstalec.sgjourney.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.ItemInit;

public class NaquadahFuelRodItem extends Item
{
	public static final String FUEL = "Fuel";
	
	public NaquadahFuelRodItem(Properties properties)
	{
		super(properties);
	}
	
	public static ItemStack fuelRodSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.NAQUADAH_FUEL_ROD.get());
		CompoundTag tag = stack.getOrCreateTag();
		
		tag.putLong(FUEL, CommonNaquadahGeneratorConfig.naquadah_rod_max_fuel.get());
		
		return stack;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getFuel(stack) / getMaxFuel());
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getFuel(stack) / getMaxFuel());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	public static long getFuel(ItemStack stack)
	{
		long fuel;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(FUEL))
			tag.putLong(FUEL, 0);
		
		fuel = tag.getLong(FUEL);
		
		return fuel;
	}
	
	public static void depleteFuel(ItemStack stack)
	{
		long fuel;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(FUEL))
			tag.putLong(FUEL, 0);
		
		fuel = tag.getLong(FUEL);
		
		fuel--;
		
		tag.putLong(FUEL, fuel);
	}
	
	public long getMaxFuel()
	{
		return CommonNaquadahGeneratorConfig.naquadah_rod_max_fuel.get();
	}
	
	
}
