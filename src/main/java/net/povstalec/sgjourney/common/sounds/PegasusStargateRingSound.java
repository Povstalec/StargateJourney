package net.povstalec.sgjourney.common.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;

public class PegasusStargateRingSound extends AbstractTickableSoundInstance
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.10F;
	
	private PegasusStargateEntity stargate;
	private int initialSymbol;
	
	public PegasusStargateRingSound(PegasusStargateEntity stargate, int initialSymbol)
	{
		super(SoundInit.PEGASUS_RING_SPIN.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
		this.stargate = stargate;
		this.initialSymbol = initialSymbol;
	}

	@Override
	public void tick()
	{
		if(!stargate.isConnected() && stargate.symbolBuffer == initialSymbol)
			this.volume = VOLUME_MAX;
		else
			this.volume = VOLUME_MIN;
		
		if(stargate.waitTick == 0)
			this.stop();
	}
	
}
