package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class VialItem extends Item
{
	public static final int MAX_CAPACITY = 150;
	
	public VialItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new FluidHandlerItemStack(stack, MAX_CAPACITY)
				{
		    		@Override
		    		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		    		{
		    			return stack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get() ||
		    					stack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
		    		}
				};
	}
	
	public static ItemStack liquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.VIAL.get());
        
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler ->
        {
        	fluidHandler.fill(new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get().getSource(), MAX_CAPACITY), FluidAction.EXECUTE);
        });
		
		return stack;
	}
	
	public static ItemStack heavyLiquidNaquadahSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.VIAL.get());
        
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler ->
        {
        	fluidHandler.fill(new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get().getSource(), MAX_CAPACITY), FluidAction.EXECUTE);
        });
		
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
		Optional<FluidStack> fluid = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(fluidHandler -> fluidHandler.getFluidInTank(0));
		
		return fluid.isPresent() ? fluid.get() : FluidStack.EMPTY;
	}
	
	public static void drainNaquadah(ItemStack stack, int amount)
	{
		stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler -> 
		{
			fluidHandler.drain(amount, FluidAction.EXECUTE);
		});
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
			MutableComponent liquidNaquadah = Component.translatable(fluidStack.getTranslationKey()).withStyle(ChatFormatting.GREEN);
			liquidNaquadah.append(Component.literal(" " + fluidStack.getAmount() + "mB").withStyle(ChatFormatting.GREEN));
	    	tooltipComponents.add(liquidNaquadah);
		}
    	
    	super.appendHoverText(stack, context, tooltipComponents, tooltipFlat);
	}
}
