package net.povstalec.sgjourney.common.entities.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;
import net.povstalec.sgjourney.common.capabilities.GoauldHostProvider;

import java.util.Optional;

public class NearestHostGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
{
	public NearestHostGoal(Mob mob, Class<T> targetType)
	{
		super(mob, targetType, 10, true, true, NearestHostGoal::isSuitableHost);
	}
	
	public static boolean isSuitableHost(LivingEntity target)
	{
		Optional<GoauldHost> cap = target.getCapability(GoauldHostProvider.GOAULD_HOST).resolve();
		
		if(cap.isEmpty() || cap.get().isHost()) // Goa'uld won't try to take over a mob that is a host to another Goa'uld
			return false;
		
		return target.getHealth() > target.getMaxHealth() / 3F;
	}
}
