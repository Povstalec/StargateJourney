package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackSounds;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public abstract class ClientStargateVariant
{
	public static final String TEXTURE = "texture";
	public static final String ENCODED_TEXTURE = "encoded_texture";
	public static final String ENGAGED_TEXTURE = "engaged_texture";
	
	public static final String WORMHOLE = "wormhole";
	public static final String SHINY_WORMHOLE = "shiny_wormhole";

	public static final String SYMBOLS = "symbols";
	
	public static final String CHEVRON_ENGAGED_SOUNDS = "chevron_engaged_sounds";
	public static final String CHEVRON_INCOMING_SOUNDS = "chevron_incoming_sounds";
	
	public static final String WORMHOLE_SOUNDS = "wormhole_sounds";
	public static final String FAIL_SOUNDS = "fail_sounds";
	
	// TODO Add a way to decide if there is a vortex
	// TODO Add a way to specify how much distortion there will be on each event horizon
	// TODO Add a way to specify the model that will be used
	
	// Textures
	protected final ResourceLocation texture;
	protected final ResourceLocation encodedTexture;
	protected final ResourceLocation engagedTexture;
	
	// Wormhole stuff
	protected ResourcepackModel.Wormhole wormhole;
	@Nullable
	protected ResourcepackModel.Wormhole shinyWormhole;
	
	// Symbol stuff
	protected ResourcepackModel.SymbolsModel symbols;
	
	// Sound stuff
	protected ResourcepackSounds.Chevron chevronEngagedSounds;
	protected ResourcepackSounds.Chevron chevronIncomingSounds;
	
	protected ResourcepackSounds.Wormhole wormholeSounds;

	protected ResourcepackSounds.Fail failSounds;
	
	public ClientStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		this.texture = texture;
		
		if(encodedTexture.isPresent())
			this.encodedTexture = encodedTexture.get();
		else
			this.encodedTexture = engagedTexture;

		this.engagedTexture = engagedTexture;
		
		this.wormhole = wormhole;
		
		if(shinyWormhole.isPresent())
			this.shinyWormhole = shinyWormhole.get();
		
		this.symbols = symbols;
		
		this.chevronEngagedSounds = chevronEngagedSounds;
		this.chevronIncomingSounds = chevronIncomingSounds;
		this.wormholeSounds = wormholeSounds;
		this.failSounds = failSounds;
	}
	
	public ResourceLocation texture()
	{
		return texture;
	}
	
	public ResourceLocation encodedTexture()
	{
		return encodedTexture;
	}
	
	public ResourceLocation engagedTexture()
	{
		return engagedTexture;
	}
	
	public ResourcepackModel.Wormhole wormhole()
	{
		return wormhole;
	}
	
	public Optional<ResourcepackModel.Wormhole> shinyWormhole()
	{
		return Optional.ofNullable(shinyWormhole);
	}
	
	/**
	 * 
	 * @return Shiny wormhole if it's present and client config specifies to use shiny event horizons, otherwise regular wormhole 
	 */
	public ResourcepackModel.Wormhole getWormhole()
	{
		if(ClientStargateConfig.shiny_event_horizons.get() && shinyWormhole != null)
			return shinyWormhole;
		
		return wormhole;
	}
	
	public ResourcepackModel.SymbolsModel symbols()
	{
		return symbols;
	}
	
	public ResourcepackSounds.Chevron chevronEngagedSounds()
	{
		return chevronEngagedSounds;
	}
	
	public ResourcepackSounds.Chevron chevronIncomingSounds()
	{
		return chevronIncomingSounds;
	}
	
	public ResourcepackSounds.Wormhole wormholeSounds()
	{
		return wormholeSounds;
	}
	
	public ResourcepackSounds.Fail failSounds()
	{
		return failSounds;
	}
}
