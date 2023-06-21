package net.povstalec.sgjourney.client.sound.sounds;

import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;

public class PegasusStargateRingSound extends StargateSound
{
	private static final float VOLUME_MAX = 0.1F;
	public PegasusStargateRingSound(PegasusStargateEntity stargate)
	{
		super(stargate, SoundInit.PEGASUS_RING_SPIN.get());
		this.volume = VOLUME_MAX;
	}
	
	@Override
	public boolean isLooping()
	{
		return false;
	}
}
