package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackSounds;

public abstract class RotatingStargateVariant extends ClientStargateVariant
{
	public static final String ROTATION_SOUNDS = "rotation_sounds";
	
	protected ResourcepackSounds.Rotation rotationSounds;
	
	public RotatingStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, wormholeSounds, failSounds);
		
		this.rotationSounds = rotationSounds;
	}
	
	public ResourcepackSounds.Rotation rotationSounds()
	{
		return rotationSounds;
	}
}
