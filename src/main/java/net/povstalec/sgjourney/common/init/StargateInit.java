package net.povstalec.sgjourney.common.init;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.stargate.*;

import javax.annotation.Nullable;
import java.util.HashMap;

public class StargateInit
{
	private static final HashMap<ResourceLocation, StargateConstructor> STARGATES = new HashMap<ResourceLocation, StargateConstructor>();
	private static final HashMap<Class<? extends Stargate>, ResourceLocation> LOCATIONS = new HashMap<Class<? extends Stargate>, ResourceLocation>();
	
	public static <T extends Stargate> void register(ResourceLocation resourceLocation, Class<T> objectClass, StargateConstructor<T> constructor)
	{
		if(STARGATES.containsKey(resourceLocation))
			throw new IllegalStateException("Duplicate registration for " + resourceLocation.toString());
		if(LOCATIONS.containsKey(objectClass))
			throw new IllegalStateException("Duplicate registration for " + objectClass.getName());
		
		STARGATES.put(resourceLocation, constructor);
		LOCATIONS.put(objectClass, resourceLocation);
	}
	
	@Nullable
	public static Stargate constructStargate(ResourceLocation resourceLocation)
	{
		if(STARGATES.containsKey(resourceLocation))
			return STARGATES.get(resourceLocation).create();
		
		return null;
	}
	
	@Nullable
	public static ResourceLocation getResourceLocation(Stargate stargate)
	{
		if(stargate != null && LOCATIONS.containsKey(stargate.getClass()))
			return LOCATIONS.get(stargate.getClass());
		
		return null;
	}
	
	
	
	public static void register()
	{
		register(StargateJourney.sgjourneyLocation("universe_stargate"), UniverseStargate.class, UniverseStargate::new);
		register(StargateJourney.sgjourneyLocation("milky_way_stargate"), MilkyWayStargate.class, MilkyWayStargate::new);
		register(StargateJourney.sgjourneyLocation("pegasus_stargate"), PegasusStargate.class, PegasusStargate::new);
		register(StargateJourney.sgjourneyLocation("tollan_stargate"), TollanStargate.class, TollanStargate::new);
		register(StargateJourney.sgjourneyLocation("classic_stargate"), ClassicStargate.class, ClassicStargate::new);
	}
	
	
	
	public interface StargateConstructor<T extends Stargate>
	{
		T create();
	}
}
