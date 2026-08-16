package net.povstalec.sgjourney.common.items.crystals;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

import java.util.List;

public class MaterializationCrystalItem extends AbstractCrystalItem
{
	//TODO Free absolute range increase
	
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.materialization_crystal.description"));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
