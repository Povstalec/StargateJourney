package net.povstalec.sgjourney.common.stargate;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class SymbolSet
{
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	
	public static final ResourceLocation SYMBOL_SET_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbol_set");
	public static final ResourceKey<Registry<SymbolSet>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOL_SET_LOCATION);
	public static final Codec<ResourceKey<SymbolSet>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<SymbolSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(SymbolSet::getName),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(SymbolSet::getTextures)
			).apply(instance, SymbolSet::new));
	
	private final String name;
	private final List<ResourceLocation> textures;
	
	public SymbolSet(String name, List<ResourceLocation> textures)
	{
		this.name = name;
		this.textures = textures;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<ResourceLocation> getTextures()
	{
		return textures;
	}
	
	public ResourceLocation texture(int i)
	{
		if(i >= textures.size() || i < 0)
			return ERROR_LOCATION;
		
		ResourceLocation path = textures.get(i);
		ResourceLocation texture = new ResourceLocation(path.getNamespace(), "textures/symbols/" + path.getPath());
		
		if(Minecraft.getInstance().getResourceManager().getResource(texture).isPresent())
			return texture;
		return ERROR_LOCATION;
	}
	
	public static SymbolSet getClientSymbolSet(ResourceKey<SymbolSet> symbols)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		
		Registry<SymbolSet> registry = registries.registryOrThrow(SymbolSet.REGISTRY_KEY);
		
		return registry.get(symbols);
		
	}
	
	public static SymbolSet getSymbolSet(Level level, ResourceKey<SymbolSet> symbols)
	{
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<SymbolSet> registry = registries.registryOrThrow(SymbolSet.REGISTRY_KEY);
		
		return registry.get(symbols);
	}
	
	public static SymbolSet getSymbolSet(Level level, String name)
	{
		String[] split = name.split(":");
		RegistryAccess registries = level.getServer().registryAccess();
		Registry<SymbolSet> registry = registries.registryOrThrow(SymbolSet.REGISTRY_KEY);
		
		return registry.get(new ResourceLocation(split[0], split[1]));
	}
	
}
