package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;

public class TransportRingsSound extends TransporterSound<AbstractTransportRingsEntity<?>>
{
	public TransportRingsSound(AbstractTransportRingsEntity<?> transporter, SoundEvent soundEvent, float volume)
	{
		super(transporter, soundEvent);
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
