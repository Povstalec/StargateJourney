package net.povstalec.sgjourney.common.items;

import java.util.List;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
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
	
	//TODO Turn off filtering if correct signal is received
	//TODO Maybe let the CFD specify if it should redirect to a random Stargate, specific Stargate or back to the dialing Stargate?
	//TODO Filter based on held items
	//TODO Activate filtering when specific addresses connect (those could be drawn from Stargate's whitelist/blacklist)
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		ItemStack stack = player.getItemInHand(usedHand);
		
		player.displayClientMessage(Component.literal("Work In Progress").withStyle(ChatFormatting.DARK_RED), true); //TODO Add filter interface GUI
		
		return InteractionResultHolder.success(stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.call_forwarding_device.usage"));
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.call_forwarding_device.description"));

		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
