package net.povstalec.sgjourney.client.resourcepack;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
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
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.client.resourcepack.symbols.SymbolSet;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class ResourcepackReloadListener
{
	public static final String PATH = StargateJourney.MODID;
	
	public static final String STARGATE_VARIANT = "stargate_variant";
	public static final String SYMBOL_SET = "symbol_set";
	public static final String SYMBOLS = "symbols";
	public static final String POINT_OF_ORIGIN = "point_of_origin";

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
			SymbolSet.clearSymbolSets();
			ClientSymbols.clearSymbols();
			ClientPointOfOrigin.clearPointsOfOrigin();
			ClientStargateVariants.clear();
    		
			ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
			
			if(clientPacketListener != null)
			{
	    		RegistryAccess registries = clientPacketListener.registryAccess();
				Registry<StargateVariant> variantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
				
				for(Entry<ResourceKey<StargateVariant>, StargateVariant> stargateVariantEntry : variantRegistry.entrySet())
				{
					stargateVariantEntry.getValue().resetMissing();
				}
			}
    		
			for(Map.Entry<ResourceLocation, JsonElement> jsonEntry : jsonMap.entrySet())
			{
				ResourceLocation location = jsonEntry.getKey();
				JsonElement element = jsonEntry.getValue();
				
				if(canShortenPath(location, POINT_OF_ORIGIN))
					addPointOfOrigin(shortenPath(location, POINT_OF_ORIGIN), element);
				else if(canShortenPath(location, SYMBOLS))
					addSymbols(shortenPath(location, SYMBOLS), element);
				else if(canShortenPath(location, SYMBOL_SET))
					addSymbolSet(shortenPath(location, SYMBOL_SET), element);
				else if(canShortenPath(location, STARGATE_VARIANT))
				{
					location = shortenPath(location, STARGATE_VARIANT);
					
					if(canShortenPath(location, UNIVERSE))
						addUniverseStargateVariant(shortenPath(location, UNIVERSE), element);
					
					else if(canShortenPath(location, MILKY_WAY))
						addMilkyWayStargateVariant(shortenPath(location, MILKY_WAY), element);
					
					else if(canShortenPath(location, PEGASUS))
						addPegasusStargateVariant(shortenPath(location, PEGASUS), element);
					
					else if(canShortenPath(location, TOLLAN))
						addTollanStargateVariant(shortenPath(location, TOLLAN), element);
					
					else if(canShortenPath(location, CLASSIC))
						addClassicStargateVariant(shortenPath(location, CLASSIC), element);
				}
			}
			
			ClientSymbols.assignSymbolSets();
		}
		
		private static void addUniverseStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				UniverseStargateVariant stargateVariant = UniverseStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Universe Stargate Variant {}", msg));
				
				ClientStargateVariants.addUniverseStargateVariant(location, stargateVariant);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Universe Stargate Variant: {}", location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addMilkyWayStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				MilkyWayStargateVariant stargateVariant = MilkyWayStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Milky Way Stargate Variant {}", msg));
				
				ClientStargateVariants.addMilkyWayStargateVariant(location, stargateVariant);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Milky Way Stargate Variant: {}", location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addPegasusStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				PegasusStargateVariant stargateVariant = PegasusStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Pegasus Stargate Variant {}", msg));
				
				ClientStargateVariants.addPegasusStargateVariant(location, stargateVariant);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Pegasus Stargate Variant: {}", location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addTollanStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				TollanStargateVariant stargateVariant = TollanStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Tollan Stargate Variant {}", msg));
				
				ClientStargateVariants.addTollanStargateVariant(location, stargateVariant);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Tollan Stargate Variant: {}", location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addClassicStargateVariant(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, STARGATE_VARIANT);
				ClassicStargateVariant stargateVariant = ClassicStargateVariant.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Classic Stargate Variant {}", msg));
				
				ClientStargateVariants.addClassicStargateVariant(location, stargateVariant);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Classic Stargate Variant: {}", location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addSymbolSet(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, SYMBOL_SET);
				SymbolSet symbolSet = SymbolSet.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Symbol Set {}", msg));
				
				SymbolSet.addSymbolSet(SymbolSet.keyFromLocation(location), symbolSet);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Symbol Set: {}", location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addSymbols(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, SYMBOLS);
				ClientSymbols symbols = ClientSymbols.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Symbols {}", msg));
				
				ClientSymbols.addSymbols(Conversion.locationToSymbols(location), symbols);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Symbols: {}", location.toString());
				StargateJourney.LOGGER.error(e.getMessage());
			}
		}
		
		private static void addPointOfOrigin(ResourceLocation location, JsonElement element)
		{
			try
			{
				JsonObject json = GsonHelper.convertToJsonObject(element, POINT_OF_ORIGIN);
				ClientPointOfOrigin pointOfOrigin = ClientPointOfOrigin.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> StargateJourney.LOGGER.error("Failed to parse Point of Origin {}", msg));
				
				ClientPointOfOrigin.addPointOfOrigin(Conversion.locationToPointOfOrigin(location), pointOfOrigin);
			}
			catch(RuntimeException e)
			{
				StargateJourney.LOGGER.error("Could not load Point of Origin: {}", location.toString());
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
