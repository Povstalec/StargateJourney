package net.povstalec.sgjourney.common.tech;

import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadah;

public interface GoauldTech
{
	default boolean canUseGoauldTech(Entity user)
	{
		BloodstreamNaquadah cap = user.getCapability(BloodstreamNaquadah.BLOODSTREAM_NAQUADAH_CAPABILITY);
		
		if(cap != null && cap.hasNaquadahInBloodstream())
			return true;
		
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
