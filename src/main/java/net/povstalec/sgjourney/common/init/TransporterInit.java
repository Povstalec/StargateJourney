package net.povstalec.sgjourney.common.init;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.transporter.*;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TransporterInit
{
	private static final HashMap<ResourceLocation, TransporterConstructor> TRANSPORTERS = new HashMap<ResourceLocation, TransporterConstructor>();
	private static final HashMap<Class<? extends Transporter>, ResourceLocation> LOCATIONS = new HashMap<Class<? extends Transporter>, ResourceLocation>();
	
	public static <T extends Transporter> void register(ResourceLocation resourceLocation, Class<T> objectClass, TransporterConstructor<T> constructor)
	{
		if(TRANSPORTERS.containsKey(resourceLocation))
			throw new IllegalStateException("Duplicate registration for " + resourceLocation.toString());
		if(LOCATIONS.containsKey(objectClass))
			throw new IllegalStateException("Duplicate registration for " + objectClass.getName());
		
		TRANSPORTERS.put(resourceLocation, constructor);
		LOCATIONS.put(objectClass, resourceLocation);
	}
	
	@Nullable
	public static Transporter constructTransporter(ResourceLocation resourceLocation)
	{
		if(TRANSPORTERS.containsKey(resourceLocation))
			return TRANSPORTERS.get(resourceLocation).create();
		
		return null;
	}
	
	@Nullable
	public static ResourceLocation getResourceLocation(Transporter transporter)
	{
		if(transporter != null && LOCATIONS.containsKey(transporter.getClass()))
			return LOCATIONS.get(transporter.getClass());
		
		return null;
	}
	
	
	
	public static void register()
	{
		register(new ResourceLocation(StargateJourney.MODID, "transport_rings"), TransportRings.class, TransportRings::new);
	}
	
	
	
	public interface TransporterConstructor<T extends Transporter>
	{
		T create();
	}
}
