package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;

public abstract class StargateSound extends AbstractTickableSoundInstance
{
	protected AbstractStargateEntity stargate;
	protected Minecraft minecraft = Minecraft.getInstance();
	protected double fullDistance;
	protected double maxDistance;
	
	/**
	 * 
	 * @param stargate Stargate the sound is centered around
	 * @param soundEvent SoundEvent used in the sound
	 * @param fullDistance Distance from which the sound can still be heard at full volume
	 * @param maxDistance Distance at which the sound can no longer be heard
	 */
	protected StargateSound(AbstractStargateEntity stargate, SoundEvent soundEvent, double fullDistance, double maxDistance)
	{
		super(soundEvent, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
		/*if (soundEvent == SoundInit.EMPTY_SOUND_INSTANCE)
			this.stop();*/
		this.stargate = stargate;
		this.x = stargate.getBlockPos().getX();
		this.y = stargate.getBlockPos().getY();
		this.z = stargate.getBlockPos().getZ();
		this.relative = true;
		
		this.fullDistance = fullDistance;
		this.maxDistance = maxDistance;
	}
	
	protected StargateSound(AbstractStargateEntity stargate, SoundEvent soundEvent)
	{
		this(stargate, soundEvent, 32.0, 64.0);
	}
	
	@Override
	public void tick()
	{
		if(!(stargate instanceof AbstractStargateEntity))
			this.stop();
	}
	
	@Override
	public boolean canStartSilent()
	{
		return true;
	}
	
	public void stopSound()
	{
		this.stop();
	}
	
	public Vec3 getPosition()
	{
		return new Vec3(x, y, z);
	}
	
	public double getDistanceFromSource()
	{
		LocalPlayer player = minecraft.player;
		Vec3 playerPos = player.position();
		return getPosition().distanceTo(playerPos);
	}
	
	public float getVolume()
	{
		float localVolume = 0.0F;
		double distanceFromSource = getDistanceFromSource();
		
		if(distanceFromSource <= this.fullDistance)
			localVolume = getMaxVolume();
		else if(distanceFromSource <= this.maxDistance)
			localVolume = (float) (getMaxVolume() - (distanceFromSource - this.fullDistance) / (this.maxDistance - this.fullDistance));
		else
			localVolume = getMinVolume();
		
		return super.getVolume() * localVolume;
	}
	
	public float getMaxVolume()
	{
		return 1.0F;
	}
	
	public float getMinVolume()
	{
		return 0.0F;
	}
}
