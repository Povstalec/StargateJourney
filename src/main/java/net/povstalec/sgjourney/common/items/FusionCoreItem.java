package net.povstalec.sgjourney.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;

public class FusionCoreItem extends Item
{
	public static final String FUEL = "fuel";
	
	public FusionCoreItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !StargateJourneyConfig.disable_energy_use.get();
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getFuel(stack) / getMaxFuel());
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		return 61183;
	}
	
	public static int getFuel(ItemStack stack)
	{
		int fuel;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(FUEL))
			tag.putInt(FUEL, getMaxFuel());
		
		fuel = tag.getInt(FUEL);
		
		return fuel;
	}
	
	public static int getMaxFuel()
	{
		return 256; //TODO Add max fuel config
	}
}
