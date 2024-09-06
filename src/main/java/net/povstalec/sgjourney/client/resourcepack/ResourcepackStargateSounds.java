package net.povstalec.sgjourney.client.resourcepack;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public class ResourcepackStargateSounds
{
	public static final String CHEVRON_ENGAGE_SOUNDS = "chevron_engage_sounds";
	public static final String CHEVRON_OPEN_SOUNDS = "chevron_open_sounds";
	public static final String CHEVRON_ENCODE_SOUNDS = "chevron_encode_sounds";
	public static final String CHEVRON_INCOMING_SOUNDS = "chevron_incoming_sounds";

	public static final String ROTATION_SOUNDS = "rotation_sounds";
	
	public static final String WORMHOLE_SOUNDS = "wormhole_sounds";
	
	public static final String FAIL_SOUND = "fail_sound";
	
	@Nullable
	private ResourcepackSounds.Chevron chevronEngageSounds;
	@Nullable
	private ResourcepackSounds.Chevron chevronOpenSounds;
	@Nullable
	private ResourcepackSounds.Chevron chevronEncodeSounds;
	@Nullable
	private ResourcepackSounds.Chevron chevronIncomingSounds;
	
	@Nullable
	private ResourcepackSounds.Rotation rotationSounds;
	
	@Nullable
	private ResourcepackSounds.Wormhole wormholeSounds;
	
	public static final Codec<ResourcepackStargateSounds> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Chevrons
			ResourcepackSounds.Chevron.CODEC.optionalFieldOf(CHEVRON_ENGAGE_SOUNDS).forGetter(ResourcepackStargateSounds::getChevronEngageSounds),
			ResourcepackSounds.Chevron.CODEC.optionalFieldOf(CHEVRON_OPEN_SOUNDS).forGetter(ResourcepackStargateSounds::getChevronOpenSounds),
			ResourcepackSounds.Chevron.CODEC.optionalFieldOf(CHEVRON_ENCODE_SOUNDS).forGetter(ResourcepackStargateSounds::getChevronEncodeSounds),
			ResourcepackSounds.Chevron.CODEC.optionalFieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(ResourcepackStargateSounds::getChevronIncomingSounds),
			// Rotation
			ResourcepackSounds.Rotation.CODEC.optionalFieldOf(ROTATION_SOUNDS).forGetter(ResourcepackStargateSounds::getRotationSounds),
			// Rotation
			ResourcepackSounds.Wormhole.CODEC.optionalFieldOf(WORMHOLE_SOUNDS).forGetter(ResourcepackStargateSounds::getWormholeSounds),
			// Rotation
			ResourceLocation.CODEC.optionalFieldOf(FAIL_SOUND).forGetter(ResourcepackStargateSounds::getFailSound)
			).apply(instance, ResourcepackStargateSounds::new));
	
	@Nullable
	private ResourceLocation failSound; //TODO Maybe introduce unique fail sounds for different errors?
	
	public ResourcepackStargateSounds(Optional<ResourcepackSounds.Chevron> chevronEngageSounds, Optional<ResourcepackSounds.Chevron> chevronOpenSounds,
			Optional<ResourcepackSounds.Chevron> chevronEncodeSounds, Optional<ResourcepackSounds.Chevron> chevronIncomingSounds,
			Optional<ResourcepackSounds.Rotation> rotationSounds, Optional<ResourcepackSounds.Wormhole> wormholeSounds,
			Optional<ResourceLocation> failSound)
	{
		if(chevronEngageSounds.isPresent())
			this.chevronEngageSounds = chevronEngageSounds.get();
		
		if(chevronOpenSounds.isPresent())
			this.chevronOpenSounds = chevronOpenSounds.get();
		
		if(chevronEncodeSounds.isPresent())
			this.chevronEncodeSounds = chevronEncodeSounds.get();
		
		if(chevronIncomingSounds.isPresent())
			this.chevronIncomingSounds = chevronIncomingSounds.get();
		
		if(rotationSounds.isPresent())
			this.rotationSounds = rotationSounds.get();
		
		if(wormholeSounds.isPresent())
			this.wormholeSounds = wormholeSounds.get();
		
		if(failSound.isPresent())
			this.failSound = failSound.get();
	}
	
	
	
	public Optional<ResourcepackSounds.Chevron> getChevronEngageSounds()
	{
		return Optional.ofNullable(this.chevronEngageSounds);
	}
	
	public Optional<ResourcepackSounds.Chevron> getChevronOpenSounds()
	{
		return Optional.ofNullable(this.chevronOpenSounds);
	}
	
	public Optional<ResourcepackSounds.Chevron> getChevronEncodeSounds()
	{
		return Optional.ofNullable(this.chevronEncodeSounds);
	}
	
	public Optional<ResourcepackSounds.Chevron> getChevronIncomingSounds()
	{
		return Optional.ofNullable(this.chevronIncomingSounds);
	}

	
	
	public Optional<ResourcepackSounds.Rotation> getRotationSounds()
	{
		return Optional.ofNullable(this.rotationSounds);
	}

	
	
	public Optional<ResourcepackSounds.Wormhole> getWormholeSounds()
	{
		return Optional.ofNullable(this.wormholeSounds);
	}
	
	
	
	public Optional<ResourceLocation> getFailSound()
	{
		return Optional.ofNullable(this.failSound);
	}
}
