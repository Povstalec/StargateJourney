package net.povstalec.sgjourney.common.sgjourney.stargate.classic;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;

public abstract class ClassicStargate extends SGJourneyStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.classic_chevron_lock_speed.get();
	
	public ClassicStargate(StargateType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	
	
	@Override
	public StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? CHEVRON_LOCK_SPEED : StargateInfo.ChevronLockSpeed.FAST;
	}
}
