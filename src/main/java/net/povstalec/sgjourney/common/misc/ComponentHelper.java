package net.povstalec.sgjourney.common.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;

public class ComponentHelper
{
	public static MutableComponent energy(String name, long energy, long maxEnergy)
	{
		return Component.translatable(name).append(": " + SGJourneyEnergy.energyToString(energy, maxEnergy)).withStyle(ChatFormatting.DARK_RED);
	}
	
	public static MutableComponent energy(long energy, long maxEnergy)
	{
		return energy("tooltip.sgjourney.energy", energy, maxEnergy);
	}
	
	public static MutableComponent energy(String name, long energy)
	{
		return Component.translatable(name).append(": " + SGJourneyEnergy.energyToString(energy)).withStyle(ChatFormatting.DARK_RED);
	}
	
	public static MutableComponent energy(long energy)
	{
		return energy("tooltip.sgjourney.energy", energy);
	}
	
	public static MutableComponent tickTimer(String name, int ticks, int maxTicks, ChatFormatting formatting)
	{
		if(maxTicks <= 0)
			return Component.translatable(name).append(": " + Conversion.ticksToString(ticks)).withStyle(formatting);
		
		return Component.translatable(name).append(": " + Conversion.ticksToString(ticks) + "/" + Conversion.ticksToString(maxTicks)).withStyle(formatting);
	}
	
	public static MutableComponent tickTimer(int ticks, int maxTicks, ChatFormatting formatting)
	{
		if(maxTicks <= 0)
			return Component.literal(Conversion.ticksToString(ticks)).withStyle(formatting);
		
		return Component.literal(Conversion.ticksToString(ticks) + "/" + Conversion.ticksToString(maxTicks)).withStyle(formatting);
	}
	
	public static MutableComponent description(MutableComponent component)
	{
		return component.withStyle(ChatFormatting.DARK_GRAY);
	}
	
	public static MutableComponent description(String name)
	{
		return description(Component.translatable(name));
	}
	
	public static MutableComponent usage(MutableComponent component)
	{
		return component.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC);
	}
	
	public static MutableComponent usage(String name)
	{
		return usage(Component.translatable(name));
	}
}
