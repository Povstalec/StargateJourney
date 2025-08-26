package net.povstalec.sgjourney.common.entities.goals;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;
import net.povstalec.sgjourney.common.capabilities.GoauldHostProvider;

import java.util.Optional;

public class EvacuateHostGoal extends Goal
{
	protected final Mob mob;
	
	public EvacuateHostGoal(Mob mob)
	{
		this.mob = mob;
	}
	
	@Override
	public boolean canUse()
	{
		if(!shouldPanic() || !isHealthLow())
			return false;
		
		return evacuateHost();
	}
	
	protected boolean evacuateHost()
	{
		Optional<GoauldHost> cap = this.mob.getCapability(GoauldHostProvider.GOAULD_HOST).resolve();
		
		if(!cap.isPresent() || !cap.get().isHost())
			return false;
		
		cap.get().leaveHost(this.mob);
		return true;
	}
	
	protected boolean shouldPanic()
	{
		return this.mob.getLastHurtByMob() != null || this.mob.isFreezing() || this.mob.isOnFire();
	}
	
	protected boolean isHealthLow()
	{
		return this.mob.getHealth() < this.mob.getMaxHealth() / 3F;
	}
}
