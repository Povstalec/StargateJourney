package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

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
	
	private static final String FREQUENCY = "frequency";
	
	public CommunicationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public static boolean hasFrequency(ItemStack stack)
	{
		if(stack.hasTag())
			return stack.getTag().contains(FREQUENCY);
		
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
	public Optional<Component> descriptionInDHD(ItemStack stack)
	{
		if(!hasFrequency(stack))
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.communication.range", getRangeIncrease()).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		else
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.communication.network").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	MutableComponent description = Component.translatable("tooltip.sgjourney.communication_crystal.frequency").append(": ").withStyle(ChatFormatting.GRAY);
        if(!hasFrequency(stack))
            tooltipComponents.add(description.append(Component.translatable("tooltip.sgjourney.crystal.none").withStyle(ChatFormatting.GRAY)));
        else
        	tooltipComponents.add(description.append(Component.literal(Integer.toString(getFrequency(stack))).withStyle(ChatFormatting.GRAY)));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
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
