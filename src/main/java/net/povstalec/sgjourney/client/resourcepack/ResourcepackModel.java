package net.povstalec.sgjourney.client.resourcepack;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.Symbols;

public class ResourcepackModel
{
	public static class WormholeTexture
	{
		public static final String TEXTURE = "texture";
		public static final String ROWS = "rows";
		public static final String COLUMNS = "columns";
		public static final String ALPHA = "alpha";
		
		public static final Codec<WormholeTexture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf(TEXTURE).forGetter(WormholeTexture::texture),
				Codec.INT.fieldOf(ROWS).forGetter(WormholeTexture::rows),
				Codec.INT.fieldOf(COLUMNS).forGetter(WormholeTexture::columns),
				Codec.intRange(0, 255).optionalFieldOf(ALPHA, 255).forGetter(WormholeTexture::alpha)
				).apply(instance, WormholeTexture::new));
		
		private final ResourceLocation texture;
		private final int rows;
		private final int columns;
		private final int alpha;
		
		public WormholeTexture(ResourceLocation texture, int rows, int columns, int alpha)
		{
			this.texture = texture;
			this.rows = rows;
			this.columns = columns;
			this.alpha = alpha;
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
		
		public int alpha()
		{
			return alpha;
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
		public static final String EVENT_HORIZON = "event_horizon";
		
		private Either<FrontBack, WormholeTexture> eventHorizon;
		
		//private Either<FrontBack, WormholeTexture> kawoosh; //TODO Add separate kawoosh texture
		
		//private Either<FrontBack, WormholeTexture> strudel; //TODO Add separate strudel texture
		
		public static final Codec<Wormhole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.either(FrontBack.CODEC, WormholeTexture.CODEC).fieldOf(EVENT_HORIZON).forGetter(Wormhole::eventHorizon)
				).apply(instance, Wormhole::new));
		
		public Wormhole(Either<FrontBack, WormholeTexture> eventHorizon)
		{
			this.eventHorizon = eventHorizon;
		}
		
		public Either<FrontBack, WormholeTexture> eventHorizon()
		{
			return eventHorizon;
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

		public static final String PERMANENT_POINT_OF_ORIGIN = "permanent_point_of_origin";
		public static final String PERMANENT_SYMBOLS = "permanent_symbols";
		
		//Symbol stuff
		private final ColorUtil.IntRGBA symbolColor;
		private ColorUtil.IntRGBA encodedSymbolColor;
		private ColorUtil.IntRGBA engagedSymbolColor;
		
		@Nullable
		private Boolean symbolsGlow;
		private Boolean encodedSymbolsGlow;
		private Boolean engagedSymbolsGlow;
		
		@Nullable
		private ResourceKey<PointOfOrigin> permanentPointOfOrigin;
		@Nullable
		private ResourceKey<Symbols> permanentSymbols;
		
		public static final Codec<SymbolsModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				// Symbol Colors
				ColorUtil.IntRGBA.CODEC.fieldOf(SYMBOL_COLOR).forGetter(SymbolsModel::symbolColor),
				ColorUtil.IntRGBA.CODEC.optionalFieldOf(ENCODED_SYMBOL_COLOR).forGetter(symbols -> Optional.of(symbols.encodedSymbolColor)),
				ColorUtil.IntRGBA.CODEC.optionalFieldOf(ENGAGED_SYMBOL_COLOR).forGetter(symbols -> Optional.of(symbols.engagedSymbolColor)),
				// Symbol glow
				Codec.BOOL.optionalFieldOf(SYMBOLS_GLOW).forGetter(SymbolsModel::symbolsGlow),
				Codec.BOOL.optionalFieldOf(ENCODED_SYMBOLS_GLOW).forGetter(SymbolsModel::encodedSymbolsGlow),
				Codec.BOOL.optionalFieldOf(ENGAGED_SYMBOLS_GLOW).forGetter(SymbolsModel::engagedSymbolsGlow),
				// Permanent Symbols
				ResourceKey.codec(PointOfOrigin.REGISTRY_KEY).optionalFieldOf(PERMANENT_POINT_OF_ORIGIN).forGetter(SymbolsModel::permanentPointOfOrigin),
				ResourceKey.codec(Symbols.REGISTRY_KEY).optionalFieldOf(PERMANENT_SYMBOLS).forGetter(SymbolsModel::permanentSymbols)
				).apply(instance, SymbolsModel::new));
		
		public SymbolsModel(ColorUtil.IntRGBA symbolColor, Optional<ColorUtil.IntRGBA> encodedSymbolColor, Optional<ColorUtil.IntRGBA> engagedSymbolColor,
				Optional<Boolean> symbolsGlow, Optional<Boolean> encodedSymbolsGlow, Optional<Boolean> engagedSymbolsGlow,
				Optional<ResourceKey<PointOfOrigin>> permanentPointOfOrigin, Optional<ResourceKey<Symbols>> permanentSymbols)
		{
			this.symbolColor = symbolColor;
			
			if(encodedSymbolColor.isPresent())
				this.encodedSymbolColor = encodedSymbolColor.get();
			else
				this.encodedSymbolColor = symbolColor;
			
			if(engagedSymbolColor.isPresent())
				this.engagedSymbolColor = engagedSymbolColor.get();
			else
				this.engagedSymbolColor = symbolColor;

			if(permanentPointOfOrigin.isPresent())
				this.permanentPointOfOrigin = permanentPointOfOrigin.get();
			if(permanentSymbols.isPresent())
				this.permanentSymbols = permanentSymbols.get();
		}
		
		public ColorUtil.IntRGBA symbolColor()
		{
			return symbolColor;
		}
		
		public ColorUtil.IntRGBA encodedSymbolColor()
		{
			return encodedSymbolColor;
		}
		
		public ColorUtil.IntRGBA engagedSymbolColor()
		{
			return engagedSymbolColor;
		}
		
		
		
		public Optional<Boolean> symbolsGlow()
		{
			return Optional.ofNullable(symbolsGlow);
		}

		public Optional<Boolean> encodedSymbolsGlow()
		{
			return Optional.ofNullable(encodedSymbolsGlow);
		}

		public Optional<Boolean> engagedSymbolsGlow()
		{
			return Optional.ofNullable(engagedSymbolsGlow);
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
