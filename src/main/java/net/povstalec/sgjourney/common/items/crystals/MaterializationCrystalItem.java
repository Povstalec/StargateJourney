package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class MaterializationCrystalItem extends AbstractCrystalItem
{
	public static final int DEFAULT_RANGE_INCREMENT = 2;
	public static final int ADVANCED_RANGE_INCREMENT = 4;
	
	private static final String CRYSTAL_MODE = "CrystalMode";
	
	public MaterializationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public enum CrystalMode
	{
		INCREASE_RANGE,
		ENABLE_INTERDIMENSIONAL_TRANSPORT;
	}
	
	public static CompoundTag tagSetup(CrystalMode crystalMode)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString(CRYSTAL_MODE, crystalMode.toString().toUpperCase());
		
		return tag;
	}
	
	public static CrystalMode getCrystalMode(ItemStack stack)
	{
		CrystalMode mode;
		CompoundTag tag = stack.getOrCreateTag();
		
		if(!tag.contains(CRYSTAL_MODE))
			tag.putString(CRYSTAL_MODE, CrystalMode.INCREASE_RANGE.toString().toUpperCase());
		
		mode = CrystalMode.valueOf(tag.getString(CRYSTAL_MODE));
		
		return mode;
	}
	
	public int getRangeIncrement()
	{
		return DEFAULT_RANGE_INCREMENT;
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        CrystalMode mode = getCrystalMode(stack);
        String text = "";
        switch(mode)
        {
        case INCREASE_RANGE:
        	text = "tooltip.sgjourney.materialization_crystal.increased_range";
        	break;
        case ENABLE_INTERDIMENSIONAL_TRANSPORT:
        	text = "tooltip.sgjourney.materialization_crystal.interdimensional";
        	break;
        }
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.mode").append(Component.literal(": ")).append(Component.translatable(text)).withStyle(ChatFormatting.DARK_AQUA));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    public static class Advanced extends MaterializationCrystalItem
    {
    	public Advanced(Properties properties)
		{
			super(properties);
		}

		@Override
    	public int getRangeIncrement()
    	{
    		return ADVANCED_RANGE_INCREMENT;
    	}
    }
}
