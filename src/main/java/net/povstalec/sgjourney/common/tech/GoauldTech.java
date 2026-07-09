package net.povstalec.sgjourney.common.tech;

import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;
import net.povstalec.sgjourney.common.capabilities.GoauldHostProvider;

public interface GoauldTech
{
	default boolean canUseGoauldTech(Entity user)
	{
		return user.getCapability(GoauldHostProvider.GOAULD_HOST).resolve().map(GoauldHost::hasNaquadahInBloodstream).orElse(false);
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
