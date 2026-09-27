package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ZPMEnergyProvider;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.config.SyncedConfig;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	private static final String ENERGY = "Energy";
	private static final String ENTROPY = "Entropy";
	
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
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return CommonZPMConfig.zpm_has_energy_capability.get() ? new ZPMEnergyProvider(stack) : super.initCapabilities(stack, tag);
	}
	
	public static void setEntropy(ItemStack stack, int entropy)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return;
		
		CompoundTag tag = stack.getOrCreateTag();
		// WARNING: Weird stuff can happen if you try to use putInt(), so just don't
		tag.put(ENTROPY, IntTag.valueOf(entropy));
	}
		
	public static int getEntropy(ItemStack stack)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return 0;
		
		if(stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if(tag.get(ENTROPY) instanceof IntTag intTag)
				// WARNING: Weird stuff can happen if you try to use getInt(), so just don't
				return intTag.getAsInt();
		}
		
		return 0;
	}
	
	public static void setEnergy(ItemStack stack, long energy)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return;
		
		CompoundTag tag = stack.getOrCreateTag();
		// WARNING: Weird stuff can happen if you try to use putLong(), so just don't
		tag.put(ENERGY, LongTag.valueOf(energy));
	}
	
	public static long getEnergy(ItemStack stack)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return 0;
		
		if(stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			// WARNING: Weird stuff can happen if you try to use getLong(), so just don't
			if(tag.get(ENERGY) instanceof LongTag longTag)
				return longTag.getAsLong();
			// WARNING: Weird stuff can happen if you try to use getInt(), so just don't
			else if(tag.get(ENERGY) instanceof IntTag intTag)
				return intTag.getAsInt();
		}
		
		return SyncedConfig.zpm_energy_per_entropy_level.get();
	}
	
	public static boolean hasEnergy(ItemStack stack)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return false;
		
		return getEntropy(stack) < SyncedConfig.zpm_max_entropy.get() || getEnergy(stack) > 0;
	}
	
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced)
	{
		int entropy = getEntropy(stack);
		long remainingEnergy = getEnergy(stack);
		
		float currentEntropy = (float) entropy * 100 / SyncedConfig.zpm_max_entropy.get();
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.zpm.entropy").append(Component.literal(": " + currentEntropy + "%")).withStyle(ChatFormatting.GOLD));
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + ZeroPointEnergy.zeroPointEnergyToString(entropy, remainingEnergy))).withStyle(ChatFormatting.DARK_RED));
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.zpm.description"));
    	
    	super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
