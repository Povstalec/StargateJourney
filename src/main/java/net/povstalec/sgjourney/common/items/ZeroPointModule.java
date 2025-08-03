package net.povstalec.sgjourney.common.items;

import java.util.List;

import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.common.capabilities.ZPMEnergyProvider;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.ItemInit;

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
	public boolean isBarVisible(ItemStack stack)
	{
		return !StargateJourneyConfig.disable_energy_use.get();
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (ZeroPointEnergy.MAX_ENTROPY - (float) getEntropy(stack)) / ZeroPointEnergy.MAX_ENTROPY);
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		return 16743680;
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ZPMEnergyProvider(stack) {};
	}
	
	private static int getEntropy(ItemStack stack)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return 0;
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains(ENTROPY, Tag.TAG_INT))
		{
			if(tag.get(ENTROPY) instanceof IntTag intTag)
				return intTag.getAsInt();
		}
		
		return 0;
	}
	
	public static long getEnergy(ItemStack stack)
	{
		if(!stack.is(ItemInit.ZPM.get()))
			return 0;
		
		CompoundTag tag = stack.getOrCreateTag();
		
		if(tag.contains(ENERGY, Tag.TAG_LONG))
		{
			if(tag.get(ENERGY) instanceof LongTag longTag)
				return longTag.getAsLong();
		}
		
		return ZeroPointEnergy.ENERGY_PER_ENTROPY_LEVEL;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		int entropy = getEntropy(stack);
		long remainingEnergy = getEnergy(stack);
		
		float currentEntropy = (float) entropy * 100 / ZeroPointEnergy.MAX_ENTROPY;
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.zpm.entropy").append(Component.literal(": " + currentEntropy + "%")).withStyle(ChatFormatting.GOLD));
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + ZeroPointEnergy.zeroPointEnergyToString(entropy, remainingEnergy))).withStyle(ChatFormatting.DARK_RED));
    	
    	super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
