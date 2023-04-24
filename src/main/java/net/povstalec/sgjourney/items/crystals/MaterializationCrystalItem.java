package net.povstalec.sgjourney.items.crystals;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MaterializationCrystalItem extends Item
{
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        CrystalMode mode = getCrystalMode(stack);
        
        switch(mode)
        {
        case INCREASE_RANGE:
        	tooltipComponents.add(Component.literal("Mode: Increased Range").withStyle(ChatFormatting.DARK_AQUA));
        	break;
        case ENABLE_INTERDIMENSIONAL_TRANSPORT:
        	tooltipComponents.add(Component.literal("Mode: Enable Interdimensional Transport").withStyle(ChatFormatting.DARK_AQUA));
        	break;
        	
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
