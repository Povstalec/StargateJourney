package net.povstalec.sgjourney.common.stargate;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class Symbols
{
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	
	public static final ResourceLocation SYMBOLS_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbols");
	public static final ResourceKey<Registry<Symbols>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOLS_LOCATION);
	public static final Codec<ResourceKey<Symbols>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<Symbols> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Symbols::getName),
			SymbolSet.RESOURCE_KEY_CODEC.fieldOf("symbol_set").forGetter(Symbols::getSymbolSet),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(Symbols::getTextures)
			).apply(instance, Symbols::new));
	
	private final String name;
	private final ResourceKey<SymbolSet> symbolSet;
	private final List<ResourceLocation> textures;
	
	public Symbols(String name, ResourceKey<SymbolSet> symbolSet, List<ResourceLocation> textures)
	{
		this.name = name;
		this.symbolSet = symbolSet;
		this.textures = textures;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getName(boolean uniqueSymbols)
	{
		if(!ClientStargateConfig.unique_symbols.get())
		{
			SymbolSet symbolSet = SymbolSet.getClientSymbolSet(this.symbolSet);
			
			return symbolSet.getName();
		}
		
		return name;
	}
	
	public ResourceKey<SymbolSet> getSymbolSet()
	{
		return symbolSet;
	}
	
	public List<ResourceLocation> getTextures()
	{
		return textures;
	}
	
	public ResourceLocation texture(int i)
	{
		if(ClientStargateConfig.unique_symbols.get())
		{
			if(i >= textures.size() || i < 0)
				return ERROR_LOCATION;
			
			ResourceLocation path = textures.get(i);
			ResourceLocation texture = new ResourceLocation(path.getNamespace(), "textures/symbols/" + path.getPath());
			
			if(Minecraft.getInstance().getResourceManager().getResource(texture).isPresent())
				return texture;
		}
		else
		{
			SymbolSet symbolSet = SymbolSet.getClientSymbolSet(this.symbolSet);
			
			return symbolSet.texture(i);
		}
		
		return ERROR_LOCATION;
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
		
		return registry.get(new ResourceLocation(split[0], split[1]));
	}
	
}
