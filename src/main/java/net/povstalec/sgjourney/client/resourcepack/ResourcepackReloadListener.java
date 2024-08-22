package net.povstalec.sgjourney.client.resourcepack;

import java.util.Map;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClassicStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.MilkyWayStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.PegasusStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.TollanStargateVariant;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.UniverseStargateVariant;
import net.povstalec.stellarview.StellarView;

public class ResourcepackReloadListener
{
	public static final String PATH = StargateJourney.MODID;
	
	public static final String STARGATE_VARIANT = "stargate_variant";

	public static final String UNIVERSE = "universe";
	public static final String MILKY_WAY = "milky_way";
	public static final String PEGASUS = "pegasus";
	public static final String TOLLAN = "tollan";
	public static final String CLASSIC = "classic";
	
	@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ReloadListener extends SimpleJsonResourceReloadListener
	{
		public ReloadListener()
		{
			super(new GsonBuilder().create(), PATH);
		}
		
		@Override
		protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager manager, ProfilerFiller filler)
		{
    		ClientStargateVariants.clear();
    		StargateJourney.LOGGER.info("---------- Loading Stargate Variants ----------");
    		
			for(Map.Entry<ResourceLocation, JsonElement> jsonEntry : jsonMap.entrySet())
			{
				ResourceLocation location = jsonEntry.getKey();
				JsonElement element = jsonEntry.getValue();
				
				if(canShortenPath(location, STARGATE_VARIANT))
				{
					location = shortenPath(location, STARGATE_VARIANT);
					
					if(canShortenPath(location, UNIVERSE))
						addUniverseStargateVariant(location, element);
					
					else if(canShortenPath(location, MILKY_WAY))
						addMilkyWayStargateVariant(location, element);
					
					else if(canShortenPath(location, PEGASUS))
						addPegasusStargateVariant(location, element);
					
					else if(canShortenPath(location, TOLLAN))
						addTollanStargateVariant(location, element);
					
					else if(canShortenPath(location, CLASSIC))
						addClassicStargateVariant(location, element);
				}
			}
		}
		
		private static void addUniverseStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				UniverseStargateVariant stargateVariant = UniverseStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StellarView.LOGGER.error("Failed to parse Stargate Variant", msg));
				
				ClientStargateVariants.addUniverseStargateVariant(location, stargateVariant);
				StargateJourney.LOGGER.info("Loaded Universe Stargate Variant: " + location.toString());
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Universe Stargate Variant: " + location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addMilkyWayStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				MilkyWayStargateVariant stargateVariant = MilkyWayStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StellarView.LOGGER.error("Failed to parse Stargate Variant", msg));
				
				ClientStargateVariants.addMilkyWayStargateVariant(location, stargateVariant);
				StargateJourney.LOGGER.info("Loaded Milky Way Stargate Variant: " + location.toString());
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Milky Way Stargate Variant: " + location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addPegasusStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				PegasusStargateVariant stargateVariant = PegasusStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StellarView.LOGGER.error("Failed to parse Stargate Variant", msg));
				
				ClientStargateVariants.addPegasusStargateVariant(location, stargateVariant);
				StargateJourney.LOGGER.info("Loaded Pegasus Stargate Variant: " + location.toString());
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Pegasus Stargate Variant: " + location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addTollanStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				TollanStargateVariant stargateVariant = TollanStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StellarView.LOGGER.error("Failed to parse Stargate Variant", msg));
				
				ClientStargateVariants.addTollanStargateVariant(location, stargateVariant);
				StargateJourney.LOGGER.info("Loaded Tollan Stargate Variant: " + location.toString());
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Tollan Stargate Variant: " + location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addClassicStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				ClassicStargateVariant stargateVariant = ClassicStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StellarView.LOGGER.error("Failed to parse Stargate Variant", msg));
				
				ClientStargateVariants.addClassicStargateVariant(location, stargateVariant);
				StargateJourney.LOGGER.info("Loaded Classic Stargate Variant: " + location.toString());
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Classic Stargate Variant: " + location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		
		
		public static void registerReloadListener(RegisterClientReloadListenersEvent event)
		{
			event.registerReloadListener(new ReloadListener());
		}
		
		private static boolean canShortenPath(ResourceLocation location, String shortenBy)
		{
			return location.getPath().startsWith(shortenBy) && location.getPath().length() > shortenBy.length(); // If it starts with the string and isn't empty after getting shortened
		}
		
		private static ResourceLocation shortenPath(ResourceLocation location, String shortenBy)
		{
			return location.withPath(location.getPath().substring(shortenBy.length() + 1)); // Magical 1 because there's also the / symbol
		}
	}
}
