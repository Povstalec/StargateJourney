package net.povstalec.sgjourney.client.sound.sounds;

import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;

public class MilkyWayStargateRingSound extends StargateSound
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.5F;
	
	public MilkyWayStargateRingSound(MilkyWayStargateEntity stargate)
	{
		super(stargate, SoundInit.MILKY_WAY_RING_SPIN.get());
        this.looping = true;
        this.volume = VOLUME_MIN;
	}

	@Override
	public void tick()
	{
		if(((MilkyWayStargateEntity) stargate).isRotating())
			fadeIn();
		else
			fadeOut();
		
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
		if(this.volume > VOLUME_MIN)
			this.volume -= 0.05F;
	}
	
}
