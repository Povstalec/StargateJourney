package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CommunicationCrystalItem extends AbstractCrystalItem
{
	public static final int DEFAULT_RANGE_INCREASE = 16;
	public static final int ADVANCED_RANGE_INCREASE = 32;
	
	public static final int DEFAULT_FREQUENCY = 0;
	
	public static final String FREQUENCY = "frequency";
	
	public CommunicationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public final CrystalCache.Type getType()
	{
		return CrystalCache.Type.COMMUNICATION;
	}
	
	public static boolean containsFrequency(@NotNull CompoundTag tag)
	{
		return tag.contains(FREQUENCY);
	}
	
	public static boolean hasFrequency(ItemStack stack)
	{
		if(stack.hasTag())
			return containsFrequency(stack.getTag());
		
		return false;
	}
	
	public static int getFrequency(ItemStack stack)
	{
		if(hasFrequency(stack))
			return stack.getTag().getInt(FREQUENCY);
		
		return DEFAULT_FREQUENCY;
	}
	
	public static void setFrequency(ItemStack stack, int frequency)
	{
		stack.getOrCreateTag().putInt(FREQUENCY, frequency);
	}
	
	public static void unsetFrequency(ItemStack stack)
	{
		if(stack.hasTag())
			stack.getTag().remove(FREQUENCY);
	}
	
	public static CompoundTag tagSetup(int frequency)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(FREQUENCY, frequency);
		
		return tag;
	}
	
	public int getRangeIncrease()
	{
		return DEFAULT_RANGE_INCREASE;
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	boolean hasFrequency = hasFrequency(stack);
		
        if(!hasFrequency)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.communication_crystal.frequency_none").withStyle(ChatFormatting.GRAY));
        else
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.communication_crystal.frequency").append(": " + getFrequency(stack)).withStyle(ChatFormatting.GRAY));
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.communication_crystal.communication_range_increase", !hasFrequency ? getRangeIncrease() : 0));
		
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.communication_crystal.description"));
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.communication_crystal.crystal_computer"));
    }
    
    public static class Advanced extends CommunicationCrystalItem
    {
		public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public int getRangeIncrease()
		{
			return ADVANCED_RANGE_INCREASE;
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
    }
}
