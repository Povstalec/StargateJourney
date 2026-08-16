package net.povstalec.sgjourney.common.tech;

import net.minecraft.world.entity.LivingEntity;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;

public interface GoauldTech
{
	/**
	 * @param user LivingEntity attempting to use this technology
	 * @return True of the user can use this technology, otherwise false
	 */
	default boolean canUseGoauldTech(LivingEntity user)
	{
		GoauldHost cap = user.getCapability(GoauldHost.GOAULD_HOST_CAPABILITY);
		
		if(cap != null)
			return cap.hasNaquadahInBloodstream();
		
		return false;
	}
}
