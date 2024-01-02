package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public abstract class AbstractCrystalItem extends Item
{
	public AbstractCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public boolean isLarge()
	{
		return false;
	}
	
	public boolean isAdvanced()
	{
		return false;
	}
	
	public boolean isRegular()
	{
		return !isAdvanced() && !isLarge();
	}
	
	public Optional<Component> descriptionInDHD()
	{
		return Optional.empty();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(descriptionInDHD().isPresent())
		{
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.crystal.in_dhd").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.ITALIC));
			tooltipComponents.add(descriptionInDHD().get());
		}
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
