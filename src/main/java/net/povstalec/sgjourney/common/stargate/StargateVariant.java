package net.povstalec.sgjourney.common.stargate;

import java.util.List;
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
			// Model
			ResourceLocation.CODEC.fieldOf("texture").forGetter(StargateVariant::getTexture),
			ResourceLocation.CODEC.fieldOf("engaged_texture").forGetter(StargateVariant::getEngagedTexture),
			Codec.BOOL.optionalFieldOf("use_alternate_model").forGetter(StargateVariant::useAlternateModel),
			Codec.INT.listOf().optionalFieldOf("symbol_color").forGetter(StargateVariant::getSymbolColor),
			Codec.INT.listOf().optionalFieldOf("engaged_symbol_color").forGetter(StargateVariant::getEngagedSymbolColor),
			Codec.INT.listOf().optionalFieldOf("event_horizon_color").forGetter(StargateVariant::getEventHorizonColor),
			// Sounds
			ResourceLocation.CODEC.optionalFieldOf("chevron_engage_sound").forGetter(StargateVariant::getChevronEngageSound),
			ResourceLocation.CODEC.optionalFieldOf("chevron_encode_sound").forGetter(StargateVariant::getChevronEncodeSound),
			ResourceLocation.CODEC.optionalFieldOf("chevron_incoming_sound").forGetter(StargateVariant::getChevronIncomingSound),
			ResourceLocation.CODEC.optionalFieldOf("dial_fail_sound").forGetter(StargateVariant::getFailSound),
			ResourceLocation.CODEC.optionalFieldOf("wormhole_open_sound").forGetter(StargateVariant::getWormholeOpenSound),
			ResourceLocation.CODEC.optionalFieldOf("wormhole_close_sound").forGetter(StargateVariant::getWormholeCloseSound)
			).apply(instance, StargateVariant::new));
	
	private final ResourceLocation baseStargate;
	
	private final ResourceLocation texture;
	private final ResourceLocation engagedTexture;
	private final Optional<Boolean> useAlternateModel;
	private final Optional<List<Integer>> symbolColor;
	private final Optional<List<Integer>> engagedSymbolColor;
	private final Optional<List<Integer>> eventHorizonColor;
	
	private final Optional<ResourceLocation> chevronEngageSound;
	private final Optional<ResourceLocation> chevronEncodeSound;
	private final Optional<ResourceLocation> chevronIncomingSound;
	private final Optional<ResourceLocation> failSound;
	private final Optional<ResourceLocation> wormholeOpenSound;
	private final Optional<ResourceLocation> wormholeCloseSound;
	
	public StargateVariant(ResourceLocation baseStargate,
			
			ResourceLocation texture,
			ResourceLocation engagedTexture,
			Optional<Boolean> useAlternateModel,
			Optional<List<Integer>> symbolColor,
			Optional<List<Integer>> engagedSymbolColor,
			Optional<List<Integer>> eventHorizonColor,
			
			Optional<ResourceLocation> chevronEngageSound,
			Optional<ResourceLocation> chevronEncodeSound,
			Optional<ResourceLocation> chevronIncomingSound,
			Optional<ResourceLocation> failSound,
			Optional<ResourceLocation> wormholeOpenSound,
			Optional<ResourceLocation> wormholeCloseSound)
	{
		this.baseStargate = baseStargate;
		
		this.texture = texture;
		this.engagedTexture = engagedTexture;
		this.useAlternateModel = useAlternateModel;
		this.symbolColor = symbolColor;
		this.engagedSymbolColor = engagedSymbolColor;
		this.eventHorizonColor = eventHorizonColor;
		
		this.chevronEngageSound = chevronEngageSound;
		this.chevronEncodeSound = chevronEncodeSound;
		this.chevronIncomingSound = chevronIncomingSound;
		this.failSound = failSound;
		this.wormholeOpenSound = wormholeOpenSound;
		this.wormholeCloseSound = wormholeCloseSound;
	}
	
	public ResourceLocation getTexture()
	{
		return this.texture;
	}
	
	public ResourceLocation getEngagedTexture()
	{
		return this.engagedTexture;
	}
	
	public ResourceLocation getBaseStargate()
	{
		return this.baseStargate;
	}
	
	public Optional<Boolean> useAlternateModel()
	{
		return this.useAlternateModel;
	}
	
	public Optional<List<Integer>> getSymbolColor()
	{
		return this.symbolColor;
	}
	
	public Optional<List<Integer>> getEngagedSymbolColor()
	{
		return this.engagedSymbolColor;
	}
	
	public Optional<List<Integer>> getEventHorizonColor()
	{
		return this.eventHorizonColor;
	}
	
	public Optional<ResourceLocation> getChevronEngageSound()
	{
		return this.chevronEngageSound;
	}
	
	public Optional<ResourceLocation> getChevronEncodeSound()
	{
		return this.chevronEncodeSound;
	}
	
	public Optional<ResourceLocation> getChevronIncomingSound()
	{
		return this.chevronIncomingSound;
	}
	
	public Optional<ResourceLocation> getFailSound()
	{
		return this.failSound;
	}
	
	public Optional<ResourceLocation> getWormholeOpenSound()
	{
		return this.wormholeOpenSound;
	}
	
	public Optional<ResourceLocation> getWormholeCloseSound()
	{
		return this.wormholeCloseSound;
	}
}
