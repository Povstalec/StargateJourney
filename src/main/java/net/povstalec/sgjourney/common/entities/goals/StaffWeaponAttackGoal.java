package net.povstalec.sgjourney.common.entities.goals;

import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;

import java.util.EnumSet;

public class StaffWeaponAttackGoal<T extends Mob & RangedAttackMob> extends Goal
{
	public static final int ATTACK_DELAY = 40;
	
	public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
	private final T mob;
	private final double speedModifier;
	private final float meleeAttackRadiusSqr;
	private final float maxAttackRadiusSqr;
	private int seeTime;
	private int attackDelay;
	private int updatePathDelay;
	
	public StaffWeaponAttackGoal(T mob, double speedModifier, float meleeRadius, float maxRadius)
	{
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.meleeAttackRadiusSqr = meleeRadius * meleeRadius;
		this.maxAttackRadiusSqr = maxRadius * maxRadius;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		
		this.attackDelay = ATTACK_DELAY;
	}
	
	@Override
	public boolean canUse()
	{
		return this.isValidTarget() && this.isHoldingStaffWeapon();
	}
	
	private boolean isHoldingStaffWeapon()
	{
		return this.mob.isHolding((is) -> is.getItem() instanceof StaffWeaponItem);
	}
	
	public boolean canContinueToUse()
	{
		return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingStaffWeapon();
	}
	
	private boolean isValidTarget()
	{
		if(this.mob.getTarget() != null && this.mob.getTarget().isAlive())
			return this.mob.distanceToSqr(this.mob.getTarget()) > meleeAttackRadiusSqr;
		
		return false;
	}
	
	@Override
	public void stop()
	{
		super.stop();
		this.mob.setAggressive(false);
		this.mob.setTarget(null);
		this.seeTime = 0;
		
		if(this.mob.isUsingItem())
			this.mob.stopUsingItem();
		
	}
	
	@Override
	public boolean requiresUpdateEveryTick()
	{
		return true;
	}
	
	@Override
	public void tick()
	{
		//TODO Melee attack when enemy is in melee range
		
		LivingEntity livingentity = this.mob.getTarget();
		
		if(livingentity == null)
			return;
		
		boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(livingentity);
		boolean hasSeenTarget = this.seeTime > 0;
		
		if(hasLineOfSight != hasSeenTarget)
			this.seeTime = 0;
		
		if(hasLineOfSight)
			++this.seeTime;
		else
			--this.seeTime;
		
		double distanceSqr = this.mob.distanceToSqr(livingentity);
		boolean shouldMoveToTarget = (distanceSqr > (double)this.maxAttackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
		
		if(shouldMoveToTarget)
		{
			--this.updatePathDelay;
			if(this.updatePathDelay <= 0)
			{
				this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
				this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
			}
		}
		else
		{
			this.updatePathDelay = 0;
			this.mob.getNavigation().stop();
		}
		
		this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
		
		if(this.attackDelay > 0)
			--this.attackDelay;
		else
		{
			this.mob.performRangedAttack(livingentity, 1.0F);
			this.attackDelay = ATTACK_DELAY;
		}
	}
}
