package net.povstalec.sgjourney.common.blocks.dhd;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CrystalDHDBlock extends AbstractDHDBlock
{
	public CrystalDHDBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.hasTag())
		{
			CompoundTag blockEntityTag = BlockItem.getBlockEntityData(stack);
			ListTag tagList = blockEntityTag.getCompound("Inventory").getList("Items", Tag.TAG_COMPOUND);
			
			if(tagList.size() > 0)
			{
				CompoundTag list1 = tagList.getCompound(0);
				
				if(list1.contains("id", Tag.TAG_STRING) && list1.getString("id").equals(InventoryUtil.itemName(ItemInit.LARGE_CONTROL_CRYSTAL.get())) && list1.contains("Count", Tag.TAG_BYTE) && list1.getByte("Count") > 0)
					tooltipComponents.add(Component.translatable("tooltip.sgjourney.dhd.has_control_crystal").withStyle(ChatFormatting.DARK_RED));
			}
		}
		
		super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
	}
}
