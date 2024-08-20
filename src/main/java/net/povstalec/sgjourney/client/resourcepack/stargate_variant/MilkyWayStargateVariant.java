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

public class MilkyWayStargateVariant extends RotatingStargateVariant
{
	public static final ResourceLocation STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_stargate.png");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_stargate_engaged.png");

	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_event_horizon.png");
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/milky_way/milky_way_event_horizon_shiny.png");

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = new ResourceLocation(StargateJourney.MODID, "milky_way_chevron_engage");

	public static final ResourceLocation STARGATE_RING_SPIN_START = new ResourceLocation(StargateJourney.MODID, "milky_way_ring_spin_start");
	public static final ResourceLocation STARGATE_RING_SPIN = new ResourceLocation(StargateJourney.MODID, "milky_way_ring_spin");
	public static final ResourceLocation STARGATE_RING_SPIN_STOP = new ResourceLocation(StargateJourney.MODID, "milky_way_ring_spin_stop");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = new ResourceLocation(StargateJourney.MODID, "milky_way_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = new ResourceLocation(StargateJourney.MODID, "milky_way_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = new ResourceLocation(StargateJourney.MODID, "milky_way_wormhole_close");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 1, 32, 255),
			new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 1, 32, 190))));
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 1, 32, 255),
			new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 1, 32, 190))));
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(new ColorUtil.IntRGBA(48, 49, 63, 255));
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	// TODO Chevron open sound
	// TODO Chevron encode sound

	public static final ResourcepackSounds.Rotation STARGATE_ROTATION_SOUNDS = new ResourcepackSounds.Rotation(Optional.of(STARGATE_RING_SPIN_START), Optional.of(STARGATE_RING_SPIN), Optional.of(STARGATE_RING_SPIN_STOP));
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	
	public static final MilkyWayStargateVariant DEFAULT_VARIANT = new MilkyWayStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS);
	
	public static final Codec<MilkyWayStargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Gate and chevron textures
			ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(MilkyWayStargateVariant::texture),
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.of(variant.encodedTexture)),
			ResourceLocation.CODEC.fieldOf(ENGAGED_TEXTURE).forGetter(MilkyWayStargateVariant::engagedTexture),
			// Wormholes
			ResourcepackModel.Wormhole.CODEC.fieldOf(WORMHOLE).forGetter(MilkyWayStargateVariant::wormhole),
			ResourcepackModel.Wormhole.CODEC.optionalFieldOf(SHINY_WORMHOLE).forGetter(MilkyWayStargateVariant::shinyWormhole),
			// Symbols
			ResourcepackModel.SymbolsModel.CODEC.fieldOf(SYMBOLS).forGetter(MilkyWayStargateVariant::symbols),
			// Sounds
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENGAGED_SOUNDS).forGetter(MilkyWayStargateVariant::chevronEngagedSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(MilkyWayStargateVariant::chevronIncomingSounds),
			
			ResourcepackSounds.Rotation.CODEC.fieldOf(ROTATION_SOUNDS).forGetter(MilkyWayStargateVariant::rotationSounds),
			ResourcepackSounds.Wormhole.CODEC.fieldOf(WORMHOLE_SOUNDS).forGetter(MilkyWayStargateVariant::wormholeSounds)
			).apply(instance, MilkyWayStargateVariant::new));
	
	public MilkyWayStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, rotationSounds, wormholeSounds);
	}
	
}
