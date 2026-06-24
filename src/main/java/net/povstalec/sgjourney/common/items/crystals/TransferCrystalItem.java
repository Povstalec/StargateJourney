package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonCrystalConfig;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
	
	public static CompoundTag tagSetup(long maxTransfer)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putLong(TRANSFER_LIMIT, maxTransfer);
		
		return tag;
	}
	
	public long getMaxTransfer()
	{
		return CommonCrystalConfig.transfer_crystal_max_transfer.get();
	}
	
	public int getEfficiencyMultiplier()
	{
		return DEFAULT_EFFICIENCY_MULTIPLIER;
	}
	
	public static long getMaxTransfer(ItemStack stack)
	{
		if(stack.getItem() instanceof TransferCrystalItem crystal)
		{
			long maxTransfer;
			CompoundTag tag = stack.getOrCreateTag();
			
			if(!tag.contains(TRANSFER_LIMIT))
				tag.putLong(TRANSFER_LIMIT, crystal.getMaxTransfer());

			if(tag.getTagType(TRANSFER_LIMIT) == Tag.TAG_INT) // TODO This is here for legacy reasons because it was originally an int
				maxTransfer = tag.getInt(TRANSFER_LIMIT);
			else
				maxTransfer = tag.getLong(TRANSFER_LIMIT);
			
			return maxTransfer;
		}
		
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		long maxEnergyTransfer = getMaxTransfer(stack);
		
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
