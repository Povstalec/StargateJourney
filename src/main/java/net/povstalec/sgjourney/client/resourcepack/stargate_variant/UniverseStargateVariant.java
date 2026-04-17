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
	// Variant: Normal RGB - Shiny RGB
	// Universe: 148 186 254 - 73 89 120
	
	public static final String DIAL_START_SOUND = "dial_start_sound";
	public static final String ONLY_FRONT_ROTATES = "only_front_rotates";
	
	public static final String STARGATE_TYPE = "milky_way";
	
	public static final ResourceLocation STARGATE_TEXTURE = simpleTexturePath(STARGATE_TYPE, "stargate");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE = simpleTexturePath(STARGATE_TYPE, "stargate_engaged");
	
	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION = simpleTexturePath(STARGATE_TYPE, "event_horizon");
	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION_UNSTABLE = simpleTexturePath(STARGATE_TYPE, "event_horizon_unstable");
	public static final ResourceLocation STARGATE_VORTEX_LOCATION = simpleTexturePath(STARGATE_TYPE, "vortex");
	
	public static final ResourceLocation STARGATE_DISCONNECT_LOCATION = simpleTexturePath(STARGATE_TYPE, "disconnect");
	
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_LOCATION = simpleTexturePath(STARGATE_TYPE, "event_horizon_shiny");
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_UNSTABLE_LOCATION = simpleTexturePath(STARGATE_TYPE, "event_horizon_shiny_unstable");
	public static final ResourceLocation STARGATE_SHINY_VORTEX_LOCATION = simpleTexturePath(STARGATE_TYPE, "vortex_shiny");

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = new ResourceLocation(StargateJourney.MODID, "universe_chevron_engage");

	public static final ResourceLocation STARGATE_DIAL_START = new ResourceLocation(StargateJourney.MODID, "universe_dial_start"); //TODO Take care of this one
	public static final ResourceLocation STARGATE_RING_SPIN_START = new ResourceLocation(StargateJourney.MODID, "universe_ring_spin_start");
	public static final ResourceLocation STARGATE_RING_SPIN = new ResourceLocation(StargateJourney.MODID, "universe_ring_spin");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = new ResourceLocation(StargateJourney.MODID, "universe_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = new ResourceLocation(StargateJourney.MODID, "universe_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = new ResourceLocation(StargateJourney.MODID, "universe_wormhole_close");

	public static final ResourceLocation STARGATE_FAIL = new ResourceLocation(StargateJourney.MODID, "universe_dial_fail");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_WORMHOLE_LOCATION, STARGATE_WORMHOLE_LOCATION_UNSTABLE, STARGATE_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_OPAQUE_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_SHINY_WORMHOLE_LOCATION, STARGATE_SHINY_WORMHOLE_UNSTABLE_LOCATION, STARGATE_SHINY_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_OPAQUE_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(
			new ColorUtil.RGBA(21, 9, 0, 255), Optional.of(new ColorUtil.RGBA(200, 220, 255, 255)), Optional.of(new ColorUtil.RGBA(200, 220, 255, 255)),
			false, true, true,
			true, false,
			Optional.empty(), Optional.empty());
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(StargateJourney.EMPTY_LOCATION);

	public static final ResourcepackSounds.Rotation STARGATE_ROTATION_SOUNDS = new ResourcepackSounds.Rotation(STARGATE_RING_SPIN_START, STARGATE_RING_SPIN, StargateJourney.EMPTY_LOCATION);
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	public static final ResourcepackSounds.Fail STARGATE_FAIL_SOUNDS = new ResourcepackSounds.Fail(STARGATE_FAIL);
	
	public static final UniverseStargateVariant DEFAULT_VARIANT = new UniverseStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS, STARGATE_DIAL_START, Optional.empty());
	
	private ResourceLocation dialStartSound;
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
			
			ResourceLocation.CODEC.optionalFieldOf(DIAL_START_SOUND, STARGATE_DIAL_START).forGetter(UniverseStargateVariant::dialStartSound),
			Codec.BOOL.optionalFieldOf(ONLY_FRONT_ROTATES).forGetter(variant -> Optional.ofNullable(variant.onlyFrontRotates))
			).apply(instance, UniverseStargateVariant::new));
	
	public UniverseStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds,
			ResourceLocation dialStartSound, Optional<Boolean> onlyFrontRotates)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
		
		this.dialStartSound = dialStartSound;
		
		if(onlyFrontRotates.isPresent())
			this.onlyFrontRotates = onlyFrontRotates.get();
	}
	
	public ResourceLocation dialStartSound()
	{
		return dialStartSound;
	}
	
	public boolean onlyFrontRotates()
	{
		if(onlyFrontRotates != null)
			return onlyFrontRotates;
		
		return ClientStargateConfig.universe_front_rotates.get();
	}
}
