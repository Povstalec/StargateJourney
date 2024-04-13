package net.povstalec.sgjourney.common.stargate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class StargateVariant
{
	public static final ResourceLocation STARGATE_VARIANT_LOCATION = new ResourceLocation(StargateJourney.MODID, "stargate_variant");
	public static final ResourceKey<Registry<StargateVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(STARGATE_VARIANT_LOCATION);
	public static final Codec<ResourceKey<StargateVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<StargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("base_stargate").forGetter(StargateVariant::getBaseStargate),
			// Textures and colors
			ResourceLocation.CODEC.fieldOf("texture").forGetter(StargateVariant::getTexture),
			ResourceLocation.CODEC.fieldOf("engaged_texture").forGetter(StargateVariant::getEngagedTexture),
			ResourceLocation.CODEC.fieldOf("event_horizon_texture").forGetter(StargateVariant::getEventHorizonTexture),
			ResourceLocation.CODEC.optionalFieldOf("shiny_event_horizon_texture").forGetter(StargateVariant::getShinyEventHorizonTexture),
			Codec.INT.optionalFieldOf("event_horizon_frames", 32).forGetter(StargateVariant::getEventHorizonFrames),

			PointOfOrigin.RESOURCE_KEY_CODEC.optionalFieldOf("point_of_origin").forGetter(StargateVariant::getPointOfOrigin),
			Symbols.RESOURCE_KEY_CODEC.optionalFieldOf("symbols").forGetter(StargateVariant::getSymbols),
			
			Codec.INT.listOf().optionalFieldOf("symbol_color").forGetter(StargateVariant::getSymbolColor),
			Codec.INT.listOf().optionalFieldOf("encoded_symbol_color").forGetter(StargateVariant::getEncodedSymbolColor),
			Codec.INT.listOf().optionalFieldOf("engaged_symbol_color").forGetter(StargateVariant::getEngagedSymbolColor),
			// Model
			Codec.unboundedMap(Codec.STRING, Codec.BOOL).optionalFieldOf("model").forGetter(StargateVariant::getModel),
			// Sounds
			Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).optionalFieldOf("sounds").forGetter(StargateVariant::getSounds))
			.apply(instance, StargateVariant::new));
	
	private final ResourceLocation baseStargate;
	
	private final ResourceLocation texture;
	private final ResourceLocation engagedTexture;
	private final ResourceLocation eventHorizonTexture;
	private final Optional<ResourceLocation> shinyEventHorizonTexture;
	private final int eventHorizonFrames;

	private final Optional<ResourceKey<PointOfOrigin>> pointOfOrigin;
	private final Optional<ResourceKey<Symbols>> symbols;
	
	private final Optional<List<Integer>> symbolColor;
	private final Optional<List<Integer>> encodedSymbolColor;
	private final Optional<List<Integer>> engagedSymbolColor;
	
	private final Optional<Map<String, Boolean>> model;

	private Optional<Boolean> useAlternateModel = Optional.empty();
	private Optional<Boolean> backChevrons = Optional.empty();
	private Optional<Boolean> symbolsGlow = Optional.empty();
	private Optional<Boolean> encodedSymbolsGlow = Optional.empty();
	private Optional<Boolean> engagedSymbolsGlow = Optional.empty();
	private Optional<Boolean> engageEncodedSymbols = Optional.empty();
	private Optional<Boolean> engageSymbolsOnIncoming = Optional.empty();
	private Optional<Boolean> vortex = Optional.empty();
	
	private final Optional<Map<String, ResourceLocation>> sounds;

	private Optional<ResourceLocation> startupSound = Optional.empty();
	
	private Optional<ResourceLocation> chevronEngageSound = Optional.empty();
	private Optional<ResourceLocation> chevronOpenSound = Optional.empty();
	private Optional<ResourceLocation> chevronEncodeSound = Optional.empty();
	private Optional<ResourceLocation> chevronIncomingSound = Optional.empty();

	private Optional<ResourceLocation> primaryChevronEngageSound = Optional.empty();
	private Optional<ResourceLocation> primaryChevronOpenSound = Optional.empty();
	private Optional<ResourceLocation> primaryChevronIncomingSound = Optional.empty();

	private Optional<ResourceLocation> rotationBuildupSound = Optional.empty();
	private Optional<ResourceLocation> rotationSound = Optional.empty();
	private Optional<ResourceLocation> rotationStopSound = Optional.empty();
	
	private Optional<ResourceLocation> failSound = Optional.empty();
	
	private Optional<ResourceLocation> wormholeOpenSound = Optional.empty();
	private Optional<ResourceLocation> wormholeIdleSound = Optional.empty();
	private Optional<ResourceLocation> wormholeCloseSound = Optional.empty();
	
	public StargateVariant(ResourceLocation baseStargate,
			
			ResourceLocation texture,
			ResourceLocation engagedTexture,
			ResourceLocation eventHorizonTexture,
			Optional<ResourceLocation> shinyEventHorizonTexture,
			int eventHorizonFrames,

			Optional<ResourceKey<PointOfOrigin>> pointOfOrigin,
			Optional<ResourceKey<Symbols>> symbols,
			
			Optional<List<Integer>> symbolColor,
			Optional<List<Integer>> encodedSymbolColor,
			Optional<List<Integer>> engagedSymbolColor,
			
			Optional<Map<String, Boolean>> model,

			Optional<Map<String, ResourceLocation>> sounds)
	{
		this.baseStargate = baseStargate;
		
		this.texture = texture;
		this.engagedTexture = engagedTexture;
		this.eventHorizonTexture = eventHorizonTexture;
		this.shinyEventHorizonTexture = shinyEventHorizonTexture;
		this.eventHorizonFrames = eventHorizonFrames;
		
		this.symbols = symbols;
		this.pointOfOrigin = pointOfOrigin;
		
		this.symbolColor = symbolColor;
		this.encodedSymbolColor = encodedSymbolColor;
		this.engagedSymbolColor = engagedSymbolColor;
		
		this.model = model;
		
		if(this.model.isPresent())
		{
			Map<String, Boolean> modelMap = this.model.get();
			setupModel(modelMap);
		}
		
		this.sounds = sounds;
		
		if(this.sounds.isPresent())
		{
			Map<String, ResourceLocation> soundMap = this.sounds.get();
			setupSounds(soundMap);
		}
	}
	
	private void setupModel(Map<String, Boolean> modelMap)
	{
		if(modelMap.containsKey("alternate_model"))
			this.useAlternateModel = Optional.of(modelMap.get("alternate_model"));
		
		if(modelMap.containsKey("back_chevrons"))
			this.backChevrons = Optional.of(modelMap.get("back_chevrons"));
		
		if(modelMap.containsKey("symbols_glow"))
			this.symbolsGlow = Optional.of(modelMap.get("symbols_glow"));
		
		if(modelMap.containsKey("encoded_symbols_glow"))
			this.encodedSymbolsGlow = Optional.of(modelMap.get("encoded_symbols_glow"));
		
		if(modelMap.containsKey("engaged_symbols_glow"))
			this.engagedSymbolsGlow = Optional.of(modelMap.get("engaged_symbols_glow"));
		
		if(modelMap.containsKey("engage_encoded_symbols"))
			this.engageEncodedSymbols = Optional.of(modelMap.get("engage_encoded_symbols"));
		
		if(modelMap.containsKey("engage_symbols_on_incoming"))
			this.engageSymbolsOnIncoming = Optional.of(modelMap.get("engage_symbols_on_incoming"));
		
		if(modelMap.containsKey("vortex"))
			this.vortex = Optional.of(modelMap.get("vortex"));
	}
	
	private void setupSounds(Map<String, ResourceLocation> soundMap)
	{
		if(soundMap.containsKey("startup_sound"))
			this.startupSound = Optional.of(soundMap.get("startup_sound"));
		
		// Normal Chevron
		if(soundMap.containsKey("chevron_engage_sound"))
			this.chevronEngageSound = Optional.of(soundMap.get("chevron_engage_sound"));
		
		if(soundMap.containsKey("chevron_open_sound"))
			this.chevronOpenSound = Optional.of(soundMap.get("chevron_open_sound"));
		
		if(soundMap.containsKey("chevron_encode_sound"))
			this.chevronEncodeSound = Optional.of(soundMap.get("chevron_encode_sound"));
		
		if(soundMap.containsKey("chevron_incoming_sound"))
			this.chevronIncomingSound = Optional.of(soundMap.get("chevron_incoming_sound"));
		
		// Primary Chevron
		if(soundMap.containsKey("primary_chevron_engage_sound"))
			this.primaryChevronEngageSound = Optional.of(soundMap.get("primary_chevron_engage_sound"));
		
		if(soundMap.containsKey("primary_chevron_open_sound"))
			this.primaryChevronOpenSound = Optional.of(soundMap.get("primary_chevron_open_sound"));
		
		if(soundMap.containsKey("primary_chevron_incoming_sound"))
			this.primaryChevronIncomingSound = Optional.of(soundMap.get("primary_chevron_incoming_sound"));
		
		// Rotation
		if(soundMap.containsKey("rotation_sound"))
			this.rotationSound = Optional.of(soundMap.get("rotation_sound"));
		
		if(soundMap.containsKey("rotation_buildup_sound"))
			this.rotationBuildupSound = Optional.of(soundMap.get("rotation_buildup_sound"));
		
		if(soundMap.containsKey("rotation_stop_sound"))
			this.rotationStopSound = Optional.of(soundMap.get("rotation_stop_sound"));
		
		// Dialing
		if(soundMap.containsKey("dial_fail_sound"))
			this.failSound = Optional.of(soundMap.get("dial_fail_sound"));
		
		// Wormhole
		if(soundMap.containsKey("wormhole_open_sound"))
			this.wormholeOpenSound = Optional.of(soundMap.get("wormhole_open_sound"));
		
		if(soundMap.containsKey("wormhole_idle_sound"))
			this.wormholeIdleSound = Optional.of(soundMap.get("wormhole_idle_sound"));
		
		if(soundMap.containsKey("wormhole_close_sound"))
			this.wormholeCloseSound = Optional.of(soundMap.get("wormhole_close_sound"));
	}
	
	
	
	public ResourceLocation getBaseStargate()
	{
		return this.baseStargate;
	}
	
	
	
	public ResourceLocation getTexture()
	{
		return this.texture;
	}
	
	public ResourceLocation getEngagedTexture()
	{
		return this.engagedTexture;
	}
	
	public ResourceLocation getEventHorizonTexture()
	{
		return this.eventHorizonTexture;
	}
	
	public Optional<ResourceLocation> getShinyEventHorizonTexture()
	{
		return this.shinyEventHorizonTexture;
	}
	
	public int getEventHorizonFrames()
	{
		return this.eventHorizonFrames;
	}
	
	
	
	public Optional<ResourceKey<Symbols>> getSymbols()
	{
		return this.symbols;
	}
	
	public Optional<ResourceKey<PointOfOrigin>> getPointOfOrigin()
	{
		return this.pointOfOrigin;
	}
	
	
	
	public Optional<List<Integer>> getSymbolColor()
	{
		return this.symbolColor;
	}
	
	public Optional<List<Integer>> getEncodedSymbolColor()
	{
		return this.encodedSymbolColor;
	}
	
	public Optional<List<Integer>> getEngagedSymbolColor()
	{
		return this.engagedSymbolColor;
	}
	
	
	
	public Optional<Map<String, Boolean>> getModel()
	{
		return this.model;
	}
	
	
	public Optional<Boolean> useAlternateModel()
	{
		return this.useAlternateModel;
	}
	
	public Optional<Boolean> backChevrons()
	{
		return this.backChevrons;
	}
	
	public Optional<Boolean> symbolsGlow()
	{
		return this.symbolsGlow;
	}
	
	public Optional<Boolean> encodedSymbolsGlow()
	{
		return this.encodedSymbolsGlow;
	}
	
	public Optional<Boolean> engagedSymbolsGlow()
	{
		return this.engagedSymbolsGlow;
	}
	
	public Optional<Boolean> engageEncodedSymbols()
	{
		return this.engageEncodedSymbols;
	}
	
	public Optional<Boolean> engageSymbolsOnIncoming()
	{
		return this.engageSymbolsOnIncoming;
	}
	
	public Optional<Boolean> hasVortex()
	{
		return this.vortex;
	}
	
	
	
	public Optional<Map<String, ResourceLocation>> getSounds()
	{
		return this.sounds;
	}
	
	
	public Optional<ResourceLocation> getStartupSound()
	{
		return this.startupSound;
	}
	
	
	public Optional<ResourceLocation> getChevronEngageSound()
	{
		return this.chevronEngageSound;
	}
	
	public Optional<ResourceLocation> getChevronOpenSound()
	{
		return this.chevronOpenSound;
	}
	
	public Optional<ResourceLocation> getChevronEncodeSound()
	{
		return this.chevronEncodeSound;
	}
	
	public Optional<ResourceLocation> getChevronIncomingSound()
	{
		return this.chevronIncomingSound;
	}
	

	public Optional<ResourceLocation> getPrimaryChevronEngageSound()
	{
		return this.primaryChevronEngageSound;
	}
	
	public Optional<ResourceLocation> getPrimaryChevronOpenSound()
	{
		return this.primaryChevronOpenSound;
	}
	
	public Optional<ResourceLocation> getPrimaryChevronIncomingSound()
	{
		return this.primaryChevronIncomingSound;
	}
	
	
	
	public Optional<ResourceLocation> getRotationBuildupSound()
	{
		return this.rotationBuildupSound;
	}
	
	public Optional<ResourceLocation> getRotationSound()
	{
		return this.rotationSound;
	}
	
	public Optional<ResourceLocation> getRotationStopSound()
	{
		return this.rotationStopSound;
	}
	
	
	
	public Optional<ResourceLocation> getFailSound()
	{
		return this.failSound;
	}
	
	
	
	public Optional<ResourceLocation> getWormholeOpenSound()
	{
		return this.wormholeOpenSound;
	}
	
	public Optional<ResourceLocation> getWormholeIdleSound()
	{
		return this.wormholeIdleSound;
	}
	
	public Optional<ResourceLocation> getWormholeCloseSound()
	{
		return this.wormholeCloseSound;
	}
	
	private static Optional<Stargate.RGBA> getRGBA(Optional<List<Integer>> symbolColor)
	{
		if(!symbolColor.isPresent())
			return Optional.empty();
		
		int[] colorArray = symbolColor.get().stream().mapToInt((integer) -> integer).toArray();
		
		if(colorArray.length < 3)
			return Optional.empty();
		
		int alpha = 255;
		if(colorArray.length >= 4)
			alpha = colorArray[3];
		
		return Optional.of(new Stargate.RGBA(colorArray[0], colorArray[1], colorArray[2], alpha));
	}
	
	public Optional<Stargate.RGBA> getSymbolRGBA()
	{
		return getRGBA(getSymbolColor());
	}
	
	public Optional<Stargate.RGBA> getEncodedSymbolRGBA()
	{
		return getRGBA(getEncodedSymbolColor());
	}
	
	public Optional<Stargate.RGBA> getEngagedSymbolRGBA()
	{
		return getRGBA(getEngagedSymbolColor());
	}
}
