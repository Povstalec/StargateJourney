package net.povstalec.sgjourney.common.sgjourney.stargate;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

import java.util.Optional;

public class StargateType<S extends Stargate>
{
	public static final String STARGATE_TYPE = "stargate_type";
	public static final ResourceLocation STARGATE_TYPE_LOCATION = StargateJourney.sgjourneyLocation(STARGATE_TYPE);
	public static final ResourceKey<Registry<StargateType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(STARGATE_TYPE_LOCATION);
	public static final Codec<ResourceKey<StargateType<?>>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResourceKey.createRegistryKey(STARGATE_TYPE_LOCATION));
	
	private final StargateInfo.Gen generation;
	private final StargateConstructor<S> stargateConstructor;
	
	public StargateType(StargateInfo.Gen generation, StargateConstructor<S> stargateConstructor)
	{
		this.generation = generation;
		
		this.stargateConstructor = stargateConstructor;
	}
	
	public StargateInfo.Gen getGeneration()
	{
		return generation;
	}
	
	public S constructStargate(MinecraftServer server)
	{
		return stargateConstructor.constructStargate(this, server);
	}
	
	
	
	public static ResourceLocation getKey(StargateType<?> type)
	{
		return StargateInit.STARGATE_TYPE_REGISTRY.getKey(type);
	}
	
	public static StargateType<?> getType(ResourceLocation key)
	{
		return StargateInit.STARGATE_TYPE_REGISTRY.get(key);
	}
	
	public static boolean tagHasStargateType(CompoundTag tag)
	{
		return tag.contains(STARGATE_TYPE, Tag.TAG_STRING);
	}
	
	public static Optional<StargateType<?>> getTypeFromTag(CompoundTag tag)
	{
		if(tagHasStargateType(tag))
		{
			String typeName = tag.getString(STARGATE_TYPE);
			StargateType<?> type = getType(ResourceLocation.tryParse(typeName));
			if(type == null)
				StargateJourney.LOGGER.error("No entry found for Stargate Type \"{}\"", typeName);
			return Optional.ofNullable(type);
			
		}
		
		return Optional.empty();
	}
	
	public static void addTypeToTag(StargateType<?> type, CompoundTag tag)
	{
		tag.putString(STARGATE_TYPE, getKey(type).toString());
	}
	
	
	
	public interface StargateConstructor<S extends Stargate>
	{
		S constructStargate(StargateType<?> type, MinecraftServer server);
	}
}
