package net.povstalec.sgjourney.common.items.blocks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

public class InterfaceBlockItem extends EnergyBlockItem.Getter
{
	public InterfaceBlockItem(Block block, Properties properties, CapacityGetter capacityGetter)
	{
		super(block, properties, capacityGetter, "tooltip.sgjourney.energy_buffer");
	}
	
	public void setEnergyTarget(ItemStack stack, long energy)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		if(blockEntityTag == null)
		{
			CompoundTag tag = stack.getOrCreateTag();
			blockEntityTag = new CompoundTag();
			tag.put(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag);
		}
		
		blockEntityTag.putLong(AbstractInterfaceEntity.ENERGY_TARGET, energy);
	}
	
	public long getEnergyTarget(ItemStack stack)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		if(blockEntityTag != null && blockEntityTag.contains(AbstractInterfaceEntity.ENERGY_TARGET, Tag.TAG_LONG))
			return blockEntityTag.getLong(AbstractInterfaceEntity.ENERGY_TARGET);
		
		return 0L;
	}
}
