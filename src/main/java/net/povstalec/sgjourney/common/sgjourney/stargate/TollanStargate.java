package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

public abstract class TollanStargate extends SGJourneyStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.tollan_chevron_lock_speed.get();
	
	public TollanStargate(StargateType<?> type)
	{
		super(type);
	}
	
	
	
	@Override
	public StargateInfo.Gen getGeneration()
	{
		return StargateInfo.Gen.GEN_2;
	}
	
	@Override
	public StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? CHEVRON_LOCK_SPEED : StargateInfo.ChevronLockSpeed.FAST;
	}
}
