package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackSounds;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.ColorUtil;

public class UniverseStargateVariant extends RotatingStargateVariant
{
	public static final String ONLY_FRONT_ROTATES = "only_front_rotates";
	
	public static final ResourceLocation STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_stargate.png");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_stargate_engaged.png");

	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_event_horizon.png");
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/universe/universe_event_horizon_shiny.png");

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = new ResourceLocation(StargateJourney.MODID, "universe_chevron_engage");

	public static final ResourceLocation STARGATE_DIAL_START = new ResourceLocation(StargateJourney.MODID, "universe_dial_start");
	public static final ResourceLocation STARGATE_RING_SPIN = new ResourceLocation(StargateJourney.MODID, "universe_ring_spin");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = new ResourceLocation(StargateJourney.MODID, "universe_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = new ResourceLocation(StargateJourney.MODID, "universe_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = new ResourceLocation(StargateJourney.MODID, "universe_wormhole_close");

	public static final ResourceLocation STARGATE_FAIL = new ResourceLocation(StargateJourney.MODID, "universe_dial_fail");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_OPAQUE_RGBA),
			new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_TRANSLUCENT_RGBA))));
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_OPAQUE_RGBA),
			new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 32, 1, 32, DEFAULT_TRANSLUCENT_RGBA))));
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(
			new ColorUtil.RGBA(21, 9, 0, 255), Optional.of(new ColorUtil.RGBA(200, 220, 255, 255)), Optional.of(new ColorUtil.RGBA(200, 220, 255, 255)),
			false, true, true,
			true, false,
			Optional.empty(), Optional.empty());
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);

	public static final ResourcepackSounds.Rotation STARGATE_ROTATION_SOUNDS = new ResourcepackSounds.Rotation(STARGATE_DIAL_START, STARGATE_RING_SPIN, StargateJourney.EMPTY_LOCATION);
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	public static final ResourcepackSounds.Fail STARGATE_FAIL_SOUNDS = new ResourcepackSounds.Fail(STARGATE_FAIL);
	
	public static final UniverseStargateVariant DEFAULT_VARIANT = new UniverseStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS, Optional.empty());
	
	@Nullable
	private Boolean onlyFrontRotates;
	
	public static final Codec<UniverseStargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Gate and chevron textures
			ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(UniverseStargateVariant::texture),
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.ofNullable(variant.encodedTexture)),
			ResourceLocation.CODEC.fieldOf(ENGAGED_TEXTURE).forGetter(UniverseStargateVariant::engagedTexture),
			// Wormholes
			ResourcepackModel.Wormhole.CODEC.fieldOf(WORMHOLE).forGetter(UniverseStargateVariant::wormhole),
			ResourcepackModel.Wormhole.CODEC.optionalFieldOf(SHINY_WORMHOLE).forGetter(UniverseStargateVariant::shinyWormhole),
			// Symbols
			ResourcepackModel.SymbolsModel.CODEC.fieldOf(SYMBOLS).forGetter(UniverseStargateVariant::symbols),
			// Sounds
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENGAGED_SOUNDS).forGetter(UniverseStargateVariant::chevronEngagedSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(UniverseStargateVariant::chevronIncomingSounds),
			
			ResourcepackSounds.Rotation.CODEC.fieldOf(ROTATION_SOUNDS).forGetter(UniverseStargateVariant::rotationSounds),
			ResourcepackSounds.Wormhole.CODEC.fieldOf(WORMHOLE_SOUNDS).forGetter(UniverseStargateVariant::wormholeSounds),
			ResourcepackSounds.Fail.CODEC.fieldOf(FAIL_SOUNDS).forGetter(UniverseStargateVariant::failSounds),
			
			Codec.BOOL.optionalFieldOf(ONLY_FRONT_ROTATES).forGetter(variant -> Optional.ofNullable(variant.onlyFrontRotates))
			).apply(instance, UniverseStargateVariant::new));
	
	public UniverseStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds,
			Optional<Boolean> onlyFrontRotates)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
		
		if(onlyFrontRotates.isPresent())
			this.onlyFrontRotates = onlyFrontRotates.get();
	}
	
	public boolean onlyFrontRotates()
	{
		if(onlyFrontRotates != null)
			return onlyFrontRotates;
		
		return ClientStargateConfig.universe_front_rotates.get();
	}
}
