package net.povstalec.sgjourney.common.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;

public class MilkyWayStargateRingSound extends AbstractTickableSoundInstance
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.75F;
	
	MilkyWayStargateEntity stargate;
	
	public MilkyWayStargateRingSound(MilkyWayStargateEntity stargate)
	{
		super(SoundInit.PEGASUS_RING_SPIN.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
		this.stargate = stargate;
	}

	@Override
	public void tick()
	{
		if(stargate.isRotating())
			this.volume = VOLUME_MAX;
		else
			this.stop();
	}
	
}
