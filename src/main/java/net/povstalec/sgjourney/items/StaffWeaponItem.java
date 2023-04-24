package net.povstalec.sgjourney.items;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.povstalec.sgjourney.capabilities.ItemInventoryProvider;
import net.povstalec.sgjourney.entities.PlasmaProjectile;
import net.povstalec.sgjourney.init.EntityInit;
import net.povstalec.sgjourney.init.ItemInit;
import net.povstalec.sgjourney.init.SoundInit;

public class StaffWeaponItem extends Item
{
	public static final String IS_OPEN = "IsOpen";
	
	public StaffWeaponItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return true;
	}

	@Override
	public int getBarWidth(ItemStack stack)
	{
		return Math.round(13.0F * (float) getLiquidNaquadahAmount(stack) / 250);
	}

	@Override
	public int getBarColor(ItemStack stack)
	{
		float f = Math.max(0.0F, (float) getLiquidNaquadahAmount(stack) / 250);
		
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
	@Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemInventoryProvider(stack)
				{
					@Override
					public int getNumberOfSlots()
					{
						return 1;
					}

					@Override
					public boolean isValid(int slot, ItemStack stack)
					{
						return stack.is(ItemInit.LIQUID_NAQUADAH_BOTTLE.get()) || stack.is(Items.GLASS_BOTTLE);
					}
				};
	}
	
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		
		if(player.isShiftKeyDown() && !level.isClientSide())
		{
			ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
			
			if(offHandStack.is(ItemInit.MATOK.get()))
			{
				offHandStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
				{
					ItemStack returnStack;
					if(!mainHandStack.isEmpty())
						returnStack = itemHandler.insertItem(0, mainHandStack, false);
					else
						returnStack = itemHandler.extractItem(0, 1, false);
					
					player.setItemInHand(InteractionHand.MAIN_HAND, returnStack);
				});
				
			}
			else if(mainHandStack.is(ItemInit.MATOK.get()))
				setOpen(mainHandStack, !isOpen(mainHandStack));
		}
		else if(!player.isShiftKeyDown() && canShoot(player, player.getItemInHand(hand)))
		{
			ItemStack stack = player.getItemInHand(hand);
			
			if(isOpen(stack))
			{
				level.playSound(player, player.blockPosition(), SoundInit.MATOK_FIRE.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
				if(!level.isClientSide())
				{
					if(!player.isCreative())
						depleteLiquidNaquadah(player.getItemInHand(hand));
					PlasmaProjectile plasmaProjectile = new PlasmaProjectile(EntityInit.JAFFA_PLASMA.get(), player, level);
					plasmaProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 1.0F);
					level.addFreshEntity(plasmaProjectile);
				}
				player.awardStat(Stats.ITEM_USED.get(this));
				player.getCooldowns().addCooldown(this, 25);
			}
		}
		
		
		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
	}
	
	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
	{
		return !player.isCreative();
	}
	
	public int getLiquidNaquadahAmount(ItemStack stack)
	{
		Optional<Integer> optional = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(itemHandler -> 
		{
			ItemStack inventoryStack = itemHandler.getStackInSlot(0);
			if(inventoryStack.is(ItemInit.LIQUID_NAQUADAH_BOTTLE.get()))
				return NaquadahBottleItem.getLiquidNaquadahAmount(inventoryStack);
			else
				return 0;
		});
		return optional.isPresent() ? optional.get() : 0;
	}
	
	public void depleteLiquidNaquadah(ItemStack stack)
	{
		stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> 
		{
			ItemStack inventoryStack = itemHandler.getStackInSlot(0);
			if(inventoryStack.is(ItemInit.LIQUID_NAQUADAH_BOTTLE.get()))
			{
				if(NaquadahBottleItem.getLiquidNaquadahAmount(inventoryStack) > 0)
					NaquadahBottleItem.drainLiquidNaquadah(inventoryStack);
				else
				{
					itemHandler.extractItem(0, 1, false);
					itemHandler.insertItem(0, new ItemStack(Items.GLASS_BOTTLE), false);
				}
					
			}
		});
	}
	
	public boolean hasLiquidNaquadah(ItemStack stack)
	{
		return getLiquidNaquadahAmount(stack) > 0;
	}
	
	public boolean canShoot(Player player, ItemStack stack)
	{
		return player.isCreative() ? true : hasLiquidNaquadah(stack);
	}
	
	public static boolean isOpen(ItemStack stack)
	{
		if(stack.is(ItemInit.MATOK.get()))
		{
			CompoundTag tag = stack.getOrCreateTag();
			
			if(!tag.contains(IS_OPEN))
			{
				tag.putBoolean(IS_OPEN, false);
				stack.setTag(tag);
			}
			
			return tag.getBoolean(IS_OPEN);
		}
		
		return false;
	}
	
	public static void setOpen(ItemStack stack, boolean isOpen)
	{
		if(stack.is(ItemInit.MATOK.get()))
		{
			CompoundTag tag = stack.getOrCreateTag();
			tag.putBoolean(IS_OPEN, isOpen);
			stack.setTag(tag);
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		int fluidAmount = getLiquidNaquadahAmount(stack);
		MutableComponent isOpen = isOpen(stack) ? 
				Component.translatable("tooltip.sgjourney.matok.open").withStyle(ChatFormatting.YELLOW) :
					Component.translatable("tooltip.sgjourney.matok.closed").withStyle(ChatFormatting.YELLOW);
		tooltipComponents.add(isOpen);
		
		MutableComponent liquidNaquadah = Component.translatable("fluid_type.sgjourney.liquid_naquadah").withStyle(ChatFormatting.GREEN);
		liquidNaquadah.append(Component.literal(" " + fluidAmount + "mB").withStyle(ChatFormatting.GREEN));
    	tooltipComponents.add(liquidNaquadah);
    	
    	super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
