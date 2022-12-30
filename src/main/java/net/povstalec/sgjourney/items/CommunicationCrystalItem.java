package net.povstalec.sgjourney.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommunicationCrystalItem extends Item
{

	public CommunicationCrystalItem(Properties properties)
	{
		super(properties);
	}

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return stack.hasTag();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        if(stack.hasTag())
        {
            String location = stack.getTag().getString("frequency");
            tooltipComponents.add(Component.literal("Frequency: " + location).withStyle(ChatFormatting.WHITE));
        }
        else
        {
            tooltipComponents.add(Component.literal("Frequency: None").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
