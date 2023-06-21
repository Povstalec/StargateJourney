package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

public class GenericStargateSound extends StargateSound
{
	public GenericStargateSound(AbstractStargateEntity stargate, SoundEvent soundEvent, float volume)
	{
		super(stargate, soundEvent);
		this.volume = volume;
	}
	
	@Override
	public boolean isLooping()
	{
		return false;
	}

	@Override
	public float getMaxVolume()
	{
		return this.volume;
	}
}
