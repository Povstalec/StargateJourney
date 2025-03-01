package net.povstalec.sgjourney.client.sound;

import net.minecraft.client.Minecraft;
import net.povstalec.sgjourney.client.sound.sounds.RotatingStargateRingStartupSound;
import net.povstalec.sgjourney.client.sound.sounds.RotatingStargateRingSound;
import net.povstalec.sgjourney.client.sound.sounds.PegasusStargateRingSound;
import net.povstalec.sgjourney.client.sound.sounds.StargateSound;
import net.povstalec.sgjourney.client.sound.sounds.WormholeIdleSound;
import net.povstalec.sgjourney.common.block_entities.stargate.*;

public abstract class StargateSoundWrapper<T extends AbstractStargateEntity> extends SoundWrapper
{
	protected static Minecraft minecraft = Minecraft.getInstance();

	protected T stargate;
	protected StargateSound<?> sound;
	protected boolean playingSound = false;

	protected StargateSoundWrapper(T stargate, StargateSound sound)
	{
		this.stargate = stargate;
		this.sound = sound;
	}
	
	@Override
	public boolean isPlaying()
	{
		return this.playingSound;
	}
	
	@Override
	public boolean hasSound()
	{
		return this.sound != null && !this.sound.isStopped();
	}

	@Override
	public void playSound()
	{
		if(!this.playingSound)
		{
			minecraft.getSoundManager().queueTickingSound(sound);
			this.playingSound = true;
		}
	}

	@Override
	public void stopSound()
	{
		if(this.playingSound)
		{
			this.sound.stopSound();
			this.playingSound = false;
		}
	}
	

	public static class WormholeIdle extends StargateSoundWrapper<AbstractStargateEntity>
	{
		public WormholeIdle(AbstractStargateEntity stargate, boolean incoming)
		{
			super(stargate, new WormholeIdleSound(stargate, SoundAccess.getWormholeIdleSound(stargate, incoming)));
		}
	}
	
	public static class RotationStartup extends StargateSoundWrapper<RotatingStargateEntity>
	{
		public RotationStartup(RotatingStargateEntity stargate)
		{
			super(stargate, new RotatingStargateRingStartupSound(stargate, SoundAccess.getRotationStartupSound(stargate)));
		}
		
		@Override
		public void playSound()
		{
			if(!this.playingSound)
			{
				this.sound = new RotatingStargateRingStartupSound(stargate, SoundAccess.getRotationStartupSound(stargate));
				minecraft.getSoundManager().play(sound);
				this.playingSound = true;
			}
		}
	}
	
	public static class RingRotation extends StargateSoundWrapper<RotatingStargateEntity>
	{
		public RingRotation(RotatingStargateEntity stargate)
		{
			super(stargate, new RotatingStargateRingSound(stargate, SoundAccess.getRotationSound(stargate)));
		}
	}
	
	public static class PegasusRingRotation extends StargateSoundWrapper<PegasusStargateEntity>
	{
		public PegasusRingRotation(PegasusStargateEntity stargate)
		{
			super(stargate, new PegasusStargateRingSound(stargate, SoundAccess.getRotationSound(stargate)));
		}
		
		@Override
		public void playSound()
		{
			this.sound = new PegasusStargateRingSound(stargate, SoundAccess.getRotationSound(stargate));
			minecraft.getSoundManager().play(sound);
			this.playingSound = true;
		}

		@Override
		public void stopSound()
		{
			this.sound.stopSound();
			this.playingSound = false;
		}
	}
}
