package net.povstalec.sgjourney.client.resourcepack;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.ColorUtil.RGBA;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class ResourcepackModel
{
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
	
	public static class Wormhole
	{
		
		public static final String DISTORTION = "distortion";
		public static final String HAS_STRUDEL = "has_strudel";
		public static final String EVENT_HORIZON = "event_horizon";
		public static final String KAWOOSH = "kawoosh";
		public static final String STRUDEL = "strudel";
		
		@Nullable
		private final Integer distortion;
		@Nullable
		private final Boolean hasStrudel;
		private final Either<FrontBack, WormholeTexture> eventHorizon;
		@Nullable
		private final Either<FrontBack, WormholeTexture> kawoosh;
		@Nullable
		private final Either<FrontBack, WormholeTexture> strudel;
		
		public static final Codec<Wormhole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.intRange(0, 25).optionalFieldOf(DISTORTION).forGetter(wormhole -> Optional.ofNullable(wormhole.distortion)),
				Codec.BOOL.optionalFieldOf(HAS_STRUDEL).forGetter(wormhole -> Optional.ofNullable(wormhole.hasStrudel)),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).fieldOf(EVENT_HORIZON).forGetter(Wormhole::eventHorizon),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).optionalFieldOf(KAWOOSH).forGetter(wormhole -> Optional.ofNullable(wormhole.kawoosh)),
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).optionalFieldOf(STRUDEL).forGetter(wormhole -> Optional.ofNullable(wormhole.strudel))
				).apply(instance, Wormhole::new));
		
		public Wormhole(Optional<Integer> distortion, Optional<Boolean> hasStrudel,
				Either<FrontBack, WormholeTexture> eventHorizon,
				Optional<Either<FrontBack, WormholeTexture>> kawoosh,
				Optional<Either<FrontBack, WormholeTexture>> strudel)
		{
			if(distortion.isPresent())
				this.distortion = distortion.get();
			else
				this.distortion = null;
			
			if(hasStrudel.isPresent())
				this.hasStrudel = hasStrudel.get();
			else
				this.hasStrudel = null;
			
			this.eventHorizon = eventHorizon;

			if(kawoosh.isPresent())
				this.kawoosh = kawoosh.get();
			else
				this.kawoosh = null;

			if(strudel.isPresent())
				this.strudel = strudel.get();
			else
				this.strudel = null;
		}
		
		public Wormhole(Either<FrontBack, WormholeTexture> eventHorizon)
		{
			this(Optional.empty(), Optional.empty(), eventHorizon, Optional.empty(), Optional.empty());
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
		
		public Either<FrontBack, WormholeTexture> eventHorizon()
		{
			return eventHorizon;
		}
		
		public Either<FrontBack, WormholeTexture> kawoosh()
		{
			return kawoosh != null ? kawoosh : eventHorizon;
		}
		
		public Either<FrontBack, WormholeTexture> strudel()
		{
			return strudel != null ? strudel : eventHorizon;
		}
		
		private static WormholeTexture getWormholeTexture(Either<FrontBack, WormholeTexture> eventHorizon, boolean front)
		{
			if(eventHorizon.left().isPresent())
				return front ? eventHorizon.left().get().front() : eventHorizon.left().get().back();
			
			return eventHorizon.right().get();
		}
		
		public WormholeTexture eventHorizonTexture(boolean front)
		{
			return getWormholeTexture(eventHorizon(), front);
		}
		
		public WormholeTexture kawooshTexture(boolean front)
		{
			return getWormholeTexture(kawoosh(), front);
		}
		
		public WormholeTexture strudelTexture(boolean front)
		{
			return getWormholeTexture(strudel(), front);
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
