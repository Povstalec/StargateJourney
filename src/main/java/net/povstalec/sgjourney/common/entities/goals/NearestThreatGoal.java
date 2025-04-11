package net.povstalec.sgjourney.common.entities.goals;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;

public class NearestThreatGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
	public NearestThreatGoal(Mob mob, Class<T> entityClass, boolean mustSee)
	{
		super(mob, entityClass, 10, mustSee, false, entity -> isHoldingWeapon(entity));
	}
	
	public static boolean isHoldingWeapon(LivingEntity entity)
	{
		return isWeapon(entity.getItemInHand(InteractionHand.MAIN_HAND)) || isWeapon(entity.getItemInHand(InteractionHand.OFF_HAND));
	}
	
	public static boolean isWeapon(ItemStack stack)
	{
		if(stack.getItem() instanceof SwordItem)
			return true;
		
		if(stack.getItem() instanceof StaffWeaponItem)
			return true;
		
		if(stack.getItem() instanceof BowItem)
			return true;
		
		if(stack.getItem() instanceof CrossbowItem)
			return true;
		
		return false;
	}
}
