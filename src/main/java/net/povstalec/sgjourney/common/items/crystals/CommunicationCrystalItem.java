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
	public static final int DEFAULT_MAX_DISTANCE = 16;
	public static final int ADVANCED_MAX_DISTANCE = 32;
	
	public static final int DEFAULT_FREQUENCY = 0;
	
	private static final String FREQUENCY = "Frequency";
	
	public CommunicationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public int getFrequency(ItemStack stack)
	{
		int frequency;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(FREQUENCY))
			tag.putInt(FREQUENCY, DEFAULT_FREQUENCY);
		
		frequency = tag.getInt(FREQUENCY);
		
		return frequency;
	}
	
	public static CompoundTag tagSetup(int frequency)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(FREQUENCY, frequency);
		
		return tag;
	}
	
	public int getMaxDistance()
	{
		return DEFAULT_MAX_DISTANCE;
	}

	@Override
	public Optional<Component> descriptionInDHD(ItemStack stack)
	{
		return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.communication").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	MutableComponent description = Component.translatable("tooltip.sgjourney.communication_crystal.frequency").append(Component.literal(": ")).withStyle(ChatFormatting.GRAY);
        int frequency = getFrequency(stack);
        if(frequency == DEFAULT_FREQUENCY)
            tooltipComponents.add(description.append(Component.translatable("tooltip.sgjourney.crystal.none").withStyle(ChatFormatting.GRAY)));
        else
        	tooltipComponents.add(description.append(Component.literal("" + frequency).withStyle(ChatFormatting.GRAY)));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    public static class Advanced extends CommunicationCrystalItem
    {
		public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public int getMaxDistance()
		{
			return ADVANCED_MAX_DISTANCE;
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}

		@Override
		public Optional<Component> descriptionInDHD(ItemStack stack)
		{
			return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.communication.advanced").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		}
    }
}
