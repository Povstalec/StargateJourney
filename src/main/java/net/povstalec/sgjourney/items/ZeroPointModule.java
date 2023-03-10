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
import net.povstalec.sgjourney.config.ServerEnergyConfig;

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
	
	public static final int maxEntropy = 1000;
	public static final long maxEnergy = ServerEnergyConfig.zpm_energy_per_level_of_entropy.get();
	
	public ZeroPointModule(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		int entropy = 0;
		long extractedEnergy = 0L;
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains("Entropy"))
			entropy = tag.getInt("Entropy");
		
		if(tag.contains("ExtractedEnergy"))
			extractedEnergy = tag.getLong("ExtractedEnergy");
		
		float currentEntropy = (float) entropy * 100 / maxEntropy;
		long remainingEnergy = maxEnergy - extractedEnergy;
		
    	tooltipComponents.add(Component.literal("Entropy: " + currentEntropy + "%").withStyle(ChatFormatting.GOLD));
    	tooltipComponents.add(Component.literal("Remaining Energy: " + remainingEnergy + " FE").withStyle(ChatFormatting.DARK_RED));
    	
    	super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	public static long extractEnergy(ItemStack stack, long energy)
	{
		if(!isZPM(stack) || !hasEnergy(stack))
			return 0;
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains("ExtractedEnergy"))
			tag.putLong("ExtractedEnergy", 0);
		
		long extractedEnergy = tag.getLong("ExtractedEnergy");
		
		extractedEnergy += energy;
		
		if(extractedEnergy > maxEnergy)
		{
			extractedEnergy -= maxEnergy;
			increaseEntropy(stack);
		}

		tag.putLong("ExtractedEnergy", extractedEnergy);
		stack.setTag(tag);
		return energy;
	}
	
	private static void increaseEntropy(ItemStack stack)
	{
		CompoundTag tag = stack.getOrCreateTag();
		
		int entropy = tag.getInt("Entropy");
		entropy++;
		tag.putInt("Entropy", entropy);
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
		
		if(tag.contains("Entropy"))
			return tag.getInt("Entropy") < 1000;
		else
		{
			tag.putInt("Entropy", 0);
			stack.setTag(tag);
			return true;
		}
	}
	
	public static boolean isNearingEntropy(ItemStack stack)
	{
		if(!isZPM(stack) || !hasEnergy(stack))
			return true;
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains("Entropy"))
			return tag.getInt("Entropy") >= 999;
		else
		{
			tag.putInt("Entropy", 0);
			stack.setTag(tag);
			return false;
		}
	}
	
	public static long getEnergyInLevel(ItemStack stack)
	{
		if(!isZPM(stack) || !hasEnergy(stack))
			return 0;
		
		long extractedEnergy = 0L;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains("ExtractedEnergy"))
			extractedEnergy = tag.getLong("ExtractedEnergy");
		
		long remainingEnergy = maxEnergy - extractedEnergy;
		
		return remainingEnergy;
	}
}
