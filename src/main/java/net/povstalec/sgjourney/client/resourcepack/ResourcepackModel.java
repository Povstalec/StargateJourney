package net.povstalec.sgjourney.client.resourcepack;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.ColorUtil.RGBA;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class ResourcepackModel
{
	public enum WormholeSide
	{
		FRONT,
		BACK
	}
	
	public static class WormholeTexture
	{
		public static final String TEXTURE = "texture";
		public static final String ROWS = "rows";
		public static final String COLUMNS = "columns";
		public static final String FRAMES = "frames";
		public static final String RGBA = "rgba";

		public static final RGBA DEFAULT_OPAQUE_RGBA = new RGBA(1F, 1F, 1F, 1F);
		
		public static final Codec<WormholeTexture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(WormholeTexture::texture),
				Codec.intRange(1, Integer.MAX_VALUE).fieldOf(ROWS).forGetter(WormholeTexture::rows),
				Codec.intRange(1, Integer.MAX_VALUE).fieldOf(COLUMNS).forGetter(WormholeTexture::columns),
				Codec.intRange(1, Integer.MAX_VALUE).fieldOf(FRAMES).forGetter(WormholeTexture::columns),
				ColorUtil.RGBA.CODEC.optionalFieldOf(RGBA, DEFAULT_OPAQUE_RGBA).forGetter(WormholeTexture::rgba)
				).apply(instance, WormholeTexture::new));
		
		private final ResourceLocation texture;
		private final int rows;
		private final int columns;
		private final int frames;
		private final ColorUtil.RGBA rgba;
		
		private final float uScale;
		private final float vScale;
		
		public WormholeTexture(ResourceLocation texture, int rows, int columns, int frames, ColorUtil.RGBA rgba)
		{
			this.texture = texture;
			this.rows = rows;
			this.columns = columns;
			this.frames = frames;
			this.rgba = rgba;
			
			this.uScale = 1F / columns;
			this.vScale = 1F / rows;
		}
		
		public ResourceLocation texture()
		{
			return texture;
		}
		
		public int rows()
		{
			return rows;
		}
		
		public int columns()
		{
			return columns;
		}
		
		public int frames()
		{
			return frames;
		}
		
		public ColorUtil.RGBA rgba()
		{
			return rgba;
		}
		
		
		
		public float uScale()
		{
			return uScale;
		}
		
		public float vScale()
		{
			return vScale;
		}
		
		public int frame(int tick)
		{
			return tick % frames;
		}
		
		public float uOffset(int frame)
		{
			int xOffset = frame / rows;
			
			return (float) (xOffset % columns) / columns;
		}
		
		public float vOffset(int frame)
		{
			return (float) (frame % rows) / rows;
		}
	}
	
	public static class FrontBack
	{
		public static final String FRONT = "front";
		public static final String BACK = "back";
		
		public static final Codec<FrontBack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				WormholeTexture.CODEC.fieldOf(FRONT).forGetter(FrontBack::front),
				WormholeTexture.CODEC.fieldOf(BACK).forGetter(FrontBack::back)
				).apply(instance, FrontBack::new));
		
		private final WormholeTexture front;
		private final WormholeTexture back;
		
		public FrontBack(WormholeTexture front, WormholeTexture back)
		{
			this.front = front;
			this.back = back;
		}
		
		public WormholeTexture front()
		{
			return front;
		}
		
		public WormholeTexture back()
		{
			return back;
		}
	}
	
	public static class DisconnectTicks
	{
		public static final String WAIT_TICKS = "wait_ticks";
		public static final String FADE_IN_TICKS = "fade_in_ticks";
		public static final String STABLE_TICKS = "stable_ticks";
		public static final String FADE_OUT_TICKS = "fade_out_ticks";
		
		public static final DisconnectTicks DEFAULT = new DisconnectTicks(16, 10, 0, 20);
		
		public static final Codec<DisconnectTicks> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.intRange(0, Integer.MAX_VALUE).fieldOf(WAIT_TICKS).forGetter(disconnectTicks -> disconnectTicks.waitTicks),
				Codec.intRange(0, Integer.MAX_VALUE).fieldOf(FADE_IN_TICKS).forGetter(disconnectTicks -> disconnectTicks.fadeInTicks),
				Codec.intRange(0, Integer.MAX_VALUE).fieldOf(STABLE_TICKS).forGetter(disconnectTicks -> disconnectTicks.stableTicks),
				Codec.intRange(0, Integer.MAX_VALUE).fieldOf(FADE_OUT_TICKS).forGetter(disconnectTicks -> disconnectTicks.fadeOutTicks)
				
		).apply(instance, DisconnectTicks::new));
		
		public final int waitTicks; // Wait this many ticks before you start the animation (does not affect the wormhole wiggling that's there as an indicator that you can't go through the gate anymore)
		public final int fadeInTicks; // How many ticks it takes for the texture to fully fade in after
		public final int stableTicks; // How many ticks the texture remains fully visible
		public final int fadeOutTicks; // How many ticks it takes for the texture to fade out, after which the animation is considered finished
		
		public final int eventHorizonStopTicks;
		public final int disconnectEndTicks;
		
		public DisconnectTicks(int waitTicks, int fadeInTicks, int stableTicks, int fadeOutTicks)
		{
			this.waitTicks = waitTicks;
			this.fadeInTicks = fadeInTicks;
			this.stableTicks = stableTicks;
			this.fadeOutTicks = fadeOutTicks;
			
			this.eventHorizonStopTicks = waitTicks + fadeInTicks;
			this.disconnectEndTicks = waitTicks + fadeInTicks + stableTicks + fadeOutTicks;
		}
	}
	
	public static class Wormhole
	{
		public static final String DISTORTION = "distortion";
		public static final String HAS_STRUDEL = "has_strudel";
		public static final String DISCONNECT_TICKS = "disconnect_ticks";
		
		public static final String EVENT_HORIZON = "event_horizon";
		public static final String KAWOOSH = "kawoosh";
		public static final String STRUDEL = "strudel";
		public static final String UNSTABLE = "event_horizon_unstable";
		public static final String DISCONNECT = "disconnect";
		
		@Nullable
		private final Integer distortion;
		@Nullable
		private final Boolean hasStrudel;
		private final DisconnectTicks disconnectTicks;
		
		private final Either<FrontBack, WormholeTexture> eventHorizon;
		@Nullable
		private final Either<FrontBack, WormholeTexture> unstableEventHorizon;
		@Nullable
		private final Either<FrontBack, WormholeTexture> kawoosh;
		@Nullable
		private final Either<FrontBack, WormholeTexture> strudel;
		
		private final Either<FrontBack, WormholeTexture> disconnect;
		
		public static final Codec<Wormhole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.intRange(0, 25).optionalFieldOf(DISTORTION).forGetter(wormhole -> Optional.ofNullable(wormhole.distortion)),
				Codec.BOOL.optionalFieldOf(HAS_STRUDEL).forGetter(wormhole -> Optional.ofNullable(wormhole.hasStrudel)),
				DisconnectTicks.CODEC.optionalFieldOf(DISCONNECT_TICKS, new DisconnectTicks(0, 0, 0, 0)).forGetter(wormhole -> wormhole.disconnectTicks),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).fieldOf(EVENT_HORIZON).forGetter(Wormhole::eventHorizon),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).optionalFieldOf(UNSTABLE).forGetter(wormhole -> Optional.ofNullable(wormhole.unstableEventHorizon)),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).optionalFieldOf(KAWOOSH).forGetter(wormhole -> Optional.ofNullable(wormhole.kawoosh)),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).optionalFieldOf(STRUDEL).forGetter(wormhole -> Optional.ofNullable(wormhole.strudel)),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).optionalFieldOf(DISCONNECT).forGetter(wormhole -> Optional.ofNullable(wormhole.disconnect))
				).apply(instance, Wormhole::new));
		
		public Wormhole(Optional<Integer> distortion, Optional<Boolean> hasStrudel, DisconnectTicks disconnectTicks,
						Either<FrontBack, WormholeTexture> eventHorizon, Optional<Either<FrontBack, WormholeTexture>> unstableEventHorizon, Optional<Either<FrontBack, WormholeTexture>> kawoosh,
				Optional<Either<FrontBack, WormholeTexture>> strudel, Optional<Either<FrontBack, WormholeTexture>> disconnect)
		{
			this.distortion = distortion.orElse(null);
			this.hasStrudel = hasStrudel.orElse(null);
			this.disconnectTicks = disconnectTicks;
			
			this.eventHorizon = eventHorizon;
			this.unstableEventHorizon = unstableEventHorizon.orElse(null);
			this.kawoosh = kawoosh.orElse(null);
			this.strudel = strudel.orElse(null);
			this.disconnect = disconnect.orElse(null);
		}
		
		public int distortion()
		{
			if(distortion != null)
				return distortion;
			
			return ClientStargateConfig.event_horizon_distortion.get();
		}
		
		public boolean hasStrudel()
		{
			if(hasStrudel != null)
				return hasStrudel;
			
			return ClientStargateConfig.enable_vortex.get();
		}
		
		public DisconnectTicks disconnectTicks()
		{
			return disconnectTicks;
		}
		
		public Either<FrontBack, WormholeTexture> eventHorizon()
		{
			return eventHorizon;
		}
		
		public Either<FrontBack, WormholeTexture> unstableEventHorizon()
		{
			return unstableEventHorizon != null ? unstableEventHorizon : eventHorizon;
		}
		
		public Either<FrontBack, WormholeTexture> kawoosh()
		{
			return kawoosh != null ? kawoosh : eventHorizon;
		}
		
		public Either<FrontBack, WormholeTexture> strudel()
		{
			return strudel != null ? strudel : eventHorizon;
		}
		
		@Nullable
		public Either<FrontBack, WormholeTexture> disconnect()
		{
			return disconnect;
		}
		
		private static WormholeTexture getWormholeTexture(Either<FrontBack, WormholeTexture> eventHorizon, WormholeSide side)
		{
			if(eventHorizon.left().isPresent())
				return side == WormholeSide.FRONT ? eventHorizon.left().get().front() : eventHorizon.left().get().back();
			
			return eventHorizon.right().get();
		}
		
		public WormholeTexture eventHorizonTexture(WormholeSide side)
		{
			return getWormholeTexture(eventHorizon(), side);
		}
		
		public WormholeTexture unstableEventHorizonTexture(WormholeSide side)
		{
			return getWormholeTexture(unstableEventHorizon(), side);
		}
		
		public WormholeTexture kawooshTexture(WormholeSide side)
		{
			return getWormholeTexture(kawoosh(), side);
		}
		
		public WormholeTexture strudelTexture(WormholeSide side)
		{
			return getWormholeTexture(strudel(), side);
		}
		
		public WormholeTexture disconnectTexture(WormholeSide side)
		{
			return getWormholeTexture(disconnect(), side);
		}
		
		public static Either<FrontBack, WormholeTexture> simpleWormholeEither(ResourceLocation texture, int rows, int columns, int frames, RGBA frontRGBA, RGBA backRGBA)
		{
			return Either.left(new ResourcepackModel.FrontBack(new ResourcepackModel.WormholeTexture(texture, rows, columns, frames, frontRGBA),
					new ResourcepackModel.WormholeTexture(texture, rows, columns, frames, backRGBA)));
		}
		
		public static Wormhole simpleWormhole(ResourceLocation eventHorizonTexture, ResourceLocation unstableEventHorizonTexture, ResourceLocation vortexTexture, ResourceLocation disconnectTexture, RGBA frontRGBA, RGBA backRGBA)
		{
			return new Wormhole(
					Optional.empty(),
					Optional.empty(),
					DisconnectTicks.DEFAULT,
					simpleWormholeEither(eventHorizonTexture, 32, 1, 32, frontRGBA, backRGBA),
					Optional.of(simpleWormholeEither(unstableEventHorizonTexture, 32, 1, 32, WormholeTexture.DEFAULT_OPAQUE_RGBA, WormholeTexture.DEFAULT_OPAQUE_RGBA)),
					Optional.empty(),
					Optional.of(simpleWormholeEither(vortexTexture, 32, 5, 160, WormholeTexture.DEFAULT_OPAQUE_RGBA, WormholeTexture.DEFAULT_OPAQUE_RGBA)),
					Optional.of(simpleWormholeEither(disconnectTexture, 20, 1, 20, WormholeTexture.DEFAULT_OPAQUE_RGBA, WormholeTexture.DEFAULT_OPAQUE_RGBA)));
		}
	}
	
	public static class SymbolsModel
	{
		public static final String SYMBOL_COLOR = "symbol_color";
		public static final String ENCODED_SYMBOL_COLOR = "encoded_symbol_color";
		public static final String ENGAGED_SYMBOL_COLOR = "engaged_symbol_color";

		public static final String SYMBOLS_GLOW = "symbols_glow";
		public static final String ENCODED_SYMBOLS_GLOW = "encoded_symbols_glow";
		public static final String ENGAGED_SYMBOLS_GLOW = "engaged_symbols_glow";

		public static final String ENGAGE_ENCODED_SYMBOLS = "engage_encoded_symbols";
		public static final String ENGAGE_SYMBOLS_ON_INCOMING = "engage_symbols_on_incoming";

		public static final String PERMANENT_POINT_OF_ORIGIN = "permanent_point_of_origin";
		public static final String PERMANENT_SYMBOLS = "permanent_symbols";
		
		//Symbol stuff
		private final ColorUtil.RGBA symbolColor;
		private final ColorUtil.RGBA encodedSymbolColor;
		private final ColorUtil.RGBA engagedSymbolColor;
		
		private final boolean symbolsGlow;
		private final boolean encodedSymbolsGlow;
		private final boolean engagedSymbolsGlow;
		
		private final boolean engageEncodedSymbols; //TODO Is this needed?
		private final boolean engageSymbolsOnIncoming; //TODO Is this needed?
		
		@Nullable
		private ResourceKey<PointOfOrigin> permanentPointOfOrigin;
		@Nullable
		private ResourceKey<Symbols> permanentSymbols;
		
		public static final Codec<SymbolsModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				// Symbol Colors
				ColorUtil.RGBA.CODEC.fieldOf(SYMBOL_COLOR).forGetter(SymbolsModel::symbolColor),
				ColorUtil.RGBA.CODEC.optionalFieldOf(ENCODED_SYMBOL_COLOR).forGetter(symbols -> Optional.ofNullable(symbols.encodedSymbolColor)),
				ColorUtil.RGBA.CODEC.optionalFieldOf(ENGAGED_SYMBOL_COLOR).forGetter(symbols -> Optional.ofNullable(symbols.engagedSymbolColor)),
				// Symbol glow
				Codec.BOOL.optionalFieldOf(SYMBOLS_GLOW, false).forGetter(symbols -> symbols.symbolsGlow),
				Codec.BOOL.optionalFieldOf(ENCODED_SYMBOLS_GLOW, false).forGetter(symbols -> symbols.encodedSymbolsGlow),
				Codec.BOOL.optionalFieldOf(ENGAGED_SYMBOLS_GLOW, false).forGetter(symbols -> symbols.engagedSymbolsGlow),
				//TODO Split incoming and outgoing glows and colors
				Codec.BOOL.optionalFieldOf(ENGAGE_ENCODED_SYMBOLS, false).forGetter(symbols -> symbols.engageEncodedSymbols),
				Codec.BOOL.optionalFieldOf(ENGAGE_SYMBOLS_ON_INCOMING, false).forGetter(symbols -> symbols.engageSymbolsOnIncoming),
				// Permanent Symbols
				ResourceKey.codec(PointOfOrigin.REGISTRY_KEY).optionalFieldOf(PERMANENT_POINT_OF_ORIGIN).forGetter(SymbolsModel::permanentPointOfOrigin),
				ResourceKey.codec(Symbols.REGISTRY_KEY).optionalFieldOf(PERMANENT_SYMBOLS).forGetter(SymbolsModel::permanentSymbols)
				).apply(instance, SymbolsModel::new));
		
		public SymbolsModel(ColorUtil.RGBA symbolColor, Optional<ColorUtil.RGBA> encodedSymbolColor, Optional<ColorUtil.RGBA> engagedSymbolColor,
				boolean symbolsGlow, boolean encodedSymbolsGlow, boolean engagedSymbolsGlow,
				boolean engageEncodedSymbols, boolean engageSymbolsOnIncoming,
				Optional<ResourceKey<PointOfOrigin>> permanentPointOfOrigin, Optional<ResourceKey<Symbols>> permanentSymbols)
		{
			this.symbolColor = symbolColor;
			
			if(engagedSymbolColor.isPresent())
				this.engagedSymbolColor = engagedSymbolColor.get();
			else
				this.engagedSymbolColor = symbolColor;
			
			// Encoded symbol color, if not specified, will be the same as engaged symbol color
			if(encodedSymbolColor.isPresent())
				this.encodedSymbolColor = encodedSymbolColor.get();
			else
				this.encodedSymbolColor = this.engagedSymbolColor;
			
			this.symbolsGlow = symbolsGlow;
			this.encodedSymbolsGlow = encodedSymbolsGlow;
			this.engagedSymbolsGlow = engagedSymbolsGlow;
			
			this.engageEncodedSymbols = engageEncodedSymbols;
			this.engageSymbolsOnIncoming = engageSymbolsOnIncoming;

			if(permanentPointOfOrigin.isPresent())
				this.permanentPointOfOrigin = permanentPointOfOrigin.get();
			if(permanentSymbols.isPresent())
				this.permanentSymbols = permanentSymbols.get();
		}
		
		public SymbolsModel(ColorUtil.RGBA symbolColor)
		{
			this(symbolColor, Optional.empty(), Optional.empty(), false, false, false, false, false, Optional.empty(), Optional.empty());
		}
		
		public ColorUtil.RGBA symbolColor()
		{
			return symbolColor;
		}
		
		public ColorUtil.RGBA encodedSymbolColor()
		{
			return encodedSymbolColor;
		}
		
		public ColorUtil.RGBA engagedSymbolColor()
		{
			return engagedSymbolColor;
		}
		
		
		
		public boolean symbolsGlow()
		{
			return symbolsGlow;
		}

		public boolean encodedSymbolsGlow()
		{
			return encodedSymbolsGlow;
		}

		public boolean engagedSymbolsGlow()
		{
			return engagedSymbolsGlow;
		}
		
		
		
		public boolean engageEncodedSymbols()
		{
			return engageEncodedSymbols;
		}
		
		public boolean engageSymbolsOnIncoming()
		{
			return engageSymbolsOnIncoming;
		}
		
		
		
		public Optional<ResourceKey<PointOfOrigin>> permanentPointOfOrigin()
		{
			return Optional.ofNullable(permanentPointOfOrigin);
		}
		
		public Optional<ResourceKey<Symbols>> permanentSymbols()
		{
			return Optional.ofNullable(permanentSymbols);
		}
	}
}
