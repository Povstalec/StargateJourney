package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.povstalec.sgjourney.common.capabilities.ItemFluidHolderProvider;
import net.povstalec.sgjourney.common.capabilities.ItemInventoryProvider;
import net.povstalec.sgjourney.common.entities.PlasmaProjectile;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;

public class StaffWeaponItem extends FluidItem.Holder
{
	public static final String IS_OPEN = "IsOpen";
	
	private static final float OPEN_ATTACK_DAMAGE = 3.0F;
	private static final float CLOSED_ATTACK_DAMAGE = 6.0F;

	private static final float OPEN_ATTACK_SPEED = -2.4F;
	private static final float CLOSED_ATTACK_SPEED = -2.8F;
	
	private static final float LIQUID_NAQUADAH_EXPLOSION_POWER = 0;
	private static final float HEAVY_LIQUID_NAQUADAH_EXPLOSION_POWER = 1;

	private static final int LIQUID_NAQUADAH_DEPLETION = 1;
	private static final int HEAVY_LIQUID_NAQUADAH_DEPLETION = 5;
	
	private final Multimap<Attribute, AttributeModifier> openModifiers;
	private final Multimap<Attribute, AttributeModifier> closedModifiers;
	
	public StaffWeaponItem(Properties properties)
	{
		super(properties);
		
		ImmutableMultimap.Builder<Attribute, AttributeModifier> openBuilder = ImmutableMultimap.builder();
		openBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", OPEN_ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION));
		openBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", OPEN_ATTACK_SPEED, AttributeModifier.Operation.ADDITION));
		this.openModifiers = openBuilder.build();
		
		ImmutableMultimap.Builder<Attribute, AttributeModifier> closedBuilder = ImmutableMultimap.builder();
		closedBuilder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", CLOSED_ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION));
		closedBuilder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", CLOSED_ATTACK_SPEED, AttributeModifier.Operation.ADDITION));
		this.closedModifiers = closedBuilder.build();
	}
	
	protected void shoot(Level level, Player player, ItemStack staffWeaponStack)
	{
		if(!player.isCreative() && !tryDepleteLiquidNaquadah(staffWeaponStack))
			return;
		
		level.playSound(player, player.blockPosition(), SoundInit.MATOK_FIRE.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
		if(!level.isClientSide())
		{
			PlasmaProjectile plasmaProjectile = new PlasmaProjectile(EntityInit.JAFFA_PLASMA.get(), player, level, getExplosionPower(staffWeaponStack));
			plasmaProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 1.0F);
			level.addFreshEntity(plasmaProjectile);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		player.getCooldowns().addCooldown(this, 25);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		
		if(player.isShiftKeyDown())
		{
			ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
			
			// Reloading
			if(offHandStack.getItem() instanceof StaffWeaponItem staffWeapon)
			{
				if(!level.isClientSide() && staffWeapon.swapItem(player, offHandStack, mainHandStack))
					return InteractionResultHolder.success(offHandStack);
				
				return InteractionResultHolder.pass(stack);
			}
			// Opening and closing
			else if(mainHandStack.is(ItemInit.MATOK.get()))
			{
				setOpen(level, player, mainHandStack, !isOpen(mainHandStack));
				player.getCooldowns().addCooldown(this, 15);
			}
		}
		// Shooting
		else if(isOpen(stack))
			shoot(level, player, stack);
		
		
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	}
	
	public float getExplosionPower(ItemStack stack)
	{
		// If the player is in Creative and it's empty, the explosions will be the same size as if it was filled with Liquid Naquadah
		return getFluidStack(stack).getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get() ?
				HEAVY_LIQUID_NAQUADAH_EXPLOSION_POWER : LIQUID_NAQUADAH_EXPLOSION_POWER;
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity player)
	{
		//TODO Find a good file for the attack sound
		player.level().playSound((Player) null, player.blockPosition(), SoundInit.MATOK_ATTACK.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
		return super.hurtEnemy(stack, target, player);
	}
	
	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
	{
		return !player.isCreative();
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
	public boolean isCorrectFluid(FluidStack fluidStack)
	{
		return fluidStack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get() ||
				fluidStack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
	}
	
	@Override
	public boolean isValidItem(ItemStack heldStack)
	{
		return heldStack.is(ItemInit.VIAL.get());
	}
	
	/**
	 * Returns true if it depletes naquadah, otherwise false
	 * @param staffWeaponItemStack
	 * @return
	 */
	public boolean tryDepleteLiquidNaquadah(ItemStack staffWeaponItemStack)
	{
		IFluidHandlerItem fluidHandler = staffWeaponItemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve().orElse(null);
		if(fluidHandler instanceof ItemFluidHolderProvider fluidHolder)
		{
			FluidStack fluidStack = fluidHolder.getFluidInTank(0);
			int drainAmount = fluidStack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get() ?
					LIQUID_NAQUADAH_DEPLETION : HEAVY_LIQUID_NAQUADAH_DEPLETION;
			
			FluidStack depleted = fluidHolder.deplete(drainAmount, IFluidHandler.FluidAction.EXECUTE);
			if(!depleted.isEmpty())
				return true;
		}
		
		return false;
	}
	
	/*public boolean canShoot(Player player, ItemStack stack)
	{
		return player.isCreative() ? true : getNaquadahAmount(stack) > 0;
	}*/
	
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
	
	public static void setOpen(Level level, Player player, ItemStack stack, boolean isOpen)
	{
		if(stack.is(ItemInit.MATOK.get()))
		{
			CompoundTag tag = stack.getOrCreateTag();
			tag.putBoolean(IS_OPEN, isOpen);
			stack.setTag(tag);
			
			level.playSound(player, player.blockPosition(), isOpen ? SoundInit.MATOK_OPEN.get() : SoundInit.MATOK_CLOSE.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
		}
	}
	
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack)
	{
		if(slot == EquipmentSlot.MAINHAND)
			return isOpen(stack) ? this.openModifiers : this.closedModifiers;
		
		return super.getAttributeModifiers(slot, stack);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
		
		MutableComponent isOpen = isOpen(stack) ? 
				Component.translatable("tooltip.sgjourney.matok.open").withStyle(ChatFormatting.YELLOW) :
					Component.translatable("tooltip.sgjourney.matok.closed").withStyle(ChatFormatting.YELLOW);
		tooltipComponents.add(isOpen);
    	
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.matok.open_close").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.matok.reload").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}
	
	public static ItemStack filledStaffWeapon(boolean heavyLiquidNaquadah, int amount)
	{
		ItemStack staffWeapon = new ItemStack(ItemInit.MATOK.get());
		ItemStack vial = heavyLiquidNaquadah ? VialItem.heavyLiquidNaquadahSetup(amount) : VialItem.liquidNaquadahSetup(amount);
		
		staffWeapon.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> itemHandler.insertItem(0, vial, false));
		
		return staffWeapon;
	}
}
