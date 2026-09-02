package net.povstalec.sgjourney.common.blocks.zpm;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractZPMEnergyHolderBlock extends AbstractZPMHolderBlock
{
	public AbstractZPMEnergyHolderBlock(Properties properties)
	{
		super(properties);
	}
	
	public abstract long getMaxEnergyTransfer();
	
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_transfer").append(Component.literal(": " + SGJourneyEnergy.energyToString(getMaxEnergyTransfer()) + "/t")).withStyle(ChatFormatting.RED));
	}
}
