package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.povstalec.sgjourney.StargateJourney;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SymbolTable
{
	public static final ResourceLocation SYMBOL_TABLES_LOCATION = StargateJourney.sgjourneyLocation("symbol_table");
	public static final ResourceKey<Registry<SymbolTable>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOL_TABLES_LOCATION);
	public static final Codec<ResourceKey<SymbolTable>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<SymbolTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			WeightedSymbol.CODEC.listOf().fieldOf("symbols").forGetter(SymbolTable::getSymbols)
			).apply(instance, SymbolTable::new));
	
	private final List<WeightedSymbol> symbols;
	
	public SymbolTable(List<WeightedSymbol> symbols)
	{
		this.symbols = symbols;
	}
	
	public List<WeightedSymbol> getSymbols()
	{
		return symbols;
	}
	
	@Nullable
	public static SymbolTable getSymbolTable(MinecraftServer server, @Nullable ResourceKey<SymbolTable> symbolTable)
	{
		if(symbolTable == null)
			return null;
		
		final RegistryAccess registries = server.registryAccess();
        final Registry<SymbolTable> registry = registries.registryOrThrow(SymbolTable.REGISTRY_KEY);
        
        return registry.get(symbolTable);
	}
	
	@Nullable
	public static Either<ResourceKey<PointOfOrigin>, Symbol> randomSymbol(RandomSource randomSource, @Nullable SymbolTable symbolTable)
	{
		if(symbolTable == null)
			return null;
		
		WeightedSymbol output = null;
		int totalWeight = 0;
		
		for(WeightedSymbol weightedSymbol : symbolTable.getSymbols())
		{
			totalWeight += weightedSymbol.weight();
			if(randomSource.nextFloat() <= (float) weightedSymbol.weight() / totalWeight)
				output = weightedSymbol;
		}
		
		if(output == null)
			return null;
		
		return output.eitherSymbol();
	}
	
	
	
	public static class Symbol
	{
		@Nullable
		private final ResourceKey<Symbols> symbols;
		private final int minSymbol;
		private final int maxSymbol;
		
		public Symbol(Optional<ResourceKey<Symbols>> symbols, int minSymbol, int maxSymbol)
		{
			if(minSymbol > maxSymbol)
				throw new IllegalArgumentException("min_symbol (" + minSymbol + ") may not be more than max_symbol (" + maxSymbol + ")");
			
			this.symbols = symbols.orElse(null);
			this.minSymbol = minSymbol;
			this.maxSymbol = maxSymbol;
		}
		
		public static final Codec<Symbol> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Symbols.RESOURCE_KEY_CODEC.optionalFieldOf("symbols").forGetter(symbol -> Optional.ofNullable(symbol.symbols)),
			Codec.intRange(1, Address.MAX_SYMBOL).fieldOf("min_symbol").forGetter(symbol -> symbol.minSymbol),
			Codec.intRange(1, Address.MAX_SYMBOL).fieldOf("max_symbol").forGetter(symbol -> symbol.maxSymbol)
		).apply(instance, Symbol::new));
		
		@Nullable
		public ResourceKey<Symbols> symbols()
		{
			return symbols;
		}
		
		public int minSymbol()
		{
			return minSymbol;
		}
		
		public int maxSymbol()
		{
			return maxSymbol;
		}
		
		public int getSymbolNumber(RandomSource randomSource)
		{
			return randomSource.nextIntBetweenInclusive(minSymbol, maxSymbol);
		}
	}
	
	
	
	public record WeightedSymbol(Either<ResourceKey<PointOfOrigin>, Symbol> eitherSymbol, int weight)
	{
		public static final Codec<WeightedSymbol> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.either(PointOfOrigin.RESOURCE_KEY_CODEC, Symbol.CODEC).fieldOf("symbol").forGetter(weightedSymbol -> weightedSymbol.eitherSymbol),
			Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("weight", 1).forGetter(weightedSymbol -> weightedSymbol.weight)
		).apply(instance, WeightedSymbol::new));
		
		public WeightedSymbol(ResourceKey<PointOfOrigin> pointOfOrigin, int weight)
		{
			this(Either.left(pointOfOrigin), weight);
		}
		
		public WeightedSymbol(Symbol symbol, int weight)
		{
			this(Either.right(symbol), weight);
		}
	}
}
