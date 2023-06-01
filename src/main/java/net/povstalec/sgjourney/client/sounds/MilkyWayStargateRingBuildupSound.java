package net.povstalec.sgjourney.client.sounds;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;

public class MilkyWayStargateRingBuildupSound extends StargateSound
{
	private static final float VOLUME_MIN = 0.0F;
	private static final float VOLUME_MAX = 0.5F;
	
	public MilkyWayStargateRingBuildupSound(MilkyWayStargateEntity stargate)
	{
		super(stargate, SoundInit.MILKY_WAY_RING_SPIN_START.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.volume = VOLUME_MIN;
	}
	
	@Override
	public boolean isLooping()
	{
		return false;
	}

	@Override
	public void tick()
	{
		if(((MilkyWayStargateEntity) stargate).isRotating())
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
			this.volume += 0.1F;
	}
	
	private void fadeOut()
	{
		if(this.volume > VOLUME_MIN)
			this.volume -= 0.1F;
	}
	
}
