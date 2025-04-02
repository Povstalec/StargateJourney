package net.povstalec.sgjourney.common.sgjourney;

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

	public static final ResourceLocation UNIVERSE_STARGATE = new ResourceLocation(StargateJourney.MODID, "universe_stargate");
	public static final ResourceLocation MILKY_WAY_STARGATE = new ResourceLocation(StargateJourney.MODID, "milky_way_stargate");
	public static final ResourceLocation PEGASUS_STARGATE = new ResourceLocation(StargateJourney.MODID, "pegasus_stargate");
	public static final ResourceLocation TOLLAN_STARGATE = new ResourceLocation(StargateJourney.MODID, "tollan_stargate");
	public static final ResourceLocation CLASSIC_STARGATE = new ResourceLocation(StargateJourney.MODID, "classic_stargate");
	
	public static final Codec<StargateVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("base_stargate").forGetter(StargateVariant::getBaseStargate),
			ResourceLocation.CODEC.fieldOf("client_variant").forGetter(StargateVariant::clientVariant))
			.apply(instance, StargateVariant::new));
	
	private final ResourceLocation baseStargate;
	private final ResourceLocation clientVariant;
	
	private boolean isFound = false;
	private boolean isMissing = false;
	
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
	
	
	
	private String getVariantPrefixFromBase(ResourceLocation baseStargate)
	{
		if(baseStargate.equals(UNIVERSE_STARGATE))
			return "universe";
		else if(baseStargate.equals(MILKY_WAY_STARGATE))
			return "milky_way";
		else if(baseStargate.equals(PEGASUS_STARGATE))
			return "pegasus";
		else if(baseStargate.equals(TOLLAN_STARGATE))
			return "tollan";
		else if(baseStargate.equals(CLASSIC_STARGATE))
			return "classic";
		else
			return "[ERROR]";
	}
	
	public boolean isFound()
	{
		return isFound;
	}

	public boolean isMissing()
	{
		return isMissing;
	}
	
	public void handleLocation(boolean isLocated)
	{
		if(isLocated)
			this.isFound = true;
		else if(!isMissing)
		{
			isMissing = true;
			StargateJourney.LOGGER.error("Could not locate {" + getBaseStargate().toString() + "} variant [" + clientVariant().getNamespace() + ':' + getVariantPrefixFromBase(getBaseStargate()) + '/' + clientVariant().getPath() + ']');
		}
	}
	
	public void resetMissing()
	{
		isFound = false;
		isMissing = false;
	}
}
