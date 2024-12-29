package net.povstalec.sgjourney.common.items.crystals;

import java.util.List;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.init.DataComponentInit;

import javax.annotation.Nullable;

public class MaterializationCrystalItem extends AbstractCrystalItem
{
	public static final int DEFAULT_RANGE_INCREMENT = 2;
	public static final int ADVANCED_RANGE_INCREMENT = 4;
	
	public static final Codec CRYSTAL_MODE_CODEC = StringRepresentable.fromValues(() -> new CrystalMode[]{CrystalMode.INCREASE_RANGE, CrystalMode.DIMENSION_TRANSPORT});
	
	public MaterializationCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public enum CrystalMode implements StringRepresentable
	{
		INCREASE_RANGE("increase_range"),
		DIMENSION_TRANSPORT("dimension_transport");
		
		private final String name;
		
		private CrystalMode(String name)
		{
			this.name = name;
		}
		
		@Override
		public String getSerializedName()
		{
			return this.name;
		}
	}
	
	@Nullable
	public static CrystalMode getCrystalMode(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.MATERIALIZATION_CRYSTAL_MODE, CrystalMode.INCREASE_RANGE);
	}
	
	public int getRangeIncrement()
	{
		return DEFAULT_RANGE_INCREMENT;
	}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        CrystalMode mode = getCrystalMode(stack);
		
        String text = "";
        switch(mode)
        {
        case INCREASE_RANGE:
        	text = "tooltip.sgjourney.materialization_crystal.increased_range";
        	break;
        case DIMENSION_TRANSPORT:
        	text = "tooltip.sgjourney.materialization_crystal.interdimensional";
        	break;
        }
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.mode").append(Component.literal(": ")).append(Component.translatable(text)).withStyle(ChatFormatting.DARK_AQUA));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
		
		@Override
		public boolean isAdvanced()
		{
			return true;
		}
    }
}
