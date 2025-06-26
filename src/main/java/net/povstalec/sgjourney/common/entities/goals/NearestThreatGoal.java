package net.povstalec.sgjourney.common.entities.goals;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.povstalec.sgjourney.common.entities.Anthropoid;

public class NearestThreatGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
	public NearestThreatGoal(Mob mob, Class<T> entityClass, boolean mustSee)
	{
		super(mob, entityClass, 10, mustSee, false, entity -> isHoldingWeapon(entity));
	}
	
	public static boolean isHoldingWeapon(LivingEntity entity)
	{
		return Anthropoid.isWeapon(entity.getItemInHand(InteractionHand.MAIN_HAND).getItem()) ||
				Anthropoid.isWeapon(entity.getItemInHand(InteractionHand.OFF_HAND).getItem());
	}
}
