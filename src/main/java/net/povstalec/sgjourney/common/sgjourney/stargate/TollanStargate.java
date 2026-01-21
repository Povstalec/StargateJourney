package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class TollanStargate extends SGJourneyStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.tollan_chevron_lock_speed.get();
	
	
	
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
	
	private void stargateRun(MinecraftServer server, Consumer<TollanStargateEntity> consumer)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof TollanStargateEntity tollanStargate)
			consumer.accept(tollanStargate);
	}
	
	private <T> T stargateReturn(MinecraftServer server, Function<TollanStargateEntity, T> consumer, @Nullable T defaultValue)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof TollanStargateEntity tollanStargate)
			return consumer.apply(tollanStargate);
		
		return defaultValue;
	}
}
