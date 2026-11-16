package net.povstalec.sgjourney.common.tech;

import net.minecraft.world.entity.LivingEntity;
import net.povstalec.sgjourney.common.capabilities.GoauldHost;
import net.povstalec.sgjourney.common.capabilities.GoauldHostProvider;

public interface GoauldTech
{
	/**
	 * @param user LivingEntity attempting to use this technology
	 * @return True of the user can use this technology, otherwise false
	 */
	default boolean canUseGoauldTech(LivingEntity user)
	{
		return user.getCapability(GoauldHostProvider.GOAULD_HOST).resolve().map(GoauldHost::hasNaquadahInBloodstream).orElse(false);
	}
}
