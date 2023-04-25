package net.povstalec.sgjourney.common.items.crystals;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommunicationCrystalItem extends Item
{
	private static final String FREQUENCY = "Frequency";
	private final int distance;
	
	public CommunicationCrystalItem(Properties properties, int distance)
	{
		super(properties);
		this.distance = distance;
	}
	
	public int getFrequency(ItemStack stack)
	{
		int frequency;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(FREQUENCY))
			tag.putInt(FREQUENCY, 0);
		
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
		return this.distance;
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	MutableComponent description = Component.translatable("tooltip.sgjourney.communication_crystal.frequency").withStyle(ChatFormatting.GRAY);
        int frequency = getFrequency(stack);
        if(frequency == 0)
            tooltipComponents.add(description.append(Component.translatable("tooltip.sgjourney.crystal.none").withStyle(ChatFormatting.GRAY)));
        else
        	tooltipComponents.add(description.append(Component.literal("" + frequency).withStyle(ChatFormatting.GRAY)));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
