package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;

public class TransportRingsTransportSound extends TransporterSound<AbstractTransportRingsEntity<?>>
{
	public TransportRingsTransportSound(AbstractTransportRingsEntity<?> transportRings, SoundEvent soundEvent, float volume)
	{
		super(transportRings, soundEvent);
		this.volume = volume;
		this.x = transportRings.transportPos().getX() + 0.5;
		this.y = transportRings.transportPos().getY() + 0.5;
		this.z = transportRings.transportPos().getZ() + 0.5;
	}
	
	@Override
	public void tick()
	{
		this.y = transporter.getBlockPos().getY() + 0.5 + transporter.getRingHeight(0, 2) / 16D;
		
		super.tick();
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
