package net.povstalec.sgjourney.common.entities.goals;

import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.povstalec.sgjourney.common.items.StaffWeaponItem;

import java.util.EnumSet;

public class StaffWeaponAttackGoal<T extends PathfinderMob & RangedAttackMob> extends MeleeAttackGoal
{
	public static final int ATTACK_DELAY = 40;
	
	public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
	protected final T mob;
	protected final double speedModifier;
	protected final float meleeAttackRadiusSqr;
	protected final float pursueRadiusSqr; // Mob will attempt to pursue target if it's outside of this radius
	protected int seeTime;
	protected int attackDelay;
	protected int updatePathDelay;
	
	public StaffWeaponAttackGoal(T mob, double speedModifier, float meleeRadius, float pursueRadius)
	{
		super(mob, speedModifier, false);
		this.mob = mob;
		this.speedModifier = speedModifier;
		this.meleeAttackRadiusSqr = meleeRadius * meleeRadius;
		this.pursueRadiusSqr = pursueRadius * pursueRadius;
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
		return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
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
	
	public void meleeTick(LivingEntity target)
	{
		super.tick();
	}
	
	public void rangedTick(LivingEntity target, double distanceSqr)
	{
		boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(target);
		boolean hasSeenTarget = this.seeTime > 0;
		
		if(hasLineOfSight != hasSeenTarget)
			this.seeTime = 0;
		
		if(hasLineOfSight)
			++this.seeTime;
		else
			--this.seeTime;
		
		boolean shouldMoveToTarget = (distanceSqr > this.pursueRadiusSqr || this.seeTime < 5)/* && this.attackDelay == 0*/;
		
		if(shouldMoveToTarget)
		{
			System.out.println("pursuing");
			--this.updatePathDelay;
			if(this.updatePathDelay <= 0)
			{
				this.mob.getNavigation().moveTo(target, this.speedModifier);
				this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
			}
		}
		else
		{
			this.updatePathDelay = 0;
			this.mob.getNavigation().stop();
		}
		
		this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
		
		if(this.attackDelay > 0)
			--this.attackDelay;
		else
		{
			this.mob.performRangedAttack(target, 1.0F);
			this.attackDelay = ATTACK_DELAY;
		}
	}
	
	@Override
	public void tick()
	{
		LivingEntity target = this.mob.getTarget();
		
		if(target == null)
			return;
		
		double distanceSqr = this.mob.distanceToSqr(target);
		
		if(distanceSqr > meleeAttackRadiusSqr)
			rangedTick(target, distanceSqr);
		else
			meleeTick(target);
	}
}
