package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.init.DataComponentInit;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CommunicationCrystalItem extends AbstractCrystalItem
{
	public static final int DEFAULT_MAX_DISTANCE = 16;
	public static final int ADVANCED_MAX_DISTANCE = 32;
	
	public static final int DEFAULT_FREQUENCY = 0;
	
	public CommunicationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public int getFrequency(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.FREQUENCY, DEFAULT_FREQUENCY);
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	MutableComponent description = Component.translatable("tooltip.sgjourney.communication_crystal.frequency").append(Component.literal(": ")).withStyle(ChatFormatting.GRAY);
        int frequency = getFrequency(stack);
        if(frequency == DEFAULT_FREQUENCY)
            tooltipComponents.add(description.append(Component.translatable("tooltip.sgjourney.crystal.none").withStyle(ChatFormatting.GRAY)));
        else
        	tooltipComponents.add(description.append(Component.literal("" + frequency).withStyle(ChatFormatting.GRAY)));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
