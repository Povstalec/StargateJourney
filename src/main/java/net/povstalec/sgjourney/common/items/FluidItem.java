package net.povstalec.sgjourney.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.IItemHandler;
import net.povstalec.sgjourney.common.capabilities.ItemFluidHolderProvider;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
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
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.fluid").append(Component.literal(": ")).append(ComponentHelper.fluidAmountComponent(fluidStack.getTranslationKey(), fluidStack.getAmount(), ComponentHelper.fluidComponentColor(fluidStack.getFluid()))));
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
	
	
	/**
	 * Class representing an item that holds a fluid item inside it
	 */
	public abstract static class Holder extends FluidItem implements IHolderItem
	{
		public Holder(Properties properties)
		{
			super(properties);
		}
		
		public void onSwapped(ItemStack holderStack, ItemStack insertedStack, ItemStack removedStack) {}
		
		@Override
		public boolean overrideStackedOnOther(ItemStack holderStack, Slot slot, ClickAction clickAction, Player player)
		{
			return stackedOnOther(holderStack, slot, clickAction, player);
		}
		
		@Override
		public boolean overrideOtherStackedOnMe(ItemStack holderStack, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess)
		{
			return otherStackedOnMe(holderStack, otherStack, slot, clickAction, player, slotAccess);
		}
		
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
		
		@Override
		public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
		{
			return new ItemFluidHolderProvider(stack)
			{
				@Override
				public boolean isValid(@NotNull ItemStack stack)
				{
					return stack.isEmpty() || (!(stack.getItem() instanceof FluidItem.Holder) && isValidItem(stack));
				}
				
				@Override
				public @NotNull FluidStack getFluidInTank(int tank)
				{
					if(!hasItem())
						return FluidStack.EMPTY;
					// Return the FluidStack of the held Item
					ItemStack heldStack = getHeldItem(stack);
					if(heldStack.getItem() instanceof FluidItem fluidItem)
						return fluidItem.getFluidStack(heldStack);
					
					return FluidStack.EMPTY;
				}
				
				@Override
				public int getTankCapacity(int tank)
				{
					if(!hasItem())
						return 0;
					// Return the capacity of the held Item
					ItemStack heldStack = getHeldItem(stack);
					if(heldStack.getItem() instanceof FluidItem fluidItem)
						return fluidItem.getFluidCapacity(heldStack);
					
					return 0;
				}
				
				@Override
				public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack)
				{
					return isCorrectFluid(fluidStack);
				}
			};
		}
		
		public boolean swapItemInHand(Player player, ItemStack holderStack, ItemStack insertedStack)
		{
			IItemHandler itemHandler = holderStack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
			if(itemHandler != null)
			{
				// Swap held item with item in player hand
				if(insertedStack.isEmpty() || isValidItem(insertedStack))
				{
					ItemStack returnStack = itemHandler.extractItem(0, 1, false);
					itemHandler.insertItem(0, insertedStack, false);
					player.setItemInHand(InteractionHand.MAIN_HAND, returnStack);
					
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
		{
			ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
			if(offHandStack.getItem() instanceof FluidItem.Holder holder)
			{
				if(!level.isClientSide() && holder.swapItemInHand(player, offHandStack, player.getItemInHand(InteractionHand.MAIN_HAND)))
					return InteractionResultHolder.success(offHandStack);
			}
			
			return super.use(level, player, hand);
		}
		
		@Override
		public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
		{
			ItemStack heldItem = getHeldItem(stack);
			
			MutableComponent itemComponent = Component.translatable("tooltip.sgjourney.holding").append(Component.literal(": "));
			if(heldItem.isEmpty())
				itemComponent.append("[-]");
			else
				itemComponent.append(heldItem.getDisplayName());
			tooltipComponents.add(itemComponent);
			
			super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
		}
	}
	
	public static ItemStack fluidSetup(Item item, Fluid fluid, int amount)
	{
		ItemStack stack = new ItemStack(item);
		
		stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluidHandler ->
		{
			fluidHandler.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE);
		});
		
		return stack;
	}
}
