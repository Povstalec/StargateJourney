package net.povstalec.sgjourney.client.resourcepack.symbols;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientUtil;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import javax.annotation.Nullable;
import java.util.*;

public class ClientSymbols
{
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbol/error");
	
	public static final ResourceLocation SYMBOLS_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbols");
	public static final ResourceKey<Registry<ClientSymbols>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOLS_LOCATION);
	public static final Codec<ResourceKey<ClientSymbols>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final List<ResourceLocation> DEFAULT_SPRITE_TEXTURES = createSpriteTexturesList("universal/universal_", 1, 38);
	public static final List<ResourceLocation> DEFAULT_EXTENDED_TEXTURES = createExtendedTexturesList("universal/universal_", 1, 38);
	
	public static final Codec<ClientSymbols> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(symbols -> symbols.name),
			SymbolSet.RESOURCE_KEY_CODEC.optionalFieldOf("symbol_set").forGetter(symbols -> Optional.ofNullable(symbols.symbolSetKey)),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(symbols -> symbols.spriteTextures)
	).apply(instance, ClientSymbols::new));
	
	private static final Map<ResourceKey<Symbols>, ClientSymbols> SYMBOLS = new HashMap<>();
	
	private final String name;
	@Nullable
	private final ResourceKey<SymbolSet> symbolSetKey;
	private final List<ResourceLocation> spriteTextures; // Names used for looking up the textures in a TextureAtlas
	private final List<ResourceLocation> extendedTextures; // Full texture paths inside assets folder
	
	@Nullable
	private SymbolSet symbolSet = null;
	
	// Constructor made specifically for the codec
	private ClientSymbols(String name, Optional<ResourceKey<SymbolSet>> symbolSetKey, List<ResourceLocation> textures)
	{
		this(name, symbolSetKey.orElse(null), textures);
	}
	
	public ClientSymbols(String name, @Nullable ResourceKey<SymbolSet> symbolSetKey, List<ResourceLocation> textures)
	{
		this.name = name;
		this.symbolSetKey = symbolSetKey;
		this.spriteTextures = textures;
		ResourceLocation[] extendedTextures = new ResourceLocation[textures.size()];
		for(int i = 0; i < extendedTextures.length; i++)
		{
			extendedTextures[i] = new ResourceLocation(textures.get(i).getNamespace(), "textures/" + textures.get(i).getPath() + ".png");
		}
		this.extendedTextures = Arrays.asList(extendedTextures);
	}
	
	public String name()
	{
		return this.name;
	}
	
	public String getTranslationName()
	{
		if(useSymbolSet())
			return this.symbolSet.name();
		
		return this.name;
	}
	
	public ResourceKey<SymbolSet> symbolSetKey()
	{
		return this.symbolSetKey;
	}
	
	@Nullable
	public SymbolSet symbolSet()
	{
		return this.symbolSet;
	}
	
	public List<ResourceLocation> spriteTextures()
	{
		return this.spriteTextures;
	}
	
	public List<ResourceLocation> extendedTextures()
	{
		return this.extendedTextures;
	}
	
	public boolean useSymbolSet()
	{
		return !ClientStargateConfig.unique_symbols.get() && this.symbolSet != null;
	}
	
	public static String symbolsOrSet()
	{
		return ClientStargateConfig.unique_symbols.get() ? "info.sgjourney.symbols" : "info.sgjourney.symbol_set";
	}
	
	public int size()
	{
		return this.spriteTextures.size();
	}
	
	public static ResourceLocation getDefaultSpriteSymbolTexture(int symbol)
	{
		if(symbol > DEFAULT_SPRITE_TEXTURES.size() || symbol <= 0)
			return ERROR_LOCATION;
		
		return DEFAULT_SPRITE_TEXTURES.get(symbol - 1);
	}
	
	public static ResourceLocation getDefaultExtendedSymbolTexture(int symbol)
	{
		if(symbol > DEFAULT_EXTENDED_TEXTURES.size() || symbol <= 0)
			return ERROR_LOCATION;
		
		return DEFAULT_EXTENDED_TEXTURES.get(symbol - 1);
	}
	
	public ResourceLocation getSpriteSymbolTexture(int symbol)
	{
		if(useSymbolSet())
			return symbolSet.getSpriteSymbolTexture(symbol);
		
		if(symbol > size())
			return getDefaultSpriteSymbolTexture(symbol);
		else if(symbol <= 0)
			return ERROR_LOCATION;
		
		return spriteTextures.get(symbol - 1);
	}
	
	public static TextureAtlasSprite getSprite(@Nullable ClientSymbols symbols, int symbol)
	{
		if(symbols == null)
			return ClientUtil.getTexture(MissingTextureAtlasSprite.getLocation());
		
		return ClientUtil.getTexture(symbols.getSpriteSymbolTexture(symbol));
	}
	
	public ResourceLocation getExtendedSymbolTexture(int symbol)
	{
		if(useSymbolSet())
			return symbolSet.getExtendedSymbolTexture(symbol);
		
		if(symbol > size())
			return getDefaultExtendedSymbolTexture(symbol);
		else if(symbol <= 0)
			return ERROR_LOCATION;
		
		return extendedTextures.get(symbol - 1);
	}
	
	public static List<ResourceLocation> createSpriteTexturesList(String prefix, int first, int last)
	{
		ResourceLocation[] textures = new ResourceLocation[last - first + 1];
		for(int i = 0; i < textures.length; i++)
		{
			textures[i] = StargateJourney.sgjourneyLocation("symbol/" + prefix + (first + i));
		}
		
		return Arrays.asList(textures);
	}
	
	public static List<ResourceLocation> createExtendedTexturesList(String prefix, int first, int last)
	{
		ResourceLocation[] textures = new ResourceLocation[last - first + 1];
		for(int i = 0; i < textures.length; i++)
		{
			textures[i] = StargateJourney.sgjourneyLocation("textures/symbol/" + prefix + (first + i) + ".png");
		}
		
		return Arrays.asList(textures);
	}
	
	
	
	public static void addSymbols(ResourceKey<Symbols> key, ClientSymbols symbols)
	{
		SYMBOLS.put(key, symbols);
	}
	
	public static void assignSymbolSets()
	{
		SYMBOLS.forEach((key, symbols) ->
		{
			if(symbols.symbolSetKey != null)
			{
				SymbolSet symbolSet = SymbolSet.getSymbolSet(symbols.symbolSetKey);
				if(symbolSet != null)
					symbols.symbolSet = symbolSet;
				else
					StargateJourney.LOGGER.error("Could not assign Symbol Set {} to Symbols {} because it was not found", symbols.symbolSetKey.location(), key.location());
			}
		});
	}
	
	@Nullable
	public static ClientSymbols getSymbols(ResourceKey<Symbols> key)
	{
		return SYMBOLS.get(key);
	}
	
	public static String translationName(@Nullable ClientSymbols symbols, String alternative)
	{
		if(symbols != null)
			return symbols.getTranslationName();
		
		return alternative;
	}
	
	public static void clearSymbols()
	{
		SYMBOLS.clear();
	}
}
