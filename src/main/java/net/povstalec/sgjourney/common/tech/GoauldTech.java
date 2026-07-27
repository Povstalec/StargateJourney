package net.povstalec.sgjourney.common.tech;

import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;

public interface GoauldTech
{
	default boolean canUseGoauldTech(Entity user)
	{
		GoauldHost cap = user.getCapability(GoauldHost.GOAULD_HOST_CAPABILITY);
		
		if(cap != null)
			return cap.hasNaquadahInBloodstream();
		
		return false;
	}
	
	/**
	 * 
	 * @param requirementsDisabled Whether or not the requirements for having Naquadah in the bloodstream to use this have been disabled
	 * @param user
	 * @return
	 */
	default boolean canUseGoauldTech(boolean requirementsDisabled, Entity user)
	{
		return requirementsDisabled ? true : canUseGoauldTech(user);
	}
}
