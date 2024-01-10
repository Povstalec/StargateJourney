package net.povstalec.sgjourney.client.sound;

import net.minecraft.client.Minecraft;
import net.povstalec.sgjourney.client.sound.sounds.MilkyWayStargateRingBuildupSound;
import net.povstalec.sgjourney.client.sound.sounds.MilkyWayStargateRingSound;
import net.povstalec.sgjourney.client.sound.sounds.PegasusStargateRingSound;
import net.povstalec.sgjourney.client.sound.sounds.StargateSound;
import net.povstalec.sgjourney.client.sound.sounds.UniverseStargateRingSound;
import net.povstalec.sgjourney.client.sound.sounds.WormholeIdleSound;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;

public abstract class StargateSoundWrapper extends SoundWrapper
{
	protected static Minecraft minecraft = Minecraft.getInstance();

	protected AbstractStargateEntity stargate;
	protected StargateSound sound;
	protected boolean playingSound = false;

	protected StargateSoundWrapper(AbstractStargateEntity stargate, StargateSound sound)
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
	
	

	public static class WormholeIdle extends StargateSoundWrapper
	{
		public WormholeIdle(AbstractStargateEntity stargate)
		{
			super(stargate, new WormholeIdleSound(stargate, SoundAccess.getWormholeIdleSound(stargate)));
		}
	}
	
	public static class UniverseRingRotation extends StargateSoundWrapper
	{
		public UniverseRingRotation(UniverseStargateEntity stargate)
		{
			super(stargate, new UniverseStargateRingSound(stargate, SoundAccess.getRotationSound(stargate)));
		}
		
		@Override
		public void playSound()
		{
			this.sound = new UniverseStargateRingSound((UniverseStargateEntity) stargate, SoundAccess.getRotationSound(stargate));
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
	
	public static class MilkyWayRingBuildup extends StargateSoundWrapper
	{
		public MilkyWayRingBuildup(MilkyWayStargateEntity stargate)
		{
			super(stargate, new MilkyWayStargateRingBuildupSound(stargate, SoundAccess.getRotationBuildupSound(stargate)));
		}
		
		@Override
		public void playSound()
		{
			if(!this.playingSound)
			{
				this.sound = new MilkyWayStargateRingBuildupSound((MilkyWayStargateEntity) stargate, SoundAccess.getRotationBuildupSound((MilkyWayStargateEntity) stargate));
				minecraft.getSoundManager().play(sound);
				this.playingSound = true;
			}
		}
	}
	
	public static class MilkyWayRingRotation extends StargateSoundWrapper
	{
		public MilkyWayRingRotation(MilkyWayStargateEntity stargate)
		{
			super(stargate, new MilkyWayStargateRingSound(stargate, SoundAccess.getRotationSound(stargate)));
		}
	}
	
	public static class PegasusRingRotation extends StargateSoundWrapper
	{
		public PegasusRingRotation(PegasusStargateEntity stargate)
		{
			super(stargate, new PegasusStargateRingSound(stargate, SoundAccess.getRotationSound(stargate)));
		}
		
		@Override
		public void playSound()
		{
			this.sound = new PegasusStargateRingSound((PegasusStargateEntity) stargate, SoundAccess.getRotationSound(stargate));
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
