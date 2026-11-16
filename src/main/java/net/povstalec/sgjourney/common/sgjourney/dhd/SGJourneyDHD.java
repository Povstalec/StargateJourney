package net.povstalec.sgjourney.common.sgjourney.dhd;

import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.sgjourney.dhd.api.DHD;
import net.povstalec.sgjourney.common.sgjourney.stargate.api.DHDControlledStargate;

public abstract class SGJourneyDHD implements DHD
{
	public final AutoCache.Receiver<DHD, DHDControlledStargate> stargateCache = new AutoCache.Receiver<>(this);
	
	@Override
	public AutoCache.Receiver<DHD, DHDControlledStargate> receiverCache()
	{
		return stargateCache;
	}
}
