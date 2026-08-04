package net.povstalec.sgjourney.common.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.init.FluidInit;

public class ComponentHelper
{
	public static MutableComponent energy(String name, long energy, long maxEnergy)
	{
		return new TranslatableComponent(name).append(": " + SGJourneyEnergy.energyToString(energy, maxEnergy)).withStyle(ChatFormatting.DARK_RED);
	}
	
	public static MutableComponent energy(long energy, long maxEnergy)
	{
		return energy("tooltip.sgjourney.energy", energy, maxEnergy);
	}
	
	public static MutableComponent energy(String name, long energy)
	{
		return new TranslatableComponent(name).append(": " + SGJourneyEnergy.energyToString(energy)).withStyle(ChatFormatting.DARK_RED);
	}
	
	public static MutableComponent energy(long energy)
	{
		return energy("tooltip.sgjourney.energy", energy);
	}
	
	public static MutableComponent tickTimer(String name, int ticks, ChatFormatting formatting)
	{
		return new TranslatableComponent(name).append(": " + Conversion.ticksToString(ticks)).withStyle(formatting);
	}
	
	public static MutableComponent tickTimer(String name, int ticks, int maxTicks, ChatFormatting formatting)
	{
		if(maxTicks <= 0)
			return tickTimer(name, ticks, formatting);
		
		return new TranslatableComponent(name).append(": " + Conversion.ticksToString(ticks) + "/" + Conversion.ticksToString(maxTicks)).withStyle(formatting);
	}
	
	public static MutableComponent tickTimer(int ticks, ChatFormatting formatting)
	{
		return new TextComponent(Conversion.ticksToString(ticks)).withStyle(formatting);
	}
	
	public static MutableComponent tickTimer(int ticks, int maxTicks, ChatFormatting formatting)
	{
		if(maxTicks <= 0)
			return tickTimer(ticks, formatting);
		
		return new TextComponent(Conversion.ticksToString(ticks) + "/" + Conversion.ticksToString(maxTicks)).withStyle(formatting);
	}
	
	public static MutableComponent description(MutableComponent component)
	{
		return component.withStyle(ChatFormatting.DARK_GRAY);
	}
	
	public static MutableComponent description(String name)
	{
		return description(new TranslatableComponent(name));
	}
	
	public static MutableComponent usage(MutableComponent component)
	{
		return component.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC);
	}
	
	public static MutableComponent usage(String name)
	{
		return usage(new TranslatableComponent(name));
	}
	
	public static ChatFormatting fluidComponentColor(Fluid fluid)
	{
		if(fluid == FluidInit.LIQUID_NAQUADAH_SOURCE.get())
			return ChatFormatting.GREEN;
		else if(fluid == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get())
			return ChatFormatting.DARK_GREEN;
		
		return ChatFormatting.WHITE;
	}
	
	public static Component unChangingFluidAmountComponent(String name, int amount, ChatFormatting formatting) // Fluid name is always displayed
	{
		return new TranslatableComponent(name).append(new TextComponent(": " + amount + " mB")).withStyle(formatting);
	}
	
	public static Component fluidAmountComponent(String name, int amount, ChatFormatting formatting)
	{
		if(amount == 0)
			return new TextComponent("0 mB").withStyle(formatting);
		else
			return unChangingFluidAmountComponent(name, amount, formatting);
	}
	
	public static Component unchangingFluidAmountComponent(String name, int amount, int tankCapacity, ChatFormatting formatting)
	{
		return new TranslatableComponent(name).append(new TextComponent(": " + amount + "/" + tankCapacity + " mB")).withStyle(formatting);
	}
	
	public static Component fluidAmountComponent(String name, int amount, int tankCapacity, ChatFormatting formatting)
	{
		if(amount == 0)
			return new TextComponent("0/" + tankCapacity + " mB").withStyle(formatting);
		else
			return unchangingFluidAmountComponent(name, amount, tankCapacity, formatting);
	}
	
	public static Component coordinate(double x, double y, double z)
	{
		return new TextComponent("[X: " + x + " Y: " + y + " Z: " + z + ']').withStyle(ChatFormatting.YELLOW);
	}
	
	public static Component coordinate(Vec3 vec)
	{
		return coordinate(vec.x(), vec.y(), vec.z());
	}
	
	public static Component coordinate(int x, int y, int z)
	{
		return new TextComponent("[X: " + x + " Y: " + y + " Z: " + z + ']').withStyle(ChatFormatting.YELLOW);
	}
	
	public static Component coordinate(Vec3i vec)
	{
		return coordinate(vec.getX(), vec.getY(), vec.getZ());
	}
}
