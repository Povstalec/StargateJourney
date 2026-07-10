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

public class ClassicStargateVariant extends RotatingStargateVariant
{
	// Variant: Normal RGB - Shiny RGB
	// Classic: 39 113 255 - 29 92 212
	
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

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = new ResourceLocation(StargateJourney.MODID, "classic_chevron_engage");
	
	public static final ResourceLocation STARGATE_RING_SPIN_START = new ResourceLocation(StargateJourney.MODID, "classic_ring_spin_start");
	public static final ResourceLocation STARGATE_RING_SPIN = new ResourceLocation(StargateJourney.MODID, "classic_ring_spin");
	public static final ResourceLocation STARGATE_RING_SPIN_STOP = new ResourceLocation(StargateJourney.MODID, "classic_ring_spin_stop");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = new ResourceLocation(StargateJourney.MODID, "classic_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = new ResourceLocation(StargateJourney.MODID, "classic_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = new ResourceLocation(StargateJourney.MODID, "classic_wormhole_close");

	public static final ResourceLocation STARGATE_FAIL = new ResourceLocation(StargateJourney.MODID, "classic_dial_fail");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_WORMHOLE_LOCATION, STARGATE_WORMHOLE_LOCATION_UNSTABLE, STARGATE_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_TRANSLUCENT_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = ResourcepackModel.Wormhole.simpleWormhole(STARGATE_SHINY_WORMHOLE_LOCATION, STARGATE_SHINY_WORMHOLE_UNSTABLE_LOCATION, STARGATE_SHINY_VORTEX_LOCATION, STARGATE_DISCONNECT_LOCATION, DEFAULT_TRANSLUCENT_RGBA, DEFAULT_TRANSLUCENT_RGBA);
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(new ColorUtil.RGBA(0, 109, 121, 255));
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	
	public static final ResourcepackSounds.Rotation STARGATE_ROTATION_SOUNDS = new ResourcepackSounds.Rotation(STARGATE_RING_SPIN_START, STARGATE_RING_SPIN, STARGATE_RING_SPIN_STOP);
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	public static final ResourcepackSounds.Fail STARGATE_FAIL_SOUNDS = new ResourcepackSounds.Fail(STARGATE_FAIL);
	
	public static final ClassicStargateVariant DEFAULT_VARIANT = new ClassicStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_ROTATION_SOUNDS, STARGATE_WROMHOLE_SOUNDS, STARGATE_FAIL_SOUNDS);
	
	public static final Codec<ClassicStargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Gate and chevron textures
			ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(ClassicStargateVariant::texture),
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.ofNullable(variant.encodedTexture)),
			ResourceLocation.CODEC.fieldOf(ENGAGED_TEXTURE).forGetter(ClassicStargateVariant::engagedTexture),
			// Wormholes
			ResourcepackModel.Wormhole.CODEC.fieldOf(WORMHOLE).forGetter(ClassicStargateVariant::wormhole),
			ResourcepackModel.Wormhole.CODEC.optionalFieldOf(SHINY_WORMHOLE).forGetter(ClassicStargateVariant::shinyWormhole),
			// Symbols
			ResourcepackModel.SymbolsModel.CODEC.fieldOf(SYMBOLS).forGetter(ClassicStargateVariant::symbols),
			// Sounds
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENGAGED_SOUNDS).forGetter(ClassicStargateVariant::chevronEngagedSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(ClassicStargateVariant::chevronIncomingSounds),
			
			ResourcepackSounds.Rotation.CODEC.fieldOf(ROTATION_SOUNDS).forGetter(ClassicStargateVariant::rotationSounds),
			ResourcepackSounds.Wormhole.CODEC.fieldOf(WORMHOLE_SOUNDS).forGetter(ClassicStargateVariant::wormholeSounds),
			ResourcepackSounds.Fail.CODEC.fieldOf(FAIL_SOUNDS).forGetter(ClassicStargateVariant::failSounds)
			).apply(instance, ClassicStargateVariant::new));
	
	public ClassicStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
	}
	
}
