package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.HashMap;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class ClientStargateVariants
{
	private static Minecraft minecraft = Minecraft.getInstance();
	
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
	
	
	
	public static boolean hasUniverseStargateVariant(ResourceLocation location)
	{
		return UNIVERSE_STARGATE_VARIANTS.containsKey(location);
	}
	
	public static UniverseStargateVariant getUniverseStargateVariant(ResourceLocation location)
	{
		if(hasUniverseStargateVariant(location))
			return UNIVERSE_STARGATE_VARIANTS.get(location);
		
		return UniverseStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addUniverseStargateVariant(ResourceLocation location, UniverseStargateVariant stargateVariant)
	{
		if(!hasUniverseStargateVariant(location))
			UNIVERSE_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StargateJourney.LOGGER.error("Universe Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static boolean hasMilkyWayStargateVariant(ResourceLocation location)
	{
		return MILKY_WAY_STARGATE_VARIANTS.containsKey(location);
	}
	
	public static MilkyWayStargateVariant getMilkyWayStargateVariant(ResourceLocation location)
	{
		if(hasMilkyWayStargateVariant(location))
			return MILKY_WAY_STARGATE_VARIANTS.get(location);
		
		return ClientStargateConfig.milky_way_stargate_back_lights_up.get() ? MilkyWayStargateVariant.DEFAULT_BACK_VARIANT : MilkyWayStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addMilkyWayStargateVariant(ResourceLocation location, MilkyWayStargateVariant stargateVariant)
	{
		if(!hasMilkyWayStargateVariant(location))
			MILKY_WAY_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StargateJourney.LOGGER.error("Milky Way Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static boolean hasPegasusStargateVariant(ResourceLocation location)
	{
		return PEGASUS_STARGATE_VARIANTS.containsKey(location);
	}
	
	public static PegasusStargateVariant getPegasusStargateVariant(ResourceLocation location)
	{
		if(hasPegasusStargateVariant(location))
			return PEGASUS_STARGATE_VARIANTS.get(location);
		
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ? PegasusStargateVariant.DEFAULT_BACK_VARIANT : PegasusStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addPegasusStargateVariant(ResourceLocation location, PegasusStargateVariant stargateVariant)
	{
		if(!hasPegasusStargateVariant(location))
			PEGASUS_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StargateJourney.LOGGER.error("Pegasus Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static boolean hasTollanStargateVariant(ResourceLocation location)
	{
		return TOLLAN_STARGATE_VARIANTS.containsKey(location);
	}
	
	public static TollanStargateVariant getTollanStargateVariant(ResourceLocation location)
	{
		if(hasTollanStargateVariant(location))
			return TOLLAN_STARGATE_VARIANTS.get(location);
		
		return TollanStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addTollanStargateVariant(ResourceLocation location, TollanStargateVariant stargateVariant)
	{
		if(!hasTollanStargateVariant(location))
			TOLLAN_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StargateJourney.LOGGER.error("Tollan Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static boolean hasClassicStargateVariant(ResourceLocation location)
	{
		return CLASSIC_STARGATE_VARIANTS.containsKey(location);
	}
	
	public static ClassicStargateVariant getClassicStargateVariant(ResourceLocation location)
	{
		if(hasClassicStargateVariant(location))
			return CLASSIC_STARGATE_VARIANTS.get(location);
		
		return ClassicStargateVariant.DEFAULT_VARIANT;
	}
	
	public static void addClassicStargateVariant(ResourceLocation location, ClassicStargateVariant stargateVariant)
	{
		if(!hasClassicStargateVariant(location))
			CLASSIC_STARGATE_VARIANTS.put(location, stargateVariant);
		else
			StargateJourney.LOGGER.error("Classic Stargate Variant " + location.toString() + " already exists");
	}
	
	
	
	public static ClientStargateVariant getClientStargateVariant(ResourceLocation location, AbstractStargateEntity stargate)
	{
		if(stargate instanceof UniverseStargateEntity)
			return getUniverseStargateVariant(location);
		else if(stargate instanceof MilkyWayStargateEntity)
			return getMilkyWayStargateVariant(location);
		else if(stargate instanceof PegasusStargateEntity)
			return getPegasusStargateVariant(location);

		else if(stargate instanceof TollanStargateEntity)
			return getTollanStargateVariant(location);

		else if(stargate instanceof ClassicStargateEntity)
			return getClassicStargateVariant(location);
		
		// Milky Way Stargate Variant will be the defaultest of defaults
		return MilkyWayStargateVariant.DEFAULT_VARIANT;
	}
	
	
	
	public static RotatingStargateVariant getRotatingStargateVariant(ResourceLocation location, AbstractStargateEntity stargate)
	{
		if(stargate instanceof UniverseStargateEntity)
			return getUniverseStargateVariant(location);
		else if(stargate instanceof MilkyWayStargateEntity)
			return getMilkyWayStargateVariant(location);
		else if(stargate instanceof PegasusStargateEntity)
			return getPegasusStargateVariant(location);
		else if(stargate instanceof ClassicStargateEntity)
			return getClassicStargateVariant(location);
		
		// Milky Way Stargate Variant will be the defaultest of defaults
		return MilkyWayStargateVariant.DEFAULT_VARIANT;
	}
	
	/**
	 * Method for getting the common variant of the Stargate
	 * @param stargate
	 * @return
	 */
	public static Optional<StargateVariant> getVariant(AbstractStargateEntity stargate)
	{
		Optional<StargateVariant> optional = Optional.empty();
		
		if(!ClientStargateConfig.stargate_variants.get())
			return optional;
		
		ResourceLocation variant = stargate.getVariant();
		
		if(variant == null || StargateJourney.EMPTY_LOCATION.equals(variant))
			return optional;
		
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<StargateVariant> variantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
		
		optional = Optional.ofNullable(variantRegistry.get(variant));
		
		return optional;
	}
}
