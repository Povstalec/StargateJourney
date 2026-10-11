package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackSounds;
import net.povstalec.sgjourney.common.block_entities.stargate.AndromedaStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;

import java.util.Optional;

public class AndromedaStargateVariant extends GenericStargateVariant<AndromedaStargateEntity>
{
	// Variant: Normal RGB - Shiny RGB
	// Andromeda: 64 233 235 - 30 144 158
	
	public static final String STARGATE_TYPE = "andromeda";
	
	public static final ResourceLocation STARGATE_TEXTURE = simpleTexturePath(STARGATE_TYPE, "stargate");
	public static final ResourceLocation STARGATE_TEXTURE_BACK = simpleTexturePath(STARGATE_TYPE, "stargate_back_chevron");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE = simpleTexturePath(STARGATE_TYPE, "stargate_engaged");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE_BACK = simpleTexturePath(STARGATE_TYPE, "stargate_back_chevron_engaged");
	
	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION = simpleTexturePath(STARGATE_TYPE, "event_horizon");
	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION_UNSTABLE = simpleTexturePath(STARGATE_TYPE, "event_horizon_unstable");
	public static final ResourceLocation STARGATE_VORTEX_LOCATION = simpleTexturePath(STARGATE_TYPE, "vortex");
	
	public static final ResourceLocation STARGATE_DISCONNECT_LOCATION = simpleTexturePath(STARGATE_TYPE, "disconnect");
	
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_LOCATION = simpleTexturePath(STARGATE_TYPE, "event_horizon_shiny");
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_UNSTABLE_LOCATION = simpleTexturePath(STARGATE_TYPE, "event_horizon_shiny_unstable");
	public static final ResourceLocation STARGATE_SHINY_VORTEX_LOCATION = simpleTexturePath(STARGATE_TYPE, "vortex_shiny");

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = new ResourceLocation(StargateJourney.MODID, "andromeda_chevron_engage");
	public static final ResourceLocation STARGATE_CHEVRON_INCOMING = new ResourceLocation(StargateJourney.MODID, "andromeda_chevron_incoming");

	public static final ResourceLocation STARGATE_RING_SPIN = new ResourceLocation(StargateJourney.MODID, "andromeda_ring_spin");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = new ResourceLocation(StargateJourney.MODID, "andromeda_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = new ResourceLocation(StargateJourney.MODID, "andromeda_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = new ResourceLocation(StargateJourney.MODID, "andromeda_wormhole_close");

	public static final ResourceLocation STARGATE_FAIL = new ResourceLocation(StargateJourney.MODID, "andromeda_dial_fail");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_WORMHOLE_LOCATION, STARGATE_WORMHOLE_LOCATION_UNSTABLE, STARGATE_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_OPAQUE_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_SHINY_WORMHOLE_LOCATION, STARGATE_SHINY_WORMHOLE_UNSTABLE_LOCATION, STARGATE_SHINY_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_OPAQUE_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	
	public static final ResourcepackModel.Symbol IDLE_SYMBOLS = new ResourcepackModel.Symbol(10, 77, 15, false);
	public static final ResourcepackModel.Symbol ENCODED_SYMBOLS = new ResourcepackModel.Symbol(125, 255, 0, false);
	public static final ResourcepackModel.Symbol INCOMING_ENCODED_SYMBOLS = new ResourcepackModel.Symbol(125, 255, 0, false);
	public static final ResourcepackModel.Symbol ENCODING_SYMBOL = new ResourcepackModel.Symbol(255, 255, 0, false);
	public static final ResourcepackModel.Symbol ENGAGED_SYMBOLS = new ResourcepackModel.Symbol(0, 255, 200, false);
	public static final ResourcepackModel.Symbol INCOMING_ENGAGED_SYMBOLS = new ResourcepackModel.Symbol(0, 255, 200, false);
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(
		IDLE_SYMBOLS,
		ENCODED_SYMBOLS, INCOMING_ENCODED_SYMBOLS, ENCODING_SYMBOL,
		IDLE_SYMBOLS, IDLE_SYMBOLS,
		ENGAGED_SYMBOLS, INCOMING_ENGAGED_SYMBOLS,
		IDLE_SYMBOLS,
		null, null);
	
	public static final GenericStargateModel GENERIC_MODEL = new GenericStargateModel(Optional.of(false), Optional.of(false), Optional.of(false));
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_INCOMING);

	public static final ResourcepackSounds.Rotation STARGATE_ROTATION_SOUNDS = new ResourcepackSounds.Rotation(StargateJourney.EMPTY_LOCATION, STARGATE_RING_SPIN, StargateJourney.EMPTY_LOCATION);
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	public static final ResourcepackSounds.Fail STARGATE_FAIL_SOUNDS = new ResourcepackSounds.Fail(STARGATE_FAIL);
	
	public static final AndromedaStargateVariant DEFAULT_VARIANT = new AndromedaStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS, GENERIC_MODEL,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS);
			
	public static final AndromedaStargateVariant DEFAULT_BACK_VARIANT = new AndromedaStargateVariant(STARGATE_TEXTURE_BACK, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE_BACK, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS, GENERIC_MODEL,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS);
	
	public static final Codec<AndromedaStargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Gate and chevron textures
			ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(AndromedaStargateVariant::texture),
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.ofNullable(variant.encodedTexture)),
			ResourceLocation.CODEC.fieldOf(ENGAGED_TEXTURE).forGetter(AndromedaStargateVariant::engagedTexture),
			// Wormholes
			ResourcepackModel.Wormhole.CODEC.fieldOf(WORMHOLE).forGetter(AndromedaStargateVariant::wormhole),
			ResourcepackModel.Wormhole.CODEC.optionalFieldOf(SHINY_WORMHOLE).forGetter(AndromedaStargateVariant::shinyWormhole),
			// Symbols
			ResourcepackModel.SymbolsModel.CODEC.fieldOf(SYMBOLS).forGetter(AndromedaStargateVariant::symbols),
			// Model
			GenericStargateModel.CODEC.optionalFieldOf(STARGATE_MODEL, GENERIC_MODEL).forGetter(AndromedaStargateVariant::stargateModel),
			// Sounds
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENGAGED_SOUNDS).forGetter(AndromedaStargateVariant::chevronEngagedSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(AndromedaStargateVariant::chevronIncomingSounds),
			
			ResourcepackSounds.Rotation.CODEC.fieldOf(ROTATION_SOUNDS).forGetter(AndromedaStargateVariant::rotationSounds),
			ResourcepackSounds.Wormhole.CODEC.fieldOf(WORMHOLE_SOUNDS).forGetter(AndromedaStargateVariant::wormholeSounds),
			ResourcepackSounds.Fail.CODEC.fieldOf(FAIL_SOUNDS).forGetter(AndromedaStargateVariant::failSounds)
			).apply(instance, AndromedaStargateVariant::new));
	
	public AndromedaStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
	                                ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
	                                GenericStargateModel stargateModel,
	                                ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
	                                ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, stargateModel,
				chevronEngagedSounds, chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
	}
	
}
