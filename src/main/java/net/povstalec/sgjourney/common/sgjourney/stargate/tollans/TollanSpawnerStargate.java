package net.povstalec.sgjourney.common.sgjourney.stargate.tollans;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneySpawnerStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.SpawnerStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;

public class TollanSpawnerStargate extends SGJourneySpawnerStargate implements SpawnerStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.tollan_chevron_lock_speed.get();
	
	public TollanSpawnerStargate(StargateType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	@Override
	public StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? CHEVRON_LOCK_SPEED : StargateInfo.ChevronLockSpeed.FAST;
	}
}
