package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.povstalec.sgjourney.common.capabilities.ItemPowerCellProvider;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
		IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
		
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
		CompoundTag tag = stack.getTag();
		if(!stack.hasTag() || !tag.contains(ItemPowerCellProvider.ENERGY_BUFFER, Tag.TAG_LONG))
			return 0;
		
		return tag.getLong(ItemPowerCellProvider.ENERGY_BUFFER);
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
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemPowerCellProvider(stack)
		{
			// ---------- Items ----------
			
			@Override
			public boolean isValid(@NotNull ItemStack stack)
			{
				return isValidItem(stack);
			}
			
			// ---------- Energy ----------
			
			@Override
			public long energyCapacity()
			{
				return getBufferCapacity(this.stack);
			}
			
			@Override
			public long energyMaxTransfer()
			{
				return getEnergyTransfer(this.stack);
			}
			
			// ---------- Fluids ----------
			
			@Override
			public @NotNull FluidStack getFluidInTank(int tank)
			{
				return getFluidStack(stack);
			}
			
			@Override
			public int getTankCapacity(int tank)
			{
				return getFluidCapacity(this.stack);
			}
			
			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack)
			{
				return isCorrectFluid(fluidStack);
			}
		};
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy_buffer").append(Component.literal(": " + SGJourneyEnergy.energyToString(getBufferEnergy(stack), getBufferCapacity(stack)))).withStyle(ChatFormatting.DARK_RED));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.naquadah_power_cell.description"));
		
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.naquadah_power_cell.reload").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}
	
	public static ItemStack liquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.NAQUADAH_POWER_CELL.get());
		
		stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
		{
			itemHandler.insertItem(0, VialItem.liquidNaquadahSetup(), false);
		});
		
		return stack;
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.NAQUADAH_POWER_CELL.get());
		
		stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
		{
			itemHandler.insertItem(0, VialItem.heavyLiquidNaquadahSetup(), false);
		});
		
		return stack;
	}
}
