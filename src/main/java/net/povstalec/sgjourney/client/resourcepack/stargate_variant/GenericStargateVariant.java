package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackSounds;

public abstract class GenericStargateVariant extends RotatingStargateVariant
{
	
	public GenericStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
	}
	
	//TODO Temporary way to handle the Movie Stargate
	public abstract boolean useMovieStargateModel();
	
	public abstract boolean raiseBackChevrons();
}
