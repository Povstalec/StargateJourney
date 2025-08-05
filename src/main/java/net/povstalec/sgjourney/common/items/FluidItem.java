package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import net.povstalec.sgjourney.common.capabilities.SingleItemHandler;
import net.povstalec.sgjourney.common.init.FluidInit;
import org.jetbrains.annotations.NotNull;

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
		IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		
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
		IFluidHandler fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
			fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
	}
	
	protected ChatFormatting fluidComponentColor(FluidStack fluidStack)
	{
		if(fluidStack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get())
			return ChatFormatting.GREEN;
		else if(fluidStack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get())
			return ChatFormatting.DARK_GREEN;
		
		return ChatFormatting.WHITE;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		FluidStack fluidStack = getFluidStack(stack);
		
		MutableComponent fluidComponent = Component.translatable("tooltip.sgjourney.fluid").append(Component.literal(": "));
		if(fluidStack.isEmpty())
			fluidComponent.append("0 mB");
		else
		{
			fluidComponent.append(fluidStack.getFluidType().getDescription());
			fluidComponent.append(Component.literal(" " + fluidStack.getAmount() + "mB"));
		}
		fluidComponent.withStyle(fluidComponentColor(fluidStack));
		tooltipComponents.add(fluidComponent);
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
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
		
		public boolean swapItem(Player player, ItemStack holderStack, ItemStack insertedStack)
		{
			IItemHandler itemHandler = holderStack.getCapability(Capabilities.ItemHandler.ITEM);
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
				if(!level.isClientSide() && holder.swapItem(player, offHandStack, player.getItemInHand(InteractionHand.MAIN_HAND)))
					return InteractionResultHolder.success(offHandStack);
			}
			
			return super.use(level, player, hand);
		}
		
		@Override
		public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
		{
			ItemStack heldItem = getHeldItem(stack);
			
			MutableComponent itemComponent = Component.translatable("tooltip.sgjourney.holding").append(Component.literal(": "));
			if(heldItem.isEmpty())
				itemComponent.append("[-]");
			else
				itemComponent.append(heldItem.getDisplayName());
			tooltipComponents.add(itemComponent);
			
			super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		}
		
		public static abstract class FluidItemHandler extends SingleItemHandler implements IFluidHandlerItem
		{
			protected ItemStack stack;
			
			public FluidItemHandler(ItemStack stack, DataComponentType<ItemContainerContents> component)
			{
				super(stack, component);
				this.stack = stack;
			}
			
			@Override
			public @NotNull ItemStack getContainer()
			{
				return stack;
			}
			
			@Override
			public int getTanks()
			{
				return 0;
			}
			
			@Override
			public @NotNull FluidStack getFluidInTank(int i)
			{
				if(!hasItem())
					return FluidStack.EMPTY;
				
				// Return the FluidStack of the held Item
				ItemStack heldStack = getHeldItem();
				if(heldStack.getItem() instanceof FluidItem fluidItem)
					return fluidItem.getFluidStack(heldStack);
				
				return FluidStack.EMPTY;
			}
			
			@Override
			public int getTankCapacity(int i)
			{
				if(!hasItem())
					return 0;
				// Return the capacity of the held Item
				ItemStack heldStack = getHeldItem();
				if(heldStack.getItem() instanceof FluidItem fluidItem)
					return fluidItem.getFluidCapacity(heldStack);
				
				return 0;
			}
			
			@Override
			public int fill(FluidStack resource, FluidAction action)
			{
				if(!hasItem())
					return 0;
				
				ItemStack heldStack = getHeldItem();
				IFluidHandlerItem fluidHandler = heldStack.getCapability(Capabilities.FluidHandler.ITEM);
				if(fluidHandler != null)
				{
					int amount = fluidHandler.fill(resource, action);
					
					if(amount != 0)
						updateContents(heldStack);
					
					return amount;
				}
				
				return 0;
			}
			
			public @NotNull FluidStack deplete(int maxDrain, FluidAction action)
			{
				if(!hasItem())
					return FluidStack.EMPTY;
				
				ItemStack heldStack = getHeldItem();
				IFluidHandlerItem fluidHandler = heldStack.getCapability(Capabilities.FluidHandler.ITEM);
				if(fluidHandler != null)
				{
					FluidStack fluidStack = fluidHandler.drain(maxDrain, action);
					
					if(!fluidStack.isEmpty())
						updateContents(heldStack);
					
					return fluidStack;
				}
				
				return FluidStack.EMPTY;
			}
			
			@Override
			public @NotNull FluidStack drain(FluidStack resource, FluidAction fluidAction)
			{
				if(stack.getCount() != 1 || resource.isEmpty() || !resource.is(getFluidInTank(0).getFluid()))
					return FluidStack.EMPTY;
				
				return drain(resource.getAmount(), fluidAction);
			}
			
			@Override
			public @NotNull FluidStack drain(int maxDrain, FluidAction fluidAction)
			{
				return FluidStack.EMPTY;
			}
			
			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack)
			{
				if(stack.getItem() instanceof FluidItem fluidItem)
					return fluidItem.isCorrectFluid(fluidStack);
				
				return false;
			}
		}
	}
	
	public static ItemStack fluidSetup(Item item, Fluid fluid, int amount)
	{
		ItemStack stack = new ItemStack(item);
		
		IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler != null)
			fluidHandler.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE);
		
		return stack;
	}
}
