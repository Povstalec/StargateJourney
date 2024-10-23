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

public class MilkyWayStargateVariant extends GenericStargateVariant
{
	public static final String CHEVRON_OPEN_SOUNDS = "chevron_open_sounds";
	public static final String CHEVRON_ENCODE_SOUNDS = "chevron_encode_sounds";
	
	public static final ResourceLocation STARGATE_TEXTURE = StargateJourney.sgjourneyLocation("textures/entity/stargate/milky_way/milky_way_stargate.png");
	public static final ResourceLocation STARGATE_TEXTURE_BACK = StargateJourney.sgjourneyLocation("textures/entity/stargate/milky_way/milky_way_stargate_back_chevron.png");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE = StargateJourney.sgjourneyLocation("textures/entity/stargate/milky_way/milky_way_stargate_engaged.png");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE_BACK = StargateJourney.sgjourneyLocation("textures/entity/stargate/milky_way/milky_way_stargate_back_chevron_engaged.png");

	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION = StargateJourney.sgjourneyLocation("textures/entity/stargate/milky_way/milky_way_event_horizon.png");
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_LOCATION = StargateJourney.sgjourneyLocation("textures/entity/stargate/milky_way/milky_way_event_horizon_shiny.png");

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = StargateJourney.sgjourneyLocation("milky_way_chevron_engage");
	public static final ResourceLocation STARGATE_CHEVRON_OPEN = StargateJourney.sgjourneyLocation("milky_way_chevron_open");
	public static final ResourceLocation STARGATE_CHEVRON_ENCODE = StargateJourney.sgjourneyLocation("milky_way_chevron_encode");

	public static final ResourceLocation STARGATE_RING_SPIN_START = StargateJourney.sgjourneyLocation("milky_way_ring_spin_start");
	public static final ResourceLocation STARGATE_RING_SPIN = StargateJourney.sgjourneyLocation("milky_way_ring_spin");
	public static final ResourceLocation STARGATE_RING_SPIN_STOP = StargateJourney.sgjourneyLocation("milky_way_ring_spin_stop");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = StargateJourney.sgjourneyLocation("milky_way_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = StargateJourney.sgjourneyLocation("milky_way_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = StargateJourney.sgjourneyLocation("milky_way_wormhole_close");

	public static final ResourceLocation STARGATE_FAIL = StargateJourney.sgjourneyLocation("milky_way_dial_fail");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_OPAQUE_RGBA),
			new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_TRANSLUCENT_RGBA))));
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_OPAQUE_RGBA),
			new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_TRANSLUCENT_RGBA))));
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(new ColorUtil.RGBA(48, 49, 63, 255));
	
	public static final GenericStargateVariant.GenericStargateModel.MilkyWay GENERIC_MODEL = new GenericStargateVariant.GenericStargateModel.MilkyWay(Optional.empty(), Optional.empty(), Optional.of(false));
	public static final GenericStargateVariant.GenericStargateModel.MilkyWay GENERIC_MODEL_BACK_CHEVRON = new GenericStargateVariant.GenericStargateModel.MilkyWay(Optional.empty(), Optional.empty(), Optional.of(true));
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_OPEN_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_OPEN);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENCODE_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENCODE);
	
	protected ResourcepackSounds.Chevron chevronOpenSounds;
	protected ResourcepackSounds.Chevron chevronEncodeSounds;

	public static final ResourcepackSounds.Rotation STARGATE_ROTATION_SOUNDS = new ResourcepackSounds.Rotation(STARGATE_RING_SPIN_START, STARGATE_RING_SPIN, STARGATE_RING_SPIN_STOP);
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	public static final ResourcepackSounds.Fail STARGATE_FAIL_SOUNDS = new ResourcepackSounds.Fail(STARGATE_FAIL);
	
	public static final MilkyWayStargateVariant DEFAULT_VARIANT = new MilkyWayStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS, GENERIC_MODEL,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_CHEVRON_OPEN_SOUNDS, STARGATE_CHEVRON_ENCODE_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS);
			
	public static final MilkyWayStargateVariant DEFAULT_BACK_VARIANT = new MilkyWayStargateVariant(STARGATE_TEXTURE_BACK, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE_BACK, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS, GENERIC_MODEL_BACK_CHEVRON,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_CHEVRON_OPEN_SOUNDS, STARGATE_CHEVRON_ENCODE_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS);
	
	public static final Codec<MilkyWayStargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Gate and chevron textures
			ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(MilkyWayStargateVariant::texture),
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.ofNullable(variant.encodedTexture)),
			ResourceLocation.CODEC.fieldOf(ENGAGED_TEXTURE).forGetter(MilkyWayStargateVariant::engagedTexture),
			// Wormholes
			ResourcepackModel.Wormhole.CODEC.fieldOf(WORMHOLE).forGetter(MilkyWayStargateVariant::wormhole),
			ResourcepackModel.Wormhole.CODEC.optionalFieldOf(SHINY_WORMHOLE).forGetter(MilkyWayStargateVariant::shinyWormhole),
			// Symbols
			ResourcepackModel.SymbolsModel.CODEC.fieldOf(SYMBOLS).forGetter(MilkyWayStargateVariant::symbols),
			// Model
			GenericStargateVariant.GenericStargateModel.MilkyWay.CODEC.fieldOf(STARGATE_MODEL).forGetter(variant -> (GenericStargateVariant.GenericStargateModel.MilkyWay) variant.stargateModel),
			// Sounds
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENGAGED_SOUNDS).forGetter(MilkyWayStargateVariant::chevronEngagedSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(MilkyWayStargateVariant::chevronIncomingSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_OPEN_SOUNDS).forGetter(MilkyWayStargateVariant::chevronOpenSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENCODE_SOUNDS).forGetter(MilkyWayStargateVariant::chevronEncodeSounds),
			
			ResourcepackSounds.Rotation.CODEC.fieldOf(ROTATION_SOUNDS).forGetter(MilkyWayStargateVariant::rotationSounds),
			ResourcepackSounds.Wormhole.CODEC.fieldOf(WORMHOLE_SOUNDS).forGetter(MilkyWayStargateVariant::wormholeSounds),
			ResourcepackSounds.Fail.CODEC.fieldOf(FAIL_SOUNDS).forGetter(MilkyWayStargateVariant::failSounds)
			).apply(instance, MilkyWayStargateVariant::new));
	
	public MilkyWayStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			GenericStargateModel stargateModel,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Chevron chevronOpenSounds, ResourcepackSounds.Chevron chevronEncodeSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, stargateModel,
				chevronEngagedSounds, chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
		
		this.chevronOpenSounds = chevronOpenSounds;
		this.chevronEncodeSounds = chevronEncodeSounds;
	}
	
	public ResourcepackSounds.Chevron chevronOpenSounds()
	{
		return chevronOpenSounds;
	}
	
	public ResourcepackSounds.Chevron chevronEncodeSounds()
	{
		return chevronEncodeSounds;
	}
}
