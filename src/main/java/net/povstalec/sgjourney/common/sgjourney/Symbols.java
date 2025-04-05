package net.povstalec.sgjourney.common.sgjourney;

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
	public static final ResourceLocation ERROR_LOCATION = StargateJourney.sgjourneyLocation("textures/symbols/error.png");
	
	public static final ResourceLocation UNIVERSAL_LOCATION = StargateJourney.sgjourneyLocation("universal");
	
	public static final ResourceLocation SYMBOLS_LOCATION = StargateJourney.sgjourneyLocation("symbols");
	public static final ResourceKey<Registry<Symbols>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOLS_LOCATION);
	public static final Codec<ResourceKey<Symbols>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<Symbols> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Symbols::getName),
			SymbolSet.RESOURCE_KEY_CODEC.optionalFieldOf("symbol_set").forGetter(Symbols::getSymbolSet),
			ResourceLocation.CODEC.fieldOf("texture").forGetter(Symbols::getTexture),
			Codec.INT.optionalFieldOf("size", 38).forGetter(Symbols::getSize)
			).apply(instance, Symbols::new));
	
	private final String name;
	private final Optional<ResourceKey<SymbolSet>> symbolSet;
	private final ResourceLocation texture;
	private final int size;
	
	public Symbols(String name, Optional<ResourceKey<SymbolSet>> symbolSet, ResourceLocation texture, int size)
	{
		this.name = name;
		this.symbolSet = symbolSet;
		this.texture = texture;
		this.size = size;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getTranslationName(boolean uniqueSymbols)
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
		if(!ClientStargateConfig.unique_symbols.get() && this.symbolSet.isPresent())
			return true;
		
		return false;
	}
	
	public static String symbolsOrSet()
	{
		return ClientStargateConfig.unique_symbols.get() ? "info.sgjourney.symbols" :  "info.sgjourney.symbol_set";
	}
	
	public Optional<ResourceKey<SymbolSet>> getSymbolSet()
	{
		return this.symbolSet;
	}
	
	public ResourceLocation getTexture()
	{
		return this.texture;
	}
	
	public int getSize()
	{
		return this.size;
	}
	
	public ResourceLocation getSymbolTexture()
	{
		if(useSymbolSet())
		{
			SymbolSet symbolSet = SymbolSet.getClientSymbolSet(this.symbolSet.get());
			
			if(symbolSet != null)
				return symbolSet.getSymbolTexture();
		}
		
		ResourceLocation texture = StargateJourney.location(this.texture.getNamespace(), "textures/symbols/" + this.texture.getPath());
		return texture;
	}
	
	public boolean shouldRenderSymbol(int symbol)
	{
		if(useSymbolSet())
		{
			SymbolSet symbolSet = SymbolSet.getClientSymbolSet(this.symbolSet.get());
			
			if(symbolSet != null)
				return symbolSet.shouldRenderSymbol(symbol);
		}
		
		if(symbol >= 0 && symbol < size)
			return true;
		
		return false;
	}
	
	public float getTextureOffset(int symbol)
	{
		symbol -= 1;
		float symbolSize = 1F / (float) size;
		return symbolSize * symbol + symbolSize / 2F;
	}
	
	public static Symbols getSymbols(Level level, ResourceKey<Symbols> symbols)
	{
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<Symbols> registry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		return registry.get(symbols);
	}
	
	public static Symbols getSymbols(Level level, String name)
	{
		String[] split = name.split(":");
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<Symbols> registry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		return registry.get(StargateJourney.location(split[0], split[1]));
	}
	
	public static ResourceKey<Symbols> defaultSymbols()
	{
		return Conversion.stringToSymbols(StargateJourney.MODID + ":universal");
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
