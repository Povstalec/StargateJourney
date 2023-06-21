package net.povstalec.sgjourney.client.sound.sounds;

import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;

public class UniverseStargateRingSound extends StargateSound
{
	private static final float VOLUME_MAX = 1.0F;
	public UniverseStargateRingSound(UniverseStargateEntity stargate)
	{
		super(stargate, SoundInit.UNIVERSE_RING_SPIN.get());
		this.volume = VOLUME_MAX;
	}
	
	@Override
	public boolean isLooping()
	{
		return false;
	}
}
