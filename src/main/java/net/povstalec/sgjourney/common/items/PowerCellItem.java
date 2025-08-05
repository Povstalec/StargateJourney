package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;

import java.util.List;

public class PowerCellItem extends FluidItem.Holder
{
	public PowerCellItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isCorrectFluid(FluidStack fluidStack)
	{
		return fluidStack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get() ||
				fluidStack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
	}
	
	@Override
	public ItemStack getHeldItem(ItemStack holderStack)
	{
		IItemHandler itemHandler = holderStack.getCapability(Capabilities.ItemHandler.ITEM);
		
		if(itemHandler == null)
			return ItemStack.EMPTY;
		
		return itemHandler.getStackInSlot(0);
	}
	
	@Override
	public boolean isValidItem(ItemStack heldStack)
	{
		return heldStack.getItem() instanceof VialItem;
	}
	
	public long getBufferEnergy(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ENERGY, 0L);
	}
	
	public long getEnergyTransfer(ItemStack stack)
	{
		return CommonTechConfig.naquadah_power_cell_max_transfer.get();
	}
	
	public long getBufferCapacity(ItemStack stack)
	{
		return CommonTechConfig.naquadah_power_cell_buffer_capacity.get();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_buffer").append(Component.literal(": " + SGJourneyEnergy.energyToString(getBufferEnergy(stack), getBufferCapacity(stack)))).withStyle(ChatFormatting.DARK_RED));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.naquadah_power_cell.reload").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}
	
	public static ItemStack liquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.NAQUADAH_POWER_CELL.get());
		
		IItemHandler itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);
		if(itemHandler != null)
			itemHandler.insertItem(0, VialItem.liquidNaquadahSetup(), false);
		
		return stack;
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.NAQUADAH_POWER_CELL.get());
		
		IItemHandler itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);
		if(itemHandler != null)
			itemHandler.insertItem(0, VialItem.heavyLiquidNaquadahSetup(), false);
		
		return stack;
	}
	
	public static class Energy extends SGJourneyEnergy.Item
	{
		public Energy(ItemStack stack)
		{
			super(stack, CommonTechConfig.naquadah_power_cell_buffer_capacity.get(), CommonTechConfig.naquadah_power_cell_max_transfer.get(), CommonTechConfig.naquadah_power_cell_max_transfer.get());
		}
		
		@Override
		public long maxReceive()
		{
			if(stack.getItem() instanceof PowerCellItem powerCell)
				return powerCell.getEnergyTransfer(stack);
			
			return 0;
		}
		
		@Override
		public long maxExtract()
		{
			if(stack.getItem() instanceof PowerCellItem powerCell)
				return powerCell.getEnergyTransfer(stack);
			
			return 0;
		}
		
		@Override
		public long loadEnergy(ItemStack stack)
		{
			return stack.getOrDefault(DataComponentInit.ENERGY, 0L);
		}
		
		@Override
		public long getTrueMaxEnergyStored()
		{
			if(stack.getItem() instanceof PowerCellItem powerCell)
				return powerCell.getBufferCapacity(stack);
			
			return 0;
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			stack.set(DataComponentInit.ENERGY, this.energy);
		}
	}
	
	public static class FluidItemHandler extends FluidItem.Holder.FluidItemHandler
	{
		public FluidItemHandler(ItemStack stack, DataComponentType<ItemContainerContents> component)
		{
			super(stack, component);
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			return stack.is(ItemInit.VIAL.get());
		}
	}
}
