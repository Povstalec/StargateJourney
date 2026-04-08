package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.misc.Conversion;

import javax.annotation.Nullable;

public record Symbols(ResourceKey<ClientSymbols> clientSymbols)
{
	public static final ResourceLocation UNIVERSAL_LOCATION = new ResourceLocation(StargateJourney.MODID, "universal");
	
	public static final ResourceLocation SYMBOLS_LOCATION = new ResourceLocation(StargateJourney.MODID, "symbols");
	public static final ResourceKey<Registry<Symbols>> REGISTRY_KEY = ResourceKey.createRegistryKey(SYMBOLS_LOCATION);
	public static final Codec<ResourceKey<Symbols>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<Symbols> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ClientSymbols.RESOURCE_KEY_CODEC.fieldOf("client_symbols").forGetter(symbols -> symbols.clientSymbols)
	).apply(instance, Symbols::new));
	
	public static ResourceKey<Symbols> defaultSymbols()
	{
		return Conversion.locationToSymbols(UNIVERSAL_LOCATION);
	}
	
	public static boolean isValid(MinecraftServer server, ResourceKey<Symbols> symbols)
	{
		if(symbols == null)
			return false;
		
		RegistryAccess registries = server.registryAccess();
		Registry<Symbols> symbolRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		return symbolRegistry.containsKey(symbols);
	}
	
	public static ResourceKey<Symbols> fromDimension(MinecraftServer server, ResourceKey<Level> dimension)
	{
		return Universe.get(server).getSymbols(dimension);
	}
	
	public static MutableComponent makeComponent(@Nullable ResourceKey<Symbols> symbols)
	{
		return Component.literal(symbols != null ? symbols.location().toString() : "-");
	}
}
