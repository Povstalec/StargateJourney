package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class VialItem extends Item
{
	public static final int MAX_CAPACITY = 150;
	
	public VialItem(Properties properties)
	{
		super(properties);
	}
	
	public static ItemStack liquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.VIAL.get());
		
		IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
			cap.fill(new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get().getSource(), MAX_CAPACITY), IFluidHandler.FluidAction.EXECUTE);
		
		return stack;
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.VIAL.get());
		
		IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
			cap.fill(new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get().getSource(), MAX_CAPACITY), IFluidHandler.FluidAction.EXECUTE);
		
		return stack;
	}
	
	public static int getFluidAmount(ItemStack stack)
	{
		FluidStack fluidStack = getFluidStack(stack);
		
		return fluidStack.getAmount();
	}
	
	public static int getNaquadahAmount(ItemStack stack)
	{
		FluidStack fluidStack = getFluidStack(stack);
		
		if(fluidStack.getFluid() != FluidInit.LIQUID_NAQUADAH_SOURCE.get())
			return 0;
		
		return fluidStack.getAmount();
	}
	
	public static FluidStack getFluidStack(ItemStack stack)
	{
		IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
			return cap.getFluidInTank(0);
		
		return FluidStack.EMPTY;
	}
	
	public static void drainNaquadah(ItemStack stack, int amount)
	{
		IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
			cap.drain(amount, IFluidHandler.FluidAction.EXECUTE);
	}
	
	public static int getMaxCapacity()
	{
		return MAX_CAPACITY;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		FluidStack fluidStack = getFluidStack(stack);
		if(!getFluidStack(stack).equals(FluidStack.EMPTY))
		{
			MutableComponent liquidNaquadah = Component.translatable(fluidStack.getFluidType().getDescriptionId(fluidStack)).withStyle(ChatFormatting.GREEN);
			liquidNaquadah.append(Component.literal(" " + fluidStack.getAmount() + "mB").withStyle(ChatFormatting.GREEN));
	    	tooltipComponents.add(liquidNaquadah);
		}
    	
    	super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	
	
	public static class FluidHandler extends FluidHandlerItemStack
	{
		public FluidHandler(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container)
		{
			super(componentType, container, MAX_CAPACITY);
		}
		
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		{
			return stack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get() ||
					stack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
		}
	}
}
