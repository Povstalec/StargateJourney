package net.povstalec.sgjourney.common.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;

public class ComponentHelper
{
	public static MutableComponent energyComponent(String name, long energy, long maxEnergy)
	{
		return Component.translatable(name).append(Component.literal(": " + SGJourneyEnergy.energyToString(energy, maxEnergy))).withStyle(ChatFormatting.DARK_RED);
	}
	
	public static MutableComponent energy(long energy, long maxEnergy)
	{
		return energyComponent("tooltip.sgjourney.energy", energy, maxEnergy);
	}
	
	public static MutableComponent energyComponent(String name, long energy)
	{
		return Component.translatable(name).append(Component.literal(": " + SGJourneyEnergy.energyToString(energy))).withStyle(ChatFormatting.DARK_RED);
	}
	
	public static MutableComponent energy(long energy)
	{
		return energyComponent("tooltip.sgjourney.energy", energy);
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
