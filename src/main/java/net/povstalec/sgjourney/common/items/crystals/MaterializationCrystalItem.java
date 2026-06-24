package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class MaterializationCrystalItem extends AbstractCrystalItem
{
	public MaterializationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public final CrystalCache.Type getType()
	{
		return CrystalCache.Type.MATERIALIZATION;
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.materialization_crystal.description"));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    public static class Advanced extends MaterializationCrystalItem
    {
    	public Advanced(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
    }
}
