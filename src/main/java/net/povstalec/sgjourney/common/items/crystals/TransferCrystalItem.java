package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;
import java.util.Optional;

import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.config.CommonTechConfig;

public class TransferCrystalItem extends AbstractCrystalItem
{
	public static final String TRANSFER_LIMIT = "TransferLimit";
	
	public TransferCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public static CompoundTag tagSetup(long maxTransfer)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putLong(TRANSFER_LIMIT, maxTransfer);
		
		return tag;
	}
	
	public long getMaxTransfer()
	{
		return CommonTechConfig.transfer_crystal_max_transfer.get();
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
	public Optional<Component> descriptionInDHD(ItemStack stack)
	{
		return Optional.of(Component.translatable("tooltip.sgjourney.crystal.in_dhd.transfer").append(Component.literal(" " + SGJourneyEnergy.energyToString(getMaxTransfer()))).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		long maxEnergyTransfer = getMaxTransfer(stack);
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_transfer").append(Component.literal(": " + SGJourneyEnergy.energyToString(maxEnergyTransfer) + "/t")).withStyle(ChatFormatting.RED));
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
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
			return CommonTechConfig.advanced_transfer_crystal_max_transfer.get();
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
	}
}
