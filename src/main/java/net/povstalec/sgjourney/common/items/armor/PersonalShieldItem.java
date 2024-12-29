package net.povstalec.sgjourney.common.items.armor;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.tech.AncientTech;
import org.jetbrains.annotations.Nullable;

public class PersonalShieldItem extends ArmorItem implements AncientTech
{
	public PersonalShieldItem(Holder<ArmorMaterial> material, ArmorItem.Type slot, Properties properties)
	{
		super(material, slot, properties);
	}
	
	public static int getMaxCapacity()
	{
		return CommonTechConfig.personal_shield_capacity.get();
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getFluidAmount(stack) / getMaxCapacity());
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getFluidAmount(stack) / getMaxCapacity());
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken)
	{
		return 0;
	}
	
	public static int getFluidAmount(ItemStack stack)
	{
		FluidStack fluidStack = getFluidStack(stack);
		
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
	
	public static ItemStack personalShieldSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.PERSONAL_SHIELD_EMITTER.get());
		
		IFluidHandlerItem cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(cap != null)
        	cap.fill(new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get().getSource(), getMaxCapacity()), IFluidHandler.FluidAction.EXECUTE);
		
		return stack;
	}
	
	
	
	public static class FluidHandler extends FluidHandlerItemStack
	{
		public FluidHandler(Supplier<DataComponentType<SimpleFluidContent>> componentType, ItemStack container)
		{
			super(componentType, container, getMaxCapacity());
		}
		
		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		{
			return stack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
		}
	}
}
