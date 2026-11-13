package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.sgjourney.dhd.DHD;

import javax.annotation.Nullable;

public interface DHDControlledStargate extends Stargate, AutoCache.IReceiver<DHD, DHDControlledStargate>
{
	Capability<DHDControlledStargate> DHD_CONTROLLED_STARGATE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	
	/**
	 * @return DHD the Stargate is currently connected to, or null if it isn't connected to one
	 */
	@Nullable
	default DHD getDHD()
	{
		return controllerCache().get();
	}
}
