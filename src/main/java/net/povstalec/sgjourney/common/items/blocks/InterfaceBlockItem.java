package net.povstalec.sgjourney.common.items.blocks;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

public class InterfaceBlockItem extends EnergyBlockItem.Getter
{
	public InterfaceBlockItem(Block block, Properties properties, CapacityGetter capacityGetter)
	{
		super(block, properties, capacityGetter, "tooltip.sgjourney.energy_buffer");
	}
	
	@Override
	protected boolean setupBlockEntity(ItemStack stack, BlockEntity baseEntity)
	{
		if(baseEntity instanceof AbstractInterfaceEntity interfaceEntity)
		{
			if(stack.has(DataComponentInit.ENERGY))
				interfaceEntity.getEnergyStorage().setEnergy(stack.get(DataComponentInit.ENERGY));
			if(stack.has(DataComponentInit.ENERGY_TARGET))
				interfaceEntity.setEnergyTarget(stack.get(DataComponentInit.ENERGY_TARGET));
			
			return true;
		}
		
		return false;
	}
	
	public void setEnergyTarget(ItemStack stack, long energy)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		if(blockEntityTag != null)
		{
			blockEntityTag.putLong(AbstractInterfaceEntity.ENERGY_TARGET, energy);
			stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
		}
		else
			stack.set(DataComponentInit.ENERGY_TARGET, energy);
	}
	
	public long getEnergyTarget(ItemStack stack)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		if(blockEntityTag != null && blockEntityTag.contains(AbstractInterfaceEntity.ENERGY_TARGET, Tag.TAG_LONG))
			return blockEntityTag.getLong(AbstractInterfaceEntity.ENERGY_TARGET);
		
		return 0L;
	}
}
