package net.povstalec.sgjourney.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.config.CommonZPMConfig;

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
	 */

	private static final String ENTROPY = "Entropy";
	private static final String ENERGY = "Energy";
	
	public static final int maxEntropy = 1000;
	
	public ZeroPointModule(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		int entropy = 0;
		long remainingEnergy = getMaxEnergy();
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains(ENTROPY))
			entropy = tag.getInt(ENTROPY);
		
		if(tag.contains(ENERGY))
			remainingEnergy = tag.getLong(ENERGY);
		
		float currentEntropy = (float) entropy * 100 / maxEntropy;
		
    	tooltipComponents.add(Component.literal("Entropy: " + currentEntropy + "%").withStyle(ChatFormatting.GOLD));
    	tooltipComponents.add(Component.literal("Energy In Level: " + remainingEnergy + " FE").withStyle(ChatFormatting.DARK_RED));
    	
    	super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	public static long extractEnergy(ItemStack stack, long energy)
	{
		if(!isZPM(stack) || !hasEnergy(stack))
			return 0;
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(ENERGY))
			tag.putLong(ENERGY, getMaxEnergy());
		
		long remainingEnergy = tag.getLong(ENERGY);
		
		remainingEnergy -= energy;
		
		if(remainingEnergy <= 0)
		{
			remainingEnergy += getMaxEnergy();
			increaseEntropy(stack);
		}

		tag.putLong(ENERGY, remainingEnergy);
		stack.setTag(tag);
		return energy;
	}
	
	private static void increaseEntropy(ItemStack stack)
	{
		CompoundTag tag = stack.getOrCreateTag();
		
		int entropy = tag.getInt(ENTROPY);
		entropy++;
		tag.putInt(ENTROPY, entropy);
		stack.setTag(tag);
	}
	
	public static boolean isZPM(ItemStack stack)
	{
		if(stack.getItem() instanceof ZeroPointModule)
			return true;
		else
			return false;
	}
	
	public static boolean hasEnergy(ItemStack stack)
	{
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains(ENTROPY))
			return tag.getInt(ENTROPY) < 1000;
		else
		{
			tag.putInt(ENTROPY, 0);
			stack.setTag(tag);
			return true;
		}
	}
	
	public static long getMaxEnergy()
	{
		return CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	}
	
	public static boolean isNearingEntropy(ItemStack stack)
	{
		if(!isZPM(stack) || !hasEnergy(stack))
			return true;
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains(ENTROPY))
			return tag.getInt(ENTROPY) >= 999;
		else
		{
			tag.putInt(ENTROPY, 0);
			stack.setTag(tag);
			return false;
		}
	}
	
	public static long getEnergyInLevel(ItemStack stack)
	{
		if(!isZPM(stack) || !hasEnergy(stack))
			return 0;
		
		long remainingEnergy = 0L;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains(ENERGY))
			remainingEnergy = tag.getLong(ENERGY);
		
		return remainingEnergy;
	}
}
