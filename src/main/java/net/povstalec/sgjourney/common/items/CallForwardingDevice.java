package net.povstalec.sgjourney.common.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CallForwardingDevice extends Item
{
	public CallForwardingDevice(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.call_forwarding_device.in_dhd").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.call_forwarding_device.info").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
