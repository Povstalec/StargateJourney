package net.povstalec.sgjourney.common.blocks.zpm;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;

import java.util.List;

public abstract class AbstractZPMEnergyHolderBlock extends AbstractZPMHolderBlock
{
	public AbstractZPMEnergyHolderBlock(Properties properties)
	{
		super(properties);
	}
	
	public abstract long getMaxEnergyTransfer();
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_transfer").append(Component.literal(": " + SGJourneyEnergy.energyToString(getMaxEnergyTransfer()) + "/t")).withStyle(ChatFormatting.RED));
	}
}
