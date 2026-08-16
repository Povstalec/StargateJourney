package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

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
	
	public static int getFuel(ItemStack stack)
	{
		int fuel;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(FUEL))
			tag.putInt(FUEL, getMaxFuel());
		
		fuel = tag.getInt(FUEL);
		
		return fuel;
	}
	
	/**
	 * 
	 * @param stack
	 * @return false if there is no fuel left
	 */
	public static boolean depleteFuel(ItemStack stack)
	{
		int fuel;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(FUEL))
			tag.putInt(FUEL, 0);
		
		fuel = tag.getInt(FUEL);
		
		fuel--;
		
		tag.putInt(FUEL, fuel);
		
		if(fuel <= 0)
			return false;
		
		return true;
	}
	
	public static int getMaxFuel()
	{
		return CommonNaquadahGeneratorConfig.naquadah_rod_max_fuel.get();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.hasTag() && isAdvanced.isAdvanced())
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.naquadah_fuel_rod.fuel").append(Component.literal(": " + getFuel(stack) + " / " + getMaxFuel())).withStyle(ChatFormatting.GREEN));
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.naquadah_fuel_rod.description"));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	public static ItemStack randomFuelRod(int minCapacity, int maxCapacity)
	{
		ItemStack fusionCore = new ItemStack(ItemInit.NAQUADAH_FUEL_ROD.get());
		Random random = new Random();
		
		fusionCore.getOrCreateTag().putInt(FUEL, random.nextInt(minCapacity, maxCapacity + 1));
		
		return fusionCore;
	}
}
