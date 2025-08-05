package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;

import java.util.List;

public class BatteryItem extends Item
{
	public static final String ENERGY = "energy";
	
	public BatteryItem(Item.Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getEnergy(stack) / getCapacity());
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getEnergy(stack) / getCapacity());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	public static CompoundTag tagSetup(int energy)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(ENERGY, energy);
		
		return tag;
	}
	
	public static long getEnergy(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ENERGY, 0L);
	}
	
	public long getCapacity()
	{
		return CommonTechConfig.small_naquadah_battery_capacity.get();
	}
	
	public long getTransfer()
	{
		return CommonTechConfig.small_naquadah_battery_max_transfer.get();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + SGJourneyEnergy.energyToString(getEnergy(stack), getCapacity()))).withStyle(ChatFormatting.DARK_RED));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	
	
	public static class Energy extends SGJourneyEnergy.Item
	{
		public Energy(ItemStack stack)
		{
			super(stack, 0, 0, 0);
		}
		
		@Override
		public long maxReceive()
		{
			if(stack.getItem() instanceof BatteryItem battery)
				return battery.getTransfer();
			
			return 0;
		}
		
		@Override
		public long maxExtract()
		{
			if(stack.getItem() instanceof BatteryItem battery)
				return battery.getTransfer();
			
			return 0;
		}
		
		@Override
		public long loadEnergy(ItemStack stack)
		{
			return stack.getOrDefault(DataComponentInit.ENERGY, 0L);
		}
		
		@Override
		public long getTrueMaxEnergyStored()
		{
			if(stack.getItem() instanceof BatteryItem battery)
				return battery.getCapacity();
			
			return 0;
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			stack.set(DataComponentInit.ENERGY, this.energy);
		}
	}
}
