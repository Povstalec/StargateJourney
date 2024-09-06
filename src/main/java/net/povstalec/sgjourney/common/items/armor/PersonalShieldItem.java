package net.povstalec.sgjourney.common.items.armor;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.tech.AncientTech;

public class PersonalShieldItem extends ArmorItem implements AncientTech
{
	public static final String ENERGY = "Energy";
	
	protected static final int MAX_ENERGY = 100000;
	
	public PersonalShieldItem(ArmorMaterial material, ArmorItem.Type slot, Properties properties)
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
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return 0;
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new FluidHandlerItemStack(stack, getMaxCapacity())
				{
		    		@Override
		    		public boolean isFluidValid(int tank, @NotNull FluidStack stack)
		    		{
		    			return stack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
		    		}
				};
	}
	
	public static int getFluidAmount(ItemStack stack)
	{
		FluidStack fluidStack = getFluidStack(stack);
		
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	FluidStack fluidStack = getFluidStack(stack);
		if(!getFluidStack(stack).equals(FluidStack.EMPTY))
		{
			MutableComponent liquidNaquadah = Component.translatable(fluidStack.getTranslationKey()).withStyle(ChatFormatting.GREEN);
			liquidNaquadah.append(Component.literal(" " + fluidStack.getAmount() + "mB").withStyle(ChatFormatting.GREEN));
	    	tooltipComponents.add(liquidNaquadah);
		}
        
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
	
	public static ItemStack personalShieldSetup()
	{
		ItemStack stack = new ItemStack(ItemInit.PERSONAL_SHIELD_EMITTER.get());
        
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler ->
        {
        	fluidHandler.fill(new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get().getSource(), getMaxCapacity()), FluidAction.EXECUTE);
        });
		
		return stack;
	}
}
