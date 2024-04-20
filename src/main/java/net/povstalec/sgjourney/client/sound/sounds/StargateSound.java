package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public abstract class StargateSound extends AbstractTickableSoundInstance
{
	protected AbstractStargateEntity stargate;
	protected Minecraft minecraft = Minecraft.getInstance();
	
	/**
	 * 
	 * @param stargate Stargate the sound is centered around
	 * @param soundEvent SoundEvent used in the sound
	 * @param fullDistance Distance from which the sound can still be heard at full volume
	 * @param maxDistance Distance at which the sound can no longer be heard
	 */
	protected StargateSound(AbstractStargateEntity stargate, SoundEvent soundEvent)
	{
		super(soundEvent, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
		
		this.stargate = stargate;
		this.x = stargate.getCenterPos().getX();
		this.y = stargate.getCenterPos().getY();
		this.z = stargate.getCenterPos().getZ();
		this.relative = true;
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
		
		float fullDistance = (float) ClientStargateConfig.stargate_full_sound_distance.get();
		float maxDistance = (float) ClientStargateConfig.stargate_max_sound_distance.get();
		
		if(fullDistance >= maxDistance)
			maxDistance = fullDistance + 1;
		
		if(distanceFromSource <= fullDistance)
			localVolume = getMaxVolume();
		else if(distanceFromSource <= maxDistance)
			localVolume = (float) (getMaxVolume() - (distanceFromSource - fullDistance) / (maxDistance - fullDistance));
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
