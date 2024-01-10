package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;

public class PegasusStargateRingSound extends StargateSound
{
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
}
