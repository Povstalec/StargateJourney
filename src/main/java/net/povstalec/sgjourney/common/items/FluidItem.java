package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class FluidItem extends Item
{
	public FluidItem(Properties properties)
	{
		super(properties);
	}
	
	public float getFluidPercentage(ItemStack stack)
	{
		if(getFluidCapacity(stack) <= 0)
			return 0;
		
		return (float) getFluidAmount(stack) / getFluidCapacity(stack);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * getFluidPercentage(stack));
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, getFluidPercentage(stack));
		
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	public abstract boolean isCorrectFluid(FluidStack fluidStack);
	
	public abstract int getFluidCapacity(ItemStack stack);
	
	public FluidStack getFluidStack(ItemStack stack)
	{
		IFluidHandler fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
		
		if(fluidHandler == null)
			return FluidStack.EMPTY;
		
		return fluidHandler.getFluidInTank(0);
	}
	
	public int getFluidAmount(ItemStack stack)
	{
		return getFluidStack(stack).getAmount();
	}
	
	public void drainFluid(ItemStack stack, int amount)
	{
		stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler ->
		{
			fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
		});
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new FluidHandlerItemStack(stack, getFluidCapacity(stack))
		{
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
		FluidStack fluidStack = getFluidStack(stack);
		if(!fluidStack.equals(FluidStack.EMPTY))
		{
			MutableComponent liquidNaquadah = Component.translatable(fluidStack.getTranslationKey()).withStyle(ChatFormatting.GREEN);
			liquidNaquadah.append(Component.literal(" " + fluidStack.getAmount() + "mB").withStyle(ChatFormatting.GREEN));
			tooltipComponents.add(liquidNaquadah);
		}
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	
	/**
	 * Class representing an item that holds a fluid item inside it
	 */
	public abstract static class Holder extends FluidItem
	{
		public Holder(Properties properties)
		{
			super(properties);
		}
		
		public abstract ItemStack getHeldItem(ItemStack holderStack);
		
		public abstract boolean isValidItem(ItemStack heldStack);
		
		@Override
		public int getFluidCapacity(ItemStack stack)
		{
			if(getHeldItem(stack).getItem() instanceof FluidItem fluidItem)
				return fluidItem.getFluidCapacity(stack);
			
			return 0;
		}
		
		@Override
		public FluidStack getFluidStack(ItemStack stack)
		{
			ItemStack heldStack = getHeldItem(stack);
			if(!heldStack.isEmpty() && heldStack.getItem() instanceof FluidItem fluidItem && isValidItem(heldStack))
				return fluidItem.getFluidStack(heldStack);
			
			return FluidStack.EMPTY;
		}
	}
}
