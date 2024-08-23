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
	public static final ResourceLocation STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_stargate.png");
	public static final ResourceLocation STARGATE_TEXTURE_BACK = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_stargate_back_chevron.png");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_stargate_engaged.png");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE_BACK = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_stargate_back_chevron_engaged.png");

	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_event_horizon.png");
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/pegasus/pegasus_event_horizon_shiny.png");

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = new ResourceLocation(StargateJourney.MODID, "pegasus_chevron_engage");
	public static final ResourceLocation STARGATE_CHEVRON_INCOMING = new ResourceLocation(StargateJourney.MODID, "pegasus_chevron_incoming");

	public static final ResourceLocation STARGATE_RING_SPIN = new ResourceLocation(StargateJourney.MODID, "pegasus_ring_spin");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = new ResourceLocation(StargateJourney.MODID, "pegasus_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = new ResourceLocation(StargateJourney.MODID, "pegasus_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = new ResourceLocation(StargateJourney.MODID, "pegasus_wormhole_close");

	public static final ResourceLocation STARGATE_FAIL = new ResourceLocation(StargateJourney.MODID, "pegasus_dial_fail");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 1, 32, 1F),
			new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 1, 32, 0.75F))));
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 1, 32, 1F),
			new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 1, 32, 0.75F))));
	
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
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.of(variant.encodedTexture)),
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
