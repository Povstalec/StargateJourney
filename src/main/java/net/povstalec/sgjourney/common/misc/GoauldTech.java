package net.povstalec.sgjourney.common.misc;

import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadahProvider;

public interface GoauldTech
{
	default boolean canUseGoauldTech(Entity user)
	{
		Optional<Boolean> canUse = user.getCapability(BloodstreamNaquadahProvider.BLOODSTREAM_NAQUADAH).map(cap -> cap.hasNaquadahInBloodstream());
		
		if(canUse.isPresent())
			return canUse.get();
		return false;
	}
	
	/**
	 * 
	 * @param requirementsDisabled Whether or not the requirements for having Naquadah in the bloodstream to use this have been disabled
	 * @param player
	 * @return
	 */
	default boolean canUseGoauldTech(boolean requirementsDisabled, Entity user)
	{
		return requirementsDisabled ? true : canUseGoauldTech(user);
	}
}
