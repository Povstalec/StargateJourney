package net.povstalec.sgjourney.common.items.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

import javax.annotation.Nullable;
import java.util.List;

public abstract class EnergyBlockItem extends BlockItem
{
	public final String energyName;
	
	public EnergyBlockItem(Block block, Properties properties, String energyName)
	{
		super(block, properties);
		
		this.energyName = energyName;
	}
	
	public EnergyBlockItem(Block block, Properties properties)
	{
		this(block, properties, "tooltip.sgjourney.energy");
	}
	
	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state)
	{
		return updateBlockEntityTag(level, player, pos, stack);
	}
	
	protected boolean updateBlockEntityTag(Level level, @Nullable Player player, BlockPos pos, ItemStack stack)
	{
		MinecraftServer minecraftserver = level.getServer();
		if(minecraftserver == null)
			return false;
		
		if(stack.has(DataComponents.BLOCK_ENTITY_DATA))
		{
			CompoundTag compoundtag = stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
			BlockEntity blockentity = level.getBlockEntity(pos);
			if(blockentity != null)
			{
				if(!level.isClientSide() && blockentity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks()))
					return false;
				
				CompoundTag compoundtag1 = blockentity.saveWithoutMetadata(minecraftserver.registryAccess());
				CompoundTag compoundtag2 = compoundtag1.copy();
				
				compoundtag1.merge(compoundtag);
				
				if(!compoundtag1.equals(compoundtag2))
				{
					blockentity.loadCustomOnly(compoundtag1, minecraftserver.registryAccess());
					blockentity.setChanged();
					
					return setupBlockEntity(stack, blockentity);
				}
			}
		}
		else
		{
			BlockEntity baseEntity = level.getBlockEntity(pos);
			return setupBlockEntity(stack, baseEntity);
		}
		
		return false;
	}
	
	protected boolean setupBlockEntity(ItemStack stack, BlockEntity baseEntity)
	{
		if(baseEntity instanceof EnergyBlockEntity energyBlockEntity)
		{
			if(stack.has(DataComponentInit.ENERGY))
				energyBlockEntity.getEnergyStorage().setEnergy(stack.get(DataComponentInit.ENERGY));
			
			return true;
		}
		
		return false;
	}
	
	public void setEnergy(ItemStack stack, long energy)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		if(blockEntityTag != null)
		{
			blockEntityTag.putLong(EnergyBlockEntity.ENERGY, energy);
			stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
		}
		else
			stack.set(DataComponentInit.ENERGY, energy);
	}
	
	public long getEnergy(ItemStack stack)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		if(blockEntityTag != null && blockEntityTag.contains(EnergyBlockEntity.ENERGY, Tag.TAG_LONG))
			return blockEntityTag.getLong(EnergyBlockEntity.ENERGY);
		
		return stack.getOrDefault(DataComponentInit.ENERGY, 0L);
	}
	
	public abstract long getCapacity();
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return getEnergy(stack) > 0;
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
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(ComponentHelper.energy(this.energyName, getEnergy(stack), getCapacity()));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	public static class Getter extends EnergyBlockItem
	{
		private final CapacityGetter capacityGetter;
		
		public Getter(Block block, Properties properties, CapacityGetter capacityGetter, String energyName)
		{
			super(block, properties, energyName);
			
			this.capacityGetter = capacityGetter;
		}
		
		public Getter(Block block, Properties properties, CapacityGetter capacityGetter)
		{
			super(block, properties);
			
			this.capacityGetter = capacityGetter;
		}
		
		public long getCapacity()
		{
			return capacityGetter.getCapacity();
		}
	}
	
	public interface CapacityGetter
	{
		long getCapacity();
	}
}
