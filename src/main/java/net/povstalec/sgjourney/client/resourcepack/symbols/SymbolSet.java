package net.povstalec.sgjourney.client.resourcepack.symbols;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolSet
{
	public static final ResourceLocation SYMBOL_SET_LOCATION = StargateJourney.sgjourneyLocation("symbol_set");
	public static final ResourceKey<Registry<SymbolSet>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOL_SET_LOCATION);
	public static final Codec<ResourceKey<SymbolSet>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<SymbolSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(symbols -> symbols.name),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(symbols -> symbols.spriteTextures)
	).apply(instance, SymbolSet::new));
	
	private static final Map<ResourceKey<SymbolSet>, SymbolSet> SYMBOL_SETS = new HashMap<>();
	
	private final String name;
	private final List<ResourceLocation> spriteTextures; // Names used for looking up the textures in a TextureAtlas
	private final List<ResourceLocation> extendedTextures; // Full texture paths inside assets folder
	
	public SymbolSet(String name, List<ResourceLocation> textures)
	{
		this.name = name;
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
	
	public List<ResourceLocation> spriteTextures()
	{
		return this.spriteTextures;
	}
	
	public List<ResourceLocation> extendedTextures()
	{
		return this.extendedTextures;
	}
	
	public int size()
	{
		return this.spriteTextures.size();
	}
	
	public boolean containsSymbol(int symbol)
	{
		return symbol >= 1 && symbol <= size();
	}
	
	public ResourceLocation getSpriteSymbolTexture(int symbol)
	{
		if(symbol > size())
			return ClientSymbols.getDefaultSpriteSymbolTexture(symbol);
		else if(symbol <= 0)
			return ClientSymbols.ERROR_LOCATION;
		
		return spriteTextures.get(symbol - 1);
	}
	
	public ResourceLocation getExtendedSymbolTexture(int symbol)
	{
		if(symbol > size())
			return ClientSymbols.getDefaultExtendedSymbolTexture(symbol);
		else if(symbol <= 0)
			return ClientSymbols.ERROR_LOCATION;
		
		return extendedTextures.get(symbol - 1);
	}
	
	@Nullable
	public static ResourceKey<SymbolSet> keyFromLocation(ResourceLocation location)
	{
		if(location != null)
			return ResourceKey.create(SymbolSet.REGISTRY_KEY, location);
		
		return null;
	}
	
	
	
	public static void addSymbolSet(ResourceKey<SymbolSet> key, SymbolSet symbolSet)
	{
		SYMBOL_SETS.put(key, symbolSet);
	}
	
	@Nullable
	public static SymbolSet getSymbolSet(ResourceKey<SymbolSet> key)
	{
		return SYMBOL_SETS.get(key);
	}
	
	public static void clearSymbolSets()
	{
		SYMBOL_SETS.clear();
	}
}
