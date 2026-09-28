package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.config.SyncedConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
	
	public ZeroPointModule(Properties properties)
	{
		super(properties);
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack)
	{
		return !StargateJourneyConfig.disable_energy_use.get();
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack)
	{
		return Math.round(13.0F * getFullPercentage(stack));
	}
	
	public static float getFullPercentage(ItemStack stack)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return 0;
		
		return (SyncedConfig.zpm_max_entropy.get() - (float) getEntropy(stack)) / SyncedConfig.zpm_max_entropy.get();
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack)
	{
		return 16743680;
	}
	
	public static void setEntropy(ItemStack stack, int entropy)
	{
		stack.set(DataComponentInit.ENTROPY, entropy);
	}
		
	public static int getEntropy(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ENTROPY, 0);
	}
	
	public static void setEnergy(ItemStack stack, long energy)
	{
		stack.set(DataComponentInit.ENERGY, energy);
	}
	
	public static long getEnergy(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ENERGY, CommonZPMConfig.zpm_energy_per_entropy_level.get());
	}
	
	public static boolean hasEnergy(ItemStack stack)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return false;
		
		return getEntropy(stack) < SyncedConfig.zpm_max_entropy.get() || getEnergy(stack) > 0;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		int entropy = getEntropy(stack);
		long remainingEnergy = getEnergy(stack);
		
		float currentEntropy = (float) entropy * 100 / SyncedConfig.zpm_max_entropy.get();
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.zpm.entropy").append(Component.literal(": " + currentEntropy + "%")).withStyle(ChatFormatting.GOLD));
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + ZeroPointEnergy.zeroPointEnergyToString(entropy, remainingEnergy))).withStyle(ChatFormatting.DARK_RED));
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.zpm.description"));
    	
    	super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	public static long getMaxEnergy()
	{
		return CommonZPMConfig.zpm_energy_per_entropy_level.get();
	}
	
	public static long getMaxExtract()
	{
		return CommonZPMConfig.zpm_energy_per_entropy_level.get();
	}
	
	
	
	public static class Energy extends ZeroPointEnergy.Item
	{
		public Energy(ItemStack stack)
		{
			super(stack, getMaxExtract());
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
