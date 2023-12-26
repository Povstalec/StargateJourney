package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class TransferCrystalItem extends AbstractCrystalItem
{
	//TODO Change it from a placeholder values
	public static final int DEFAULT_MAX_TRANSFER = 2500;
	public static final int ADVANCED_MAX_TRANSFER = 5000;
	
	public static final String TRANSFER_LIMIT = "TransferLimit";
	
	public TransferCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public static CompoundTag tagSetup(int maxTransfer)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(TRANSFER_LIMIT, maxTransfer);
		
		return tag;
	}
	
	public int getMaxTransfer()
	{
		return DEFAULT_MAX_TRANSFER;
	}
	
	public static int getMaxTransfer(ItemStack stack)
	{
		if(stack.getItem() instanceof TransferCrystalItem crystal)
		{
			int maxTransfer;
			CompoundTag tag = stack.getOrCreateTag();
			
			if(!tag.contains(TRANSFER_LIMIT))
				tag.putInt(TRANSFER_LIMIT, crystal.getMaxTransfer());
			
			maxTransfer = tag.getInt(TRANSFER_LIMIT);
			
			return maxTransfer;
		}
		
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		int maxEnergyTransfer = getMaxTransfer(stack);
		
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_transfer").append(Component.literal(": " + maxEnergyTransfer + " FE")).withStyle(ChatFormatting.RED));
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	public static class Advanced extends TransferCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public int getMaxTransfer()
		{
			return ADVANCED_MAX_TRANSFER;
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
	}
}
