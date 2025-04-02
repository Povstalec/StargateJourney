package net.povstalec.sgjourney.common.sgjourney;

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
			ResourceLocation.CODEC.fieldOf("texture").forGetter(SymbolSet::getTexture),
			Codec.INT.optionalFieldOf("size", 38).forGetter(SymbolSet::getSize)
			).apply(instance, SymbolSet::new));
	
	private final String name;
	private final ResourceLocation texture;
	private final int size;
	
	public SymbolSet(String name, ResourceLocation texture, int size)
	{
		this.name = name;
		this.texture = texture;
		this.size = size;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ResourceLocation getTexture()
	{
		return texture;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public ResourceLocation getSymbolTexture()
	{
		ResourceLocation texture = new ResourceLocation(this.texture.getNamespace(), "textures/symbols/" + this.texture.getPath());
		return texture;
	}
	
	public boolean shouldRenderSymbol(int symbol)
	{
		if(symbol >= 0 && symbol < size)
			return true;
		
		return false;
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
