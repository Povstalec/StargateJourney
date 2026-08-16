package net.povstalec.sgjourney.common.items.energy_cores;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FusionCoreItem extends Item implements IEnergyCore
{
	public FusionCoreItem(Properties properties)
	{
		super(properties);
	}
	
	private static boolean showEnergy()
	{
		return !StargateJourneyConfig.disable_energy_use.get() && !CommonTechConfig.fusion_core_infinite_energy.get();
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return showEnergy();
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getFuel(stack) / getMaxFuel());
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		return 61183;
	}
	
	public static int getFuel(ItemStack energyCore)
	{
		return energyCore.getOrDefault(DataComponentInit.FUSION_FUEL, getMaxFuel());
	}
	
	public static int getMaxFuel()
	{
		return CommonTechConfig.fusion_core_fuel_capacity.get();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		if(showEnergy())
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.fusion_core.fuel").append(Component.literal(": " + getFuel(stack) + " / " + getMaxFuel())).withStyle(ChatFormatting.AQUA));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.fusion_core.description").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	
	
	@Override
	public long maxGeneratedEnergy(ItemStack energyCore, ItemStack input)
	{
		return CommonTechConfig.fusion_core_energy_from_fuel.get();
	}
	
	@Override
	public long generateEnergy(ItemStack energyCore, ItemStack input)
	{
		if(CommonTechConfig.fusion_core_infinite_energy.get())
			return maxGeneratedEnergy(energyCore, input);
		
		int fuel = getFuel(energyCore);
		if(fuel > 0)
		{
			energyCore.set(DataComponentInit.FUSION_FUEL, fuel - 1);
			return maxGeneratedEnergy(energyCore, input);
		}
		else
			return 0;
	}
	
	public static ItemStack randomFusionCore(int minCapacity, int maxCapacity)
	{
		ItemStack fusionCore = new ItemStack(ItemInit.FUSION_CORE.get());
		Random random = new Random();
		
		fusionCore.set(DataComponentInit.FUSION_FUEL, random.nextInt(minCapacity, maxCapacity + 1));
		
		return fusionCore;
	}
}
