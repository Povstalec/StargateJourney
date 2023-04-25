package net.povstalec.sgjourney.common.stargate;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class GalaxyType
{
	public static final ResourceLocation GALAXY_TYPE_LOCATION = new ResourceLocation(StargateJourney.MODID, "galaxy_type");
	public static final ResourceKey<Registry<GalaxyType>> REGISTRY_KEY = ResourceKey.createRegistryKey(GALAXY_TYPE_LOCATION);
	public static final Codec<ResourceKey<GalaxyType>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResourceKey.createRegistryKey(GALAXY_TYPE_LOCATION));
	
	private final int size;
	
	public GalaxyType(int size)
	{
		this.size = size;
	}
	
	public int getSize()
	{
		return size;
	}
}
