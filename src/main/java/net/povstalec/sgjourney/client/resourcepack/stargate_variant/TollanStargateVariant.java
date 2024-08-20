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

public class TollanStargateVariant extends ClientStargateVariant
{
	public static final ResourceLocation STARGATE_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_stargate.png");
	public static final ResourceLocation STARGATE_ENGAGED_TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_stargate_engaged.png");

	public static final ResourceLocation STARGATE_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_event_horizon.png");
	public static final ResourceLocation STARGATE_SHINY_WORMHOLE_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/tollan/tollan_event_horizon_shiny.png");

	public static final ResourceLocation STARGATE_CHEVRON_ENGAGE = new ResourceLocation(StargateJourney.MODID, "tollan_chevron_engage");

	public static final ResourceLocation STARGATE_WORMHOLE_OPEN = new ResourceLocation(StargateJourney.MODID, "tollan_wormhole_open");
	public static final ResourceLocation STARGATE_WORMHOLE_IDLE = new ResourceLocation(StargateJourney.MODID, "tollan_wormhole_idle");
	public static final ResourceLocation STARGATE_WORMHOLE_CLOSE = new ResourceLocation(StargateJourney.MODID, "tollan_wormhole_close");
	
	public static final ResourcepackModel.Wormhole STARGATE_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 1, 32, 255),
			new ResourcepackModel.WormholeTexture(STARGATE_WORMHOLE_LOCATION, 1, 32, 190))));
	public static final ResourcepackModel.Wormhole STARGATE_SHINY_WORMHOLE_TEXTURE = new ResourcepackModel.Wormhole(Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 1, 32, 255),
			new ResourcepackModel.WormholeTexture(STARGATE_SHINY_WORMHOLE_LOCATION, 1, 32, 190))));
	
	public static final ResourcepackModel.SymbolsModel STARGATE_SYMBOLS = new ResourcepackModel.SymbolsModel(new ColorUtil.IntRGBA(255, 255, 255, 255));
	
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_ENGAGED_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	public static final ResourcepackSounds.Chevron STARGATE_CHEVRON_INCOMING_SOUNDS = new ResourcepackSounds.Chevron(STARGATE_CHEVRON_ENGAGE);
	
	public static final ResourcepackSounds.Wormhole STARGATE_WROMHOLE_SOUNDS = new ResourcepackSounds.Wormhole(Either.right(STARGATE_WORMHOLE_OPEN), Either.right(STARGATE_WORMHOLE_IDLE), Either.right(STARGATE_WORMHOLE_CLOSE));
	
	public static final TollanStargateVariant DEFAULT_VARIANT = new TollanStargateVariant(STARGATE_TEXTURE, Optional.empty(),
			STARGATE_ENGAGED_TEXTURE, STARGATE_WORMHOLE_TEXTURE, Optional.of(STARGATE_SHINY_WORMHOLE_TEXTURE), STARGATE_SYMBOLS,
			STARGATE_CHEVRON_ENGAGED_SOUNDS, STARGATE_CHEVRON_INCOMING_SOUNDS, STARGATE_WROMHOLE_SOUNDS);
	
	public static final Codec<TollanStargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			// Gate and chevron textures
			ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(TollanStargateVariant::texture),
			ResourceLocation.CODEC.optionalFieldOf(ENCODED_TEXTURE).forGetter(variant -> Optional.of(variant.encodedTexture)),
			ResourceLocation.CODEC.fieldOf(ENGAGED_TEXTURE).forGetter(TollanStargateVariant::engagedTexture),
			// Wormholes
			ResourcepackModel.Wormhole.CODEC.fieldOf(WORMHOLE).forGetter(TollanStargateVariant::wormhole),
			ResourcepackModel.Wormhole.CODEC.optionalFieldOf(SHINY_WORMHOLE).forGetter(TollanStargateVariant::shinyWormhole),
			// Symbols
			ResourcepackModel.SymbolsModel.CODEC.fieldOf(SYMBOLS).forGetter(TollanStargateVariant::symbols),
			// Sounds
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_ENGAGED_SOUNDS).forGetter(TollanStargateVariant::chevronEngagedSounds),
			ResourcepackSounds.Chevron.CODEC.fieldOf(CHEVRON_INCOMING_SOUNDS).forGetter(TollanStargateVariant::chevronIncomingSounds),
			
			ResourcepackSounds.Wormhole.CODEC.fieldOf(WORMHOLE_SOUNDS).forGetter(TollanStargateVariant::wormholeSounds)
			).apply(instance, TollanStargateVariant::new));
	
	public TollanStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Wormhole wormholeSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, wormholeSounds);
	}
	
}
