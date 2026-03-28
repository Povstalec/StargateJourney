package net.povstalec.sgjourney.common.sgjourney;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Symbols
{
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbol/error");
	
	public static final ResourceLocation UNIVERSAL_LOCATION = new ResourceLocation(StargateJourney.MODID, "universal");
	
	public static final ResourceLocation SYMBOLS_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbols");
	public static final ResourceKey<Registry<Symbols>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOLS_LOCATION);
	public static final Codec<ResourceKey<Symbols>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<Symbols> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Symbols::getName),
			SymbolSet.RESOURCE_KEY_CODEC.optionalFieldOf("symbol_set").forGetter(Symbols::getSymbolSet),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(symbols -> symbols.textures)
			).apply(instance, Symbols::new));
	
	private final String name;
	private final Optional<ResourceKey<SymbolSet>> symbolSet;
	private final ArrayList<ResourceLocation> textures;
	
	public Symbols(String name, Optional<ResourceKey<SymbolSet>> symbolSet, List<ResourceLocation> textures)
	{
		this.name = name;
		this.symbolSet = symbolSet;
		this.textures = new ArrayList<>(textures);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getTranslationName()
	{
		if(useSymbolSet())
		{
			SymbolSet symbolSet = SymbolSet.getClientSymbolSet(this.symbolSet.get());
			
			return symbolSet.getName();
		}
		
		return this.name;
	}
	
	public boolean useSymbolSet()
	{
		return !ClientStargateConfig.unique_symbols.get() && this.symbolSet.isPresent();
	}
	
	public static String symbolsOrSet()
	{
		return ClientStargateConfig.unique_symbols.get() ? "info.sgjourney.symbols" :  "info.sgjourney.symbol_set";
	}
	
	public Optional<ResourceKey<SymbolSet>> getSymbolSet()
	{
		return this.symbolSet;
	}
	
	public int getSize()
	{
		return this.textures.size();
	}
	
	public ResourceLocation getSymbolTexture(int symbol)
	{
		if(useSymbolSet())
		{
			SymbolSet symbolSet = SymbolSet.getClientSymbolSet(this.symbolSet.get());
			
			if(symbolSet != null)
				return symbolSet.getSymbolTexture(symbol);
		}
		
		symbol--;
		if(symbol < 0 || symbol >= textures.size())
			return ERROR_LOCATION;
		
		return textures.get(symbol);
	}
	
	public boolean shouldRenderSymbol(int symbol)
	{
		if(useSymbolSet())
		{
			SymbolSet symbolSet = SymbolSet.getClientSymbolSet(this.symbolSet.get());
			
			if(symbolSet != null)
				return symbolSet.shouldRenderSymbol(symbol);
		}
		
		if(symbol >= 0 && symbol < textures.size())
			return true;
		
		return false;
	}
	
	public static ResourceKey<Symbols> defaultSymbols()
	{
		return Conversion.locationToSymbols(UNIVERSAL_LOCATION);
	}
	
	public static boolean validLocation(MinecraftServer server, ResourceLocation symbols)
	{
		if(symbols == null || StargateJourney.EMPTY_LOCATION.equals(symbols))
			return false;
		
		RegistryAccess registries = server.registryAccess();
		Registry<Symbols> symbolRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		return symbolRegistry.containsKey(symbols);
	}
	
	public static ResourceLocation fromDimension(MinecraftServer server, ResourceKey<Level> dimension)
	{
		return Universe.get(server).getSymbols(dimension).location();
	}
}
