package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;

public class WormholeIdleSound extends StargateSound<AbstractStargateEntity>
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 1.0F;
	
	public WormholeIdleSound(AbstractStargateEntity stargate, SoundEvent soundEvent)
	{
		super(stargate, soundEvent);
		this.volume = VOLUME_MIN;
		this.looping = true;
	}

	@Override
	public void tick()
	{
		if(stargate.isConnected() && stargate.getKawooshTickCount() >= StargateConnection.KAWOOSH_DURATION)
			fadeIn();
		else
			fadeOut();
		
		if(getDistanceFromSource() > ClientStargateConfig.stargate_max_sound_distance.get())
			this.stopSound();
		
		super.tick();
	}
	
	@Override
	public boolean canStartSilent()
	{
		return true;
	}
	
	private void fadeIn()
	{
		if(this.volume < VOLUME_MAX)
			this.volume += 0.05F;
	}
	
	private void fadeOut()
	{
		if(this.volume >= VOLUME_MIN)
			this.volume -= 0.05F;
	}
}
