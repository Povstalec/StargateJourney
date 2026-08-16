package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.Optional;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackSounds;
import net.povstalec.sgjourney.common.misc.ColorUtil;

public class PegasusStargateVariant extends GenericStargateVariant
{
	// Variant: Normal RGB - Shiny RGB
	// Pegasus: 20 123 255 - 30 90 158
	
	public static final String STARGATE_TYPE = "pegasus";
	
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

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = StargateJourney.sgjourneyLocation("pegasus_chevron_engage");
	public static final ResourceLocation STARGATE_CHEVRON_INCOMING = StargateJourney.sgjourneyLocation("pegasus_chevron_incoming");

	public static final ResourceLocation STARGATE_RING_SPIN = StargateJourney.sgjourneyLocation("pegasus_ring_spin");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = StargateJourney.sgjourneyLocation("pegasus_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = StargateJourney.sgjourneyLocation("pegasus_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = StargateJourney.sgjourneyLocation("pegasus_wormhole_close");

	public static final ResourceLocation STARGATE_FAIL = StargateJourney.sgjourneyLocation("pegasus_dial_fail");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_WORMHOLE_LOCATION, STARGATE_WORMHOLE_LOCATION_UNSTABLE, STARGATE_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_OPAQUE_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_SHINY_WORMHOLE_LOCATION, STARGATE_SHINY_WORMHOLE_UNSTABLE_LOCATION, STARGATE_SHINY_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_OPAQUE_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(
			new ColorUtil.RGBA(0, 100, 200, 255), Optional.of(new ColorUtil.RGBA(0, 200, 255, 255)), Optional.of(new ColorUtil.RGBA(0, 200, 255, 255)),
			true, true, true,
			true, true,
			Optional.empty(), Optional.empty());
	
	public static final GenericStargateVariant.GenericStargateModel GENERIC_MODEL = new GenericStargateVariant.GenericStargateModel(Optional.of(false), Optional.of(false), Optional.of(false));
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_INCOMING);

	public static final ResourcepackSounds.Rotation STARGATE_ROTATION_SOUNDS = new ResourcepackSounds.Rotation(StargateJourney.EMPTY_LOCATION, STARGATE_RING_SPIN, StargateJourney.EMPTY_LOCATION);
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	public static final ResourcepackSounds.Fail STARGATE_FAIL_SOUNDS = new ResourcepackSounds.Fail(STARGATE_FAIL);
	
	public static final PegasusStargateVariant DEFAULT_VARIANT = new PegasusStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS, GENERIC_MODEL,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS);
			
	public static final PegasusStargateVariant DEFAULT_BACK_VARIANT = new PegasusStargateVariant(STARGATE_TEXTURE_BACK, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE_BACK, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS, GENERIC_MODEL,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS);
	
	public static final Codec<PegasusStargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Gate and chevron textures
			ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(PegasusStargateVariant::texture),
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.ofNullable(variant.encodedTexture)),
			ResourceLocation.CODEC.fieldOf(ENGAGED_TEXTURE).forGetter(PegasusStargateVariant::engagedTexture),
			// Wormholes
			ResourcepackModel.Wormhole.CODEC.fieldOf(WORMHOLE).forGetter(PegasusStargateVariant::wormhole),
			ResourcepackModel.Wormhole.CODEC.optionalFieldOf(SHINY_WORMHOLE).forGetter(PegasusStargateVariant::shinyWormhole),
			// Symbols
			ResourcepackModel.SymbolsModel.CODEC.fieldOf(SYMBOLS).forGetter(PegasusStargateVariant::symbols),
			// Model
			GenericStargateVariant.GenericStargateModel.CODEC.optionalFieldOf(STARGATE_MODEL, GENERIC_MODEL).forGetter(PegasusStargateVariant::stargateModel),
			// Sounds
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENGAGED_SOUNDS).forGetter(PegasusStargateVariant::chevronEngagedSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(PegasusStargateVariant::chevronIncomingSounds),
			
			ResourcepackSounds.Rotation.CODEC.fieldOf(ROTATION_SOUNDS).forGetter(PegasusStargateVariant::rotationSounds),
			ResourcepackSounds.Wormhole.CODEC.fieldOf(WORMHOLE_SOUNDS).forGetter(PegasusStargateVariant::wormholeSounds),
			ResourcepackSounds.Fail.CODEC.fieldOf(FAIL_SOUNDS).forGetter(PegasusStargateVariant::failSounds)
			).apply(instance, PegasusStargateVariant::new));
	
	public PegasusStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			GenericStargateModel stargateModel,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, stargateModel,
				chevronEngagedSounds, chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
	}
	
}
