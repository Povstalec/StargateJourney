package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class MaterializationCrystalItem extends AbstractCrystalItem
{
	public static final int DEFAULT_RANGE_MULTIPLIER = 16;
	public static final int ADVANCED_RANGE_MULTIPLIER = 32;
	
	public MaterializationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public int getRangeMultiplier()
	{
		return DEFAULT_RANGE_MULTIPLIER;
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.materialization_crystal").withStyle(ChatFormatting.DARK_AQUA));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    public static class Advanced extends MaterializationCrystalItem
    {
    	public Advanced(Properties properties)
		{
			super(properties);
		}

		@Override
    	public int getRangeMultiplier()
    	{
    		return ADVANCED_RANGE_MULTIPLIER;
    	}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
    }
}
