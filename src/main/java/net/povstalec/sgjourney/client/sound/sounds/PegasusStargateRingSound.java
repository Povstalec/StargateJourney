package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;

public class PegasusStargateRingSound extends StargateSound<PegasusStargateEntity>
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.1F;
	public PegasusStargateRingSound(PegasusStargateEntity stargate, SoundEvent soundEvent)
	{
		super(stargate, soundEvent);
		this.volume = VOLUME_MAX;
	}
	
	@Override
	public boolean isLooping()
	{
		return false;
	}
	
	@Override
	public void tick()
	{
		if(stargate.isSymbolSpinning())
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
