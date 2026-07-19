package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class EnergyCrystalItem extends AbstractCrystalItem
{
	public EnergyCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public final CrystalCache.Type getType()
	{
		return CrystalCache.Type.ENERGY;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !StargateJourneyConfig.disable_energy_use.get() && getEnergy(stack) > 0;
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
	
	public static long getEnergy(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ENERGY, 0L);
	}
	
	public long getCapacity()
	{
		return CommonCrystalConfig.energy_crystal_capacity.get();
	}
	
	public long getTransfer()
	{
		return CommonCrystalConfig.energy_crystal_max_transfer.get();
	}
	
	public long energyTargetIncrease()
	{
		return CommonDHDConfig.energy_crystal_energy_target_increase.get();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + SGJourneyEnergy.energyToString(getEnergy(stack), getCapacity()))).withStyle(ChatFormatting.DARK_RED));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_crystal.energy_target_increase").append(Component.literal(": " + SGJourneyEnergy.energyToString(energyTargetIncrease()))).withStyle(ChatFormatting.RED));
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.energy_crystal.description"));
	}
	
	public static final class Advanced extends EnergyCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public long getCapacity()
		{
			return CommonCrystalConfig.advanced_energy_crystal_capacity.get();
		}
		
		@Override
		public long energyTargetIncrease()
		{
			return CommonDHDConfig.advanced_energy_crystal_energy_target_increase.get();
		}

		@Override
		public long getTransfer()
		{
			return CommonCrystalConfig.advanced_energy_crystal_max_transfer.get();
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
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
			if(stack.getItem() instanceof EnergyCrystalItem energyCrystal)
				return energyCrystal.getTransfer();
			
			return 0;
		}
		
		@Override
		public long maxExtract()
		{
			if(stack.getItem() instanceof EnergyCrystalItem energyCrystal)
				return energyCrystal.getTransfer();
			
			return 0;
		}
		
		@Override
		public long loadEnergy(ItemStack stack)
		{
			return getEnergy(stack);
		}
		
		@Override
		public long getTrueMaxEnergyStored()
		{
			if(stack.getItem() instanceof EnergyCrystalItem energyCrystal)
				return energyCrystal.getCapacity();
			
			return 0;
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			stack.set(DataComponentInit.ENERGY, this.energy);
		}
	}
}
