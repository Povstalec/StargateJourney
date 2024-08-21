package net.povstalec.sgjourney.common.stargate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class StargateVariant
{
	public static final ResourceLocation STARGATE_VARIANT_LOCATION = new ResourceLocation(StargateJourney.MODID, "stargate_variant");
	public static final ResourceKey<Registry<StargateVariant>> REGISTRY_KEY = ResourceKey.createRegistryKey(STARGATE_VARIANT_LOCATION);
	public static final Codec<ResourceKey<StargateVariant>> RESOURCE_KEY_CODEC = ResourceKey.codec(REGISTRY_KEY);
	
	public static final Codec<StargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("base_stargate").forGetter(StargateVariant::getBaseStargate),
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(StargateVariant::clientVariant))
			.apply(instance, StargateVariant::new));
	
	private final ResourceLocation baseStargate;
	private final ResourceLocation clientVariant;
	
	public StargateVariant(ResourceLocation baseStargate, ResourceLocation clientVariant)
	{
		this.baseStargate = baseStargate;
		
		this.clientVariant = clientVariant;
	}
	
	
	
	public ResourceLocation getBaseStargate()
	{
		return this.baseStargate;
	}
	
	public ResourceLocation clientVariant()
	{
		return this.clientVariant;
	}
}
