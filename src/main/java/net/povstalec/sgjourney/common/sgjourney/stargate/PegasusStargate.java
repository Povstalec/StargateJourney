package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class PegasusStargate extends SGJourneyStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.pegasus_chevron_lock_speed.get();
	
	
	
	@Override
	public StargateInfo.Gen getGeneration()
	{
		return StargateInfo.Gen.GEN_3;
	}
	
	@Override
	public StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? CHEVRON_LOCK_SPEED : StargateInfo.ChevronLockSpeed.FAST;
	}
	
	//TODO
	
	private void stargateRun(MinecraftServer server, Consumer<PegasusStargateEntity> consumer)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof PegasusStargateEntity pegasusStargate)
			consumer.accept(pegasusStargate);
	}
	
	private <T> T stargateReturn(MinecraftServer server, Function<PegasusStargateEntity, T> consumer, @Nullable T defaultValue)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof PegasusStargateEntity pegasusStargate)
			return consumer.apply(pegasusStargate);
		
		return defaultValue;
	}
}
