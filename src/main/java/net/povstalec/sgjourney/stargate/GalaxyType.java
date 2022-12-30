package net.povstalec.sgjourney.stargate;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class GalaxyType
{
	public static final Codec<ResourceKey<GalaxyType>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "galaxy_type")));
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
