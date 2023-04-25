package net.povstalec.sgjourney.common.stargate;


import static java.util.Map.entry;

import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;

public class Symbols
{
	public static final ResourceLocation ERROR_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/symbols/error.png");
	
	public static final ResourceLocation SYMBOLS_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbols");
	public static final ResourceKey<Registry<Symbols>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOLS_LOCATION);
	public static final Codec<ResourceKey<Symbols>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<Symbols> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		Codec.STRING.fieldOf("name").forGetter(Symbols::getName),
			ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(Symbols::getTextures)
			).apply(instance, Symbols::new));
	
	private final String name;
	private final List<ResourceLocation> textures;
	private static Map<Integer, String> symbolMap = Map.ofEntries(
			entry(1, "\uE001"), entry(2, "\uE002"), entry(3, "\uE003"), entry(4, "\uE004"), entry(5, "\uE005"), entry(6, "\uE006"),
			entry(7, "\uE007"), entry(8, "\uE008"), entry(9, "\uE009"), entry(10, "\uE010"), entry(11, "\uE011"), entry(12, "\uE012"),
			entry(13, "\uE013"), entry(14, "\uE014"), entry(15, "\uE015"), entry(16, "\uE016"), entry(17, "\uE017"), entry(18, "\uE018"),
			entry(19, "\uE019"), entry(20, "\uE020"), entry(21, "\uE021"), entry(22, "\uE022"), entry(23, "\uE023"), entry(24, "\uE024"),
			entry(25, "\uE025"), entry(26, "\uE026"), entry(27, "\uE027"), entry(28, "\uE028"), entry(29, "\uE029"), entry(30, "\uE030"),
			entry(31, "\uE031"), entry(32, "\uE032"), entry(33, "\uE033"), entry(34, "\uE034"), entry(35, "\uE035"), entry(36, "\uE036"),
			entry(37, "\uE037"), entry(38, "\uE038")
			);
	
	public Symbols(String name, List<ResourceLocation> textures)
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

	public static String unicode(int symbolNumber)
	{
		return symbolMap.get(symbolNumber);
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
	
	public static String addressUnicode(String address)
	{
		String unicode = "";
		
		String[] symbols = address.split("-");
		
		for(int i = 0; i < symbols.length; i++)
		{
			if(!symbols[i].equals(""))
			{
				int symbolNumber = Integer.parseInt(symbols[i]);
			
				unicode = unicode + Symbols.unicode(symbolNumber);
			}
		}
		
		return unicode;
	}
	
}
