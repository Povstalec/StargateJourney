package net.povstalec.sgjourney.common.items;

import java.util.List;

import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;

public class ZeroPointModule extends Item
{
	/*
	 * My original idea was to make something ridiculously overpowered based on canon
	 * ZPM explosion could potentially destroy the Earth
	 * Gravitational binding energy of the Earth is 249 000 000 000 000 000 000 000 000 000 000 J
	 * Not even long has enough zeros to cover that
	 * Well, this is too overpowered, so I'll be changing it
	 * But I'll still leave some way for people to make it ridiculously strong
	 * 
	 * ZPM can't be recharged, so the energy can only ever go down
	 * 
	 * One level of Entropy corresponds to 0.1%
	 * 
	 * When Entropy reaches its max state, the ZPM is considered depleted
	 */

	private static final String ENERGY = "Energy";
	private static final String ENTROPY = "Entropy";
	
	public static final int MAX_ENTROPY = 1000;
	
	public ZeroPointModule(Properties properties)
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
		return Math.round(13.0F * (MAX_ENTROPY - (float) getEntropy(stack)) / MAX_ENTROPY);
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		return 16743680;
	}
	
	private static int getEntropy(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ENTROPY, 0);
	}
	
	public static long getEnergy(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ENERGY, CommonZPMConfig.zpm_energy_per_level_of_entropy.get());
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		int entropy = getEntropy(stack);
		long remainingEnergy = getEnergy(stack);
		
		float currentEntropy = (float) entropy * 100 / MAX_ENTROPY;
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.zpm.entropy").append(Component.literal(": " + currentEntropy + "%")).withStyle(ChatFormatting.GOLD));
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.zpm.energy_in_level").append(Component.literal(": " + remainingEnergy + " FE")).withStyle(ChatFormatting.DARK_RED));
    	
    	super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	public static long getMaxEnergy()
	{
		return CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	}
	
	public static long getMaxExtract()
	{
		return CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	}
	
	
	
	public static class Energy extends ZeroPointEnergy.Item
	{
		public Energy(ItemStack stack)
		{
			super(stack, MAX_ENTROPY, getMaxEnergy(), 0, getMaxExtract());
		}
		
		public long maxReceive()
		{
			return getMaxEnergy();
		}
		
		public long maxExtract()
		{
			return getMaxEnergy();
		}
		
		public long getTrueMaxEnergyStored()
		{
			return getMaxEnergy();
		}
	}
}
