package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class TransferCrystalItem extends AbstractCrystalItem
{
	public TransferCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public long getMaxTransfer()
	{
		return CommonCrystalConfig.transfer_crystal_max_transfer.get();
	}

	@Override
	public Optional<Component> descriptionInDHD(ItemStack stack)
	{
		return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.transfer").append(Component.literal(" " + SGJourneyEnergy.energyToString(getMaxTransfer()))).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		long maxEnergyTransfer = getMaxTransfer();
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_transfer").append(Component.literal(": " + SGJourneyEnergy.energyToString(maxEnergyTransfer) + "/t")).withStyle(ChatFormatting.RED));
        
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
	
	public static class Advanced extends TransferCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public long getMaxTransfer()
		{
			return CommonCrystalConfig.advanced_transfer_crystal_max_transfer.get();
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
	}
}
