package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.RotatingStargateEntity;

public class RotatingStargateRingSound extends StargateSound<RotatingStargateEntity>
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.5F;
	
	public RotatingStargateRingSound(RotatingStargateEntity stargate, SoundEvent soundEvent)
	{
		super(stargate, soundEvent);
        this.looping = true;
        this.volume = VOLUME_MIN;
	}

	@Override
	public void tick()
	{
		if(stargate.isRotating())
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
