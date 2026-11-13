package net.povstalec.sgjourney.common.sgjourney.dhd;

import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import javax.annotation.Nullable;

public interface DHD/* extends AutoCache.IController<DHD, Stargate>*/
{
	@Nullable
	Stargate getStargate();
	
	double getMaxConnectionDistance();
	
	long getEnergyTarget();
	
	boolean enableAdvancedProtocols();
	
	int autocloseTicks();
	
	Address getAddress();
	
	boolean callForwardingEnabled();
	
	boolean hasNetworkRestrictions();
	
	void engageStargate();
	
	void encodeSymbol(int symbol);
}
