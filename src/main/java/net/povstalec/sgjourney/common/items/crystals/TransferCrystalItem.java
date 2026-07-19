package net.povstalec.sgjourney.common.items.crystals;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

import java.util.List;

public class TransferCrystalItem extends AbstractCrystalItem
{
	public static final String TRANSFER_LIMIT = "transfer_limit";
	
	public static final int DEFAULT_EFFICIENCY_MULTIPLIER = 2;
	public static final int ADVANCED_EFFICIENCY_MULTIPLIER = 4;
	
	public TransferCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public final CrystalCache.Type getType()
	{
		return CrystalCache.Type.TRANSFER;
	}
	
	public long getMaxTransfer()
	{
		return CommonCrystalConfig.transfer_crystal_max_transfer.get();
	}
	
	public int getEfficiencyMultiplier()
	{
		return DEFAULT_EFFICIENCY_MULTIPLIER;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		long maxEnergyTransfer = getMaxTransfer();
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_transfer").append(Component.literal(": " + SGJourneyEnergy.energyToString(maxEnergyTransfer) + "/t")).withStyle(ChatFormatting.RED));
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.transfer_crystal.transfer_efficiency_multiplier").append(Component.literal(": " + getEfficiencyMultiplier())).withStyle(ChatFormatting.GOLD));
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.transfer_crystal.description"));
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
		public int getEfficiencyMultiplier()
		{
			return ADVANCED_EFFICIENCY_MULTIPLIER;
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
	}
}
