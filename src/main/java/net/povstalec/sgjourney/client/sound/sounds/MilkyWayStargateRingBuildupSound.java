package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class MilkyWayStargateRingBuildupSound extends StargateSound
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.5F;
	
	public MilkyWayStargateRingBuildupSound(MilkyWayStargateEntity stargate, SoundEvent soundEvent)
	{
		super(stargate, soundEvent);
        this.volume = VOLUME_MIN;
	}
	
	@Override
	public boolean isLooping()
	{
		return false;
	}

	@Override
	public void tick()
	{
		if(((MilkyWayStargateEntity) stargate).isRotating())
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
			this.volume += 0.1F;
	}
	
	private void fadeOut()
	{
		if(this.volume > VOLUME_MIN)
			this.volume -= 0.1F;
	}
	
}
