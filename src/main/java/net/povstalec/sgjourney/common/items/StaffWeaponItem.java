package net.povstalec.sgjourney.common.items;

import java.util.Arrays;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.povstalec.sgjourney.common.entities.PlasmaProjectile;
import net.povstalec.sgjourney.common.init.*;

public class StaffWeaponItem extends FluidItem.Holder
{
	public static final String IS_OPEN = "is_open";
	
	private static final float OPEN_ATTACK_DAMAGE = 3.0F;
	private static final float CLOSED_ATTACK_DAMAGE = 6.0F;

	private static final float OPEN_ATTACK_SPEED = -2.4F;
	private static final float CLOSED_ATTACK_SPEED = -2.8F;
	
	private static final float LIQUID_NAQUADAH_EXPLOSION_POWER = 0;
	private static final float HEAVY_LIQUID_NAQUADAH_EXPLOSION_POWER = 1;

	private static final int LIQUID_NAQUADAH_DEPLETION = 1;
	private static final int HEAVY_LIQUID_NAQUADAH_DEPLETION = 5;
	
	private final ItemAttributeModifiers openModifiers;
	private final ItemAttributeModifiers closedModifiers;
	
	public StaffWeaponItem(Properties properties)
	{
		super(properties);
		
		ItemAttributeModifiers.Entry damageEntry = new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, OPEN_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		ItemAttributeModifiers.Entry speedEntry = new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, OPEN_ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		
		openModifiers = new ItemAttributeModifiers(Arrays.asList(damageEntry, speedEntry), true);
		
		damageEntry = new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, CLOSED_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		speedEntry = new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, CLOSED_ATTACK_SPEED, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		
		closedModifiers = new ItemAttributeModifiers(Arrays.asList(damageEntry, speedEntry), true);
	}
	
	protected void shoot(Level level, Player player, ItemStack staffWeaponStack)
	{
		if(!player.isCreative() && !depleteLiquidNaquadah(staffWeaponStack))
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
		else if(isOpen(stack) && !level.isClientSide())
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
		IItemHandler itemHandler = holderStack.getCapability(Capabilities.ItemHandler.ITEM);
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
	public boolean depleteLiquidNaquadah(ItemStack staffWeaponItemStack)
	{
		IFluidHandlerItem fluidHandler = staffWeaponItemStack.getCapability(Capabilities.FluidHandler.ITEM);
		if(fluidHandler instanceof FluidItem.Holder.Capability fluidHolder)
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
		return stack.getOrDefault(DataComponentInit.IS_OPEN, false);
	}
	
	public static void setOpen(Level level, Player player, ItemStack stack, boolean isOpen)
	{
		stack.set(DataComponentInit.IS_OPEN, isOpen);
		
		level.playSound(player, player.blockPosition(), isOpen ? SoundInit.MATOK_OPEN.get() : SoundInit.MATOK_CLOSE.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
	}
	
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		return isOpen(stack) ? this.openModifiers : this.closedModifiers;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		
		MutableComponent isOpen = isOpen(stack) ? 
				Component.translatable("tooltip.sgjourney.matok.open").withStyle(ChatFormatting.YELLOW) :
					Component.translatable("tooltip.sgjourney.matok.closed").withStyle(ChatFormatting.YELLOW);
		tooltipComponents.add(isOpen);
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.matok.open_close").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.matok.reload").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
	}
	
	
	
	public static class ItemHandler extends ComponentItemHandler
	{
		public ItemHandler(MutableDataComponentHolder parent, DataComponentType<ItemContainerContents> component)
		{
			super(parent, component, 1);
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack)
		{
			return stack.is(ItemInit.VIAL.get());
		}
	}
}
