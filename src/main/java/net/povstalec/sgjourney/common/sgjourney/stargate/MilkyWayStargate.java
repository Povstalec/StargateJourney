package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;

public class MilkyWayStargate extends SGJourneyStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.milky_way_chevron_lock_speed.get();
	
	
	
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
	
	//TODO
	
	private void stargateRun(MinecraftServer server, StargateConsumer<MilkyWayStargateEntity> consumer)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
			consumer.run(milkyWayStargate);
	}
	
	private <T> T stargateReturn(MinecraftServer server, ReturnStargateConsumer<T, MilkyWayStargateEntity> consumer, @Nullable T defaultValue)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
			return consumer.run(milkyWayStargate);
		
		return defaultValue;
	}
}
