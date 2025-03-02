package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.config.CommonTechConfig;

public class EnergyCrystalItem extends AbstractCrystalItem
{
	public EnergyCrystalItem(Properties properties)
	{
		super(properties);
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
		return CommonTechConfig.energy_crystal_capacity.get();
	}
	
	public long getTransfer()
	{
		return CommonTechConfig.advanced_energy_crystal_max_transfer.get();
	}

	@Override
	public Optional<Component> descriptionInDHD(ItemStack stack)
	{
		return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.energy").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		long energy = getEnergy(stack);
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + energy +  "/" + getCapacity() + " FE")).withStyle(ChatFormatting.DARK_RED));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
			return CommonTechConfig.advanced_energy_crystal_capacity.get();
		}

		@Override
		public long getTransfer()
		{
			return CommonTechConfig.advanced_energy_crystal_max_transfer.get();
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}

		@Override
		public Optional<Component> descriptionInDHD(ItemStack stack)
		{
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.energy.advanced").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}
	}
	
	public static class Energy extends SGJourneyEnergy.Item
	{
		public Energy(ItemStack stack)
		{
			super(stack, 0, 0, 0);
		}
		
		public long maxReceive()
		{
			if(stack.getItem() instanceof EnergyCrystalItem energyCrystal)
				return energyCrystal.getTransfer();
			
			return 0;
		}
		
		public long maxExtract()
		{
			if(stack.getItem() instanceof EnergyCrystalItem energyCrystal)
				return energyCrystal.getTransfer();
			
			return 0;
		}
		
		public long getTrueMaxEnergyStored()
		{
			if(stack.getItem() instanceof EnergyCrystalItem energyCrystal)
				return energyCrystal.getCapacity();
			
			return 0;
		}
	}
}
