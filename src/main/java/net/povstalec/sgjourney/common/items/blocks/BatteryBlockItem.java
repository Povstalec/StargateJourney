package net.povstalec.sgjourney.common.items.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.block_entities.tech.BatteryBlockEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryBlockItem extends BlockItem
{
	public BatteryBlockItem(Block block, Properties properties)
	{
		super(block, properties);
	}
	
	public long getEnergy(ItemStack stack)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		if(blockEntityTag != null && blockEntityTag.contains(BatteryBlockEntity.ENERGY, Tag.TAG_LONG))
			return blockEntityTag.getLong(BatteryBlockEntity.ENERGY);
		
		return 0L;
	}
	
	public long getCapacity()
	{
		return 1000000000L; //TODO
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getEnergy(stack) / getCapacity());
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getEnergy(stack) / getCapacity());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + SGJourneyEnergy.energyToString(getEnergy(stack), getCapacity()))).withStyle(ChatFormatting.DARK_RED));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
