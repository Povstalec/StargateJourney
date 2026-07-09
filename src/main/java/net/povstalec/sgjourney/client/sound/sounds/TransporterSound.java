package net.povstalec.sgjourney.client.sound.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public abstract class TransporterSound<T extends AbstractTransporterEntity<?>> extends AbstractTickableSoundInstance
{
	protected T transporter;
	protected BlockPos transporterPos;
	protected Minecraft minecraft = Minecraft.getInstance();
	
	/**
	 *
	 * @param transporter Stargate the sound is centered around
	 * @param soundEvent SoundEvent used in the sound
	 */
	protected TransporterSound(T transporter, SoundEvent soundEvent)
	{
		super(soundEvent, SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
		
		this.transporter = transporter;
		this.transporterPos = transporter.getBlockPos();
		this.x = transporter.getBlockPos().getX();
		this.y = transporter.getBlockPos().getY();
		this.z = transporter.getBlockPos().getZ();
		this.relative = true;
	}
	
	@Override
	public void tick()
	{
		if(!(Minecraft.getInstance().level.getBlockEntity(transporterPos) instanceof AbstractTransporterEntity<?>))
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
	
	@Override
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
