package net.povstalec.sgjourney.client.sound.sounds;

import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;

public class WormholeIdleSound extends StargateSound
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 1.0F;
	
	public WormholeIdleSound(AbstractStargateEntity stargate)
	{
		super(stargate, SoundInit.WORMHOLE_IDLE.get());
		this.volume = VOLUME_MIN;
		this.looping = true;
	}

	@Override
	public void tick()
	{
		if(stargate.isConnected())
			fadeIn();
		else
			fadeOut();
		
		if(getDistanceFromSource() > this.fullDistance)
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
		
		if(this.volume < VOLUME_MIN)
			this.stopSound();
	}
}
