package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

import java.util.ArrayList;
import java.util.List;

public class SymbolSet
{
	public static final ResourceLocation SYMBOL_SET_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbol_set");
	public static final ResourceKey<Registry<SymbolSet>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOL_SET_LOCATION);
	public static final Codec<ResourceKey<SymbolSet>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<SymbolSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(SymbolSet::getName),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(symbols -> symbols.textures)
			).apply(instance, SymbolSet::new));
	
	private final String name;
	private final ArrayList<ResourceLocation> textures;
	
	public SymbolSet(String name, List<ResourceLocation> textures)
	{
		this.name = name;
		this.textures = new ArrayList<>(textures);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getSize()
	{
		return this.textures.size();
	}
	
	public ResourceLocation getSymbolTexture(int symbol)
	{
		symbol--;
		if(symbol < 0 || symbol >= textures.size())
			return Symbols.ERROR_LOCATION;
		
		return textures.get(symbol);
	}
	
	public boolean shouldRenderSymbol(int symbol)
	{
		return symbol >= 0 && symbol < textures.size();
	}
	
	public static SymbolSet getClientSymbolSet(ResourceKey<SymbolSet> symbols)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		
		Registry<SymbolSet> registry = registries.registryOrThrow(SymbolSet.REGISTRY_KEY);
		
		return registry.get(symbols);
		
	}
}
