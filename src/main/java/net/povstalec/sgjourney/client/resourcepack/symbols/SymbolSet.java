package net.povstalec.sgjourney.client.resourcepack.symbols;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SymbolSet(String name, List<ResourceLocation> textures)
{
	public static final ResourceLocation SYMBOL_SET_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbol_set");
	public static final ResourceKey<Registry<SymbolSet>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOL_SET_LOCATION);
	public static final Codec<ResourceKey<SymbolSet>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<SymbolSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(symbols -> symbols.name),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(symbols -> symbols.textures)
	).apply(instance, SymbolSet::new));
	
	private static final Map<ResourceKey<SymbolSet>, SymbolSet> SYMBOL_SETS = new HashMap<>();
	
	public int size()
	{
		return this.textures.size();
	}
	
	public boolean containsSymbol(int symbol)
	{
		return symbol >= 1 && symbol <= size();
	}
	
	public ResourceLocation getSymbolTexture(int symbol)
	{
		if(symbol > size())
			return ClientSymbols.getDefaultSymbolTexture(symbol);
		else if(symbol <= 0)
			return ClientSymbols.ERROR_LOCATION;
		
		return textures.get(symbol - 1);
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
