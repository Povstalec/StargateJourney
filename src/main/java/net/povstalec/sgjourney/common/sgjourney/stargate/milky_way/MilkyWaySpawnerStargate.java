package net.povstalec.sgjourney.common.sgjourney.stargate.milky_way;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.stargate.*;

public class MilkyWaySpawnerStargate extends SGJourneySpawnerStargate implements SpawnerStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.milky_way_chevron_lock_speed.get();
	
	public MilkyWaySpawnerStargate(StargateType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	@Override
	public StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? CHEVRON_LOCK_SPEED : StargateInfo.ChevronLockSpeed.FAST;
	}
}
