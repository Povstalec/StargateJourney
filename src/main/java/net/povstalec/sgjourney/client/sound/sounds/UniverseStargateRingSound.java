package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;

public class UniverseStargateRingSound extends StargateSound<UniverseStargateEntity>
{
	private static final float VOLUME_MAX = 0.5F;
	public UniverseStargateRingSound(UniverseStargateEntity stargate, SoundEvent soundEvent)
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
