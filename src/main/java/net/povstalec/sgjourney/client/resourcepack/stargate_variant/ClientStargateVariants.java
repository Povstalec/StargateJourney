package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.HashMap;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.stellarview.StellarView;

public class ClientStargateVariants
{
	private static final HashMap<ResourceLocation, UniverseStargateVariant> UNIVERSE_STARGATE_VARIANTS = new HashMap<>();
	private static final HashMap<ResourceLocation, MilkyWayStargateVariant> MILKY_WAY_STARGATE_VARIANTS = new HashMap<>();
	private static final HashMap<ResourceLocation, PegasusStargateVariant> PEGASUS_STARGATE_VARIANTS = new HashMap<>();

	private static final HashMap<ResourceLocation, TollanStargateVariant> TOLLAN_STARGATE_VARIANTS = new HashMap<>();

	private static final HashMap<ResourceLocation, ClassicStargateVariant> CLASSIC_STARGATE_VARIANTS = new HashMap<>();
	
	public static void clear()
	{
		UNIVERSE_STARGATE_VARIANTS.clear();
		MILKY_WAY_STARGATE_VARIANTS.clear();
		PEGASUS_STARGATE_VARIANTS.clear();
		
		TOLLAN_STARGATE_VARIANTS.clear();
		
		CLASSIC_STARGATE_VARIANTS.clear();
	}
	
	
	
	public static UniverseStargateVariant getUniverseStargateVariant(ResourceLocation location)
	{
		if(UNIVERSE_STARGATE_VARIANTS.containsKey(location))
			return UNIVERSE_STARGATE_VARIANTS.get(location);
		
		return UniverseStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addUniverseStargateVariant(ResourceLocation location, UniverseStargateVariant stargateVariant)
	{
		if(!UNIVERSE_STARGATE_VARIANTS.containsKey(location))
			UNIVERSE_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StellarView.LOGGER.error("Universe Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static MilkyWayStargateVariant getMilkyWayStargateVariant(ResourceLocation location)
	{
		if(MILKY_WAY_STARGATE_VARIANTS.containsKey(location))
			return MILKY_WAY_STARGATE_VARIANTS.get(location);
		
		return MilkyWayStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addMilkyWayStargateVariant(ResourceLocation location, MilkyWayStargateVariant stargateVariant)
	{
		if(!MILKY_WAY_STARGATE_VARIANTS.containsKey(location))
			MILKY_WAY_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StellarView.LOGGER.error("Milky Way Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static PegasusStargateVariant getPegasusStargateVariant(ResourceLocation location)
	{
		if(PEGASUS_STARGATE_VARIANTS.containsKey(location))
			return PEGASUS_STARGATE_VARIANTS.get(location);
		
		return PegasusStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addPegasusStargateVariant(ResourceLocation location, PegasusStargateVariant stargateVariant)
	{
		if(!PEGASUS_STARGATE_VARIANTS.containsKey(location))
			PEGASUS_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StellarView.LOGGER.error("Milky Way Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static TollanStargateVariant getTollanStargateVariant(ResourceLocation location)
	{
		if(TOLLAN_STARGATE_VARIANTS.containsKey(location))
			return TOLLAN_STARGATE_VARIANTS.get(location);
		
		return TollanStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addTollanStargateVariant(ResourceLocation location, TollanStargateVariant stargateVariant)
	{
		if(!TOLLAN_STARGATE_VARIANTS.containsKey(location))
			TOLLAN_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StellarView.LOGGER.error("Tollan Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static ClassicStargateVariant getClassicStargateVariant(ResourceLocation location)
	{
		if(CLASSIC_STARGATE_VARIANTS.containsKey(location))
			return CLASSIC_STARGATE_VARIANTS.get(location);
		
		return ClassicStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addClassicStargateVariant(ResourceLocation location, ClassicStargateVariant stargateVariant)
	{
		if(!CLASSIC_STARGATE_VARIANTS.containsKey(location))
			CLASSIC_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StellarView.LOGGER.error("Milky Way Stargate Variant " + location.toString() + " already exists");
	}
}
