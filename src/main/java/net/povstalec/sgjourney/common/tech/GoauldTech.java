package net.povstalec.sgjourney.common.tech;

import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadah;
import net.povstalec.sgjourney.common.capabilities.BloodstreamNaquadahProvider;

public interface GoauldTech
{
	default boolean canUseGoauldTech(Entity user)
	{
		Optional<BloodstreamNaquadah> cap = user.getCapability(BloodstreamNaquadahProvider.BLOODSTREAM_NAQUADAH).resolve();
		
		if(cap.isEmpty())
			return false;
		
		return cap.get().hasNaquadahInBloodstream();
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
