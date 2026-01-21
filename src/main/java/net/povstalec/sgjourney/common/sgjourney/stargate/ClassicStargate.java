package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClassicStargate extends SGJourneyStargate
{
	public static final StargateInfo.ChevronLockSpeed CHEVRON_LOCK_SPEED = CommonStargateConfig.classic_chevron_lock_speed.get();
	
	
	
	@Override
	public StargateInfo.Gen getGeneration()
	{
		return StargateInfo.Gen.NONE;
	}
	
	@Override
	public StargateInfo.ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? CHEVRON_LOCK_SPEED : StargateInfo.ChevronLockSpeed.FAST;
	}
	
	//TODO
	
	private void stargateRun(MinecraftServer server, Consumer<ClassicStargateEntity> consumer)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof ClassicStargateEntity classicStargate)
			consumer.accept(classicStargate);
	}
	
	private <T> T stargateReturn(MinecraftServer server, Function<ClassicStargateEntity, T> consumer, @Nullable T defaultValue)
	{
		AbstractStargateEntity stargate = getStargateEntity(server);
		
		if(stargate instanceof ClassicStargateEntity classicStargate)
			return consumer.apply(classicStargate);
		
		return defaultValue;
	}
}
